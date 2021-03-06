package com.example.vucuong12.kaistchicken;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.example.vucuong12.kaistchicken.Services.NotiService;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    String TAG = "MainActivity";
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        displayMenu();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent intent = new Intent(this, NotiService.class);
        Log.d(TAG, "Trigger service");


        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void displayMenu(){
        Calendar now = Calendar.getInstance();
        int day = now.get(Calendar.DATE);
        int month = now.get(Calendar.MONTH) + 1;
        int year = now.get(Calendar.YEAR) + 1;
        if (day < 10) day = day + 10;
        if (month < 10) month = month + 10;
        String dayString = String.valueOf(day);
        String monthString = String.valueOf(month);
        String yearString = String.valueOf(year);

        WebView myWebView = (WebView) findViewById(R.id.webview);
        String url = "http://www.kaist.ac.kr/_prog/fodlst/index.php?site_dvs_cd=en&menu_dvs_cd=050303&dvs_cd=fclt&dvs_cd=west&stt_dt=" + yearString + "-" + monthString + "-" + dayString +"&site_dvs=";
        myWebView.loadUrl(url);
    }


}
