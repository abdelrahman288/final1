package com.noor.launcher;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import java.io.File;

/** كل اللي بيحصل لما الشاشة تفتح: فتح اللانشر (أو التطبيق التلقائي) + الإنترو + صوت البسملة (مرة واحدة وبس هنا). */
final class Startup {
    private Startup() {}

    static final long WINDOW_MS = 25000;
    static volatile MainActivity act;
    static volatile boolean resumed;
    static volatile boolean introPending;
    static volatile long screenOnAt = -WINDOW_MS * 2;
    private static boolean bootDone;
    private static MediaPlayer bism;
    private static final Handler H = new Handler(Looper.getMainLooper());

    static boolean recent() {
        return SystemClock.elapsedRealtime() - screenOnAt < WINDOW_MS;
    }

    static void onBoot(Context c) {
        if (bootDone) return;
        bootDone = true;
        onScreenOn(c);
    }

    /** من الـ accessibility service بعد الإقلاع لو الـ BOOT_COMPLETED ما وصلش. */
    static void onServiceUp(Context c) {
        if (!bootDone && SystemClock.elapsedRealtime() < 120000) onBoot(c);
        else bootDone = true;
    }

    static void onScreenOn(final Context c) {
        // الشاشة اتفتحت بعد ما اتطفت من زرار التطبيق: الصوت شغّال، ما نعملش أي إنترو ولا صوت
        if (Player.screenOffByUser) {
            Player.screenOffByUser = false;
            return;
        }
        screenOnAt = SystemClock.elapsedRealtime();
        String auto = Pref.s(Pref.AUTO_APP, "");
        if (!auto.isEmpty() && launchApp(c, auto)) {
            introPending = false; // اللانشر مش هيتفتح: مفيش صوت
            return;
        }
        introPending = true;
        final MainActivity a = act;
        if (a != null && resumed) {
            a.runOnUiThread(a::runStartup);
        } else {
            bringLauncher(c);
            H.postDelayed(() -> {
                if (introPending && !resumed) bringLauncher(c);
            }, 1500);
        }
    }

    static boolean consumeIntro() {
        boolean v = introPending && recent();
        introPending = false;
        return v;
    }

    static void bringLauncher(Context c) {
        try {
            Intent i = new Intent(c, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            c.startActivity(i);
        } catch (Throwable ignored) {
        }
    }

    static boolean launchApp(Context c, String pkg) {
        try {
            Intent i = c.getPackageManager().getLeanbackLaunchIntentForPackage(pkg);
            if (i == null) i = c.getPackageManager().getLaunchIntentForPackage(pkg);
            if (i == null) return false;
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            c.startActivity(i);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    // ---------------- صوت البسملة ----------------

    /** بيتنادى فقط من runStartup (فتح الشاشة). مفيش صوت لو فيه قرآن شغّال. */
    static void playBismillah(Context c) {
        if (!Pref.b(Pref.SOUND, true)) return;
        if (Player.playing || Player.loading) return;
        stopBismillah();
        try {
            MediaPlayer m = new MediaPlayer();
            m.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).setUsage(AudioAttributes.USAGE_MEDIA).build());
            boolean ok = false;
            try {
                AssetFileDescriptor fd = c.getAssets().openFd("bismillah.mp3");
                m.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
                fd.close();
                ok = true;
            } catch (Exception ignored) {
            }
            if (!ok) {
                File f = new File("/sdcard/Abdou-Luncher/bismillah.mp3");
                if (f.isFile() && f.length() > 1000) {
                    m.setDataSource(f.getPath());
                    ok = true;
                }
            }
            if (!ok) {
                m.release();
                return;
            }
            m.setOnPreparedListener(MediaPlayer::start);
            m.setOnCompletionListener(x -> stopBismillah());
            m.setOnErrorListener((x, w, e) -> {
                stopBismillah();
                return true;
            });
            bism = m;
            m.prepareAsync();
        } catch (Throwable t) {
            stopBismillah();
        }
    }

    static void stopBismillah() {
        MediaPlayer m = bism;
        bism = null;
        if (m != null) {
            try {
                m.release();
            } catch (Exception ignored) {
            }
        }
    }
}
