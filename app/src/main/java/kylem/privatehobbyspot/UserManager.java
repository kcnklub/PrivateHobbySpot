package kylem.privatehobbyspot;

import com.facebook.login.LoginManager;

import io.realm.Realm;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;
import io.realm.annotations.RealmModule;
import kylem.privatehobbyspot.entities.DayOptions;
import kylem.privatehobbyspot.entities.LocationPing;
import kylem.privatehobbyspot.entities.User;
import kylem.privatehobbyspot.entities.UserLocationPingViewOptions;

/**
 * Created by kylem on 1/18/2018.
 */

public class UserManager {

    public enum AUTH_MODE {
        PASSWORD,
        FACEBOOK,
        GOOGLE
    }
    private static AUTH_MODE mode = AUTH_MODE.PASSWORD; // DEFAULT

    public static void setAuthMode(AUTH_MODE m){
        mode = m;
    }

    public static void logoutActiveUser(){
        switch(mode){
            case PASSWORD:{
                // DO NOTHING, HANDLED LAST
                break;
            }
            case FACEBOOK: {
                LoginManager.getInstance().logOut();
                break;
            }
            case GOOGLE: {
                // the connection is handled by 'enableAutoManage' mode
                break;
            }
        }
        SyncUser.currentUser().logout();
    }

    public static void setActiveUser(SyncUser user){
        SyncConfiguration defaultConfig = new SyncConfiguration.Builder(user, PrivateHobbySpot.REALM_URL).build();
        Realm.setDefaultConfiguration(defaultConfig);
    }
}
