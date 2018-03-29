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
import java.util.HashMap;
import java.util.List;

public class WeightNotificationServices extends Service {


    Handler handler = new Handler();
    public static int notificationId = 1;

    private Runnable periodicUpdate = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(periodicUpdate, 10000); // schedule next wake up every second
            Intent notificationIntent = new Intent(WeightNotificationServices.this, KicthenNotificationServices.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(WeightNotificationServices.this, 0, notificationIntent, 0);
            AlarmManager keepAwake = (AlarmManager) getSystemService(ALARM_SERVICE);
            keepAwake.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 10000, pendingIntent);

            long current = System.currentTimeMillis();
            if ((current-current%10000)%(10000*10)  == 0) { // record on every tenth seconds (0s, 10s, 20s, 30s...)
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
        Toast.makeText(this, "Notification Services started" ,Toast.LENGTH_SHORT).show();
        handler.post(periodicUpdate);
     /*    Message msg = mServiceHandler.obtainMessage();
         msg.arg1 = startId;
         mServiceHandler.sendMessage(msg); */

        return START_STICKY;


    }

    public void getWeigthValue(){
        // Toast.makeText(this, "SUCCESS" ,Toast.LENGTH_SHORT).show();

      //  KicthenNotificationServices.GetTemperatureValueAsync getTemp = new KicthenNotificationServices.GetTemperatureValueAsync();
      //  getTemp.execute("Get temperature");
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
                List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("fridges");
                double temperatureValue = 0.0;
                double threshold = 0.0;
                String fridgeId = "";
                List<KicthenNotificationServices.FridgeTemp> fridges = new ArrayList<>();
                //jsonObject.getString()
                for (int i = 0; i < jsonArray.length(); i++) {

                    temperatureValue = Double.parseDouble(jsonArray.getJSONObject(i).getString("temperature"));
                    threshold = Double.parseDouble(jsonArray.getJSONObject(i).getString("threshold"));

                    System.err.println("****************************temperatureValue" + temperatureValue);
                    System.err.println("****************************temperatureValue" + threshold);


                    if (temperatureValue > threshold) {
                        double tempDiff = temperatureValue - threshold;
                        //fridges.add(new KicthenNotificationServices.FridgeTemp(fridgeId, tempDiff));


                    }

                }
                for (KicthenNotificationServices.FridgeTemp fridge : fridges) {
                    String tittle="ALERT";
                    String subject="ALERT Fridge Temperature";
                    String body="Fridge ID#" +  fridge.getFridgeId() + " temperature is above threshold by " + fridge.getTemperatureDiff() + "C";
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


                  /*  Intent notificationIntent = new Intent(context, HomeActivity.class);

                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    PendingIntent intent = PendingIntent.getActivity(context, 0,
                            notificationIntent, 0);

                    notification.setLatestEventInfo(context, title, message, intent);
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    notificationManager.notify(0, notification);*/
                }


            }

            catch(Exception ex){

            }


        }

    }


    public void getToastMessage(String val){

        Toast.makeText(this, val ,Toast.LENGTH_SHORT).show();
    }


}
