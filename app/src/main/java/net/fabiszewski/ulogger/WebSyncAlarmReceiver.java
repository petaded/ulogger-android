package net.fabiszewski.ulogger;

import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.ComponentName;

import androidx.core.content.ContextCompat;

public class WebSyncAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = WebSyncAlarmReceiver.class.getSimpleName();

    public static final String EXTRA_SERVICE_CLASS = "net.fabiszewski.ulogger.WebSyncService";

    /**
     * @param intent an Intent meant for a {@link android.support.v4.app.JobIntentService}
     * @return a new Intent intended for use by this receiver based off the passed intent
     */
    public static Intent getIntent(Context context, Intent intent) {
        ComponentName component = intent.getComponent();
        if (component == null)
            throw new RuntimeException("Missing intent component");

        Intent new_intent = new Intent(intent)
                .putExtra(EXTRA_SERVICE_CLASS, component.getClassName());

        new_intent.setClass(context, WebSyncAlarmReceiver.class);

        return new_intent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Logger.DEBUG) { Log.d(TAG, "[websyncreceiver onReceive]"); }
        try {
            if (intent.getExtras() == null)
                throw new Exception("No extras found");


            // change intent's class to its intended service's class
            String service_class_name = intent.getStringExtra(EXTRA_SERVICE_CLASS);

            if (service_class_name == null)
                throw new Exception("No service class found in extras");

            Class service_class = Class.forName(service_class_name);

            if (!WebSyncService.class.isAssignableFrom(service_class))
                throw new Exception("Service class found is not a WebSyncService: " + service_class.getName());

            intent.setClass(context, service_class);
            // start the service
            WebSyncService.enqueueWork(context, intent);

        } catch (Exception e) {
            System.err.println("Error starting service from receiver: " + e.getMessage());
        }
    }

}