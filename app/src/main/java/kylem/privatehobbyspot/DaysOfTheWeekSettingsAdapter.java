package kylem.privatehobbyspot;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    public DaysOfTheWeekSettingsAdapter(Context context, int layoutResourceId, List<DayOptions> data){
        super(context, layoutResourceId, data);
        this.layoutResourceID = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View row = convertView;
        //LayoutInflater inflater = (TextView)row.findViewById();
        return row;
    }

}
