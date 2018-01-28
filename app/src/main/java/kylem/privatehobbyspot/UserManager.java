package kylem.privatehobbyspot;

import android.util.Log;

import com.facebook.login.LoginManager;

import io.realm.ObjectServerError;
import io.realm.PermissionManager;
import io.realm.Realm;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;
import io.realm.annotations.RealmModule;
import io.realm.permissions.AccessLevel;
import io.realm.permissions.PermissionRequest;
import io.realm.permissions.UserCondition;
import kylem.privatehobbyspot.entities.DayOptions;
import kylem.privatehobbyspot.entities.LocationPing;
import kylem.privatehobbyspot.entities.User;
import kylem.privatehobbyspot.entities.UserLocationPingViewOptions;

/**
 * Created by kylem on 1/18/2018.
 */

public class UserManager {

    private static final String TAG = "User Manager";

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

        PermissionManager pm = user.getPermissionManager();

        UserCondition condition = UserCondition.userId(user.getIdentity());
        AccessLevel accessLevel = AccessLevel.WRITE;
        PermissionRequest request = new PermissionRequest(condition, PrivateHobbySpot.COMMON_URL, accessLevel);

        pm.applyPermissions(request, new PermissionManager.ApplyPermissionsCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "we got it shared");
            }

            @Override
            public void onError(ObjectServerError error) {
                Log.d(TAG, "we fucked up i think");
            }
        });

        //set the default realm to the user personal realm. when the common realm needs to be referenced will declare it then.
        @RealmModule(classes = {LocationPing.class, UserLocationPingViewOptions.class, DayOptions.class}) class personalModule {}

        SyncConfiguration defaultConfig = new SyncConfiguration.Builder(user, PrivateHobbySpot.REALM_URL)
                .modules(new personalModule())
                .build();
        Realm.setDefaultConfiguration(defaultConfig);
    }
}
