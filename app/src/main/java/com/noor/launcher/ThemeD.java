package com.noor.launcher;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;

import java.util.List;

/** 4) السكينة: هادي جداً — ساعة نحيفة، جملة واحدة للصلاة القادمة، وخط زمني بسيط. */
final class ThemeD extends Theme {
    private final RectF rr = new RectF();
    private final int sage = 0xFF9CC5AE, sand = 0xFFE7D6A8;

    ThemeD() {
        super("السكينة (هادئ)");
        bg1 = 0xFF15292E;
        bg2 = 0xFF0A1417;
        tile = 0xFF173236;
        tileF = 0xFF2A5654;
        panel = 0xFF112528;
        txt = 0xFFEAF2EE;
        dim = 0xFF8FB0A4;
        accent = 0xFFE7D6A8;
        onAccent = 0xFF2A2412;
        good = 0xFF9CC5AE;
        bad = 0xFFFF9E8A;
        line = 0x26E7D6A8;
    }

    @Override
    void base(Canvas c, Ui u) {
        Paint p = new Paint();
        p.setShader(new LinearGradient(0, 0, 0, Ui.H, 0xFF163035, 0xFF081215, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, Ui.W, Ui.H, p);
        Paint g = new Paint();
        g.setShader(new RadialGradient(960, 330, 760, new int[]{0x449CC5AE, 0x1A9CC5AE, 0x009CC5AE}, new float[]{0f, 0.5f, 1f}, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, Ui.W, Ui.H, g);
        pattern(c, 0xFFFFFFFF, 0.025f);
    }

    @Override
    void layout(Ui u) {
        u.addHome(Ui.K_SETTINGS, 0, 64, 44, 112, 92);
        u.addHome(Ui.K_NET, 0, 124, 44, 172, 92);
        u.addHome(Ui.K_RADIO, 0, 184, 44, 232, 92);
        u.addHome(Ui.K_WARN, 0, 244, 44, 424, 92);
        u.addHome(Ui.K_PRAYER, 0, 330, 520, 1590, 740);
        u.addHome(Ui.K_PLAYER, 0, 1180, 860, 1590, 914);
        u.addHome(Ui.K_SCREENOFF, 0, 372, 860, 424, 912);
        u.addHome(Ui.K_REPEAT, 0, 442, 862, 490, 910);
        u.addHome(Ui.K_PREV, 0, 512, 862, 560, 910);
        u.addHome(Ui.K_PLAY, 0, 580, 856, 640, 916);
        u.addHome(Ui.K_NEXT, 0, 660, 862, 708, 910);
        u.addHome(Ui.K_TIMER, 0, 728, 862, 776, 910);
        int i = 0;
        Ui.El q = u.addHome(Ui.K_QURAN, 0, 0, 0, 0, 0);
        dock(q, 0);
        q.label = "مشغل القرآن";
        i = 1;
        for (int k = 0; k < u.favs.size(); k++, i++) {
            Ui.El e = u.addHome(Ui.K_FAV, k, 0, 0, 0, 0);
            dock(e, i);
            e.app = u.favs.get(k);
            e.label = u.fit(e.app.label, 22, 200, Fonts.medium);
        }
        for (int k = 0; k < u.more.size(); k++, i++) {
            Ui.El e = u.addHome(Ui.K_MORE, k, 0, 0, 0, 0);
            dock(e, i);
            e.app = u.more.get(k);
            e.label = u.fit(e.app.label, 22, 200, Fonts.medium);
        }
        Ui.El a = u.addHome(Ui.K_ALL, u.more.size(), 0, 0, 0, 0);
        dock(a, i);
        u.addHome(Ui.K_ADHKAR, 0, 1716, 934, 1856, 982);
        u.addHome(Ui.K_TASBEEH, 0, 1566, 934, 1700, 982);
        u.addHome(Ui.K_LOGO, 0, 56, 1000, 560, 1070);
    }

    private void dock(Ui.El e, int i) {
        float cx = 960 + (4.5f - i) * 96f;
        e.r.set(cx - 42, 934, cx + 42, 1000);
    }

    private void round(Canvas c, Ui u, Ui.El e, int ico, int icoCol) {
        u.pop(c, e, 0.1f);
        u.circle(c, e.cx(), e.cy(), 25, Ui.lerp(0x1AFFFFFF, 0x40FFFFFF, e.f));
        u.ring(c, e, 25, sand);
        u.icon(c, ico, e.cx(), e.cy(), 26, icoCol, 1.8f);
        c.restore();
    }

    @Override
    void home(Canvas c, Ui u) {
        final Paint.Align R = Paint.Align.RIGHT, C = Paint.Align.CENTER;
        if (u.entBegin(c, 0.02f)) {
            salawat(c, u, 960, 70, 36, sand);
            u.textSp(c, "Abdou-Luncher", 1856, 70, 20, Ui.alpha(txt, 0.45f), R, Fonts.medium, 0.18f);
            u.entEnd(c);
        }
        if (u.entBegin(c, 0.1f)) {
            float w = u.measure(u.clockDigits, 230, Fonts.light);
            u.text(c, u.clockDigits, 960 + 40, 330, 230, txt, C, Fonts.light);
            u.text(c, u.clockSfx, 960 + 40 - w / 2f - 22, 330, 50, sage, R, Fonts.medium);
            u.text(c, u.dateStr, 960, 404, 40, 0xFFCFE3DA, C, Fonts.regular);
            u.text(c, u.hijriStr, 960, 450, 28, dim, C, Fonts.regular);
            u.entEnd(c);
        }
        if (u.entBegin(c, 0.2f)) {
            hero(c, u);
            u.entEnd(c);
        }
        if (u.entBegin(c, 0.3f)) {
            float size = 36;
            List<String> lines = null;
            for (; size >= 26; size -= 2) {
                lines = u.wrap(u.ayaText, size, Fonts.quran, 1100);
                if (lines.size() <= 2) break;
            }
            float lh = size * 1.5f;
            float y = u.paragraph(c, lines, 960, 794, size, lh, Ui.alpha(sand, 0.92f), C, Fonts.quran);
            u.text(c, u.ayaSrc, 960, y - lh + 34, 20, dim, C, Fonts.regular);
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
        if (e.f > 0.02f) {
            u.fill(c, e.r, 36, Ui.alpha(0xFFFFFFFF, 0.06f * e.f));
            u.stroke(c, e.r, 36, Ui.alpha(sand, e.f), 2.5f);
        }
        String name = u.panelName;
        String s = u.adhanNow ? u.panelLabel : ((name.equals("الإفطار") ? name : "صلاة " + name) + " بعد " + u.cdHuman);
        u.text(c, u.fit(s, 46, 1180, Fonts.medium), 960, 580, 46, sand, Paint.Align.CENTER, Fonts.medium);
        u.text(c, u.adhanStr, 960, 622, 24, dim, Paint.Align.CENTER, Fonts.regular);
        // الخط الزمني
        float y = 690;
        float[] x = new float[6];
        for (int i = 0; i < 6; i++) x[i] = 1530 - i * 212f;
        u.pStroke.setStrokeWidth(2f);
        u.pStroke.setPathEffect(null);
        u.pStroke.setColor(u.col(0x33FFFFFF));
        c.drawLine(x[5], y, x[0], y, u.pStroke);
        // التقدم
        float px = x[0];
        if (u.nowIdx >= 0 && u.nowIdx < 5) {
            long a = u.today[u.nowIdx], b = u.today[u.nowIdx + 1];
            float f = Math.max(0f, Math.min(1f, (Clock.now() - a) / (float) Math.max(1L, b - a)));
            px = x[u.nowIdx] + (x[u.nowIdx + 1] - x[u.nowIdx]) * f;
        } else if (u.nowIdx == 5) {
            px = x[5];
        }
        if (u.nowIdx >= 0) {
            u.pStroke.setStrokeWidth(3f);
            u.pStroke.setColor(u.col(sage));
            c.drawLine(px, y, x[0], y, u.pStroke);
        }
        for (int i = 0; i < 6; i++) {
            boolean past = i <= u.nowIdx, nxt = i == u.nextIdx && !u.nextTomorrow;
            if (nxt) {
                float pl = pulse();
                u.circle(c, x[i], y, 15 + 5 * pl, Ui.alpha(sand, 0.12f + 0.12f * pl));
                u.circle(c, x[i], y, 9, sand);
            } else if (past) {
                u.circle(c, x[i], y, 7, sage);
            } else {
                u.circle(c, x[i], y, 7, 0xFF16292D);
                u.pStroke.setStrokeWidth(2f);
                u.pStroke.setColor(u.col(Ui.alpha(sage, 0.7f)));
                c.drawCircle(x[i], y, 7, u.pStroke);
            }
            u.text(c, Prayer.NAMES[i], x[i], y - 30, 26, nxt ? sand : (past ? txt : dim), Paint.Align.CENTER, nxt ? Fonts.bold : Fonts.medium);
            u.text(c, u.tFull[i], x[i], y + 46, 22, nxt ? sand : dim, Paint.Align.CENTER, Fonts.regular);
        }
        c.restore();
    }

    private void el(Canvas c, Ui u, Ui.El e) {
        switch (e.kind) {
            case Ui.K_SETTINGS: round(c, u, e, Icons.GEAR, txt); break;
            case Ui.K_NET: round(c, u, e, Icons.WIFI, u.netOk ? sage : bad); break;
            case Ui.K_RADIO: round(c, u, e, Icons.RADIO, sand); break;
            case Ui.K_WARN:
                if (u.clockWarn != 0) {
                    u.pop(c, e, 0.05f);
                    u.fill(c, e.r, 24, Ui.lerp(0x1AFFFFFF, 0x40FFFFFF, e.f));
                    u.ring(c, e, 24, sand);
                    u.icon(c, Icons.ALERT, e.r.right - 30, e.cy(), 24, bad, 1.9f);
                    u.text(c, "اضبط الساعة", e.r.right - 56, e.cy() + 8, 22, txt, Paint.Align.RIGHT, Fonts.medium);
                    c.restore();
                }
                break;
            case Ui.K_PLAYER: {
                u.pop(c, e, 0.012f);
                if (e.f > 0.02f) u.stroke(c, e.r, 26, Ui.alpha(sand, e.f), 2.5f);
                u.icon(c, Icons.MUSIC, e.r.right - 30, e.cy(), 26, sand, 1.9f);
                u.text(c, u.fit(u.plTitle, 26, 280, Fonts.medium), e.r.right - 60, e.cy() - 2, 26, txt, Paint.Align.RIGHT, Fonts.medium);
                u.text(c, u.fit(u.plSub, 17, 280, Fonts.regular), e.r.right - 60, e.cy() + 22, 17, dim, Paint.Align.RIGHT, Fonts.regular);
                rr.set(800, 884, 1160, 888);
                u.fill(c, rr, 2, 0x33FFFFFF);
                if (u.plProg > 0.003f) {
                    rr.set(1160 - 360 * u.plProg, 884, 1160, 888);
                    u.fill(c, rr, 2, sage);
                }
                c.restore();
                break;
            }
            case Ui.K_SCREENOFF: round(c, u, e, Icons.SCREENOFF, txt); break;
            case Ui.K_TIMER: round(c, u, e, Icons.TIMER, Player.sleepAt > Clock.mono() ? sand : txt); break;
            case Ui.K_REPEAT: case Ui.K_PREV: case Ui.K_NEXT: case Ui.K_PLAY: Screens.controls(c, u, e); break;
            case Ui.K_ADHKAR: pill(c, u, e, "الأذكار", Icons.BOOK); break;
            case Ui.K_TASBEEH: pill(c, u, e, "المسبحة", Icons.TASBEEH); break;
            case Ui.K_LOGO: logo(c, u, e); break;
            case Ui.K_QURAN: case Ui.K_FAV: case Ui.K_MORE: {
                u.pop(c, e, 0.12f);
                u.circle(c, e.cx(), e.cy(), 33, Ui.lerp(0x14FFFFFF, 0x33FFFFFF, e.f));
                u.ring(c, e, 33, sand);
                if (e.kind == Ui.K_QURAN) {
                    u.icon(c, Icons.MUSIC, e.cx(), e.cy(), 34, sand, 2f);
                } else {
                    u.tmp.set(e.cx() - 26, e.cy() - 26, e.cx() + 26, e.cy() + 26);
                    u.bmp(c, e.app.icon, u.tmp);
                }
                c.restore();
                if (e.f > 0.4f) u.text(c, e.label, e.cx(), e.r.bottom + 30 > 1050 ? e.cy() - 50 : e.r.bottom + 28, 22, Ui.alpha(txt, e.f), Paint.Align.CENTER, Fonts.medium);
                break;
            }
            case Ui.K_ALL: {
                u.pop(c, e, 0.12f);
                u.fill(c, e.r, 33, Ui.lerp(0x00FFFFFF, 0x26FFFFFF, e.f));
                u.icon(c, Icons.GRID, e.cx(), e.cy(), 30, sage, 1.9f);
                u.ring(c, e, 33, sand);
                c.restore();
                if (e.f > 0.4f) u.text(c, "كل التطبيقات", e.cx(), e.cy() - 50, 22, Ui.alpha(txt, e.f), Paint.Align.CENTER, Fonts.medium);
                break;
            }
            default: break;
        }
    }

    private void pill(Canvas c, Ui u, Ui.El e, String label, int ico) {
        u.pop(c, e, 0.06f);
        u.fill(c, e.r, 24, Ui.lerp(0x14FFFFFF, 0x40FFFFFF, e.f));
        u.stroke(c, e.r, 24, 0x26E7D6A8, 1.2f);
        u.ring(c, e, 24, sand);
        u.icon(c, ico, e.r.right - 28, e.cy(), 24, sage, 1.9f);
        u.text(c, label, e.r.right - 52, e.cy() + 8, 22, txt, Paint.Align.RIGHT, Fonts.medium);
        c.restore();
    }
}
