package ca.morrisonlive.scott.jsoptimizer.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestedScript {

	@JsonProperty
	public String  localURL;
	
	@JsonProperty
	public String  libraryName;
	
	@JsonProperty
	public String  libraryVersion;
	
	//some salesforce urls require authorization tokens, this is true in the case of standalone intranets
	
	@JsonProperty
	public Boolean requiresAuthToken;
	
	
}
