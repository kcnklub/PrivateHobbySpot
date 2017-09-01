package kylem.privatehobbyspot.entities;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created by kylem on 8/26/2017.
 */

public class Migration implements RealmMigration {

    private String TAG = "Migration";

    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, long newVersion){
        Log.d(TAG, "beginning of Migrate");
        RealmSchema schema = realm.getSchema();

        /*************************************
          //version 0
         class user
            @PrimaryKey
            int UID
            String displayName
            String email
            String password
            RealmList<LocationPing> LocationPings
            RealmList<People> peoples


         //Version 1
         class user
            @PrimaryKey
            String email                             // remove UID and make email the primary key
            String displayName
            String password
            RealmList<LocationPing> LocationPings
            RealmList<People> peoples
         *************************************/

        if(oldVersion == 0){
            RealmObjectSchema userSchema = schema.get("User");
            userSchema
                    .removeField("UID")
                    .removeField("email")
                    .addField("Email", String.class, FieldAttribute.PRIMARY_KEY);
            oldVersion++;
        }

    }
}
