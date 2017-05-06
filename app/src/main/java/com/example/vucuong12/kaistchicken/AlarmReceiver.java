package com.example.vucuong12.kaistchicken;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

/**
 * Created by vucuong12 on 17. 5. 6.
 */
public class AlarmReceiver extends BroadcastReceiver
{
    String TAG = "AlarmReceiver";
    Context mContext;
    KAISTMenu menu;

    @Override
    public void onReceive(Context context, Intent intent)
    {

        mContext = context;
        // Your code to execute when the alarm triggers
        // and the broadcast is received.
        menu = KAISTMenu.getInstance(context.getApplicationContext());
        String mealType = intent.getExtras().getString("mealType");
        String hasChicken = intent.getExtras().getString("hasChicken");
        Log.d(TAG, "Alarm is triggered " + mealType);

        displayNoti(mealType, hasChicken);

    }

    void displayNoti (String mealType, String hasChicken) {
        int notifyID = 1;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_weekend_white_24dp)
                        .setContentTitle(mealType + " at W2")
                        .setContentText("Has chicken: " + hasChicken);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(mContext, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(notifyID, mBuilder.build());
    }
}
