package kylem.privatehobbyspot.entities;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by kylem on 1/20/2018.
 */

public class MyMigrations implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion){
        RealmSchema schema = realm.getSchema();

        if(oldVersion == 0){
            schema.get("User")
                    .removeField("peoples")
                    .removeField("password")
                    .renameField("Email", User.USER_ID);


            schema.get("LocationPing")
                    .removeField("createdByUser")
                    .removeField("usersThatCanViewThisLocationPing")
                    .addRealmListField("userThatCanViewThisLocationPing", String.class);

            oldVersion++;
        }
    }
}
