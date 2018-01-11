package ch.ethz.ivt.abmt.project;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.ActivityEndEvent;
import org.matsim.api.core.v01.events.ActivityStartEvent;
import org.matsim.api.core.v01.events.handler.ActivityEndEventHandler;
import org.matsim.api.core.v01.events.handler.ActivityStartEventHandler;
import org.matsim.core.utils.io.UncheckedIOException;
import org.matsim.facilities.ActivityFacilities;
import org.matsim.facilities.ActivityFacility;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

/**
 * MySIREventHandler
 *
 * Evaluate the events and extract information for the SIR model
 *
 * **/

public class MySIREventHandler implements ActivityStartEventHandler,ActivityEndEventHandler {

    // initialize values
    private ActivityFacilities activityFacilities;
    private Set<Id> infectedPersonsIds;
    private Double infectionProbability;
    private Set<Id> recoveredPersonsIds;
    // create an empty map with facilities ids as keys and empty lists as values
    public Map<Id,Set<Id>> facilitesVisitors = new HashMap();

    // initialize vector to sum up the results per hour
    // in order to deal with plans going beyond 24 hours 30 hours are considered
    int[] infected = new int[30];

    // initialize output file
    private BufferedWriter writer = null;


    public MySIREventHandler(Scenario scenario, Set<Id> infectedPersonsIds,Double infectionProbability, Set<Id> recoveredPersonsIds){
        this.activityFacilities = scenario.getActivityFacilities();
        this.infectedPersonsIds = infectedPersonsIds;
        this.recoveredPersonsIds = recoveredPersonsIds;
        this.infectionProbability = infectionProbability;

        // go through all facilities and append Id to map
        for ( ActivityFacility fac : activityFacilities.getFacilities().values() ) {
            Set<Id> entries = new HashSet<Id>();
            facilitesVisitors.put(fac.getId(),entries);
        }

    }

    @Override
    public void handleEvent(ActivityStartEvent event) {
        // todo find a way to account for pt interactions
        if (!"pt interaction".equals(event.getActType())) {
            // add person to the facility
            facilitesVisitors.get(event.getFacilityId()).add(event.getPersonId());

            // check if person is not already infected
            if (!infectedPersonsIds.contains(event.getPersonId())) {
                // check if person is immune (recovered)
                if (!recoveredPersonsIds.contains(event.getPersonId())) {
                // check if infected person is in the facility
                if (!Collections.disjoint(facilitesVisitors.get(event.getFacilityId()), infectedPersonsIds)) {

                        // infect person with probability p
                        double rand = Math.random();
                        if (rand < infectionProbability) {
                            infectedPersonsIds.add(event.getPersonId());
                        }

                        // Update result vectors
                        double hour = event.getTime() / 3600;
                        this.infected[(int) hour] = infectedPersonsIds.size();
                    }
                }
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

    public void writeLine2CSV(BufferedWriter writer,Integer simNumber){
        // write SI output to file
        try{
            for (int i=1; i<25;i++){
                writer.newLine();
                writer.write(simNumber+","+i+","+this.infected[i]);
            }
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }
}
