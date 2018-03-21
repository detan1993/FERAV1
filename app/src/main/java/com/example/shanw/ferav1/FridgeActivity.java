package com.example.shanw.ferav1;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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

public class FridgeActivity extends AppCompatActivity {

    Handler mHandler;

    private DoAsyncTaskButtonAsyncTask doAsyncTaskButtonAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fridge);

        this.mHandler = new Handler();
        System.out.println("Calling Runnable object");
        this.mHandler.postDelayed(m_Runnable,5000);
        m_Runnable.run();

       // retrieveFridge();


    }

    private final Runnable m_Runnable = new Runnable()
    {
        public void run()
        {
            retrieveFridge();
            System.out.println("Refresh Date :" + new Date());
            //refresh.this.mHandler.postDelayed(m_Runnable, 5000);
            FridgeActivity.this.mHandler.postDelayed(m_Runnable,10000);
        }

    };//


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
        FridgeActivity.this.mHandler.postDelayed(m_Runnable, 10000);
        super.onResume();

    }

    protected class DoAsyncTaskButtonAsyncTask extends AsyncTask<String, Integer, String>
    {
        String label;

        @Override
        protected void onPreExecute()
        {
        }

        @Override
        protected String doInBackground(String... params)
        {
            label = params[0];

            try
            {
                //Shan please help me get restaurant ID from your local file.

                String restaurantId = "1"; // get restaurant Id from local file
                System.err.println("********** Calling get Fridges Rest web service");
                URL url = new URL(getString(R.string.VM_address) + "FoodEmblemV1-war/Resources/Sensor/getFridgesByRestaurantId/" + restaurantId + "");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder(); //cimplete json string

                String line = null;

                while ((line = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(line);
                }

                return stringBuilder.toString(); //complete json string
            }
            catch(Exception ex)
            {

                System.out.println("erro calling API");


                ex.printStackTrace();
            }


            return "Async Task Completed";
        }

        @Override
        protected void onProgressUpdate(Integer... progress)
        {

        }

        @Override
        protected void onPostExecute(String jsonString)
        {

            //this is how we populate the value obtained from web service to list view.
            try{
                List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("fridges");
                //jsonObject.getString()
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    HashMap<String,String>  dataToStore = new HashMap<String,String>();
                    dataToStore.put("fridgeTxt" , "Fridge ID");
                    dataToStore.put("fridgeId" , jsonArray.getJSONObject(i).getString("id"));
                    dataToStore.put("tempTxt" , "Temperature");
                    dataToStore.put("tempValue", jsonArray.getJSONObject(i).getString("temperature") + " C");
                    aList.add(dataToStore);
                }

                // Keys used in Hashmap
                String[] from = {"fridgeTxt","fridgeId" , "tempTxt" , "tempValue" };

                // Ids of views in fridgelistview_layout
                int[] to = {R.id.fridgetxt, R.id.fridgeId , R.id.tempTxt,  R.id.tempValue};

                // Instantiating an adapter to store each items
                // R.layout.listview_layout defines the layout of each item
                SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.fridgelistview_layout, from, to);

                // Getting a reference to listview of Fridge.xml layout file
                ListView listView = ( ListView ) findViewById(R.id.fridgeListView);

                // Setting the adapter to the listView
                listView.setAdapter(adapter);
            }

            catch(Exception ex){

            }
        }

    }

    //retrieving data from fridge
    public void retrieveFridge(){

        System.err.println("Retrieve Fridge Method");
        doAsyncTaskButtonAsyncTask = new DoAsyncTaskButtonAsyncTask();
        doAsyncTaskButtonAsyncTask.execute("Current progress");
    }


}
