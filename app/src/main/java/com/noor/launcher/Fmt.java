package com.noor.launcher;

import java.time.LocalDate;
import java.time.chrono.HijrahDate;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.TimeZone;

/** تنسيق الأرقام والتواريخ بالعربي. */
final class Fmt {
    private Fmt() {}

    static final String[] DAYS = {"الأحد", "الاثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت"};
    static final String[] GREG = {"يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو", "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"};
    static final String[] HIJRI = {"محرم", "صفر", "ربيع الأول", "ربيع الآخر", "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان", "رمضان", "شوال", "ذو القعدة", "ذو الحجة"};

    static String digits(String s) {
        StringBuilder b = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            b.append(c >= '0' && c <= '9' ? (char) ('٠' + (c - '0')) : c);
        }
        return b.toString();
    }

    static String digits(long n) {
        return digits(Long.toString(n));
    }

    /** "٣:٥١" (نظام 12 ساعة) */
    static String hm(long ms, TimeZone tz) {
        Calendar c = Calendar.getInstance(tz);
        c.setTimeInMillis(ms);
        int h = c.get(Calendar.HOUR);
        if (h == 0) h = 12;
        int m = c.get(Calendar.MINUTE);
        return digits(h + ":" + (m < 10 ? "0" : "") + m);
    }

    static String ampm(long ms, TimeZone tz) {
        Calendar c = Calendar.getInstance(tz);
        c.setTimeInMillis(ms);
        return c.get(Calendar.AM_PM) == Calendar.AM ? "ص" : "م";
    }

    /** "١:٤٣:١٥" */
    static String countdown(long ms) {
        if (ms < 0) ms = 0;
        long s = ms / 1000;
        long h = s / 3600, m = (s % 3600) / 60, sec = s % 60;
        return digits(h + ":" + (m < 10 ? "0" : "") + m + ":" + (sec < 10 ? "0" : "") + sec);
    }

    /** "الخميس ٢٢ أكتوبر ٢٠٢٦" */
    static String gregorian(Calendar c) {
        return DAYS[c.get(Calendar.DAY_OF_WEEK) - 1] + " " + digits(c.get(Calendar.DAY_OF_MONTH)) + " "
                + GREG[c.get(Calendar.MONTH)] + " " + digits(c.get(Calendar.YEAR));
    }

    /** {يوم, شهر(1-12), سنة} بالتقويم الهجري (أم القرى) مع إزاحة بالأيام. */
    static int[] hijri(int y, int m, int d, int adjustDays) {
        try {
            LocalDate ld = LocalDate.of(y, m, d).plusDays(adjustDays);
            HijrahDate h = HijrahDate.from(ld);
            return new int[]{h.get(ChronoField.DAY_OF_MONTH), h.get(ChronoField.MONTH_OF_YEAR), h.get(ChronoField.YEAR)};
        } catch (Exception e) {
            return new int[]{1, 1, 1447};
        }
    }

    static String hijriText(int[] h, String city) {
        return digits(h[0]) + " " + HIJRI[h[1] - 1] + " " + digits(h[2]) + " هـ" + (city == null ? "" : "  •  " + city);
    }

    /** "٠٣:٤٥" من ms */
    static String mmss(long ms) {
        if (ms < 0) ms = 0;
        long s = ms / 1000;
        long h = s / 3600, m = (s % 3600) / 60, sec = s % 60;
        String r = (h > 0 ? h + ":" + (m < 10 ? "0" : "") : "") + m + ":" + (sec < 10 ? "0" : "") + sec;
        return digits(r);
    }

    /** "١ ساعة و٤٣ دقيقة" (هادي، من غير ثواني) */
    static String human(long ms) {
        if (ms < 0) ms = 0;
        long mins = (ms + 59999) / 60000;
        long h = mins / 60, m = mins % 60;
        if (h == 0) return digits(m) + (m == 1 ? " دقيقة" : (m == 2 ? " دقيقتين" : (m <= 10 ? " دقائق" : " دقيقة")));
        String hs = h == 1 ? "ساعة" : (h == 2 ? "ساعتين" : digits(h) + (h <= 10 ? " ساعات" : " ساعة"));
        if (m == 0) return hs;
        String ms2 = m == 1 ? "دقيقة" : (m == 2 ? "دقيقتين" : digits(m) + (m <= 10 ? " دقائق" : " دقيقة"));
        return hs + " و" + ms2;
    }
}
