package ch.ethz.ivt.abmt.project.archive;

import ch.ethz.ivt.abmt.project.MyPathEventHandler;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
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


        //Logger.getRootLogger().setLevel( Level.ERROR );

//        /** define load paths of the input files **/
//        //Path ot the network file
//        String inputNetworkFile = "output/output_network.xml.gz";
//        //Path to the event file
//        String inputEventFile = "output/ITERS/it.200/200.events.xml.gz";
//        //Path to the facilities file
//        String inputFacilitiesFile = "output/output_facilities.xml.gz";
//        //Path to the population file
//        String inputPopulationFile = "scenarios/SiouxFalls/population.xml.gz";
//        //Path to the transit vehicle file
//        String inputVehicleFile = "output/output_transitVehicles.xml.gz";


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


//        // Single person
        //Id<Person> myPersonId = Id.createPersonId("909125500");//"18869_2");
        //Id<Person> myPersonId2 = Id.createPersonId("18869_1");
        //Id<Person> myPerson3 = Id.createPersonId("20730_4");
//        Set<Id> randomPersonsIds = new HashSet<Id>();
//        randomPersonsIds.add(myPersonId);
//        //randomPersonsIds.add(myPersonId2);
//        //randomPersonsIds.add(myPerson3);
//
//

        // Simulations for the presentation
        Set<Id> randomPersonsIds = new HashSet<Id>();
        String filename;

        // infected day 1
        randomPersonsIds.add(Id.createPersonId("1267806100"));
        filename = "infected_day_1_xxx.csv";


//        // infected day 2
//        int[] a = new int[]{1314387300, 821765200, 918039800, 1456171500, 1090559500, 1281699500, 834469900, 867771200, 1156086600, 1176076400, 1055691500, 1185198701, 1411653500, 1035026300, 1433752600, 764067900, 1388479600, 891557800, 1311039200, 1579537500, 1423592700, 876099200, 1267806100, 990734800, 1440135600, 1119928800, 811719600, 1253688001, 846872100, 1201662100, 929145200, 1137478800, 1378945300, 1109814900, 1214093600, 1532428300, 815542800, 781141600, 868005500, 845756700, 824413100, 925792300, 1290827300, 863939300, 1489296200, 1006881400, 991039600, 883999400, 1507801600, 995023100};
//        for (int i:a){
//            randomPersonsIds.add(Id.createPersonId(i));
//        }
//        filename = "infected_day_2.csv";


//        // recovered day 2
//        int[] a = new int[]{1440135600, 1314387300, 1449039300, 811719600, 1072796400, 940735200, 1154727200, 1201662100, 1419943400, 1523147800, 1092304000, 1456171500, 1188848400, 1319100701, 1359632800, 1531886100, 877461900, 1281699500, 1408578100, 850911900, 1313727701, 1184684500, 1532428300, 1011126000, 1156086600, 1140115401, 947893200, 824413100, 1602231800, 1433752600, 1521712900, 1311039200, 903429800, 883999400, 1448712800, 1152605401, 990734800, 1474042800, 1526076700};
//        for (int i:a){
//            randomPersonsIds.add(Id.createPersonId(i));
//        }
//        filename = "recovered_day_2.csv";


//        // infected day 3
//        int[] a = new int[]{821765200, 1000381800, 1090559500, 1393541900, 800341900, 1072721600, 1406050201, 834469900, 867771200, 794090400, 924757600, 1268110800, 1403154401, 1185198701, 1411653500, 764067900, 1388479600, 891557800, 1423592700, 876099200, 1081427600, 852380600, 1119928800, 936809800, 802416800, 929145200, 1050177000, 798011100, 970366300, 1378945300, 1057780800, 1214093600, 947339801, 815542800, 781141600, 868005500, 937899900, 785194400, 845756700, 1594423300, 863939300, 1006881400, 1051532200, 1218086300, 1366533501, 927462800, 878444500, 1321627900, 918039800, 1214803400, 841429400, 1253546300, 1078789700, 891021500, 859153900, 778703900, 1442508701, 1176076400, 1160788100, 1055691500, 1035026300, 1477144901, 1579537500, 1215842800, 1267806100, 1185244500, 1080949300, 985846101, 1253688001, 881331100, 846872100, 1512719600, 1137478800, 1235645300, 1109814900, 1543316200, 1413050100, 1567453900, 924917300, 1336003500, 1260843800, 925792300, 1290827300, 1392051000, 1579653800, 1489296200, 1504241900, 991039600, 1220778100, 1398561500, 968061800, 1507801600, 1082043700, 1554159500, 995023100};
//        for (int i:a){
//            randomPersonsIds.add(Id.createPersonId(i));
//        }
//        filename = "infected_day_3.csv";






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

        //pathEventHandler.printMisc();
        pathEventHandler.writeCSV("results/via/"+filename);
        pathEventHandler.writeFriends2CSV("results/via/friends.csv");


        Set<Id> infectedPersonsIds = pathEventHandler.getInfected();
        System.out.println(infectedPersonsIds.size());

        Set<Id> recoveredPersonsIds = new HashSet<Id>();
        for (Id personId: infectedPersonsIds){
            // infect person with probability p
            double rand = Math.random();
            if ( rand < .3){
                recoveredPersonsIds.add(personId);
            }
        }

        infectedPersonsIds.removeAll(recoveredPersonsIds);
        System.out.println(recoveredPersonsIds);

        System.out.println(infectedPersonsIds);

        //pathEventHandler.closeFile();


//        vecEventHandler.printInfected();
//        vecEventHandler.writeChart();
//        vecEventHandler.closeFile();

//        //fspEventHandler.printPersonId();
//        facEventHandler.printInfected();
//
//
//        // todo find a better solution for this
//        facEventHandler.closeFile();
//        facEventHandler.writeChart();

//        for (Person person : population.getPersons().values()){
//            PlanElement myPlan = person.getSelectedPlan().getPlanElements().get(0);
//            //System.out.println(person.getSelectedPlan().getPlanElements().get(0));
//            System.out.println(myPlan.);
//        }


        System.out.println("Events file read!");

    }
}
