package com.noor.launcher;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;

import java.io.File;
import java.util.List;

/** مشغّل القرآن (ملفات الفولدر + الإذاعات). شغّال كخدمة أمامية عشان يكمّل والشاشة مطفية. */
public class PlayerService extends Service {
    static final String EXTRA_CMD = "cmd", EXTRA_ARG = "arg";

    private final Handler h = new Handler(Looper.getMainLooper());

    private void say(String m) {
        Player.error = m;
        android.util.Log.e("AbdouPlayer", m);
        try {
            android.widget.Toast.makeText(getApplicationContext(), m, android.widget.Toast.LENGTH_LONG).show();
        } catch (Throwable ignored) {
        }
        Player.notifyChanged();
    }
    private MediaPlayer mp;
    private boolean prepared, wantPlay, pausedByFocus, started;
    private List<File> list;
    private List<File> full;
    private int cur;
    private int retry;
    private String lastErr = "";
    private PowerManager.WakeLock wl;
    private WifiManager.WifiLock wifi;
    private AudioManager am;
    private AudioFocusRequest focusReq;

    private final Runnable progress = new Runnable() {
        @Override
        public void run() {
            try {
                if (mp != null && prepared) {
                    Player.pos = mp.getCurrentPosition();
                    Player.dur = Player.mode == Player.MODE_RADIO ? 0 : mp.getDuration();
                }
                if (Player.sleepAt > 0 && Clock.mono() >= Player.sleepAt) {
                    Player.sleepAt = 0;
                    stopAll();
                    return;
                }
            } catch (Exception ignored) {
            }
            Player.notifyChanged();
            if (Player.mode != Player.MODE_NONE) h.postDelayed(this, 1000);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AbdouLuncher:audio");
        wl.setReferenceCounted(false);
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm != null) {
            wifi = wm.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, "AbdouLuncher:radio");
            wifi.setReferenceCounted(false);
        }
        NotificationChannel ch = new NotificationChannel("play", "تشغيل القرآن", NotificationManager.IMPORTANCE_LOW);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(ch);
    }

    @Override
    public int onStartCommand(Intent in, int flags, int startId) {
        try {
            goForeground();
        } catch (Throwable t) {
            say("الإشعار فشل: " + t);
        }
        if (in != null) {
            try {
                handle(in.getIntExtra(EXTRA_CMD, 0), in.getIntExtra(EXTRA_ARG, 0));
            } catch (Throwable t) {
                Player.loading = false;
                say("تعذّر التشغيل: " + t);
            }
        }
        if (Player.mode == Player.MODE_NONE) {
            stopForeground(true);
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    private void goForeground() {
        Intent open = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(this, 0, open, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        Notification n = new Notification.Builder(this, "play")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Abdou-Luncher")
                .setContentText(Player.title == null || Player.title.isEmpty() ? "القرآن الكريم" : Player.title)
                .setContentIntent(pi)
                .setOngoing(true)
                .build();
        startForeground(7, n);
        started = true;
    }

    private void handle(int cmd, int arg) {
        switch (cmd) {
            case Player.CMD_TOGGLE:
                if (mp == null) {
                    startFolder(0);
                } else if (wantPlay) {
                    pause(false);
                } else {
                    resume();
                }
                break;
            case Player.CMD_TRACK: startFolder(arg); break;
            case Player.CMD_NEXT: step(1); break;
            case Player.CMD_PREV: step(-1); break;
            case Player.CMD_REPEAT: Pref.set(Pref.REPEAT, arg); break;
            case Player.CMD_STATION: startRadio(arg); break;
            case Player.CMD_SLEEP: Player.sleepAt = arg <= 0 ? 0 : Clock.mono() + arg * 60000L; Player.notifyChanged(); break;
            case Player.CMD_SEEK:
                if (mp != null && prepared && Player.mode == Player.MODE_FOLDER) mp.seekTo(arg);
                break;
            case Player.CMD_RELOAD: reload(); break;
            case Player.CMD_STOP: stopAll(); break;
            default: break;
        }
    }

    // ---------------- ملفات ----------------

    private void startFolder(int arg) {
        String folder = Pref.s(Pref.FOLDER, "");
        full = Media.scan(folder);
        list = Media.playlist(folder);
        if (list.isEmpty()) {
            say("مفيش ملفات صوت في الفولدر ده (" + Pref.s(Pref.FOLDER, "") + ")");
            return;
        }
        File f;
        if (arg > 0 && arg - 1 < full.size()) {
            f = full.get(arg - 1);
            if (!list.contains(f)) list = full;
        } else {
            String last = Pref.s(Pref.LAST_TRACK, "");
            f = list.get(0);
            for (File x : list) if (x.getName().equals(last)) f = x;
        }
        cur = list.indexOf(f);
        playFile();
    }

    private void reload() {
        if (Player.mode != Player.MODE_FOLDER) return;
        String folder = Pref.s(Pref.FOLDER, "");
        File now = (list != null && cur >= 0 && cur < list.size()) ? list.get(cur) : null;
        full = Media.scan(folder);
        List<File> nl = Media.playlist(folder);
        if (nl.isEmpty()) {
            stopAll();
            return;
        }
        list = nl;
        int i = now == null ? -1 : list.indexOf(now);
        if (i >= 0) {
            cur = i;
            publishFolder();
        } else {
            cur = Math.min(cur, list.size() - 1);
            playFile();
        }
    }

    private void step(int d) {
        if (Player.mode == Player.MODE_RADIO) {
            int n = Content.STATIONS.length;
            startRadio(((Math.max(0, Player.station) + d) % n + n) % n);
            return;
        }
        if (list == null || list.isEmpty()) {
            startFolder(0);
            return;
        }
        cur = ((cur + d) % list.size() + list.size()) % list.size();
        playFile();
    }

    private void playFile() {
        File f = list.get(cur);
        Pref.set(Pref.LAST_TRACK, f.getName());
        Player.mode = Player.MODE_FOLDER;
        Player.station = -1;
        Player.error = "";
        publishFolder();
        begin(f.getPath());
    }

    private void publishFolder() {
        File f = list.get(cur);
        Player.title = Media.nice(f.getName());
        Player.sub = Ui.folderName(Pref.s(Pref.FOLDER, "")) + "  •  " + Fmt.digits(cur + 1) + " / " + Fmt.digits(list.size());
        Player.count = list.size();
        int fi = full == null ? -1 : full.indexOf(f);
        Player.index = fi >= 0 ? fi : cur;
        Player.notifyChanged();
    }

    // ---------------- إذاعة ----------------

    private void startRadio(int i) {
        if (i < 0 || i >= Content.STATIONS.length) return;
        Player.mode = Player.MODE_RADIO;
        Player.station = i;
        Player.title = Content.STATIONS[i][0];
        Player.sub = "إذاعة القرآن الكريم  •  بث مباشر";
        Player.index = 0;
        Player.count = Content.STATIONS.length;
        Player.error = "";
        Pref.set(Pref.STATION, i);
        retry = 0;
        Player.notifyChanged();
        begin(Content.STATIONS[i][1]);
    }

    // ---------------- المشغّل ----------------

    private void begin(String src) {
        release();
        try {
            focus();
        } catch (Throwable ignored) {
        }
        try {
            mp = new MediaPlayer();
            mp.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build());
            mp.setOnPreparedListener(m -> {
                prepared = true;
                Player.loading = false;
                if (wantPlay && !pausedByFocus) m.start();
                Player.playing = m.isPlaying();
                Player.pos = 0;
                Player.dur = Player.mode == Player.MODE_RADIO ? 0 : m.getDuration();
                Player.notifyChanged();
            });
            mp.setOnCompletionListener(m -> onDone());
            mp.setOnErrorListener((m, w, e) -> {
                android.util.Log.e("AbdouPlayer", "MediaPlayer error " + w + "/" + e);
                lastErr = " (" + w + "/" + e + ")";
                onError();
                return true;
            });
            wantPlay = true;
            pausedByFocus = false;
            prepared = false;
            Player.loading = true;
            Player.playing = false;
            wl.acquire();
            if (Player.mode == Player.MODE_RADIO && wifi != null) wifi.acquire();
            mp.setDataSource(src);
            mp.prepareAsync();
            h.removeCallbacks(progress);
            h.post(progress);
        } catch (Throwable e) {
            lastErr = " (" + e + ")";
            onError();
        }
        Player.notifyChanged();
    }

    private void onDone() {
        if (Player.mode != Player.MODE_FOLDER || list == null) return;
        int r = Pref.i(Pref.REPEAT, 1);
        if (r == 2) {
            playFile();
        } else if (r == 1) {
            cur = (cur + 1) % list.size();
            playFile();
        } else if (cur + 1 < list.size()) {
            cur++;
            playFile();
        } else {
            wantPlay = false;
            Player.playing = false;
            Player.pos = 0;
            cur = 0;
            publishFolder();
            release();
            Player.mode = Player.MODE_FOLDER;
            Player.notifyChanged();
        }
    }

    private void onError() {
        Player.loading = false;
        Player.playing = false;
        if (Player.mode == Player.MODE_RADIO && retry < 2 && Player.station >= 0) {
            retry++;
            final int s = Player.station;
            Player.error = "جارٍ إعادة الاتصال…";
            Player.notifyChanged();
            h.postDelayed(() -> {
                if (Player.mode == Player.MODE_RADIO && Player.station == s) begin(Content.STATIONS[s][1]);
            }, 3000);
            return;
        }
        if (Player.mode == Player.MODE_FOLDER && list != null && list.size() > 1 && retry < list.size()) {
            retry++;
            cur = (cur + 1) % list.size();
            playFile();
            return;
        }
        release();
        say((Player.mode == Player.MODE_RADIO ? "الإذاعة مش شغالة، جرّب تانية" : "تعذّر تشغيل الملف") + lastErr);
    }

    private void pause(boolean byFocus) {
        wantPlay = false;
        pausedByFocus = byFocus;
        try {
            if (mp != null && prepared) mp.pause();
        } catch (Exception ignored) {
        }
        Player.playing = false;
        Player.notifyChanged();
    }

    private void resume() {
        wantPlay = true;
        pausedByFocus = false;
        focus();
        try {
            if (mp != null && prepared) mp.start();
            Player.playing = mp != null && prepared && mp.isPlaying();
        } catch (Exception ignored) {
        }
        h.removeCallbacks(progress);
        h.post(progress);
        Player.notifyChanged();
    }

    private boolean focus() {
        AudioManager.OnAudioFocusChangeListener l = c -> h.post(() -> {
            if (c == AudioManager.AUDIOFOCUS_LOSS || c == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                if (wantPlay) pause(true);
            } else if (c == AudioManager.AUDIOFOCUS_GAIN) {
                if (pausedByFocus) resume();
            }
        });
        focusReq = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build())
                .setOnAudioFocusChangeListener(l).build();
        return am.requestAudioFocus(focusReq) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private void release() {
        if (mp != null) {
            try {
                mp.release();
            } catch (Exception ignored) {
            }
            mp = null;
        }
        prepared = false;
        if (wl.isHeld()) wl.release();
        if (wifi != null && wifi.isHeld()) wifi.release();
    }

    private void stopAll() {
        h.removeCallbacks(progress);
        release();
        if (focusReq != null) am.abandonAudioFocusRequest(focusReq);
        wantPlay = false;
        Player.mode = Player.MODE_NONE;
        Player.playing = false;
        Player.loading = false;
        Player.station = -1;
        Player.pos = 0;
        Player.dur = 0;
        Player.sleepAt = 0;
        Player.title = "";
        Player.sub = "";
        Player.notifyChanged();
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        h.removeCallbacksAndMessages(null);
        release();
        Player.mode = Player.MODE_NONE;
        Player.playing = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
