package kylem.privatehobbyspot;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import kylem.privatehobbyspot.entities.LocationPing;

public class LocationDetails extends android.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LOCATION_ID = "Location id";
    private static final String LOCATION_NAME = "Location Name";
    private static final String LOCATION_DESCRIPTION = "Location Description";
    private static final String LOCATION_TYPE = "Location Type";
    private static final String TAG = "Location Details";


    // TODO: Rename and change types of parameters
    private int mlocationId;
    private String mlocationName;
    private String mlocationDescription;
    private int mlocationType;

    private TextView locationNameView;
    private TextView locationDescriptionView;
    private TextView locationTypeView;
    private Button deletePingButton;

    private OnFragmentInteractionListener mListener;

    public LocationDetails() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static LocationDetails newInstance(int locationId, String locationName, String locationDescription, int locationType) {
        LocationDetails fragment = new LocationDetails();
        Bundle args = new Bundle();
        args.putInt(LOCATION_ID, locationId);
        args.putString(LOCATION_NAME, locationName);
        args.putString(LOCATION_DESCRIPTION, locationDescription);
        args.putInt(LOCATION_TYPE, locationType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mlocationId = getArguments().getInt(LOCATION_ID);
            mlocationName = getArguments().getString(LOCATION_NAME);
            mlocationDescription = getArguments().getString(LOCATION_DESCRIPTION);
            mlocationType = getArguments().getInt(LOCATION_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_details, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        locationNameView = (TextView) getView().findViewById(R.id.location_name);
        locationDescriptionView = (TextView) getView().findViewById(R.id.location_description);
        locationTypeView = (TextView) getView().findViewById(R.id.location_type);
        deletePingButton = (Button) getView().findViewById(R.id.delete_ping);

        locationNameView.setText(mlocationName);
        locationDescriptionView.setText(mlocationDescription);
        switch (mlocationType){
            case 0:
                locationTypeView.setText("Longboarding");
                break;
            case 1:
                locationTypeView.setText("Biking");
                break;
            case 2:
                locationTypeView.setText("Hiking");
                break;
            default:
                locationTypeView.setText("Other");
        }

        deletePingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete Ping");
                builder.setMessage("Are you sure you want to delete this Ping");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    @TargetApi(19)
                    public void onClick(DialogInterface dialog, int which) {
                        Realm realm = null;
                        try{
                            realm = Realm.getDefaultInstance();
                            final RealmResults<LocationPing> locationToDelete = realm.where(LocationPing.class)
                                    .equalTo("Id", mlocationId)
                                    .findAll();
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm){
                                    LocationPing ping = locationToDelete.first();
                                    ping.deleteFromRealm();
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess(){
                                    Log.d(TAG, "Location was deleted.");
                                }
                            }, new Realm.Transaction.OnError(){
                                @Override
                                public void onError(Throwable error){
                                    Log.d(TAG, error.getMessage());
                                }
                            });

                        } finally {
                            //The location has been deleted so move the user back the map view and
                            //reload their map markers.
                            MainActivity mainActivity = ((MainActivity) getActivity());
                            mainActivity.setLookingAtMap(true);
                            mainActivity.getFloatingActionMenu().setVisibility(View.VISIBLE);
                            mainActivity.getUserLocationPings();
                            if(realm != null){
                                realm.close();
                            }
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        //When the user is done adding the location they are redirected back the "home page"
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getFloatingActionMenu().setVisibility(View.VISIBLE);
        mainActivity.setLookingAtMap(true);
        mainActivity.getUserLocationPings();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
