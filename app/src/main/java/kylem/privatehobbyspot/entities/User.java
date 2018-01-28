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
    public static final String USER_REALM_URL = "personalRealmUrl";

    @PrimaryKey
    private String Id;

    private String displayName;

    private String personalRealmUrl;

    private RealmList<String> sharedRealmUrls;

    public User(){
    }

    public User(String n_displayName, String n_id, String realmUrl){
        displayName = n_displayName;
        Id = n_id;
        personalRealmUrl = realmUrl;
        sharedRealmUrls = new RealmList<>();
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

    public String getPersonalRealmUrl() {
        return personalRealmUrl;
    }

    public void setPersonalRealmUrl(String personalRealmUrl) {
        this.personalRealmUrl = personalRealmUrl;
    }

    public RealmList<String> getSharedRealmUrls() {
        return sharedRealmUrls;
    }

    public void setSharedRealmUrls(RealmList<String> sharedRealmUrls) {
        this.sharedRealmUrls = sharedRealmUrls;
    }
}
