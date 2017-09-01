/**
 * Created by kylem on 8/22/2017.
 */
package kylem.privatehobbyspot.entities;

import io.realm.RealmList;
import io.realm.RealmObject;

public class LocationPing extends RealmObject {

    private String Name;
    private double Longtitude, Latitude;
    private String Description;
    private int LocationType;
    private String MarkerID;
    private int DatabaseID;
    private RealmList<User> usersThatCanViewThisLocationPing;

    public LocationPing(){
        Name = null;
        Longtitude = 0;
        Latitude = 0;
        Description = null;
        LocationType = -1;
        DatabaseID = -1;
        MarkerID = null;
    }

    public LocationPing(String New_Name, double New_Long, double New_Lat, String New_Desc, int New_Type, int New_Id) {
        Name = New_Name;
        Longtitude = New_Long;
        Latitude = New_Lat;
        Description = New_Desc;
        LocationType = New_Type;
        DatabaseID = New_Id;
        MarkerID = null;
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

    public int GetDatabaseID(){
        return DatabaseID;
    }

    public String GetMarkerID(){
        return MarkerID;
    }

    public void SetMarkerID(String New_MarkerID)
    {
        MarkerID = New_MarkerID;
    }

}
