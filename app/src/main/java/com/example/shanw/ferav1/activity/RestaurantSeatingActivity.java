package com.example.shanw.ferav1.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.shanw.ferav1.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RestaurantSeatingActivity extends AppCompatActivity {

    Handler mHandler;
    private DoAsyncTaskRetrieveActive doAsyncTaskRetrieveActive;
    private DoAsyncTaskRetrieveAllSeating doAsyncTaskRetrieveAllSeating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_seating);

        this.mHandler = new Handler();
        m_Runnable.run();
    }

    private final Runnable m_Runnable = new Runnable()
    {
        public void run()
        {
            retrieveSeating();
            RestaurantSeatingActivity.this.mHandler.postDelayed(m_Runnable,10000);
        }

    };

    //I override the on BackPress button to stop the timer before navigating back to main page
    @Override
    public void onBackPressed(){
        this.mHandler.removeCallbacks(m_Runnable); // removing the timer.
        Intent intent = new Intent(this, RestaurantStaffActivity.class);
        startActivity(intent);

    }


    /*
    * This method stop the timer and background services that i wrote to query the web services every 10 seconds once the user click on home button
    * */
    @Override
    protected void onPause(){   //when user click on home button at this page

        System.out.println("*********************** HOME BUTTON IS CLICKED");
        this.mHandler.removeCallbacks(m_Runnable);
        super.onPause();

    }

    /*
     * This method start the timer and background services to query the web services again once the user back to the page.
     * pre-condition : User pressed home button when after visiting fridge.xml
      * */
    @Override
    protected void onResume(){   //when the app user open the app again.

        System.out.println("*********************** Back to the APP. RESUME CALLING OF WEB SERVICE");
        RestaurantSeatingActivity.this.mHandler.postDelayed(m_Runnable, 10000);
        super.onResume();

    }

    protected class DoAsyncTaskRetrieveActive extends AsyncTask<String, Integer, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                String restaurantId = sharedPref.getString("staff_restaurantId",null);; // get restaurant Id from local file

                System.err.println("********** Calling get Seating Rest web service");
                URL url = new URL(getString(R.string.VM_address) + "FoodEmblemV1-war/Resources/Restaurant/retrieveActiveSeating/" + restaurantId + "");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();

                String line = null;
                while ((line = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(line);
                }

                return stringBuilder.toString(); //complete json string
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }

            return "Async Task Completed";
        }

        @Override
        protected void onPostExecute(String jsonString)
        {
            try{
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("restaurantSeatings");
                TextView tv = findViewById(R.id.tv_activeSeating);
                tv.setText(jsonArray.length());
            }

            catch(Exception ex){
                ex.printStackTrace();
            }
        }

    }

    protected class DoAsyncTaskRetrieveAllSeating extends AsyncTask<String, Integer, String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                String restaurantId = sharedPref.getString("staff_restaurantId",null);; // get restaurant Id from local file

                System.err.println("********** Calling get Seating Rest web service");
                URL url = new URL(getString(R.string.VM_address) + "FoodEmblemV1-war/Resources/Restaurant/retrieveCountAllSeating/" + restaurantId + "");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();

                String line = null;
                while ((line = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(line);
                }

                return stringBuilder.toString(); //complete json string
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }

            return "Async Task Completed";
        }

        @Override
        protected void onPostExecute(String jsonString)
        {
            try{
                JSONObject jsonObject = new JSONObject(jsonString);
                String result = jsonObject.getString("rsCount");
                TextView tv = findViewById(R.id.tv_allSeating);
                tv.setText(result);
            }

            catch(Exception ex){
                ex.printStackTrace();
            }
        }

    }

    public void retrieveSeating(){

        System.err.println("Retrieve Fridge Method");
        doAsyncTaskRetrieveActive = new DoAsyncTaskRetrieveActive();
        doAsyncTaskRetrieveActive.execute("Current progress");

        doAsyncTaskRetrieveAllSeating = new DoAsyncTaskRetrieveAllSeating();
        doAsyncTaskRetrieveAllSeating.execute("Current progress");
    }

    public void goHome(View view) {
        startActivity(new Intent(RestaurantSeatingActivity.this, RestaurantStaffActivity.class));
    }
}
