package com.sw;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;
import static java.lang.Integer.parseInt;


class RetrieveActivity {
    private final Context mContext;
    private static String result = "no";


    // constructor
    public RetrieveActivity(Context context) {
        this.mContext = context;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setAlarm() {

        Intent i = new Intent(mContext, AlarmEvents.class);
        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, i, 0);
        AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        SharedPreferences prefos = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        String interval = prefos.getString("sync_frequency", "15");
        Integer NOTIFY_INTERVAL = parseInt(interval) * 1000;

        assert manager != null;
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), NOTIFY_INTERVAL, pi);
        Toast.makeText(mContext, "Alarm is ON", Toast.LENGTH_SHORT).show();
    }


    public void stopAlarm() {
        Intent i = new Intent(mContext, AlarmEvents.class);
        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, i, 0);
        AlarmManager manager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        assert manager != null;
        manager.cancel(pi);
        Toast.makeText(mContext, "Alarm is OFF", Toast.LENGTH_SHORT).show();
    }


    @SuppressLint("StaticFieldLeak")
    public static class useAPI extends AsyncTask<String, Void, String> {
        private Context zContext;
        private boolean triggered = false;
        private String resulto = "";

        public useAPI(Context context, String entry) {

        }


        @Override
        protected synchronized String doInBackground(String... strings) {

            URL url = null;
            String last_all = "";

            try

            {
                url = new URL("https://min-api.cryptocompare.com/data/pricemulti?fsyms=ETH,BTC,XRP&tsyms=EUR");
            } catch (
                    MalformedURLException ignored)

            {

            }

            HttpURLConnection connection = null;
            try

            {
                assert url != null;
                connection = (HttpURLConnection) url.openConnection();
            } catch (
                    IOException ignored)

            {

            }
            assert connection != null;
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            try

            {
                connection.setRequestMethod("GET");
            } catch (
                    ProtocolException ignored)

            {

            }
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            int responceCode = 0;
            try

            {
                responceCode = connection.getResponseCode();
            } catch (
                    IOException ignored)

            {

            }

            if (responceCode == HttpURLConnection.HTTP_OK)

            {
                String line;
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } catch (IOException ignored) {

                }
                try {
                    assert br != null;
                    while ((line = br.readLine()) != null) {
                        last_all = "";// String variable declared global
                        last_all += line;
                        Log.i("response_line", last_all);
                    }
                } catch (IOException ignored) {

                }
            } else

            {
                last_all = "no";
            }

            return last_all;
        }
    }


    @SuppressLint("StaticFieldLeak")
    public static class getValue extends AsyncTask<String, Void, String> {
        private final Context zContext;
        private boolean triggered = false;
        private String resulto = "";


        public getValue(Context context, String entry) {
            zContext = context;

        }


        private void send_notif(String how_much) {


            NotificationCompat.Builder mBuildera =
                    new NotificationCompat.Builder(zContext.getApplicationContext())
                            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                            .setContentTitle("Wawa Stock Watcher: price(s)")
                            .setContentText("Waiting for values...")
                            .setPriority(PRIORITY_MAX)
                            .setAutoCancel(true);

            NotificationManager mNotifyMgra =
                    (NotificationManager) zContext.getSystemService(NOTIFICATION_SERVICE);

            assert mNotifyMgra != null;
            mNotifyMgra.notify(9, mBuildera.build());


            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(zContext.getApplicationContext())
                            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                            .setContentTitle("Wawa Stock Watcher: price(s)")
                            .setContentText(how_much)
                            .setPriority(PRIORITY_MAX)
                            .setAutoCancel(true);

            NotificationManager mNotifyMgr =
                    (NotificationManager) zContext.getSystemService(NOTIFICATION_SERVICE);

            assert mNotifyMgr != null;
            mNotifyMgr.notify(9, mBuilder.build());

        }


        @Override
        protected synchronized String doInBackground(String... strings) {

            String pre = "";
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(zContext);


            String min_dur = prefs.getString("pref_title_vibdur_min", "5000");
            String max_dur = prefs.getString("pref_title_vibdur_max", "5000");
            SettingsActivity.minimum_duration = parseInt(min_dur);
            SettingsActivity.maximum_duration = parseInt(max_dur);

            SettingsActivity.min_sound = prefs.getString("pref_min_filePicker", "default ringtone");
            SettingsActivity.max_sound = prefs.getString("pref_max_filePicker", "default ringtone");

            result = SettingsActivity.entry;

            SettingsActivity.entry = SettingsActivity.entry.substring(0, 3);


            switch (SettingsActivity.entry) {

                case "ETH":
                    pre = "ETH = ";
                    SettingsActivity.what = "ETH";
                    String min_eth = prefs.getString("pref_title_def_min", "0");
                    String max_eth = prefs.getString("pref_title_def_max", "99999999");
                    SettingsActivity.min = Float.parseFloat(min_eth);
                    SettingsActivity.max = Float.parseFloat(max_eth);
                    break;

                case "BTC":
                    pre = "BTC = ";
                    SettingsActivity.what = "BTC";
                    String min_btc = prefs.getString("pref_title_def_min_btc", "0");
                    String max_btc = prefs.getString("pref_title_def_max_btc", "99999999");
                    SettingsActivity.min = Float.parseFloat(min_btc);
                    SettingsActivity.max = Float.parseFloat(max_btc);
                    break;

                case "XRP":
                    pre = "XRP = ";
                    SettingsActivity.what = "XRP";
                    String min_xrp = prefs.getString("pref_title_def_min_xrp", "0");
                    String max_xrp = prefs.getString("pref_title_def_max_xrp", "99999999");
                    SettingsActivity.min = Float.parseFloat(min_xrp);
                    SettingsActivity.max = Float.parseFloat(max_xrp);
                    break;
            }


            if (!result.toLowerCase().contains("no".toLowerCase())) {
                result = result.substring(4);
            } else {
                result = "no";
            }


            String result_all = result;     // full file name
            String[] parts = result_all.split(","); // String array, each element is text between dots


            switch (SettingsActivity.entry) {

                case "ETH":
                    result = parts[0];
                    break;

                case "BTC":
                    result = parts[1];
                    break;

                case "XRP":
                    result = parts[2];
                    break;
            }


            result = result.replace("ETH", "");
            result = result.replace("BTC", "");
            result = result.replace("XRP", "");
            result = result.replace("EUR", "");
            result = result.replace(":", "");
            result = result.replace("{", "");
            result = result.replace("}", "");
            result = result.replace("\"", "");

            String brut_text = result;
            if (!brut_text.equalsIgnoreCase("no")) {
                SettingsActivity.current = Float.parseFloat(brut_text);
            } else {
                SettingsActivity.current = 0.0f;
            }

            result = pre + result + "€";


            int mincheck = SettingsActivity.min.compareTo(SettingsActivity.current);
            int maxcheck = SettingsActivity.max.compareTo(SettingsActivity.current);

            if (SettingsActivity.current > 0.1f) {
                if (mincheck > 0) {
                    resulto = "";
                    resulto = "Min " + SettingsActivity.what + " triggered : " + brut_text + " < " + SettingsActivity.min;

                    long[] vibrate = {0, SettingsActivity.minimum_duration, SettingsActivity.minimum_duration / 2, SettingsActivity.minimum_duration / 2};
                    Uri uri = Uri.parse(SettingsActivity.min_sound);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(zContext)
                                    .setSmallIcon(R.drawable.ic_notifications_white_24dp)
                                    .setContentTitle("Wawa Stock Watcher")
                                    .setContentText("MIN " + SettingsActivity.what + " triggered")
                                    .setPriority(PRIORITY_MAX)
                                    .setSound(uri)
                                    .setVibrate(vibrate)
                                    .setAutoCancel(true);

                    NotificationManager mNotifyMgr =
                            (NotificationManager) zContext.getSystemService(NOTIFICATION_SERVICE);

                    assert mNotifyMgr != null;
                    mNotifyMgr.notify(3, mBuilder.build());

                    Ringtone r = RingtoneManager.getRingtone(zContext, uri);
                    if (r != null)
                        r.play();

                    SharedPreferences.Editor editor = prefs.edit();
                    if (SettingsActivity.what.equalsIgnoreCase("ETH")) {
                        editor.putString("pref_title_def_min", "0").apply();
                    }
                    if (SettingsActivity.what.equalsIgnoreCase("BTC")) {
                        editor.putString("pref_title_def_min_btc", "0").apply();
                    }
                    if (SettingsActivity.what.equalsIgnoreCase("XRP")) {
                        editor.putString("pref_title_def_min_xrp", "0").apply();
                    }

                    triggered = true;
                } else if (maxcheck < 0) {
                    resulto = "";
                    resulto = "Max " + SettingsActivity.what + " triggered : " + brut_text + " > " + SettingsActivity.max;

                    long[] vibrate = {0, SettingsActivity.maximum_duration, SettingsActivity.maximum_duration / 2, SettingsActivity.maximum_duration / 2};
                    Uri uri = Uri.parse(SettingsActivity.max_sound);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(zContext)
                                    .setSmallIcon(R.drawable.ic_notifications_white_24dp)
                                    .setContentTitle("Wawa Stock Watcher")
                                    .setContentText("MAX " + SettingsActivity.what + " triggered")
                                    .setPriority(PRIORITY_MAX)
                                    .setSound(uri)
                                    .setVibrate(vibrate)
                                    .setAutoCancel(true);

                    NotificationManager mNotifyMgr =
                            (NotificationManager) zContext.getSystemService(NOTIFICATION_SERVICE);

                    assert mNotifyMgr != null;
                    mNotifyMgr.notify(3, mBuilder.build());

                    Ringtone r = RingtoneManager.getRingtone(zContext, uri);
                    if (r != null)
                        r.play();

                    SharedPreferences.Editor editor = prefs.edit();
                    if (SettingsActivity.what.equalsIgnoreCase("ETH")) {
                        editor.putString("pref_title_def_max", "999999").apply();
                    }
                    if (SettingsActivity.what.equalsIgnoreCase("BTC")) {
                        editor.putString("pref_title_def_max_btc", "999999").apply();
                    }
                    if (SettingsActivity.what.equalsIgnoreCase("XRP")) {
                        editor.putString("pref_title_def_max_xrp", "999999").apply();
                    }

                    triggered = true;
                }
            }

            return result;
        }


        @Override
        protected synchronized void onPostExecute(String result) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.zContext);

            if ((!triggered) && (prefs.getBoolean("notifications_current", false))) {
                Toast.makeText(zContext, result, Toast.LENGTH_SHORT).show();
            }

            if (triggered) {
                Toast.makeText(this.zContext, resulto, Toast.LENGTH_LONG).show();
                triggered = false;
            }


            if (prefs.getBoolean("notifs_checkbox", false)) {
                AlarmEvents.to_notif = AlarmEvents.to_notif + result;
                AlarmEvents.to_notif = AlarmEvents.to_notif.replace(" = ", ":");
                AlarmEvents.to_notif = AlarmEvents.to_notif.replace("€", "€ ");
                AlarmEvents.to_notif = AlarmEvents.to_notif.replace("€€", "€");
                AlarmEvents.to_notif = AlarmEvents.to_notif.replace("€€€", "€");
                send_notif(AlarmEvents.to_notif);
            }

        }
    }


    @SuppressLint("StaticFieldLeak")
    public static class getIValue extends AsyncTask<String, Void, String> {
        private final Context zContext;


        public getIValue(Context context, String entry) {
            zContext = context;

        }


        @Override
        protected synchronized String doInBackground(String... strings) {

            URL url = null;
            String urlo = "no";
            String pre = "";


            switch (SettingsActivity.entry) {

                case "ETH":
                    urlo = "https://min-api.cryptocompare.com/data/price?fsym=ETH&tsyms=EUR";
                    pre = "1ETH = ";
                    break;

                case "BTC":
                    urlo = "https://min-api.cryptocompare.com/data/price?fsym=BTC&tsyms=EUR";
                    pre = "1BTC = ";
                    break;

                case "XRP":
                    urlo = "https://min-api.cryptocompare.com/data/price?fsym=XRP&tsyms=EUR";
                    pre = "1XRP = ";
                    break;

            }

            try {
                url = new URL(urlo);
            } catch (MalformedURLException ignored) {

            }

            HttpURLConnection connection = null;
            try {
                assert url != null;
                connection = (HttpURLConnection) url.openConnection();
            } catch (IOException ignored) {

            }
            assert connection != null;
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            try {
                connection.setRequestMethod("GET");
            } catch (ProtocolException ignored) {

            }
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            int responceCode = 0;
            try {
                responceCode = connection.getResponseCode();
            } catch (IOException ignored) {

            }

            if (responceCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } catch (IOException ignored) {

                }
                try {
                    assert br != null;
                    while ((line = br.readLine()) != null) {
                        result = "";// String variable declared global
                        result += line;
                        Log.i("response_line", result);
                    }
                } catch (IOException ignored) {

                }
            } else {
                result = "no";
            }


            result = result.replace("{\"EUR\":", "");
            result = result.replace("}", "");
            result = pre + result + "€";

            return result;
        }


        @Override
        protected synchronized void onPostExecute(String result) {

            Toast.makeText(zContext, result, Toast.LENGTH_SHORT).show();

        }
    }

}
