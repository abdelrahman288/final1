package com.noor.launcher;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

public class NoorApp extends Application {

    /** تخزين الإعدادات في SharedPreferences. */
    static final class SpStore implements Pref.Store {
        private final SharedPreferences sp;

        SpStore(Context c) {
            sp = c.getSharedPreferences("abdou", Context.MODE_PRIVATE);
        }

        public int getInt(String k, int d) {
            try {
                return sp.getInt(k, d);
            } catch (Exception e) {
                return d;
            }
        }

        public long getLong(String k, long d) {
            try {
                return sp.getLong(k, d);
            } catch (Exception e) {
                return d;
            }
        }

        public String getString(String k, String d) {
            try {
                return sp.getString(k, d);
            } catch (Exception e) {
                return d;
            }
        }

        public boolean getBool(String k, boolean d) {
            try {
                return sp.getBoolean(k, d);
            } catch (Exception e) {
                return d;
            }
        }

        public void put(String k, Object v) {
            SharedPreferences.Editor ed = sp.edit();
            if (v instanceof Integer) ed.putInt(k, (Integer) v);
            else if (v instanceof Long) ed.putLong(k, (Long) v);
            else if (v instanceof Boolean) ed.putBoolean(k, (Boolean) v);
            else ed.putString(k, String.valueOf(v));
            ed.apply();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Pref.use(new SpStore(this));
        Clock.load();
        // لازم يتسجّل ديناميكياً (ACTION_SCREEN_ON مش بيشتغل من الـ manifest)
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                Startup.onScreenOn(c.getApplicationContext());
            }
        }, new IntentFilter(Intent.ACTION_SCREEN_ON));
    }
}
