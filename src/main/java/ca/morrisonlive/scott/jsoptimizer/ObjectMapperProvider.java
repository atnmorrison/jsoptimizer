package ca.morrisonlive.scott.jsoptimizer;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper>{

	final ObjectMapper defaultObjectMapper; 
	public ObjectMapperProvider() {
		defaultObjectMapper = new ObjectMapper();
        defaultObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}
	
	@Override
	public ObjectMapper getContext(final Class<?> type){
        final ObjectMapper result = new ObjectMapper();
        result.enable(SerializationFeature.INDENT_OUTPUT);
        return result;
	}
	
}
