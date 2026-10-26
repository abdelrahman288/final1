package com.noor.launcher;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

public class MainActivity extends Activity implements Ui.Host {

    private Ui ui;
    private HomeView home;
    private boolean receiverOn;

    private final BroadcastReceiver pkgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            reloadApps();
        }
    };

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.BLACK));
        Fonts.init(f -> Typeface.createFromAsset(getAssets(), "fonts/" + f));
        ui = new Ui(this);
        try {
            ui.logo = BitmapFactory.decodeStream(getAssets().open("logo.png"));
        } catch (Exception ignored) {
        }
        home = new HomeView(this, ui);
        setContentView(home);
        home.requestFocus();
        Startup.act = this;
        reloadApps();
        if (Startup.consumeIntro()) runStartup();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!receiverOn) {
            IntentFilter f = new IntentFilter();
            f.addAction(Intent.ACTION_PACKAGE_ADDED);
            f.addAction(Intent.ACTION_PACKAGE_REMOVED);
            f.addAction(Intent.ACTION_PACKAGE_REPLACED);
            f.addDataScheme("package");
            registerReceiver(pkgReceiver, f);
            receiverOn = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (receiverOn) {
            unregisterReceiver(pkgReceiver);
            receiverOn = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Startup.act = this;
        Startup.resumed = true;
        Player.changed = () -> home.post(() -> {
            ui.tick();
            home.invalidate();
        });
        home.start();
        if (Startup.consumeIntro()) runStartup();
    }

    @Override
    protected void onPause() {
        Startup.resumed = false;
        Player.changed = null;
        home.stop();
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent i) {
        super.onNewIntent(i);
        homePressed();
    }

    @Override
    public void onBackPressed() {
        ui.back();
    }

    @Override
    protected void onDestroy() {
        if (Startup.act == this) Startup.act = null;
        super.onDestroy();
    }

    /** يتنادى لما الشاشة تفتح: إنترو + صوت البسملة. */
    void runStartup() {
        ui.goHome();
        ui.restartEntrance(Pref.b(Pref.INTRO, true));
        ui.kick();
        Startup.playBismillah(getApplicationContext());
    }

    void homePressed() {
        if (Startup.introPending) return;
        ui.goHome();
        ui.restartEntrance(false);
        ui.kick();
    }

    private void reloadApps() {
        final Context app = getApplicationContext();
        new Thread(() -> {
            AppRepo.load(app);
            runOnUiThread(() -> ui.onAppsLoaded());
        }).start();
    }

    // ---------------- Host ----------------

    @Override
    public void launch(AppItem a) {
        try {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setComponent(a.cn);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(i);
        } catch (Exception e) {
            toast("تعذّر فتح التطبيق");
        }
    }

    @Override
    public void openSystem(String action) {
        try {
            startActivity(new Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (Exception e) {
            try {
                startActivity(new Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } catch (Exception e2) {
                toast("الإعدادات غير متاحة");
            }
        }
    }

    @Override
    public void appInfo(String pkg) {
        try {
            startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + pkg))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (Exception e) {
            toast("تعذّر فتح معلومات التطبيق");
        }
    }

    @Override
    public boolean online() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            Network n = cm.getActiveNetwork();
            if (n == null) return false;
            NetworkCapabilities nc = cm.getNetworkCapabilities(n);
            return nc != null && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void redraw() {
        if (home != null) home.postInvalidateOnAnimation();
    }

    @Override
    public String deviceInfo() {
        return Build.MANUFACTURER + " " + Build.MODEL + " (Android " + Build.VERSION.RELEASE + ")";
    }

    // ---------------- المشغّل ----------------

    @Override
    public void player(int cmd, int arg) {
        if (cmd == Player.CMD_STATION && !online()) {
            toast("الإذاعة محتاجة إنترنت");
            return;
        }
        if (cmd == Player.CMD_TRACK && !hasStorage()) {
            toast("اسمح بالوصول للملفات الأول");
            return;
        }
        if (cmd == Player.CMD_STOP && !Player.active()) return;
        if (cmd == Player.CMD_RELOAD && !Player.active()) return;
        Intent i = new Intent(this, PlayerService.class).putExtra(PlayerService.EXTRA_CMD, cmd).putExtra(PlayerService.EXTRA_ARG, arg);
        try {
            startForegroundService(i);
        } catch (Exception e) {
            toast("تعذّر تشغيل خدمة الصوت");
        }
    }

    // ---------------- إطفاء الشاشة الحقيقي ----------------

    @Override
    public void screenOff() {
        GuardService g = GuardService.inst;
        if (g == null) {
            toast("فعّل خدمة الحماية الأول (الإعدادات ← خدمة الحماية)");
            return;
        }
        Player.screenOffByUser = true;
        if (!g.sleepNow()) {
            Player.screenOffByUser = false;
            toast("التلفزيون رفض أمر إطفاء الشاشة");
        }
    }

    // ---------------- التخزين ----------------

    @Override
    public boolean hasStorage() {
        return checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void requestStorage() {
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 11);
        // لو نافذة السماح مش بتظهر على التلفزيون، افتح معلومات التطبيق
        home.postDelayed(() -> {
            if (!hasStorage()) appInfo(getPackageName());
        }, 1800);
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] p, int[] r) {
        super.onRequestPermissionsResult(code, p, r);
        ui.buildQuran();
        ui.kick();
    }

    // ---------------- خدمة الحماية ----------------

    private ComponentName guardCn() {
        return new ComponentName(this, GuardService.class);
    }

    @Override
    public boolean guardOn() {
        if (GuardService.inst != null) return true;
        try {
            String s = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            return s != null && s.contains(guardCn().flattenToString());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void enableGuard() {
        String id = guardCn().flattenToString();
        try {
            if (checkSelfPermission("android.permission.WRITE_SECURE_SETTINGS") == PackageManager.PERMISSION_GRANTED) {
                String s = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
                if (s == null) s = "";
                if (!s.contains(id)) s = s.isEmpty() ? id : s + ":" + id;
                Settings.Secure.putString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, s);
                Settings.Secure.putInt(getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 1);
                toast("تم تفعيل خدمة الحماية");
                return;
            }
        } catch (Exception ignored) {
        }
        try {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            toast("فعّل «Abdou-Luncher» من القائمة");
        } catch (Exception e) {
            toast("الشاشة دي ملهاش قائمة إمكانية الوصول، استخدم أمر ADB من الشرح");
        }
    }
}
