package com.example.shanw.ferav1.activity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import com.example.shanw.ferav1.R;
import com.example.shanw.ferav1.helper.InputFilterMinMax;

import org.json.JSONObject;

public class PromotionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion);

        final EditText promo = findViewById(R.id.editPromo);
        promo.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "99")});
        final EditText desc = findViewById(R.id.editDesc);
        final EditText startdate = findViewById(R.id.editStartDate);
        final EditText starttime = findViewById(R.id.editStartTime);
        final EditText enddate = findViewById(R.id.editEndDate);
        final EditText endtime = findViewById(R.id.editEndTime);

        startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int yy = c.get(Calendar.YEAR);
                int mm = c.get(Calendar.MONTH);
                int dd = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(PromotionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        EditText startdate = findViewById(R.id.editStartDate);
                        String day = "";
                        String month ="";
                        if (dayOfMonth < 10){
                            day = "0" + String.valueOf(dayOfMonth);
                        }
                        else {
                            day = String.valueOf(dayOfMonth);
                        }
                        if (monthOfYear < 10){
                            month = "0" + String.valueOf(monthOfYear + 1);
                        }
                        else {
                            month = String.valueOf(monthOfYear + 1);
                        }
                        String date = String.valueOf(year) +"-"+month
                                +"-"+day;
                        startdate.setText(date);
                    }
                }, yy, mm, dd);
                datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePicker.show();
            }
        });

        enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int yy = c.get(Calendar.YEAR);
                int mm = c.get(Calendar.MONTH);
                int dd = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(PromotionActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        EditText enddate = findViewById(R.id.editEndDate);
                        String day = "";
                        String month ="";
                        if (dayOfMonth < 10){
                             day = "0" + String.valueOf(dayOfMonth);
                        }
                        else {
                            day = String.valueOf(dayOfMonth);
                        }
                        if (monthOfYear < 10){
                            month = "0" + String.valueOf(monthOfYear + 1);
                        }
                        else {
                            month = String.valueOf(monthOfYear + 1);
                        }
                        String date = String.valueOf(year) +"-"+month
                                +"-"+day;
                        enddate.setText(date);
                    }
                }, yy, mm, dd);
                datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePicker.show();
            }
        });

        starttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(PromotionActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        EditText starttime = findViewById(R.id.editStartTime);
                        String hour = "";
                        String min ="";
                        if (hourOfDay < 10){
                            hour = "0"+String.valueOf(hourOfDay);
                        }
                        else {
                            hour = String.valueOf(hourOfDay);
                        }
                        if (minute < 10){
                            min = "0" + String.valueOf(minute);
                        }
                        else {
                            min = String.valueOf(minute);
                        }
                        String time = hour + ":" + min;
                        starttime.setText(time);
                    }
                },hour,minute,true);
                timePickerDialog.show();
            }
        });

       endtime.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final Calendar c = Calendar.getInstance();
               int hour = c.get(Calendar.HOUR_OF_DAY);
               int minute = c.get(Calendar.MINUTE);
               TimePickerDialog timePickerDialog = new TimePickerDialog(PromotionActivity.this, new TimePickerDialog.OnTimeSetListener() {
                   @Override
                   public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                       EditText endtime = findViewById(R.id.editEndTime);
                       String hour = "";
                       String min ="";
                       if (hourOfDay < 10){
                           hour = "0"+String.valueOf(hourOfDay);
                       }
                       else {
                           hour = String.valueOf(hourOfDay);
                       }
                       if (minute < 10){
                           min = "0" + String.valueOf(minute);
                       }
                       else {
                           min = String.valueOf(minute);
                       }
                       String time = hour + ":" + min;
                       endtime.setText(time);
                   }
               },hour,minute,true);
               timePickerDialog.show();
           }
       });
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//
        Button createBtn = (Button)findViewById(R.id.createBtn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ws to create promo
                if (TextUtils.isEmpty(promo.getText())||TextUtils.isEmpty(desc.getText())||TextUtils.isEmpty(startdate.getText())||
                        TextUtils.isEmpty(starttime.getText())||TextUtils.isEmpty(enddate.getText())||
                        TextUtils.isEmpty(endtime.getText())){
                    Toast.makeText(PromotionActivity.this,"Please fill in all the details properly",Toast.LENGTH_SHORT).show();
//                    if (!TextUtils.isEmpty(promo.getText())) {
//                        if (Double.parseDouble(promo.getText().toString()) > 100.00) {
//                            Toast.makeText(PromotionActivity.this, "Promo % must be less than 100!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
                }
                else {
                    createPromo();
                }
            }
        });
    }

    public void goBackPromo(View view){
        startActivity(new Intent(PromotionActivity.this, ViewPromotionActivity.class));
    }

    public void createPromo(){
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {

            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    String data="";
                    SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    String restid = sharedPref.getString("staff_restaurantId","");
                     EditText promo = findViewById(R.id.editPromo);
                     EditText desc = findViewById(R.id.editDesc);
                     EditText startdate = findViewById(R.id.editStartDate);
                     EditText starttime = findViewById(R.id.editStartTime);
                     EditText enddate = findViewById(R.id.editEndDate);
                     EditText endtime = findViewById(R.id.editEndTime);
                     String startdatetime = startdate.getText() + "T" + starttime.getText()+":00";
                     String enddatetime = enddate.getText() + "T" + endtime.getText()+":00";
//                     SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
//                     Date start = sdf.parse(startdatetime);

//                     Date end = sdf.parse(enddatetime);
                     System.err.println("**** Calling rest web service");
                     URL url = new URL(getString(R.string.VM_address) + "FoodEmblemV1-war/Resources/Promotion/createRestaurantPromotion/" + restid);
                    // http://localhost:3446/FoodEmblemV1-war/Resources/Sensor/getFridgesByRestaurantId/1
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("PUT");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Accept", "application/json");
                    JSONObject promotion = new JSONObject();
                    promotion.put("promotionPercentage",Double.parseDouble(promo.getText().toString()));
                    promotion.put("startDateTime",startdatetime);
                    promotion.put("endDateTime",enddatetime);
                    promotion.put("description",desc.getText());
                    DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                    wr.writeBytes("{\n"+"\"promotion\":" +promotion.toString()+"}");
                    wr.flush();
                    wr.close();
                    InputStream in = httpURLConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(in);

                    int inputStreamData = inputStreamReader.read();
                    while (inputStreamData != -1) {
                        char current = (char) inputStreamData;
                        inputStreamData = inputStreamReader.read();
                        data += current;
                    }
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
                    Intent i = new Intent(PromotionActivity.this, ViewPromotionActivity.class);
                    startActivity(i);
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

}
