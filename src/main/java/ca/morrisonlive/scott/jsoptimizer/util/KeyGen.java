package ca.morrisonlive.scott.jsoptimizer.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import ca.morrisonlive.scott.jsoptimizer.entity.Script;

public class KeyGen {

	
	
	public static String getCombinedScriptKey(Map<String, Script> scripts, List<String> order) throws NoSuchAlgorithmException {
	
		StringBuilder key = new StringBuilder(); 
		
		for(String scriptKey: order) {
			key.append(scripts.get(scriptKey).id);
			key.append('~');
		}
		
		MessageDigest md;
		md = MessageDigest.getInstance("MD5");

		
		String hash = md.digest(key.toString().getBytes()).toString();
		return hash; 
		
	}
	
	
}
