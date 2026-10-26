package com.noor.launcher;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;

import java.util.List;

/** 2) الزمرد والشمس: أخضر عميق + قوس الشمس + بطاقات عاجية. */
final class ThemeB extends Theme {
    private static final float CX = 679f, CY = 653f, RAD = 400f;
    private final RectF rr = new RectF(), arcR = new RectF(CX - RAD, CY - RAD, CX + RAD, CY + RAD);
    private final Path card = new Path();
    private Shader ivory, gold, sunGlow;
    private final int cream = 0xFFF4EBD3, dark = 0xFF0C3B2E;

    ThemeB() {
        super("الزمرد والشمس");
        bg1 = 0xFF0A4A3C;
        bg2 = 0xFF04140F;
        tile = 0xFF0F3E34;
        tileF = 0xFF1A5C4C;
        panel = 0xFF0B3028;
        txt = 0xFFF4EBD3;
        dim = 0xFFA7C4B8;
        accent = 0xFFE8C46A;
        onAccent = 0xFF2A1E05;
        good = 0xFF7FE0A8;
        bad = 0xFFFF9E8A;
        line = 0x33E8C46A;
        // بطاقة الذكر: قبة من فوق
        float l = 1340, t = 392, r = 1856, b = 686, tr = 170, br = 40;
        card.moveTo(l, t + tr);
        card.arcTo(new RectF(l, t, l + 2 * tr, t + 2 * tr), 180, 90, false);
        card.lineTo(r - tr, t);
        card.arcTo(new RectF(r - 2 * tr, t, r, t + 2 * tr), 270, 90, false);
        card.lineTo(r, b - br);
        card.arcTo(new RectF(r - 2 * br, b - 2 * br, r, b), 0, 90, false);
        card.lineTo(l + br, b);
        card.arcTo(new RectF(l, b - 2 * br, l + 2 * br, b), 90, 90, false);
        card.close();
    }

    @Override
    void base(Canvas c, Ui u) {
        Paint p = new Paint();
        p.setShader(new LinearGradient(0, 0, Ui.W, Ui.H, 0xFF0B4D3E, 0xFF03120E, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, Ui.W, Ui.H, p);
        Paint g = new Paint();
        g.setShader(new RadialGradient(380, 260, 900, new int[]{0x990F7A5F, 0x44095640, 0x00053B2E}, new float[]{0f, 0.5f, 1f}, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, Ui.W, Ui.H, g);
        Paint s = new Paint(Paint.ANTI_ALIAS_FLAG);
        s.setColor(0x14FFFFFF);
        c.drawCircle(1700, 1180, 640, s);
        pattern(c, 0xFFFFFFFF, 0.05f);
    }

    @Override
    void layout(Ui u) {
        u.addHome(Ui.K_SETTINGS, 0, 64, 40, 118, 94);
        u.addHome(Ui.K_NET, 0, 132, 40, 322, 94);
        u.addHome(Ui.K_RADIO, 0, 336, 40, 566, 94);
        u.addHome(Ui.K_WARN, 0, 580, 40, 760, 94);
        u.addHome(Ui.K_PRAYER, 0, 64, 120, 1294, 686);
        u.addHome(Ui.K_ADHKAR, 0, 1340, 392, 1856, 686);
        u.addHome(Ui.K_TASBEEH, 0, 1470, 598, 1726, 646);
        u.addHome(Ui.K_PLAYER, 0, 1380, 708, 1852, 784);
        u.addHome(Ui.K_SCREENOFF, 0, 84, 718, 310, 774);
        u.addHome(Ui.K_REPEAT, 0, 330, 722, 378, 770);
        u.addHome(Ui.K_PREV, 0, 382, 722, 430, 770);
        u.addHome(Ui.K_PLAY, 0, 437, 716, 497, 776);
        u.addHome(Ui.K_NEXT, 0, 503, 722, 551, 770);
        u.addHome(Ui.K_TIMER, 0, 555, 722, 603, 770);
        int i = 0;
        float top = 812, bot = 944;
        Ui.El q = u.addHome(Ui.K_QURAN, 0, 1856 - 166, top, 1856, bot);
        q.label = "مشغل القرآن";
        i = 1;
        for (int k = 0; k < u.favs.size(); k++, i++) {
            float right = 1856 - i * 180;
            Ui.El e = u.addHome(Ui.K_FAV, k, right - 166, top, right, bot);
            e.app = u.favs.get(k);
            e.label = u.fit(e.app.label, 21, 150, Fonts.bold);
        }
        for (int k = 0; k < u.more.size(); k++, i++) {
            float right = 1856 - i * 180;
            Ui.El e = u.addHome(Ui.K_MORE, k, right - 166, top, right, bot);
            e.app = u.more.get(k);
            e.label = u.fit(e.app.label, 21, 150, Fonts.bold);
        }
        float right = 1856 - i * 180;
        u.addHome(Ui.K_ALL, u.more.size(), right - 166, top, right, bot);
        u.addHome(Ui.K_LOGO, 0, 56, 994, 560, 1068);
    }

    private void chip(Canvas c, Ui u, Ui.El e, String label, int ico, int icoColor) {
        u.pop(c, e, 0.05f);
        float rad = e.r.height() / 2f;
        u.fill(c, e.r, rad, Ui.lerp(0x33FFFFFF, 0x66FFFFFF, e.f));
        u.stroke(c, e.r, rad, 0x33FFFFFF, 1.3f);
        u.ring(c, e, rad, cream);
        u.icon(c, ico, e.r.right - 34, e.cy(), 26, icoColor, 1.9f);
        u.text(c, label, e.r.right - 58, e.cy() + 9, 25, cream, Paint.Align.RIGHT, Fonts.medium);
        c.restore();
    }

    @Override
    void home(Canvas c, Ui u) {
        final Paint.Align R = Paint.Align.RIGHT, C = Paint.Align.CENTER;
        if (u.entBegin(c, 0.02f)) {
            salawat(c, u, 960, 64, 42, accent);
            u.tmp.set(1802, 34, 1856, 88);
            u.fillShader(c, u.tmp, 16, goldS());
            u.icon(c, Icons.MOON, 1829, 61, 30, onAccent, 2.2f);
            u.text(c, "Abdou-Luncher", 1788, 62, 30, cream, R, Fonts.bold);
            u.text(c, u.hijriShort, 1788, 86, 18, dim, R, Fonts.regular);
            u.entEnd(c);
        }
        if (u.entBegin(c, 0.08f)) {
            float w = u.measure(u.clockDigits, 150, Fonts.extra);
            u.text(c, u.clockDigits, 1856, 250, 150, cream, R, Fonts.extra);
            u.text(c, u.clockSfx, 1856 - w - 20, 250, 52, accent, R, Fonts.bold);
            u.text(c, u.dateStr, 1856, 322, 40, cream, R, Fonts.bold);
            u.entEnd(c);
        }
        if (u.entBegin(c, 0.12f)) {
            heroCard(c, u);
            u.entEnd(c);
        }
        if (u.entBegin(c, 0.2f)) {
            dhikrCard(c, u);
            u.entEnd(c);
        }
        int n = u.homeEls.size();
        for (int pass = 0; pass < 2; pass++) {
            for (int i = 0; i < n; i++) {
                if ((i == u.hf) != (pass == 1)) continue;
                Ui.El e = u.homeEls.get(i);
                if (!u.entBegin(c, e.delay)) continue;
                el(c, u, e);
                u.entEnd(c);
            }
        }
    }

    private Shader goldS() {
        if (gold == null) gold = new LinearGradient(0, 0, 0, 1080, 0xFFF9DE8F, 0xFFC8932F, Shader.TileMode.CLAMP);
        return gold;
    }

    private static float[] pt(float theta) {
        double a = Math.toRadians(theta);
        return new float[]{CX + RAD * (float) Math.cos(a), CY - RAD * (float) Math.sin(a)};
    }

    private void heroCard(Canvas c, Ui u) {
        Ui.El e = u.find(u.homeEls, Ui.K_PRAYER, 0);
        u.pop(c, e, 0.012f);
        u.fill(c, e.r, 40, Ui.lerp(0x55052E26, 0x7710493C, e.f));
        u.stroke(c, e.r, 40, Ui.lerp(0x40E8C46A, accent, e.f), 1.5f + 2f * e.f);
        // القوس
        u.pStroke.setStrokeCap(Paint.Cap.BUTT);
        float theta = u.dayFrac >= 0 ? u.dayFrac * 180f : -1f;
        u.pStroke.setPathEffect(new android.graphics.DashPathEffect(new float[]{3, 12}, 0));
        u.pStroke.setStrokeWidth(3.5f);
        u.pStroke.setColor(u.col(0x66E8C46A));
        float futStart = 180f, futSweep = theta >= 0 ? 180f - theta : 180f;
        if (theta < 0 && u.nowIdx < 0) futSweep = 180f;
        c.drawArc(arcR, futStart, futSweep, false, u.pStroke);
        u.pStroke.setPathEffect(null);
        float pastSweep = theta >= 0 ? theta : (u.nowIdx >= Prayer.ISHA ? 180f : 0f);
        if (pastSweep > 0.5f) {
            u.pStroke.setStrokeCap(Paint.Cap.ROUND);
            u.pStroke.setStrokeWidth(6f);
            u.pStroke.setColor(u.col(accent));
            c.drawArc(arcR, 360f - pastSweep, pastSweep, false, u.pStroke);
            u.pStroke.setStrokeCap(Paint.Cap.BUTT);
        }
        u.pStroke.setStrokeWidth(1.5f);
        u.pStroke.setColor(u.col(0x33FFFFFF));
        c.drawLine(123, CY, 1235, CY, u.pStroke);
        // علامات الصلوات + أسمائها
        long f0 = u.today[Prayer.FAJR], span = Math.max(1L, u.today[Prayer.ISHA] - f0);
        for (int i = 0; i < 6; i++) {
            float th = Math.max(0f, Math.min(180f, 180f * (u.today[i] - f0) / (float) span));
            float[] p = pt(th);
            boolean past = i <= u.nowIdx, nxt = i == u.nextIdx && !u.nextTomorrow;
            if (nxt) {
                u.circle(c, p[0], p[1], 13, 0xFF0B3A30);
                u.pStroke.setStrokeWidth(3f);
                u.pStroke.setColor(u.col(0xFFFFFFFF));
                c.drawCircle(p[0], p[1], 13, u.pStroke);
            } else if (past) {
                u.circle(c, p[0], p[1], 9, accent);
            } else {
                u.circle(c, p[0], p[1], 9, 0xFF0B3A30);
                u.pStroke.setStrokeWidth(2.5f);
                u.pStroke.setColor(u.col(accent));
                c.drawCircle(p[0], p[1], 9, u.pStroke);
            }
            double cs = Math.cos(Math.toRadians(th));
            float ny, ty, x;
            Paint.Align al;
            if (cs > 0.5) { al = Paint.Align.LEFT; x = p[0] + 26; ny = p[1] - 34; ty = p[1] - 4; }
            else if (cs < -0.5) { al = Paint.Align.RIGHT; x = p[0] - 26; ny = p[1] - 34; ty = p[1] - 4; }
            else { al = Paint.Align.CENTER; x = p[0]; ny = p[1] - 62; ty = p[1] - 32; }
            u.text(c, Prayer.NAMES[i], x, ny, 26, nxt ? 0xFFFFFFFF : cream, al, Fonts.bold);
            u.text(c, u.tFull[i], x, ty, 22, accent, al, Fonts.bold);
        }
        // الشمس
        if (theta >= 0) {
            float[] p = pt(theta);
            if (sunGlow == null) sunGlow = new RadialGradient(0, 0, 70, new int[]{0xCCFFD66B, 0x66F2A93B, 0x00F2A93B}, new float[]{0f, 0.5f, 1f}, Shader.TileMode.CLAMP);
            c.save();
            c.translate(p[0], p[1]);
            u.pFill.setShader(sunGlow);
            u.pFill.setAlpha((int) (255 * u.ga));
            c.drawCircle(0, 0, 70, u.pFill);
            u.pFill.setShader(null);
            c.restore();
            u.circle(c, p[0], p[1], 22, 0xFFF2C14E);
            u.circle(c, p[0], p[1], 12, 0xFFFFF1BF);
        }
        float cx = 679;
        u.text(c, u.panelLabel, cx, 372, 27, u.adhanNow ? accent : dim, Paint.Align.CENTER, Fonts.medium);
        u.text(c, u.panelName, cx, 482, 100, accent, Paint.Align.CENTER, Fonts.serifBold);
        u.text(c, u.cdStr, cx, 580, 86, cream, Paint.Align.CENTER, Fonts.extra);
        u.text(c, u.fit(u.adhanStr, 26, 520, Fonts.regular), cx, 626, 26, dim, Paint.Align.CENTER, Fonts.regular);
        c.restore();
    }

    private void dhikrCard(Canvas c, Ui u) {
        Ui.El e = u.find(u.homeEls, Ui.K_ADHKAR, 0);
        u.pop(c, e, 0.02f);
        if (ivory == null) ivory = new LinearGradient(0, 392, 0, 686, 0xFFFAF2DA, 0xFFEADCB6, Shader.TileMode.CLAMP);
        u.pFill.setShader(ivory);
        u.pFill.setColor(0xFFFFFFFF);
        if (u.ga < 0.999f) u.pFill.setAlpha((int) (255 * u.ga));
        c.drawPath(card, u.pFill);
        u.pFill.setShader(null);
        if (e.f > 0.02f) {
            u.pStroke.setStrokeWidth(5);
            u.pStroke.setColor(u.col(Ui.alpha(0xFFFFFFFF, e.f)));
            c.drawPath(card, u.pStroke);
        }
        float cx = e.cx();
        u.text(c, "حديث اليوم  •  " + u.hadSrc, cx, 456, 20, 0xFF9A6A12, Paint.Align.CENTER, Fonts.bold);
        float size = 34;
        List<String> lines = null;
        for (; size >= 24; size -= 2) {
            lines = u.wrap(u.hadText, size, Fonts.serifBold, 430);
            if (lines.size() <= 3) break;
        }
        float lh = size * 1.45f, y = 505f - (lines.size() > 2 ? 10 : 0);
        u.paragraph(c, lines, cx, y, size, lh, 0xFF0C4A3A, Paint.Align.CENTER, Fonts.serifBold);
        c.restore();
    }

    private void el(Canvas c, Ui u, Ui.El e) {
        switch (e.kind) {
            case Ui.K_SETTINGS: {
                u.pop(c, e, 0.08f);
                u.circle(c, e.cx(), e.cy(), 27, Ui.lerp(0x33FFFFFF, 0x66FFFFFF, e.f));
                u.ring(c, e, 27, cream);
                u.icon(c, Icons.GEAR, e.cx(), e.cy(), 28, cream, 1.9f);
                c.restore();
                break;
            }
            case Ui.K_NET: chip(c, u, e, u.netOk ? "متصل" : "بدون إنترنت", Icons.WIFI, u.netOk ? 0xFF7FE0A8 : bad); break;
            case Ui.K_RADIO: chip(c, u, e, "إذاعة القرآن", Icons.RADIO, accent); break;
            case Ui.K_WARN: if (u.clockWarn != 0) chip(c, u, e, "اضبط الساعة", Icons.ALERT, bad); break;
            case Ui.K_TASBEEH: {
                u.pop(c, e, 0.05f);
                u.fill(c, e.r, 24, Ui.lerp(0x1A0C4A3A, 0x330C4A3A, e.f));
                u.ring(c, e, 24, 0xFF0C4A3A);
                u.icon(c, Icons.TASBEEH, e.r.right - 30, e.cy(), 26, 0xFF0C4A3A, 1.9f);
                u.text(c, "التسبيح  " + Fmt.digits(u.tasCount) + " / " + Fmt.digits(u.tasGoal), e.r.right - 56, e.cy() + 9, 24, 0xFF0C4A3A, Paint.Align.RIGHT, Fonts.bold);
                c.restore();
                break;
            }
            case Ui.K_PLAYER: playerPill(c, u, e); break;
            case Ui.K_SCREENOFF: {
                u.pop(c, e, 0.05f);
                u.fill(c, e.r, 28, Ui.lerp(0x33FFFFFF, 0x66FFFFFF, e.f));
                u.stroke(c, e.r, 28, 0x33FFFFFF, 1.3f);
                u.ring(c, e, 28, cream);
                u.icon(c, Icons.SCREENOFF, e.r.left + 36, e.cy(), 26, cream, 1.9f);
                u.text(c, "إطفاء الشاشة", e.r.right - 22, e.cy() + 9, 25, cream, Paint.Align.RIGHT, Fonts.bold);
                c.restore();
                break;
            }
            case Ui.K_TIMER: {
                u.pop(c, e, 0.08f);
                u.icon(c, Icons.TIMER, e.cx(), e.cy(), 26, Player.sleepAt > Clock.mono() ? accent : cream, 1.9f);
                if (e.f > 0.02f) {
                    u.pStroke.setStrokeWidth(3);
                    u.pStroke.setColor(u.col(Ui.alpha(cream, e.f)));
                    c.drawCircle(e.cx(), e.cy(), 24, u.pStroke);
                }
                c.restore();
                break;
            }
            case Ui.K_REPEAT: case Ui.K_PREV: case Ui.K_NEXT: case Ui.K_PLAY: Screens.controls(c, u, e); break;
            case Ui.K_LOGO: logo(c, u, e); break;
            case Ui.K_QURAN: case Ui.K_FAV: case Ui.K_MORE: dockTile(c, u, e); break;
            case Ui.K_ALL: {
                u.pop(c, e, 0.05f);
                u.fill(c, e.r, 26, Ui.lerp(0x00000000, 0x33FFFFFF, e.f));
                u.dashed(c, e.r, 26, Ui.lerp(0xAAE8C46A, 0xFFFFFFFF, e.f), 2.5f);
                u.icon(c, Icons.GRID, e.cx(), e.top() + 52, 44, accent, 1.9f);
                u.text(c, "كل التطبيقات", e.cx(), e.r.bottom - 24, 20, cream, Paint.Align.CENTER, Fonts.bold);
                u.text(c, Fmt.digits(AppRepo.apps.size()), e.cx(), e.r.bottom - 52, 20, accent, Paint.Align.CENTER, Fonts.extra);
                c.restore();
                break;
            }
            default: break;
        }
    }

    private void dockTile(Canvas c, Ui u, Ui.El e) {
        u.pop(c, e, 0.06f);
        if (e.kind == Ui.K_QURAN) {
            u.fillShader(c, e.r, 26, new LinearGradient(0, e.r.top, 0, e.r.bottom, 0xFFFCE7A3, 0xFFE3AE45, Shader.TileMode.CLAMP));
        } else {
            u.fillShader(c, e.r, 26, ivoryTile(e));
        }
        u.ring(c, e, 26, 0xFFFFFFFF);
        appIconIn(c, u, e, e.cx() - 31, e.r.top + 18, 62);
        u.text(c, e.label, e.cx(), e.r.bottom - 20, 21, dark, Paint.Align.CENTER, Fonts.bold);
        c.restore();
    }

    private Shader ivoryTile(Ui.El e) {
        return new LinearGradient(0, e.r.top, 0, e.r.bottom, 0xFFFAF2DA, 0xFFEADCB6, Shader.TileMode.CLAMP);
    }

    private void playerPill(Canvas c, Ui u, Ui.El e) {
        rr.set(64, 704, 1856, 788);
        u.fill(c, rr, 42, 0x33FFFFFF);
        u.stroke(c, rr, 42, 0x33FFFFFF, 1.3f);
        if (e.f > 0.02f) u.stroke(c, rr, 42, Ui.alpha(cream, e.f), 3f);
        // أيقونة دائرية
        u.circle(c, 1808, 746, 28, 0x330C4A3A);
        u.pStroke.setStrokeWidth(4);
        u.pStroke.setColor(u.col(0x55E8C46A));
        c.drawCircle(1808, 746, 28, u.pStroke);
        u.icon(c, Icons.MUSIC, 1808, 746, 28, accent, 2f);
        u.text(c, u.fit(u.plTitle, 27, 360, Fonts.bold), 1768, 744, 27, cream, Paint.Align.RIGHT, Fonts.bold);
        u.text(c, u.fit(u.plSub, 18, 360, Fonts.regular), 1768, 770, 18, dim, Paint.Align.RIGHT, Fonts.regular);
        rr.set(690, 744, 1380, 749);
        u.fill(c, rr, 3, 0x33FFFFFF);
        if (u.plProg > 0.003f) {
            rr.set(1380 - 690 * u.plProg, 744, 1380, 749);
            u.fill(c, rr, 3, accent);
        }
        u.text(c, u.plPosS, 1412, 754, 19, dim, Paint.Align.RIGHT, Fonts.regular);
        u.text(c, u.plDurS, 664, 754, 19, dim, Paint.Align.RIGHT, Fonts.regular);
    }
}
