package ch.ethz.ivt.abmt.project;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
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
import org.matsim.vehicles.VehicleReaderV1;
import org.matsim.vehicles.Vehicles;

import java.util.HashSet;
import java.util.Set;

/**
 * RunTravelerAnalysis
 *
 * Detailed analysis of each agent
 * (e.g. paths taken, contacts with other agents,...)
 *
 * **/

public class RunTravelerAnalysis {
    public static void main(String[] args){

        /** parameters **/
        // initialize main model parameters
        String outputFolder = "results/test/";

        /** general project settings **/
        // turn of information messages
        //Logger.getRootLogger().setLevel( Level.ERROR );

        /** define load paths of the input files **/
        //Path ot the network file
        String inputNetworkFile = "simulation_output/output_network.xml.gz";
        //Path to the event file
        String inputEventFile = "simulation_output/output_events.xml.gz";
        //Path to the facilities file
        String inputFacilitiesFile = "simulation_output/output_facilities.xml.gz";
        //Path to the population file
        String inputPopulationFile = "simulation_output/output_plans.xml.gz";
        //Path to the transit vehicle file
        String inputVehicleFile = "simulation_output/output_vehicles.xml.gz";


        /** Setup example and load objects**/
        // create a config file
        Config config = ConfigUtils.createConfig();
        // create scenario
        Scenario scenario = ScenarioUtils.createScenario(config);

        //create a network object
        NetworkReaderMatsimV2 networkReader = new NetworkReaderMatsimV2(scenario.getNetwork());
        networkReader.readFile(inputNetworkFile);
        Network network = scenario.getNetwork();

        // create a facility object
        FacilitiesReaderMatsimV1 facilitiesReader = new FacilitiesReaderMatsimV1(scenario);
        facilitiesReader.readFile(inputFacilitiesFile);
        ActivityFacilities activityFacilities = scenario.getActivityFacilities();

        // create a population object
        PopulationReader populationReader = new PopulationReader(scenario);
        populationReader.readFile(inputPopulationFile);
        Population population = scenario.getPopulation();

        // create a vehicles object
        VehicleReaderV1 vehicleReader = new VehicleReaderV1(scenario.getVehicles());
        vehicleReader.readFile(inputVehicleFile);
        Vehicles vehicles = scenario.getVehicles();

        // Generate a random sample of persons from the population
//        RandomPopulationSample rps = new RandomPopulationSample(1000,population);
//        Set<Id> randomPersonsIds = rps.randomPopulation();
//        System.out.println(randomPersonsIds);

        // Simulate the whole population
//        Set<Id> randomPersonsIds = new HashSet<Id>(population.getPersons().keySet());


        // Simulations for a single person
        Set<Id> randomPersonsIds = new HashSet<Id>();
        String filename;

        // infected day 1
        randomPersonsIds.add(Id.createPersonId("1267806100"));
        filename = "infected_day_1";

        /** run simulation **/
        //create an event object
        EventsManager events = EventsUtils.createEventsManager();

        //create the handler and add it to the events
        MyPathEventHandler pathEventHandler = new MyPathEventHandler(scenario,randomPersonsIds);
        events.addHandler(pathEventHandler);

        // create the reader and read the file
        MatsimEventsReader eventReader = new MatsimEventsReader(events);
        eventReader.readFile(inputEventFile);

        // access event handler to print the results
        pathEventHandler.writeCSV(outputFolder+filename+"_paths.csv");
        pathEventHandler.writeFriends2CSV(outputFolder+filename+"_friends.csv");

        // get infected persons
        Set<Id> infectedPersonsIds = pathEventHandler.getInfected();
        System.out.println(infectedPersonsIds.size());

    }
}
