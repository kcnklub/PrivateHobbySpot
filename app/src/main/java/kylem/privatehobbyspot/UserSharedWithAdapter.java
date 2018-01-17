package kylem.privatehobbyspot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
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
import kylem.privatehobbyspot.entities.LocationPing;
import kylem.privatehobbyspot.entities.User;
import kylem.privatehobbyspot.entities.UserLocationPingViewOptions;

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

        User user = data.get(position);
        holder.userEmail.setText(user.getEmail());
        holder.userDisplayName.setText(user.getDisplayName());


        holder.unshareButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){
                Realm realm = Realm.getDefaultInstance();
                LocationDetailsActivity locationDetailsActivity = (LocationDetailsActivity) context;

                if(locationDetailsActivity != null){
                    RealmResults<LocationPing> location = realm.where(LocationPing.class).equalTo("Id", locationDetailsActivity.getMlocationId()).findAll();
                    RealmResults<User> user = realm.where(User.class).equalTo("Email", data.get(position).getEmail()).findAll();
                    User userUnshare = user.first();
                    LocationPing ping = location.first();
                    realm.beginTransaction();
                    if(ping.getUsersThatCanViewThisLocationPing() != null){
                        ping.getUsersThatCanViewThisLocationPing().remove(userUnshare);
                    }

                    RealmResults<UserLocationPingViewOptions> userLocationPingViewOptionsRealmResults = realm.where(UserLocationPingViewOptions.class)
                            .equalTo("UserID", userUnshare.getEmail())
                            .equalTo("LocationPingID", ping.getId())
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
                //TODO: start the user options activity.
                Intent intent = new Intent((LocationDetailsActivity) context, UserViewOptionsActivity.class);
                intent.putExtra("LocationID", ((LocationDetailsActivity) context).getMlocationId());
                intent.putExtra("userEmail", data.get(position).getEmail());
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
