package kylem.privatehobbyspot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import kylem.privatehobbyspot.entities.DayOptions;
import kylem.privatehobbyspot.entities.User;
import kylem.privatehobbyspot.entities.UserLocationPingViewOptions;

public class UserViewOptionsActivity extends AppCompatActivity {

    private String userEmail;
    private int locationID;
    private ListView days;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view_options);

        userEmail = getIntent().getStringExtra("userEmail");
        locationID = getIntent().getIntExtra("LocationID", -1);

        Realm realm = Realm.getDefaultInstance();

        RealmResults<User> user = realm.where(User.class).equalTo("Email", userEmail).findAll();
        User userInQuestion = user.first();


        RealmResults<UserLocationPingViewOptions> userLocationPingViewOptions = realm.where(UserLocationPingViewOptions.class)
                .equalTo("UserID", userInQuestion.getEmail())
                .equalTo("LocationPingID", locationID)
                .findAll();


        ArrayList<DayOptions> dayOptionsArrayList = new ArrayList<DayOptions>(userLocationPingViewOptions.first().getDayOptionsArrayList());

        days = (ListView) findViewById(R.id.daysList);

        DaysOfTheWeekSettingsAdapter daysOfTheWeekSettingsAdapter = new DaysOfTheWeekSettingsAdapter(
                this, R.layout.listview_day_setting_row, dayOptionsArrayList);

        days.setAdapter(daysOfTheWeekSettingsAdapter);
    }
}
