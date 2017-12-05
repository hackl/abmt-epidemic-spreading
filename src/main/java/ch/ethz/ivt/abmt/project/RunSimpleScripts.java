package ch.ethz.ivt.abmt.project;

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
        myList.add("A");
        myList.add("E");

        System.out.println(myList);
        System.out.println(!Collections.disjoint(myMap.get(1),myList));

        Set setboth = new HashSet(myList);
        setboth.addAll(myMap.get(1));
        myList.clear();
        myList.addAll(setboth);

        System.out.println(myList);

        System.out.println("Events file read!");



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
    }
}
