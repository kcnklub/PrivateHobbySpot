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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import kylem.privatehobbyspot.entities.LocationPing;
import kylem.privatehobbyspot.entities.User;

public class LocationDetails extends android.app.Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LOCATION_ID = "Location id";
    private static final String LOCATION_NAME = "Location Name";
    private static final String LOCATION_DESCRIPTION = "Location Description";
    private static final String LOCATION_TYPE = "Location Type";
    private static final String TAG = "Location Details";
    private static final String USER_IS_CREATOR = "User Is Creator";


    // TODO: Rename and change types of parameters
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

    private OnFragmentInteractionListener mListener;

    public LocationDetails() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static LocationDetails newInstance(int locationId, String locationName, String locationDescription, int locationType, boolean userIsCreator) {
        LocationDetails fragment = new LocationDetails();
        Bundle args = new Bundle();
        args.putInt(LOCATION_ID, locationId);
        args.putString(LOCATION_NAME, locationName);
        args.putString(LOCATION_DESCRIPTION, locationDescription);
        args.putInt(LOCATION_TYPE, locationType);
        args.putBoolean(USER_IS_CREATOR, userIsCreator);
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
            mUserIsCreator = getArguments().getBoolean(USER_IS_CREATOR);
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
        addUserToView = (EditText) getView().findViewById(R.id.user_to_view);
        addUserToViewButton = (Button) getView().findViewById(R.id.add_user_to_view);
        usersSharedWith = (ListView) getView().findViewById(R.id.users_shared_with);

        if(!mUserIsCreator){
            addUserToView.setVisibility(View.INVISIBLE);
            addUserToViewButton.setVisibility(View.INVISIBLE);
            deletePingButton.setVisibility(View.INVISIBLE);
            usersSharedWith.setVisibility(View.INVISIBLE);
        } else {
            Realm realm = Realm.getDefaultInstance();
            RealmResults<LocationPing> locationPingRealmResults = realm.where(LocationPing.class).equalTo("Id", mlocationId).findAll();


            final ArrayList<User> userSharedList = new ArrayList<User>(locationPingRealmResults.first().getUsersThatCanViewThisLocationPing());
            /*
            final ArrayList<String> list = new ArrayList<String>();
            for(int i = 0; i < userSharedList.size(); i++){
                list.add(userSharedList.get(i).getEmail());
            }

            ListAdapter adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, list);
            usersSharedWith.setAdapter(adapter);
               */

            UserSharedWithAdapter adapter = new UserSharedWithAdapter(
                    getActivity().getBaseContext(), R.layout.listview_item_row,
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
                        location.getUsersThatCanViewThisLocationPing().add(friendUser);
                        realm.commitTransaction();
                        Log.d(TAG, "User can view this now");
                        Toast.makeText(getActivity().getApplicationContext(), "Location Shared with " + friendUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "User Does NOT Exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Input Blank", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

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
