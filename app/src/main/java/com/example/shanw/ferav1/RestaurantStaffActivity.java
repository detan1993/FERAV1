package com.example.shanw.ferav1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class RestaurantStaffActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_staff);
        printSharedPreferences();
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

        System.err.print("**************************Email: " + email);
        System.err.print("**************************Email: " + fullName);
        //((EditText) findViewById(R.id.editText)).setText(email + " " + fullName);
    }
}
