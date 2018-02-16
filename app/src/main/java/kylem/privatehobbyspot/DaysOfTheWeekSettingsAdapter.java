package kylem.privatehobbyspot;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.util.Log;
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

    private final static String TAG = "DOTW ADAPTER";

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
            holder.startingHour = (TextView)row.findViewById(R.id.starting_hour);
            holder.stoppingHour = (TextView)row.findViewById(R.id.stopping_hour);

            row.setTag(holder);
        } else {
            holder = (SettingsHolder)row.getTag();
        }

        final DayOptions dayOptions = data.get(position);

        holder.canViewSwitch.setChecked(dayOptions.isCanView());
        holder.canViewSwitch.setClickable(false);
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
        holder.canViewAllDayCheckbox.setClickable(false);
        if(dayOptions.isCanViewAllDay()){
            holder.startingHour.setVisibility(View.GONE);
            holder.stoppingHour.setVisibility(View.GONE);
        } else {
            String temp = String.valueOf(dayOptions.getHourStart()) + (dayOptions.isAMStart() ? "AM" : "PM");
            holder.startingHour.setText(temp);
            temp = String.valueOf(dayOptions.getHourStop()) + (dayOptions.isAMStop() ? "AM" : "PM");
            holder.stoppingHour.setText(temp);
        }
        return row;
    }

    static class SettingsHolder{
        TextView dayOfTheWeek;
        Switch canViewSwitch;
        CheckBox canViewAllDayCheckbox;
        TextView startingHour;
        TextView stoppingHour;
    }
}
