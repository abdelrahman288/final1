package com.noor.launcher;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;

import java.util.List;

/** 1) منتصف الليل الذهبي: كحلي عميق + ذهبي + محراب. */
final class ThemeA extends Theme {
    static final float R0 = 1170f;      // الحافة اليمنى للمنطقة اليسرى
    private final Path arch = new Path();
    private Shader archFill, goldBtn, barGold;
    private final RectF rr = new RectF();

    ThemeA() {
        super("منتصف الليل الذهبي");
        bg1 = 0xFF132458;
        bg2 = 0xFF060E26;
        tile = 0xFF182A5E;
        tileF = 0xFF2B4592;
        panel = 0xFF0D1A44;
        txt = 0xFFFFFFFF;
        dim = 0xFF9FB0D9;
        accent = 0xFFF2C14E;
        onAccent = 0xFF1B1305;
        good = 0xFF7FE0A8;
        bad = 0xFFFF8A80;
        line = 0x26FFFFFF;
        float px = 1240, py = 40, pw = 600, ph = 970;
        arch.moveTo(px, py + ph);
        arch.lineTo(px, py + 340);
        arch.cubicTo(px, py + 150, px + pw * 0.30f, py + 40, px + pw / 2f, py);
        arch.cubicTo(px + pw * 0.70f, py + 40, px + pw, py + 150, px + pw, py + 340);
        arch.lineTo(px + pw, py + ph);
        arch.close();
    }

    @Override
    void base(Canvas c, Ui u) {
        Paint p = new Paint();
        p.setShader(new LinearGradient(0, 0, Ui.W, Ui.H, 0xFF0B1A47, 0xFF050C22, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, Ui.W, Ui.H, p);
        Paint g = new Paint();
        g.setShader(new RadialGradient(1540, 520, 820, new int[]{0xAA24449A, 0x5518327A, 0x00101E50}, new float[]{0f, 0.5f, 1f}, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, Ui.W, Ui.H, g);
        Paint g2 = new Paint();
        g2.setShader(new RadialGradient(120, 1000, 620, new int[]{0x6618327A, 0x00101E50}, null, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, Ui.W, Ui.H, g2);
        pattern(c, 0xFFB8C6F0, 0.07f);
    }

    @Override
    void layout(Ui u) {
        Ui.El e;
        u.addHome(Ui.K_SETTINGS, 0, 64, 40, 120, 96).ico = Icons.GEAR;
        e = u.addHome(Ui.K_NET, 0, 136, 40, 346, 96);
        e = u.addHome(Ui.K_RADIO, 0, 362, 40, 602, 96);
        u.addHome(Ui.K_WARN, 0, 1010, 40, 1170, 96);
        for (int i = 0; i < 6; i++) u.addHome(Ui.K_PRAYER, i, R0 - i * 186 - 170, 372, R0 - i * 186, 512);
        for (int i = 0; i < 4; i++) {
            float right = R0 - i * 280;
            Ui.El f = u.addHome(i == 0 ? Ui.K_QURAN : Ui.K_FAV, i == 0 ? 0 : i - 1, right - 264, 566, right, 686);
            if (i == 0) f.label = "مشغل القرآن";
            else if (i - 1 < u.favs.size()) {
                f.app = u.favs.get(i - 1);
                f.label = u.fit(f.app.label, 30, 264 - 44, Fonts.bold);
            } else {
                u.homeEls.remove(u.homeEls.size() - 1);
            }
        }
        for (int i = 0; i < Config.MAX_MORE; i++) {
            if (i >= u.more.size()) break;
            float right = R0 - i * 186;
            Ui.El m = u.addHome(Ui.K_MORE, i, right - 170, 744, right, 848);
            m.app = u.more.get(i);
            m.label = u.fit(m.app.label, 22, 170 - 30, Fonts.bold);
        }
        int n = Math.min(u.more.size(), Config.MAX_MORE);
        float allRight = R0 - n * 186;
        u.addHome(Ui.K_ALL, n, allRight - 170, 744, allRight, 848);
        // المشغل
        u.addHome(Ui.K_PLAYER, 0, 440, 880, 1160, 976);
        u.addHome(Ui.K_SCREENOFF, 0, 96, 880, 176, 984);
        u.addHome(Ui.K_REPEAT, 0, 225, 904, 273, 952);
        u.addHome(Ui.K_PREV, 0, 276, 904, 324, 952);
        u.addHome(Ui.K_PLAY, 0, 330, 894, 398, 962);
        u.addHome(Ui.K_NEXT, 0, 404, 904, 452, 952);
        // المحراب
        u.addHome(Ui.K_ADHKAR, 0, 1548, 790, 1798, 858);
        u.addHome(Ui.K_TASBEEH, 0, 1282, 790, 1532, 858);
        u.addHome(Ui.K_LOGO, 0, 56, 994, 560, 1068);
    }

    private void chip(Canvas c, Ui u, Ui.El e, String label, int ico, int icoColor) {
        u.pop(c, e, 0.05f);
        float rad = e.r.height() / 2f;
        u.fill(c, e.r, rad, Ui.lerp(0xFF152760, tileF, e.f));
        u.stroke(c, e.r, rad, line, 1.3f);
        u.ring(c, e, rad, 0xFFFFFFFF);
        u.icon(c, ico, e.r.right - 36, e.cy(), 28, icoColor, 1.9f);
        u.text(c, label, e.r.right - 62, e.cy() + 9, 26, txt, Paint.Align.RIGHT, Fonts.medium);
        c.restore();
    }

    @Override
    void home(Canvas c, Ui u) {
        final Paint.Align R = Paint.Align.RIGHT, C = Paint.Align.CENTER, L = Paint.Align.LEFT;
        // نص ثابت
        if (u.entBegin(c, 0.02f)) {
            salawat(c, u, 910, 66, 44, accent);
            u.entEnd(c);
        }
        if (u.entBegin(c, 0.08f)) {
            float w = u.measure(u.clockDigits, 134, Fonts.extra);
            u.text(c, u.clockDigits, R0, 222, 134, txt, R, Fonts.extra);
            u.text(c, u.clockSfx, R0 - w - 18, 222, 48, accent, R, Fonts.bold);
            u.text(c, u.dateStr, R0, 298, 44, txt, R, Fonts.bold);
            u.text(c, u.hijriStr, R0, 342, 30, dim, R, Fonts.regular);
            u.entEnd(c);
        }
        if (u.entBegin(c, 0.2f)) {
            u.text(c, "المفضلة", R0, 552, 26, dim, R, Fonts.medium);
            u.text(c, "المزيد", R0, 730, 26, dim, R, Fonts.medium);
            u.entEnd(c);
        }
        if (u.entBegin(c, 0.15f)) {
            drawMihrab(c, u);
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

    private void drawMihrab(Canvas c, Ui u) {
        float cx = 1540;
        if (archFill == null) archFill = new LinearGradient(0, 40, 0, 1010, 0xFF1A2F78, 0xFF0A1742, Shader.TileMode.CLAMP);
        u.pFill.setShader(archFill);
        u.pFill.setColor(0xFFFFFFFF);
        if (u.ga < 0.999f) u.pFill.setAlpha((int) (255 * u.ga));
        c.drawPath(arch, u.pFill);
        u.pFill.setShader(null);
        u.pStroke.setPathEffect(null);
        u.pStroke.setStrokeWidth(4);
        u.pStroke.setColor(u.col(accent));
        c.drawPath(arch, u.pStroke);
        c.save();
        c.scale(0.955f, 0.972f, cx, 1010);
        u.pStroke.setStrokeWidth(2);
        u.pStroke.setColor(u.col(Ui.alpha(accent, 0.4f)));
        c.drawPath(arch, u.pStroke);
        c.restore();

        u.icon(c, Icons.MOON, cx, 142, 54, accent, 2f);
        u.text(c, "آية اليوم", cx, 200, 22, accent, Paint.Align.CENTER, Fonts.medium);
        // الآية
        float size = 42, lh;
        List<String> lines = null;
        for (; size >= 28; size -= 2) {
            lines = u.wrap(u.ayaText, size, Fonts.quran, 470);
            if (lines.size() <= (size > 34 ? 2 : 3)) break;
        }
        lh = size * 1.5f;
        float y = 250f - (lines.size() - 1) * lh * 0.1f;
        y = u.paragraph(c, lines, cx, y, size, lh, 0xFFF3F6FF, Paint.Align.CENTER, Fonts.quran);
        u.text(c, u.ayaSrc, cx, y - lh + 40, 22, dim, Paint.Align.CENTER, Fonts.regular);
        u.pStroke.setStrokeWidth(1.5f);
        u.pStroke.setColor(u.col(0x33FFFFFF));
        c.drawLine(cx - 150, 392, cx + 150, 392, u.pStroke);

        u.text(c, u.panelLabel, cx, 440, 30, u.adhanNow ? accent : dim, Paint.Align.CENTER, Fonts.medium);
        u.text(c, u.panelName, cx, 540, 94, accent, Paint.Align.CENTER, Fonts.serifBold);
        u.text(c, u.cdStr, cx, 660, 112, txt, Paint.Align.CENTER, Fonts.extra);
        u.text(c, u.fit(u.adhanStr, 26, 500, Fonts.regular), cx, 712, 26, dim, Paint.Align.CENTER, Fonts.regular);
        // شريط التقدم (من اليمين)
        rr.set(1350, 740, 1730, 748);
        u.fill(c, rr, 4, 0x33FFFFFF);
        rr.set(1730 - 380 * u.progress, 740, 1730, 748);
        u.fill(c, rr, 4, accent);
        if (u.ramadan && !u.suhoorStr.isEmpty()) u.text(c, u.suhoorStr, cx, 776, 22, dim, Paint.Align.CENTER, Fonts.regular);
        // مناسبة
        rr.set(1290, 886, 1790, 972);
        u.fill(c, rr, 26, 0x1AFFFFFF);
        u.stroke(c, rr, 26, 0x26FFFFFF, 1.2f);
        u.icon(c, Icons.CAL, 1748, 929, 40, accent, 1.9f);
        u.text(c, u.occTitle, 1700, 920, 20, accent, Paint.Align.RIGHT, Fonts.medium);
        u.text(c, u.fit(u.occText, 26, 380, Fonts.bold), 1700, 956, 26, txt, Paint.Align.RIGHT, Fonts.bold);
    }

    private void el(Canvas c, Ui u, Ui.El e) {
        switch (e.kind) {
            case Ui.K_SETTINGS: {
                u.pop(c, e, 0.08f);
                u.circle(c, e.cx(), e.cy(), 28, Ui.lerp(0xFF152760, tileF, e.f));
                u.stroke(c, e.r, 28, line, 1.3f);
                u.ring(c, e, 28, 0xFFFFFFFF);
                u.icon(c, Icons.GEAR, e.cx(), e.cy(), 30, txt, 1.9f);
                c.restore();
                break;
            }
            case Ui.K_NET: chip(c, u, e, u.netOk ? "متصل" : "بدون إنترنت", Icons.WIFI, u.netOk ? 0xFF7FE0A8 : bad); break;
            case Ui.K_RADIO: chip(c, u, e, "إذاعة القرآن", Icons.RADIO, accent); break;
            case Ui.K_WARN:
                if (u.clockWarn != 0) chip(c, u, e, "اضبط الساعة", Icons.ALERT, bad);
                break;
            case Ui.K_PRAYER: prayerTile(c, u, e); break;
            case Ui.K_QURAN: case Ui.K_FAV: favTile(c, u, e); break;
            case Ui.K_MORE: moreTile(c, u, e); break;
            case Ui.K_ALL: {
                u.pop(c, e, 0.06f);
                u.fill(c, e.r, 24, Ui.lerp(0x00000000, 0x33FFFFFF, e.f));
                u.dashed(c, e.r, 24, Ui.lerp(0x99B8C6F0, 0xFFFFFFFF, e.f), 2.5f);
                u.text(c, "كل التطبيقات", e.cx(), e.cy() - 4, 22, txt, Paint.Align.CENTER, Fonts.bold);
                u.text(c, Fmt.digits(AppRepo.apps.size()), e.cx(), e.cy() + 34, 30, accent, Paint.Align.CENTER, Fonts.extra);
                c.restore();
                break;
            }
            case Ui.K_PLAYER: playerBar(c, u, e); break;
            case Ui.K_SCREENOFF: {
                u.pop(c, e, 0.06f);
                u.circle(c, e.cx(), e.top() + 28, 26, Ui.lerp(0x22FFFFFF, 0x55FFFFFF, e.f));
                u.icon(c, Icons.SCREENOFF, e.cx(), e.top() + 28, 28, txt, 1.9f);
                u.text(c, "إطفاء", e.cx(), e.top() + 74, 17, dim, Paint.Align.CENTER, Fonts.regular);
                u.text(c, "الشاشة", e.cx(), e.top() + 94, 17, dim, Paint.Align.CENTER, Fonts.regular);
                if (e.f > 0.02f) {
                    u.pStroke.setStrokeWidth(3);
                    u.pStroke.setColor(u.col(Ui.alpha(0xFFFFFFFF, e.f)));
                    c.drawCircle(e.cx(), e.top() + 28, 28, u.pStroke);
                }
                c.restore();
                break;
            }
            case Ui.K_REPEAT: case Ui.K_PREV: case Ui.K_NEXT: case Ui.K_PLAY: ctl(c, u, e); break;
            case Ui.K_ADHKAR: {
                u.pop(c, e, 0.04f);
                u.fillShader(c, e.r, 34, goldH(e));
                u.ring(c, e, 34, 0xFFFFFFFF);
                u.icon(c, Icons.BOOK, e.r.right - 50, e.cy(), 34, onAccent, 2f);
                u.text(c, "الأذكار", e.r.right - 90, e.cy() + 12, 34, onAccent, Paint.Align.RIGHT, Fonts.extra);
                c.restore();
                break;
            }
            case Ui.K_TASBEEH: {
                u.pop(c, e, 0.04f);
                u.fill(c, e.r, 34, Ui.lerp(0x2AFFFFFF, 0x55FFFFFF, e.f));
                u.stroke(c, e.r, 34, 0x33FFFFFF, 1.3f);
                u.ring(c, e, 34, 0xFFFFFFFF);
                u.icon(c, Icons.TASBEEH, e.r.right - 50, e.cy(), 32, txt, 1.9f);
                u.text(c, "التسبيح", e.r.right - 90, e.cy() + 12, 34, txt, Paint.Align.RIGHT, Fonts.extra);
                c.restore();
                break;
            }
            case Ui.K_LOGO: logo(c, u, e); break;
            default: break;
        }
    }

    private Shader goldH(Ui.El e) {
        if (goldBtn == null) goldBtn = new LinearGradient(1548, 0, 1798, 0, 0xFFFFE08A, 0xFFE0A93A, Shader.TileMode.CLAMP);
        return goldBtn;
    }

    private void prayerTile(Canvas c, Ui u, Ui.El e) {
        int i = e.idx;
        boolean isNow = i == u.nowIdx, isNext = i == u.nextIdx && !isNow;
        u.pop(c, e, 0.06f);
        u.fill(c, e.r, 26, isNow ? Ui.lerp(0xFFF3F6FF, 0xFFFFFFFF, e.f) : Ui.lerp(0xFF152760, tileF, e.f));
        if (isNext) u.stroke(c, e.r, 26, accent, 3f);
        else u.stroke(c, e.r, 26, isNow ? 0x00000000 : line, 1.3f);
        u.ring(c, e, 26, 0xFFFFFFFF);
        float cx = e.cx(), top = e.r.top;
        int main = isNow ? 0xFF0B1533 : txt;
        String lab = u.tLabel[i];
        if (lab != null) u.text(c, lab, cx, top + 36, 20, isNow ? 0xFF5C6B9A : (isNext ? accent : dim), Paint.Align.CENTER, Fonts.medium);
        u.text(c, Prayer.NAMES[i], cx, top + 86, 38, main, Paint.Align.CENTER, Fonts.bold);
        u.text(c, u.tFull[i], cx, top + 128, 34, main, Paint.Align.CENTER, Fonts.extra);
        c.restore();
    }

    private void favTile(Canvas c, Ui u, Ui.El e) {
        boolean q = e.kind == Ui.K_QURAN;
        u.pop(c, e, 0.05f);
        u.fill(c, e.r, 26, q ? Ui.lerp(0xFF2D2A3A, 0xFF4A4338, e.f) : Ui.lerp(0xFF152760, tileF, e.f));
        u.stroke(c, e.r, 26, q ? 0x99F2C14E : line, q ? 2f : 1.3f);
        u.ring(c, e, 26, 0xFFFFFFFF);
        appIconIn(c, u, e, e.r.right - 22 - 56, e.r.top + 20, 56);
        u.text(c, e.label, e.r.right - 22, e.r.bottom - 22, 30, txt, Paint.Align.RIGHT, Fonts.bold);
        c.restore();
    }

    private void moreTile(Canvas c, Ui u, Ui.El e) {
        u.pop(c, e, 0.06f);
        u.fill(c, e.r, 24, Ui.lerp(0xFF152760, tileF, e.f));
        u.stroke(c, e.r, 24, line, 1.3f);
        u.ring(c, e, 24, 0xFFFFFFFF);
        appIconIn(c, u, e, e.r.right - 18 - 44, e.r.top + 12, 44);
        u.text(c, e.label, e.r.right - 16, e.r.bottom - 16, 22, txt, Paint.Align.RIGHT, Fonts.bold);
        c.restore();
    }

    private void ctl(Canvas c, Ui u, Ui.El e) {
        Screens.controls(c, u, e);
    }

    private void playerBar(Canvas c, Ui u, Ui.El e) {
        // الخلفية
        rr.set(64, 872, 1170, 984);
        u.fill(c, rr, 34, Ui.lerp(0xFF2A2B45, 0xFF3A3A55, e.f));
        u.stroke(c, rr, 34, 0x66F2C14E, 1.6f);
        if (e.f > 0.02f) u.stroke(c, rr, 34, Ui.alpha(0xFFFFFFFF, e.f), 3.5f);
        quranIcon(c, u, 1072, 892, 72);
        u.text(c, u.fit(u.plTitle, 30, 360, Fonts.bold), 1050, 918, 30, txt, Paint.Align.RIGHT, Fonts.bold);
        u.text(c, u.fit(u.plSub, 22, 340, Fonts.regular), 480, 918, 22, dim, Paint.Align.LEFT, Fonts.regular);
        rr.set(480, 940, 1050, 946);
        u.fill(c, rr, 3, 0x33FFFFFF);
        if (u.plProg > 0.003f) {
            rr.set(1050 - 570 * u.plProg, 940, 1050, 946);
            u.fill(c, rr, 3, accent);
            u.circle(c, 1050 - 570 * u.plProg, 943, 9, 0xFFFFFFFF);
        }
        u.text(c, u.plPosS, 1050, 972, 20, dim, Paint.Align.RIGHT, Fonts.regular);
        u.text(c, u.plDurS, 480, 972, 20, dim, Paint.Align.LEFT, Fonts.regular);
        u.pStroke.setStrokeWidth(1.5f);
        u.pStroke.setColor(u.col(0x33FFFFFF));
        c.drawLine(205, 896, 205, 960, u.pStroke);
    }
}
