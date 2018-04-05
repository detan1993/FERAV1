package com.example.shanw.ferav1.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

import com.example.shanw.ferav1.R;
import com.example.shanw.ferav1.activity.FridgeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WeightNotificationServices extends Service {


    Handler handler = new Handler();
    public static int notificationId = 1;

    private Runnable periodicUpdate = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(periodicUpdate, 1000); // schedule next wake up every second
            Intent notificationIntent = new Intent(WeightNotificationServices.this, WeightNotificationServices.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(WeightNotificationServices.this, 0, notificationIntent, 0);
            AlarmManager keepAwake = (AlarmManager) getSystemService(ALARM_SERVICE);
            keepAwake.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000, pendingIntent);

            long current = System.currentTimeMillis();
            if ((current-current%1000)%(1000*10)  == 0) { // record on every tenth seconds (0s, 10s, 20s, 30s...)
                // whatever you want to do
                getWeigthValue();
            }
        }
    };


    public WeightNotificationServices() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Toast.makeText(this, "Weight Notification Services started" ,Toast.LENGTH_SHORT).show();
        handler.post(periodicUpdate);
     /*    Message msg = mServiceHandler.obtainMessage();
         msg.arg1 = startId;
         mServiceHandler.sendMessage(msg); */

        return START_STICKY;


    }

    public void getWeigthValue(){
        // Toast.makeText(this, "SUCCESS" ,Toast.LENGTH_SHORT).show();

         GetTemperatureValueAsync monitorWeight = new GetTemperatureValueAsync();
        monitorWeight.execute("Get Weight");
    }



    protected class GetTemperatureValueAsync extends AsyncTask<String, Integer, String>
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
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                String restaurantId = sharedPref.getString("staff_restaurantId",null);; // get restaurant Id from local file

                System.err.println("********** Calling get weight Rest web service");
                URL url = new URL(getString(R.string.VM_address) + "FoodEmblemV1-war/Resources/Sensor/getContainersByRestaurantId/" + restaurantId + "");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder(); //complete json string

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
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("containers");
                double containerWeight = 0.0;
                double inventoryWeight = 0.0;
                double threshold =0.0;
                String containerId= "";
                String inventoryId= "";
                String inventoryName = "";
                List<Container> containers = new ArrayList<>();
                //jsonObject.getString()
                for (int i = 0; i < jsonArray.length(); i++) {

                    containerId = jsonArray.getJSONObject(i).getString("id");
                    containerWeight = Double.parseDouble(jsonArray.getJSONObject(i).getString("weight"));
                    /*JSONArray inventoryInformation = jsonArray.getJSONObject(i).getJSONArray("inventory");
                    inventoryWeight = Double.parseDouble(inventoryInformation.getJSONObject(0).getString("weight"));
                    threshold = Double.parseDouble(inventoryInformation.getJSONObject(0).getString("threshold"));
                    inventoryName = inventoryInformation.getJSONObject(0).getString("name");
                    inventoryId = inventoryInformation.getJSONObject(0).getString("id");

                    System.err.println("****************************Inventory Threshold" + threshold);
                    System.err.println("****************************Inventory Weight" + inventoryWeight);

                    double netInventoryWeight = inventoryWeight - containerWeight;


                    if (netInventoryWeight < threshold) {
                        double weigthDiff = threshold - netInventoryWeight;
                        containers.add( new Container(containerId, inventoryId, inventoryName, weigthDiff));
                    }
                    else{
                        getToastMessage("Name is  " + inventoryName + " weight is " + inventoryWeight);
                    }*/

                    getToastMessage("containe id " + containerId);

                }
             /*   for (Container container : containers) {
                    String tittle="ALERT";
                    String subject="ALERT Container Weight";
                    String body= container.getInventoryName() + " ID#" +  container.getInventoryId() + " in Container ID#" + container.getContainerId() + " is below threshold by " + container.getWeightDiff() + "KG";
                    NotificationManager notif=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notify=new Notification.Builder    (getApplicationContext()).setContentTitle(tittle).setContentText(body).
                            setContentTitle(subject).setSmallIcon(R.drawable.ic_launcher_background).setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 }).
                            setDefaults(Notification.DEFAULT_SOUND).build();


                    notify.flags |= Notification.FLAG_AUTO_CANCEL;
                    notif.notify(notificationId, notify);

                    Intent notificationIntent = new Intent( getApplicationContext() , FridgeActivity.class);

                    PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0,
                            notificationIntent, 0);

                    notif.notify(notificationId, notify);

                }*/


            }

            catch(Exception ex){

            }


        }

    }


    public void getToastMessage(String val){

        Toast.makeText(this, val ,Toast.LENGTH_SHORT).show();
    }

    public class Container{

        private String containerId;
        private String inventoryId;
        private String inventoryName;
        private double weightDiff;

        public Container(){

        }

        public Container(String containerId , String inventoryId , String inventoryName , double weightDiff){

            this.setContainerId(containerId);
            this.setInventoryId(inventoryId);
            this.setInventoryName(inventoryName);
            this.setWeightDiff(weightDiff);

        }


        public String getContainerId() {
            return containerId;
        }

        public void setContainerId(String containerId) {
            this.containerId = containerId;
        }

        public String getInventoryId() {
            return inventoryId;
        }

        public void setInventoryId(String inventoryId) {
            this.inventoryId = inventoryId;
        }

        public String getInventoryName() {
            return inventoryName;
        }

        public void setInventoryName(String inventoryName) {
            this.inventoryName = inventoryName;
        }

        public double getWeightDiff() {
            return weightDiff;
        }

        public void setWeightDiff(double weightDiff) {
            this.weightDiff = weightDiff;
        }
    }




}
