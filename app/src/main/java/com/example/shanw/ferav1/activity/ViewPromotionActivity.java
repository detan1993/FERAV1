package com.example.shanw.ferav1.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shanw.ferav1.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewPromotionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_promotion);
        loadPromo();
        TextView startdatetime = findViewById(R.id.startdatetime);
        TextView enddatetime = findViewById(R.id.enddatetime);
        TextView desc = findViewById(R.id.description);
    }
    public void loadPromo() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {

            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    System.err.println("**** Calling rest web service");
                    SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    String restid = sharedPref.getString("staff_restaurantId","");
                    URL url = new URL(getString(R.string.VM_address) + "FoodEmblemV1-war/Resources/Promotion/getPromotionByRestaurantId/" + restid);
                    // http://localhost:3446/FoodEmblemV1-war/Resources/Sensor/getFridgesByRestaurantId/1
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();

                    String line = null;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    return stringBuilder.toString();

                } catch (Exception ex) {

                    System.out.println("error calling API");
                    //Toast.makeText(getApplicationContext(), "Error calling REST web service", Toast.LENGTH_LONG).show();

                    ex.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String jsonString) {
                try {
                    TextView tvstartdatetime = findViewById(R.id.startdatetime);
                    TextView tvenddatetime = findViewById(R.id.enddatetime);
                    TextView tvdesc = findViewById(R.id.description);
                    TextView promodisplay = findViewById(R.id.promoamt);
                    if (!jsonString.equals("{\"promotions\":[{\"promotionPercentage\":0.0,\"restid\":0}]}")) {
                        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        JSONObject jsonObject = new JSONObject(jsonString);
                        JSONArray promo = jsonObject.getJSONArray("promotions");
                        JSONObject p = promo.getJSONObject(0);
                        String description = p.getString("description");
                        String startdatetime = p.getString("startDateTime");
                        String enddatetime = p.getString("endDateTime");
                        String promoamt = String.valueOf(p.getDouble("promotionPercentage"));
                        int promoid = p.getInt("id");
                        sharedPref.edit().putInt("promoid",promoid).apply();
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                        Date startjson = (df).parse(startdatetime);
                        Date endjson = (df).parse(enddatetime);
                        SimpleDateFormat myDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        SimpleDateFormat myTimeFormat = new SimpleDateFormat("HH:mm");
                        String startdatejson = myDateFormat.format(startjson);
                        String starttimejson = myTimeFormat.format(startjson);
                        String enddatejson = myDateFormat.format(endjson);
                        String endtimejson = myTimeFormat.format(endjson);
                        tvstartdatetime.setText(startdatejson + " " + starttimejson);
                        tvenddatetime.setText(enddatejson + " " + endtimejson);
                        tvdesc.setText(description);
                        promodisplay.setText(promoamt + "%");
                        ImageButton addbtn = findViewById(R.id.addBtn);
                            addbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(ViewPromotionActivity.this,"Your restaurant already has a promotion!" , Toast.LENGTH_LONG).show();
                                }
                            });
                        ImageButton removebtn = findViewById(R.id.deleteBtn);
                            //Delete ws here
                            removebtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deletePromo();
                                }
                            });
                    }
                    else {
                        tvstartdatetime.setText("No Current Promotions");
                        tvenddatetime.setText("No Current Promotions");
                        tvdesc.setText("No Current Promotions");
                        promodisplay.setText("No Current Promotions");
                        ImageButton addbtn = findViewById(R.id.addBtn);
                        ImageButton removebtn = findViewById(R.id.deleteBtn);
                        addbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(ViewPromotionActivity.this, PromotionActivity.class);
                                startActivity(i);
                            }
                        });
                        removebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(ViewPromotionActivity.this, "Your restaurant has no promotion to delete!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }.execute();
    }

    public void deletePromo() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {

            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    System.err.println("**** Calling rest web service");
                    SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    int promoid = sharedPref.getInt("promoid", 0);
                    URL url = new URL(getString(R.string.VM_address) + "FoodEmblemV1-war/Resources/Promotion/deletePromotion/" + promoid);
                    // http://localhost:3446/FoodEmblemV1-war/Resources/Sensor/getFridgesByRestaurantId/1
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();

                    String line = null;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    return stringBuilder.toString();

                } catch (Exception ex) {

                    System.out.println("error calling API");
                    //Toast.makeText(getApplicationContext(), "Error calling REST web service", Toast.LENGTH_LONG).show();

                    ex.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String jsonString) {

                try {
                    finish();
                    startActivity(getIntent());
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }.execute();
    }
}
