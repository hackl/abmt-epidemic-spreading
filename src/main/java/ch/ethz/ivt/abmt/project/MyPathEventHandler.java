package ch.ethz.ivt.abmt.project;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.*;
import org.matsim.api.core.v01.events.handler.*;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.core.utils.io.UncheckedIOException;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.facilities.ActivityFacility;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.Vehicles;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class MyPathEventHandler implements ActivityEndEventHandler,ActivityStartEventHandler,LinkEnterEventHandler,LinkLeaveEventHandler,PersonLeavesVehicleEventHandler,PersonEntersVehicleEventHandler{
    private Network network;
    private Vehicles vehicles;
    private ActivityFacilities activityFacilities;
    // initialize map of travelers
    private Map<Id,Traveler> travelers = new HashMap<Id, Traveler>();
    // initialize output writer
    private BufferedWriter writer = null;
    // create an empty map with facilities ids as keys and empty set as values
    public Map<Id,Set<Id>> facilitesVisitors = new HashMap();
    // create an empty map with vehicle ids as keys and empty set as values
    private Map<Id,Set<Id>> vehicleVisitors = new HashMap();
    public Set<Id> myPersonsIds;

    public MyPathEventHandler(Scenario scenario, Set<Id> myPersonsIds){
        this.network = scenario.getNetwork();
        this.activityFacilities = scenario.getActivityFacilities();
        this.myPersonsIds = myPersonsIds;
        this.vehicles = scenario.getVehicles();
        // go through all persons and append Id to map
        for ( Id person : myPersonsIds ) {
            travelers.put(person,new Traveler(person));
        }

        // go through all facilities and append Id to map
        for ( ActivityFacility facility : activityFacilities.getFacilities().values() ) {
            facilitesVisitors.put(facility.getId(),new HashSet<Id>());
        }

        // go through all facilities and append Id to map
        for ( Vehicle vec : vehicles.getVehicles().values() ) {
            vehicleVisitors.put(vec.getId(),new HashSet<Id>());
        }

    }

    @Override
    public void handleEvent(ActivityEndEvent event) {
        // do not consider pt interactions
        if (!"pt interaction".equals(event.getActType())) {

//            System.out.println("facId "+event.getFacilityId());
//            System.out.println(facilitesVisitors.get(event.getFacilityId()));
            Set<Id> intersection = new HashSet<Id>(facilitesVisitors.get(event.getFacilityId())); // use the copy constructor
            intersection.retainAll(myPersonsIds);

            if(intersection.size() != 0){
                for(Id personId:intersection){
//                    travelers.get(personId).addFriendsIds(facilitesVisitors.get(event.getFacilityId()));
                    travelers.get(personId).addFriendInteraction(event.getPersonId(),event.getTime(),"disconnect2",event.getFacilityId(),event.getLinkId(),event.getActType());
                }
            }

            // remove person from the facility
            facilitesVisitors.get(event.getFacilityId()).remove(event.getPersonId());

            // check if the person is in the map of travelers
            Traveler value = travelers.get(event.getPersonId());
            if (value != null){
                Id<Link> linkId = event.getLinkId();
                Link link = network.getLinks().get(linkId);
                Coord coord = link.getCoord();

                // add data to the traveler
                travelers.get(event.getPersonId()).addPathPoint(coord.getX(),coord.getY(),event.getTime(),event.getEventType(),event.getFacilityId(),event.getLinkId(),event.getActType());

                // add friends to the traveler
                for (Id friend:facilitesVisitors.get(event.getFacilityId())){
                    travelers.get(event.getPersonId()).addFriendInteraction(friend,event.getTime(),"disconnect1",event.getFacilityId(),event.getLinkId(),event.getActType());
                }
                //travelers.get(event.getPersonId()).addFriendsIds(facilitesVisitors.get(event.getFacilityId()));

            }
        }
    }

    @Override
    public void handleEvent(ActivityStartEvent event) {
        // do not consider pt interactions
        if (!"pt interaction".equals(event.getActType())) {

            // add person to the facility
            facilitesVisitors.get(event.getFacilityId()).add(event.getPersonId());

            // check if infected person is in the facility
            // todo code is very slow, probably there is a better solution
//            if (!Collections.disjoint(facilitesVisitors.get(event.getFacilityId()),myPersonsIds)){
//                myPersonsIds.
//            }
//                infectedPersonsIds.add(event.getPersonId());

            Set<Id> intersection = new HashSet<Id>(facilitesVisitors.get(event.getFacilityId())); // use the copy constructor
            intersection.retainAll(myPersonsIds);

            if(intersection.size() != 0){
                for(Id personId:intersection){
                    travelers.get(personId).addFriendsIds(facilitesVisitors.get(event.getFacilityId()));
                    travelers.get(personId).addFriendInteraction(event.getPersonId(),event.getTime(),"connect2",event.getFacilityId(),event.getLinkId(),event.getActType());
                }
            }


            // check if the person is in the map of travelers
            Traveler value = travelers.get(event.getPersonId());
            if (value != null){
                Id<Link> linkId = event.getLinkId();
                Link link = network.getLinks().get(linkId);
                Coord coord = link.getCoord();

                // add data to the traveler
                travelers.get(event.getPersonId()).addPathPoint(coord.getX(),coord.getY(),event.getTime(),event.getEventType(),event.getFacilityId(),event.getLinkId(),event.getActType());

                // add friends to the traveler
                for (Id friend:facilitesVisitors.get(event.getFacilityId())){
                    travelers.get(event.getPersonId()).addFriendInteraction(friend,event.getTime(),"connect1",event.getFacilityId(),event.getLinkId(),event.getActType());
                }
                //travelers.get(event.getPersonId()).addFriendsIds(facilitesVisitors.get(event.getFacilityId()));

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
                traveler.addPathPoint(coord.getX(),coord.getY(),event.getTime(),event.getEventType(),event.getVehicleId(),event.getLinkId(),"travel");
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
                traveler.addPathPoint(coord.getX(),coord.getY(),event.getTime(),event.getEventType(),event.getVehicleId(),event.getLinkId(),"travel");
            }
        }
    }

    @Override
    public void reset(int iteration) {
        System.out.println("reset ...");
    }

    @Override
    public void handleEvent(PersonEntersVehicleEvent event) {
        // consider only public transport
        // todo fix the way how private vs public is divided
        if (!Character.isDigit(event.getVehicleId().toString().charAt(0))) {
            // go through all persons without considering the bus drivers
            if (Character.isDigit(event.getPersonId().toString().charAt(0))) {

                // add person to the vehicle
                vehicleVisitors.get(event.getVehicleId()).add(event.getPersonId());


                Set<Id> intersection = new HashSet<Id>(vehicleVisitors.get(event.getVehicleId())); // use the copy constructor
                intersection.retainAll(myPersonsIds);

                if (intersection.size() != 0) {
                    for (Id personId : intersection) {
                        travelers.get(personId).addFriendInteraction(event.getPersonId(), event.getTime(), "connect2", event.getVehicleId(), null, "pt");
                    }
                    //travelers.get(personId).addFriendInteraction(event.getPersonId(), event.getTime(), "connect2", event.getVehicleId(), null, "pt");
                }
            }
        }



        // check if the person is in the map of travelers
        if (travelers.get(event.getPersonId()) != null){

            // Assign vehicle to traveler
            travelers.get(event.getPersonId()).setVehicleId(event.getVehicleId());

            // consider only public transport
            // todo fix the way how private vs public is divided
            if (!Character.isDigit(event.getVehicleId().toString().charAt(0))) {
                // go through all persons without considering the bus drivers
                if (Character.isDigit(event.getPersonId().toString().charAt(0))) {
                    // add friends to the traveler
                    for (Id friend:vehicleVisitors.get(event.getVehicleId())){
                        travelers.get(event.getPersonId()).addFriendInteraction(friend,event.getTime(),"connect1",event.getVehicleId(),null,"pt");
                    }
                }
            }

            // add data to the traveler
            //travelers.get(event.getPersonId())
            //System.out.println(event.getAttributes());
        }
    }

    @Override
    public void handleEvent(PersonLeavesVehicleEvent event) {
        // consider only public transport
        // todo fix the way how private vs public is divided
        if (!Character.isDigit(event.getVehicleId().toString().charAt(0))) {
            // go through all persons without considering the bus drivers
            if (Character.isDigit(event.getPersonId().toString().charAt(0))) {

                Set<Id> intersection = new HashSet<Id>(vehicleVisitors.get(event.getVehicleId())); // use the copy constructor
                intersection.retainAll(myPersonsIds);

                if (intersection.size() != 0) {
                    for (Id personId : intersection) {
                        travelers.get(personId).addFriendInteraction(event.getPersonId(), event.getTime(), "disconnect2", event.getVehicleId(), null, "pt");
                    }
                    //travelers.get(personId).addFriendInteraction(event.getPersonId(), event.getTime(), "connect2", event.getVehicleId(), null, "pt");
                }

                // remove person to the vehicle
                vehicleVisitors.get(event.getVehicleId()).remove(event.getPersonId());
            }
        }
        // check if the person is in the map of travelers
        if (travelers.get(event.getPersonId()) != null){

            // Remove vehicle from traveler
            travelers.get(event.getPersonId()).setVehicleId(null);

            // consider only public transport
            // todo fix the way how private vs public is divided
            if (!Character.isDigit(event.getVehicleId().toString().charAt(0))) {
                // go through all persons without considering the bus drivers
                if (Character.isDigit(event.getPersonId().toString().charAt(0))) {
                    // add friends to the traveler
                    for (Id friend:vehicleVisitors.get(event.getVehicleId())){
                        travelers.get(event.getPersonId()).addFriendInteraction(friend,event.getTime(),"connect1",event.getVehicleId(),null,"pt");
                    }
                }
            }
//            // add friends to the traveler
//            for (Id friend:vehicleVisitors.get(event.getVehicleId())){
//                travelers.get(event.getPersonId()).addFriendInteraction(friend,event.getTime(),"disconnect1",event.getVehicleId(),null,"pt");
//            }
        }
    }

    public void printMisc(){
        for (Traveler traveler: travelers.values()){
            //trav.writePaths2CSV(trav.getTravelerId().toString()+".csv");
            //ArrayList myList = traveler.getPathList();
            //System.out.println(myList.size());
            //traveler.writePaths2CSV("text.csv");
//            traveler.getFriendsIds();
            //traveler.getFriends();
        }

        //traveler.writePaths2CSV("test.csv");
        //System.out.println(traveler.getPathList());


    }

    public void writeCSV(String filename){
        writer = IOUtils.getBufferedWriter(filename);
        try{
            writer.write("id,x,y,t1,t2");
            for (Traveler traveler: travelers.values()){
                ArrayList<Map<String,Object>> pathList = traveler.getPathList();
                for (Map<String,Object> row: pathList){
                    writer.newLine();
                    writer.write(traveler.getTravelerId() +","+
                            row.get("x").toString()+","+
                            row.get("y").toString()+","+
                            row.get("t1").toString()+","+
                            row.get("t2").toString());//+","+
                            //row.get("actType").toString());
                }
            }
            writer.close();
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

}
