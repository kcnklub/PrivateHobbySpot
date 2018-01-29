package kylem.privatehobbyspot;

import android.location.Location;
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

import io.realm.ObjectServerError;
import io.realm.PermissionManager;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;
import io.realm.permissions.AccessLevel;
import io.realm.permissions.PermissionRequest;
import io.realm.permissions.UserCondition;
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

    Realm realm;
    Realm commonRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);

        //get all of the intent data.
        mlocationId = getIntent().getIntExtra("locationID", 0);
        mlocationName = getIntent().getStringExtra("locationName");
        mlocationDescription = getIntent().getStringExtra("locationDescription");
        mlocationType = getIntent().getIntExtra("locationMarkerID", 0);
        mUserIsCreator = getIntent().getBooleanExtra("isUserCreator", false);

        // reference all of the UI elements
        locationNameView = (TextView) findViewById(R.id.location_name);
        locationDescriptionView = (TextView) findViewById(R.id.location_description);
        locationTypeView = (TextView) findViewById(R.id.location_type);
        deletePingButton = (Button) findViewById(R.id.delete_ping);
        addUserToView = (EditText) findViewById(R.id.user_to_view);
        addUserToViewButton = (Button) findViewById(R.id.add_user_to_view);
        usersSharedWith = (ListView) findViewById(R.id.users_shared_with);

        if(!mUserIsCreator){
            // adjust the UI if the user is not the creator of this point.
            addUserToView.setVisibility(View.INVISIBLE);
            addUserToViewButton.setVisibility(View.INVISIBLE);
            deletePingButton.setVisibility(View.INVISIBLE);
            usersSharedWith.setVisibility(View.INVISIBLE);
        } else {
            realm = Realm.getDefaultInstance();
            //get all of the user ids of the users that this location is shared with.
            RealmResults<LocationPing> locationPingRealmResults = realm.where(LocationPing.class).equalTo(LocationPing.LOCATION_PING_ID, mlocationId).findAll();
            final RealmList<String> userSharedList = locationPingRealmResults.first().getUsersThatCanViewThisLocationPing();

            //turn the user ids into the user objects for the adapter.
            SyncConfiguration config = new SyncConfiguration.Builder(SyncUser.currentUser(), PrivateHobbySpot.COMMON_URL)
                    .build();
            commonRealm = Realm.getInstance(config);
            ArrayList<User> userSharedObjects = new ArrayList<>();
            for(String user_id : userSharedList){
                User temp = commonRealm.where(User.class).equalTo(User.USER_ID, user_id).findFirst();
                userSharedObjects.add(temp);
            }

            UserSharedWithAdapter adapter = new UserSharedWithAdapter(
                    this, R.layout.listview_item_row,
                    userSharedObjects);

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
                Realm realm = Realm.getDefaultInstance();
                try {
                    if(!input.equals("")){

                        RealmResults<User> friend = commonRealm.where(User.class)
                                .equalTo(User.USER_DISPLAY_NAME, input)
                                .findAll();
                        if(friend.size() != 0){

                            //give the user that the location is being shared with and give them permission to view this user's realm.
                            SyncUser user = SyncUser.currentUser();
                            PermissionManager pm = user.getPermissionManager();
                            UserCondition condition = UserCondition.username(input);
                            AccessLevel accessLevel = AccessLevel.READ;
                            PermissionRequest request = new PermissionRequest(condition, PrivateHobbySpot.REALM_URL, accessLevel);
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

                            // add the url to this users realm to the user that this location is being shared with.
                            final User shareUser = commonRealm.where(User.class).equalTo(User.USER_DISPLAY_NAME, input).findFirst();
                            commonRealm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    if(shareUser.getSharedRealmUrls() == null){
                                        shareUser.setSharedRealmUrls(new RealmList<String>());
                                        shareUser.getSharedRealmUrls().add("/" + SyncUser.currentUser().getIdentity() + "/PHS");
                                    } else {
                                        shareUser.getSharedRealmUrls().add("/" + SyncUser.currentUser().getIdentity() + "/PHS");
                                    }
                                }
                            });

                            final User friendUser = friend.first();
                            RealmResults<LocationPing> Query = realm.where(LocationPing.class)
                                    .equalTo(LocationPing.LOCATION_PING_ID, mlocationId)
                                    .findAll();
                            final LocationPing location = Query.first();
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
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
                                }
                            });
                            Log.d(TAG, "User can view this now");
                            Toast.makeText(getApplicationContext(), "Location Shared with " + friendUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                            onRestart();
                        } else {
                            Toast.makeText(getApplicationContext(), "User Does NOT Exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Input Blank", Toast.LENGTH_SHORT).show();
                    }
                } finally {
                    realm.close();
                }

            }
        });

        deletePingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm realm = Realm.getDefaultInstance();
                try{
                    // The Ping itself.
                    RealmResults<LocationPing> locationPingsResults = realm.where(LocationPing.class)
                            .equalTo(LocationPing.LOCATION_PING_ID, mlocationId)
                            .findAll();
                    final LocationPing locationPing = locationPingsResults.first();

                    //all view options for the specific ping
                    final RealmResults<UserLocationPingViewOptions> userLocationPingViewOptionsRealmResults = realm.where(UserLocationPingViewOptions.class)
                            .equalTo(UserLocationPingViewOptions.VIEW_OPTIONS_LOCATION_ID, mlocationId)
                            .findAll();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            // delete the ping itself;
                            locationPing.deleteFromRealm();

                            // delete the view and dayOptions objects that are tied to the location ping.
                            for(UserLocationPingViewOptions userLocationPingViewOptions : userLocationPingViewOptionsRealmResults){
                                for(DayOptions dayOptions : userLocationPingViewOptions.getDayOptionsArrayList()){
                                    dayOptions.deleteFromRealm();
                                }
                                userLocationPingViewOptions.deleteFromRealm();
                            }
                        }
                    });

                } finally {
                    realm.close();
                }

                finish();
            }
        });

    }

    @Override
    public void onStop(){
        super.onStop();
        commonRealm.close();
    }

    public int getMlocationId() {
        return mlocationId;
    }

}
