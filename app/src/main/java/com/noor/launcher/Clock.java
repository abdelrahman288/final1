package com.noor.launcher;

/**
 * الوقت: كله بيتحسب من ساعة الشاشة نفسها (من غير إنترنت)، مع إمكانية ضبط يدوي دقيق.
 * مفيش أي اتصال بالشبكة هنا نهائياً.
 */
final class Clock {
    private Clock() {}

    /** أقدم وقت منطقي (24 أكتوبر 2026). لو الساعة أقدم من كده يبقى الشاشة نسيت الوقت (فصل كهرباء). */
    static final long MIN_VALID = 1792800000000L;

    static long override = -1;     // للاختبار فقط
    static long offsetMs;          // الضبط اليدوي
    static boolean autoSync;       // الشاشة ضابطة وقتها تلقائياً من الشبكة حالياً → نتجاهل الضبط اليدوي

    static void load() {
        offsetMs = Pref.l(Pref.CLOCK_OFF, 0);
    }

    static long now() {
        if (override >= 0) return override;
        return System.currentTimeMillis() + (autoSync ? 0 : offsetMs);
    }

    static long mono() {
        return System.nanoTime() / 1000000L;
    }

    /** 0 سليمة، 1 أقدم من تاريخ بناء التطبيق، 2 رجعت لورا عن آخر مرة */
    static int status() {
        long n = now();
        if (n < MIN_VALID) return 1;
        long last = Pref.l(Pref.LAST_GOOD, 0);
        if (last > 0 && n < last - 10 * 60000L) return 2;
        return 0;
    }

    /** يتنادى كل دقيقة تقريباً: يسجّل آخر وقت سليم (بكتابة نادرة عشان الفلاش). */
    static void remember() {
        if (status() != 0) return;
        long n = now();
        long last = Pref.l(Pref.LAST_GOOD, 0);
        if (n - last > 15 * 60000L || n < last) Pref.set(Pref.LAST_GOOD, n);
    }

    /** اضبط الساعة على وقت معيّن (بالـ ms UTC). */
    static void setTo(long target) {
        offsetMs = target - System.currentTimeMillis();
        Pref.set(Pref.CLOCK_OFF, offsetMs);
        Pref.set(Pref.LAST_GOOD, target);
    }

    static void reset() {
        offsetMs = 0;
        Pref.set(Pref.CLOCK_OFF, 0L);
        Pref.set(Pref.LAST_GOOD, 0L);
    }
}
