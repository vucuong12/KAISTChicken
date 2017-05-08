package com.example.vucuong12.kaistchicken.Services;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.vucuong12.kaistchicken.KAISTMenu;
import com.example.vucuong12.kaistchicken.MainActivity;
import com.example.vucuong12.kaistchicken.Networking;
import com.example.vucuong12.kaistchicken.R;
import com.example.vucuong12.kaistchicken.WifiListener;
import com.example.vucuong12.kaistchicken.AlarmReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


/**
 * Created by vucuong12 on 17. 5. 5.
 */

public class NotiService extends Service {
    private static final String TAG = "NotiService";
    private Networking networking;
    public WifiListener wifiListener = null;
    KAISTMenu menu = null;







    @Override
    public void onCreate() {
        super.onCreate();
        Runnable r = new Runnable() {
            public void run() {
                mainJob();
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        wifiListener.myContext.unregisterReceiver(wifiListener.mReceiver);
        super.onDestroy();
    }

    void actionOnWifiConnection(){
        menu.updateMenu(new KAISTMenu.MyCallback() {
            @Override
            public void callback(JSONObject result) {
                menu.lastModified = Calendar.getInstance();
                menu.firstRun = false;
                try {
                    menu.menuByDay = new JSONObject(result.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, result.toString());
            }
        });
    }

    void actionOnWifiDisconnection(){

    }


    public void mainJob(){
        Log.d(TAG, "Run mainJob inside service");

        menu = KAISTMenu.getInstance(this.getApplicationContext());

        //1. Create wifi listener
        wifiListener = new WifiListener(this){
            @Override
            public void WifiConnectionHandler(String connectionType, String MAC, String IP) {
                if (connectionType.equals("WIFI_CONNECTED")){
                    Log.d(TAG, "WIFI_CONNECTED with MAC " + MAC + " and IP "  + IP);
                    actionOnWifiConnection();
                } else {
                    Log.d(TAG, "WIFI_DISCONNECTED with MAC " + MAC + " and IP "  + IP);
                    actionOnWifiDisconnection();
                }
            }
        };
        wifiListener.receive();

        //2. Schedule notifications
        //Lunch: 10:30am
        //Dinner: 4:30pm
        //displayNoti();
        menu.updateMenu(new KAISTMenu.MyCallback() {
            @Override
            public void callback(JSONObject result) {
                menu.lastModified = Calendar.getInstance();
                menu.firstRun = false;
                try {
                    menu.menuByDay = new JSONObject(result.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                triggerTimer();
                Log.d(TAG, result.toString());
            }
        });



    }

    void triggerTimer(){



        Calendar now = Calendar.getInstance();
        String dayName = now.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
        Log.d(TAG, dayName);
        JSONObject menuByDay = menu.menuByDay;
        String hasLunchChicken = "", hasDinnerChicken = "";
        Log.d(TAG, "--> " + menuByDay.toString());
        try {
            hasLunchChicken = menuByDay.getJSONObject(dayName).getString("lunch");
            hasDinnerChicken = menuByDay.getJSONObject(dayName).getString("dinner");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        int oneDayTime = 24 * 60 * 60 * 1000;
        //int oneDayTime = 4000;
        // Set the alarm to start at 8:30 a.m.
        Calendar lunchTime = Calendar.getInstance();
        lunchTime.setTimeInMillis(System.currentTimeMillis());
        lunchTime.set(Calendar.HOUR_OF_DAY, 10);
        lunchTime.set(Calendar.MINUTE, 36);

        Calendar dinnerTime = Calendar.getInstance();
        dinnerTime.setTimeInMillis(System.currentTimeMillis());
        dinnerTime.set(Calendar.HOUR_OF_DAY, 16);
        dinnerTime.set(Calendar.MINUTE, 30);

        //1. Lunch
        Intent myIntent1 = new Intent(this, AlarmReceiver.class);
        myIntent1.putExtra("mealType", "Lunch");
        myIntent1.putExtra("hasChicken", hasLunchChicken);
        PendingIntent alarmIntent1 = PendingIntent.getBroadcast(this, 0, myIntent1, 0);
        AlarmManager alarmMgr1 = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        alarmMgr1.setRepeating(AlarmManager.RTC_WAKEUP, lunchTime.getTimeInMillis(), oneDayTime, alarmIntent1);
        //2. Dinner
        Intent myIntent2 = new Intent(this, AlarmReceiver.class);
        myIntent2.putExtra("mealType", "Dinner");
        myIntent2.putExtra("hasChicken", hasDinnerChicken);
        PendingIntent alarmIntent2 = PendingIntent.getBroadcast(this, 1, myIntent2, 0);
        AlarmManager alarmMgr2 = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);

        alarmMgr2.setRepeating(AlarmManager.RTC_WAKEUP, dinnerTime.getTimeInMillis(), oneDayTime, alarmIntent2);
    }






}

