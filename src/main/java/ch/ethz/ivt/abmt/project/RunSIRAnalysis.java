package ch.ethz.ivt.abmt.project;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
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
import org.matsim.facilities.FacilitiesReaderMatsimV1;
import org.matsim.vehicles.VehicleReaderV1;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

/**
 * RunSIRAnalysis
 *
 * Main function to run the SIR model on MATSim
 *
 * **/


public class RunSIRAnalysis {


    public static void main(String[] args){
        /** parameters **/
        // initialize main model parameters
        String simId;
        double percentageInfectedPersons;
        double recoverProbability;
        double infectionProbability;
        int numberOfSimulatedDays = 50;
        boolean printToCommandLine = true;
        String outputFolder = "results/";

        // check if model parameters are entered via command line
        // otherwise use predefined model parameters
        if (args.length<1) {
            simId = "1";
            percentageInfectedPersons = 0.01;
            infectionProbability = 1.0;
            recoverProbability = 0.1;

        }
        // use model parameters from the command line
        else {
            simId = args[0];
            percentageInfectedPersons = Double.parseDouble(args[1]);
            infectionProbability = Double.parseDouble(args[2]);
            recoverProbability = Double.parseDouble(args[3]);
        }

        // initialize output file names
        // sir_id_PI_Pb_Pg.csv
        String outputFileSIR = outputFolder+"sir/sir_"+simId+"_"+percentageInfectedPersons*100+"_"+infectionProbability*100+"_"+recoverProbability*100+".csv";
        // si_id_PI_Pb_Pg.csv
        String outputFileSI = outputFolder+"si/si_"+simId+"_"+percentageInfectedPersons*100+"_"+infectionProbability*100+"_"+recoverProbability*100+".csv";


        /** general project settings **/
        // turn of information messages
        Logger.getRootLogger().setLevel( Level.ERROR );

        /** define load paths of the input files **/
        // path ot the network file
        String inputNetworkFile = "simulation_output/output_network.xml.gz";
        // path to the event file
        String inputEventFile = "simulation_output/output_events.xml.gz";
        // path to the facilities file
        String inputFacilitiesFile = "simulation_output/output_facilities.xml.gz";
        // path to the population file
        String inputPopulationFile = "simulation_output/output_plans.xml.gz";
        // path to the transit vehicle file
        String inputVehicleFile = "simulation_output/output_vehicles.xml.gz";

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
        double sample = scenario.getPopulation().getPersons().size()*percentageInfectedPersons;
        RandomPopulationSample rps = new RandomPopulationSample((int) sample,scenario.getPopulation());

        // initialize population
        Set<Id> infectedPersonsIds = rps.randomPopulation();
        Set<Id> recoveredPersonsIds = new HashSet<Id>();
        Set<Id> susceptiblePersonsIds = new HashSet<Id>(scenario.getPopulation().getPersons().keySet());

//        // show case example
//        // using the most influential agent of the zurich_scenario_1p scenario
//        Set<Id> infectedPersonsIds = new HashSet<Id>();
//        // Airport
//        infectedPersonsIds.add(Id.createPersonId("1267806100"));


        // initialize output vectors
        List<Integer> susceptible = new ArrayList<Integer>();
        List<Integer> infected = new ArrayList<Integer>();
        List<Integer> recovered = new ArrayList<Integer>();
        List<Integer> days = new ArrayList<Integer>();

        // store initial values
        days.add(0);
        susceptible.add(scenario.getPopulation().getPersons().size() - infectedPersonsIds.size());
        infected.add(infectedPersonsIds.size());
        recovered.add(0);

        // create writer for hourly values
        BufferedWriter siWriter = IOUtils.getBufferedWriter(outputFileSI);
        try{
            siWriter.write("day,hour,infected");
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }

        /** run simulations **/
        // start simulation runs
        for (int i=1;i<numberOfSimulatedDays+1;i++){

            // start time of one simulation run
            long startTime = System.currentTimeMillis();

            if(printToCommandLine) {
                System.out.println("===================================================");
                // print current iteration
                System.out.println("day "+i);
            }

            // create an event object
            EventsManager events = EventsUtils.createEventsManager();

            // create the handler and add it
            MySIREventHandler sirEventHandler = new MySIREventHandler(scenario, infectedPersonsIds, infectionProbability,recoveredPersonsIds);
            events.addHandler(sirEventHandler);

            // create the reader and read the file
            MatsimEventsReader eventReader = new MatsimEventsReader(events);
            eventReader.readFile(inputEventFile);

            // recover infected agent with probability p
            for (Id personId: infectedPersonsIds){
                double rand = Math.random();
                if ( rand < recoverProbability){
                    recoveredPersonsIds.add(personId);
                }
            }

            // update the new population states
            infectedPersonsIds.removeAll(recoveredPersonsIds);
            susceptiblePersonsIds.removeAll(infectedPersonsIds);
            susceptiblePersonsIds.removeAll(recoveredPersonsIds);

            // store results
            days.add(i);
            susceptible.add(susceptiblePersonsIds.size());
            infected.add(infectedPersonsIds.size());
            recovered.add(recoveredPersonsIds.size());

            // write SI result to file
            sirEventHandler.writeLine2CSV(siWriter,i);

            // end time for one simulation run
            long endTime = System.currentTimeMillis() - startTime;


            if(printToCommandLine) {
                System.out.println("susceptible "+susceptiblePersonsIds.size());
                System.out.println("infected "+infectedPersonsIds.size());
                System.out.println("recovered "+recoveredPersonsIds.size());
                System.out.println("duration " + DurationFormatUtils.formatDuration(endTime, "HH:mm:ss:SS"));
            }


            // stopping criterion
            // end simulation when there are no infected persons
            if(infectedPersonsIds.size()==0){
                break;
            }
            // end simulation when all persons are infected
            if(infectedPersonsIds.size()==scenario.getPopulation().getPersons().size()){
                break;
            }
        }

        // close SI writer
        try{
            siWriter.close();
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }

        // write SIR data to file
        BufferedWriter writer = IOUtils.getBufferedWriter(outputFileSIR);
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

    }

}
