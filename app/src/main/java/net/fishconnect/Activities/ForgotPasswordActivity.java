package net.fishconnect.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import net.fishconnect.R;
import net.fishconnect.Volley.AppController;
import net.fishconnect.Volley.CustomRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button submitemail;
    EditText email;
    private ProgressDialog pDialog;
    String registeredemail;
    JSONObject myJsonObject;
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");

        email = (EditText) findViewById(R.id.regemail);
        submitemail = (Button) findViewById(R.id.submit_email);
        submitemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registeredemail = email.getText().toString().trim();

                if (TextUtils.isEmpty(registeredemail)|| !(android.util.Patterns.EMAIL_ADDRESS.matcher(registeredemail).matches()) )
                {
                    Toast.makeText(ForgotPasswordActivity.this, "Enter Valid Email Id", Toast.LENGTH_LONG).show();
                } else
                {
                  //  Toast.makeText(ForgotPasswordActivity.this, "Server Busy please try again later", Toast.LENGTH_LONG).show();
                    pDialog.show();
                    pDialog.setCancelable(false);
                    apicall();
                }
            }
        });
    }

    void apicall() {
        if (isNetworkAvailable())
        {
            String urlString = "http://blueeeagle.in/demo/mobile/api/forgot_pwd.php";
            Log.v("TAG", "Final Requsting URL is :" + urlString);
            // Params
            Map<String, String> jsonParams = new HashMap<String, String>();
            jsonParams.put("email", registeredemail);

            CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST, urlString, jsonParams, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    // Just flush the areas & Buildings to insert new one.
                    Log.e("jsonresponse", response.toString());
                    pDialog.dismiss();

                    try {
                        myJsonObject = response;
                        try {
                            String result = myJsonObject.getString("result");
                            AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                            builder.setCancelable(false);
                            builder.setTitle("Forgot Password?");
                            builder.setMessage(result);

                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    dialog.dismiss();

                                }
                            });

                            AlertDialog dialog = builder.create();
                            // calling builder.create after adding buttons
                            dialog.show();

                        } catch (JSONException e) {
                            e.printStackTrace();
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
                        Toast.makeText(ForgotPasswordActivity.this, "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            AppController.getInstance().addToRequestQueue(jsonObjReq);
            //AppController.getInstance(getActivity()).addToRequestQueue(jsonObjReq);
            //Volley.newRequestQueue(getActivity()).add(jsonObjReq);
        }else
        {
            pDialog.dismiss();
            Toast.makeText(ForgotPasswordActivity.this, "Check your internet Connection and try again", Toast.LENGTH_LONG).show();
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
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }


}
