/**
 * Created by kylem on 8/22/2017.
 */
package kylem.privatehobbyspot.entities;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class LocationPing extends RealmObject {

    public static final String LOCATION_PING_ID = "Id";
    public static final String LOCATION_PING_NAME = "Name";
    public static final String LOCATION_PING_LONG = "Longtitude";
    public static final String LOCATION_PING_LAT = "Latitude";
    public static final String LOCATION_PING_DESCRIPTION = "Description";
    public static final String LOCATION_PING_TYPE = "LocationType";
    public static final String LOCATION_PING_USERS_SHARED = "usersThatCanViewThisLocationPing";
    public static final String LOCATION_PING_MARKER_ID = "MarkerId";

    @Ignore
    public static final int LONGBOARDING = 1;
    @Ignore
    public static final int HIKING = 2;
    @Ignore
    public static final int BIKING = 3;
    @Ignore
    public static final int OTHER = -1;

    @PrimaryKey
    @Index
    private int Id;

    private String createdByUserID;

    private String Name;

    private double Longtitude, Latitude;

    private String Description;

    private int LocationType;

    private RealmList<User> usersThatCanViewThisLocationPing;

    @Ignore
    private int MarkerId;

    public LocationPing(){
        Name = null;
        Longtitude = 0;
        Latitude = 0;
        Description = null;
        LocationType = -1;
    }

    public LocationPing(String New_Name, String creationUserID, double New_Long, double New_Lat, String New_Desc, int New_Type, int New_Id) {
        Name = New_Name;
        createdByUserID = creationUserID;
        Longtitude = New_Long;
        Latitude = New_Lat;
        Description = New_Desc;
        LocationType = New_Type;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setCreatedByUser(String createdByUser) {
        this.createdByUserID = createdByUser;
    }

    public void setLongtitude(double longtitude) {
        Longtitude = longtitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setLocationType(int locationType) {
        LocationType = locationType;
    }

    public void setMarkerId(int n_markerId){
        MarkerId = n_markerId;
    }

    public int getMarkerId() {
        return MarkerId;
    }

    public String GetName(){
        return Name;
    }

    public String getCreatedByUser() {
        return createdByUserID;
    }

    public double GetLongtitude(){
        return Longtitude;
    }

    public double GetLatitude(){
        return Latitude;
    }

    public String GetDescription(){
        return Description;
    }

    public int GetLocationType(){
        return LocationType;
    }

    public RealmList<User> getUsersThatCanViewThisLocationPing() {
        return usersThatCanViewThisLocationPing;
    }
}
