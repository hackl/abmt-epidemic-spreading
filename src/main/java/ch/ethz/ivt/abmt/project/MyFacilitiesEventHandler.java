package ch.ethz.ivt.abmt.project;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.ActivityEndEvent;
import org.matsim.api.core.v01.events.ActivityStartEvent;
import org.matsim.api.core.v01.events.handler.ActivityEndEventHandler;
import org.matsim.api.core.v01.events.handler.ActivityStartEventHandler;
import org.matsim.core.controler.events.ShutdownEvent;
import org.matsim.core.controler.events.StartupEvent;
import org.matsim.core.controler.listener.ShutdownListener;
import org.matsim.core.controler.listener.StartupListener;
import org.matsim.core.utils.charts.XYLineChart;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.core.utils.io.UncheckedIOException;
import org.matsim.core.utils.misc.Time;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.facilities.ActivityFacility;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class MyFacilitiesEventHandler implements ActivityStartEventHandler,ActivityEndEventHandler,StartupListener,ShutdownListener{
    private ActivityFacilities activityFacilities;
    private List<Id> randomPersonsIds;
    // create an empty map with facilities ids as keys and empty lists as values
    public Map<Id,List<Id>> facilitesVisitors = new HashMap();

    // initialize output file
    private BufferedWriter writer = null;
    private final String filePath = "test.csv";

    // initialize XY plot
    private List<Integer> infected = new ArrayList<Integer>();
    private List<Double> time = new ArrayList<Double>();
    private final String plotPath = "test.png";



    public MyFacilitiesEventHandler(ActivityFacilities activityFacilities,List<Id> randomPersonsIds){
        this.activityFacilities = activityFacilities;
        this.randomPersonsIds = randomPersonsIds;


        // go through all facilities and append Id to map
        for ( ActivityFacility fac : activityFacilities.getFacilities().values() ) {
            List<Id> entries = new ArrayList<Id>();
            facilitesVisitors.put(fac.getId(),entries);
        }

        // open file
        // todo find a better solution
        openFile();
    }

    @Override
    public void handleEvent(ActivityStartEvent event) {
        // todo find a way to account for pt interactions
        if (!"pt interaction".equals(event.getActType())) {
            // add person to the facility
            facilitesVisitors.get(event.getFacilityId()).add(event.getPersonId());

            // check if infected person is in the facility
            // todo code is very slow, probably there is a better solution
            if (!Collections.disjoint(facilitesVisitors.get(event.getFacilityId()),randomPersonsIds)){
                randomPersonsIds.add(event.getPersonId());

                // write output to file
                try{
                    writer.newLine();
                    writer.write(event.getTime()+"\t"+
                                    event.getPersonId()+"\t"+
                                    randomPersonsIds.size()+"\t"+
                                    Time.writeTime(event.getTime())+"\t"+
                                    event.getActType());
                }catch (IOException e){
                    throw new UncheckedIOException(e);
                }

                // Update plot vectors
                this.infected.add(randomPersonsIds.size());
                this.time.add(event.getTime()/3600);

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

    public void printVisitors(){
        System.out.println(facilitesVisitors);
    }

    public void printInfected(){
        System.out.println(randomPersonsIds.size());
    }

    public void writeChart(){
        double[] y = new double[this.infected.size()];
        double[] x = new double[this.infected.size()];
        for (int i = 0; i < y.length; i++) {
            y[i] = this.infected.get(i);
            x[i] = this.time.get(i);
        }
        XYLineChart chart = new XYLineChart("Spreading","Hour","Infected");
        chart.addSeries("times", x, y);
        chart.saveAsPng(plotPath, 800, 600);
    }

    public void closeFile(){
        try{
            writer.close();
        }catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    public void openFile(){
        this.writer = IOUtils.getBufferedWriter(filePath);
        try{
            writer.write("time\tpersonId\tsumInfected\tsTime\tactType");
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void notifyStartup(StartupEvent event) {
        openFile();
    }


    @Override
    public void notifyShutdown(ShutdownEvent event) {
        closeFile();
    }


}
