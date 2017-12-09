package ch.ethz.ivt.abmt.project;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.mobsim.qsim.pt.TransitVehicle;
import org.matsim.core.network.io.NetworkReaderMatsimV2;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.facilities.FacilitiesReaderMatsimV1;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.VehicleReaderV1;
import org.matsim.vehicles.Vehicles;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        //Path to the transit vehicle file
        String inputVehicleFile = "output/output_transitVehicles.xml.gz";

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
//        RandomPopulationSample rps = new RandomPopulationSample(5,population);
//        Set<Id> randomPersonsIds = rps.randomPopulation();
        //System.out.println(randomPersonsIds);

//        // Single person
        Id<Person> myPersonId = Id.createPersonId("18869_2");
//        Id<Person> myPersonId2 = Id.createPersonId("18869_1");
        Set<Id> randomPersonsIds = new HashSet<Id>();
        randomPersonsIds.add(myPersonId);
//        randomPersonsIds.add(myPersonId2);

        //create an event object
        EventsManager events = EventsUtils.createEventsManager();

//        //create the handler and add it
//        MyFacilitiesEventHandler facEventHandler = new MyFacilitiesEventHandler(activityFacilities,randomPersonsIds);
//        events.addHandler(facEventHandler);


        //create the handler and add it
//        MyXYFacilitiesEventHandler facEventHandler = new MyXYFacilitiesEventHandler(activityFacilities,randomPersonsIds,network);
//        events.addHandler(facEventHandler);

//        MyVehiclesEventHandler vecEventHandler = new MyVehiclesEventHandler(vehicles,randomPersonsIds);
//        events.addHandler(vecEventHandler);

        //create the handler and add it
        MyPathEventHandler pathEventHandler = new MyPathEventHandler(scenario,randomPersonsIds);
        events.addHandler(pathEventHandler);


        // create the reader and read the file
        MatsimEventsReader eventReader = new MatsimEventsReader(events);
        eventReader.readFile(inputEventFile);

        pathEventHandler.writeCSV("output/xy_test.csv");
        pathEventHandler.printMisc();
        //pathEventHandler.closeFile();


//        vecEventHandler.printInfected();
//        vecEventHandler.writeChart();
//        vecEventHandler.closeFile();

//        //fspEventHandler.printPersonId();
//        facEventHandler.printInfected();
//
//
        // todo find a better solution for this
//        facEventHandler.closeFile();
////        facEventHandler.writeChart();

//        for (Person person : population.getPersons().values()){
//            PlanElement myPlan = person.getSelectedPlan().getPlanElements().get(0);
//            //System.out.println(person.getSelectedPlan().getPlanElements().get(0));
//            System.out.println(myPlan.);
//        }


        System.out.println("Events file read!");

    }
}
