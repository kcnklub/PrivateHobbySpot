package kylem.privatehobbyspot.entities;

import io.realm.RealmObject;

/**
 * Created by Kyle on 1/8/2018.
 */

public class DayOptions extends RealmObject{

    // if [0] is -1 then the user cant see the locatoin on that day at all.
    // if [0] is 2 then the user can see the location for the full day.
    // if [0] is a 0 or a 1 they can see it for the hours that are a 1 and cannot see it for the hours that are a 0.

    private int hourStart;
    private boolean isAMStart;

    private int hourStop;
    private boolean isAMStop;

    public DayOptions(){}

    public DayOptions(int HourStart, boolean IsAMStart, int HourStop, boolean IsAMStop){
        hourStart = HourStart;
        isAMStart = IsAMStart;
        hourStop = HourStop;
        isAMStop = IsAMStop;
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
