package kylem.privatehobbyspot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;
import kylem.privatehobbyspot.entities.LocationPing;
import kylem.privatehobbyspot.entities.User;
import kylem.privatehobbyspot.entities.UserLocationPingViewOptions;
import kylem.privatehobbyspot.modules.commonModule;

/**
 * Created by kylem on 12/31/2017.
 */

public class UserSharedWithAdapter extends ArrayAdapter<User>{

    private final String TAG = "User Shared With Adapter";

    Context context;
    int layoutResourceId;
    List<User> data = null;

    public UserSharedWithAdapter(Context context, int layoutResourceId, List<User> data){
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        UserHolder holder = null;

        if(row == null){
            Activity activity = (Activity) context;
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new UserHolder();
            holder.userEmail = (TextView)row.findViewById(R.id.userEmail);
            holder.userDisplayName = (TextView)row.findViewById(R.id.userDisplayName);
            holder.unshareButton = (Button)row.findViewById(R.id.unshareButton);
            holder.settingsButton = (Button)row.findViewById(R.id.userOptions);

            row.setTag(holder);
        } else {
            holder = (UserHolder)row.getTag();
        }

        final User user = data.get(position);
        holder.userEmail.setText(user.getDisplayName());
        holder.userDisplayName.setText(user.getDisplayName());
        holder.unshareButton.setOnClickListener(new View.OnClickListener(){
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View arg0){
                Realm realm = Realm.getDefaultInstance();
                LocationDetailsActivity locationDetailsActivity = (LocationDetailsActivity) context;
                if(locationDetailsActivity != null){
                    RealmResults<LocationPing> location = realm.where(LocationPing.class).equalTo(LocationPing.LOCATION_PING_ID, locationDetailsActivity.getMlocationId()).findAll();
                    LocationPing ping = location.first();
                    realm.beginTransaction();
                    if(ping.getUsersThatCanViewThisLocationPing() != null){
                        ping.getUsersThatCanViewThisLocationPing().remove(user);
                    }
                    RealmResults<UserLocationPingViewOptions> userLocationPingViewOptionsRealmResults = realm.where(UserLocationPingViewOptions.class)
                            .equalTo(UserLocationPingViewOptions.VIEW_OPTIONS_USER_ID, user.getId())
                            .equalTo(UserLocationPingViewOptions.VIEW_OPTIONS_LOCATION_ID, ping.getId())
                            .findAll();

                    userLocationPingViewOptionsRealmResults.deleteAllFromRealm();
                    Log.d(TAG, "deleted");

                    realm.commitTransaction();
                    realm.close();
                    locationDetailsActivity.recreate();
                } else{
                    Toast.makeText(context, "location details null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start the user options activity.
                Intent intent = new Intent((LocationDetailsActivity) context, UserViewOptionsActivity.class);
                intent.putExtra("LocationID", ((LocationDetailsActivity) context).getMlocationId());
                intent.putExtra("userID", user.getId());
                intent.putExtra("userDisplayName", user.getDisplayName());
                ((LocationDetailsActivity) context).startActivity(intent);
            }
        });
        return row;
    }

    static class UserHolder {
        TextView userEmail;
        TextView userDisplayName;
        Button unshareButton;
        Button settingsButton;
    }
}
