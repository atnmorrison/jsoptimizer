package ca.morrisonlive.scott.jsoptimizer.entity;

import java.util.List;
import java.util.logging.Logger;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;


/**
 * @author Scott
 *
 * Datastore object that points a fileset to a corresponding cloud store object. The cloud store object uses the unique id as the file name. The key is the set of file names delimited by ~ 
 * 
 */
@Entity
public class CombinedScript {

	public static final Logger log = Logger.getLogger(CombinedScript.class.getName());
	
	public @Id Long id; 
	public @Index String key;
	public String filename;
	public Boolean loadComplete; 
	public List<String> files;  
	
	
	public String getId() {
		return id.toString();
	}
	
	public void setKey(String key) {
		this.key = key; 
	}
	
	public void setFileName(String filename) {
		this.filename = filename;	
	}
	
	public void setFiles(List<String> filenames) {
		this.files = filenames; 
	}
	
	public String getFileName() {
		if(this.filename == null) {
			log.info("the combined script id is "+this.id); 
			this.filename = "cs"+this.id+".js";
		}	
			
		return this.filename;
	}
	
	
}
