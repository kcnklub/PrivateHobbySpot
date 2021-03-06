package kylem.privatehobbyspot.entities;

import android.location.Location;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Kyle on 1/8/2018.
 */

public class UserLocationPingViewOptions extends RealmObject {

    public static final String VIEW_OPTIONS_ID = "id";
    public static final String VIEW_OPTIONS_USER_ID = "UserID";
    public static final String VIEW_OPTIONS_LOCATION_ID = "LocationPingID";
    public static final String VIEW_OPTIONS_DAY_LIST = "dayOptionsArrayList";

    @PrimaryKey
    private int id;

    private String UserID;

    private int LocationPingID;

    private int locationMarkerID;

    private RealmList<DayOptions> dayOptionsArrayList = null;

    public UserLocationPingViewOptions(){}

    public UserLocationPingViewOptions(String userID, int locationPingID){
        UserID = userID;
        LocationPingID = locationPingID;
        dayOptionsArrayList = new RealmList<DayOptions>();
        for(int i = 0; i < 7; i++){
            dayOptionsArrayList.add(new DayOptions(true, 0, false, 0, false));
        }

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

    public int getLocationMarkerID() {
        return locationMarkerID;
    }

    public void setLocationMarkerID(int locationMarkerID) {
        this.locationMarkerID = locationMarkerID;
    }

    public RealmList<DayOptions> getDayOptionsArrayList() {
        return dayOptionsArrayList;
    }

    public void setDayOptionsArrayList(RealmList<DayOptions> dayOptionsArrayList) {
        this.dayOptionsArrayList = dayOptionsArrayList;
    }
}
