package ca.morrisonlive.scott.jsoptimizer.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Script {
	
	public @Id Long id; 
	public @Index String filename; 
	public String content;
	public String minified; 
	public Boolean loaded; 
	
}

