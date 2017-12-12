package ch.ethz.ivt.abmt.project;

import com.sun.org.apache.xpath.internal.functions.FuncFalse;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.core.utils.io.UncheckedIOException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

public class Traveler {
    private Id<Person> travelerId;
    private Id vehicleId;
    private ArrayList<Map<String,Object>> pathListRaw = new ArrayList<Map<String, Object>>();
    private ArrayList<Map<String,Object>> pathList = new ArrayList<Map<String, Object>>();
    private BufferedWriter writer = null;

//    private HashMap map = new HashMap();
//    private Set<Map.Entry<Id,String>> friends = map.entrySet();
    private Set<Id> friendsIds = new HashSet<Id>();
    private Map<Id,ArrayList<Map<String,Object>>> friends = new HashMap();

/**
 * Class to collect specific information from the traffic simulation
 * **/


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

    public void getFriendsIds(){
        System.out.println("========================");
        System.out.println(friendsIds);
    }

//    public void addFriend(Id personId,Double time){
//        Map<String,Object> entry = new HashMap();
//        entry.put("time1",time);
//        friends.put(personId,entry);
//
//    }

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

//    public void removeFriend(Id personId,Double time){
//
//        if(friends.get(personId) != null){
//            friends.get(personId).put("time2",time);
//        }
//        else{
//            Map<String,Object> entry = new HashMap();
//            entry.put("time2",time);
//            friends.put(personId,entry);
//        }
//    }

    public void getFriends(){
        for (Map.Entry<Id,ArrayList<Map<String,Object>>> friend:friends.entrySet()){
            int flag = 0;
            for (Map<String,Object> interaction : friend.getValue()){
                if (interaction.get("actType").equals("work")){
                    flag += 1;
                }
            }
            if(flag > 0){
                System.out.println(friend.getKey() +"  ->  "+friend.getValue());
            }
            //if(friend.getValue().get())

            //System.out.println(friend.getValue().size());
        }

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
            pathPoint.put("t2", 3600 * 24);
            //pathPoint.put("actType",v.get("actType"));
            pathList.add(pathPoint);
        }
        else{
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
                    pathPoint.put("t2", 3600 * 24);
                    //pathPoint.put("actType",v.get("actType"));
                    pathList.add(pathPoint);
                }
            }

        }

            pathList.remove(pathList.size()-1);


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
