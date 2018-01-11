package ch.ethz.ivt.abmt.project.archive.tutorials;

import java.util.*;

public class RunSimpleScripts {
    public static void main(String[] args){
        /**
         * create a map of lists and add to the list
         * **/
        Map<Integer, List<String>> myMap = new HashMap();
        for (int i = 0; i<5; i++){
            List<String> entries = new ArrayList<String>();
            myMap.put(i,entries);
        }

        myMap.get(1).add("A");
        myMap.get(1).add("B");
        myMap.get(1).add("C");
        myMap.get(1).add("D");
        System.out.println(myMap);
        System.out.println(myMap.get(1));
        myMap.get(1).remove("A");
        System.out.println(myMap);

        // find single value
        System.out.println(myMap.get(1).contains("A"));

        List<String> myList = new ArrayList<String>();
        myList.add("C");
        myList.add("Y");
        myList.add("Z");

        System.out.println(myList);
        System.out.println(!Collections.disjoint(myMap.get(1),myList));


        Set<String> intersection = new HashSet<String>(myMap.get(1)); // use the copy constructor
        intersection.retainAll(myList);

        System.out.println("inter"+intersection);

        if(intersection.size() == 0){
            System.out.println("ddddddddddddddddddd");
        }


        Set setboth = new HashSet(myList);
        setboth.addAll(myMap.get(1));
        myList.clear();
        myList.addAll(setboth);

        System.out.println(myList);

        System.out.println("Events file read!");

        Set<String> facilitesVisitors = new HashSet<>();
        Set<String> infectedPersonsIds = new HashSet<>();

        facilitesVisitors.add("A");
        facilitesVisitors.add("B");
        facilitesVisitors.add("C");
        facilitesVisitors.add("D");

        infectedPersonsIds.add("C");
        infectedPersonsIds.add("E");

        System.out.println(!Collections.disjoint(facilitesVisitors,infectedPersonsIds));
        //if (!Collections.disjoint(facilitesVisitors.get(event.getFacilityId()),infectedPersonsIds)){

        //Set<String> intersection = new HashSet<>(); // use the copy constructor
        System.out.println(facilitesVisitors.retainAll(infectedPersonsIds));


        int[] a = new int[] {0, 0, 0, -1, 1, 8, 3, 0, 0};

        int value = 0;
        for (int i:a) {
            if(i>0){
                value=i;
                break;
            }
        }

        for (int i=0;i<a.length;i++) {
            if(a[i]<1){
                a[i] = value;
            }
        }
        for(int i:a) {
            System.out.println(i);
        }
        //System.out.println("second lareges "+ secondLargest(a));

        //        // go through all facilities and print Id
//        for ( ActivityFacility fac : activityFacilities.getFacilities().values() ) {
//            System.out.println("facId " + fac.getId());
//        }


        //        int numOfPersons = 0;
//        for (Person person : population.getPersons().values()){
//            numOfPersons += 1;
//            System.out.println("personId "+ person.getId());
//            System.out.println(numOfPersons);
//        }
        // Number of persons considered
        // System.out.println(population.getPersons().size());

        //population.getPersons().keySet()



        //Vehicles vehicles = (Vehicles) scenario.getTransitVehicles().getVehicles();

//        System.out.println(vehicles.getVehicles().size());
//
//        for (Vehicle vec: vehicles.getVehicles().values()){
//            System.out.println(vec.getId());
//        }
    }



}
