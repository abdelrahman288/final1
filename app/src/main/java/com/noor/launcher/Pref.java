package com.noor.launcher;

/** تخزين الإعدادات. على الجهاز بيتحوّل لـ SharedPreferences، وعلى الكمبيوتر (للاختبار) لخريطة في الذاكرة. */
final class Pref {
    private Pref() {}

    interface Store {
        int getInt(String k, int d);
        long getLong(String k, long d);
        String getString(String k, String d);
        boolean getBool(String k, boolean d);
        void put(String k, Object v);
    }

    static final class MemStore implements Store {
        private final java.util.HashMap<String, Object> m = new java.util.HashMap<>();
        public int getInt(String k, int d) { Object o = m.get(k); return o instanceof Integer ? (Integer) o : d; }
        public long getLong(String k, long d) { Object o = m.get(k); return o instanceof Long ? (Long) o : d; }
        public String getString(String k, String d) { Object o = m.get(k); return o instanceof String ? (String) o : d; }
        public boolean getBool(String k, boolean d) { Object o = m.get(k); return o instanceof Boolean ? (Boolean) o : d; }
        public void put(String k, Object v) { m.put(k, v); }
    }

    private static Store s = new MemStore();

    static void use(Store st) { s = st; }

    static int i(String k, int d) { return s.getInt(k, d); }
    static long l(String k, long d) { return s.getLong(k, d); }
    static String s(String k, String d) { return s.getString(k, d); }
    static boolean b(String k, boolean d) { return s.getBool(k, d); }
    static void set(String k, Object v) { s.put(k, v); }

    // مفاتيح
    static final String THEME = "theme";          // 0..3
    static final String CITY = "cityName";        // اسم المدينة (الافتراضي: سوهاج)
    static final String HADJ = "hadj";            // تعديل الهجري بالأيام
    static final String CLOCK_OFF = "clockOff";   // ضبط يدوي للساعة (ms)
    static final String LAST_GOOD = "lastGood";   // آخر وقت سليم
    static final String AUTO_APP = "autoApp";     // تطبيق يفتح تلقائياً عند تشغيل الشاشة
    static final String SOUND = "bismillah";      // صوت البسملة عند تشغيل الشاشة
    static final String INTRO = "intro";          // إنترو عند تشغيل الشاشة
    static final String GUARD_HOME = "guardHome"; // اعتراض زر Home
    static final String FOLDER = "folder";        // فولدر القرآن
    static final String REPEAT = "repeat";        // 0 تتابع، 1 تكرار الكل، 2 تكرار واحدة
    static final String LAST_TRACK = "lastTrack"; // اسم آخر ملف
    static final String TASBEEH = "tasbeeh";      // عدّاد المسبحة
    static final String TASBEEH_GOAL = "tasbeehGoal";
    static final String TASBEEH_TOTAL = "tasbeehTotal";
    static final String INTRO_STYLE = "introStyle"; // 0 الأصلي، 1 هلال، 2 محراب
    static final String DST = "dst";            // التوقيت الصيفي (افتراضي: شغال)
    static final String STATION = "station";      // آخر إذاعة
}
