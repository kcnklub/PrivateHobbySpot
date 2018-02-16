package kylem.privatehobbyspot;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import io.realm.Realm;
import kylem.privatehobbyspot.entities.DayOptions;

public class SingleDaySettingActivity extends AppCompatActivity {

    private static final String TAG = "SINGLE DAY";

    private int id;
    private int dayOfTheWeek;
    private boolean canView;
    private boolean canViewAllDay;
    private int hourStart;
    private int hourStop;
    private boolean hourStartAM;
    private boolean hourStopAM;

    private TextView day;
    private Switch canViewSwitch;
    private Switch canViewAllDaySwitch;
    private TextInputEditText newStartingHour;
    private TextInputEditText newStoppingHour;
    private Switch hourStartSwitch;
    private Switch hourStopSwitch;
    private Button saveButton;
    private TextView oldStartHour;
    private TextView oldStopHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_day_setting);

        // get day information.
        Intent intent = this.getIntent();
        id = intent.getIntExtra("id", -1);
        dayOfTheWeek = intent.getIntExtra("dayOfTheWeek", -1);
        canView = intent.getBooleanExtra("canView", false);
        canViewAllDay = intent.getBooleanExtra("canViewAllDay", false);
        hourStart = intent.getIntExtra("hourStart", -1);
        hourStop = intent.getIntExtra("hourStop", -1);
        hourStartAM = intent.getBooleanExtra("hourStartAM", false);
        hourStopAM = intent.getBooleanExtra("hourSpotAM", false);

        day = (TextView) findViewById(R.id.day_of_the_week);
        switch(dayOfTheWeek){
            case 0:
                day.setText("Monday");
                break;
            case 1:
                day.setText("Tuesday");
                break;
            case 2:
                day.setText("Wednesday");
                break;
            case 3:
                day.setText("Thursday");
                break;
            case 4:
                day.setText("Friday");
                break;
            case 5:
                day.setText("Saturday");
                break;
            case 6:
                day.setText("Sunday");
        }

        canViewSwitch = (Switch) findViewById(R.id.can_view_setting);
        canViewSwitch.setChecked(canView);

        canViewAllDaySwitch = (Switch) findViewById(R.id.can_view_all_day_setting);
        canViewAllDaySwitch.setChecked(canViewAllDay);

        oldStartHour = (TextView) findViewById(R.id.hour_start_time);
        oldStopHour = (TextView) findViewById(R.id.hour_stop_time);

        newStartingHour = (TextInputEditText) findViewById(R.id.new_start);
        newStoppingHour = (TextInputEditText) findViewById(R.id.new_stop);

        hourStartSwitch = (Switch) findViewById(R.id.starting_am_switch);
        hourStopSwitch = (Switch) findViewById(R.id.stopping_am_switch);

        if(canViewAllDay){
            newStartingHour.setVisibility(View.GONE);
            newStoppingHour.setVisibility(View.GONE);
            hourStartSwitch.setVisibility(View.GONE);
            hourStopSwitch.setVisibility(View.GONE);
            oldStartHour.setVisibility(View.GONE);
            oldStopHour.setVisibility(View.GONE);
        } else {
            hourStartSwitch.setChecked(hourStartAM);
            hourStopSwitch.setChecked(hourStopAM);
            String temp = String.valueOf(hourStart) + (hourStartAM ? "AM" : "PM");
            oldStartHour.setText(temp);
            temp = String.valueOf(hourStop) + (hourStopAM ? "AM" : "PM");
            oldStopHour.setText(temp);
        }
        saveButton = (Button) findViewById(R.id.save_confirm);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm realm = Realm.getDefaultInstance();
                final DayOptions day = realm.where(DayOptions.class).equalTo(DayOptions.DAY_OPTIONS_ID, id).findFirst();
                try{
                    if(day != null){
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                day.setCanView(canViewSwitch.isChecked());
                                day.setCanViewAllDay(canViewAllDaySwitch.isChecked());
                                if(canViewAllDaySwitch.isChecked()){
                                    day.setHourStart(0);
                                    day.setHourStop(0);
                                    day.setAMStart(false);
                                    day.setAMStop(false);
                                } else {
                                    if(!(newStartingHour.getText().toString().equals(""))  || !(newStoppingHour.getText().toString().equals(""))){
                                        if(Integer.valueOf(newStartingHour.getText().toString()) > 12 || Integer.valueOf(newStartingHour.getText().toString()) < 12 ){
                                            day.setHourStart(Integer.valueOf(newStartingHour.getText().toString()));
                                        }
                                        if(Integer.valueOf(newStoppingHour.getText().toString()) > 12 || Integer.valueOf(newStoppingHour.getText().toString()) < 12 ){
                                            day.setHourStop(Integer.valueOf(newStoppingHour.getText().toString()));
                                        }
                                    } else {
                                        realm.cancelTransaction();
                                    }
                                    day.setAMStart(hourStartSwitch.isChecked());
                                    day.setAMStop(hourStopSwitch.isChecked());
                                }
                            }
                        }, new Realm.Transaction.OnSuccess(){
                            @Override
                            public void onSuccess(){
                                Log.d(TAG, "success");
                            }
                        }, new Realm.Transaction.OnError(){
                            @Override
                            public void onError(Throwable error){
                                Log.d(TAG, error.getMessage());
                            }
                        });
                    }
                }
                finally {
                    realm.close();
                    SingleDaySettingActivity.this.finish();
                }
            }
        });

        canViewAllDaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    newStartingHour.setVisibility(View.GONE);
                    newStoppingHour.setVisibility(View.GONE);
                    hourStartSwitch.setVisibility(View.GONE);
                    hourStopSwitch.setVisibility(View.GONE);
                    oldStartHour.setVisibility(View.GONE);
                    oldStopHour.setVisibility(View.GONE);

                } else {
                    newStartingHour.setVisibility(View.VISIBLE);
                    newStoppingHour.setVisibility(View.VISIBLE);
                    hourStartSwitch.setVisibility(View.VISIBLE);
                    hourStopSwitch.setVisibility(View.VISIBLE);
                    oldStartHour.setVisibility(View.VISIBLE);
                    oldStopHour.setVisibility(View.VISIBLE);

                    hourStartSwitch.setChecked(hourStartAM);
                    hourStopSwitch.setChecked(hourStopAM);

                    String temp = String.valueOf(hourStart) + (hourStartAM ? "AM" : "PM");
                    oldStartHour.setText(temp);
                    temp = String.valueOf(hourStop) + (hourStopAM ? "AM" : "PM");
                    oldStopHour.setText(temp);
                }
            }
        });

    }
}
