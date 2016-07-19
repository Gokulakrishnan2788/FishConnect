package net.fishconnect.Activities.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.fishconnect.Models.IncidentModel;
import net.fishconnect.R;

import java.util.List;

/**
 * Created by MY on 5/28/2016.
 */
public class IncidentAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<IncidentModel> incidentModel;
    private Context adaptercontext;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    public IncidentAdapter()
    {

    }
    public IncidentAdapter(Activity activity, List<IncidentModel> incidentModel) {

        this.activity = activity;
        this.incidentModel = incidentModel;
    }

    @Override
    public int getCount() {

        return incidentModel.size();
    }

    @Override
    public Object getItem(int pos) {

        return incidentModel.get(pos);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.incident_result_item_layout, null);
        TextView inc_result=(TextView) convertView.findViewById(R.id.incident_result);


        IncidentModel im = incidentModel.get(position);
        String result= "Rating "+im.getRating()+" "+im.getDate()+" spot:" +im.getDescription()+" #"+ im.getHot_spot()+" "+ im.getDirection()+" within " + im.getDistance();
        inc_result.setText(result);
        return convertView;


    }

    public void clearlist(){
        incidentModel.clear();
    }

}
