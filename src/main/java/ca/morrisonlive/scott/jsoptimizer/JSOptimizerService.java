package ca.morrisonlive.scott.jsoptimizer;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import ca.morrisonlive.scott.jsoptimizer.entity.CombinedScript;
import ca.morrisonlive.scott.jsoptimizer.entity.Script;
import ca.morrisonlive.scott.jsoptimizer.util.ClosureUtil;
import ca.morrisonlive.scott.jsoptimizer.util.KeyGen;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.ListItem;
import com.google.appengine.tools.cloudstorage.ListOptions;
import com.google.appengine.tools.cloudstorage.ListResult;
import com.google.appengine.tools.cloudstorage.RetryParams;


@Path("/")
public class JSOptimizerService {


	public static final Logger log = Logger.getLogger(JSOptimizerService.class.getName());
	public final GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());

	// Plain old Java Object it does not extend as class or implements 
	// an interface

	// The class registers its methods for the HTTP GET request using the @GET annotation. 
	// Using the @Produces annotation, it defines that it can deliver several MIME types,
	// text, XML and HTML. 

	// The browser requests per default the HTML MIME type.

	//Sets the path to base URL + /hello
	
	class JSOptimizerResponse{
		public String status;
		public String message; 
	}
	
	
	class ScriptLoadRequest {
	
		public String url;
		public Long combinedKey;
		public String callbackURL; 
		
	}
	
	
	public static final String bucketURL = "http://storage.googleapis.com/jovial-sight-107214.appspot.com/";
	
    /**
     * Service call used to load a single javascript file into the data store. Optionally a combined script key can be passed if we are generating a combined script and a call back if the combined script has finished generating 
     * 
     * @param payload
     * @return
     */
    @POST
    @Consumes("application/json")
    @Path("/loadscript") 
    public void loadScript(JsonNode payload) throws Exception{

    	//try {
    	
	    	String url = payload.get("url").asText();
	    	Long combinedKey = payload.get("combinedKey").asLong();
	    	//String callbackURL = payload.get("callbackURL").asText(); 
	    	
			log.info("Processing file: "+url);	
			
			log.warning("The key value "+combinedKey.toString());
			
			String minified = ClosureUtil.loadScript(url);
		
	    	Script s = new Script();
	    	s.filename = url;
	    	s.minified = minified;
	    	ofy().save().entity(s).now(); 
	    	
	    	if(combinedKey != null) {
	    		CombinedScript cs = ofy().load().type(CombinedScript.class).filter("id", combinedKey).first().now();
	    		if(cs != null) {
		    		if(cs.files != null) {
		    			List<Script> savedScripts = ofy().load().type(Script.class).filter("filename in", cs.files).list(); 
		    			log.warning("files size: "+cs.files.size()+" - savedScripts size: "+savedScripts.size());
		    			
			    		if(savedScripts.size() == cs.files.size()) {
			    			
			    			
			    			Map<String, Script> scriptMap = new HashMap<String, Script>(); 
			    			
			    			for(Script savedScript: savedScripts) {
			    				scriptMap.put(savedScript.filename, savedScript);
			    			}
			    			
			    			//combine the scripts 
			    			generateFile(scriptMap, cs.files, cs);
			        		
			        		//send the filename to the callback url 
			        		log.info("make http callback call here to send the combined resource target to the waiting cms resource" );
			        		
			    		}
		    		}
	    		} else {
	    			log.warning("combined script record is null");
	    		}
	    		
	    	}
	    	log.info("trying to save the script file");	
	    	
    	
    	/**} catch (Exception e) {
    		log.severe(e.getMessage()+e.getStackTrace());
    	}*/

    	
    }
	
	
    public void generateFile(Map<String, Script> savedScripts, List<String> order, CombinedScript cs) throws NoSuchAlgorithmException, IOException {
    	
		StringBuilder combined = new StringBuilder();     			
		for(String scriptKey: order) {
			combined.append(savedScripts.get(scriptKey).minified).append(System.lineSeparator());
		}

		cs.key = KeyGen.getCombinedScriptKey(savedScripts, cs.files);
		cs.loadComplete = true; 
		ofy().save().entity(cs).now();
		
		GcsOutputChannel outputChannel = gcsService.createOrReplace(new GcsFilename("jovial-sight-107214.appspot.com", cs.getFileName()), new GcsFileOptions.Builder().mimeType("application/javascript").acl("public-read").build());    		
		outputChannel.write(ByteBuffer.wrap(combined.toString().getBytes()));
		outputChannel.close(); 
    	
    }
    
    
    @GET
    @Path("/deletefiles") 
    @Produces("application/json")
    public Object deleteFiles() {
 
    	//get all files in the default bucket 
    	try {
	    	ListResult list = gcsService.list("jovial-sight-107214.appspot.com", ListOptions.DEFAULT);
	    	while(list.hasNext()) {
	    		ListItem item = list.next(); 
	    		gcsService.delete(new GcsFilename("jovial-sight-107214.appspot.com", item.getName())); 
	    	}
    	} catch(IOException exception) {
    		log.severe(exception.getMessage());
	    }
    	
    	JSOptimizerResponse response = new JSOptimizerResponse(); 
    	response.status = "success";
    	response.message = "files deleted";
    	return response; 
    	
    }
    
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @Path("/getjs") 
    public Object createJavascriptResource(JsonNode payload) throws Exception {
    	
    	JSOptimizerResponse resp = new JSOptimizerResponse();
    	List<String> filenames = new ArrayList<String>();
    	log.info("JsonNode type is: "+payload.getNodeType());
 
    	if(payload.isObject()) {
    		
    		JsonNode resourceList = payload.get("msg");  		
    		log.info("JsonNode type is: "+resourceList.getNodeType()+" : "+resourceList.asText() );
    		
    		if(resourceList.isArray()) {
	    		for(JsonNode n: resourceList) {
	    			if(n.isTextual()) {
	    				log.info("Adding filename :"+n.asText());			
	    				String location = n.asText().toString();
	    				filenames.add(location);
	    			}		
	    		}
    		} else {
    			log.severe("The message was not formated correctly");
    		}
    		
    	} else {
    		log.severe("Body is not an object :"+payload.asText());
    	}
    	
    	try {
    	    		
    		List<Script> savedScripts = ofy().load().type(Script.class).filter("filename in", filenames).list(); 
			Map<String, Script> scriptMap = new HashMap<String, Script>(); 
			
			for(Script savedScript: savedScripts) {
				scriptMap.put(savedScript.filename, savedScript);
			}
    		CombinedScript cs;
    		
    		if(savedScripts.size() == filenames.size()) {
    		//check to see if we already have the combined script 
    		    			
    			String key = KeyGen.getCombinedScriptKey(scriptMap, filenames); 
    			
	    		cs = ofy().load().type(CombinedScript.class).filter("key", key).first().now();		
	    		if(cs != null) {
	    			
	    			if(cs.loadComplete != true) {
	    				//combine the scripts
	    				generateFile(scriptMap, cs.files, cs);
	    			}  			
	    		} else {
	        		cs = new CombinedScript(); 
	        		cs.setFiles(filenames);
	        		cs.key = KeyGen.getCombinedScriptKey(scriptMap, cs.files);
	        		generateFile(scriptMap, cs.files, cs);    			
	    		}
	    		
				log.info("found an existing database entry: "+cs.getFileName());
	    		
				resp.status = "Success";
				resp.message = bucketURL+cs.getFileName();
				return resp;
	    	
    		}
	    		
	    	
    		log.info("number of saved scripts: "+savedScripts.size());
    		
    		cs = new CombinedScript(); 
    		cs.setFiles(filenames);
    		ofy().save().entity(cs).now();
    		
    		cs.loadComplete = false; 
    		 		    		    		
    		for(String file: filenames) {

    			Queue queue = QueueFactory.getDefaultQueue();
    			ObjectMapper mapper = new ObjectMapper(); 
    			if(!scriptMap.containsKey(file)) {
    			
    				ScriptLoadRequest slr = new ScriptLoadRequest();
    				slr.url = file; 
    				slr.combinedKey = cs.id;
        			
    				TaskOptions task = TaskOptions.Builder.withUrl("/rest/loadscript").method(Method.POST).header("Content-Type", "application/json;charset=UTF-8");
    
    				task.payload(mapper.writeValueAsBytes(slr));
    				queue.add(task);
    				
    			}
    		}
    		
	
    		log.info("unique id: "+cs.getId());
    			
    		resp.status = "Success";
    		resp.message = bucketURL+cs.getFileName();
    
    	
    	} catch(Exception e) {    	
    		log.severe("Process failed with exception: "+e.getMessage());
    	}
    	
    	return resp; 
    	
    }
    
    	
	
}
