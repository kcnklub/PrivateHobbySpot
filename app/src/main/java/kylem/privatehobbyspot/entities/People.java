package kylem.privatehobbyspot.entities;

import io.realm.RealmObject;

/**
 * Created by kylem on 8/25/2017.
 */

public class People extends RealmObject {
    private int PID;
    private String status;

    public int getPID() {
        return PID;
    }

    public void setPID(int PID) {
        this.PID = PID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
