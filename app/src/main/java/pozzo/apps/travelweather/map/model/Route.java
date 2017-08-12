package pozzo.apps.travelweather.map.model;

import android.provider.BaseColumns;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

import pozzo.apps.travelweather.BaseModel;

/**
 * Created by soldier on 10/4/15.
 */
@Table(name = "route", id = BaseColumns._ID)
public class Route extends BaseModel {
    public interface Col {
        String START = "start";
        String FINISH = "finish";
        String START_TIME = "startTime";
        String FINISH_TIME = "finishTime";
    }

    @Column(name = Col.START)
    private Address start;

    @Column(name = Col.FINISH)
    private Address finish;

    @Column(name = Col.START_TIME)
    private Date startTime;

    @Column(name = Col.FINISH_TIME)
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
