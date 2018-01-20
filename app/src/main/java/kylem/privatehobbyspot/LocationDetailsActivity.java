package kylem.privatehobbyspot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import kylem.privatehobbyspot.entities.DayOptions;
import kylem.privatehobbyspot.entities.LocationPing;
import kylem.privatehobbyspot.entities.User;
import kylem.privatehobbyspot.entities.UserLocationPingViewOptions;

public class LocationDetailsActivity extends AppCompatActivity {

    private static final String LOCATION_ID = "Location id";
    private static final String LOCATION_NAME = "Location Name";
    private static final String LOCATION_DESCRIPTION = "Location Description";
    private static final String LOCATION_TYPE = "Location Type";
    private static final String TAG = "Location Details";
    private static final String USER_IS_CREATOR = "User Is Creator";

    private int mlocationId;
    private String mlocationName;
    private String mlocationDescription;
    private int mlocationType;
    private boolean mUserIsCreator;

    private TextView locationNameView;
    private TextView locationDescriptionView;
    private TextView locationTypeView;
    private EditText addUserToView;
    private Button addUserToViewButton;
    private Button deletePingButton;
    private ListView usersSharedWith;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);

        mlocationId = getIntent().getIntExtra("locationID", 0);
        mlocationName = getIntent().getStringExtra("locationName");
        mlocationDescription = getIntent().getStringExtra("locationDescription");
        mlocationType = getIntent().getIntExtra("locationMarkerID", 0);
        mUserIsCreator = getIntent().getBooleanExtra("isUserCreator", false);



        locationNameView = (TextView) findViewById(R.id.location_name);
        locationDescriptionView = (TextView) findViewById(R.id.location_description);
        locationTypeView = (TextView) findViewById(R.id.location_type);
        deletePingButton = (Button) findViewById(R.id.delete_ping);
        addUserToView = (EditText) findViewById(R.id.user_to_view);
        addUserToViewButton = (Button) findViewById(R.id.add_user_to_view);
        usersSharedWith = (ListView) findViewById(R.id.users_shared_with);

        if(!mUserIsCreator){
            addUserToView.setVisibility(View.INVISIBLE);
            addUserToViewButton.setVisibility(View.INVISIBLE);
            deletePingButton.setVisibility(View.INVISIBLE);
            usersSharedWith.setVisibility(View.INVISIBLE);
        } else {
            Realm realm = Realm.getDefaultInstance();
            RealmResults<LocationPing> locationPingRealmResults = realm.where(LocationPing.class).equalTo("Id", mlocationId).findAll();
            final ArrayList<String> userSharedList = new ArrayList<String>(locationPingRealmResults.first().getUsersThatCanViewThisLocationPing());

            UserSharedWithAdapter adapter = new UserSharedWithAdapter(
                    this, R.layout.listview_item_row,
                    userSharedList);

            usersSharedWith.setAdapter(adapter);
        }

        locationNameView.setText(mlocationName);
        locationDescriptionView.setText(mlocationDescription);
        switch (mlocationType){
            case 0:
                locationTypeView.setText(R.string.Longboarding);
                break;
            case 1:
                locationTypeView.setText(R.string.Biking);
                break;
            case 2:
                locationTypeView.setText(R.string.Hiking);
                break;
            default:
                locationTypeView.setText(R.string.Other);
        }

        addUserToViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String input = addUserToView.getText().toString();
                if(!input.equals("")){
                    Log.d(TAG, input);
                    Realm realm = Realm.getDefaultInstance();
                    RealmResults<User> friend = realm.where(User.class).equalTo("Email", input).findAll();
                    if(friend.size() != 0){
                        User friendUser = friend.first();
                        RealmResults<LocationPing> Query = realm.where(LocationPing.class).equalTo("Id", mlocationId).findAll();
                        LocationPing location = Query.first();
                        realm.beginTransaction();
                        location.getUsersThatCanViewThisLocationPing().add(friendUser.getId());

                        RealmQuery<UserLocationPingViewOptions> query = realm.where(UserLocationPingViewOptions.class);

                        UserLocationPingViewOptions userLocationPingViewOptions = realm.createObject(UserLocationPingViewOptions.class, query.count() + 1 );
                        userLocationPingViewOptions.setUserID(friendUser.getId());
                        userLocationPingViewOptions.setLocationPingID(location.getId());

                        RealmQuery<DayOptions> dayOptionsRealmQuery = realm.where(DayOptions.class);
                        RealmList<DayOptions> dayOptionsArrayList = userLocationPingViewOptions.getDayOptionsArrayList();
                        for(int i = 0; i < 7; i++){
                            DayOptions dayOptions = realm.createObject(DayOptions.class, dayOptionsRealmQuery.count() + 1);
                            dayOptions.setAMStart(false);
                            dayOptions.setAMStop(false);
                            dayOptions.setHourStart(0);
                            dayOptions.setHourStop(0);
                            dayOptions.setCanViewAllDay(true);
                            dayOptionsArrayList.add(dayOptions);
                        }
                        realm.commitTransaction();
                        Log.d(TAG, "User can view this now");
                        Toast.makeText(getApplicationContext(), "Location Shared with " + friendUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                        onRestart();
                    } else {
                        Toast.makeText(getApplicationContext(), "User Does NOT Exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Input Blank", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deletePingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm realm = Realm.getDefaultInstance();

                // The Ping itself.
                RealmResults<LocationPing> locationPingsResults = realm.where(LocationPing.class).equalTo("Id", mlocationId).findAll();
                LocationPing locationPing = locationPingsResults.first();

                //all view options for the specific ping
                RealmResults<UserLocationPingViewOptions> userLocationPingViewOptionsRealmResults = realm.where(UserLocationPingViewOptions.class).equalTo("LocationPingID", mlocationId).findAll();

                realm.beginTransaction();
                // delete the ping itself;
                locationPing.deleteFromRealm();

                // delete the view and dayoptions objects that are tied to the location ping.
                for(UserLocationPingViewOptions userLocationPingViewOptions : userLocationPingViewOptionsRealmResults){
                    for(DayOptions dayOptions : userLocationPingViewOptions.getDayOptionsArrayList()){
                        dayOptions.deleteFromRealm();
                    }
                    userLocationPingViewOptions.deleteFromRealm();
                }
                realm.commitTransaction();

                realm.close();

                finish();
            }
        });

    }

    public int getMlocationId() {
        return mlocationId;
    }

}
