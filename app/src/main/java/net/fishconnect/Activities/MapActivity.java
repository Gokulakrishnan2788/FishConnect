package net.fishconnect.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import net.fishconnect.Models.IncidentModel;
import net.fishconnect.R;
import net.fishconnect.Volley.AppController;
import net.fishconnect.Volley.CustomRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    ProgressDialog pDialog;
    private GoogleMap mMap;
String lat,lon,spot,desc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");

        Intent i = getIntent();
        spot=i.getStringExtra("spot");
        desc=i.getStringExtra("desc");
        //lat=i.getStringExtra("lat");
       // lon=i.getStringExtra("long");
        //Log.e("lat",lat);
        //Log.e("long",lon);
        Log.e("spot", spot);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


    apicall();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in loc and move the camera
        LatLng loc = new LatLng(Double.parseDouble(lat.trim()), Double.parseDouble(lon.trim()));
        mMap.addMarker(new MarkerOptions().position(loc).title(desc));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 7));
    }


    public void apicall() {

        // final ProgressDialog pDialog;
        pDialog = new ProgressDialog(MapActivity.this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();


        if (isNetworkAvailable()) {

            pDialog.show();
            String urlString = "";
            Map<String, String> jsonParams = new HashMap<String, String>();
            urlString = "http://blueeeagle.in/demo/mobile/api/get_spot_lat_lon.php";
            jsonParams.put("spot", spot);

            Log.v("TAG", "Final Requsting URL is :" + urlString);

            CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST, urlString, jsonParams, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    // rl.setVisibility(View.GONE);
                    // Just flush the areas & Buildings to insert new one.
                    Log.e("jsonresponse", response.toString());

                    try {
                        JSONObject myJsonObject = response;
                        Boolean status = myJsonObject.getBoolean("status");

                        if (status) {
                            try {

                                JSONArray rootarray = myJsonObject.getJSONArray("result");

                                    JSONObject latlon = rootarray.getJSONObject(0);

                                        Log.e("lat",latlon.getString("lat"));
                                        Log.e("lon",latlon.getString("lon"));
                                        lat= latlon.getString("lat");
                                        lon= latlon.getString("lon");
                                 pDialog.dismiss();

                                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                        .findFragmentById(R.id.map);
                                mapFragment.getMapAsync(MapActivity.this);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                        {

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

                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(MapActivity.this, "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(MapActivity.this, "Some thing went wrong,can not reach the server", Toast.LENGTH_SHORT).show();


        }

    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) MapActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }




}
