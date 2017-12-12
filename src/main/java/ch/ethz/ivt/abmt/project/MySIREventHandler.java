package ch.ethz.ivt.abmt.project;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.ActivityEndEvent;
import org.matsim.api.core.v01.events.ActivityStartEvent;
import org.matsim.api.core.v01.events.handler.ActivityEndEventHandler;
import org.matsim.api.core.v01.events.handler.ActivityStartEventHandler;
import org.matsim.core.utils.charts.XYLineChart;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.core.utils.io.UncheckedIOException;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.facilities.ActivityFacility;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class MySIREventHandler implements ActivityStartEventHandler,ActivityEndEventHandler {
    private ActivityFacilities activityFacilities;
    private Set<Id> infectedPersonsIds;
    private Set<Id> recoveredPersonsIds;
    // create an empty map with facilities ids as keys and empty lists as values
    public Map<Id,Set<Id>> facilitesVisitors = new HashMap();
    private Double infectionProbability;

    // initialize XY plot
    private List<Integer> infected = new ArrayList<Integer>();
    private List<Double> time = new ArrayList<Double>();
    // initialize XY plot
    int[] infectedV = new int[25];

    // initialize output file
    private BufferedWriter writer = null;


    public MySIREventHandler(Scenario scenario, Set<Id> infectedPersonsIds,Double infectionProbability, Set<Id> recoveredPersonsIds){
        this.activityFacilities = scenario.getActivityFacilities();
        this.infectedPersonsIds = infectedPersonsIds;
        this.recoveredPersonsIds = recoveredPersonsIds;
        this.infectionProbability = infectionProbability;


        // go through all facilities and append Id to map
        for ( ActivityFacility fac : activityFacilities.getFacilities().values() ) {
            Set<Id> entries = new HashSet<Id>();
            facilitesVisitors.put(fac.getId(),entries);
        }

    }

    @Override
    public void handleEvent(ActivityStartEvent event) {
        // todo find a way to account for pt interactions
        if (!"pt interaction".equals(event.getActType())) {
            // add person to the facility
            facilitesVisitors.get(event.getFacilityId()).add(event.getPersonId());

            // check if infected person is in the facility
            if (!Collections.disjoint(facilitesVisitors.get(event.getFacilityId()),infectedPersonsIds)){
                // check if person is immune (recoverd)
                if (!recoveredPersonsIds.contains(event.getPersonId())) {
                    // infect person with probability p
                    double rand = Math.random();

                    if (rand < infectionProbability) {
                        infectedPersonsIds.add(event.getPersonId());
                    }

                    // Update plot vectors
                    double hour = event.getTime() / 3600;
                    this.infectedV[(int) hour] = infectedPersonsIds.size();
//                    this.infected.add(infectedPersonsIds.size());
//                    this.time.add(event.getTime() / 3600);
                }
            }
        }

    }


    @Override
    public void handleEvent(ActivityEndEvent event) {
        // todo find a way to account for pt interactions
        if (!"pt interaction".equals(event.getActType())) {
            // remove person from the facility
            facilitesVisitors.get(event.getFacilityId()).remove(event.getPersonId());
        }
    }

    @Override
    public void reset(int iteration) {
        System.out.println("reset...");
    }

    public void printInfected(){
        System.out.println(infectedPersonsIds.size());
    }

    public void writeChart(String filename){
        double[] y = new double[this.infected.size()];
        double[] x = new double[this.infected.size()];
        for (int i = 0; i < y.length; i++) {
            y[i] = this.infected.get(i);
            x[i] = this.time.get(i);
        }
        XYLineChart chart = new XYLineChart("Spreading","Hour","Infected");
        chart.addSeries("times", x, y);
        chart.saveAsPng(filename, 800, 600);
    }


    public void writeCSV(String filename){
        this.writer = IOUtils.getBufferedWriter(filename);
        try{
            writer.write("time,infected");
//            for (int i=0; i<this.time.size();i++){
//                writer.newLine();
//                writer.write(this.time.get(i)+","+this.infected.get(i));
//            }
            for (int i=0; i<25;i++){
                writer.newLine();
                writer.write(i+","+this.infectedV[i]);
            }
            writer.close();
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

}
