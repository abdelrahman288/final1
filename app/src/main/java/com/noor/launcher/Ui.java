package com.noor.launcher;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * قلب الواجهة: الحالة + الحسابات + التحكم + الرسم المشترك.
 * مفيش هنا أي View ولا Layout — كله Canvas واحد، وبيتحجّم من مساحة تصميم 1920x1080.
 */
final class Ui {

    interface Host {
        void launch(AppItem a);
        void openSystem(String action);
        void appInfo(String pkg);
        boolean online();
        void toast(String s);
        void player(int cmd, int arg);
        void screenOff();
        boolean hasStorage();
        void requestStorage();
        boolean guardOn();
        void enableGuard();
        String deviceInfo();
        void redraw();
    }

    static final float W = 1920f, H = 1080f;
    static final String[] INTRO_NAMES = {"الأصلي (النجمة والتوقيع)", "الهلال والنجوم", "المحراب والنور"};

    // الشاشات
    static final int HOME = 0, APPS = 1, TODAY = 2, QURAN = 3, LIST = 4, ADHKAR = 5, TASBEEH = 6, CLOCKSET = 7;

    // أنواع العناصر
    static final int K_SETTINGS = 0, K_NET = 1, K_RADIO = 2, K_WARN = 3, K_PRAYER = 4, K_QURAN = 5, K_FAV = 6,
            K_MORE = 7, K_ALL = 8, K_PLAYER = 9, K_PREV = 10, K_PLAY = 11, K_NEXT = 12, K_REPEAT = 13,
            K_SCREENOFF = 14, K_ADHKAR = 15, K_TASBEEH = 16, K_LOGO = 17, K_BACK = 18, K_BTN = 19, K_APP = 20,
            K_ROW = 21, K_FIELD = 22, K_CARD = 23, K_TIMER = 24;

    // أزرار K_BTN
    static final int B_CITY = 1, B_HADJ = 2, B_TUNE = 3, B_FOLDER = 4, B_STATIONS = 5, B_SLEEP = 6, B_SELALL = 7,
            B_PERM = 8, B_APPLY = 9, B_RESETCLOCK = 10, B_SYSTIME = 11, B_PREVD = 12, B_NEXTD = 13, B_RESETD = 14,
            B_GOAL = 15, B_RESETT = 16, B_CATS = 17, B_SETTINGS_SYS = 18;

    // قوائم
    static final int L_CITY = 1, L_STATION = 2, L_FOLDER = 3, L_AUTOAPP = 4, L_SETTINGS = 5, L_TUNE = 6, L_ADHKAR = 7,
            L_APPMENU = 8, L_THEME = 9;

    // مفاتيح
    static final int KEY_UP = 19, KEY_DOWN = 20, KEY_LEFT = 21, KEY_RIGHT = 22, KEY_CENTER = 23, KEY_ENTER = 66,
            KEY_BACK = 4, KEY_ESC = 111;

    static final int INTRO_LEN_MS = 2900;

    static final class El {
        final int kind, idx;
        final RectF r = new RectF();
        AppItem app;
        String label = "", sub = "", val = "";
        int ico = -1;
        boolean sc;        // جوه منطقة سكرول
        boolean on;        // محدد/مفعل
        float f;           // حركة التركيز 0..1
        float delay;       // تأخير ظهور الدخول
        El(int kind, int idx) { this.kind = kind; this.idx = idx; }
        float cx() { return r.centerX(); }
        float cy() { return r.centerY(); }
        float top() { return r.top; }
    }

    final Host host;
    Bitmap logo;                 // التوقيع (assets/logo.png)
    Theme theme;
    int themeIdx;

    // الرسم
    final Paint pFill = new Paint(Paint.ANTI_ALIAS_FLAG);
    final Paint pStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    final Paint pText = new Paint(Paint.ANTI_ALIAS_FLAG);
    final Paint pBmp = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
    final Paint pIcon = new Paint(Paint.ANTI_ALIAS_FLAG);
    final RectF tmp = new RectF(), tmp2 = new RectF();
    final Rect srcIcon = new Rect(0, 0, AppRepo.ICON, AppRepo.ICON);
    float ga = 1f;               // شفافية عامة (للدخول)

    // الشاشة
    int vw, vh;
    float sc = 1f, ox, oy;
    private Bitmap baseBmp;
    private int baseTheme = -1;

    // الحالة
    int mode = HOME;
    final ArrayList<El> homeEls = new ArrayList<>(), appEls = new ArrayList<>(), todayEls = new ArrayList<>(),
            quranEls = new ArrayList<>(), listEls = new ArrayList<>(), adhkarEls = new ArrayList<>(),
            tasbeehEls = new ArrayList<>(), clockEls = new ArrayList<>();
    int hf, af, tf, qf, lf, df, bf, cf;
    float scroll, scrollTarget, scrollMax;
    float viewTop = 170f, viewBot = 1040f;
    long lastFrame;
    float modeT = 1f;           // دخول الشاشة
    float tAnim = 99f;          // زمن من بداية الدخول/الإنترو
    float introLen = 0f;        // طول الإنترو بالثواني (0 = بدون)
    float entStart = 0f;

    // مواقيت
    int cityIdx;
    City city;
    TimeZone tz;
    Calendar cal;
    final long[] today = new long[6];
    long tomorrowFajr, yesterdayIsha;
    int dayKey = -1;
    boolean dirty = true, ramadan, nextTomorrow, netOk, adhanNow;
    int nowIdx = -1, nextIdx = 0, netTick, hadj;
    final String[] tFull = new String[6], tTime = new String[6], tAp = new String[6], tLabel = new String[6];
    String clockDigits = "", clockSfx = "", dateStr = "", hijriStr = "", hijriShort = "", dateLine = "";
    String panelLabel = "", panelName = "", cdStr = "", cdHuman = "", adhanStr = "", suhoorStr = "";
    String occTitle = "", occText = "", ayaText = "", ayaSrc = "", hadText = "", hadSrc = "";
    float progress;             // نسبة الوقت بين الصلاة اللي فاتت والجاية
    float dayFrac;              // مكان الشمس بين الفجر والعشاء (0..1) أو -1
    int clockWarn;              // 0 سليمة، 1/2 محتاجة ضبط
    long nextTime;

    // المشغل
    String plTitle = "", plSub = "", plPosS = "", plDurS = "";
    float plProg;
    boolean plPlaying, plActive;

    // المسبحة
    int tasCount, tasGoal = 33, tasTotal;

    // القوائم
    int listId, listBack = HOME;
    String listTitle = "", listHint = "";
    final ArrayList<String> lLabel = new ArrayList<>(), lSub = new ArrayList<>(), lVal = new ArrayList<>();
    final ArrayList<Boolean> lCheck = new ArrayList<>();
    int listSel;
    File browseDir;
    int listPayload;

    // الأذكار
    int adhCat;
    Content.Dhikr[] adhList = new Content.Dhikr[0];
    int[] adhDone = new int[0];
    int adhIdx;
    float doneAnim;

    // القرآن
    List<File> tracks = new ArrayList<>();
    java.util.HashSet<String> selected = new java.util.HashSet<>();
    String folder = "";

    // ضبط الساعة
    final int[] cs = new int[5]; // يوم، شهر، سنة، ساعة، دقيقة
    int csField;

    // طبقات
    boolean legal;
    float legalScroll;
    java.util.List<String> legalLines;
    long toastUntil;
    String toastMsg = "";

    final List<AppItem> favs = new ArrayList<>(), more = new ArrayList<>();
    final Theme[] themes;

    Ui(Host host) {
        this.host = host;
        themes = new Theme[]{new ThemeA(), new ThemeB(), new ThemeC(), new ThemeD()};
        pStroke.setStyle(Paint.Style.STROKE);
        Clock.load();
        themeIdx = Math.max(0, Math.min(3, Pref.i(Pref.THEME, 0)));
        theme = themes[themeIdx];
        cityIdx = City.indexOf(Pref.s(Pref.CITY, City.DEFAULT));
        hadj = Pref.i(Pref.HADJ, 0);
        tasGoal = Pref.i(Pref.TASBEEH_GOAL, 33);
        tasCount = Pref.i(Pref.TASBEEH, 0);
        tasTotal = Pref.i(Pref.TASBEEH_TOTAL, 0);
        folder = Pref.s(Pref.FOLDER, "");
        applyCity();
        rebuildHome();
        hf = defaultHomeFocus();
        tick();
    }

    // ---------------- تجهيز ----------------

    void applyCity() {
        city = City.ALL[cityIdx];
        tz = city.tz();
        cal = Calendar.getInstance(tz);
        dirty = true;
    }

    El add(ArrayList<El> l, int kind, int idx, float left, float top, float right, float bottom) {
        El e = new El(kind, idx);
        e.r.set(left, top, right, bottom);
        e.delay = l.size() * 0.028f;
        l.add(e);
        return e;
    }

    /** للتصاميم: إضافة عنصر للرئيسية. */
    El addHome(int kind, int idx, float left, float top, float right, float bottom) {
        return add(homeEls, kind, idx, left, top, right, bottom);
    }

    int defaultHomeFocus() {
        for (int i = 0; i < homeEls.size(); i++) if (homeEls.get(i).kind == K_QURAN) return i;
        return 0;
    }

    void refreshApps() {
        favs.clear();
        more.clear();
        List<AppItem> f = AppRepo.resolve(AppRepo.KEY_FAVS);
        for (int i = 0; i < f.size() && i < Config.MAX_FAVS; i++) favs.add(f.get(i));
        List<AppItem> m = AppRepo.resolve(AppRepo.KEY_MORE);
        for (int i = 0; i < m.size() && i < Config.MAX_MORE; i++) more.add(m.get(i));
    }

    void rebuildHome() {
        int fk = -1, fi = 0;
        if (hf >= 0 && hf < homeEls.size()) {
            fk = homeEls.get(hf).kind;
            fi = homeEls.get(hf).idx;
        }
        homeEls.clear();
        refreshApps();
        theme.layout(this);
        hf = -1;
        for (int i = 0; i < homeEls.size(); i++) {
            if (homeEls.get(i).kind == fk && homeEls.get(i).idx == fi) hf = i;
        }
        if (hf < 0) hf = defaultHomeFocus();
    }

    void setTheme(int i) {
        themeIdx = (i + 4) % 4;
        theme = themes[themeIdx];
        Pref.set(Pref.THEME, themeIdx);
        baseTheme = -1;
        rebuildHome();
        restartEntrance(false);
        kick();
    }

    void onAppsLoaded() {
        rebuildApps();
        rebuildHome();
        kick();
    }

    void onSize(int w, int h) {
        vw = w;
        vh = h;
        sc = Math.min(w / W, h / H);
        ox = (w - W * sc) / 2f;
        oy = (h - H * sc) / 2f;
        baseTheme = -1;
    }

    void kick() {
        lastFrame = Clock.mono();
        host.redraw();
    }

    /** دخول بحركة: بإنترو (فتح الشاشة) أو دخول سريع (الرجوع للرئيسية). */
    void restartEntrance(boolean intro) {
        if (intro) {
            introLen = INTRO_LEN_MS / 1000f;
            entStart = introLen - 0.75f;
        } else {
            introLen = 0f;
            entStart = 0f;
        }
        tAnim = 0f;
        lastFrame = Clock.mono();
    }

    boolean introActive() {
        return introLen > 0f && tAnim < introLen;
    }

    void skipIntro() {
        if (introActive()) tAnim = introLen - 0.2f;
    }

    // ---------------- الوقت والمواقيت ----------------

    void tick() {
        long now = Clock.now();
        cal.setTimeInMillis(now);
        int y = cal.get(Calendar.YEAR), m = cal.get(Calendar.MONTH) + 1, d = cal.get(Calendar.DAY_OF_MONTH);
        int key = y * 10000 + m * 100 + d;
        if (dirty || key != dayKey) {
            recomputeDay(y, m, d);
            dayKey = key;
            dirty = false;
        }
        nowIdx = -1;
        for (int i = 0; i < 6; i++) if (now >= today[i]) nowIdx = i;
        nextIdx = -1;
        int[] prayers = {Prayer.FAJR, Prayer.DHUHR, Prayer.ASR, Prayer.MAGHRIB, Prayer.ISHA};
        for (int p : prayers) {
            if (today[p] > now) {
                nextIdx = p;
                break;
            }
        }
        long prevT;
        if (nextIdx < 0) {
            nextIdx = Prayer.FAJR;
            nextTime = tomorrowFajr;
            nextTomorrow = true;
            prevT = today[Prayer.ISHA];
        } else {
            nextTime = today[nextIdx];
            nextTomorrow = false;
            prevT = nextIdx == Prayer.FAJR ? yesterdayIsha
                    : today[nextIdx == Prayer.DHUHR ? Prayer.FAJR : nextIdx - 1 == Prayer.SUNRISE ? Prayer.FAJR : nextIdx - 1];
        }
        progress = Math.max(0f, Math.min(1f, (now - prevT) / (float) Math.max(1L, nextTime - prevT)));
        dayFrac = (now >= today[Prayer.FAJR] && now <= today[Prayer.ISHA])
                ? (now - today[Prayer.FAJR]) / (float) (today[Prayer.ISHA] - today[Prayer.FAJR]) : -1f;

        // آخر 90 ثانية بعد الأذان
        adhanNow = false;
        int[] ap = {Prayer.FAJR, Prayer.DHUHR, Prayer.ASR, Prayer.MAGHRIB, Prayer.ISHA};
        String adhanName = "";
        for (int p : ap) {
            if (now >= today[p] && now < today[p] + 90000L) {
                adhanNow = true;
                adhanName = Prayer.NAMES[p];
            }
        }

        for (int i = 0; i < 6; i++) {
            String lab = null;
            if (i == nowIdx) lab = "الآن";
            else if (i == nextIdx) lab = "التالية";
            else if (ramadan && i == Prayer.FAJR) lab = "الإمساك";
            else if (ramadan && i == Prayer.MAGHRIB) lab = "الإفطار";
            tLabel[i] = lab;
        }
        boolean iftar = ramadan && nextIdx == Prayer.MAGHRIB;
        panelLabel = adhanNow ? "حان الآن موعد أذان " + adhanName
                : (iftar ? "الإفطار بعد" : (ramadan && nextIdx == Prayer.FAJR ? "الإمساك بعد" : "الصلاة القادمة"));
        panelName = iftar ? "الإفطار" : Prayer.NAMES[nextIdx];
        cdStr = Fmt.countdown(nextTime - now);
        cdHuman = Fmt.human(nextTime - now);
        adhanStr = "الأذان " + Fmt.hm(nextTime, tz) + " " + Fmt.ampm(nextTime, tz) + "  •  " + city.name;
        suhoorStr = ramadan ? "السحور حتى " + Fmt.hm(today[Prayer.FAJR], tz) + " " + Fmt.ampm(today[Prayer.FAJR], tz) : "";
        clockDigits = Fmt.hm(now, tz);
        clockSfx = Fmt.ampm(now, tz);

        clockWarn = Clock.status();
        Clock.remember();

        // المشغل
        plActive = Player.active();
        plPlaying = Player.playing;
        if (plActive) {
            plTitle = Player.title;
            plSub = Player.sub;
            plPosS = Fmt.mmss(Player.pos);
            plDurS = Player.dur > 0 ? Fmt.mmss(Player.dur) : "";
            plProg = Player.dur > 0 ? Math.max(0f, Math.min(1f, Player.pos / (float) Player.dur)) : 0f;
        } else {
            plTitle = "مشغل القرآن";
            plSub = folder.isEmpty() ? "اختار فولدر القرآن من الإعدادات" : folderName(folder);
            plPosS = "";
            plDurS = "";
            plProg = 0f;
        }
        if (netTick++ % 5 == 0) netOk = host.online();
        host.redraw();
    }

    static String folderName(String path) {
        int i = path.lastIndexOf('/');
        return i >= 0 && i < path.length() - 1 ? path.substring(i + 1) : path;
    }

    private void recomputeDay(int y, int m, int d) {
        long[] t = Prayer.times(y, m, d, city);
        System.arraycopy(t, 0, today, 0, 6);
        Calendar c2 = (Calendar) cal.clone();
        c2.add(Calendar.DAY_OF_MONTH, 1);
        tomorrowFajr = Prayer.times(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH) + 1, c2.get(Calendar.DAY_OF_MONTH), city)[Prayer.FAJR];
        c2.add(Calendar.DAY_OF_MONTH, -2);
        yesterdayIsha = Prayer.times(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH) + 1, c2.get(Calendar.DAY_OF_MONTH), city)[Prayer.ISHA];
        for (int i = 0; i < 6; i++) {
            tTime[i] = Fmt.hm(today[i], tz);
            tAp[i] = Fmt.ampm(today[i], tz);
            tFull[i] = tTime[i] + " " + tAp[i];
        }
        int[] h = Fmt.hijri(y, m, d, hadj);
        ramadan = h[1] == 9;
        hijriShort = Fmt.digits(h[0]) + " " + Fmt.HIJRI[h[1] - 1] + " " + Fmt.digits(h[2]) + " هـ";
        hijriStr = hijriShort + "  •  " + city.name;
        dateStr = Fmt.gregorian(cal);
        dateLine = dateStr + "   •   " + hijriShort;
        int doy = cal.get(Calendar.DAY_OF_YEAR);
        String[] a = Content.ayaOfDay(doy), hd = Content.hadithOfDay(doy);
        ayaText = a[0];
        ayaSrc = a[1];
        hadText = hd[0];
        hadSrc = hd[1];
        String[] oc = Content.occasion(cal, hadj);
        occTitle = oc[0];
        occText = oc[1];
    }

    // ---------------- أدوات الرسم ----------------

    static float clamp01(float v) { return v < 0f ? 0f : (v > 1f ? 1f : v); }
    static float ease(float t) { t = clamp01(t); float u = 1f - t; return 1f - u * u * u; }

    int col(int c) {
        if (ga >= 0.999f) return c;
        return (((int) ((c >>> 24) * ga)) << 24) | (c & 0xFFFFFF);
    }

    static int lerp(int a, int b, float t) {
        if (t <= 0f) return a;
        if (t >= 1f) return b;
        int aa = a >>> 24, ar = (a >> 16) & 255, ag = (a >> 8) & 255, ab = a & 255;
        int ba = b >>> 24, br = (b >> 16) & 255, bg = (b >> 8) & 255, bb = b & 255;
        return ((int) (aa + (ba - aa) * t) << 24) | ((int) (ar + (br - ar) * t) << 16)
                | ((int) (ag + (bg - ag) * t) << 8) | (int) (ab + (bb - ab) * t);
    }

    static int alpha(int c, float a) {
        return (((int) ((c >>> 24) * a)) << 24) | (c & 0xFFFFFF);
    }

    void text(Canvas c, String s, float x, float y, float size, int color, Paint.Align al, Typeface tf) {
        if (s == null || s.isEmpty()) return;
        pText.setTypeface(tf);
        pText.setTextSize(size);
        pText.setColor(col(color));
        pText.setTextAlign(al);
        c.drawText(s, x, y, pText);
    }

    void textSp(Canvas c, String s, float x, float y, float size, int color, Paint.Align al, Typeface tf, float spacing) {
        pText.setLetterSpacing(spacing);
        text(c, s, x, y, size, color, al, tf);
        pText.setLetterSpacing(0f);
    }

    void textShader(Canvas c, String s, float x, float y, float size, Shader sh, Paint.Align al, Typeface tf) {
        pText.setTypeface(tf);
        pText.setTextSize(size);
        pText.setColor(0xFFFFFFFF);
        pText.setTextAlign(al);
        pText.setShader(sh);
        if (ga < 0.999f) pText.setAlpha((int) (255 * ga));
        c.drawText(s, x, y, pText);
        pText.setShader(null);
    }

    float measure(String s, float size, Typeface tf) {
        pText.setTypeface(tf);
        pText.setTextSize(size);
        pText.setLetterSpacing(0f);
        return pText.measureText(s);
    }

    String fit(String s, float size, float maxW, Typeface tf) {
        if (s == null) return "";
        if (measure(s, size, tf) <= maxW) return s;
        int n = s.length();
        while (n > 1 && measure(s.substring(0, n) + "…", size, tf) > maxW) n--;
        return s.substring(0, n) + "…";
    }

    void fill(Canvas c, RectF r, float rad, int color) {
        pFill.setShader(null);
        pFill.setColor(col(color));
        c.drawRoundRect(r, rad, rad, pFill);
    }

    void fillShader(Canvas c, RectF r, float rad, Shader sh) {
        pFill.setShader(sh);
        pFill.setColor(0xFFFFFFFF);
        if (ga < 0.999f) pFill.setAlpha((int) (255 * ga));
        c.drawRoundRect(r, rad, rad, pFill);
        pFill.setShader(null);
    }

    void stroke(Canvas c, RectF r, float rad, int color, float w) {
        pStroke.setPathEffect(null);
        pStroke.setStrokeWidth(w);
        pStroke.setColor(col(color));
        c.drawRoundRect(r, rad, rad, pStroke);
    }

    void dashed(Canvas c, RectF r, float rad, int color, float w) {
        pStroke.setPathEffect(new DashPathEffect(new float[]{14, 10}, 0));
        pStroke.setStrokeWidth(w);
        pStroke.setColor(col(color));
        c.drawRoundRect(r, rad, rad, pStroke);
        pStroke.setPathEffect(null);
    }

    void circle(Canvas c, float x, float y, float r, int color) {
        pFill.setShader(null);
        pFill.setColor(col(color));
        c.drawCircle(x, y, r, pFill);
    }

    void icon(Canvas c, int id, float cx, float cy, float size, int color, float sw) {
        Icons.draw(c, id, cx, cy, size, col(color), pIcon, sw);
    }

    void bmp(Canvas c, Bitmap b, RectF dst) {
        pBmp.setAlpha((int) (255 * ga));
        c.drawBitmap(b, srcIcon, dst, pBmp);
    }

    void ring(Canvas c, El e, float rad, int color) {
        if (e.f > 0.02f) {
            stroke(c, e.r, rad, alpha(color, e.f), 4f);
        }
    }

    void pop(Canvas c, El e, float amount) {
        float s = 1f + amount * e.f;
        c.save();
        c.scale(s, s, e.cx(), e.cy());
    }

    /** يقسّم النص لأسطر بعرض معيّن (بالترتيب المنطقي — يصلح للعربي والإنجليزي). */
    List<String> wrap(String s, float size, Typeface tf, float maxW) {
        ArrayList<String> out = new ArrayList<>();
        for (String para : s.split("\n")) {
            String[] words = para.split(" ");
            StringBuilder line = new StringBuilder();
            for (String w : words) {
                if (w.isEmpty()) continue;
                String t = line.length() == 0 ? w : line + " " + w;
                if (line.length() > 0 && measure(t, size, tf) > maxW) {
                    out.add(line.toString());
                    line.setLength(0);
                    line.append(w);
                } else {
                    line.setLength(0);
                    line.append(t);
                }
            }
            out.add(line.toString());
        }
        return out;
    }

    /** يرسم فقرة من أسطر (محاذاة لليمين أو وسط). يرجّع y بعد آخر سطر. */
    float paragraph(Canvas c, List<String> lines, float x, float y, float size, float lh, int color, Paint.Align al, Typeface tf) {
        for (String l : lines) {
            text(c, l, x, y, size, color, al, tf);
            y += lh;
        }
        return y;
    }

    // دخول بحركة: تأخير (ثانية) → يرجّع 0..1
    float ent(float delay) {
        float t = tAnim - entStart - delay;
        return ease(t / 0.5f);
    }

    boolean entBegin(Canvas c, float delay) {
        float p = ent(delay);
        if (p <= 0f) return false;
        c.save();
        c.translate(0, (1f - p) * 36f);
        ga = p;
        return true;
    }

    void entEnd(Canvas c) {
        ga = 1f;
        c.restore();
    }

    El find(ArrayList<El> l, int kind, int idx) {
        for (El e : l) if (e.kind == kind && e.idx == idx) return e;
        return null;
    }

    // ---------------- الرسم الرئيسي ----------------

    ArrayList<El> cur() {
        switch (mode) {
            case APPS: return appEls;
            case TODAY: return todayEls;
            case QURAN: return quranEls;
            case LIST: return listEls;
            case ADHKAR: return adhkarEls;
            case TASBEEH: return tasbeehEls;
            case CLOCKSET: return clockEls;
            default: return homeEls;
        }
    }

    int curFocus() {
        switch (mode) {
            case APPS: return af;
            case TODAY: return tf;
            case QURAN: return qf;
            case LIST: return lf;
            case ADHKAR: return df;
            case TASBEEH: return bf;
            case CLOCKSET: return cf;
            default: return hf;
        }
    }

    void setCurFocus(int i) {
        switch (mode) {
            case APPS: af = i; break;
            case TODAY: tf = i; break;
            case QURAN: qf = i; break;
            case LIST: lf = i; break;
            case ADHKAR: df = i; break;
            case TASBEEH: bf = i; break;
            case CLOCKSET: cf = i; break;
            default: hf = i;
        }
    }

    private void ensureBase() {
        if (baseBmp != null && baseTheme == themeIdx) return;
        int bw = (int) Math.min(1920, Math.max(640, vw)), bh = (int) Math.min(1080, Math.max(360, vh));
        // نخلي النسبة 16:9 دايماً
        bh = Math.round(bw * 9f / 16f);
        baseBmp = Bitmap.createBitmap(bw, bh, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(baseBmp);
        float s = bw / W;
        c.scale(s, s);
        float save = ga;
        ga = 1f;
        theme.base(c, this);
        ga = save;
        baseTheme = themeIdx;
    }

    /** يرسم إطار. يرجّع true لو لسه فيه حركة. */
    boolean draw(Canvas c, int w, int h) {
        if (w != vw || h != vh) onSize(w, h);
        long nowM = Clock.mono();
        float dt = Math.min(0.05f, Math.max(0f, (nowM - lastFrame) / 1000f));
        lastFrame = nowM;
        float k = Math.min(1f, dt * 16f);
        boolean anim = false;

        ArrayList<El> els = cur();
        int fi = curFocus();
        for (int i = 0; i < els.size(); i++) {
            El e = els.get(i);
            float t = (i == fi && !legal) ? 1f : 0f;
            if (e.f != t) {
                e.f += (t - e.f) * k;
                if (Math.abs(t - e.f) < 0.01f) e.f = t;
                else anim = true;
            }
        }
        if (scroll != scrollTarget) {
            scroll += (scrollTarget - scroll) * k;
            if (Math.abs(scrollTarget - scroll) < 0.5f) scroll = scrollTarget;
            else anim = true;
        }
        if (tAnim < 30f) {
            tAnim += dt;
            if (tAnim < entStart + 0.5f + els.size() * 0.03f + 0.3f) anim = true;
            if (introActive()) anim = true;
        }
        if (modeT < 1f) {
            modeT = Math.min(1f, modeT + dt / 0.22f);
            anim = true;
        }
        if (doneAnim > 0f) {
            doneAnim = Math.max(0f, doneAnim - dt);
            anim = true;
        }

        ensureBase();
        c.drawColor(theme.bg2);
        c.save();
        c.translate(ox, oy);
        c.scale(sc, sc);
        c.clipRect(0, 0, W, H);
        tmp2.set(0, 0, W, H);
        pBmp.setAlpha(255);
        c.drawBitmap(baseBmp, null, tmp2, pBmp);

        float mp = ease(modeT);
        boolean sub = mode != HOME;
        if (sub && mp < 1f) {
            c.save();
            c.translate(0, (1f - mp) * 24f);
            ga = mp;
        }
        switch (mode) {
            case APPS: Screens.apps(c, this); break;
            case TODAY: Screens.today(c, this); break;
            case QURAN: Screens.quran(c, this); break;
            case LIST: Screens.list(c, this); break;
            case ADHKAR: Screens.adhkar(c, this); break;
            case TASBEEH: Screens.tasbeeh(c, this); break;
            case CLOCKSET: Screens.clockset(c, this); break;
            default: theme.home(c, this);
        }
        if (sub && mp < 1f) {
            ga = 1f;
            c.restore();
        }

        if (legal) Screens.legal(c, this);
        if (toastUntil > Clock.mono()) Screens.toast(c, this);
        if (introActive()) Intro.draw(c, this, tAnim / introLen);
        c.restore();
        return anim || toastUntil > nowM;
    }

    // ---------------- بناء الشاشات ----------------

    void rebuildApps() {
        appEls.clear();
        List<AppItem> l = AppRepo.apps;
        final int cols = 8;
        for (int i = 0; i < l.size(); i++) {
            int row = i / cols, col = i % cols;
            float right = 1804f - col * (190f + 24f);
            float top = 190f + row * 204f;
            El e = add(appEls, K_APP, i, right - 190f, top, right, top + 180f);
            e.sc = true;
            e.app = l.get(i);
            e.label = fit(e.app.label, 24f, 170f, Fonts.medium);
            e.delay = Math.min(0.3f, i * 0.01f);
        }
        if (af >= appEls.size()) af = Math.max(0, appEls.size() - 1);
    }

    void buildSubHeader(ArrayList<El> l) {
        El b = add(l, K_BACK, 0, 116, 56, 296, 124);
        b.label = "رجوع";
        b.ico = Icons.BACK;
    }

    void setMode(int m) {
        if (mode == m) return;
        mode = m;
        modeT = 0f;
        scroll = scrollTarget = 0f;
        scrollMax = 0f;
        switch (m) {
            case APPS:
                rebuildApps();
                af = 0;
                viewTop = 170f;
                viewBot = 1040f;
                updateScrollMax(appEls, 1040f);
                break;
            case TODAY: buildToday(); break;
            case QURAN: buildQuran(); break;
            case ADHKAR: buildAdhkar(); break;
            case TASBEEH: buildTasbeeh(); break;
            case CLOCKSET: buildClockSet(); break;
            default: break;
        }
        kick();
    }

    void updateScrollMax(ArrayList<El> l, float bottomView) {
        float mx = 0;
        for (El e : l) if (e.sc && e.r.bottom > mx) mx = e.r.bottom;
        scrollMax = Math.max(0f, mx - bottomView + 16f);
    }

    void goHome() {
        legal = false;
        if (mode != HOME) {
            mode = HOME;
            modeT = 1f;
        }
        scroll = scrollTarget = 0f;
        hf = Math.max(0, Math.min(hf, homeEls.size() - 1));
        kick();
    }

    boolean back() {
        if (legal) {
            legal = false;
            kick();
            return true;
        }
        if (introActive()) {
            skipIntro();
            kick();
            return true;
        }
        if (mode == HOME) return true;
        if (mode == LIST) {
            if (listId == L_FOLDER && browseDir != null) {
                // طلوع لفولدر أعلى
                File p = browseDir.getParentFile();
                if (p != null && p.canRead() && !browseDir.getPath().equals("/storage")) {
                    browseDir = p;
                    openFolderList();
                    return true;
                }
            }
            int bm = listBack;
            if (bm == HOME) {
                mode = HOME;
                modeT = 0f;
            } else {
                mode = HOME;
                setMode(bm);
            }
            kick();
            return true;
        }
        if (mode == ADHKAR) {
            openAdhkarCats();
            return true;
        }
        mode = HOME;
        modeT = 0f;
        scroll = scrollTarget = 0f;
        kick();
        return true;
    }

    // ---- اليوم ----
    void buildToday() {
        todayEls.clear();
        buildSubHeader(todayEls);
        El c = add(todayEls, K_BTN, B_CITY, 1384, 150, 1804, 216);
        c.label = "المدينة: " + city.name;
        c.ico = Icons.MOSQUE;
        El h = add(todayEls, K_BTN, B_HADJ, 944, 150, 1364, 216);
        h.label = hadjLabel();
        h.ico = Icons.CAL;
        El t = add(todayEls, K_BTN, B_TUNE, 464, 150, 924, 216);
        t.label = "تعديل المواقيت بالدقائق";
        t.ico = Icons.TIMER;
        tf = 3;
    }

    String hadjLabel() {
        return "ضبط الهجري: " + (hadj == 0 ? "٠" : (hadj > 0 ? "+" + Fmt.digits(hadj) : "-" + Fmt.digits(-hadj))) + " يوم";
    }

    // ---- القرآن ----
    void buildQuran() {
        quranEls.clear();
        buildSubHeader(quranEls);
        selected = Media.selection();
        tracks = Media.scan(folder);
        float y = 560;
        float[] cx = {112, 196, 280, 364, 448}; // سيتم تعيينها بالترتيب أدناه
        // أزرار التحكم (يسار): تتابع | قبل | تشغيل | بعد | تكرار | إطفاء الشاشة
        float base = 520f, step = 100f;
        int[] kinds = {K_SCREENOFF, K_REPEAT, K_PREV, K_PLAY, K_NEXT};
        for (int i = 0; i < kinds.length; i++) {
            float x = 100f + i * step + (i >= 3 ? 20f : 0f) + (i == 3 ? 0 : 0);
            float sz = kinds[i] == K_PLAY ? 88f : 72f;
            add(quranEls, kinds[i], 0, x, base - sz / 2f + 24f, x + sz, base - sz / 2f + 24f + sz);
        }
        // أزرار تحت
        El a = add(quranEls, K_BTN, B_FOLDER, 64, 660, 424, 724);
        a.label = "تغيير الفولدر";
        a.ico = Icons.FOLDER;
        El b = add(quranEls, K_BTN, B_STATIONS, 440, 660, 800, 724);
        b.label = "الإذاعات";
        b.ico = Icons.RADIO;
        El s = add(quranEls, K_BTN, B_SLEEP, 64, 740, 424, 804);
        s.label = sleepLabel();
        s.ico = Icons.TIMER;
        El sa = add(quranEls, K_BTN, B_SELALL, 440, 740, 800, 804);
        sa.label = selected.isEmpty() ? "تحديد الكل" : "مسح التحديد";
        sa.ico = Icons.CHECK;
        if (!host.hasStorage()) {
            El p = add(quranEls, K_BTN, B_PERM, 64, 820, 800, 890);
            p.label = "اسمح بالوصول لملفات الشاشة";
            p.ico = Icons.ALERT;
        }
        // صفوف الملفات (سكرول)
        for (int i = 0; i < tracks.size(); i++) {
            float top = 180f + i * 84f;
            El r = add(quranEls, K_ROW, i, 860, top, 1856, top + 76f);
            r.sc = true;
            r.label = Media.nice(tracks.get(i).getName());
            r.on = selected.contains(tracks.get(i).getName());
            r.delay = Math.min(0.25f, i * 0.01f);
        }
        viewTop = 150f;
        viewBot = 1040f;
        updateScrollMax(quranEls, 1040f);
        qf = tracks.isEmpty() ? 6 : (Player.mode == Player.MODE_FOLDER ? Math.min(Player.index, tracks.size() - 1) + (quranEls.size() - tracks.size()) : 4);
        if (qf >= quranEls.size()) qf = 0;
        // لو مفيش مشغّل شغال: ركّز على زر التشغيل
        if (Player.mode != Player.MODE_FOLDER) {
            for (int i = 0; i < quranEls.size(); i++) if (quranEls.get(i).kind == K_PLAY) qf = i;
        }
    }

    String sleepLabel() {
        if (Player.sleepAt > Clock.mono()) {
            long m = (Player.sleepAt - Clock.mono() + 59999) / 60000;
            return "مؤقت النوم: " + Fmt.digits(m) + " د";
        }
        return "مؤقت النوم: متوقف";
    }

    // ---- الأذكار ----
    void openAdhkarCats() {
        listId = L_ADHKAR;
        listBack = HOME;
        listTitle = "الأذكار والأدعية";
        listHint = "اختار القسم";
        lLabel.clear();
        lSub.clear();
        lVal.clear();
        lCheck.clear();
        int sug = Content.suggestedCat(Clock.now(), today);
        for (int i = 0; i < Content.CAT_NAMES.length; i++) {
            lLabel.add(Content.CAT_NAMES[i]);
            lSub.add(i == sug ? "مناسب للوقت الحالي" : "");
            lVal.add("");
            lCheck.add(false);
        }
        listSel = sug >= 0 ? sug : 0;
        mode = LIST;
        modeT = 0f;
        buildList();
        kick();
    }

    void openAdhkar(int cat) {
        adhCat = cat;
        adhList = Content.adhkar(cat);
        adhDone = new int[adhList.length];
        adhIdx = 0;
        setMode(ADHKAR);
    }

    void buildAdhkar() {
        adhkarEls.clear();
        buildSubHeader(adhkarEls);
        El card = add(adhkarEls, K_CARD, 0, 240, 190, 1680, 800);
        El p = add(adhkarEls, K_BTN, B_PREVD, 240, 850, 560, 926);
        p.label = "السابق";
        El r = add(adhkarEls, K_BTN, B_RESETD, 800, 850, 1120, 926);
        r.label = "إعادة العدّ";
        El n = add(adhkarEls, K_BTN, B_NEXTD, 1360, 850, 1680, 926);
        n.label = "التالي";
        df = 1;
        doneAnim = 0f;
    }

    // ---- المسبحة ----
    void buildTasbeeh() {
        tasbeehEls.clear();
        buildSubHeader(tasbeehEls);
        add(tasbeehEls, K_CARD, 0, 560, 170, 1360, 830);
        El g = add(tasbeehEls, K_BTN, B_GOAL, 560, 880, 940, 956);
        g.label = "الهدف: " + Fmt.digits(tasGoal);
        El r = add(tasbeehEls, K_BTN, B_RESETT, 980, 880, 1360, 956);
        r.label = "تصفير";
        bf = 1;
    }

    // ---- ضبط الساعة ----
    void buildClockSet() {
        clockEls.clear();
        buildSubHeader(clockEls);
        Calendar c = Calendar.getInstance(tz);
        c.setTimeInMillis(Clock.now());
        cs[0] = c.get(Calendar.DAY_OF_MONTH);
        cs[1] = c.get(Calendar.MONTH) + 1;
        cs[2] = c.get(Calendar.YEAR);
        cs[3] = c.get(Calendar.HOUR_OF_DAY);
        cs[4] = c.get(Calendar.MINUTE);
        // يمين لشمال: يوم، شهر، سنة، ساعة، دقيقة
        float right = 1700f;
        float[] wd = {200, 200, 280, 200, 200};
        for (int i = 0; i < 5; i++) {
            float l = right - wd[i];
            El e = add(clockEls, K_FIELD, i, l, 330, right, 520);
            right = l - 28f;
        }
        El ap = add(clockEls, K_BTN, B_APPLY, 1300, 640, 1700, 716);
        ap.label = "تطبيق الضبط";
        El rs = add(clockEls, K_BTN, B_RESETCLOCK, 860, 640, 1280, 716);
        rs.label = "الرجوع لساعة الشاشة";
        El st = add(clockEls, K_BTN, B_SYSTIME, 380, 640, 840, 716);
        st.label = "إعدادات وقت النظام";
        csField = 4;
        cf = 5;
    }

    void applyClockSet() {
        try {
            Calendar c = Calendar.getInstance(tz);
            c.clear();
            c.set(cs[2], cs[1] - 1, 1, cs[3], cs[4], 0);
            int maxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            c.set(Calendar.DAY_OF_MONTH, Math.min(cs[0], maxDay));
            Clock.setTo(c.getTimeInMillis());
            dirty = true;
            tick();
            say("تم ضبط الساعة");
        } catch (Exception e) {
            say("تعذّر ضبط الساعة");
        }
    }

    void adjustField(int dir) {
        // dir +1 زيادة، -1 نقصان
        int f = csField;
        int[] mn = {1, 1, 2026, 0, 0}, mx = {31, 12, 2099, 23, 59};
        cs[f] += dir;
        if (cs[f] > mx[f]) cs[f] = mn[f];
        if (cs[f] < mn[f]) cs[f] = mx[f];
    }

    void say(String s) {
        toastMsg = s;
        toastUntil = Clock.mono() + 2200;
        host.redraw();
    }

    // ---- القوائم ----
    void buildList() {
        listEls.clear();
        buildSubHeader(listEls);
        for (int i = 0; i < lLabel.size(); i++) {
            float top = 190f + i * 92f;
            El r = add(listEls, K_ROW, i, 360, top, 1560, top + 80f);
            r.sc = true;
            r.label = lLabel.get(i);
            r.sub = lSub.get(i);
            r.val = lVal.get(i);
            r.on = lCheck.get(i);
            r.delay = Math.min(0.25f, i * 0.01f);
        }
        viewTop = 170f;
        viewBot = 1040f;
        updateScrollMax(listEls, 1040f);
        lf = Math.min(listEls.size() - 1, 1 + Math.max(0, listSel));
        if (lf < 0) lf = 0;
        scrollTarget = 0f;
        scroll = 0f;
        ensureVisible();
        scroll = scrollTarget;
    }

    void openList(int id, String title, String hint, int back) {
        listId = id;
        listTitle = title;
        listHint = hint;
        listBack = back;
        lLabel.clear();
        lSub.clear();
        lVal.clear();
        lCheck.clear();
        listSel = 0;
    }

    void addRow(String label, String sub, String val, boolean check) {
        lLabel.add(label);
        lSub.add(sub == null ? "" : sub);
        lVal.add(val == null ? "" : val);
        lCheck.add(check);
    }

    void showList(int fromMode) {
        mode = LIST;
        modeT = 0f;
        buildList();
        kick();
    }

    void openSettings() {
        int sel = listId == L_SETTINGS && mode == LIST ? lf - 1 : 0;
        openList(L_SETTINGS, "الإعدادات", "يمين/يسار لتغيير القيمة • OK للدخول", HOME);
        addRow("التصميم", "", theme.title(), false);
        addRow("المدينة (المواقيت)", "", city.name, false);
        String cw = clockWarn != 0 ? "تحتاج ضبط!" : (Clock.offsetMs != 0 ? "مضبوطة يدوياً" : "ساعة الشاشة");
        addRow("ضبط الساعة والتاريخ", "من غير إنترنت", cw, false);
        addRow("تعديل المواقيت بالدقائق", "لكل صلاة", "", false);
        addRow("ضبط التاريخ الهجري", "", (hadj == 0 ? "٠" : (hadj > 0 ? "+" : "-") + Fmt.digits(Math.abs(hadj))) + " يوم", false);
        String auto = Pref.s(Pref.AUTO_APP, "");
        AppItem ai = auto.isEmpty() ? null : AppRepo.find(auto);
        addRow("تطبيق يفتح تلقائياً عند تشغيل الشاشة", "بدل اللانشر مباشرة", ai == null ? "بدون" : ai.label, false);
        addRow("صوت البسملة عند تشغيل الشاشة", "", Pref.b(Pref.SOUND, true) ? "يعمل" : "متوقف", false);
        addRow("الإنترو عند تشغيل الشاشة", "", Pref.b(Pref.INTRO, true) ? "يعمل" : "متوقف", false);
        addRow("خدمة الحماية والتشغيل التلقائي", "مطلوبة لفتح اللانشر عند تشغيل الشاشة ولإطفاء الشاشة فعلياً", host.guardOn() ? "مفعّلة" : "غير مفعّلة", false);
        addRow("فولدر القرآن", folder.isEmpty() ? "لم يتم تحديده" : folder, "", false);
        addRow("إعدادات النظام", "", "", false);
        addRow("عن Abdou-Luncher والحقوق", "", "", false);
        addRow("شكل الإنترو", "يمين/يسار للتبديل ويظهر مثال فوراً", INTRO_NAMES[Pref.i(Pref.INTRO_STYLE, 0) % 3], false);
        addRow("التوقيت الصيفي", "مصر ولبنان — يتغيّر تلقائياً حسب القاعدة", Pref.b(Pref.DST, true) ? "يعمل" : "متوقف", false);
        listSel = Math.max(0, sel);
        mode = LIST;
        modeT = 0f;
        buildList();
        kick();
    }

    void openCities() {
        openList(L_CITY, "اختار المدينة", "المواقيت بتتحسب من غير إنترنت لأي مدينة", HOME);
        for (int i = 0; i < City.ALL.length; i++) addRow(City.ALL[i].name, "", "", i == cityIdx);
        listSel = cityIdx;
        showList(HOME);
    }

    void openThemes() {
        openList(L_THEME, "اختار التصميم", "", HOME);
        for (int i = 0; i < themes.length; i++) addRow(themes[i].title(), "", "", i == themeIdx);
        listSel = themeIdx;
        showList(HOME);
    }

    void openStations() {
        openList(L_STATION, "إذاعات القرآن الكريم", "تحتاج إنترنت • الصوت يكمل حتى لو طفيت الشاشة", QURAN);
        for (int i = 0; i < Content.STATIONS.length; i++) addRow(Content.STATIONS[i][0], "", "", Player.mode == Player.MODE_RADIO && Player.station == i);
        listSel = Math.max(0, Player.station);
        showList(QURAN);
    }

    void openTune() {
        openList(L_TUNE, "تعديل المواقيت بالدقائق", "يمين/يسار للتعديل (مثلاً لو عايزها تطابق مسجدكم)", HOME);
        for (int i = 0; i < 6; i++) {
            int v = Pref.i("tune" + i, 0);
            addRow(Prayer.NAMES[i], tFull[i], (v == 0 ? "٠" : (v > 0 ? "+" : "-") + Fmt.digits(Math.abs(v))) + " د", false);
        }
        listSel = 0;
        showList(HOME);
    }

    void openAutoApp() {
        openList(L_AUTOAPP, "التطبيق التلقائي", "بيفتح أول ما الشاشة تشتغل", HOME);
        String auto = Pref.s(Pref.AUTO_APP, "");
        addRow("بدون (يفتح اللانشر)", "", "", auto.isEmpty());
        for (AppItem a : AppRepo.apps) addRow(a.label, "", "", a.pkg.equals(auto));
        listSel = 0;
        showList(HOME);
    }

    void openFolderList() {
        openList(L_FOLDER, "اختار فولدر القرآن", browseDir == null ? "الذاكرة والفلاشات" : browseDir.getPath(), QURAN);
        List<File> items;
        if (browseDir == null) {
            items = Media.roots();
        } else {
            items = Media.dirs(browseDir);
            int n = Media.scan(browseDir.getPath()).size();
            addRow("✔ استخدم هذا الفولدر", Fmt.digits(n) + " ملف صوت", "", false);
        }
        for (File f : items) {
            String sub = browseDir == null ? f.getPath() : "";
            addRow(browseDir == null ? rootName(f) : f.getName(), sub, "", false);
        }
        browseList = items;
        listSel = 0;
        showList(QURAN);
    }

    List<File> browseList = new ArrayList<>();

    static String rootName(File f) {
        String p = f.getPath();
        if (p.equals("/storage/emulated/0") || p.equals("/sdcard")) return "ذاكرة الشاشة";
        return "فلاشة / كارت  " + f.getName();
    }

    void openLegal() {
        legal = true;
        legalScroll = 0f;
        legalLines = null;
        kick();
    }

    // ---------------- التحكم ----------------

    private int findNext(ArrayList<El> l, int cur, int dir) {
        if (l.isEmpty()) return -1;
        if (cur < 0 || cur >= l.size()) return 0;
        El c = l.get(cur);
        float cx = c.cx(), cy = c.cy();
        int best = -1;
        float bs = Float.MAX_VALUE;
        for (int i = 0; i < l.size(); i++) {
            if (i == cur) continue;
            El e = l.get(i);
            if (e.kind == K_WARN && clockWarn == 0) continue;
            float dx = e.cx() - cx, dy = e.cy() - cy;
            boolean ok;
            float prim, sec;
            switch (dir) {
                case 0: ok = dx < -4; prim = -dx; sec = Math.abs(dy); break;
                case 2: ok = dx > 4; prim = dx; sec = Math.abs(dy); break;
                case 1: ok = dy < -4; prim = -dy; sec = Math.abs(dx); break;
                default: ok = dy > 4; prim = dy; sec = Math.abs(dx); break;
            }
            if (!ok) continue;
            float s = prim + 2.5f * sec;
            if (s < bs) {
                bs = s;
                best = i;
            }
        }
        return best;
    }

    El currentEl() {
        ArrayList<El> l = cur();
        int i = curFocus();
        return (i >= 0 && i < l.size()) ? l.get(i) : null;
    }

    void ensureVisible() {
        El e = currentEl();
        if (e == null || !e.sc) return;
        if (e.idx < 8 && mode == APPS) {
            scrollTarget = 0;
            return;
        }
        if (e.r.top - scrollTarget < viewTop) scrollTarget = e.r.top - viewTop;
        else if (e.r.bottom - scrollTarget > viewBot) scrollTarget = e.r.bottom - viewBot;
        scrollTarget = Math.max(0, Math.min(scrollMax, scrollTarget));
    }

    /** dir: 0 يسار، 1 فوق، 2 يمين، 3 تحت */
    boolean move(int dir) {
        if (introActive()) {
            skipIntro();
            kick();
            return true;
        }
        if (legal) {
            if (dir == 1) legalScroll = Math.max(0f, legalScroll - 120f);
            else if (dir == 3) legalScroll += 120f;
            kick();
            return true;
        }
        El f = currentEl();
        if (f != null) {
            if (mode == CLOCKSET && f.kind == K_FIELD) {
                if (dir == 1 || dir == 3) {
                    csField = f.idx;
                    adjustField(dir == 1 ? 1 : -1);
                    kick();
                    return true;
                }
            }
            if (mode == LIST && f.kind == K_ROW && (dir == 0 || dir == 2)) {
                adjustList(f.idx, dir == 0 ? 1 : -1);
                return true;
            }
            if (f.kind == K_BTN && (dir == 0 || dir == 2)) {
                if (f.idx == B_HADJ) {
                    hadj = Math.max(-2, Math.min(2, hadj + (dir == 0 ? 1 : -1)));
                    Pref.set(Pref.HADJ, hadj);
                    dirty = true;
                    tick();
                    f.label = hadjLabel();
                    kick();
                    return true;
                }
                if (f.idx == B_GOAL) {
                    int[] g = {33, 99, 100, 500, 1000};
                    int ix = 0;
                    for (int i = 0; i < g.length; i++) if (g[i] == tasGoal) ix = i;
                    tasGoal = g[(ix + (dir == 0 ? 1 : g.length - 1)) % g.length];
                    Pref.set(Pref.TASBEEH_GOAL, tasGoal);
                    f.label = "الهدف: " + Fmt.digits(tasGoal);
                    kick();
                    return true;
                }
            }
            if (mode == ADHKAR && f.kind == K_CARD && (dir == 0 || dir == 2)) {
                stepDhikr(dir == 0 ? 1 : -1);
                return true;
            }
        }
        int n = findNext(cur(), curFocus(), dir);
        if (n >= 0) {
            setCurFocus(n);
            ensureVisible();
            kick();
        } else if (mode == APPS && (dir == 1 || dir == 3)) {
            kick();
        }
        return true;
    }

    void stepDhikr(int d) {
        int n = adhList.length;
        if (n == 0) return;
        adhIdx = Math.max(0, Math.min(n - 1, adhIdx + d));
        kick();
    }

    void adjustList(int i, int dir) {
        switch (listId) {
            case L_SETTINGS:
                if (i == 0) {
                    setTheme(themeIdx + dir);
                    openSettings();
                    listSel = 0;
                    lf = 1;
                } else if (i == 4) {
                    hadj = Math.max(-2, Math.min(2, hadj + dir));
                    Pref.set(Pref.HADJ, hadj);
                    dirty = true;
                    tick();
                    refreshSettingsRows();
                } else if (i == 6) {
                    Pref.set(Pref.SOUND, !Pref.b(Pref.SOUND, true));
                    refreshSettingsRows();
                } else if (i == 12) {
                    Pref.set(Pref.INTRO_STYLE, (Pref.i(Pref.INTRO_STYLE, 0) + (dir > 0 ? 1 : 2)) % 3);
                    refreshSettingsRows();
                    restartEntrance(true);
                } else if (i == 13) {
                    Pref.set(Pref.DST, !Pref.b(Pref.DST, true));
                    applyCity();
                    dirty = true;
                    tick();
                    refreshSettingsRows();
                } else if (i == 7) {
                    Pref.set(Pref.INTRO, !Pref.b(Pref.INTRO, true));
                    refreshSettingsRows();
                } else if (i == 1) {
                    cityIdx = (cityIdx + (dir > 0 ? 1 : City.ALL.length - 1)) % City.ALL.length;
                    Pref.set(Pref.CITY, City.ALL[cityIdx].name);
                    applyCity();
                    tick();
                    refreshSettingsRows();
                }
                break;
            case L_TUNE: {
                int v = Math.max(-15, Math.min(15, Pref.i("tune" + i, 0) + dir));
                Pref.set("tune" + i, v);
                dirty = true;
                tick();
                el(i).val = (v == 0 ? "٠" : (v > 0 ? "+" : "-") + Fmt.digits(Math.abs(v))) + " د";
                el(i).sub = tFull[i];
                break;
            }
            default: break;
        }
        kick();
    }

    private El el(int rowIdx) {
        for (El e : listEls) if (e.kind == K_ROW && e.idx == rowIdx) return e;
        return listEls.get(0);
    }

    void refreshSettingsRows() {
        int f = lf;
        float sc0 = scrollTarget;
        openSettings();
        lf = f;
        scrollTarget = sc0;
        scroll = sc0;
    }

    static boolean isCenter(int k) {
        return k == KEY_CENTER || k == KEY_ENTER || k == 160 || k == 96;
    }

    boolean key(int k) {
        switch (k) {
            case KEY_LEFT: return move(0);
            case KEY_UP: return move(1);
            case KEY_RIGHT: return move(2);
            case KEY_DOWN: return move(3);
            default: return false;
        }
    }

    void activate() {
        if (introActive()) {
            skipIntro();
            kick();
            return;
        }
        if (legal) {
            legal = false;
            kick();
            return;
        }
        activate(currentEl());
    }

    void activate(El e) {
        if (e == null) return;
        switch (e.kind) {
            case K_SETTINGS: openSettings(); break;
            case K_NET: host.openSystem("android.settings.WIFI_SETTINGS"); break;
            case K_RADIO: setMode(QURAN); openStations(); listBack = QURAN; break;
            case K_WARN: setMode(CLOCKSET); break;
            case K_PRAYER: setMode(TODAY); break;
            case K_QURAN: setMode(QURAN); break;
            case K_PLAYER: setMode(QURAN); break;
            case K_PLAY:
                if (!plActive && !folder.isEmpty()) host.player(Player.CMD_TRACK, 0); // 0 = من آخر ملف
                else if (!plActive) setMode(QURAN);
                else host.player(Player.CMD_TOGGLE, 0);
                break;
            case K_NEXT: host.player(Player.CMD_NEXT, 0); break;
            case K_PREV: host.player(Player.CMD_PREV, 0); break;
            case K_REPEAT: {
                int r = (Player.repeat() + 1) % 3;
                Pref.set(Pref.REPEAT, r);
                host.player(Player.CMD_REPEAT, r);
                say(Player.repeatName(r));
                break;
            }
            case K_SCREENOFF: host.screenOff(); break;
            case K_TIMER: cycleSleep(); break;
            case K_ADHKAR: openAdhkarCats(); break;
            case K_TASBEEH: setMode(TASBEEH); break;
            case K_LOGO: openLegal(); break;
            case K_FAV:
            case K_MORE:
            case K_APP: if (e.app != null) host.launch(e.app); break;
            case K_ALL: setMode(APPS); break;
            case K_BACK: back(); break;
            case K_BTN: onButton(e); break;
            case K_ROW:
                if (mode == QURAN) host.player(Player.CMD_TRACK, e.idx + 1);
                else onRow(e);
                break;
            case K_CARD:
                if (mode == ADHKAR) tapDhikr();
                else if (mode == TASBEEH) tapTasbeeh();
                break;
            case K_FIELD: csField = e.idx; kick(); break;
            default: break;
        }
        kick();
    }

    void cycleSleep() {
        int[] opts = {0, 15, 30, 45, 60, 90};
        long left = Player.sleepAt > Clock.mono() ? (Player.sleepAt - Clock.mono()) / 60000 : 0;
        int ix = 0;
        for (int i = 0; i < opts.length; i++) if (left > opts[i] - 1 && left > 0 && opts[i] <= left + 1) ix = i;
        if (left == 0) ix = 0;
        int nx = (ix + 1) % opts.length;
        host.player(Player.CMD_SLEEP, opts[nx]);
        say(opts[nx] == 0 ? "تم إلغاء مؤقت النوم" : "الصوت هيقف بعد " + Fmt.digits(opts[nx]) + " دقيقة");
    }

    void tapDhikr() {
        if (adhList.length == 0) return;
        Content.Dhikr d = adhList[adhIdx];
        if (adhDone[adhIdx] < d.count) adhDone[adhIdx]++;
        if (adhDone[adhIdx] >= d.count) {
            doneAnim = 0.5f;
            if (adhIdx < adhList.length - 1) adhIdx++;
        }
    }

    void tapTasbeeh() {
        tasCount++;
        tasTotal++;
        if (tasCount > tasGoal) tasCount = 1;
        Pref.set(Pref.TASBEEH, tasCount);
        Pref.set(Pref.TASBEEH_TOTAL, tasTotal);
    }

    void onButton(El e) {
        switch (e.idx) {
            case B_CITY: openCities(); listBack = TODAY; break;
            case B_HADJ: break;
            case B_TUNE: openTune(); listBack = TODAY; break;
            case B_FOLDER:
                browseDir = null;
                openFolderList();
                break;
            case B_STATIONS: openStations(); break;
            case B_SLEEP: cycleSleep(); e.label = sleepLabel(); break;
            case B_SELALL:
                if (selected.isEmpty()) for (File f : tracks) selected.add(f.getName());
                else selected.clear();
                Media.saveSelection(selected);
                host.player(Player.CMD_RELOAD, 0);
                for (El r : quranEls) if (r.kind == K_ROW) r.on = selected.contains(tracks.get(r.idx).getName());
                e.label = selected.isEmpty() ? "تحديد الكل" : "مسح التحديد";
                break;
            case B_PERM: host.requestStorage(); break;
            case B_APPLY: applyClockSet(); break;
            case B_RESETCLOCK: Clock.reset(); dirty = true; tick(); say("رجعت لساعة الشاشة"); buildClockSet(); break;
            case B_SYSTIME: host.openSystem("android.settings.DATE_SETTINGS"); break;
            case B_PREVD: stepDhikr(-1); break;
            case B_NEXTD: stepDhikr(1); break;
            case B_RESETD:
                if (adhDone.length > 0) adhDone[adhIdx] = 0;
                break;
            case B_GOAL: break;
            case B_RESETT: tasCount = 0; Pref.set(Pref.TASBEEH, 0); break;
            default: break;
        }
    }

    void onRow(El e) {
        int i = e.idx;
        switch (listId) {
            case L_CITY:
                cityIdx = i;
                Pref.set(Pref.CITY, City.ALL[i].name);
                applyCity();
                tick();
                say("تم اختيار " + city.name);
                back();
                break;
            case L_THEME:
                setTheme(i);
                back();
                break;
            case L_STATION:
                host.player(Player.CMD_STATION, i);
                mode = HOME;
                setMode(QURAN);
                break;
            case L_FOLDER:
                if (browseDir != null && i == 0) {
                    folder = browseDir.getPath();
                    Pref.set(Pref.FOLDER, folder);
                    Pref.set(Media.KEY_SEL, "");
                    host.player(Player.CMD_RELOAD, 0);
                    say("تم تحديد الفولدر");
                    tick();
                    mode = HOME;
                    setMode(QURAN);
                } else {
                    int j = browseDir == null ? i : i - 1;
                    if (j >= 0 && j < browseList.size()) {
                        browseDir = browseList.get(j);
                        openFolderList();
                    }
                }
                break;
            case L_AUTOAPP:
                Pref.set(Pref.AUTO_APP, i == 0 ? "" : AppRepo.apps.get(i - 1).pkg);
                say(i == 0 ? "تم إلغاء التطبيق التلقائي" : "هيفتح " + AppRepo.apps.get(i - 1).label + " عند تشغيل الشاشة");
                openSettings();
                break;
            case L_SETTINGS: onSettingsRow(i); break;
            case L_TUNE: adjustList(i, 1); break;
            case L_ADHKAR: openAdhkar(i); break;
            case L_APPMENU: onAppMenu(i); break;
            default: break;
        }
    }

    AppItem menuApp;
    int menuKind;

    void onAppMenu(int i) {
        AppItem a = menuApp;
        if (a == null) return;
        if (menuKind == K_FAV || menuKind == K_MORE) {
            String key = menuKind == K_FAV ? AppRepo.KEY_FAVS : AppRepo.KEY_MORE;
            if (i == 0) {
                AppRepo.removeFrom(key, a.pkg);
                rebuildHome();
            } else if (i == 1) {
                host.appInfo(a.pkg);
            }
        } else {
            if (i == 0) {
                AppRepo.addTo(AppRepo.KEY_FAVS, a.pkg);
                rebuildHome();
                say("تمت الإضافة إلى المفضلة");
            } else if (i == 1) {
                AppRepo.addTo(AppRepo.KEY_MORE, a.pkg);
                rebuildHome();
                say("تمت الإضافة إلى صف المزيد");
            } else if (i == 2) {
                host.appInfo(a.pkg);
            }
        }
        back();
    }

    void onSettingsRow(int i) {
        switch (i) {
            case 0: openThemes(); listBack = HOME; break;
            case 1: openCities(); break;
            case 2: setMode(CLOCKSET); break;
            case 3: openTune(); break;
            case 4: break;
            case 5: openAutoApp(); break;
            case 6:
            case 7: adjustList(i, 1); break;
            case 8: host.enableGuard(); break;
            case 9: browseDir = null; openFolderList(); listBack = HOME; break;
            case 10: host.openSystem("android.settings.SETTINGS"); break;
            case 11: openLegal(); break;
            case 12:
            case 13: adjustList(i, 1); break;
            default: break;
        }
    }

    void longPress() {
        final El e = currentEl();
        if (e == null) return;
        if (mode == QURAN && e.kind == K_ROW) {
            String n = tracks.get(e.idx).getName();
            if (selected.contains(n)) selected.remove(n);
            else selected.add(n);
            e.on = selected.contains(n);
            Media.saveSelection(selected);
            host.player(Player.CMD_RELOAD, 0);
            for (El b : quranEls) if (b.kind == K_BTN && b.idx == B_SELALL) b.label = selected.isEmpty() ? "تحديد الكل" : "مسح التحديد";
            kick();
            return;
        }
        if (e.app == null) return;
        menuApp = e.app;
        menuKind = e.kind;
        int back = mode;
        if (e.kind == K_FAV || e.kind == K_MORE) {
            openList(L_APPMENU, e.app.label, "", back);
            addRow("إزالة من الشاشة الرئيسية", "", "", false);
            addRow("معلومات التطبيق", "", "", false);
            addRow("إلغاء", "", "", false);
        } else {
            openList(L_APPMENU, e.app.label, "", back);
            addRow("إضافة إلى المفضلة", "", "", false);
            addRow("إضافة إلى صف المزيد", "", "", false);
            addRow("معلومات التطبيق / إلغاء التثبيت", "", "", false);
            addRow("إلغاء", "", "", false);
        }
        showList(back);
    }

    // ---------------- لمس / ماوس ----------------

    private float downY, lastY;
    private boolean dragged;

    int hit(float vx, float vy) {
        ArrayList<El> l = cur();
        for (int i = l.size() - 1; i >= 0; i--) {
            El e = l.get(i);
            float y = e.sc ? vy + scroll : vy;
            if (e.sc && (vy < viewTop - 10 || vy > viewBot)) continue;
            if (e.r.contains(vx, y)) return i;
        }
        return -1;
    }

    void setFocus(int i) {
        if (i < 0 || i == curFocus()) return;
        setCurFocus(i);
        kick();
    }

    void touch(int action, float x, float y) {
        float vx = (x - ox) / sc, vy = (y - oy) / sc;
        switch (action) {
            case 0:
                downY = lastY = y;
                dragged = false;
                break;
            case 2:
                if (scrollMax > 0) {
                    if (Math.abs(y - downY) > 14) dragged = true;
                    if (dragged) {
                        scrollTarget = Math.max(0, Math.min(scrollMax, scrollTarget - (y - lastY) / sc));
                        scroll = scrollTarget;
                        kick();
                    }
                    lastY = y;
                }
                break;
            case 1:
                if (!dragged) {
                    if (introActive()) {
                        skipIntro();
                        kick();
                        break;
                    }
                    if (legal) {
                        legal = false;
                        kick();
                        break;
                    }
                    int i = hit(vx, vy);
                    if (i >= 0) {
                        setFocus(i);
                        activate(currentEl());
                    }
                }
                break;
            default: break;
        }
    }

    void hover(float x, float y) {
        if (legal || introActive()) return;
        setFocus(hit((x - ox) / sc, (y - oy) / sc));
    }

    void scrollBy(float v) {
        if (scrollMax <= 0) return;
        if (legal) {
            legalScroll = Math.max(0, legalScroll - v * 140f);
            kick();
            return;
        }
        scrollTarget = Math.max(0, Math.min(scrollMax, scrollTarget - v * 140f));
        kick();
    }
}
