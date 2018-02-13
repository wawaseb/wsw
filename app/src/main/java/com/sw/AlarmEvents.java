package com.sw;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;

import java.util.concurrent.ExecutionException;

import static com.sw.SettingsActivity.entry;

public class AlarmEvents extends BroadcastReceiver {

    public static String to_notif = "";

    public AlarmEvents() {
        super();
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        PowerManager _pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        assert _pm != null;
        PowerManager.WakeLock _wl = _pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "to start ALARM frequently...  :p");
        if (!_wl.isHeld()) {
            _wl.acquire(10000);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext().getApplicationContext());
        Boolean eth_watch = prefs.getBoolean("eth_watch", true);
        Boolean btc_watch = prefs.getBoolean("btc_watch", false);
        Boolean xrp_watch = prefs.getBoolean("xrp_watch", true);
        String values = "";
        to_notif = "";


        String all = null;
        try {
            all = new RetrieveActivity.useAPI(context.getApplicationContext(), entry).execute("").get();
        } catch (InterruptedException ignored) {

        } catch (ExecutionException ignored) {

        }


        if (eth_watch) {
            String entri = "ETH_";
            entry = entri + all;
            try {
                new RetrieveActivity.getValue(context.getApplicationContext(), entry).execute("").get();
            } catch (InterruptedException ignored) {

            } catch (ExecutionException ignored) {

            }
        }


        if (btc_watch) {
            String entri = "BTC_";
            entry = entri + all;
            try {
                new RetrieveActivity.getValue(context.getApplicationContext(), entry).execute("").get();
            } catch (InterruptedException ignored) {

            } catch (ExecutionException ignored) {

            }
        }


        if (xrp_watch) {
            String entri = "XRP_";
            entry = entri + all;
            try {
                new RetrieveActivity.getValue(context.getApplicationContext(), entry).execute("").get();
            } catch (InterruptedException ignored) {

            } catch (ExecutionException ignored) {

            }
        }


        _wl.acquire(10000);
    }
}