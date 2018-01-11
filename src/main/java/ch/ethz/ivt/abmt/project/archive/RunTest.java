package ch.ethz.ivt.abmt.project.archive;

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

public class RunTest {
    public static void main(String[] args){
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

        // show case example
        Set<Id> infectedPersonsIds = new HashSet<Id>();
        // Airport
        infectedPersonsIds.add(Id.createPersonId("1267806100"));

        //create an event object
        EventsManager events = EventsUtils.createEventsManager();
        //create the handler and add it
        MyVehiclesEventHandler vehiclesEventHandler = new MyVehiclesEventHandler(vehicles,infectedPersonsIds);
        //MyPathEventHandler pathEventHandler = new MyPathEventHandler(scenario,infectedPersonsIds);
        events.addHandler(vehiclesEventHandler);


        // create the reader and read the file
        MatsimEventsReader eventReader = new MatsimEventsReader(events);
        eventReader.readFile(inputEventFile);

        vehiclesEventHandler.closeFile();


    }
}
