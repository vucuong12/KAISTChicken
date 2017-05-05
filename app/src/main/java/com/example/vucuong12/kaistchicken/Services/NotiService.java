package com.example.vucuong12.kaistchicken.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.vucuong12.kaistchicken.Networking;
import com.example.vucuong12.kaistchicken.WifiListener;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;


/**
 * Created by vucuong12 on 17. 5. 5.
 */

public class NotiService extends Service {
    private static final String TAG = "NotiService";
    private Networking networking;
    public WifiListener wifiListener = null;
    KAISTMenu menu = null;

    abstract class MyCallback{
        abstract void callback(JSONObject result);
    }

    public class KAISTMenu {
        //Last modified
        Calendar lastModified;
        Boolean firstRun = false;
        //menu Array

        public KAISTMenu (){
            lastModified =  Calendar.getInstance();
            firstRun = true;
        }





//        public String getLastModified(){
//            int thisYear = lastModified.get(Calendar.YEAR);
//
//
//            int thisMonth = lastModified.get(Calendar.MONTH) + 1;
//
//
//            int thisDay = lastModified.get(Calendar.DAY_OF_MONTH) + 1;
//
//
//        }

        public void updateMenu(final MyCallback callback){
            //Send reservation
            if (!firstRun && lastModified.compareTo(Calendar.getInstance()) < 0) {
                Log.d(TAG, "No need to update");
                return ;
            }
            String URL = "http://52.41.100.153:3000";
            Log.d(TAG, "Send Post request to " + URL);
            HashMap<String, String> params = new HashMap<String, String>();
            JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            callback.callback(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("POST ERROR", String.valueOf(error.getClass()));
                            Log.d("POST ERROR", error.toString());
                        }
                    });

            req.setRetryPolicy(new DefaultRetryPolicy(
                    100000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            networking.addToRequestQueue(req);
        }

    }
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
        menu.updateMenu(new MyCallback() {
            @Override
            void callback(JSONObject result) {
                menu.lastModified = Calendar.getInstance();
                menu.firstRun = false;
                Log.d(TAG, result.toString());
            }
        });
    }

    void actionOnWifiDisconnection(){

    }


    public void mainJob(){
        networking = Networking.getInstance(this.getApplicationContext());
        menu = new KAISTMenu();


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

    }


}

