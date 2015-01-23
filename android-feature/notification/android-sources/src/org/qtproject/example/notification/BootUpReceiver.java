
package org.qtproject.example.notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.io.*;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.SystemClock;

public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
           Intent i = new Intent(context, NotificationClient.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

            // 启动完成
                        Intent intent1 = new Intent(context, AlarmReceiver.class);
                        intent1.setAction("arui.alarm.action");
                        PendingIntent sender = PendingIntent.getBroadcast(context, 0,
                                intent1, 0);
                        long firstime = SystemClock.elapsedRealtime();
                        AlarmManager am = (AlarmManager) context
                                .getSystemService(Context.ALARM_SERVICE);

                        // 10秒一个周期，不停的发送广播
                        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime,
                                10 * 1000, sender);
        }
    }

}

