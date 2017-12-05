package ch.ethz.ivt.abmt.project;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.scenario.ScenarioUtils;

/**
 * @author thibautd
 */
public class RunExample {
	public static void main(String[] args) {

		Config config ;
		if ( args.length==0 || args[0]=="" ) {
			config = ConfigUtils.loadConfig( "scenarios/equil/config.xml" ) ;
//			config = ConfigUtils.loadConfig( "scenarios/SiouxFalls/config.xml" ) ;
			config.controler().setLastIteration(1);
			config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists );
		} else {
			config = ConfigUtils.loadConfig(args[0]) ;
		}

		Scenario scenario = ScenarioUtils.loadScenario(config) ;

		Controler controler = new Controler( scenario ) ;

		controler.getEvents().addHandler(new MyEventHandler3());

		MyEventHandler3 handler3 = new MyEventHandler3();

		controler.getEvents().addHandler(handler3);
		handler3.writeChart("test.png");
		controler.run();

	}

}