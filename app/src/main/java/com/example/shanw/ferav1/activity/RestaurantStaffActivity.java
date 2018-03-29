package com.example.shanw.ferav1.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.shanw.ferav1.R;
import com.example.shanw.ferav1.services.KicthenNotificationServices;
import com.example.shanw.ferav1.services.NotificationService;

public class RestaurantStaffActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_staff);

        boolean serviceRunningStatus = isServiceRunning(KicthenNotificationServices.class);
        if(!serviceRunningStatus){
            Intent intent = new Intent(this, KicthenNotificationServices.class);
            startService(intent);

        }
       printSharedPreferences();
    }


    public boolean isServiceRunning(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {

                //Toast.makeText(this, "IT IS RUNNING" ,Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    public void goToFridge(View view){
        startActivity(new Intent(RestaurantStaffActivity.this, FridgeActivity.class));
    }

    public void goToContainer(View view){
        startActivity(new Intent(RestaurantStaffActivity.this, ContainerActivity.class));
    }

    public void printSharedPreferences(){
        System.err.print("************************* PRINTSHAREDPREFERENCES CALLED");
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String email = sharedPref.getString("staff_email",null);
        String fullName = sharedPref.getString("staff_name",null);
        String restaurantId = sharedPref.getString("staff_restaurantId",null);

        System.err.print("**************************Email: " + email);
        System.err.print("**************************Name: " + fullName);
        System.err.print("**************************Restaurant Id: " + restaurantId);
        //((EditText) findViewById(R.id.editText)).setText(email + " " + fullName);
    }
}
