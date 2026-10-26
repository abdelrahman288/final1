package com.noor.launcher;

import java.util.Calendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * مدينة + قاعدة التوقيت الصيفي مكتوبة جوه التطبيق نفسه (SimpleTimeZone)،
 * يعني مش معتمدين على بيانات المناطق الزمنية اللي في الشاشة (ممكن تكون قديمة وتغلّط ساعة كاملة).
 */
final class City {
    static final int EGYPT = 0, PLUS3 = 1, PLUS4 = 2, LEBANON = 3;
    static final String DEFAULT = "سوهاج";

    final String name;
    final double lat, lng;
    final int tzKind;
    final int method; // 0 الهيئة المصرية، 1 أم القرى
    private TimeZone tz;
    private boolean tzDst = true;

    City(String name, double lat, double lng, int tzKind, int method) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.tzKind = tzKind;
        this.method = method;
    }

    TimeZone tz() {
        boolean d = Pref.b(Pref.DST, true);
        if (tz == null || tzDst != d) {
            tz = make(tzKind, d);
            tzDst = d;
        }
        return tz;
    }

    private static TimeZone make(int kind, boolean dst) {
        final int H = 3600000;
        switch (kind) {
            case EGYPT:
                if (!dst) return new SimpleTimeZone(2 * H, "EG");
                // مصر: UTC+2، والصيفي +1 من آخر جمعة في أبريل 00:00 لحد آخر خميس في أكتوبر 24:00
                return new SimpleTimeZone(2 * H, "EG", Calendar.APRIL, -1, Calendar.FRIDAY, 0,
                        Calendar.OCTOBER, -1, Calendar.THURSDAY, 24 * H, H);
            case LEBANON:
                if (!dst) return new SimpleTimeZone(2 * H, "LB");
                // لبنان: UTC+2، والصيفي من آخر أحد في مارس 00:00 لآخر أحد في أكتوبر 00:00
                return new SimpleTimeZone(2 * H, "LB", Calendar.MARCH, -1, Calendar.SUNDAY, 0,
                        Calendar.OCTOBER, -1, Calendar.SUNDAY, 0, H);
            case PLUS4:
                return new SimpleTimeZone(4 * H, "P4");
            default:
                return new SimpleTimeZone(3 * H, "P3");
        }
    }

    static final City[] ALL = {
            new City("القاهرة", 30.0444, 31.2357, EGYPT, 0),
            new City("الجيزة", 30.0131, 31.2089, EGYPT, 0),
            new City("الإسكندرية", 31.2001, 29.9187, EGYPT, 0),
            new City("المنصورة", 31.0409, 31.3785, EGYPT, 0),
            new City("طنطا", 30.7865, 31.0004, EGYPT, 0),
            new City("الزقازيق", 30.5877, 31.502, EGYPT, 0),
            new City("بورسعيد", 31.2653, 32.3019, EGYPT, 0),
            new City("السويس", 29.9668, 32.5498, EGYPT, 0),
            new City("الإسماعيلية", 30.5965, 32.2715, EGYPT, 0),
            new City("دمياط", 31.4165, 31.8133, EGYPT, 0),
            new City("كفر الشيخ", 31.1107, 30.9388, EGYPT, 0),
            new City("دمنهور", 31.0341, 30.4682, EGYPT, 0),
            new City("بنها", 30.466, 31.1848, EGYPT, 0),
            new City("شبين الكوم", 30.5594, 31.0119, EGYPT, 0),
            new City("الفيوم", 29.3084, 30.8428, EGYPT, 0),
            new City("بني سويف", 29.0661, 31.0994, EGYPT, 0),
            new City("المنيا", 28.1099, 30.7503, EGYPT, 0),
            new City("أسيوط", 27.1783, 31.1859, EGYPT, 0),
            new City("سوهاج", 26.5569, 31.6948, EGYPT, 0),
            new City("قنا", 26.1551, 32.716, EGYPT, 0),
            new City("الأقصر", 25.6872, 32.6396, EGYPT, 0),
            new City("أسوان", 24.0889, 32.8998, EGYPT, 0),
            new City("الغردقة", 27.2579, 33.8116, EGYPT, 0),
            new City("شرم الشيخ", 27.9158, 34.33, EGYPT, 0),
            new City("مرسى مطروح", 31.3543, 27.2373, EGYPT, 0),
            new City("العريش", 31.1316, 33.7984, EGYPT, 0),
            new City("مكة المكرمة", 21.4225, 39.8262, PLUS3, 1),
            new City("المدينة المنورة", 24.4686, 39.6142, PLUS3, 1),
            new City("الرياض", 24.7136, 46.6753, PLUS3, 1),
            new City("جدة", 21.4858, 39.1925, PLUS3, 1),
            new City("الكويت", 29.3759, 47.9774, PLUS3, 1),
            new City("الدوحة", 25.2854, 51.531, PLUS3, 1),
            new City("دبي", 25.2048, 55.2708, PLUS4, 1),
            new City("بغداد", 33.3152, 44.3661, PLUS3, 0),
            new City("عمّان", 31.9454, 35.9284, PLUS3, 0),
            new City("بيروت", 33.8938, 35.5018, LEBANON, 0)
    };

    static int indexOf(String name) {
        for (int i = 0; i < ALL.length; i++) if (ALL[i].name.equals(name)) return i;
        for (int i = 0; i < ALL.length; i++) if (ALL[i].name.equals(DEFAULT)) return i;
        return 0;
    }
}
