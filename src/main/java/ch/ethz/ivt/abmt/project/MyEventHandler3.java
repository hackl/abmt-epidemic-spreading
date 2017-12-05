package ch.ethz.ivt.abmt.project;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.core.utils.charts.XYLineChart;

public class MyEventHandler3 implements LinkEnterEventHandler {

    private double[] volumeLink6;

    public MyEventHandler3(){
        reset(0);
    }

    public double getTravelTime(int slot){
        return this.volumeLink6[slot];
    }

    private int getSlot(double time){
        return (int)time/3600;
    }

    @Override
    public void handleEvent(LinkEnterEvent event) {
        if (event.getLinkId().equals(Id.create("42145829_4",Link.class))){
            this.volumeLink6[getSlot((event.getTime()))]++;
        }
    }

    @Override
    public void reset(int iteration) {
        this.volumeLink6 = new double[24];
    }

    public void writeChart(String filename){
        double[] hours = new double[24];
        for (double i = 0.0; i<24.0; i++){
            hours[(int)i] = i;
        }
        XYLineChart chart = new XYLineChart("Traffic link 6","hour","depatures");
        chart.addSeries("times",hours,this.volumeLink6);
        chart.saveAsPng(filename,800,600);
    }
}
