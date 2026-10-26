package com.noor.launcher;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import java.util.HashSet;
import java.util.List;

/**
 * خدمة الحماية:
 *  1) بتعترض زر Home وبتفتح اللانشر بدل الواجهة الرسمية (ضغطتين سريعتين = الواجهة الرسمية).
 *  2) لو الواجهة الرسمية ظهرت لوحدها بتحوّلها للانشر.
 *  3) بتنفّذ إطفاء الشاشة الحقيقي (GLOBAL_ACTION_LOCK_SCREEN).
 * مش بتقرأ محتوى الشاشة نهائياً (canRetrieveWindowContent=false) ومفيش أي اتصال بالإنترنت.
 */
public class GuardService extends AccessibilityService {
    static volatile GuardService inst;

    private final HashSet<String> stock = new HashSet<>();
    private long lastHome, escapeUntil, lastRedirect;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        inst = this;
        refreshHomes();
        Startup.onServiceUp(getApplicationContext());
    }

    private void refreshHomes() {
        stock.clear();
        try {
            Intent i = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME);
            List<ResolveInfo> l = getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo r : l) {
                if (r.activityInfo != null && !r.activityInfo.packageName.equals(getPackageName())) {
                    stock.add(r.activityInfo.packageName);
                }
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent e) {
        if (e == null || e.getEventType() != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return;
        if (!Pref.b(Pref.GUARD_HOME, true)) return;
        CharSequence p = e.getPackageName();
        if (p == null) return;
        long now = SystemClock.elapsedRealtime();
        if (now < escapeUntil || now - lastRedirect < 900) return;
        if (stock.contains(p.toString())) {
            lastRedirect = now;
            redirect();
        }
    }

    private void redirect() {
        String auto = Pref.s(Pref.AUTO_APP, "");
        if (Startup.recent() && !auto.isEmpty() && Startup.launchApp(this, auto)) return;
        Startup.bringLauncher(this);
    }

    @Override
    protected boolean onKeyEvent(KeyEvent ev) {
        if (ev.getKeyCode() != KeyEvent.KEYCODE_HOME || !Pref.b(Pref.GUARD_HOME, true)) return false;
        long now = SystemClock.elapsedRealtime();
        if (now < escapeUntil) return false; // مخرج الطوارئ: سيب النظام يتصرف
        if (ev.getAction() == KeyEvent.ACTION_DOWN) {
            if (lastHome > 0 && now - lastHome < 700) {
                escapeUntil = now + 10000; // ضغطتين سريعتين = الواجهة الرسمية
                lastHome = 0;
                return false;
            }
            lastHome = now;
            final MainActivity a = Startup.act;
            if (a != null && Startup.resumed) a.runOnUiThread(a::homePressed);
            else Startup.bringLauncher(this);
        }
        return true;
    }

    /** إطفاء الشاشة فعلياً. */
    boolean sleepNow() {
        try {
            return performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN);
        } catch (Throwable t) {
            return false;
        }
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public boolean onUnbind(Intent i) {
        inst = null;
        return super.onUnbind(i);
    }

    @Override
    public void onDestroy() {
        inst = null;
        super.onDestroy();
    }
}
