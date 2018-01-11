package ch.ethz.ivt.abmt.project.archive.tutorials;

import org.matsim.api.core.v01.events.ActivityEndEvent;
import org.matsim.api.core.v01.events.ActivityStartEvent;
import org.matsim.api.core.v01.events.PersonArrivalEvent;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.events.handler.ActivityEndEventHandler;
import org.matsim.api.core.v01.events.handler.ActivityStartEventHandler;
import org.matsim.api.core.v01.events.handler.PersonArrivalEventHandler;
import org.matsim.api.core.v01.events.handler.PersonDepartureEventHandler;

public class MyEventHandler4 implements PersonArrivalEventHandler,PersonDepartureEventHandler,ActivityStartEventHandler,ActivityEndEventHandler {

    @Override
    public void reset(int iteration) {
        System.out.println("reset...");
    }

    @Override
    public void handleEvent(PersonArrivalEvent event) {
//        System.out.println("EventType "+ event.getEventType());
//        System.out.println("Attributes "+event.getAttributes());
    }

    @Override
    public void handleEvent(PersonDepartureEvent event) {
//        System.out.println("LegMode " + event.getLegMode());
    }
    @Override
    public void handleEvent(ActivityStartEvent event) {
//        System.out.println("Attributes " + event.getAttributes());
    }

    @Override
    public void handleEvent(ActivityEndEvent event) {
        System.out.println("Attributes " + event.getAttributes());
    }
}
