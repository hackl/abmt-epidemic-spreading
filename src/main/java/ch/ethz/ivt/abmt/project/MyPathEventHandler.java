package ch.ethz.ivt.abmt.project;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.*;
import org.matsim.api.core.v01.events.handler.*;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.core.utils.io.UncheckedIOException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class MyPathEventHandler implements ActivityEndEventHandler,ActivityStartEventHandler,LinkEnterEventHandler,LinkLeaveEventHandler,PersonLeavesVehicleEventHandler,PersonEntersVehicleEventHandler{
    private Network network;
    // initialize map of travelers
    private Map<Id,Traveler> travelers = new HashMap<Id, Traveler>();
    // initialize output writer
    private BufferedWriter writer = null;


    public MyPathEventHandler(Network network,Set<Id> myPersonIds){
        this.network = network;

        // go through all persons and append Id to map
        for ( Id person : myPersonIds ) {
            travelers.put(person,new Traveler(person));
        }

    }

    @Override
    public void handleEvent(ActivityEndEvent event) {
        // do not consider pt interactions
        if (!"pt interaction".equals(event.getActType())) {

            // check if the person is in the map of travelers
            Traveler value = travelers.get(event.getPersonId());
            if (value != null){
                Id<Link> linkId = event.getLinkId();
                Link link = network.getLinks().get(linkId);
                Coord coord = link.getCoord();

                // add data to the traveler
                travelers.get(event.getPersonId()).addPathPoint(coord.getX(),coord.getY(),event.getTime(),event.getEventType(),event.getFacilityId(),event.getActType());
            }
        }
    }

    @Override
    public void handleEvent(ActivityStartEvent event) {
        // do not consider pt interactions
        if (!"pt interaction".equals(event.getActType())) {

            // check if the person is in the map of travelers
            Traveler value = travelers.get(event.getPersonId());
            if (value != null){
                Id<Link> linkId = event.getLinkId();
                Link link = network.getLinks().get(linkId);
                Coord coord = link.getCoord();

                // add data to the traveler
                travelers.get(event.getPersonId()).addPathPoint(coord.getX(),coord.getY(),event.getTime(),event.getEventType(),event.getFacilityId(),event.getActType());
            }
        }
    }

    @Override
    public void handleEvent(LinkEnterEvent event) {
        // check if the traveler is using a vehicle
        for (Traveler traveler:travelers.values()){
            if (event.getVehicleId().equals(traveler.getVehicleId())){
                Id<Link> linkId = event.getLinkId();
                Link link = network.getLinks().get(linkId);
                Coord coord = link.getCoord();

                // add data to the traveler
                traveler.addPathPoint(coord.getX(),coord.getY(),event.getTime(),event.getEventType(),event.getVehicleId(),"travel");
            }
        }
    }

    @Override
    public void handleEvent(LinkLeaveEvent event) {
        // check if the traveler is using a vehicle
        for (Traveler traveler: travelers.values()){
            if (event.getVehicleId().equals(traveler.getVehicleId())){
                Id<Link> linkId = event.getLinkId();
                Link link = network.getLinks().get(linkId);
                Coord coord = link.getCoord();

                // add data to the traveler
                traveler.addPathPoint(coord.getX(),coord.getY(),event.getTime(),event.getEventType(),event.getVehicleId(),"travel");
            }
        }
    }

    @Override
    public void reset(int iteration) {
        System.out.println("reset ...");
    }

    @Override
    public void handleEvent(PersonEntersVehicleEvent event) {
        // check if the person is in the map of travelers
        Traveler value = travelers.get(event.getPersonId());
        if (value != null){

            // Assign vehicle to traveler
            travelers.get(event.getPersonId()).setVehicleId(event.getVehicleId());
        }
    }

    @Override
    public void handleEvent(PersonLeavesVehicleEvent event) {
        // check if the person is in the map of travelers
        Traveler value = travelers.get(event.getPersonId());
        if (value != null){

            // Remove vehicle from traveler
            travelers.get(event.getPersonId()).setVehicleId(null);
        }
    }

    public void printMisc(){
        for (Traveler trav: travelers.values()){
            //trav.writePaths2CSV(trav.getTravelerId().toString()+".csv");
            ArrayList myList = trav.getPathList();
            System.out.println(myList);
        }

        //traveler.writePaths2CSV("test.csv");
        //System.out.println(traveler.getPathList());


    }

    public void writeCSV(String filename){
        writer = IOUtils.getBufferedWriter(filename);
        try{
            writer.write("id,x,y,time");
            for (Traveler trav: travelers.values()){
                ArrayList<Map<String,Object>> pathList = trav.getPathList();
                for (Map<String,Object> row: pathList){
                    writer.newLine();
                    writer.write(trav.getTravelerId() +","+
                            row.get("x").toString()+","+
                            row.get("y").toString()+","+
                            row.get("t").toString());
                }
            }
            writer.close();
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

}
