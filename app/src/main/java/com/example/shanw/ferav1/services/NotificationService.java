package com.example.shanw.ferav1.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.widget.Toast;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NotificationService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    /*private static final String ACTION_FOO = "com.example.shanw.ferav1.services.action.FOO";
    private static final String ACTION_BAZ = "com.example.shanw.ferav1.services.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.shanw.ferav1.services.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.shanw.ferav1.services.extra.PARAM2"; */

    public NotificationService() {
        super("NotificationService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {

        try{

            Thread.sleep(10000);
            System.err.println("******CHECKING TEMP VALUE");
            // call rest web service here
        }
        catch (InterruptedException ex){
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public int onStartCommand(Intent intent , int flags , int startId)
    {
        Toast.makeText(this, "Notifaction service is starting", Toast.LENGTH_SHORT).show();

        return super.onStartCommand(intent, flags, startId);

    }

}
