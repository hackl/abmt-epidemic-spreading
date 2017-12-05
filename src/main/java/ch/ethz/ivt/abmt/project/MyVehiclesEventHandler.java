package ch.ethz.ivt.abmt.project;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.PersonLeavesVehicleEvent;
import org.matsim.api.core.v01.events.handler.PersonEntersVehicleEventHandler;
import org.matsim.api.core.v01.events.handler.PersonLeavesVehicleEventHandler;
import org.matsim.core.utils.charts.XYLineChart;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.core.utils.io.UncheckedIOException;
import org.matsim.core.utils.misc.Time;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.Vehicles;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class MyVehiclesEventHandler implements PersonEntersVehicleEventHandler,PersonLeavesVehicleEventHandler{
    private Vehicles vehicles;
    private Set<Id> infectedPersonsIds;
    // create an empty map with vehicle ids as keys and a empty set as values
    private Map<Id,Set<Id>> vehicleVisitors = new HashMap<Id, Set<Id>>();

    // initialize output file
    private BufferedWriter writer = null;
    private final String filePath = "vehicles.csv";

    // initialize XY plot
    private List<Integer> infected = new ArrayList<Integer>();
    private List<Double> time = new ArrayList<Double>();
    private final String plotPath = "vehicles.png";



    public MyVehiclesEventHandler(Vehicles vehicles, Set<Id> infectedPersonsIds){
        this.vehicles = vehicles;
        this.infectedPersonsIds = infectedPersonsIds;

        // go through all facilities and append Id to map
        for ( Vehicle vec : vehicles.getVehicles().values() ) {
            Set<Id> entries = new HashSet<Id>();
            vehicleVisitors.put(vec.getId(),entries);
        }

        // open file
        // todo find a better solution
        openFile();

    }

    @Override
    public void handleEvent(PersonEntersVehicleEvent event) {
        // consider only public transport
        // todo fix the way how private vs public is divided
        if (!Character.isDigit(event.getVehicleId().toString().charAt(0))){

            // go through all persons without considering the bus drivers
            if (Character.isDigit(event.getPersonId().toString().charAt(0))){

                // add person to the vehicle
                vehicleVisitors.get(event.getVehicleId()).add(event.getPersonId());

                // check if infected person is in the vehicle
                if (!Collections.disjoint(vehicleVisitors.get(event.getVehicleId()),infectedPersonsIds)) {
                    infectedPersonsIds.add(event.getPersonId());

                    // write output to file
                    try{
                        writer.newLine();
                        writer.write(event.getTime()+"\t"+
                                event.getPersonId()+"\t"+
                                infectedPersonsIds.size()+"\t"+
                                Time.writeTime(event.getTime())+"\t"+
                                event.getVehicleId());
                    }catch (IOException e){
                        throw new UncheckedIOException(e);
                    }
                    // Update plot vectors
                    this.infected.add(infectedPersonsIds.size());
                    this.time.add(event.getTime()/3600);
                }
            }
        }
    }

    @Override
    public void handleEvent(PersonLeavesVehicleEvent event) {
        // consider only public transport
        // todo fix the way how private vs public is divided
        if (!Character.isDigit(event.getVehicleId().toString().charAt(0))) {

            // go through all persons without considering the bus drivers
            if (Character.isDigit(event.getPersonId().toString().charAt(0))) {

                // add person to the vehicle
                vehicleVisitors.get(event.getVehicleId()).remove(event.getPersonId());
            }
        }
    }

    @Override
    public void reset(int iteration) {
        System.out.println("reset...");
    }

    public void printVisitors(){
        System.out.println(vehicleVisitors);
    }
    public void printInfected(){
        System.out.println(infectedPersonsIds.size());
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
            writer.write("time\tpersonId\tsumInfected\tsTime\tvecId");
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

}
