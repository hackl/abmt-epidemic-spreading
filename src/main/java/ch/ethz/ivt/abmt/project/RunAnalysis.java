package ch.ethz.ivt.abmt.project;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.io.NetworkReaderMatsimV2;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.facilities.FacilitiesReaderMatsimV1;

import java.util.List;

public class RunAnalysis {
    public static void main(String[] args){
        /** define load paths of the input files **/
        //Path ot the network file
        String inputNetworkFile = "output/output_network.xml.gz";
        //Path to the event file
        String inputEventFile = "output/ITERS/it.200/200.events.xml.gz";
        //Path to the facilities file
        String inputFacilitiesFile = "output/output_facilities.xml.gz";
        //Path to the population file
        String inputPopulationFile = "scenarios/SiouxFalls/population.xml.gz";

        /** Setup example and load objects**/
        // create a config file
        Config config = ConfigUtils.createConfig();
        // create scenario
        Scenario scenario = ScenarioUtils.createScenario(config);

        //create a network object
        NetworkReaderMatsimV2 networkReader = new NetworkReaderMatsimV2(scenario.getNetwork());
        networkReader.readFile(inputNetworkFile);

        // create a facility object
        FacilitiesReaderMatsimV1 facilitiesReader = new FacilitiesReaderMatsimV1(scenario);
        facilitiesReader.readFile(inputFacilitiesFile);
        ActivityFacilities activityFacilities = scenario.getActivityFacilities();

        // create a population object
        PopulationReader populationReader = new PopulationReader(scenario);
        populationReader.readFile(inputPopulationFile);
        Population population = scenario.getPopulation();

        // Generate a random sample of persons from the population
        RandomPopulationSample rps = new RandomPopulationSample(5,population);
        List<Id> randomPersonsIds = rps.randomPopulation();
        System.out.println(randomPersonsIds);


        //create an event object
        EventsManager events = EventsUtils.createEventsManager();

        //create the handler and add it
        MyFacilitiesEventHandler facEventHandler = new MyFacilitiesEventHandler(activityFacilities,randomPersonsIds);
        events.addHandler(facEventHandler);

        //todo assign given Id
        //Id<Person> myPersonId = (Id<Person>) population.getPersons().get("10614_2");
        //Id<Person> myPersonId = population.getPersons().keySet().
        //FollowSinglePersonEventHandler fspEventHandler = new FollowSinglePersonEventHandler(randomPersonsIds.get(0));
        //events.addHandler(fspEventHandler);

        //create the reader and read the file
        MatsimEventsReader eventReader = new MatsimEventsReader(events);
        eventReader.readFile(inputEventFile);



        //fspEventHandler.printPersonId();
        facEventHandler.printInfected();


        // todo find a better solution for this
        facEventHandler.closeFile();
        facEventHandler.writeChart();

        System.out.println("Events file read!");

    }
}
