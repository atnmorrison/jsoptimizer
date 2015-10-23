package ca.morrisonlive.scott.jsoptimizer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import ca.morrisonlive.scott.jsoptimizer.entity.CombinedScript;
import ca.morrisonlive.scott.jsoptimizer.entity.Script;

import com.googlecode.objectify.ObjectifyService;

public class JSOptimizerContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		//nothing to do 
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		//init objectify objects
		
		ObjectifyService.register(Script.class);
		ObjectifyService.register(CombinedScript.class);

	}

}
