package com.tikjuti.bai2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

public class MyBroadcastReceiver extends BroadcastReceiver {
    Database database;

    @Override
    public void onReceive(Context context, Intent intent) {

        database = MainNote.database;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder nbuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "reminder";
            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
            if (channel == null)
                notificationManager.createNotificationChannel(new NotificationChannel(channelId, "Ghi chú", NotificationManager.IMPORTANCE_DEFAULT));
            nbuilder = new Notification.Builder(context, channelId);
        } else {
            nbuilder = new Notification.Builder(context);
        }


        SQLiteDatabase db = database.getReadableDatabase();
        Cursor dataById = db.query("Notes", null, null, null, null, null, null);
        Long reminder = 0L;
        String title = "";
        while (dataById.moveToNext()) {
            title = dataById.getString(1);
            reminder = dataById.getLong(5);
            if (reminder == Long.parseLong(intent.getData().getSchemeSpecificPart())) {
                nbuilder.setSmallIcon(R.mipmap.ic_launcher);
                nbuilder.setContentTitle("Ghi chú");
                nbuilder.setContentText(title);
                nbuilder.setPriority(Notification.PRIORITY_DEFAULT);
                notificationManager.notify(1, nbuilder.build());
                break;
            }
        }
    }
}
