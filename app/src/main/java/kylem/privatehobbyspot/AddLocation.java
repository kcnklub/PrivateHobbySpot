package kylem.privatehobbyspot;

import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.maps.model.LatLng;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import kylem.privatehobbyspot.entities.LocationPing;
import kylem.privatehobbyspot.entities.User;


public class AddLocation extends android.app.Fragment {

    private final String TAG = "ADD LOCATION FRAGMENT";

    private static LatLng staticLatLngToAdd;
    private LatLng latLngToAdd;

    private Realm realm;

    private OnFragmentInteractionListener mListener;

    private EditText name;
    private EditText description;
    private RadioGroup locationType;
    private Button confirmAddLocation;
    private String userEmail;

    public AddLocation() {
        // Required empty public constructor
    }

    public static AddLocation newInstance(LatLng m_latLngToAdd) {
        AddLocation fragment = new AddLocation();
        staticLatLngToAdd = m_latLngToAdd;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        latLngToAdd = staticLatLngToAdd;
        MainActivity mainActivity = (MainActivity) getActivity();
        userEmail = mainActivity.getUserEmail();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_location, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        this.confirmAddLocation = (Button) getView().findViewById(R.id.confirmAddLocation);
        this.name = (EditText) getView().findViewById(R.id.locName);
        this.description = (EditText) getView().findViewById(R.id.description);
        this.locationType = (RadioGroup) getView().findViewById(R.id.locationType);
        setOnClickListeners();


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void setOnClickListeners(){
        if(this.confirmAddLocation != null){
            this.confirmAddLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    realm = Realm.getDefaultInstance();
                    try{
                        realm.executeTransactionAsync(new Realm.Transaction() {
                              @Override
                              public void execute(Realm realm) {
                                  Log.d(TAG, "executing transaction");
                                  RealmResults<User> currUser = realm.where(User.class).equalTo("Email", userEmail)
                                          .findAll();
                                  long numberOfPings = realm.where(LocationPing.class).count();
                                  LocationPing ping = realm.createObject(LocationPing.class, numberOfPings + 1);
                                  ping.setName(AddLocation.this.name.getText().toString());
                                  ping.setLatitude(latLngToAdd.latitude);
                                  ping.setLongtitude(latLngToAdd.longitude);
                                  ping.setDescription(AddLocation.this.description.getText().toString());
                                  int LocationType = AddLocation.this.locationType.getCheckedRadioButtonId();
                                  ping.setLocationType(LocationType);
                                  User user = currUser.first();
                                  user.getLocationPings().add(ping);
                              }
                          }, new Realm.Transaction.OnSuccess() {
                              @Override
                              public void onSuccess() {
                                  Log.d(TAG, "Location has been added to the database");
                              }

                          }, new Realm.Transaction.OnError() {
                              @Override
                              public void onError(Throwable error){
                                  Log.d(TAG, error.getMessage());
                              }
                          }
                        );
                    } finally {
                        realm.close();
                        //wrap up the query by updating the UI and reload the pings.
                        getFragmentManager().popBackStack();
                    }
                }
            });
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //When the user is done adding the location they are redirected back the "home page"
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getFloatingActionMenu().setVisibility(View.VISIBLE);
        mainActivity.setLookingAtMap(true);
        mainActivity.getUserLocationPings();
    }
}
