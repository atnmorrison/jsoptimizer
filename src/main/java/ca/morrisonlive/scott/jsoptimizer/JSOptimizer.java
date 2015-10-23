package ca.morrisonlive.scott.jsoptimizer;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class JSOptimizer extends ResourceConfig{

	public JSOptimizer() {
		super(
				ObjectMapperProvider.class,
				JacksonFeature.class
		 );	
		
	}	
	
}
