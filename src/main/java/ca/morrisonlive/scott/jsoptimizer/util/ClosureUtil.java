package ca.morrisonlive.scott.jsoptimizer.util;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
//import java.net.URLEncoder;
import java.util.logging.Logger;

import ca.morrisonlive.scott.jsoptimizer.entity.Script;

import com.google.common.collect.Lists;
import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.Result;

//import ca.morrisonlive.scott.jsoptimizer.JSOptimizerService;

public class ClosureUtil {	
	
	public static final Logger log = Logger.getLogger(ClosureUtil.class.getName());
	
	/*public static String loadScript(String file) throws Exception{
		
		URL url = new URL("http://closure-compiler.appspot.com/compile");
		StringBuilder postData = new StringBuilder(); 

		postData.append(URLEncoder.encode("code_url", "UTF-8"));
		postData.append("=");
		postData.append(URLEncoder.encode(file, "UTF-8"));
		postData.append("&");
		postData.append(URLEncoder.encode("compilation_level", "UTF-8"));
		postData.append("=");
		postData.append(URLEncoder.encode("WHITESPACE_ONLY", "UTF-8"));
		postData.append("&");   		
		postData.append(URLEncoder.encode("language", "UTF-8"));
		postData.append("=");
		postData.append(URLEncoder.encode("ECMASCRIPTS", "UTF-8"));
		postData.append("&"); 
		postData.append(URLEncoder.encode("output_format", "UTF-8"));
		postData.append("=");
		postData.append(URLEncoder.encode("text", "UTF-8"));
		postData.append("&");     		
		postData.append(URLEncoder.encode("output_info", "UTF-8"));
		postData.append("=");
		postData.append(URLEncoder.encode("compiled_code", "UTF-8"));    		

		HttpURLConnection connection = (HttpURLConnection) url.openConnection(); 
		connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded"); 
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(postData.toString());
        writer.close();
        
        if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
     	 	
        	BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        	
        	StringBuilder responseBody = new StringBuilder(); 
        	
        	String line;
        	while((line = in.readLine()) != null) {
        		responseBody.append(line);
        	}
        	
        	return responseBody.toString(); 
        	
        } else {
        	return null; 
        }
	
	
	
	}*/
	
	
	public static String loadScript(String file) throws Exception{
	
		
		log.info("The file name is: "+file);
		
		URL url = new URL(file);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection(); 
		connection.setDoOutput(true);
		connection.setRequestMethod("GET");
		
	    if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	 	 	
	    	BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	    	StringBuilder responseBody = new StringBuilder(); 
	    	
	    	String line;
	    	while((line = in.readLine()) != null) {
	    		responseBody.append(line);
	    		responseBody.append(System.lineSeparator());
	    	}
	    	
	    	String unminified = responseBody.toString();
	    	
	    	log.info("unminified: "+unminified);
	    	
	    
	    	Compiler compiler = new Compiler(); 
	    	compiler.disableThreads();
	    	CompilerOptions options = new CompilerOptions(); 
	
	        CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(
	                options);
	    	
	       
	        SourceFile input = SourceFile.fromCode("input1.js", unminified);
	        Result r = compiler.compile(CommandLineRunner.getDefaultExterns(), Lists.newArrayList(input), options);
	        
	        
	        if(!r.success) {
	        	
	        	for(JSError e: r.errors) {
	        		log.severe(e.toString());
	        	}
	        	
	        }
	        
	        log.info("Was compilation succesful? "+r.success);
	    	return compiler.toSource();
	    	
	    } else {
	    	return null; 
	    }
    
	}
		
}
