package ch.ethz.ivt.abmt.project.help;

import abmt17.pt.ABMTPTModule;
import abmt17.scoring.ABMTScoringModule;
import ch.ethz.matsim.baseline_scenario.analysis.simulation.ModeShareListenerModule;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.scenario.ScenarioUtils;

/**
 * @author thibautd
 */
public class RunZHExample {
	static public void main(String[] args) {
		Config config = ConfigUtils.loadConfig(args[0]);

		Scenario scenario = ScenarioUtils.loadScenario(config);
		Controler controler = new Controler(scenario);

		controler.addOverridingModule(new ABMTScoringModule());
		controler.addOverridingModule(new ABMTPTModule());
		controler.addOverridingModule(new ModeShareListenerModule());

		controler.run();
	}
}
