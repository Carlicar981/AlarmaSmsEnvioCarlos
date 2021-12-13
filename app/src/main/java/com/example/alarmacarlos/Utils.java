package com.example.alarmacarlos;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import static android.content.Context.ALARM_SERVICE;

import java.util.Calendar;
import java.util.Date;

public class Utils {

    public static void setAlarm(int i, long timestamp, Context ctx) {
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(ctx, AlarmReceiver.class);
        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getBroadcast(ctx, i, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        alarmIntent.setData((Uri.parse("custom://" + System.currentTimeMillis())));
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis()+(timestamp - System.currentTimeMillis()), pendingIntent);
        System.out.println("Metodo_SetAlarm "+timestamp);
        /*
        alarmIntent.setData((Uri.parse("custom://" + Calendar.getInstance())));
        alarmManager.set(AlarmManager.RTC_WAKEUP,timestamp.getTime() , pendingIntent);
        System.out.println(timestamp.getTime());
         */
    }
}
