package kylem.privatehobbyspot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.android.gms.maps.model.LatLng;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.SyncUser;
import kylem.privatehobbyspot.entities.LocationPing;

public class AddLocationActivity extends AppCompatActivity {

    private final String TAG = "ADD LOCATION FRAGMENT";

    private static LatLng staticLatLngToAdd;
    private LatLng latLngToAdd;

    private EditText name;
    private EditText description;
    private RadioGroup locationType;
    private Button addLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        name = (EditText) findViewById(R.id.location_name);
        description = (EditText) findViewById(R.id.location_description);
        locationType = (RadioGroup) findViewById(R.id.location_type);

        addLocationButton = (Button) findViewById(R.id.add_location);
        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm realm = Realm.getDefaultInstance();
                final long locationPingCount = realm.where(LocationPing.class).count();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        LocationPing ping = realm.createObject(LocationPing.class, locationPingCount + 1);
                        ping.setCreatedByUser(SyncUser.currentUser().getIdentity());
                        ping.setName(name.getText().toString());
                        ping.setDescription(description.getText().toString());
                        ping.setLatitude(latLngToAdd.latitude);
                        ping.setLongtitude(latLngToAdd.longitude);
                    }
                });
            }
        });


    }
}
