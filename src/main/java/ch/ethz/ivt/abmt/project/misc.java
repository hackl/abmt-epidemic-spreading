package ch.ethz.ivt.abmt.project;


/**
import org.apache.commons.math3.stat.descriptive.SynchronizedSummaryStatistics;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.*;
import org.matsim.api.core.v01.events.handler.*;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.core.utils.io.UncheckedIOException;
import org.matsim.core.utils.misc.Time;
import org.matsim.vehicles.Vehicle;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MyPathEventHandler implements ActivityEndEventHandler,ActivityStartEventHandler,LinkEnterEventHandler,LinkLeaveEventHandler,PersonLeavesVehicleEventHandler,PersonEntersVehicleEventHandler{
    private Id<Person> myPersonId;
    private Network network;
    private Map<Id,Id> vehicleTakenByPerson = new HashMap<Id, Id>();
    private Traveler traveler;
    private Map<Id,Traveler> travelers = new HashMap<Id, Traveler>();
    private Set<Id> myPersonIds;

    // initialize output file
    private BufferedWriter writer = null;
    private final String filePath = "output/xy.csv";

    public MyPathEventHandler(Id<Person> myPersonId,Network network,Set<Id> myPersonIds){
        this.network = network;
        this.myPersonId = myPersonId;

        // go through all facilities and append Id to map
        for ( Id person : myPersonIds ) {
            travelers.put(person,new Traveler(person));
        }
        //traveler.setTravelerId(myPersonId);
        traveler = new Traveler(myPersonId);
        // open file
        openFile();
    }


    @Override
    public void handleEvent(ActivityEndEvent event) {
        if (!"pt interaction".equals(event.getActType())) {
            if (event.getPersonId().equals(traveler.getTravelerId())) {

                Id<Link> linkId = event.getLinkId();
                Link link = network.getLinks().get(linkId);
                Coord coord = link.getCoord();

                // write output to file
                writeNewLine(this.myPersonId,"endAct",coord.getX(),coord.getY(),event.getTime());
                traveler.addPathPoint(coord.getX(),coord.getY(),event.getTime(),event.getEventType(),event.getFacilityId(),event.getActType());
            }
        }

    }

    @Override
    public void handleEvent(ActivityStartEvent event) {
        if (!"pt interaction".equals(event.getActType())) {
            if (event.getPersonId().equals(this.myPersonId)) {

                Id<Link> linkId = event.getLinkId();
                Link link = network.getLinks().get(linkId);
                Coord coord = link.getCoord();

                // write output to file
                writeNewLine(this.myPersonId,"startAct",coord.getX(),coord.getY(),event.getTime());
                traveler.addPathPoint(coord.getX(),coord.getY(),event.getTime(),event.getEventType(),event.getFacilityId(),event.getActType());
            }
        }
    }

    @Override
    public void handleEvent(LinkEnterEvent event) {
        if (event.getVehicleId().equals(traveler.getVehicleId())){

            Id<Link> linkId = event.getLinkId();
            Link link = network.getLinks().get(linkId);
            Coord coord = link.getCoord();

            // write output to file
            writeNewLine(this.myPersonId,"startTrav",coord.getX(),coord.getY(),event.getTime());
            traveler.addPathPoint(coord.getX(),coord.getY(),event.getTime(),event.getEventType(),event.getVehicleId(),"travel");
        }

    }





    @Override
    public void handleEvent(LinkLeaveEvent event) {
        if (event.getVehicleId().equals(traveler.getVehicleId())){

            Id<Link> linkId = event.getLinkId();
            Link link = network.getLinks().get(linkId);
            Coord coord = link.getCoord();

            // write output to file
            writeNewLine(this.myPersonId,"endTrav",coord.getX(),coord.getY(),event.getTime());
            traveler.addPathPoint(coord.getX(),coord.getY(),event.getTime(),event.getEventType(),event.getVehicleId(),"travel");
        }
    }

    @Override
    public void reset(int iteration) {

    }

    @Override
    public void handleEvent(PersonEntersVehicleEvent event) {
        if(event.getPersonId().equals(traveler.getTravelerId())){
            traveler.setVehicleId(event.getVehicleId());
            //vehicleTakenByPerson.put(this.myPersonId,event.getVehicleId());
        }
    }

    @Override
    public void handleEvent(PersonLeavesVehicleEvent event) {
        if(event.getPersonId().equals(traveler.getTravelerId())){
            traveler.setVehicleId(null);
            //vehicleTakenByPerson.remove(this.myPersonId);
        }
    }

    public void printMisc(){
        traveler.writePaths2CSV("test.csv");
        //System.out.println(traveler.getPathList());


    }

    public void writeNewLine(Id<Person> personId,String event, Double coordX, Double coordY, Double time){
        // write output to file
        try{
            writer.newLine();
            writer.write(personId+","+event+","+coordX+","+coordY+","+time);
        }catch (IOException e){
            throw new UncheckedIOException(e);
        }
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
            writer.write("personId,event,x,y,time");
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }
}
**/