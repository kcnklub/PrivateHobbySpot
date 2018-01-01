package kylem.privatehobbyspot;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import kylem.privatehobbyspot.entities.User;

/**
 * Created by kylem on 12/31/2017.
 */

public class UserSharedWithAdapter extends ArrayAdapter<User>{

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
    public View getView(int position, View convertView, ViewGroup parent) {
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

            row.setTag(holder);
        } else {
            holder = (UserHolder)row.getTag();
        }

        User user = data.get(position);
        holder.userEmail.setText(user.getEmail());
        holder.userDisplayName.setText(user.getDisplayName());

        return row;

    }

    static class UserHolder {
        TextView userEmail;
        TextView userDisplayName;
        Button unshareButton;
    }
}
