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

public class ContainerActivity extends AppCompatActivity {

    Handler mHandler;

    private DoAsyncTaskButtonAsyncTask doAsyncTaskButtonAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        this.mHandler = new Handler();
        this.mHandler.postDelayed(m_Runnable,5000);
        m_Runnable.run();
    }

    private final Runnable m_Runnable = new Runnable()
    {
        public void run()
        {
            retrieveContainer();
            System.out.println("Refresh Date :" + new Date());
            ContainerActivity.this.mHandler.postDelayed(m_Runnable,10000);
        }

    };

    @Override
    public void onBackPressed(){
        this.mHandler.removeCallbacks(m_Runnable); // removing the timer.
        Intent intent = new Intent(this, RestaurantStaffActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onPause(){
        this.mHandler.removeCallbacks(m_Runnable);
        super.onPause();
    }

    @Override
    protected void onResume(){
        ContainerActivity.this.mHandler.postDelayed(m_Runnable, 10000);
        super.onResume();
    }

    protected class DoAsyncTaskButtonAsyncTask extends AsyncTask<String, Integer, String>
    {
        @Override
        protected void onPreExecute()
        {
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                System.out.println("Container webservice doInBackground()");

                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                String restaurantId = sharedPref.getString("staff_restaurantId",null);; // get restaurant Id from local file
                System.out.println("Restaurant Id: " + restaurantId);

                URL url = new URL(getString(R.string.VM_address) + "FoodEmblemV1-war/Resources/Sensor/getContainersByRestaurantId/" + restaurantId);
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
        protected void onProgressUpdate(Integer... progress)
        {

        }

        @Override
        protected void onPostExecute(String jsonString)
        {
            try{
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("containers");
                List<HashMap<String,String>> containerList = new ArrayList<HashMap<String,String>>();

                System.out.println("Number of containers: " + jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    System.out.println("Inside Container number : " + i+1);
                    HashMap<String,String>  dataToStore = new HashMap<String,String>();
                    dataToStore.put("containerId" , "Container Id : " + jsonArray.getJSONObject(i).getString("id"));

                    JSONObject inventoryJSONObject = jsonArray.getJSONObject(i).getJSONObject("inventory");
                    dataToStore.put("inventoryId", "Inventory Id : " + inventoryJSONObject.getString("id"));
                    dataToStore.put("inventoryName", "Name : " + inventoryJSONObject.getString("name"));

                    double inventoryWeight = Double.parseDouble(inventoryJSONObject.getString("weight"));
                    double containerWeight = Double.parseDouble(jsonArray.getJSONObject(i).getString(   "weight"));
                    dataToStore.put("inventoryWeight", "Weight : " + String.format("%.2f",inventoryWeight));

                    containerList.add(dataToStore);
                }
                String[] from = {"containerId" , "inventoryId" , "inventoryName" , "inventoryWeight"};
                int[] to = {R.id.tv_container, R.id.tv_inventoryId , R.id.tv_inventoryName,  R.id.tv_inventoryWeight};
                SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), containerList, R.layout.containerlistview_layout, from, to);
                ListView listView = ( ListView ) findViewById(R.id.lv_inventory);
                listView.setAdapter(adapter);
            }

            catch(Exception ex){
                ex.printStackTrace();
            }
        }

    }

    public void retrieveContainer(){
        doAsyncTaskButtonAsyncTask = new DoAsyncTaskButtonAsyncTask();
        doAsyncTaskButtonAsyncTask.execute("Current progress");
    }

    public void goHome(View view){
        startActivity(new Intent(ContainerActivity.this, RestaurantStaffActivity.class));
    }
}
