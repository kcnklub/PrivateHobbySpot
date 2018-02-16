package kylem.privatehobbyspot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;
import kylem.privatehobbyspot.entities.DayOptions;
import kylem.privatehobbyspot.entities.User;
import kylem.privatehobbyspot.entities.UserLocationPingViewOptions;
import kylem.privatehobbyspot.modules.commonModule;

public class UserViewOptionsActivity extends AppCompatActivity {

    private static final String TAG = "USER VIEW OPTIONS ACT";

    private String userID;
    private String userDisplayName;
    private int locationID;
    private ListView days;
    
    private TextView usernameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view_options);

        userID = getIntent().getStringExtra("userID");
        locationID = getIntent().getIntExtra("LocationID", -1);
        userDisplayName = getIntent().getStringExtra("userDisplayName");
        
        usernameView = (TextView) findViewById(R.id.details_username);
        usernameView.setText(userDisplayName);

        Realm realm = Realm.getDefaultInstance();
        UserLocationPingViewOptions userLocationPingViewOptions = realm.where(UserLocationPingViewOptions.class)
                .equalTo("UserID", userID)
                .equalTo("LocationPingID", locationID)
                .findFirst();

        if(userLocationPingViewOptions != null){
            final ArrayList<DayOptions> dayOptionsArrayList = new ArrayList<DayOptions>(userLocationPingViewOptions.getDayOptionsArrayList());

            days = (ListView) findViewById(R.id.daysList);

            DaysOfTheWeekSettingsAdapter daysOfTheWeekSettingsAdapter = new DaysOfTheWeekSettingsAdapter(
                    this, R.layout.listview_day_setting_row, dayOptionsArrayList);

            days.setAdapter(daysOfTheWeekSettingsAdapter);
            days.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent singleDaySetting = new Intent(UserViewOptionsActivity.this, SingleDaySettingActivity.class);
                    singleDaySetting.putExtra("id", dayOptionsArrayList.get(position).getId());
                    singleDaySetting.putExtra("dayOfTheWeek", position);
                    singleDaySetting.putExtra("canView", dayOptionsArrayList.get(position).isCanView());
                    singleDaySetting.putExtra("canViewAllDay", dayOptionsArrayList.get(position).isCanViewAllDay());
                    singleDaySetting.putExtra("hourStart", dayOptionsArrayList.get(position).getHourStart());
                    singleDaySetting.putExtra("hourStop", dayOptionsArrayList.get(position).getHourStop());
                    singleDaySetting.putExtra("hourStartAM", dayOptionsArrayList.get(position).isAMStart());
                    singleDaySetting.putExtra("hourStopAM", dayOptionsArrayList.get(position).isAMStop());

                    UserViewOptionsActivity.this.startActivity(singleDaySetting);

                }
            });
        } else {
            Toast.makeText(this, "The length of userLocationPing si 0", Toast.LENGTH_SHORT).show();
        }
    }
}
