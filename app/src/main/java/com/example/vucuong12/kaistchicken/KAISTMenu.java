package com.example.vucuong12.kaistchicken;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.vucuong12.kaistchicken.Services.NotiService;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by vucuong12 on 17. 5. 6.
 */

public class KAISTMenu {
    public abstract static class MyCallback{
         public abstract void callback(JSONObject result);
    }

    private static KAISTMenu mInstance;


    public static String TAG = "KAISTMenu";
    private Networking networking;

    //Last modified
    public Calendar lastModified;
    public Boolean firstRun = false;
    private Context appContext;
    //menu Array
    public JSONObject menuByDay;


    private KAISTMenu (Context context){
        appContext = context;
        networking = Networking.getInstance(appContext);
        lastModified =  Calendar.getInstance();
        firstRun = true;
        menuByDay = new JSONObject();
    }

    public static synchronized  KAISTMenu getInstance (Context context) {
        if (mInstance == null){
            mInstance = new KAISTMenu(context);
        }
        return mInstance;
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

    public void updateMenu(final KAISTMenu.MyCallback callback){
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