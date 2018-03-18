package pozzo.apps.travelweather.map.model;

import java.util.Date;

public class Route {
    public interface Col {
        String START = "start";
        String FINISH = "finish";
        String START_TIME = "startTime";
        String FINISH_TIME = "finishTime";
    }

    private Address start;
    private Address finish;
    private Date startTime;
    private Date finishTime;

    public Address getStart() {
        return start;
    }

    public void setStart(Address start) {
        this.start = start;
    }

    public Address getFinish() {
        return finish;
    }

    public void setFinish(Address finish) {
        this.finish = finish;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }
}
