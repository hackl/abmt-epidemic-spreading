package ch.ethz.ivt.abmt.project;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;

/**
 * @author thibautd
 */
public class RunExample {
	public static void main( String... args ) {
		Config config = ConfigUtils.loadConfig( args[ 0 ] );

		// you could modify some config settings here

		Scenario scenario = ScenarioUtils.createScenario( config );

		// you could modify some scenario elements here

		Controler controler = new Controler( scenario );

		// you could configure your scenario here

		controler.run();
	}
}
