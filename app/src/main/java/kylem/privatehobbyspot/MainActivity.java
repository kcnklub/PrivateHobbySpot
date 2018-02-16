package kylem.privatehobbyspot;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

import io.realm.ObjectServerError;
import io.realm.PermissionManager;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;
import io.realm.permissions.Permission;
import kylem.privatehobbyspot.entities.LocationPing;
import kylem.privatehobbyspot.entities.User;
import kylem.privatehobbyspot.entities.UserLocationPingViewOptions;
import kylem.privatehobbyspot.modules.commonModule;
import kylem.privatehobbyspot.modules.personalModule;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback,
        GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener,
        LocationListener{

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;

    private static final long MIN_TIME_BW_UPDATES = 0;

    private int MY_PERMISSIONS_REQUEST_LOCATION;
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private MapFragment mMapFragment;
    private String TAG = "Main Activity";

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private Location userUpdatedLocation;
    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;
    private boolean locationServiceAvaible;

    //UI objects.
    private FloatingActionButton addLocationButton;
    private FloatingActionButton settingsButton;
    private FloatingActionButton signoutButton;
    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton confirmLocationButton;
    private FloatingActionButton cancelLocationButton;
    private FloatingActionButton viewSettingsButton;
    private MaterialSearchView searchView;

    private MarkerOptions markerOptions;
    private boolean isLookingAtMap;
    private Marker newLocationMarker;

    public ArrayList<LocationPing> userLocations;
    public ArrayList<UserLocationPingViewOptions> sharedLocations;

    private SyncUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent signInData = getIntent();


        mMapFragment = MapFragment.newInstance();
        android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, mMapFragment);
        fragmentTransaction.commit();
        getInitLocation(getApplicationContext());
        isLookingAtMap = true;

        user = SyncUser.currentUser();


        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.fab);
        addLocationButton = (FloatingActionButton) findViewById(R.id.add_location);
        addLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLookingAtMap) {
                    markerOptions = new MarkerOptions()
                            .position(new LatLng(userUpdatedLocation.getLatitude(), userUpdatedLocation.getLongitude()))
                            .title("You are here")
                            .draggable(true);
                    newLocationMarker = mMap.addMarker(markerOptions);
                    floatingActionMenu.close(false);
                    isLookingAtMap = false;
                    floatingActionMenu.setVisibility(View.GONE);
                    confirmLocationButton.setVisibility(View.VISIBLE);
                    cancelLocationButton.setVisibility(View.VISIBLE);
                } else {
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, "You already have a marker on the map.", duration);
                    toast.show();
                    floatingActionMenu.close(true);
                }

            }
        });

        settingsButton = (FloatingActionButton) findViewById(R.id.settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                //startActivity(intent);

                //Testing the Common Realm
                SyncConfiguration configuration = new SyncConfiguration
                        .Builder(SyncUser.currentUser(), PrivateHobbySpot.COMMON_URL)
                        .modules(new commonModule())
                        .build();
                Realm realm = Realm.getInstance(configuration);
                long count = realm.where(User.class).count();
                Toast.makeText(MainActivity.this, String.valueOf(count), Toast.LENGTH_SHORT).show();

            }
        });

        signoutButton = (FloatingActionButton) findViewById(R.id.sign_out_button);
        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManager.logoutActiveUser();
                Intent signInActivity = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(signInActivity);
            }
        });

        viewSettingsButton = (FloatingActionButton) findViewById(R.id.view_settings_button);
        viewSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        cancelLocationButton = (FloatingActionButton) findViewById(R.id.cancelLocationButton);
        cancelLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newLocationMarker.remove();
                confirmLocationButton.setVisibility(View.GONE);
                cancelLocationButton.setVisibility(View.GONE);
                floatingActionMenu.setVisibility(View.VISIBLE);
                isLookingAtMap = true;
            }
        });

        confirmLocationButton = (FloatingActionButton) findViewById(R.id.confirmLocationButton);
        confirmLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng markerPos = newLocationMarker.getPosition();
                confirmLocationButton.setVisibility(View.GONE);
                cancelLocationButton.setVisibility(View.GONE);
                Intent addLocationActivity = new Intent(MainActivity.this, AddLocationActivity.class);
                Log.d(TAG, String.valueOf(markerPos.latitude));
                Log.d(TAG, String.valueOf(markerPos.longitude));
                addLocationActivity.putExtra("Lat", markerPos.latitude);
                addLocationActivity.putExtra("Long", markerPos.longitude);
                startActivity(addLocationActivity);
            }
        });

        PermissionManager pm = SyncUser.currentUser().getPermissionManager();

        pm.getPermissions(new PermissionManager.PermissionsCallback() {
            @Override
            public void onSuccess(RealmResults<Permission> permissions) {
                Permission p = permissions.where().equalTo("path", PrivateHobbySpot.URL_BASE + "/7e757a15d6bb7c5b2369463071e4eca1/PHS").findFirst();

                permissions.addChangeListener(new RealmChangeListener<RealmResults<Permission>>() {
                    @Override
                    public void onChange(RealmResults<Permission> permissions) {
                        Log.d(TAG, "WE GOT PERMS");
                    }
                });
            }

            @Override
            public void onError(ObjectServerError error) {
                Log.d(TAG, "for some reason we do not have perms");
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        floatingActionMenu.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        // Associate search config with the searchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setMenuItem(item);
        return true;
    }

    //Permission for stuff and do stuff.
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int grantResults[]) {
        Log.d(TAG, "result");
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "we are in");
                getInitLocation(getApplicationContext());
            } else {
                Toast.makeText(this, "Permissions was not granted", Toast.LENGTH_SHORT).show();
            }
            return;
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void onMapReady(GoogleMap googleMap) {

        final PackageManager packageManager = getApplicationContext().getPackageManager();
        if (packageManager.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getApplicationContext().getPackageName()) == PackageManager.PERMISSION_GRANTED) {
            mMap = googleMap;
            mMap.getUiSettings().setRotateGesturesEnabled(false);
            if (userUpdatedLocation != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userUpdatedLocation.getLatitude(), userUpdatedLocation.getLongitude()), 14), 2000, null);
            }
            mMap.setOnMarkerDragListener(this);
            mMap.setOnMarkerClickListener(this);
        }
        getUserLocationPings();


    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Log.d(TAG, markerOptions.getPosition().toString());
        newLocationMarker.setPosition(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int markerId = Integer.valueOf(marker.getId().substring(1));
        for (LocationPing location : userLocations) {
            if (location.getMarkerId() == markerId) {
                Realm realm = Realm.getDefaultInstance();
                boolean isUserCreator = location.getCreatedByUser().equals(SyncUser.currentUser().getIdentity());
                Log.d(TAG, location.getCreatedByUser());
                Log.d(TAG, String.valueOf(isUserCreator));
                Intent intent = new Intent(this, LocationDetailsActivity.class);
                intent.putExtra("locationID", location.getId());
                intent.putExtra("locationName", location.GetName());
                intent.putExtra("locationDescription", location.GetDescription());
                intent.putExtra("locationMarkerID", location.getMarkerId());
                intent.putExtra("isUserCreator", isUserCreator);
                startActivity(intent);
            }
        }

        for(UserLocationPingViewOptions shareLocation : sharedLocations){
            if(shareLocation.getLocationMarkerID() == markerId){
            }
        }

        return true;
    }

    @TargetApi(23)
    public void getInitLocation(Context context) {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "we have permissions right before try block");
            try {
                this.mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                this.isGPSEnabled = this.mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                this.isNetworkEnabled = this.mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (!isNetworkEnabled && !isGPSEnabled) {
                    this.locationServiceAvaible = false;
                    Log.d(TAG, "GPS and network are not avaible");
                } else {
                    this.locationServiceAvaible = true;
                    if (this.isNetworkEnabled) {
                        Log.d(TAG, "Network");
                        this.mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (this.mLocationManager != null) {
                            userUpdatedLocation = this.mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            //update coord.
                        }
                    }

                    if (this.isGPSEnabled) {
                        Log.d(TAG, "GPS");
                        this.mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (this.mLocationManager != null) {
                            userUpdatedLocation = this.mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            //update coord.
                        }
                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, "Error create Location service: " + ex.getMessage());
            } finally {
                // had to move the call for the callback into this part of the code
                // so that the call back would happen after the permissions were accepted or denied
                // before the OnMapReady was called.
                mMapFragment.getMapAsync(this);
            }
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Location is need to show you location pings near you", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        userUpdatedLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onBackPressed() {
        if (!isLookingAtMap) {
            super.onBackPressed();
            floatingActionMenu.setVisibility(View.VISIBLE);
            mMap.clear();
            isLookingAtMap = true;
            //getUserLocationPings();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.sign_out);
            builder.setMessage(R.string.sign_out_msg);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    user.logout();
                    Intent signInActivity = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(signInActivity);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public FloatingActionMenu getFloatingActionMenu() {
        return floatingActionMenu;
    }

    public boolean isLookingAtMap() {
        return isLookingAtMap;
    }

    public void setLookingAtMap(boolean lookingAtMap) {
        isLookingAtMap = lookingAtMap;
    }

    public void getUserLocationPings() {
        //making sure to empty the arraylist before adding the data to it.
        userLocations = new ArrayList<LocationPing>();
        sharedLocations = new ArrayList<UserLocationPingViewOptions>();

        SyncConfiguration config = new SyncConfiguration.Builder(SyncUser.currentUser(), PrivateHobbySpot.REALM_URL)
                .modules(new personalModule())
                .build();

        RealmAsyncTask realmAsyncTask = Realm.getInstanceAsync(config, new Realm.Callback() {
            @Override
            public void onSuccess(Realm realm) {
                Log.d(TAG, "personal pings success");
                RealmQuery<LocationPing> query = realm.where(LocationPing.class);
                RealmResults<LocationPing> Locations = query.findAll();
                Log.d(TAG, String.valueOf(Locations.size()));
                if (Locations.size() != 0) {
                    Log.d(TAG, "Number of Locations: " + (String.valueOf(Locations.size())));
                    for (LocationPing location : Locations) {
                        String markerId = mMap.addMarker(new MarkerOptions()
                                .draggable(false)
                                .position(new LatLng(location.GetLatitude(), location.GetLongtitude()))
                                .title(location.GetName())
                        ).getId();
                        markerId = markerId.substring(1);
                        int n_markerId = Integer.valueOf(markerId);
                        location.setMarkerId(n_markerId);
                        userLocations.add(location);
                    }
                }
            }
        });

        SyncConfiguration configuration = new SyncConfiguration
                .Builder(SyncUser.currentUser(), PrivateHobbySpot.COMMON_URL)
                .modules(new commonModule())
                .build();

        Log.d(TAG, SyncUser.currentUser().getIdentity());
        RealmAsyncTask commonRealm = Realm.getInstanceAsync(configuration, new Realm.Callback() {
            @Override
            public void onSuccess(Realm realm) {
                User user = realm.where(User.class).equalTo(User.USER_ID, SyncUser.currentUser().getIdentity()).findFirst();
                Log.d(TAG, user.getDisplayName());
                for(String url: user.getSharedRealmUrls()){
                    Log.d(TAG, url);
                    SyncConfiguration config = new SyncConfiguration
                            .Builder(SyncUser.currentUser(), PrivateHobbySpot.URL_BASE + url)
                            .modules(new personalModule())
                            .build();
                    RealmAsyncTask otherRealm = Realm.getInstanceAsync(config, new Realm.Callback() {
                        @Override
                        public void onSuccess(Realm realm) {
                            RealmResults<UserLocationPingViewOptions> locationViewOptions = realm
                                    .where(UserLocationPingViewOptions.class)
                                    .equalTo(UserLocationPingViewOptions.VIEW_OPTIONS_USER_ID, SyncUser.currentUser().getIdentity())
                                    .findAll();

                            for(UserLocationPingViewOptions viewOptions : locationViewOptions){
                                LocationPing location = realm.where(LocationPing.class).equalTo(LocationPing.LOCATION_PING_ID, viewOptions.getLocationPingID()).findFirst();
                                    Log.d(TAG, location.GetName());
                                    String markedId = mMap.addMarker(new MarkerOptions()
                                            .draggable(false)
                                            .position(new LatLng(location.GetLatitude(), location.GetLongtitude()))
                                            .title(location.GetName())
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                    ).getId();
                                    markedId = markedId.substring(1);
                                    int n_markerId = Integer.valueOf(markedId);
                                    realm.beginTransaction();
                                    viewOptions.setLocationMarkerID(n_markerId);
                                    realm.commitTransaction();
                                    sharedLocations.add(viewOptions);
                            }
                        }

                        @Override
                        public void onError(Throwable error){
                            Log.d(TAG, error.getMessage());
                        }
                    });

                }
            }

            @Override
            public void onError(Throwable error){
                Log.d(TAG, error.getMessage());
            }

        });
    }
}


