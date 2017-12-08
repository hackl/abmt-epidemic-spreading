package ch.ethz.ivt.abmt.project;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.utils.io.IOUtils;
import org.matsim.core.utils.io.UncheckedIOException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Traveler {
    private Id<Person> travelerId;
    private Id vehicleId;
    private ArrayList<Map<String,Object>> pathList = new ArrayList<Map<String, Object>>();
    private BufferedWriter writer = null;

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

    public void addPathPoint(Double x,Double y, Double t, String eventType, Id miscId,String actType){
        Map<String,Object> pathPoint = new HashMap();
        pathPoint.put("x",x);
        pathPoint.put("y",y);
        pathPoint.put("t",t);
        pathPoint.put("eventType",eventType);
        pathPoint.put("id",miscId);
        pathPoint.put("actType",actType);
        pathList.add(pathPoint);

    }

    public ArrayList getPathList() {
        return pathList;
    }

    public void writePaths2CSV(String filename){
        this.writer = IOUtils.getBufferedWriter(filename);
        try{
            writer.write("id,x,y,time");
            for (Map<String,Object> row:pathList){
                writer.newLine();
                writer.write(this.travelerId +","+
                        row.get("x").toString()+","+
                        row.get("y").toString()+","+
                        row.get("t").toString());
            }
           writer.close();
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }

    }
}
