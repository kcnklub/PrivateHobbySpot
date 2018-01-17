package kylem.privatehobbyspot;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import kylem.privatehobbyspot.entities.DayOptions;

/**
 * Created by Kyle on 1/8/2018.
 */

public class DaysOfTheWeekSettingsAdapter extends ArrayAdapter<DayOptions> {

    Context context;
    int layoutResourceID;
    List<DayOptions> data;
    SettingsHolder holder = null;

    public DaysOfTheWeekSettingsAdapter(Context context, int layoutResourceId, List<DayOptions> data){
        super(context, layoutResourceId, data);
        this.layoutResourceID = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View row = convertView;
        holder = null;

        if(row == null){
            Activity activity = (Activity) context;
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(layoutResourceID, parent, false);

            holder = new SettingsHolder();
            holder.dayOfTheWeek = (TextView)row.findViewById(R.id.dayOfTheWeek);
            holder.canViewSwitch = (Switch)row.findViewById(R.id.CanView);
            holder.canViewAllDayCheckbox = (CheckBox)row.findViewById(R.id.allDayCheckBox);
            holder.startingHour = (TextView)row.findViewById(R.id.startingHour);
            holder.stoppingHour = (TextView)row.findViewById(R.id.stoppingHour);
            holder.setStartingHourButton = (Button)row.findViewById(R.id.setStartingHourButton);
            holder.setStoppingHourButton = (Button)row.findViewById(R.id.setStoppingHourButton);

            row.setTag(holder);
        } else {
            holder = (SettingsHolder)row.getTag();
        }

        final DayOptions dayOptions = data.get(position);

        switch(position){
            case 0:
                holder.dayOfTheWeek.setText("Monday");
                break;
            case 1:
                holder.dayOfTheWeek.setText("Tuesday");
                break;
            case 2:
                holder.dayOfTheWeek.setText("Wednesday");
                break;
            case 3:
                holder.dayOfTheWeek.setText("Thursday");
                break;
            case 4:
                holder.dayOfTheWeek.setText("Friday");
                break;
            case 5:
                holder.dayOfTheWeek.setText("Saturday");
                break;
            case 6:
                holder.dayOfTheWeek.setText("Sunday");
        }

        holder.canViewAllDayCheckbox.setChecked(dayOptions.isCanViewAllDay());
        if(dayOptions.isCanViewAllDay()){
            holder.startingHour.setVisibility(View.INVISIBLE);
            holder.stoppingHour.setVisibility(View.INVISIBLE);
            holder.setStartingHourButton.setVisibility(View.INVISIBLE);
            holder.setStoppingHourButton.setVisibility(View.INVISIBLE);
        } else {
            holder.startingHour.setText(dayOptions.getHourStart());
            holder.stoppingHour.setText(dayOptions.getHourStop());

        }

        holder.canViewAllDayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // your code for checked checkbox
                    holder.startingHour.setVisibility(View.INVISIBLE);
                    holder.stoppingHour.setVisibility(View.INVISIBLE);
                    holder.setStartingHourButton.setVisibility(View.INVISIBLE);
                    holder.setStoppingHourButton.setVisibility(View.INVISIBLE);

                } else {
                    // your code to no checked checkbox
                    holder.startingHour.setText(dayOptions.getHourStart());
                    holder.stoppingHour.setText(dayOptions.getHourStop());
                }
            }
        });

        holder.setStartingHourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.setStoppingHourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        //LayoutInflater inflater = (TextView)row.findViewById();
        return row;
    }

    static class SettingsHolder{
        TextView dayOfTheWeek;
        Switch canViewSwitch;
        CheckBox canViewAllDayCheckbox;
        TextView startingHour;
        TextView stoppingHour;
        Button setStartingHourButton;
        Button setStoppingHourButton;
    }

    public void onCheckBoxClicked(View view){

    }

}
