package com.noor.launcher;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import java.util.ArrayList;

/** أيقونات خطية صغيرة (مسارات SVG مُحوّلة لـ Path) — مرسومة بالكود من غير ملفات صور. */
final class Icons {
    private Icons() {}

    static final int PLAY = 0, PAUSE = 1, NEXT = 2, PREV = 3, REPEAT = 4, REPEAT1 = 5, SHUFFLE = 6, SCREENOFF = 7,
            GEAR = 8, WIFI = 9, RADIO = 10, TV = 11, GLOBE = 12, BAG = 13, FILM = 14, FOLDER = 15, GRID = 16,
            MUSIC = 17, TASBEEH = 18, BOOK = 19, SUN = 20, MOON = 21, MOSQUE = 22, CAL = 23, VOL = 24, TIMER = 25,
            CHECK = 26, BACK = 27, CLOCK = 28, ALERT = 29, LIST = 30, SPARK = 31;
    private static final int N = 32;

    private static final String[][] DEF = new String[N][];

    static {
        DEF[PLAY] = new String[]{"F:M8 5v14l11-7z"};
        DEF[PAUSE] = new String[]{"F:M7 5h4v14H7zM13 5h4v14h-4z"};
        DEF[NEXT] = new String[]{"F:M6 5l9 7-9 7z", "S:M18 5v14"};
        DEF[PREV] = new String[]{"F:M18 5l-9 7 9 7z", "S:M6 5v14"};
        DEF[REPEAT] = new String[]{"S:M17 2l4 4-4 4M3 11V9a3 3 0 013-3h15M7 22l-4-4 4-4M21 13v2a3 3 0 01-3 3H3"};
        DEF[REPEAT1] = new String[]{"S:M17 2l4 4-4 4M3 11V9a3 3 0 013-3h15M7 22l-4-4 4-4M21 13v2a3 3 0 01-3 3H3", "S:M11 10l1.5-1v6"};
        DEF[SHUFFLE] = new String[]{"S:M3 6h3c4 0 5 12 9 12h6M3 18h3c1.5 0 2.5-1.5 3.4-3.2M21 6h-6c-1.6 0-2.7 1.2-3.6 2.8M18 3l3 3-3 3M18 15l3 3-3 3"};
        DEF[SCREENOFF] = new String[]{"SR:3,4,18,12,2", "S:M8 20h8M12 16v4", "S:M3 2l18 20"};
        DEF[GEAR] = new String[]{"SC:12,12,3.2", "S:M12 2.8v2.6M12 18.6v2.6M2.8 12h2.6M18.6 12h2.6M5.5 5.5l1.8 1.8M16.7 16.7l1.8 1.8M18.5 5.5l-1.8 1.8M7.3 16.7l-1.8 1.8"};
        DEF[WIFI] = new String[]{"S:M2.5 9a14 14 0 0119 0M5.5 12.3a9.5 9.5 0 0113 0M8.6 15.6a5 5 0 016.8 0", "FC:12,19,1.2"};
        DEF[RADIO] = new String[]{"FC:12,12,2", "S:M7.8 7.8a6 6 0 000 8.4M16.2 7.8a6 6 0 010 8.4M4.9 4.9a10 10 0 000 14.2M19.1 4.9a10 10 0 010 14.2"};
        DEF[TV] = new String[]{"SR:3,5,18,12,2", "S:M8 21h8M9 2l3 3 3-3"};
        DEF[GLOBE] = new String[]{"SC:12,12,9", "S:M3 12h18M12 3c3 3 3 15 0 18M12 3c-3 3-3 15 0 18"};
        DEF[BAG] = new String[]{"S:M5 8h14l-1 12H6z", "S:M9 8a3 3 0 016 0"};
        DEF[FILM] = new String[]{"SR:3,4,18,16,2", "S:M3 9h18M3 15h18M8 4v16M16 4v16"};
        DEF[FOLDER] = new String[]{"S:M3 7a1 1 0 011-1h5l2 2h9a1 1 0 011 1v9a2 2 0 01-2 2H5a2 2 0 01-2-2z"};
        DEF[GRID] = new String[]{"SR:4,4,6.5,6.5,1.5", "SR:13.5,4,6.5,6.5,1.5", "SR:4,13.5,6.5,6.5,1.5", "SR:13.5,13.5,6.5,6.5,1.5"};
        DEF[MUSIC] = new String[]{"S:M9 18V6l10-2v12", "SC:7,18,2.4", "SC:17,16,2.4"};
        DEF[TASBEEH] = new String[]{"SC:12,5,2", "SC:6,10,2", "SC:18,10,2", "SC:8,17,2", "SC:16,17,2"};
        DEF[BOOK] = new String[]{"S:M4 5a2 2 0 012-2h13v16H6a2 2 0 00-2 2z", "S:M4 19V5"};
        DEF[SUN] = new String[]{"SC:12,12,4", "S:M12 2v3M12 19v3M2 12h3M19 12h3M4.9 4.9l2.1 2.1M17 17l2.1 2.1M19.1 4.9L17 7M7 17l-2.1 2.1"};
        DEF[MOON] = new String[]{"S:M20 14.5A8.5 8.5 0 119.5 4a7 7 0 0010.5 10.5z"};
        DEF[MOSQUE] = new String[]{"S:M3 21V12h4v9M17 21V12h4v9M7 21v-6a5 5 0 0110 0v6M12 6V3M10.5 4.5h3"};
        DEF[CAL] = new String[]{"SR:3,5,18,16,2", "S:M3 10h18M8 3v4M16 3v4"};
        DEF[VOL] = new String[]{"S:M4 9v6h4l5 4V5L8 9z", "S:M16.5 8.5a5 5 0 010 7"};
        DEF[TIMER] = new String[]{"SC:12,13,8", "S:M12 9v4l3 2M9 2h6"};
        DEF[CHECK] = new String[]{"S:M4 12.5l5 5L20 6.5"};
        DEF[BACK] = new String[]{"S:M9 5l7 7-7 7"};
        DEF[CLOCK] = new String[]{"SC:12,12,9", "S:M12 7v5l3.5 2"};
        DEF[ALERT] = new String[]{"S:M12 3l10 18H2z", "S:M12 10v5M12 18v.5"};
        DEF[LIST] = new String[]{"S:M8 6h13M8 12h13M8 18h13M3 6h.5M3 12h.5M3 18h.5"};
        DEF[SPARK] = new String[]{"F:M12 2l2.2 7.3L22 12l-7.8 2.7L12 22l-2.2-7.3L2 12l7.8-2.7z"};
    }

    private static final class Prim {
        int kind; // 0 stroke path, 1 fill path, 2 stroke circle, 3 fill circle, 4 stroke rect
        Path path;
        float a, b, c, d, e;
    }

    private static final ArrayList<Prim>[] CACHE = new ArrayList[N];
    private static final RectF R = new RectF();

    private static ArrayList<Prim> get(int id) {
        if (CACHE[id] != null) return CACHE[id];
        ArrayList<Prim> l = new ArrayList<>();
        for (String s : DEF[id]) {
            Prim p = new Prim();
            int i = s.indexOf(':');
            String t = s.substring(0, i), v = s.substring(i + 1);
            switch (t) {
                case "S": p.kind = 0; p.path = parse(v); break;
                case "F": p.kind = 1; p.path = parse(v); break;
                case "SC": case "FC": {
                    p.kind = t.equals("SC") ? 2 : 3;
                    String[] f = v.split(",");
                    p.a = Float.parseFloat(f[0]);
                    p.b = Float.parseFloat(f[1]);
                    p.c = Float.parseFloat(f[2]);
                    break;
                }
                default: {
                    p.kind = 4;
                    String[] f = v.split(",");
                    p.a = Float.parseFloat(f[0]);
                    p.b = Float.parseFloat(f[1]);
                    p.c = Float.parseFloat(f[2]);
                    p.d = Float.parseFloat(f[3]);
                    p.e = Float.parseFloat(f[4]);
                }
            }
            l.add(p);
        }
        CACHE[id] = l;
        return l;
    }

    /** يرسم الأيقونة في مربع حجمه size مركزه (cx,cy). */
    static void draw(Canvas c, int id, float cx, float cy, float size, int color, Paint p, float strokeW) {
        ArrayList<Prim> l = get(id);
        c.save();
        c.translate(cx - size / 2f, cy - size / 2f);
        float s = size / 24f;
        c.scale(s, s);
        p.setColor(color);
        p.setStrokeWidth(strokeW);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeJoin(Paint.Join.ROUND);
        for (Prim q : l) {
            switch (q.kind) {
                case 0: p.setStyle(Paint.Style.STROKE); c.drawPath(q.path, p); break;
                case 1: p.setStyle(Paint.Style.FILL); c.drawPath(q.path, p); break;
                case 2: p.setStyle(Paint.Style.STROKE); c.drawCircle(q.a, q.b, q.c, p); break;
                case 3: p.setStyle(Paint.Style.FILL); c.drawCircle(q.a, q.b, q.c, p); break;
                default:
                    p.setStyle(Paint.Style.STROKE);
                    R.set(q.a, q.b, q.a + q.c, q.b + q.d);
                    c.drawRoundRect(R, q.e, q.e, p);
            }
        }
        c.restore();
        p.setStyle(Paint.Style.FILL);
        p.setStrokeCap(Paint.Cap.BUTT);
    }

    // ---------------- محلّل مسارات SVG مبسّط ----------------

    private static Path parse(String d) {
        Path path = new Path();
        int[] pos = {0};
        char cmd = 'M';
        float cx = 0, cy = 0, sx = 0, sy = 0, lcx = 0, lcy = 0;
        char prev = 'M';
        int n = d.length();
        while (true) {
            skip(d, pos);
            if (pos[0] >= n) break;
            char ch = d.charAt(pos[0]);
            if (Character.isLetter(ch)) {
                cmd = ch;
                pos[0]++;
                if (cmd == 'z' || cmd == 'Z') {
                    path.close();
                    cx = sx;
                    cy = sy;
                    prev = 'Z';
                    continue;
                }
            }
            boolean rel = Character.isLowerCase(cmd);
            switch (Character.toUpperCase(cmd)) {
                case 'M': {
                    float x = num(d, pos), y = num(d, pos);
                    if (rel) { x += cx; y += cy; }
                    path.moveTo(x, y);
                    cx = sx = x;
                    cy = sy = y;
                    cmd = rel ? 'l' : 'L';
                    prev = 'M';
                    break;
                }
                case 'L': {
                    float x = num(d, pos), y = num(d, pos);
                    if (rel) { x += cx; y += cy; }
                    path.lineTo(x, y);
                    cx = x;
                    cy = y;
                    prev = 'L';
                    break;
                }
                case 'H': {
                    float x = num(d, pos);
                    if (rel) x += cx;
                    path.lineTo(x, cy);
                    cx = x;
                    prev = 'L';
                    break;
                }
                case 'V': {
                    float y = num(d, pos);
                    if (rel) y += cy;
                    path.lineTo(cx, y);
                    cy = y;
                    prev = 'L';
                    break;
                }
                case 'C': {
                    float x1 = num(d, pos), y1 = num(d, pos), x2 = num(d, pos), y2 = num(d, pos), x = num(d, pos), y = num(d, pos);
                    if (rel) { x1 += cx; y1 += cy; x2 += cx; y2 += cy; x += cx; y += cy; }
                    path.cubicTo(x1, y1, x2, y2, x, y);
                    lcx = x2;
                    lcy = y2;
                    cx = x;
                    cy = y;
                    prev = 'C';
                    break;
                }
                case 'S': {
                    float x2 = num(d, pos), y2 = num(d, pos), x = num(d, pos), y = num(d, pos);
                    if (rel) { x2 += cx; y2 += cy; x += cx; y += cy; }
                    float x1 = prev == 'C' ? 2 * cx - lcx : cx, y1 = prev == 'C' ? 2 * cy - lcy : cy;
                    path.cubicTo(x1, y1, x2, y2, x, y);
                    lcx = x2;
                    lcy = y2;
                    cx = x;
                    cy = y;
                    prev = 'C';
                    break;
                }
                case 'Q': {
                    float x1 = num(d, pos), y1 = num(d, pos), x = num(d, pos), y = num(d, pos);
                    if (rel) { x1 += cx; y1 += cy; x += cx; y += cy; }
                    path.quadTo(x1, y1, x, y);
                    cx = x;
                    cy = y;
                    prev = 'Q';
                    break;
                }
                case 'A': {
                    float rx = num(d, pos), ry = num(d, pos), rot = num(d, pos);
                    int large = flag(d, pos), sweep = flag(d, pos);
                    float x = num(d, pos), y = num(d, pos);
                    if (rel) { x += cx; y += cy; }
                    arc(path, cx, cy, rx, ry, rot, large != 0, sweep != 0, x, y);
                    cx = x;
                    cy = y;
                    prev = 'A';
                    break;
                }
                default:
                    pos[0]++;
            }
        }
        return path;
    }

    private static void skip(String d, int[] p) {
        while (p[0] < d.length() && (d.charAt(p[0]) == ' ' || d.charAt(p[0]) == ',')) p[0]++;
    }

    private static int flag(String d, int[] p) {
        skip(d, p);
        char c = d.charAt(p[0]++);
        return c == '1' ? 1 : 0;
    }

    private static float num(String d, int[] p) {
        skip(d, p);
        int s = p[0], i = s, n = d.length();
        if (i < n && (d.charAt(i) == '-' || d.charAt(i) == '+')) i++;
        boolean dot = false;
        while (i < n) {
            char c = d.charAt(i);
            if (c >= '0' && c <= '9') i++;
            else if (c == '.' && !dot) { dot = true; i++; }
            else break;
        }
        p[0] = i;
        return Float.parseFloat(d.substring(s, i));
    }

    /** قوس SVG → منحنيات بيزييه. */
    private static void arc(Path path, float x1, float y1, float rx, float ry, float rotDeg, boolean large, boolean sweep, float x2, float y2) {
        if (rx == 0 || ry == 0 || (x1 == x2 && y1 == y2)) {
            path.lineTo(x2, y2);
            return;
        }
        rx = Math.abs(rx);
        ry = Math.abs(ry);
        double phi = Math.toRadians(rotDeg), cp = Math.cos(phi), sp = Math.sin(phi);
        double dx = (x1 - x2) / 2.0, dy = (y1 - y2) / 2.0;
        double x1p = cp * dx + sp * dy, y1p = -sp * dx + cp * dy;
        double lam = (x1p * x1p) / (rx * (double) rx) + (y1p * y1p) / (ry * (double) ry);
        if (lam > 1) {
            double sl = Math.sqrt(lam);
            rx *= sl;
            ry *= sl;
        }
        double num = rx * rx * ry * ry - rx * rx * y1p * y1p - ry * ry * x1p * x1p;
        double den = rx * rx * y1p * y1p + ry * ry * x1p * x1p;
        double co = Math.sqrt(Math.max(0, num / den)) * (large == sweep ? -1 : 1);
        double cxp = co * rx * y1p / ry, cyp = -co * ry * x1p / rx;
        double cx = cp * cxp - sp * cyp + (x1 + x2) / 2.0, cy = sp * cxp + cp * cyp + (y1 + y2) / 2.0;
        double th1 = Math.atan2((y1p - cyp) / ry, (x1p - cxp) / rx);
        double th2 = Math.atan2((-y1p - cyp) / ry, (-x1p - cxp) / rx);
        double dth = th2 - th1;
        if (sweep && dth < 0) dth += 2 * Math.PI;
        else if (!sweep && dth > 0) dth -= 2 * Math.PI;
        int segs = (int) Math.ceil(Math.abs(dth) / (Math.PI / 2) - 1e-9);
        double step = dth / segs;
        double t = 4.0 / 3.0 * Math.tan(step / 4);
        double a = th1;
        for (int i = 0; i < segs; i++) {
            double ca = Math.cos(a), sa = Math.sin(a), cb = Math.cos(a + step), sb = Math.sin(a + step);
            double e1x = ca - t * sa, e1y = sa + t * ca, e2x = cb + t * sb, e2y = sb - t * cb;
            double[] p1 = ell(cx, cy, rx, ry, cp, sp, e1x, e1y), p2 = ell(cx, cy, rx, ry, cp, sp, e2x, e2y), p3 = ell(cx, cy, rx, ry, cp, sp, cb, sb);
            path.cubicTo((float) p1[0], (float) p1[1], (float) p2[0], (float) p2[1], (float) p3[0], (float) p3[1]);
            a += step;
        }
    }

    private static double[] ell(double cx, double cy, double rx, double ry, double cp, double sp, double x, double y) {
        double px = rx * x, py = ry * y;
        return new double[]{cp * px - sp * py + cx, sp * px + cp * py + cy};
    }
}
