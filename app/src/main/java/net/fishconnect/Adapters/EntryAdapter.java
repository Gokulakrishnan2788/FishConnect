package net.fishconnect.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.fishconnect.Models.EntryModel;
import net.fishconnect.Models.IncidentModel;
import net.fishconnect.R;

import java.util.List;

/**
 * Created by MY on 6/3/2016.
 */
public class EntryAdapter  extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<EntryModel> entrylist;
    private Context adaptercontext;
    public static final String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;

    public EntryAdapter()
    {

    }
    public EntryAdapter(Activity activity, List<EntryModel> entrylist) {

        this.activity = activity;
        this.entrylist = entrylist;
    }

    @Override
    public int getCount() {

        return entrylist.size();
    }

    @Override
    public Object getItem(int pos) {

        return entrylist.get(pos);
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
            convertView = inflater.inflate(R.layout.entry_item_layout, null);
        TextView entry =(TextView) convertView.findViewById(R.id.item);

        EntryModel em = entrylist.get(position);

        String result= "Incident Entry "+(position+1) + " On "+ em.getEntry_time();
        entry.setText(result);
        return convertView;


    }



}
