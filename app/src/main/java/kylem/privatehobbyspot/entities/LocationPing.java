/**
 * Created by kylem on 8/22/2017.
 */
package kylem.privatehobbyspot.entities;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class LocationPing extends RealmObject {

    @Ignore
    public static final int LONGBOARDING = 1;
    @Ignore
    public static final int HIKING = 2;
    @Ignore
    public static final int BIKING = 3;
    @Ignore
    public static final int OTHER = -1;


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

    public LocationPing(String New_Name, double New_Long, double New_Lat, String New_Desc, int New_Type, int New_Id) {
        Name = New_Name;
        Longtitude = New_Long;
        Latitude = New_Lat;
        Description = New_Desc;
        LocationType = New_Type;
    }

    public void setName(String name) {
        Name = name;
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

    public String GetName(){
        return Name;
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

}
