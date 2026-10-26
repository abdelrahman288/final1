package com.noor.launcher;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;

/** 3) الشفق الزجاجي: خلفية بنفسجية/زمردية + بطاقات زجاجية + حلقة تقدّم. */
final class ThemeC extends Theme {
    private final RectF rr = new RectF(), ringR = new RectF(313 - 178, 377 - 178, 313 + 178, 377 + 178);
    private Shader clockSh, ringSh, accSh, btnSh;
    private final int teal = 0xFF2DD4BF, violet = 0xFF8B7CFF, orange = 0xFFFF9B6A;

    ThemeC() {
        super("الشفق الزجاجي");
        bg1 = 0xFF1B1030;
        bg2 = 0xFF080812;
        tile = 0xFF1E1D2E;
        tileF = 0xFF35345A;
        panel = 0xFF151524;
        txt = 0xFFFFFFFF;
        dim = 0xFFA7A9C7;
        accent = 0xFF2DD4BF;
        onAccent = 0xFF04201C;
        good = 0xFF7FE0A8;
        bad = 0xFFFF8A80;
        line = 0x1FFFFFFF;
    }

    @Override
    void base(Canvas c, Ui u) {
        Paint p = new Paint();
        p.setColor(0xFF090912);
        c.drawRect(0, 0, Ui.W, Ui.H, p);
        Paint g = new Paint();
        g.setShader(new RadialGradient(120, 60, 900, new int[]{0xFF5B2BB5, 0x883A1D7A, 0x00200F45}, new float[]{0f, 0.5f, 1f}, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, Ui.W, Ui.H, g);
        Paint g2 = new Paint();
        g2.setShader(new RadialGradient(1560, 120, 560, new int[]{0x66A5522B, 0x00402010}, null, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, Ui.W, Ui.H, g2);
        Paint g3 = new Paint();
        g3.setShader(new RadialGradient(1900, 1040, 860, new int[]{0xCC0E8A68, 0x66075A44, 0x00042A20}, new float[]{0f, 0.5f, 1f}, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, Ui.W, Ui.H, g3);
        // خط مساجد خفيف في الأسفل
        Paint s = new Paint(Paint.ANTI_ALIAS_FLAG);
        s.setStyle(Paint.Style.STROKE);
        s.setStrokeWidth(2);
        s.setColor(0x14FFFFFF);
        Path d = new Path();
        d.moveTo(0, 1040);
        d.lineTo(280, 1040);
        d.cubicTo(280, 960, 400, 960, 400, 1040);
        d.lineTo(700, 1040);
        d.lineTo(700, 990);
        d.lineTo(1000, 990);
        d.lineTo(1000, 1040);
        d.lineTo(1300, 1040);
        d.cubicTo(1300, 950, 1460, 950, 1460, 1040);
        d.lineTo(1920, 1040);
        c.drawPath(d, s);
        pattern(c, 0xFFFFFFFF, 0.02f);
    }

    @Override
    void layout(Ui u) {
        u.addHome(Ui.K_SETTINGS, 0, 64, 44, 116, 96);
        u.addHome(Ui.K_NET, 0, 130, 44, 306, 96);
        u.addHome(Ui.K_RADIO, 0, 320, 44, 540, 96);
        u.addHome(Ui.K_WARN, 0, 554, 44, 734, 96);
        u.addHome(Ui.K_PRAYER, 0, 64, 122, 1254, 630);
        u.addHome(Ui.K_ADHKAR, 0, 1706, 452, 1856, 502);
        u.addHome(Ui.K_TASBEEH, 0, 1546, 452, 1690, 502);
        u.addHome(Ui.K_PLAYER, 0, 1380, 668, 1850, 756);
        u.addHome(Ui.K_SCREENOFF, 0, 88, 682, 320, 742);
        u.addHome(Ui.K_REPEAT, 0, 344, 688, 392, 736);
        u.addHome(Ui.K_PREV, 0, 397, 688, 445, 736);
        u.addHome(Ui.K_PLAY, 0, 452, 678, 520, 746);
        u.addHome(Ui.K_NEXT, 0, 528, 688, 576, 736);
        u.addHome(Ui.K_TIMER, 0, 581, 688, 629, 736);
        int i = 0;
        Ui.El q = u.addHome(Ui.K_QURAN, 0, 1680, 806, 1840, 966);
        q.label = "مشغل القرآن";
        i = 1;
        for (int k = 0; k < u.favs.size(); k++, i++) {
            float cx = 1760 - i * 172;
            Ui.El e = u.addHome(Ui.K_FAV, k, cx - 80, 806, cx + 80, 966);
            e.app = u.favs.get(k);
            e.label = u.fit(e.app.label, 22, 150, Fonts.medium);
        }
        for (int k = 0; k < u.more.size(); k++, i++) {
            float cx = 1760 - i * 172;
            Ui.El e = u.addHome(Ui.K_MORE, k, cx - 80, 806, cx + 80, 966);
            e.app = u.more.get(k);
            e.label = u.fit(e.app.label, 22, 150, Fonts.medium);
        }
        float cx = 1760 - i * 172;
        u.addHome(Ui.K_ALL, u.more.size(), cx - 80, 806, cx + 80, 966);
        u.addHome(Ui.K_LOGO, 0, 56, 994, 560, 1068);
    }

    private void chip(Canvas c, Ui u, Ui.El e, String label, int ico, int icoColor) {
        u.pop(c, e, 0.05f);
        float rad = e.r.height() / 2f;
        u.fill(c, e.r, rad, Ui.lerp(0x24FFFFFF, 0x4DFFFFFF, e.f));
        u.stroke(c, e.r, rad, 0x26FFFFFF, 1.3f);
        u.ring(c, e, rad, 0xFFFFFFFF);
        u.icon(c, ico, e.r.right - 34, e.cy(), 26, icoColor, 1.9f);
        u.text(c, label, e.r.right - 58, e.cy() + 9, 24, txt, Paint.Align.RIGHT, Fonts.medium);
        c.restore();
    }

    @Override
    void home(Canvas c, Ui u) {
        final Paint.Align R = Paint.Align.RIGHT, C = Paint.Align.CENTER;
        if (u.entBegin(c, 0.02f)) {
            salawat(c, u, 960, 64, 40, 0xFFFFFFFF);
            u.icon(c, Icons.MOON, 1826, 60, 36, orange, 2.2f);
            u.textSp(c, "ABDOU·LUNCHER", 1796, 70, 28, 0xFFFFFFFF, R, Fonts.bold, 0.28f);
            u.entEnd(c);
        }
        if (u.entBegin(c, 0.08f)) {
            if (clockSh == null) clockSh = new LinearGradient(0, 140, 0, 320, 0xFFFFFFFF, 0xFFA9A6FF, Shader.TileMode.CLAMP);
            float w = u.measure(u.clockDigits, 236, Fonts.extra);
            u.textShader(c, u.clockDigits, 1850, 308, 236, clockSh, R, Fonts.extra);
            u.text(c, u.clockSfx, 1850 - w - 20, 308, 54, orange, R, Fonts.bold);
            u.text(c, u.dateStr, 1850, 388, 46, txt, R, Fonts.bold);
            u.text(c, u.hijriStr, 1850, 432, 28, dim, R, Fonts.regular);
            // شريحة المناسبة
            rr.set(1290, 452, 1524, 502);
            u.fill(c, rr, 25, 0x1AFFFFFF);
            u.stroke(c, rr, 25, 0x26FFFFFF, 1.2f);
            u.icon(c, Icons.CAL, 1500, 477, 24, teal, 1.9f);
            u.text(c, u.fit(u.occText, 20, 180, Fonts.medium), 1474, 485, 20, txt, R, Fonts.medium);
            u.entEnd(c);
        }
        if (u.entBegin(c, 0.14f)) {
            hero(c, u);
            u.entEnd(c);
        }
        if (u.entBegin(c, 0.3f)) {
            rr.set(64, 796, 1856, 974);
            u.fill(c, rr, 48, 0x14FFFFFF);
            u.stroke(c, rr, 48, 0x24FFFFFF, 1.3f);
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

    private void hero(Canvas c, Ui u) {
        Ui.El e = u.find(u.homeEls, Ui.K_PRAYER, 0);
        u.pop(c, e, 0.01f);
        u.fill(c, e.r, 40, Ui.lerp(0x14FFFFFF, 0x26FFFFFF, e.f));
        u.stroke(c, e.r, 40, Ui.lerp(0x24FFFFFF, 0xFFFFFFFF, e.f), 1.3f + 2f * e.f);
        // الحلقة
        if (ringSh == null) ringSh = new LinearGradient(135, 540, 491, 215, teal, violet, Shader.TileMode.CLAMP);
        u.pStroke.setPathEffect(null);
        u.pStroke.setStrokeCap(Paint.Cap.ROUND);
        u.pStroke.setStrokeWidth(13);
        u.pStroke.setShader(null);
        u.pStroke.setColor(u.col(0x22FFFFFF));
        c.drawCircle(313, 377, 178, u.pStroke);
        u.pStroke.setShader(ringSh);
        u.pStroke.setColor(0xFFFFFFFF);
        if (u.ga < 0.999f) u.pStroke.setAlpha((int) (255 * u.ga));
        float sw = Math.max(2f, 360f * u.progress);
        c.drawArc(ringR, -90, sw, false, u.pStroke);
        u.pStroke.setShader(null);
        u.pStroke.setStrokeCap(Paint.Cap.BUTT);
        u.circle(c, 313, 377, 148, 0x14FFFFFF);
        u.text(c, u.fit(u.panelLabel, 22, 250, Fonts.regular), 313, 300, 22, u.adhanNow ? teal : dim, Paint.Align.CENTER, Fonts.regular);
        u.text(c, u.panelName, 313, 378, 78, txt, Paint.Align.CENTER, Fonts.serifBold);
        if (accSh == null) accSh = new LinearGradient(210, 0, 416, 0, teal, violet, Shader.TileMode.CLAMP);
        u.textShader(c, u.cdStr, 313, 446, 56, accSh, Paint.Align.CENTER, Fonts.extra);
        u.text(c, u.fit(u.adhanStr, 21, 250, Fonts.regular), 313, 488, 21, dim, Paint.Align.CENTER, Fonts.regular);
        // الصفوف
        for (int i = 0; i < 6; i++) {
            float cy = 187 + i * 74f;
            rr.set(545, cy - 34, 1205, cy + 34);
            boolean isNow = i == u.nowIdx, isNext = i == u.nextIdx && !isNow;
            if (isNow) {
                u.fill(c, rr, 22, 0x4029C7B1);
                u.stroke(c, rr, 22, 0x6629C7B1, 1.4f);
            } else if (isNext) {
                u.stroke(c, rr, 22, 0xAAFF9B6A, 1.6f);
            }
            u.text(c, Prayer.NAMES[i], 1178, cy + 13, 38, isNow || isNext ? txt : 0xFFE6E6F5, Paint.Align.RIGHT, Fonts.bold);
            u.text(c, u.tFull[i], 572, cy + 12, 35, isNow || isNext ? txt : 0xFFE6E6F5, Paint.Align.LEFT, Fonts.extra);
            String lab = isNow ? "الآن" : (isNext ? "التالية" : null);
            if (lab != null) {
                float nw = u.measure(Prayer.NAMES[i], 38, Fonts.bold);
                float bl = 1178 - nw - 20;
                rr.set(bl - 70, cy - 15, bl, cy + 15);
                u.fill(c, rr, 15, isNow ? 0x33FFFFFF : 0x33FF9B6A);
                u.text(c, lab, bl - 35, cy + 7, 17, isNow ? txt : orange, Paint.Align.CENTER, Fonts.medium);
            }
        }
        c.restore();
    }

    private void el(Canvas c, Ui u, Ui.El e) {
        switch (e.kind) {
            case Ui.K_SETTINGS: {
                u.pop(c, e, 0.08f);
                u.circle(c, e.cx(), e.cy(), 26, Ui.lerp(0x24FFFFFF, 0x55FFFFFF, e.f));
                u.ring(c, e, 26, 0xFFFFFFFF);
                u.icon(c, Icons.GEAR, e.cx(), e.cy(), 28, txt, 1.9f);
                c.restore();
                break;
            }
            case Ui.K_NET: chip(c, u, e, u.netOk ? "متصل" : "بدون إنترنت", Icons.WIFI, u.netOk ? teal : bad); break;
            case Ui.K_RADIO: chip(c, u, e, "إذاعة القرآن", Icons.RADIO, orange); break;
            case Ui.K_WARN: if (u.clockWarn != 0) chip(c, u, e, "اضبط الساعة", Icons.ALERT, bad); break;
            case Ui.K_ADHKAR: smallChip(c, u, e, "الأذكار", Icons.BOOK); break;
            case Ui.K_TASBEEH: smallChip(c, u, e, "المسبحة", Icons.TASBEEH); break;
            case Ui.K_PLAYER: playerPill(c, u, e); break;
            case Ui.K_SCREENOFF: {
                u.pop(c, e, 0.05f);
                u.fill(c, e.r, 30, Ui.lerp(0x2EFFFFFF, 0x66FFFFFF, e.f));
                u.stroke(c, e.r, 30, 0x33FFFFFF, 1.3f);
                u.ring(c, e, 30, 0xFFFFFFFF);
                u.icon(c, Icons.SCREENOFF, e.r.left + 40, e.cy(), 28, txt, 1.9f);
                u.text(c, "إطفاء الشاشة", e.r.right - 24, e.cy() + 9, 25, txt, Paint.Align.RIGHT, Fonts.bold);
                c.restore();
                break;
            }
            case Ui.K_TIMER: {
                u.pop(c, e, 0.08f);
                u.icon(c, Icons.TIMER, e.cx(), e.cy(), 26, Player.sleepAt > Clock.mono() ? teal : txt, 1.9f);
                if (e.f > 0.02f) {
                    u.pStroke.setStrokeWidth(3);
                    u.pStroke.setColor(u.col(Ui.alpha(0xFFFFFFFF, e.f)));
                    c.drawCircle(e.cx(), e.cy(), 24, u.pStroke);
                }
                c.restore();
                break;
            }
            case Ui.K_REPEAT: case Ui.K_PREV: case Ui.K_NEXT: case Ui.K_PLAY: Screens.controls(c, u, e); break;
            case Ui.K_LOGO: logo(c, u, e); break;
            case Ui.K_QURAN: case Ui.K_FAV: case Ui.K_MORE: dockItem(c, u, e); break;
            case Ui.K_ALL: {
                u.pop(c, e, 0.06f);
                u.fill(c, e.r, 30, Ui.lerp(0x00000000, 0x26FFFFFF, e.f));
                u.pStroke.setPathEffect(new android.graphics.DashPathEffect(new float[]{8, 8}, 0));
                u.pStroke.setStrokeWidth(2.5f);
                u.pStroke.setColor(u.col(0xAAA7A9C7));
                c.drawCircle(e.cx(), 866, 48, u.pStroke);
                u.pStroke.setPathEffect(null);
                u.icon(c, Icons.GRID, e.cx(), 866, 40, txt, 1.9f);
                u.text(c, "كل التطبيقات", e.cx(), 944, 22, e.f > 0.5f ? txt : dim, Paint.Align.CENTER, Fonts.medium);
                c.restore();
                break;
            }
            default: break;
        }
    }

    private void smallChip(Canvas c, Ui u, Ui.El e, String label, int ico) {
        u.pop(c, e, 0.06f);
        u.fill(c, e.r, 25, Ui.lerp(0x24FFFFFF, 0x55FFFFFF, e.f));
        u.stroke(c, e.r, 25, 0x33FFFFFF, 1.3f);
        u.ring(c, e, 25, 0xFFFFFFFF);
        u.icon(c, ico, e.r.right - 30, e.cy(), 24, teal, 1.9f);
        u.text(c, label, e.r.right - 54, e.cy() + 9, 24, txt, Paint.Align.RIGHT, Fonts.bold);
        c.restore();
    }

    private void dockItem(Canvas c, Ui u, Ui.El e) {
        u.pop(c, e, 0.07f);
        u.fill(c, e.r, 30, Ui.lerp(0x00FFFFFF, 0x26FFFFFF, e.f));
        u.ring(c, e, 30, 0xFFFFFFFF);
        float cx = e.cx();
        if (e.kind == Ui.K_QURAN) {
            u.pFill.setShader(new LinearGradient(0, 818, 0, 914, 0xFFFBE29A, 0xFFC8932F, Shader.TileMode.CLAMP));
            u.pFill.setColor(0xFFFFFFFF);
            if (u.ga < 0.999f) u.pFill.setAlpha((int) (255 * u.ga));
            c.drawCircle(cx, 866, 48, u.pFill);
            u.pFill.setShader(null);
            u.icon(c, Icons.MUSIC, cx, 866, 48, 0xFF1B1305, 2.1f);
        } else {
            u.circle(c, cx, 866, 48, 0x14FFFFFF);
            u.tmp.set(cx - 38, 828, cx + 38, 904);
            u.bmp(c, e.app.icon, u.tmp);
        }
        u.text(c, e.label, cx, 944, 22, e.f > 0.5f || e.kind == Ui.K_QURAN ? (e.kind == Ui.K_QURAN ? 0xFFF2C14E : txt) : dim, Paint.Align.CENTER, Fonts.medium);
        c.restore();
    }

    private void playerPill(Canvas c, Ui u, Ui.El e) {
        rr.set(64, 660, 1856, 764);
        u.fill(c, rr, 52, 0x14FFFFFF);
        u.stroke(c, rr, 52, 0x24FFFFFF, 1.3f);
        if (e.f > 0.02f) u.stroke(c, rr, 52, Ui.alpha(0xFFFFFFFF, e.f), 3f);
        // أيقونة بحلقة متدرجة
        if (btnSh == null) btnSh = new LinearGradient(1764, 744, 1832, 680, orange, teal, Shader.TileMode.CLAMP);
        u.pStroke.setShader(btnSh);
        u.pStroke.setColor(0xFFFFFFFF);
        u.pStroke.setStrokeWidth(5);
        if (u.ga < 0.999f) u.pStroke.setAlpha((int) (255 * u.ga));
        c.drawCircle(1798, 712, 34, u.pStroke);
        u.pStroke.setShader(null);
        u.circle(c, 1798, 712, 28, 0xFF14122B);
        u.icon(c, Icons.MUSIC, 1798, 712, 28, txt, 2f);
        u.text(c, u.fit(u.plTitle, 28, 340, Fonts.bold), 1752, 706, 28, txt, Paint.Align.RIGHT, Fonts.bold);
        u.text(c, u.fit(u.plSub + "  •  " + Player.repeatName(Player.repeat()), 18, 340, Fonts.regular), 1752, 735, 18, dim, Paint.Align.RIGHT, Fonts.regular);
        rr.set(710, 708, 1360, 716);
        u.fill(c, rr, 4, 0x2EFFFFFF);
        if (u.plProg > 0.003f) {
            rr.set(1360 - 650 * u.plProg, 708, 1360, 716);
            u.fillShader(c, rr, 4, new LinearGradient(1360 - 650 * u.plProg, 0, 1360, 0, violet, teal, Shader.TileMode.CLAMP));
            u.circle(c, 1360 - 650 * u.plProg, 712, 10, 0xFFFFFFFF);
        }
        u.text(c, u.plPosS, 1396, 720, 19, dim, Paint.Align.RIGHT, Fonts.regular);
        u.text(c, u.plDurS, 690, 720, 19, dim, Paint.Align.RIGHT, Fonts.regular);
    }
}
