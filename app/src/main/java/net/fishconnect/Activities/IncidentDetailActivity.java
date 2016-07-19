package net.fishconnect.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class IncidentDetailActivity extends AppCompatActivity {
    String slnumber;
    ProgressDialog pDialog;
    public String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    String res_sno, res_inc_id, res_date, res_specie, res_bow,
            res_basin, res_ma_area, res_mi_ara, res_lat,
            res_lon, res_dist, res_rating, res_depth, res_action, res_hot_spot, res_comment, res_direction, res_desc;
    public List<IncidentModel> incidentlist = new ArrayList<IncidentModel>();
    TextView inc_id_tv, date_tv, specie_tv, wbody_tv, basin_tv, marea_tv, mi_area_tv, direction_tv, distance_tv, rating_tv, depth_tv,
            action_tv, hot_spot_tv, comment_tv, lat_tv, lon_tv;
    Button map_btn;
    int days = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, MODE_WORLD_READABLE);
        //inc_id_tv=(TextView)findViewById(R.id.inc_id);
        date_tv = (TextView) findViewById(R.id.date);
        specie_tv = (TextView) findViewById(R.id.specie);
        wbody_tv = (TextView) findViewById(R.id.waterbody);
        basin_tv = (TextView) findViewById(R.id.basin);
        marea_tv = (TextView) findViewById(R.id.major_area);
        mi_area_tv = (TextView) findViewById(R.id.minor_area);
        direction_tv = (TextView) findViewById(R.id.direction);
        distance_tv = (TextView) findViewById(R.id.distance);
        rating_tv = (TextView) findViewById(R.id.rating);
        depth_tv = (TextView) findViewById(R.id.depth);
        action_tv = (TextView) findViewById(R.id.action);
        hot_spot_tv = (TextView) findViewById(R.id.hot_spot);
        comment_tv = (TextView) findViewById(R.id.comment);
        lat_tv = (TextView) findViewById(R.id.latitude);
        lon_tv = (TextView) findViewById(R.id.longitude);
        map_btn = (Button) findViewById(R.id.map_button);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");


        Intent i = getIntent();
        slnumber = i.getStringExtra("slnumber");
        //  Toast.makeText(IncidentDetailActivity.this, slnumber, Toast.LENGTH_SHORT).show();
        apicall();


        map_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IncidentDetailActivity.this, MapActivity.class);
                intent.putExtra("spot", res_hot_spot);
                intent.putExtra("desc", res_desc);
                intent.putExtra("lat", "42.066446");
                intent.putExtra("long", "-81.340597");
                startActivity(intent);
            }
        });
    }

    public void apicall() {

        // final ProgressDialog pDialog;
        pDialog = new ProgressDialog(IncidentDetailActivity.this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();


        if (isNetworkAvailable()) {

            pDialog.show();
            String urlString = "";
            Map<String, String> jsonParams = new HashMap<String, String>();
            urlString = "http://blueeeagle.in/demo/mobile/api/get_detailed_incident_result.php";
            jsonParams.put("slnumber", slnumber);

            Log.v("TAG", "Final Requsting URL is :" + urlString);

            CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST, urlString, jsonParams, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    // rl.setVisibility(View.GONE);
                    // Just flush the areas & Buildings to insert new one.
                    Log.e("jsonresponse", response.toString());
                    pDialog.dismiss();

                    try {
                        JSONObject myJsonObject = response;
                        Boolean status = myJsonObject.getBoolean("status");

                        if (status) {
                            try {


                                JSONArray rootarray = myJsonObject.getJSONArray("result");
                                incidentlist.clear();
                                for (int i = 0; i < rootarray.length(); i++) {
                                    JSONObject incident = rootarray.getJSONObject(i);
                                    IncidentModel im = new IncidentModel();

                                    res_sno = incident.getString("sno");
                                    res_inc_id = incident.getString("inc_id");
                                    res_date = incident.getString("date");


                                    String[] items1 = res_date.split("-");
                                    String y1 = items1[0];
                                    String m1 = items1[1];
                                    String d1 = items1[2];
                                    int d2 = Integer.parseInt(d1);
                                    int m2 = Integer.parseInt(m1);
                                    int y2 = Integer.parseInt(y1);

                                  //  Log.e("dd/mm/yyyy", d2 + "-" + m2 + "-" + y2);

                                    try {
                                        days = getDays(d2 + "-" + m2 + "-" + y2);
                                    } catch (ParseException e) {

                                    }

                                    res_specie = incident.getString("specie");
                                    res_bow = incident.getString("bow");
                                    res_basin = incident.getString("basin");
                                    res_ma_area = incident.getString("ma_area");
                                    res_mi_ara = incident.getString("mi_area");
                                    res_direction = incident.getString("direction");
                                    res_dist = incident.getString("dist");
                                    res_rating = incident.getString("rating");
                                    res_depth = incident.getString("depth");
                                    res_action = incident.getString("action");
                                    res_hot_spot = incident.getString("hot_spot");
                                    res_desc = incident.getString("description");
                                    res_lat = incident.getString("latitude");
                                    res_lon = incident.getString("longitude");
                                    res_comment = incident.getString("comment");

                                    // inc_id_tv.setText(res_inc_id);
                                    if(days>1) {
                                        date_tv.setText(days + " Days ago");
                                    }else if(days==0)
                                    {
                                        date_tv.setText("Today");
                                    }else if(days==1)
                                    {
                                        date_tv.setText("Yesterday");
                                    }
                                    specie_tv.setText(res_specie);
                                    wbody_tv.setText(res_bow);
                                    basin_tv.setText(res_basin);
                                    marea_tv.setText(res_ma_area);
                                    mi_area_tv.setText(res_mi_ara);
                                    direction_tv.setText(res_direction);
                                    distance_tv.setText(res_dist);
                                    rating_tv.setText(res_rating);
                                    depth_tv.setText(res_depth);
                                    action_tv.setText(res_action);
                                    hot_spot_tv.setText(res_desc + " #" + res_hot_spot);
                                    lat_tv.setText(res_lat);
                                    lon_tv.setText(res_lon);
                                    comment_tv.setText(res_comment);

                                    im.setSlnumber(res_sno);
                                    im.setInc_id(res_inc_id);
                                    im.setDate(res_date);
                                    im.setSpecie(res_specie);
                                    im.setBow(res_bow);
                                    im.setBasin(res_basin);
                                    im.setMajor(res_ma_area);
                                    im.setMinor(res_mi_ara);
                                    im.setDirection(res_direction);
                                    im.setDistance(res_dist);
                                    im.setRating(res_rating);
                                    im.setDepth(res_depth);
                                    im.setAction(res_action);
                                    im.setHot_spot(res_hot_spot);
                                    im.setDescription(res_desc);
                                    im.setComment(res_comment);
                                    im.setLat(res_lat);
                                    im.setLon(res_lon);
                                    incidentlist.add(im);

                                }
                                Log.e("days", days + "");
                                Log.e("inc_list", incidentlist.size() + "");


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {

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
                        //Toast.makeText(IncidentDetailActivity.this, "Some thing went wrong,can not reach the server", Toast.LENGTH_SHORT).show();

                        // tverrortext.setText("Conneciton timed out. Do Refersh");


                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(IncidentDetailActivity.this, "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(IncidentDetailActivity.this, "Some thing went wrong,can not reach the server", Toast.LENGTH_SHORT).show();


        }

    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) IncidentDetailActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            this.onBackPressed();
            return true;

        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (sharedpreferences.getBoolean(Sharedprefstrings.from_view_myentry, false)) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(Sharedprefstrings.from_view_myentry, false);
            editor.apply();

            map_btn.setVisibility(View.GONE);

        }

    }


    public int getDays(String date) throws ParseException {

        long MILLIS_PER_DAY = 24 * 60 * 60 * 1000;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        long begin = dateFormat.parse(date).getTime();
        long end = new Date().getTime(); // 2nd date want to compare
        long diff = (end - begin) / (MILLIS_PER_DAY);
        Log.e("days",diff+"");
        return (int) diff;
    }




}
