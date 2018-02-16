package kylem.privatehobbyspot;

import android.app.Application;

import com.facebook.FacebookSdk;

import io.realm.Realm;
import io.realm.log.LogLevel;
import io.realm.log.RealmLog;

/**
 * Created by kylem on 8/24/2017.
 */

public class PrivateHobbySpot extends Application {

    private String TAG = "PrivateHobbySpotApp";

    public static final String AUTH_URL = "http://" + BuildConfig.OBJECT_SERVER_IP + ":9080/auth";
    public static final String REALM_URL = "http://" + BuildConfig.OBJECT_SERVER_IP + ":9080/~/PHS";
    public static final String COMMON_URL = "http://" + BuildConfig.OBJECT_SERVER_IP + ":9080/PHS_Common";
    public static final String URL_BASE = "http://" + BuildConfig.OBJECT_SERVER_IP + ":9080";
    @Override
    public void onCreate(){
        super.onCreate();
        Realm.init(this);
    }

}
