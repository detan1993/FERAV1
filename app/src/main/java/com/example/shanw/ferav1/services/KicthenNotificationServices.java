package com.example.shanw.ferav1.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.app.PendingIntent;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.os.Process;
import android.app.AlarmManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;


import com.example.shanw.ferav1.R;
import com.example.shanw.ferav1.activity.ContainerActivity;
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
import java.util.HashMap;
import java.util.List;


public class KicthenNotificationServices extends Service {


   Handler handler = new Handler();
    public static int notificationId = 1;

    private Runnable periodicUpdate = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(periodicUpdate, 10000); // schedule next wake up every second
            Intent notificationIntent = new Intent(KicthenNotificationServices.this, KicthenNotificationServices.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(KicthenNotificationServices.this, 0, notificationIntent, 0);
            AlarmManager keepAwake = (AlarmManager) getSystemService(ALARM_SERVICE);
            keepAwake.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +10000, pendingIntent);

            long current = System.currentTimeMillis();
            if ((current-current%10000)%(10000*10)  == 0) { // record on every tenth seconds (0s, 10s, 20s, 30s...)
                // whatever you want to do
                getTemperatureValue();
            }
        }
    };

    public KicthenNotificationServices() {
    }


    @Override
    public void onCreate(){

      //  HandlerThread thread = new HandlerThread("Notification Start " , Process.THREAD_PRIORITY_BACKGROUND);
      //  thread.start();

    }



     @Override
     public int onStartCommand(Intent intent, int flags, int startId)
     {
         Toast.makeText(this, "Notification Services started" ,Toast.LENGTH_SHORT).show();
         handler.post(periodicUpdate);
     /*    Message msg = mServiceHandler.obtainMessage();
         msg.arg1 = startId;
         mServiceHandler.sendMessage(msg); */

         return START_NOT_STICKY ;

     }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy(){
        Toast.makeText(this , "Notifification Service terminated" , Toast.LENGTH_LONG).show();
    }

    public void getTemperatureValue(){
       // Toast.makeText(this, "SUCCESS" ,Toast.LENGTH_SHORT).show();

        GetTemperatureValueAsync getTemp = new GetTemperatureValueAsync();
        getTemp.execute("Get temperature");

        GetWeightValueAsync getWeight = new GetWeightValueAsync();
        getWeight.execute("Get Weigth");


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
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("fridges");
                double temperatureValue = 0.0;
                double threshold = 0.0;
                String fridgeId = "";
                List<FridgeTemp> fridges = new ArrayList<>();
                //jsonObject.getString()
                for (int i = 0; i < jsonArray.length(); i++) {

                    temperatureValue = Double.parseDouble(jsonArray.getJSONObject(i).getString("temperature"));
                    threshold = Double.parseDouble(jsonArray.getJSONObject(i).getString("threshold"));
                    fridgeId = jsonArray.getJSONObject(i).getString("id");

                    System.err.println("****************************temperatureValue" + temperatureValue);
                    System.err.println("****************************temperatureValue" + threshold);


                    if (temperatureValue > threshold) {
                        double tempDiff = temperatureValue - threshold;
                        fridges.add(new FridgeTemp(fridgeId, tempDiff));

                      /*  String tittle="ALERT";
                        String subject="ALERT Fridge Temperature";
                        String body="Fridge ID#" +  fridgeId + "temperature is above your threshold by " + tempDiff + "C";

                        NotificationManager notif=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        Notification notify=new Notification.Builder    (getApplicationContext()).setContentTitle(tittle).setContentText(body).
                                setContentTitle(subject).setSmallIcon(R.drawable.ic_launcher_background).setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 }).
                                setDefaults(Notification.DEFAULT_SOUND).build();

                        notify.flags |= Notification.FLAG_AUTO_CANCEL;
                        notif.notify(notificationId, notify);*/

                    } /*else
                    {
                        getToastMessage("Safe!!");
                    }*/

                }
                for (FridgeTemp fridge : fridges) {
                    String tittle="ALERT";
                    String subject="ALERT Fridge Temperature";
                    String body="Fridge ID#" +  fridge.getFridgeId() + " temperature is above threshold by " + fridge.getTemperatureDiff() + "C";
                    NotificationManager notif=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notify=new Notification.Builder    (getApplicationContext()).setContentTitle(tittle).setContentText(body).
                            setContentTitle(subject).setSmallIcon(R.drawable.ic_launcher_background).setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 }).
                            setDefaults(Notification.DEFAULT_SOUND).build();


                    Intent viewIntent = new Intent(KicthenNotificationServices.this, FridgeActivity.class);
                    // viewIntent.putExtra(EXTRA_EVENT_ID, eventId);
                    PendingIntent viewPendingIntent =   PendingIntent.getActivity(KicthenNotificationServices.this, 0, viewIntent, 0);

                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(KicthenNotificationServices.this).setSmallIcon(R.drawable.food_emblem_home_text)
                                    .setContentTitle("ALERT Fridge Temperature")
                                    .setContentText("Fridge ID#" +  fridge.getFridgeId() + " temperature is above threshold by " + fridge.getTemperatureDiff() + "C")
                                    .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 }).setDefaults(Notification.DEFAULT_SOUND).setContentIntent(viewPendingIntent);

                    // Get an instance of the NotificationManager service
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(KicthenNotificationServices.this);

                    //Build the notification and issues it with notification manager.
                    notificationManager.notify(notificationId, notificationBuilder.build());
                    ++notificationId;

                }


            }

            catch(Exception ex){

            }


        }

    }

    protected class GetWeightValueAsync extends AsyncTask<String, Integer, String>
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
                    JSONObject inventoryInformation = jsonArray.getJSONObject(i).getJSONObject("inventory");
                     inventoryWeight = Double.parseDouble(inventoryInformation.getString("weight"));
                     threshold = Double.parseDouble(inventoryInformation.getString("threshold"));
                    inventoryName = inventoryInformation.getString("name");
                    inventoryId = inventoryInformation.getString("id");

                    System.err.println("****************************Inventory Threshold" + threshold);
                    System.err.println("****************************Inventory Weight" + inventoryWeight);

                    double netInventoryWeight = inventoryWeight - containerWeight;


                    if (netInventoryWeight < threshold) {
                        double weigthDiff = threshold - netInventoryWeight;
                        containers.add( new Container(containerId, inventoryId, inventoryName, weigthDiff));
                    }
                  /*  else{
                        getToastMessage("Coninatener safe!!");
                     }*/
                    
                  // getToastMessage("containe id " + containerId + " size  " + inventoryInformation.length());

                }
                for (Container container : containers) {

                    Intent viewIntent = new Intent(KicthenNotificationServices.this, ContainerActivity.class);
                    // viewIntent.putExtra(EXTRA_EVENT_ID, eventId);
                    PendingIntent viewPendingIntent =   PendingIntent.getActivity(KicthenNotificationServices.this, 0, viewIntent, 0);

                    NotificationCompat.Builder notificationBuilder =
                            new NotificationCompat.Builder(KicthenNotificationServices.this).setSmallIcon(R.drawable.food_emblem_home_text)
                                    .setContentTitle("Inventory Alert")
                                    .setContentText(container.getInventoryName() + " ID#" +  container.getInventoryId() + " in Container ID#" + container.getContainerId() + " is below threshold by " + container.getWeightDiff() + "KG")
                                    .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 }).setDefaults(Notification.DEFAULT_SOUND).setContentIntent(viewPendingIntent);

                    // Get an instance of the NotificationManager service
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(KicthenNotificationServices.this);

                    //Build the notification and issues it with notification manager.
                    notificationManager.notify(notificationId, notificationBuilder.build());
                    ++notificationId;


                }


            }

            catch(Exception ex){

            }


        }

    }


   /* private Intent getPreviousIntent() {
        Intent newIntent = null;
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final List<ActivityManager.AppTask> recentTaskInfos = activityManager.getAppTasks();
            if (!recentTaskInfos.isEmpty()) {
                for (ActivityManager.AppTask appTaskTaskInfo: recentTaskInfos) {
                    if (appTaskTaskInfo.getTaskInfo().baseIntent.getComponent().getPackageName().equals(ContextConstants.PACKAGE_NAME)) {
                        newIntent = appTaskTaskInfo.getTaskInfo().baseIntent;
                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                }
            }
        } else {
            final List<ActivityManager.RecentTaskInfo> recentTaskInfos = activityManager.getRecentTasks(1024, 0);
            if (!recentTaskInfos.isEmpty()) {
                for (ActivityManager.RecentTaskInfo recentTaskInfo: recentTaskInfos) {
                    if (recentTaskInfo.baseIntent.getComponent().getPackageName().equals(ContextConstants.PACKAGE_NAME)) {
                        newIntent = recentTaskInfo.baseIntent;
                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                }
            }
        }
        if (newIntent == null) newIntent = new Intent();
        return newIntent;
    }*/

    public void getToastMessage(String val){

        Toast.makeText(this, val ,Toast.LENGTH_SHORT).show();
    }

    public class FridgeTemp{

        private String fridgeId;
        private double temperatureDiff;

        public  FridgeTemp(){

        }

        public  FridgeTemp(String fridgeId, Double temperatureDiff){

            this.fridgeId = fridgeId;
            this.temperatureDiff = temperatureDiff;
        }

        public String getFridgeId(){

            return fridgeId;
        }

        public void setFridgeId(String fridgeId){
            this.fridgeId = fridgeId;
        }

        public double getTemperatureDiff(){
            return temperatureDiff;
        }
        public void setTemperatureDiff (double temperatureDiff){
            this.temperatureDiff = temperatureDiff;
        }


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
