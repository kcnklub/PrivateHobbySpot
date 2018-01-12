package kylem.privatehobbyspot.entities;

import android.location.Location;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Kyle on 1/8/2018.
 */

public class UserLocationPingViewOptions extends RealmObject {

    @PrimaryKey
    private String UserID;

    private int LocationPingID;

    private RealmList<DayOptions> dayOptionsArrayList = null;

    public UserLocationPingViewOptions(){}

    public UserLocationPingViewOptions(int Id, String userID, int locationPingID, RealmList<DayOptions> dayOptions){
        UserID = userID;
        LocationPingID = locationPingID;
        dayOptionsArrayList = dayOptions;

    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public int getLocationPingID() {
        return LocationPingID;
    }

    public void setLocationPingID(int locationPingID) {
        LocationPingID = locationPingID;
    }

    public RealmList<DayOptions> getDayOptionsArrayList() {
        return dayOptionsArrayList;
    }

    public void setDayOptionsArrayList(RealmList<DayOptions> dayOptionsArrayList) {
        this.dayOptionsArrayList = dayOptionsArrayList;
    }
}
