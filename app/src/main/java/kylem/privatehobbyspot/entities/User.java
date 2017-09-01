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

    @PrimaryKey
    private String Email;

    private String displayName;

    private String password;

    private RealmList<LocationPing> locationPings;

    private RealmList<People> peoples;

    public User(){

    }

    public User(String n_displayName, String n_email, String n_password){

        displayName = n_displayName;
        Email = n_email;
        password = n_password;
        locationPings = null;
        peoples = null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RealmList<LocationPing> getLocationPings() {
        return locationPings;
    }

    public void setLocationPings(RealmList<LocationPing> locationPings) {
        this.locationPings = locationPings;
    }

    public RealmList<People> getPeoples() {
        return peoples;
    }

    public void setPeoples(RealmList<People> peoples) {
        this.peoples = peoples;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public String toString(){
        return displayName + " " + Email;
    }

}
