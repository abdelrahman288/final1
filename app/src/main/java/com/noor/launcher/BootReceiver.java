package com.noor.launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** بيشغّل العملية عند تشغيل التلفزيون (ويفتح اللانشر/التطبيق التلقائي). */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context c, Intent i) {
        Startup.onBoot(c.getApplicationContext());
    }
}
