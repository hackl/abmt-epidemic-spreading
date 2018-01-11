package ch.ethz.ivt.abmt.project;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.core.utils.io.UncheckedIOException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

/**
 * Traveler
 *
 * Class to collect specific information from the traffic simulation
 *
 * **/


public class Traveler {
    // initialize variables
    private Id<Person> travelerId;
    private Id vehicleId;
    private ArrayList<Map<String,Object>> pathListRaw = new ArrayList<Map<String, Object>>();
    private ArrayList<Map<String,Object>> pathList = new ArrayList<Map<String, Object>>();
    private ArrayList<Map<String,Object>> friendList = new ArrayList<Map<String, Object>>();
    private BufferedWriter writer = null;
    private final int maxDayLength = 30;
    private Set<Id> friendsIds = new HashSet<Id>();
    private Map<Id,ArrayList<Map<String,Object>>> friends = new HashMap();

    public Traveler(Id<Person> travelerId){
        this.travelerId = travelerId;
    }

    public Id<Person> getTravelerId(){
        return travelerId;
    }

    public Id getVehicleId(){
        return vehicleId;
    }

    public void setVehicleId(Id vehicleId){
        this.vehicleId = vehicleId;
    }

    public void setTravelerId(Id<Person> travelerId) {
        this.travelerId = travelerId;
    }

    public void addPathPoint(Double x,Double y, Double t, String eventType, Id miscId,Id linkId, String actType){
        Map<String,Object> pathPoint = new HashMap();
        pathPoint.put("x",x);
        pathPoint.put("y",y);
        pathPoint.put("t",t);
        pathPoint.put("eventType",eventType);
        pathPoint.put("miscId",miscId);
        pathPoint.put("linkId",linkId);
        pathPoint.put("actType",actType);
        pathListRaw.add(pathPoint);
    }

    public  void addFriendsIds(Set<Id> newFriendsIds){
        friendsIds.addAll(newFriendsIds);
    }

    public void addFriendInteraction(Id personId,Double time,String eventType, Id miscId,Id linkId, String actType){
        if(friends.get(personId) != null){
            Map<String,Object> entry = new HashMap();
            entry.put("t",time);
            entry.put("eventType",eventType);
            entry.put("miscId",miscId);
            entry.put("linkId",linkId);
            entry.put("actType",actType);
            friends.get(personId).add(entry);
        }
        else{
            ArrayList<Map<String,Object>> attributes = new ArrayList<Map<String, Object>>();
            Map<String,Object> entry = new HashMap();
            entry.put("t",time);
            entry.put("eventType",eventType);
            entry.put("miscId",miscId);
            entry.put("linkId",linkId);
            entry.put("actType",actType);
            attributes.add(entry);
            friends.put(personId,attributes);
        }
    }

    public ArrayList getFriendList(){
        // remove self loops
        friends.remove(travelerId);
        for (Map.Entry<Id,ArrayList<Map<String,Object>>> friend:friends.entrySet()){

            double times [] = new double[(int) Math.ceil((double)friend.getValue().size()/2)*2];
            for (int i=0;i<friend.getValue().size();i++){
                times[i] = new Double(friend.getValue().get(i).get("t").toString());
            }

            if(friend.getValue().size()<times.length){
                times[times.length-1] = 3600 * maxDayLength;
            }

            for (int i=0;i<times.length/2;i++){
                Map<String, Object> connectionPoint = new HashMap();
                connectionPoint.put("u",travelerId);
                connectionPoint.put("v",friend.getKey());
                for (int j=0;j<2;j++){
                    if(j==0){
                        connectionPoint.put("t1",times[i*2+j]);
                    }
                    else{
                        connectionPoint.put("t2",times[i*2+j]);
                    }
                }
                friendList.add(connectionPoint);
            }
        }

        return friendList;
    }

    public ArrayList getPathListRaw() {
        return pathListRaw;
    }

    public ArrayList getPathList() {

        if (pathListRaw.size() == 1){
            Map v = pathListRaw.get(0);
            Map<String, Object> pathPoint = new HashMap();
            pathPoint.put("x", v.get("x"));
            pathPoint.put("y", v.get("y"));
            pathPoint.put("t1", v.get("t"));
            pathPoint.put("t2", 3600 * maxDayLength);
            //pathPoint.put("actType",v.get("actType"));
            pathList.add(pathPoint);
        }
        else if (pathListRaw.size()>1){
        for(int i = 0;i<pathListRaw.size()-1;i++) {
            Map u = pathListRaw.get(i);
            Map v = pathListRaw.get(i + 1);
            Map<String, Object> pathPoint = new HashMap();

            if ((Double) u.get("t") - (Double) v.get("t") != 0.0) {
                pathPoint.put("x", u.get("x"));
                pathPoint.put("y", u.get("y"));
                pathPoint.put("t1", u.get("t"));
                pathPoint.put("t2", v.get("t"));
                //pathPoint.put("actType",u.get("actType"));
                pathList.add(pathPoint);

                if (i == pathListRaw.size() - 2) {
                    pathPoint.put("x", v.get("x"));
                    pathPoint.put("y", v.get("y"));
                    pathPoint.put("t1", v.get("t"));
                    pathPoint.put("t2", 3600 * maxDayLength);
                    //pathPoint.put("actType",v.get("actType"));
                    pathList.add(pathPoint);
                }
            }
        }

        if(pathList.size()>2) {
            pathList.remove(pathList.size() - 1);
        }

        }
        return pathList;
    }

    public void writePaths2CSV(String filename){
        this.writer = IOUtils.getBufferedWriter(filename);
        try{
            writer.write("id,x,y,time,event");
            for (Map<String,Object> row:pathListRaw){
                writer.newLine();
                writer.write(this.travelerId +","+
                        row.get("x").toString()+","+
                        row.get("y").toString()+","+
                        row.get("t").toString()+","+
                        row.get("eventType").toString());
            }
           writer.close();
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }

    }
}
