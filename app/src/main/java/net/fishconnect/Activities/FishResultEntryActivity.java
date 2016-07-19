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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import net.fishconnect.Activities.Utilities.DateandTime;
import net.fishconnect.Models.IncidentModel;
import net.fishconnect.R;
import net.fishconnect.Utilities.Sharedprefstrings;
import net.fishconnect.Volley.AppController;
import net.fishconnect.Volley.CustomRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FishResultEntryActivity extends AppCompatActivity {
    public List<IncidentModel> spotlist = new ArrayList<IncidentModel>();
    ScrollView fish_entry;
    ProgressDialog pDialog;
    JSONObject myJsonObject;
    TextView julian_date_tv;
    String res_spot;
    String res_description;
    String res_lat;
    String res_long;
    RelativeLayout water_body_layout, basin_layout, ma_area_layout, mi_area_layout, spot_layout, specie_layout,
            rating_layout, direction_layout, distance_layout, action_layout;
    Spinner water_body_spnr, basin_spnr, ma_area_spnr, mi_area_spnr, spot_spnr, specie_spnr, rating_spnr, direction_spnr,
            distance_spnr, action_spnr;
    EditText depth_et, comment_et;
    Button submit_btn;
    String date;
    DateandTime dateandTime = new DateandTime();
    int marker=0;
    String[] waterbody;
    String[] basin;
    String[] majorarea;
    String[] minorarea;
    String[] spot;
    String[] specie;

    String[] rating = new String[]{"Select Rating", "Hot Biting", "Some Taken", "Present But Not Biting", "No Rating"};

    String[] direction = new String[]{"Select Direction", "North", "East", "West", "South"};

    String[] distance = new String[]{"Select Distance", "0.5 Mile", "1 Mile", "2 Mile", "5 Mile", "Over 5 Mile"};

    String[] action = new String[]{"Select Action", "Anchored", "Drifting", "Trolling", "Jigging"};

    String currentdate, selected_waterbody="",selected_basin="",selected_ma_area="",selected_mi_area="",selected_spot="",selected_specie="",selected_rating="",selected_direction="",selected_distance="",entered_depth="",selected_action="",entered_comment="";

    public String MyPREFERENCES = "MyPrefs";
    SharedPreferences sharedpreferences;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish_result_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // fish_entry=(ScrollView)findViewById(R.id.fish_result_entry);
        sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, MODE_WORLD_READABLE);
        julian_date_tv = (TextView) findViewById(R.id.julian_date);
        water_body_layout = (RelativeLayout) findViewById(R.id.water_body_spinner_layout);
        basin_layout = (RelativeLayout) findViewById(R.id.basin_spinner_layout);
        ma_area_layout = (RelativeLayout) findViewById(R.id.ma_area_spinner_layout);
        mi_area_layout = (RelativeLayout) findViewById(R.id.mi_area_spinner_layout);
        spot_layout = (RelativeLayout) findViewById(R.id.spot_spinner_layout);
        specie_layout = (RelativeLayout) findViewById(R.id.specie_spinner_layout);
        rating_layout = (RelativeLayout) findViewById(R.id.rating_spinner_layout);
        direction_layout = (RelativeLayout) findViewById(R.id.direction_spinner_layout);
        distance_layout = (RelativeLayout) findViewById(R.id.direction_spinner_layout);
        action_layout = (RelativeLayout) findViewById(R.id.action_spinner_layout);

        water_body_spnr = (Spinner) findViewById(R.id.spinner_water_body);
        basin_spnr = (Spinner) findViewById(R.id.spinner_basin);
        ma_area_spnr = (Spinner) findViewById(R.id.spinner_ma_area);
        mi_area_spnr = (Spinner) findViewById(R.id.spinner_mi_area);
        spot_spnr = (Spinner) findViewById(R.id.spinner_spot);
        specie_spnr = (Spinner) findViewById(R.id.spinner_specie);
        rating_spnr = (Spinner) findViewById(R.id.spinner_rating);
        direction_spnr = (Spinner) findViewById(R.id.spinner_direction);
        distance_spnr = (Spinner) findViewById(R.id.spinner_distance);
        action_spnr = (Spinner) findViewById(R.id.spinner_action);

        depth_et = (EditText) findViewById(R.id.depth);
        comment_et = (EditText) findViewById(R.id.comment);
        submit_btn = (Button) findViewById(R.id.submit_button);

        date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        julian_date_tv.setText(date);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");


        spot_apicall();

         new Handler().postDelayed(new Runnable() {
             @Override
             public void run() {
                 specie_apicall();

             }
         }, 3000);

        specie_apicall();


        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentdate=date;
                entered_depth=depth_et.getText().toString().trim();
                entered_comment=comment_et.getText().toString().trim();

                if(date.length()>1)
                {
                    if(selected_waterbody.length()>1)
                    {
                        if(selected_basin.length()>1)
                        {
                            if(selected_ma_area.length()>1)
                            {
                                if(selected_mi_area.length()>1)
                                {
                                    if(selected_spot.length()>0)
                                    {
                                        if(selected_specie.length()>1)
                                        {
                                            if(selected_rating.length()>1)
                                            {
                                                if(selected_direction.length()>1)
                                                {
                                                    if(selected_distance.length()>1)
                                                    {
                                                        if(entered_depth.length()>1)
                                                        {
                                                            if(selected_action.length()>1)
                                                            {
                                                                if(entered_comment.length()>1)
                                                                {

                                                                    insertrecord();

                                                                    //Toast.makeText(FishResultEntryActivity.this,"validation success",Toast.LENGTH_SHORT).show();


                                                                }else
                                                                    Toast.makeText(FishResultEntryActivity.this,"Please Enter the Comment",Toast.LENGTH_SHORT).show();
                                                            }else
                                                                Toast.makeText(FishResultEntryActivity.this,"Please Select the action",Toast.LENGTH_SHORT).show();
                                                        }else
                                                            Toast.makeText(FishResultEntryActivity.this,"Please Enter the depth",Toast.LENGTH_SHORT).show();
                                                    }else
                                                        Toast.makeText(FishResultEntryActivity.this,"Please Select the distance",Toast.LENGTH_SHORT).show();

                                                }else
                                                    Toast.makeText(FishResultEntryActivity.this,"Please Select the direction",Toast.LENGTH_SHORT).show();
                                            }else
                                                Toast.makeText(FishResultEntryActivity.this,"Please Select the rating",Toast.LENGTH_SHORT).show();
                                        }else
                                            Toast.makeText(FishResultEntryActivity.this,"Please Select the specie",Toast.LENGTH_SHORT).show();
                                    }else
                                        Toast.makeText(FishResultEntryActivity.this,"Please Select the spot",Toast.LENGTH_SHORT).show();
                                }else
                                    Toast.makeText(FishResultEntryActivity.this,"Please Select the minor area",Toast.LENGTH_SHORT).show();
                            }else
                                Toast.makeText(FishResultEntryActivity.this,"Please Select the major area",Toast.LENGTH_SHORT).show();
                        }else
                            Toast.makeText(FishResultEntryActivity.this,"Please Select the basin",Toast.LENGTH_SHORT).show();
                    }else
                        Toast.makeText(FishResultEntryActivity.this,"Please Select the water Body",Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(FishResultEntryActivity.this,"Please get the current date",Toast.LENGTH_SHORT).show();

            }
        });





//*******************Rating spinner ********************************//
        List<String> rating_list = new ArrayList<>(Arrays.asList(rating));
        ArrayAdapter<String> spinnerArrayAdapter_rating = new ArrayAdapter<String>(
                FishResultEntryActivity.this, R.layout.spinner_item_fish_result, rating_list) {

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
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerArrayAdapter_rating.setDropDownViewResource(R.layout.spinner_item_fish_result);
        rating_spnr.setAdapter(spinnerArrayAdapter_rating);

        rating_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (position > 0) {
                    selected_rating = (String) parent.getItemAtPosition(position);

                    //Toast.makeText(FishResultEntryActivity.this, "Selected : " + rating, Toast.LENGTH_SHORT).show();


                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//*******************Direction spinner ********************************//

        List<String> direction_list = new ArrayList<>(Arrays.asList(direction));
        ArrayAdapter<String> spinnerArrayAdapter_direction = new ArrayAdapter<String>(
                FishResultEntryActivity.this, R.layout.spinner_item_fish_result, direction_list) {

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {

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
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerArrayAdapter_direction.setDropDownViewResource(R.layout.spinner_item_fish_result);
        direction_spnr.setAdapter(spinnerArrayAdapter_direction);

        direction_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



                if (position > 0) {

                    selected_direction = (String) parent.getItemAtPosition(position);

                   // Toast.makeText(FishResultEntryActivity.this, "Selected : " + direction, Toast.LENGTH_SHORT).show();


                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//*******************Distance spinner ********************************//


        List<String> distance_list = new ArrayList<>(Arrays.asList(distance));
        ArrayAdapter<String> spinnerArrayAdapter_distance = new ArrayAdapter<String>(
                FishResultEntryActivity.this, R.layout.spinner_item_fish_result, distance_list) {

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
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerArrayAdapter_distance.setDropDownViewResource(R.layout.spinner_item_fish_result);
        distance_spnr.setAdapter(spinnerArrayAdapter_distance);
        distance_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



                if (position > 0) {
                        selected_distance = (String) parent.getItemAtPosition(position);

                   // Toast.makeText(FishResultEntryActivity.this, "Selected : " + distance, Toast.LENGTH_SHORT).show();


                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //*******************action spinner ********************************//
        List<String> action_list = new ArrayList<>(Arrays.asList(action));
        ArrayAdapter<String> spinnerArrayAdapter_action = new ArrayAdapter<String>(
                FishResultEntryActivity.this, R.layout.spinner_item_fish_result, action_list) {

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
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerArrayAdapter_action.setDropDownViewResource(R.layout.spinner_item_fish_result);
        action_spnr.setAdapter(spinnerArrayAdapter_action);

        action_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (position > 0) {
                   selected_action = (String) parent.getItemAtPosition(position);

                    //Toast.makeText(FishResultEntryActivity.this, "Selected : " + action, Toast.LENGTH_SHORT).show();


                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

    public void setupspeciespinner() {
        List<String> specie_list = new ArrayList<>(Arrays.asList(specie));
        ArrayAdapter<String> spinnerArrayAdapter_specie = new ArrayAdapter<String>(
                FishResultEntryActivity.this, R.layout.spinner_item_fish_result, specie_list) {

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
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerArrayAdapter_specie.setDropDownViewResource(R.layout.spinner_item_fish_result);
        specie_spnr.setAdapter(spinnerArrayAdapter_specie);
        specie_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0) {

               selected_specie = (String) parent.getItemAtPosition(position);

                    //Toast.makeText(FishResultEntryActivity.this, "Selected : " + rating, Toast.LENGTH_SHORT).show();


                }

            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

   public void setupwaterbody()
   {
       List<String> waterbody_list = new ArrayList<>(Arrays.asList(waterbody));
       ArrayAdapter<String> spinnerArrayAdapter_waterbody = new ArrayAdapter<String>(
               FishResultEntryActivity.this, R.layout.spinner_item_fish_result,  waterbody_list) {

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
                   tv.setTextColor(Color.BLACK);
               }
               return view;
           }
       };

       spinnerArrayAdapter_waterbody.setDropDownViewResource(R.layout.spinner_item_fish_result);
      water_body_spnr.setAdapter(spinnerArrayAdapter_waterbody);
       water_body_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

               selected_basin="";
               selected_ma_area="";
               selected_mi_area="";
               selected_spot="";
                basin_layout.setVisibility(View.GONE);
                ma_area_layout.setVisibility(View.GONE);
                mi_area_layout.setVisibility(View.GONE);
                spot_layout.setVisibility(View.GONE);

              // Toast.makeText(FishResultEntryActivity.this, "Selected : " + selected_waterbody, Toast.LENGTH_SHORT).show();

               if (position > 0) {
                   selected_waterbody = (String) parent.getItemAtPosition(position);
                   marker = 1;

                   if (isNetworkAvailable()) {
                       spot_apicall();
                   } else {
                      // Toast.makeText(FishResultEntryActivity.this, "Check your internet connection and try again", Toast.LENGTH_SHORT).show();

                   }
               }

           }


           @Override
           public void onNothingSelected(AdapterView<?> parent) {

           }
       });



   }



  public void  setupbasinspinner()
  {

      List<String> basin_list = new ArrayList<>(Arrays.asList(basin));
      ArrayAdapter<String> spinnerArrayAdapter_basin = new ArrayAdapter<String>(
              FishResultEntryActivity.this, R.layout.spinner_item_fish_result,  basin_list) {

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
                  tv.setTextColor(Color.BLACK);
              }
              return view;
          }
      };

      spinnerArrayAdapter_basin.setDropDownViewResource(R.layout.spinner_item_fish_result);
      basin_spnr.setAdapter(spinnerArrayAdapter_basin);
      basin_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

              selected_ma_area="";
              selected_mi_area="";
              selected_spot="";
              ma_area_layout.setVisibility(View.GONE);
              mi_area_layout.setVisibility(View.GONE);
              spot_layout.setVisibility(View.GONE);

             // Toast.makeText(FishResultEntryActivity.this, "Selected : " + selected_basin, Toast.LENGTH_SHORT).show();

              if (position > 0) {
                  selected_basin = (String) parent.getItemAtPosition(position);
                  marker = 2;

                  if (isNetworkAvailable()) {
                     spot_apicall();
                  } else {
                     // Toast.makeText(FishResultEntryActivity.this, "Check your internet connection and try again", Toast.LENGTH_SHORT).show();

                  }
              }

          }


          @Override
          public void onNothingSelected(AdapterView<?> parent) {

          }
      });



  }


  public void  setup_ma_area_spinner()
  {

      List<String> major_area_list = new ArrayList<>(Arrays.asList(majorarea));
      ArrayAdapter<String> spinnerArrayAdapter_major_area = new ArrayAdapter<String>(
              FishResultEntryActivity.this, R.layout.spinner_item_fish_result,  major_area_list) {

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
                  tv.setTextColor(Color.BLACK);
              }
              return view;
          }
      };

      spinnerArrayAdapter_major_area.setDropDownViewResource(R.layout.spinner_item_fish_result);
      ma_area_spnr.setAdapter(spinnerArrayAdapter_major_area);
      ma_area_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


              selected_mi_area = "";
              selected_spot = "";
              mi_area_layout.setVisibility(View.GONE);
              spot_layout.setVisibility(View.GONE);

              if (position > 0) {
                  selected_ma_area = (String) parent.getItemAtPosition(position);
                  //Toast.makeText(FishResultEntryActivity.this, "Selected : " + selected_ma_area, Toast.LENGTH_SHORT).show();

                  marker = 3;

                  if (isNetworkAvailable()) {
                      spot_apicall();
                  } else {
                     // Toast.makeText(FishResultEntryActivity.this, "Check your internet connection and try again", Toast.LENGTH_SHORT).show();

                  }
              }

          }


          @Override
          public void onNothingSelected(AdapterView<?> parent) {

          }
      });


  }


 public void   setup_mi_area_spinner()
 {

     List<String> minor_area_list = new ArrayList<>(Arrays.asList(minorarea));
     ArrayAdapter<String> spinnerArrayAdapter_minor_area = new ArrayAdapter<String>(
             FishResultEntryActivity.this, R.layout.spinner_item_fish_result,  minor_area_list) {

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
                 tv.setTextColor(Color.BLACK);
             }
             return view;
         }
     };

     spinnerArrayAdapter_minor_area.setDropDownViewResource(R.layout.spinner_item_fish_result);
     mi_area_spnr.setAdapter(spinnerArrayAdapter_minor_area);
     mi_area_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

             selected_spot = "";
             spot_layout.setVisibility(View.GONE);

             if (position > 0) {

                 selected_mi_area = (String) parent.getItemAtPosition(position);
                // Toast.makeText(FishResultEntryActivity.this, "Selected : " + selected_mi_area, Toast.LENGTH_SHORT).show();

                 marker = 4;

                 if (isNetworkAvailable()) {
                     spot_apicall();
                 } else {
                     //Toast.makeText(FishResultEntryActivity.this, "Check your internet connection and try again", Toast.LENGTH_SHORT).show();

                 }
             }

         }


         @Override
         public void onNothingSelected(AdapterView<?> parent) {

         }
     });

 }




public void  setup_spot_spinner(){


    final List<String> spot_list = new ArrayList<>(Arrays.asList(spot));
    ArrayAdapter<String> spinnerArrayAdapter_spot = new ArrayAdapter<String>(
            FishResultEntryActivity.this, R.layout.spinner_item_fish_result,  spot_list) {

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
                tv.setTextColor(Color.BLACK);
            }
            return view;
        }
    };

    spinnerArrayAdapter_spot.setDropDownViewResource(R.layout.spinner_item_fish_result);
    spot_spnr.setAdapter(spinnerArrayAdapter_spot);
    spot_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


            if (position > 0) {
                selected_spot = (String) parent.getItemAtPosition(position);
                //IncidentModel im = spotlist.get(position);
               //Toast.makeText(FishResultEntryActivity.this, "Selected : " + selected_mi_area+","+ im.getDescription()+","+im.getLat(), Toast.LENGTH_SHORT).show();



            }

        }


        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    });



    }

    public void specie_apicall() {

        if (isNetworkAvailable()) {


            String urlString = "";
            Map<String, String> jsonParams = new HashMap<String, String>();

                urlString = "http://blueeeagle.in/demo/mobile/api/get_specie.php";

                Log.v("TAG", "Final Requsting URL is :" + urlString);

            CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST, urlString, jsonParams, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.e("jsonresponse", response.toString());
                   // pDialog.dismiss();

                    try {
                        myJsonObject = response;
                        Boolean status = myJsonObject.getBoolean("status");

                        if (status) {
                            try {

                                JSONArray rootarray = myJsonObject.getJSONArray("result");

                                    specie = new String[rootarray.length() + 1];
                                    specie[0] = "Select Specie";
                                    for (int i = 1; i <= rootarray.length(); i++) {

                                        specie[i] = rootarray.getString(i - 1);
                                        Log.e(i + "",  specie[i]);

                                    }
                                setupspeciespinner();

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
                   // pDialog.dismiss();


                    Log.e("error", error.toString());
                    if (error instanceof TimeoutError) {

                        specie_apicall();

                    } else if (error instanceof NoConnectionError) {
                       // Toast.makeText(FishResultEntryActivity.this, "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                        //tverrortext.setText("Oops! No Network Connection available");
                    }
                }
            });

            jsonObjReq.setShouldCache(false);
            AppController.getInstance().addToRequestQueue(jsonObjReq);
            //AppController.getInstance(getActivity()).addToRequestQueue(jsonObjReq);
            //Volley.newRequestQueue(getActivity()).add(jsonObjReq);
        } else {

           // pDialog.dismiss();
            //Toast.makeText(FishResultEntryActivity.this, "Some thing went wrong,can not reach the server", Toast.LENGTH_SHORT).show();


        }

    }

    public void spot_apicall() {
        pDialog.show();

        if (isNetworkAvailable()) {

            String urlString = "";
            Map<String, String> jsonParams = new HashMap<String, String>();


            if (marker == 0) {
                Log.e("marker ", marker + "");
                urlString = "http://blueeeagle.in/demo/mobile/api/get_waterbody.php";
            }
            else if (marker == 1) {
                urlString = "http://blueeeagle.in/demo/mobile/api/get_basin.php";
                jsonParams.put("w_body", selected_waterbody);

            } else if (marker == 2) {
                urlString = "http://blueeeagle.in/demo/mobile/api/get_major_area.php";
                Log.e("w_body",selected_waterbody);
                Log.e("basin",selected_basin);
                jsonParams.put("w_body", selected_waterbody);
                jsonParams.put("basin", selected_basin);

            }else if(marker==3)
            {
                urlString = "http://blueeeagle.in/demo/mobile/api/get_minor_area.php";
                Log.e("w_body",selected_waterbody);
                Log.e("basin",selected_basin);
                Log.e("ma_area",selected_ma_area);

                jsonParams.put("w_body", selected_waterbody);
                jsonParams.put("basin", selected_basin);
                jsonParams.put("major_area", selected_ma_area);
            }else if(marker==4)
            {
                urlString = "http://blueeeagle.in/demo/mobile/api/get_spot.php";
                Log.e("w_body",selected_waterbody);
                Log.e("basin",selected_basin);
                Log.e("ma_area",selected_ma_area);
                Log.e("mi_area",selected_mi_area);

                jsonParams.put("w_body", selected_waterbody);
                jsonParams.put("basin", selected_basin);
                jsonParams.put("major_area", selected_ma_area);
                jsonParams.put("minor_area", selected_mi_area);
            }

            Log.v("TAG", "Final Requsting URL is :" + urlString);

            CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST, urlString, jsonParams, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
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
                                    waterbody[0] = "Select Water Body";
                                    for (int i = 1; i <= rootarray.length(); i++) {

                                        waterbody[i] = rootarray.getString(i - 1);
                                        Log.e(i + "", waterbody[i]);

                                    }
                                    setupwaterbody();

                                } else if (marker == 1) {
                                    basin = new String[rootarray.length() + 1];
                                    basin[0] = "Select Basin";
                                    for (int i = 1; i <= rootarray.length(); i++) {

                                        basin[i] = rootarray.getString(i - 1);
                                        Log.e(i + "", basin[i]);

                                    }
                                    FishResultEntryActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            basin_layout.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    setupbasinspinner();
                                } else if (marker == 2) {
                                    majorarea = new String[rootarray.length() + 1];
                                    majorarea[0] = "Select Major Area";
                                    for (int i = 1; i <= rootarray.length(); i++) {

                                        majorarea[i] = rootarray.getString(i - 1);
                                        Log.e(i + "", majorarea[i]);
                                    }
                                    FishResultEntryActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            ma_area_layout.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    setup_ma_area_spinner();
                                }else if (marker==3)
                                {
                                    minorarea = new String[rootarray.length() + 1];
                                    minorarea[0] = "Select Minor Area";
                                    for (int i = 1; i <= rootarray.length(); i++) {

                                        minorarea[i] = rootarray.getString(i - 1);
                                        Log.e(i + "", minorarea[i]);
                                    }
                                    FishResultEntryActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            mi_area_layout.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    setup_mi_area_spinner();

                                }else if (marker==4)
                                {

                                   // {"result":[{"spot":"1","description":"Maumee River","latitude":"41.69833333","longitude":"-83.474722"}],"status":true}

                                    JSONObject spotobject = rootarray.getJSONObject(0);
                                    IncidentModel im = new IncidentModel();
                                    res_spot= spotobject.getString("spot");
                                    res_description=spotobject.getString("description");
                                    res_lat=spotobject.getString("latitude");
                                    res_long=spotobject.getString("longitude");
                                    Log.e("spot", res_spot);
                                    Log.e("des", res_description);
                                    Log.e("lat", res_lat);
                                    Log.e("lon", res_long);

                                    im.setSpot(res_spot);
                                    im.setDescription(res_description);
                                    im.setLat(res_lat);
                                    im.setLon(res_long);
                                    spotlist.add(im);


                                    spot = new String[2];
                                    spot[0] = "Select Spot";
                                    for (int i = 1; i <= 1; i++) {

                                        spot[i] = res_spot;
                                        Log.e(i + "", spot[i]);
                                    }

                                    //spot = new String[2];
                                    //spot[0] = "Select Spot";
                                    //spot[1] = res_spot;


                                    FishResultEntryActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            spot_layout.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    setup_spot_spinner();

                                }

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

                        spot_apicall();

                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(FishResultEntryActivity.this, "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(FishResultEntryActivity.this, "Some thing went wrong,can not reach the server", Toast.LENGTH_SHORT).show();


        }

    }


    public void insertrecord()
    {
        pDialog.show();
        if (isNetworkAvailable()) {

            String urlString = "";
            Map<String, String> jsonParams = new HashMap<String, String>();
            jsonParams.put("mid", sharedpreferences.getString(Sharedprefstrings.member_reg_id,""));
            jsonParams.put("date", currentdate);
            jsonParams.put("specie",  selected_specie);
            jsonParams.put("w_body", selected_waterbody);
            jsonParams.put("basin",  selected_basin);
            jsonParams.put("ma_area", selected_ma_area);
            jsonParams.put("mi_area", selected_mi_area);
            jsonParams.put("spot", selected_spot);
            jsonParams.put("descriptor", res_description);
            jsonParams.put("latitude", res_lat);
            jsonParams.put("longitude", res_long);
            jsonParams.put("rating", selected_rating);
            jsonParams.put("direction", selected_direction);
            jsonParams.put("distance", selected_distance);
            jsonParams.put("depth", entered_depth);
            jsonParams.put("action", selected_action);
            jsonParams.put("comment", entered_comment);

            urlString = "http://blueeeagle.in/demo/mobile/api/submit_fishing_report.php";

            Log.v("TAG", "Final Requsting URL is :" + urlString);

            CustomRequest jsonObjReq = new CustomRequest(Request.Method.POST, urlString, jsonParams, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.e("jsonresponse", response.toString());
                    // pDialog.dismiss();

                    try {
                        myJsonObject = response;
                        Boolean status = myJsonObject.getBoolean("status");

                        if (status) {

                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putBoolean(Sharedprefstrings.inserted,true);
                            editor.apply();
                            Intent i = new Intent(FishResultEntryActivity.this, FishResultEntryActivity.class);
                            startActivity(i);
                            finish();


                        } else {


                            pDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(FishResultEntryActivity.this);
                            builder.setCancelable(false);
                            builder.setTitle("Failed to Insert");
                            builder.setMessage("Data can not be inserted, server may be busy right now.");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                    dialog.dismiss();

                                }
                            });

                            AlertDialog dialog = builder.create();

                            dialog.show();


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

                        insertrecord();

                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(FishResultEntryActivity.this, "Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
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
            //Toast.makeText(FishResultEntryActivity.this, "Some thing went wrong,can not reach the server", Toast.LENGTH_SHORT).show();


        }

    }




    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) FishResultEntryActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }


    @Override
    protected void onResume() {
        super.onResume();


      if(sharedpreferences.getBoolean(Sharedprefstrings.inserted,false))
      {
          SharedPreferences.Editor editor = sharedpreferences.edit();
          editor.putBoolean(Sharedprefstrings.inserted,false);
          editor.apply();

          AlertDialog.Builder builder = new AlertDialog.Builder(FishResultEntryActivity.this);
          builder.setCancelable(false);
          builder.setTitle("Incident Insertion");
                  builder.setMessage("Recored Inserted Sucessfully");
          builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {


                  dialog.dismiss();

              }
          });

          AlertDialog dialog = builder.create();

          dialog.show();


      }








    }











}
