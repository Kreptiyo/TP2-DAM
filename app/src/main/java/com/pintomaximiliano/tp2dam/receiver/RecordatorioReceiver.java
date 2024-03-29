package com.pintomaximiliano.tp2dam.receiver;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.pintomaximiliano.tp2dam.R;

public class RecordatorioReceiver extends BroadcastReceiver {
    public static String accionRecordatorio = "RECORDATORIO";

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(accionRecordatorio)){
            PendingIntent pendingIntent = PendingIntent.getService(context, 1, intent,PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, String.valueOf(R.string.id_canal))
                    .setSmallIcon(android.R.drawable.star_on)
                    .setContentTitle("RECORDATORIO " + intent.getStringExtra("FECHA"))
                    .setContentText(intent.getStringExtra("DESCRIPCION"))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            Notification n = mBuilder.build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(99, n);
        }
    }
}
