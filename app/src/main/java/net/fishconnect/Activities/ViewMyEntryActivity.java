package net.fishconnect.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.android.gms.maps.SupportMapFragment;

import net.fishconnect.Adapters.EntryAdapter;
import net.fishconnect.Models.EntryModel;
import net.fishconnect.Models.IncidentModel;
import net.fishconnect.R;
import net.fishconnect.Utilities.Sharedprefstrings;
import net.fishconnect.Volley.AppController;
import net.fishconnect.Volley.CustomRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewMyEntryActivity extends AppCompatActivity {
    ProgressDialog pDialog;
    public String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    public List<EntryModel> entrylist = new ArrayList<EntryModel>();
    ListView Entrylist;
    TextView title;
    String slnumber;
    EntryAdapter entryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       title=(TextView)findViewById(R.id.title);
        Entrylist=(ListView)findViewById(R.id.entrylist);

        sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, MODE_WORLD_READABLE);
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");


        Entrylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(Sharedprefstrings.from_view_myentry, true);
                editor.apply();
                EntryModel em= entrylist.get(position);
                slnumber=em.getSlnumber();
                Intent intent = new Intent(ViewMyEntryActivity.this, IncidentDetailActivity.class);
                intent.putExtra("slnumber",slnumber);
                startActivity(intent);
            }
        });


        apicall();

    }

    void apicall() {
        if (isNetworkAvailable())
        {
            String urlString = "http://blueeeagle.in/demo/mobile/api/get_my_entries.php";
            Log.v("TAG", "Final Requsting URL is :" + urlString);
            // Params
            Map<String, String> jsonParams = new HashMap<String, String>();
            jsonParams.put("m_reg_id", sharedpreferences.getString(Sharedprefstrings.member_reg_id, ""));

            CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST, urlString, jsonParams, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    Log.e("jsonresponse", response.toString());
                    pDialog.dismiss();
                  //  {"result":["1","2","3","5","11"],"status":true}
                    entrylist.clear();
                    try {
                        JSONObject myJsonObject = response;
                        Boolean status = myJsonObject.getBoolean("status");

                        if (status) {
                            try {

                                JSONArray rootarray = myJsonObject.getJSONArray("result");

                                for(int i=0;i<rootarray.length();i++)
                                {
                                    EntryModel em= new EntryModel();

                                    JSONObject entry = rootarray.getJSONObject(i);

                                    String slno= entry.getString("slno");
                                    String entry_time = entry.getString("time_stamp");

                                    em.setSlnumber(slno);
                                    em.setEntry_time(entry_time);
                                    entrylist.add(em);
                                }

                                title.setText("My Total Entry : "+entrylist.size());
                                entryAdapter=new EntryAdapter(ViewMyEntryActivity.this,entrylist);
                                Entrylist.setAdapter(entryAdapter);
                                entryAdapter.notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            title.setText("My Total Entry : "+"0");

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

                    Log.e("error", "getDeliveryOptions = " + error.toString());
                    if (error instanceof TimeoutError) {

                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(ViewMyEntryActivity.this, "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            AppController.getInstance().addToRequestQueue(jsonObjReq);
            //AppController.getInstance(getActivity()).addToRequestQueue(jsonObjReq);
            //Volley.newRequestQueue(getActivity()).add(jsonObjReq);
        }else
        {
            pDialog.dismiss();
            Toast.makeText(ViewMyEntryActivity.this, "Check your internet Connection and try again", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }
        else if(id==android.R.id.home) {
            this.onBackPressed();
            return true;

        }


        return super.onOptionsItemSelected(item);
    }







    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ViewMyEntryActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

}
