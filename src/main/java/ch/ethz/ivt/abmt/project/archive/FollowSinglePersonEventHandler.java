package ch.ethz.ivt.abmt.project.archive;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.ActivityEndEvent;
import org.matsim.api.core.v01.events.ActivityStartEvent;
import org.matsim.api.core.v01.events.handler.ActivityEndEventHandler;
import org.matsim.api.core.v01.events.handler.ActivityStartEventHandler;
import org.matsim.api.core.v01.population.Person;

public class FollowSinglePersonEventHandler implements ActivityStartEventHandler,ActivityEndEventHandler{
    private Id<Person> personId;


    public FollowSinglePersonEventHandler(Id<Person> personId){
        this.personId = personId;
    }

    @Override
    public void reset(int iteration) {
        System.out.println("reset...");
    }

    @Override
    public void handleEvent(ActivityStartEvent event) {
        if(event.getPersonId().equals(this.personId)){
            System.out.println("Attributes "+event.getAttributes());
        }
    }

    @Override
    public void handleEvent(ActivityEndEvent event) {
        if(event.getPersonId().equals(this.personId)){
            System.out.println("Attributes "+event.getAttributes());
        }
    }

    public void printPersonId(){
        System.out.println(this.personId);
    }
}
