package net.fishconnect.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import net.fishconnect.Activities.Adapters.IncidentAdapter;
import net.fishconnect.Models.IncidentModel;
import net.fishconnect.R;
import net.fishconnect.Utilities.Sharedprefstrings;
import net.fishconnect.Volley.AppController;
import net.fishconnect.Volley.CustomRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    ImageView hambarger_btn;
    PopupMenu popupMenu;
    ProgressDialog pDialog;
    JSONObject myJsonObject;
    String[] waterbody;
    String selectedwaterbody, selectedbasin, selectedspecie;
    String[] basin;
    String[] specie;
    int marker = 0;
    Spinner basin_spinner, specie_spinner;
    RelativeLayout searchbar_bsin, searchbar_specie;
    ListView result_list;
    String res_sno,res_hot_spot,res_direction,res_rating,res_distance,res_description, res_date;
    public List<IncidentModel> incidentlist = new ArrayList<IncidentModel>();
    IncidentAdapter incadapter;
    TextView noresult_tv,result_title;
    Boolean w_body_filled=false;
    String slnumber;
    public String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  setSupportActionBar(toolbar);
        searchbar_bsin = (RelativeLayout) findViewById(R.id.searchbar_basin);
        searchbar_specie = (RelativeLayout) findViewById(R.id.searchbar_specie);

        basin_spinner = (Spinner) findViewById(R.id.spinner_basin);
        specie_spinner = (Spinner) findViewById(R.id.spinner_specie);
        result_list=(ListView) findViewById(R.id.result_list);
        setListViewHeightBasedOnChildren(result_list);
        result_list.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
        noresult_tv=(TextView)findViewById(R.id.noresult_tv);
        result_title=(TextView)findViewById(R.id.result_title);
        sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, MODE_WORLD_READABLE);
       // incadapter = new IncidentAdapter(HomeActivity.this,incidentlist);
        //result_list.setAdapter(incadapter);


        pDialog = new ProgressDialog(HomeActivity.this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");

        if(isNetworkAvailable())
        {
            apicall();
        }else
        {
            Toast.makeText(HomeActivity.this, "Check your internet connection and try again", Toast.LENGTH_SHORT).show();
            finish();
        }
        Log.e("marker ", marker + "");

        hambarger_btn = (ImageView) findViewById(R.id.hambergarbutton);
        hambarger_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu = new PopupMenu(HomeActivity.this, v);
                popupMenu.setOnDismissListener(new OnDismissListener());
                popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener());
                setForceShowIcon(popupMenu);
                popupMenu.inflate(R.menu.home_menu);
                popupMenu.show();

            }

        });

        result_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                slnumber= incidentlist.get(position).getSlnumber();
                Intent intent = new Intent(HomeActivity.this, IncidentDetailActivity.class);
                intent.putExtra("slnumber",slnumber);
                startActivity(intent);
            }
        });
    }
    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    public void setup_waterbody()
    {


        final Spinner waterbody_spinner = (Spinner) findViewById(R.id.spinner_wtrbdy);
        final List<String> waterbody_list = new ArrayList<>(Arrays.asList(waterbody));

        final ArrayAdapter<String> spinnerArrayAdapter_Waterbody_Item = new ArrayAdapter<String>(
                HomeActivity.this, R.layout.spinner_item, waterbody_list) {

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.WHITE);
                }
                return view;
            }
        };

        spinnerArrayAdapter_Waterbody_Item.setDropDownViewResource(R.layout.spinner_item);
        waterbody_spinner.setAdapter(spinnerArrayAdapter_Waterbody_Item);

        waterbody_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedwaterbody = (String) parent.getItemAtPosition(position);
                searchbar_bsin.setVisibility(View.GONE);
                searchbar_specie.setVisibility(View.GONE);
                result_title.setVisibility(View.GONE);
                noresult_tv.setVisibility(View.GONE);
                result_list.setVisibility(View.GONE);
                //Toast.makeText(HomeActivity.this, "Selected : " + selectedwaterbody, Toast.LENGTH_SHORT).show();

                if (position > 0) {

                    marker = 1;

                    if(isNetworkAvailable())
                    {
                        apicall();
                    }else
                    {
                        Toast.makeText(HomeActivity.this, "Check your internet connection and try again", Toast.LENGTH_SHORT).show();

                    }




                }

            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    public static void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private class OnDismissListener implements PopupMenu.OnDismissListener {

        @Override
        public void onDismiss(PopupMenu menu) {
            // TODO Auto-generated method stub

        }

    }

    private class OnMenuItemClickListener implements
            PopupMenu.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            // TODO Auto-generated method stub
            switch (item.getItemId()) {

                case R.id.exit:
                    showecloseAlert();

                    //Toast.makeText(getApplicationContext(), "Android got clicked",
                    // Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.fish_connect:
                    Intent intent = new Intent(HomeActivity.this, FishResultEntryActivity.class);
                    startActivity(intent);
                    return true;


                case R.id.view_entry:
                    Intent i = new Intent(HomeActivity.this, ViewMyEntryActivity.class);
                    startActivity(i);
                    return true;


            }
            return false;
        }


    }

    public void setbasinspinner() {
                List<String> basin_list = new ArrayList<>(Arrays.asList(basin));
                ArrayAdapter<String> spinnerArrayAdapter_Bsin_Item = new ArrayAdapter<String>(
                        HomeActivity.this, R.layout.spinner_item, basin_list) {

                    @Override
                    public boolean isEnabled(int position) {
                        if (position == 0) {
                            // Disable the first item from Spinner
                            // First item will be use for hint
                            return false;
                        } else {
                            return true;
                        }
                    }

                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position == 0) {
                            // Set the hint text color gray
                            tv.setTextColor(Color.GRAY);
                        } else {
                            tv.setTextColor(Color.WHITE);
                        }
                        return view;
                    }
                };

                spinnerArrayAdapter_Bsin_Item.setDropDownViewResource(R.layout.spinner_item);
                basin_spinner.setAdapter(spinnerArrayAdapter_Bsin_Item);

                basin_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedbasin = (String) parent.getItemAtPosition(position);
                        searchbar_specie.setVisibility(View.GONE);
                        result_title.setVisibility(View.GONE);
                        noresult_tv.setVisibility(View.GONE);
                        result_list.setVisibility(View.GONE);
                        // if (position == 1) {

                        //Toast.makeText(HomeActivity.this, "Selected : " + selectedbasin, Toast.LENGTH_SHORT).show();

                        if (position > 0) {

                            marker = 2;



                            if(isNetworkAvailable())
                            {
                                apicall();
                            }else
                            {
                                Toast.makeText(HomeActivity.this, "Check your internet connection and try again", Toast.LENGTH_SHORT).show();

                            }


                        }
                    }


                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


    }


    public void setspeciespinner() {

                List<String> specie_list = new ArrayList<>(Arrays.asList(specie));
                ArrayAdapter<String> spinnerArrayAdapter_Specie_Item = new ArrayAdapter<String>(
                        HomeActivity.this, R.layout.spinner_item, specie_list) {

                    @Override
                    public boolean isEnabled(int position) {
                        if (position == 0) {
                            // Disable the first item from Spinner
                            // First item will be use for hint
                            return false;
                        } else {
                            return true;
                        }
                    }

                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position == 0) {
                            // Set the hint text color gray
                            tv.setTextColor(Color.GRAY);
                        } else {
                            tv.setTextColor(Color.WHITE);
                        }
                        return view;
                    }
                };

                spinnerArrayAdapter_Specie_Item.setDropDownViewResource(R.layout.spinner_item);
                specie_spinner.setAdapter(spinnerArrayAdapter_Specie_Item);

                specie_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedspecie = (String) parent.getItemAtPosition(position);
                        result_title.setVisibility(View.GONE);
                        noresult_tv.setVisibility(View.GONE);
                        result_list.setVisibility(View.GONE);
                        if (position > 0) {
                            marker = 3;
                            if(isNetworkAvailable())
                            {
                                apicall();
                            }else
                            {
                                Toast.makeText(HomeActivity.this, "Check your internet connection and try again", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }


                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
    }


    public void apicall() {

        // final ProgressDialog pDialog;
        pDialog = new ProgressDialog(HomeActivity.this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();


        if (isNetworkAvailable()) {

            pDialog.show();
            String urlString = "";
            Map<String, String> jsonParams = new HashMap<String, String>();
            if (marker == 0) {
                Log.e("marker ",marker+"");
                urlString = "http://blueeeagle.in/demo/mobile/api/waterbody.php";
                jsonParams.put("t", "");
            } else if (marker == 1) {
                urlString = "http://blueeeagle.in/demo/mobile/api/basin.php";
                jsonParams.put("m_reg_id",sharedpreferences.getString(Sharedprefstrings.member_reg_id,""));

            } else if (marker == 2) {
                urlString = "http://blueeeagle.in/demo/mobile/api/specie.php";
                Log.e("w_body",selectedwaterbody);
                Log.e("basin",selectedbasin);
                jsonParams.put("w_body", selectedwaterbody);
                jsonParams.put("basin", selectedbasin);

            }else if(marker==3)
            {
                urlString = "http://blueeeagle.in/demo/mobile/api/get_incident_result.php";
                Log.e("w_body",selectedwaterbody);
                Log.e("w_body",selectedbasin);
                Log.e("specie",selectedspecie);

                jsonParams.put("w_body", selectedwaterbody);
                jsonParams.put("basin", selectedbasin);
                jsonParams.put("specie", selectedspecie);
            }
            // Params
            Log.v("TAG", "Final Requsting URL is :" + urlString);

            CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST, urlString, jsonParams, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    // rl.setVisibility(View.GONE);
                    // Just flush the areas & Buildings to insert new one.
                    Log.e("jsonresponse", response.toString());
                    pDialog.dismiss();

                    try {
                        myJsonObject = response;
                        Boolean status = myJsonObject.getBoolean("status");

                        if (status) {
                            try {

                                JSONArray rootarray = myJsonObject.getJSONArray("result");

                                if (marker == 0) {

                                    waterbody = new String[rootarray.length() + 1];
                                    waterbody[0] = "Select Water body...";
                                    for (int i = 1; i <= rootarray.length(); i++) {

                                        waterbody[i] = rootarray.getString(i - 1);
                                        Log.e(i + "", waterbody[i]);

                                    }

                                    setup_waterbody();

                                } else if (marker == 1) {
                                    basin = new String[rootarray.length() + 1];
                                    basin[0] = "Select Basin...";
                                    for (int i = 1; i <= rootarray.length(); i++) {

                                        basin[i] = rootarray.getString(i - 1);
                                        Log.e(i + "", basin[i]);

                                    }
                                    HomeActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            searchbar_bsin.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    setbasinspinner();
                                } else if (marker == 2) {
                                    specie = new String[rootarray.length() + 1];
                                    specie[0] = "Select Specie...";
                                    for (int i = 1; i <= rootarray.length(); i++) {

                                        specie[i] = rootarray.getString(i - 1);
                                        Log.e(i + "", specie[i]);
                                    }
                                    HomeActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            searchbar_specie.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    setspeciespinner();
                                }else if (marker==3)
                                {
                                    result_title.setVisibility(View.VISIBLE);
                                    noresult_tv.setVisibility(View.GONE);
                                    result_list.setVisibility(View.VISIBLE);
                                    incidentlist.clear();
                                    for(int i=0;i<rootarray.length();i++)
                                    {
                                        JSONObject incident = rootarray.getJSONObject(i);
                                        IncidentModel im = new IncidentModel();

                                        res_sno=incident.getString("sno");
                                        res_description=incident.getString("description");
                                        res_hot_spot=incident.getString("hot_spot");
                                        res_direction=incident.getString("direction");
                                        res_distance=incident.getString("distance");
                                        res_rating=incident.getString("rating");
                                        res_date=incident.getString("date");
                                        im.setSlnumber(res_sno);
                                        im.setHot_spot(res_hot_spot);
                                        im.setDirection(res_direction);
                                        im.setDistance(res_distance);
                                        im.setRating(res_rating);
                                        im.setDescription(res_description);
                                        im.setDate(res_date);

                                        incidentlist.add(im);

                                    }
                                    Log.e("inc_length", incidentlist.size()+"");
                                    incadapter = new IncidentAdapter(HomeActivity.this,incidentlist);
                                    result_list.setAdapter(incadapter);
                                    incadapter.notifyDataSetChanged();


                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            if(marker==3)
                            {
                                result_list.setVisibility(View.GONE);
                                noresult_tv.setVisibility(View.VISIBLE);

                            }
                        }

                        VolleyLog.v("Response:%n %s", response.toString(4));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    pDialog.dismiss();


                    Log.e("error", error.toString());
                    if (error instanceof TimeoutError) {

                        apicall();
                      //  Toast.makeText(HomeActivity.this, "Some thing went wrong,can not reach the server", Toast.LENGTH_SHORT).show();

                        // tverrortext.setText("Conneciton timed out. Do Refersh");


                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(HomeActivity.this, "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                        //tverrortext.setText("Oops! No Network Connection available");
                    }
                }
            });

            jsonObjReq.setShouldCache(false);
            AppController.getInstance().addToRequestQueue(jsonObjReq);
            //AppController.getInstance(getActivity()).addToRequestQueue(jsonObjReq);
            //Volley.newRequestQueue(getActivity()).add(jsonObjReq);
        } else {

            pDialog.dismiss();
            Toast.makeText(HomeActivity.this, "Some thing went wrong,can not reach the server", Toast.LENGTH_SHORT).show();


        }

    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) HomeActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    @Override
    public void onBackPressed() {
            // super.onBackPressed();

            showecloseAlert();

    }

    public void showecloseAlert() {

        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle("Exit")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //Intent intent = new Intent(Intent.ACTION_MAIN);
                        //intent.addCategory(Intent.CATEGORY_HOME);
                        // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        // startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("No", null).show();
    }

}
