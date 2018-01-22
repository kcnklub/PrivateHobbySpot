package kylem.privatehobbyspot.entities;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import kylem.privatehobbyspot.entities.LocationPing;

/**
 * Created by kylem on 8/24/2017.
 */

public class User extends RealmObject implements Serializable{

    public static final String USER_ID = "Id";
    public static final String USER_DISPLAY_NAME = "displayName";
    public static final String USER_LOCATION_PINGS = "locationPings";

    @PrimaryKey
    private String Id;

    private String displayName;

    private RealmList<LocationPing> locationPings;

    public User(){

    }

    public User(String n_displayName, String n_id){

        displayName = n_displayName;
        Id = n_id;
        locationPings = new RealmList<LocationPing>();
    }

    public RealmList<LocationPing> getLocationPings() {
        return locationPings;
    }

    public void setLocationPings(RealmList<LocationPing> locationPings) {
        this.locationPings = locationPings;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getId() {
        return Id;
    }

    public void setEmail(String id) {
        this.Id = id;
    }

}
