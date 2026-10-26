package com.noor.launcher;

/** حالة المشغّل (بتتحدّث من PlayerService وبتتقرأ من الواجهة). */
final class Player {
    private Player() {}

    static final int CMD_TOGGLE = 1, CMD_NEXT = 2, CMD_PREV = 3, CMD_TRACK = 4, CMD_REPEAT = 5, CMD_STOP = 6,
            CMD_STATION = 7, CMD_SLEEP = 8, CMD_SEEK = 9, CMD_RELOAD = 10;

    static final int MODE_NONE = 0, MODE_FOLDER = 1, MODE_RADIO = 2;

    static volatile int mode = MODE_NONE;
    static volatile boolean playing, loading;
    static volatile String title = "", sub = "", error = "";
    static volatile int index, count, station = -1;
    static volatile long pos, dur;
    static volatile long sleepAt;  // وقت إيقاف المؤقت (Clock.mono)
    static volatile boolean screenOffByUser; // الشاشة اتطفت من زرار التطبيق والصوت شغال

    static volatile Runnable changed; // الواجهة بتسجّل نفسها هنا عشان تتحدّث فوراً
    static void notifyChanged() { Runnable r = changed; if (r != null) r.run(); }

    static int repeat() { return Pref.i(Pref.REPEAT, 1); }

    static boolean active() { return mode != MODE_NONE; }

    static String repeatName(int r) {
        return r == 0 ? "بالتتابع" : (r == 1 ? "تكرار الكل" : "تكرار واحدة");
    }
}
