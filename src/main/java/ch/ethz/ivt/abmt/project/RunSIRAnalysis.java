package ch.ethz.ivt.abmt.project;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.network.io.NetworkReaderMatsimV2;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.core.utils.io.UncheckedIOException;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.facilities.FacilitiesReaderMatsimV1;
import org.matsim.vehicles.VehicleReaderV1;
import org.matsim.vehicles.Vehicles;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class RunSIRAnalysis {
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

        // create a facility object
        FacilitiesReaderMatsimV1 facilitiesReader = new FacilitiesReaderMatsimV1(scenario);
        facilitiesReader.readFile(inputFacilitiesFile);

        // create a population object
        PopulationReader populationReader = new PopulationReader(scenario);
        populationReader.readFile(inputPopulationFile);

        // create a vehicles object
        VehicleReaderV1 vehicleReader = new VehicleReaderV1(scenario.getVehicles());
        vehicleReader.readFile(inputVehicleFile);

        // Generate a random sample of persons from the population
        double sample = scenario.getPopulation().getPersons().size()*0.01;
        RandomPopulationSample rps = new RandomPopulationSample((int) sample,scenario.getPopulation());
        Set<Id> infectedPersonsIds = rps.randomPopulation();
        Set<Id> recoveredPersonsIds = new HashSet<Id>();

        Set<Id> susceptiblePersonsIds = new HashSet<Id>(scenario.getPopulation().getPersons().keySet());


//        // Single person
//        Id<Person> myPersonId = Id.createPersonId("18869_2");
//        Set<Id> randomPersonsIds = new HashSet<Id>();
//        randomPersonsIds.add(myPersonId);


        double recoverProbability = 0.00;
        double infectionProbability = 0.15;

        List<Integer> susceptible = new ArrayList<Integer>();
        List<Integer> infected = new ArrayList<Integer>();
        List<Integer> recovered = new ArrayList<Integer>();
        List<Integer> days = new ArrayList<Integer>();

        for (int i=0;i<10;i++){
            //create an event object
            EventsManager events = EventsUtils.createEventsManager();

            //create the handler and add it
            MySIREventHandler sirEventHandler = new MySIREventHandler(scenario, infectedPersonsIds, infectionProbability,recoveredPersonsIds);
            events.addHandler(sirEventHandler);

            // create the reader and read the file
            MatsimEventsReader eventReader = new MatsimEventsReader(events);
            eventReader.readFile(inputEventFile);

            //Set<Id> recoveredPersonsIds = new HashSet<Id>();
            for (Id personId: infectedPersonsIds){
                // infect person with probability p
                double rand = Math.random();
                if ( rand < recoverProbability){
                    recoveredPersonsIds.add(personId);
                }
            }

            infectedPersonsIds.removeAll(recoveredPersonsIds);
            susceptiblePersonsIds.removeAll(infectedPersonsIds);
            susceptiblePersonsIds.removeAll(recoveredPersonsIds);

            // store results
            days.add(i);
            susceptible.add((susceptiblePersonsIds.size()));
            infected.add(infectedPersonsIds.size());
            recovered.add(recoveredPersonsIds.size());

            System.out.println("days "+i);
            System.out.println("susceptible "+susceptiblePersonsIds.size());
            System.out.println("infected "+infectedPersonsIds.size());
            System.out.println("recovered "+recoveredPersonsIds.size());

            sirEventHandler.writeCSV(i+"_si.csv");
            if(infectedPersonsIds.size()==0){
                break;
            }
            if(infectedPersonsIds.size()==scenario.getPopulation().getPersons().size()){
                break;
            }

        }



        BufferedWriter writer = IOUtils.getBufferedWriter("out.csv");
        try{
            writer.write("day,susceptible,infected,recovered");
            for (int i=0;i<days.size();i++){
                writer.newLine();
                writer.write(days.get(i)+","+susceptible.get(i)+","+infected.get(i)+","+recovered.get(i));
            }
            writer.close();
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }


        // last line
        System.out.println("Events file read!");

    }










}
