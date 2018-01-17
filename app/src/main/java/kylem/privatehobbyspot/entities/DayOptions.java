package kylem.privatehobbyspot.entities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Kyle on 1/8/2018.
 */

public class DayOptions extends RealmObject{

    @PrimaryKey
    private int id;

    private boolean canViewAllDay;

    private int hourStart;
    private boolean isAMStart;

    private int hourStop;
    private boolean isAMStop;

    public DayOptions(){}

    public DayOptions(boolean CanViewAllDay, int HourStart, boolean IsAMStart, int HourStop, boolean IsAMStop){
        canViewAllDay = CanViewAllDay;
        hourStart = HourStart;
        isAMStart = IsAMStart;
        hourStop = HourStop;
        isAMStop = IsAMStop;
    }

    public boolean isCanViewAllDay() {
        return canViewAllDay;
    }

    public void setCanViewAllDay(boolean canViewAllDay) {
        this.canViewAllDay = canViewAllDay;
    }

    public int getHourStart() {
        return hourStart;
    }

    public void setHourStart(int hourStart) {
        this.hourStart = hourStart;
    }

    public boolean isAMStart() {
        return isAMStart;
    }

    public void setAMStart(boolean AMStart) {
        isAMStart = AMStart;
    }

    public int getHourStop() {
        return hourStop;
    }

    public void setHourStop(int hourStop) {
        this.hourStop = hourStop;
    }

    public boolean isAMStop() {
        return isAMStop;
    }

    public void setAMStop(boolean AMStop) {
        isAMStop = AMStop;
    }
}