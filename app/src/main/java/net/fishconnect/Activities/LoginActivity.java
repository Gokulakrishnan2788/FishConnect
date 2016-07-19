package net.fishconnect.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.android.gms.maps.SupportMapFragment;

import net.fishconnect.R;
import net.fishconnect.Utilities.Sharedprefstrings;
import net.fishconnect.Volley.AppController;
import net.fishconnect.Volley.CustomRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity  {
    Button login_button,forgetpwd;
    private EditText exist_inputEmail, exist_inputPassword;
String login_email,login_pwd;
    ProgressDialog pDialog;
    public String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;
    private TextInputLayout  exist_inputLayoutEmail, exist_inputLayoutPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);

        sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, MODE_WORLD_READABLE);
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");

        exist_inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        exist_inputLayoutPassword = (TextInputLayout)  findViewById(R.id.input_layout_password);

        //inputName = (EditText)  mview.findViewById(R.id.input_name);
        exist_inputEmail = (EditText) findViewById(R.id.input_email);
        exist_inputPassword = (EditText)  findViewById(R.id.input_password);


        //inputName.addTextChangedListener(new MyTextWatcher(inputName));
        exist_inputEmail.addTextChangedListener(new MyTextWatcher(exist_inputEmail));
        exist_inputPassword.addTextChangedListener(new MyTextWatcher(exist_inputPassword));
        login_button=(Button) findViewById((R.id.login_btn));

        forgetpwd=(Button) findViewById((R.id.forgetpwd));
        forgetpwd.setPaintFlags(  forgetpwd.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {

                    submitForm();

                } else {
                    Toast.makeText(LoginActivity.this, "Some thing went wrong,can not reach the server", Toast.LENGTH_SHORT).show();
                }
            }
        });

        forgetpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                 startActivity(i);

            }
        });







    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) LoginActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }
    private void submitForm() {

        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }
        login_email = exist_inputEmail.getText().toString();
        login_pwd = exist_inputPassword.getText().toString();
        Log.e("email",login_email);
        Log.e("pwd", login_pwd);

        //Intent i = new Intent(LoginActivity.this, HomeActivity.class);
       // startActivity(i);
       // Toast.makeText(LoginActivity.this,"validation sucess",Toast.LENGTH_SHORT).show();

        pDialog.show();
        apicall();



    }


    private boolean validateEmail() {
        String email = exist_inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            exist_inputLayoutEmail.setError(getString(R.string.err_msg_email));
            exist_requestFocus(exist_inputEmail);
            return false;
        } else {
            exist_inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (exist_inputPassword.getText().toString().trim().isEmpty()) {
            exist_inputLayoutPassword.setError(getString(R.string.err_msg_password));
            exist_requestFocus(exist_inputPassword);
            return false;
        } else {
            exist_inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void exist_requestFocus(View view) {
        if (view.requestFocus()) {
            LoginActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                /*case R.id.input_name:
                validateName();
                break;*/
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }



    }


   public void apicall()
   {

       if (isNetworkAvailable()) {

           pDialog.show();
           String urlString = "";
           Map<String, String> jsonParams = new HashMap<String, String>();
           urlString = "http://blueeeagle.in/demo/mobile/api/login.php";
         //  urlString = "http://192.168.1.4:80/fishconnect/login.php";
           jsonParams.put("email", login_email);
           jsonParams.put("pwd", login_pwd);

           Log.v("TAG", "Final Requsting URL is :" + urlString);

           CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST, urlString, jsonParams, new Response.Listener<JSONObject>() {

               @Override
               public void onResponse(JSONObject response) {

                   Log.e("jsonresponse", response.toString());
                    pDialog.dismiss();
                   try {
                       JSONObject myJsonObject = response;
                       Boolean status = myJsonObject.getBoolean("status");

                       if (status) {
                           try {

                               JSONObject rootobject = myJsonObject.getJSONObject("result");
                               String m_id= rootobject.getString("m_id");
                               String m_reg_id=rootobject.getString("m_reg_id");

                               SharedPreferences.Editor editor = sharedpreferences.edit();
                               editor.putString(Sharedprefstrings.member_id,m_id);
                               editor.putString(Sharedprefstrings.member_reg_id, m_reg_id);
                               editor.apply();
                               Log.e("m_id",m_id);
                               Log.e("m_reg_id",m_reg_id);
                               //Toast.makeText(LoginActivity.this,"login sucess",Toast.LENGTH_LONG).show();
                               Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                               startActivity(i);
                               finish();


                           } catch (JSONException e) {
                               e.printStackTrace();
                           }
                       }
                       else
                       {

                            String errormsg= myJsonObject.getString("result");
                            Toast.makeText(LoginActivity.this,errormsg,Toast.LENGTH_LONG).show();

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
                       Toast.makeText(LoginActivity.this, "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
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
           Toast.makeText(LoginActivity.this, "Some thing went wrong,can not reach the server", Toast.LENGTH_SHORT).show();


       }



   }













}