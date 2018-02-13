package com.sw;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static java.lang.Integer.parseInt;


public class BroadcastReceiverOnBootComplete extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        //noinspection ConstantConditions
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {

            SharedPreferences prefos = PreferenceManager.getDefaultSharedPreferences(context);
            Boolean svcstate = prefos.getBoolean("alarm_state", false);
            Boolean svcboot = prefos.getBoolean("atboot_switch", false);

            if (svcstate && svcboot) {
                Intent i = new Intent(context, AlarmEvents.class);
                PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
                AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                String interval = prefos.getString("sync_frequency", "15");
                Integer NOTIFY_INTERVAL = parseInt(interval) * 1000;
                assert manager != null;
                manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), NOTIFY_INTERVAL, pi);
                manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), NOTIFY_INTERVAL, pi);
                Toast.makeText(context, "WSW is monitoring...    :) ", Toast.LENGTH_SHORT).show();
            }
        }
    }
}



