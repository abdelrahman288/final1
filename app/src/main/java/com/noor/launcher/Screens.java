package com.noor.launcher;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** رسم الشاشات الفرعية (التطبيقات، اليوم، القرآن، القوائم، الأذكار، المسبحة، ضبط الساعة، الإشعار القانوني). */
final class Screens {
    private Screens() {}

    static final Paint.Align R = Paint.Align.RIGHT, L = Paint.Align.LEFT, C = Paint.Align.CENTER;

    static void header(Canvas c, Ui u, String title, String sub) {
        Theme t = u.theme;
        u.text(c, title, 1804, 104, 56, t.txt, R, Fonts.bold);
        if (sub != null && !sub.isEmpty()) u.text(c, u.fit(sub, 24, 1200, Fonts.regular), 1804, 134, 22, t.dim, R, Fonts.regular);
    }

    /** زرار عام (كبسولة). */
    static void btn(Canvas c, Ui u, Ui.El e) {
        Theme t = u.theme;
        float rad = e.r.height() / 2f;
        u.pop(c, e, 0.04f);
        boolean acc = e.kind == Ui.K_BACK ? false : false;
        u.fill(c, e.r, rad, Ui.lerp(t.tile, t.tileF, e.f));
        u.stroke(c, e.r, rad, Ui.alpha(t.line, 1f), 1.5f);
        u.ring(c, e, rad, t.accent);
        float cy = e.cy();
        int col = t.txt;
        if (e.ico >= 0) {
            u.icon(c, e.ico, e.r.right - 38, cy, 30, t.accent, 1.9f);
            u.text(c, u.fit(e.label, 28, e.r.width() - 100, Fonts.bold), e.r.right - 70, cy + 10, 28, col, R, Fonts.bold);
        } else {
            u.text(c, u.fit(e.label, 28, e.r.width() - 40, Fonts.bold), e.cx(), cy + 10, 28, col, C, Fonts.bold);
        }
        c.restore();
    }

    static void backBtn(Canvas c, Ui u, Ui.El e) {
        Theme t = u.theme;
        u.pop(c, e, 0.05f);
        float rad = e.r.height() / 2f;
        u.fill(c, e.r, rad, Ui.lerp(t.tile, t.tileF, e.f));
        u.stroke(c, e.r, rad, t.line, 1.5f);
        u.ring(c, e, rad, t.accent);
        u.icon(c, Icons.BACK, e.r.right - 38, e.cy(), 28, t.accent, 2.2f);
        u.text(c, "رجوع", e.r.right - 66, e.cy() + 10, 28, t.txt, R, Fonts.bold);
        c.restore();
    }

    static void controls(Canvas c, Ui u, Ui.El e) {
        Theme t = u.theme;
        u.pop(c, e, 0.08f);
        float cx = e.cx(), cy = e.cy();
        float r = e.r.width() / 2f;
        switch (e.kind) {
            case Ui.K_PLAY:
                u.circle(c, cx, cy, r, t.accent);
                u.icon(c, u.plPlaying ? Icons.PAUSE : Icons.PLAY, cx, cy, r * 1.0f, t.onAccent, 1.9f);
                if (e.f > 0.02f) {
                    u.pStroke.setStrokeWidth(4);
                    u.pStroke.setColor(u.col(Ui.alpha(0xFFFFFFFF, e.f)));
                    c.drawCircle(cx, cy, r + 5, u.pStroke);
                }
                break;
            default: {
                u.circle(c, cx, cy, r, Ui.lerp(Ui.alpha(t.tile, 1f), t.tileF, e.f));
                if (e.f > 0.02f) {
                    u.pStroke.setPathEffect(null);
                    u.pStroke.setStrokeWidth(4);
                    u.pStroke.setColor(u.col(Ui.alpha(t.accent, e.f)));
                    c.drawCircle(cx, cy, r, u.pStroke);
                }
                int ic = e.kind == Ui.K_PREV ? Icons.PREV : e.kind == Ui.K_NEXT ? Icons.NEXT
                        : e.kind == Ui.K_SCREENOFF ? Icons.SCREENOFF
                        : (Player.repeat() == 2 ? Icons.REPEAT1 : Icons.REPEAT);
                int icol = e.kind == Ui.K_REPEAT && Player.repeat() == 0 ? t.dim : t.txt;
                u.icon(c, ic, cx, cy, r * 0.95f, icol, 1.9f);
            }
        }
        c.restore();
    }

    // ---------------- كل التطبيقات ----------------

    static void apps(Canvas c, Ui u) {
        Theme t = u.theme;
        header(c, u, "كل التطبيقات", "اضغط OK للفتح • اضغط مطولاً للإضافة للشاشة الرئيسية");
        u.text(c, Fmt.digits(u.appEls.size()) + " تطبيق", 330, 104, 30, t.accent, L, Fonts.medium);
        for (Ui.El e : u.appEls) if (e.kind == Ui.K_BACK) backBtn(c, u, e);
        c.save();
        c.clipRect(0, 160, Ui.W, Ui.H);
        c.translate(0, -u.scroll);
        for (int i = 0; i < u.appEls.size(); i++) {
            if (i == u.af) continue;
            gridEl(c, u, u.appEls.get(i));
        }
        if (u.af >= 0 && u.af < u.appEls.size()) gridEl(c, u, u.appEls.get(u.af));
        c.restore();
        if (u.appEls.isEmpty()) u.text(c, "جارٍ تحميل التطبيقات…", Ui.W / 2f, Ui.H / 2f, 36, t.dim, C, Fonts.regular);
    }

    private static void gridEl(Canvas c, Ui u, Ui.El e) {
        Theme t = u.theme;
        if (e.r.bottom - u.scroll < 150 || e.r.top - u.scroll > Ui.H) return;
        u.pop(c, e, 0.06f);
        u.fill(c, e.r, 24, Ui.lerp(t.tile, t.tileF, e.f));
        u.ring(c, e, 24, t.accent);
        float s = 84;
        float l = e.cx() - s / 2f;
        u.tmp.set(l, e.r.top + 20, l + s, e.r.top + 20 + s);
        u.bmp(c, e.app.icon, u.tmp);
        u.text(c, e.label, e.cx(), e.r.bottom - 26, 24, t.txt, C, Fonts.medium);
        c.restore();
    }

    // ---------------- اليوم ----------------

    static void today(Canvas c, Ui u) {
        Theme t = u.theme;
        header(c, u, "مواقيت اليوم والمناسبات", u.dateLine);
        for (Ui.El e : u.todayEls) {
            if (e.kind == Ui.K_BACK) backBtn(c, u, e);
            else btn(c, u, e);
        }
        for (int i = 0; i < 6; i++) {
            float top = 250 + i * 108f;
            u.tmp.set(944, top, 1804, top + 96);
            boolean isNow = i == u.nowIdx, isNext = i == u.nextIdx && !isNow;
            u.fill(c, u.tmp, 26, isNow ? t.accent : t.tile);
            if (isNext) u.stroke(c, u.tmp, 26, t.accent, 3f);
            else u.stroke(c, u.tmp, 26, t.line, 1.2f);
            int main = isNow ? t.onAccent : t.txt;
            u.text(c, Prayer.NAMES[i], 1764, top + 64, 44, main, R, Fonts.serifBold);
            u.text(c, u.tFull[i], 990, top + 64, 44, main, L, Fonts.bold);
            if (u.tLabel[i] != null) u.text(c, u.tLabel[i], 1350, top + 62, 26, isNow ? t.onAccent : t.accent, C, Fonts.medium);
        }
        // آية اليوم
        card(c, u, 116, 250, 900, 520, "آية اليوم", Icons.BOOK);
        List<String> ay = u.wrap(u.ayaText, 38, Fonts.quran, 700);
        float y = 360;
        for (String l : ay) {
            u.text(c, l, 508, y, 38, t.txt, C, Fonts.quran);
            y += 64;
        }
        u.text(c, u.ayaSrc, 508, 495, 24, t.dim, C, Fonts.regular);
        // حديث
        card(c, u, 116, 540, 900, 790, "حديث اليوم", Icons.SPARK);
        List<String> hd = u.wrap(u.hadText, 34, Fonts.serif, 700);
        y = 640;
        for (String l : hd) {
            u.text(c, l, 508, y, 34, t.txt, C, Fonts.serif);
            y += 56;
        }
        u.text(c, u.hadSrc, 508, 765, 24, t.dim, C, Fonts.regular);
        // مناسبة
        card(c, u, 116, 810, 900, 920, null, -1);
        u.icon(c, Icons.CAL, 860, 865, 40, t.accent, 1.9f);
        u.text(c, u.occTitle, 800, 852, 24, t.accent, R, Fonts.medium);
        u.text(c, u.fit(u.occText, 28, 640, Fonts.bold), 800, 892, 28, t.txt, R, Fonts.bold);
        if (u.clockWarn != 0) {
            u.text(c, "تنبيه: ساعة الشاشة محتاجة ضبط (من الإعدادات)", 1804, 960, 26, t.bad, R, Fonts.medium);
        }
    }

    static void card(Canvas c, Ui u, float l, float tp, float r, float b, String label, int ico) {
        Theme t = u.theme;
        u.tmp.set(l, tp, r, b);
        u.fill(c, u.tmp, 30, t.panel);
        u.stroke(c, u.tmp, 30, t.line, 1.5f);
        if (label != null) {
            u.text(c, label, r - 70, tp + 56, 26, t.accent, R, Fonts.medium);
            u.icon(c, ico, r - 38, tp + 46, 28, t.accent, 1.9f);
        }
    }

    // ---------------- القرآن ----------------

    static void quran(Canvas c, Ui u) {
        Theme t = u.theme;
        header(c, u, "مشغل القرآن", "");
        // لوحة التشغيل
        u.tmp.set(64, 150, 820, 640);
        u.fill(c, u.tmp, 36, t.panel);
        u.stroke(c, u.tmp, 36, t.line, 1.5f);
        u.circle(c, 716, 250, 64, t.accent);
        u.icon(c, u.plActive && Player.mode == Player.MODE_RADIO ? Icons.RADIO : Icons.MUSIC, 716, 250, 60, t.onAccent, 2f);
        u.text(c, u.fit(u.plTitle, 40, 520, Fonts.bold), 630, 246, 40, t.txt, R, Fonts.bold);
        u.text(c, u.fit(u.plSub, 24, 540, Fonts.regular), 630, 292, 24, t.dim, R, Fonts.regular);
        // شريط
        u.tmp.set(110, 366, 770, 374);
        u.fill(c, u.tmp, 4, Ui.alpha(t.txt, 0.15f));
        if (u.plProg > 0.005f) {
            u.tmp.set(110, 366, 110 + 660 * u.plProg, 374);
            u.fill(c, u.tmp, 4, t.accent);
        }
        u.text(c, u.plPosS, 110, 410, 22, t.dim, L, Fonts.regular);
        u.text(c, u.plDurS, 770, 410, 22, t.dim, R, Fonts.regular);
        String st = Player.repeatName(Player.repeat()) + (u.selected.isEmpty() ? "" : "  •  محدد " + Fmt.digits(u.selected.size()))
                + (Player.sleepAt > Clock.mono() ? "  •  مؤقت نوم" : "");
        u.text(c, st, 442, 610, 24, t.dim, C, Fonts.regular);
        if (Player.error != null && !Player.error.isEmpty()) u.text(c, Player.error, 442, 456, 24, t.bad, C, Fonts.medium);

        for (Ui.El e : u.quranEls) {
            switch (e.kind) {
                case Ui.K_BACK: backBtn(c, u, e); break;
                case Ui.K_BTN: btn(c, u, e); break;
                case Ui.K_PLAY: case Ui.K_PREV: case Ui.K_NEXT: case Ui.K_REPEAT: case Ui.K_SCREENOFF: controls(c, u, e); break;
                default: break;
            }
        }
        // الملفات
        u.text(c, "الملفات (" + Fmt.digits(u.tracks.size()) + ")  •  اضغط مطولاً للتحديد", 1856, 128, 24, t.dim, R, Fonts.regular);
        c.save();
        c.clipRect(840, 150, Ui.W, Ui.H);
        c.translate(0, -u.scroll);
        for (int i = 0; i < u.quranEls.size(); i++) {
            Ui.El e = u.quranEls.get(i);
            if (e.kind != Ui.K_ROW || i == u.qf) continue;
            trackRow(c, u, e);
        }
        Ui.El fe = u.currentEl();
        if (fe != null && fe.kind == Ui.K_ROW) trackRow(c, u, fe);
        c.restore();
        if (u.tracks.isEmpty()) {
            String m = u.folder.isEmpty() ? "اختار فولدر القرآن من زر (تغيير الفولدر)" : (u.host.hasStorage() ? "مفيش ملفات صوت في الفولدر ده" : "محتاج تسمح بالوصول للملفات");
            u.text(c, m, 1358, 560, 32, t.dim, C, Fonts.medium);
        }
    }

    private static void trackRow(Canvas c, Ui u, Ui.El e) {
        Theme t = u.theme;
        if (e.r.bottom - u.scroll < 160 || e.r.top - u.scroll > Ui.H) return;
        u.pop(c, e, 0.02f);
        boolean playing = Player.mode == Player.MODE_FOLDER && Player.index == e.idx && u.plActive;
        u.fill(c, e.r, 22, Ui.lerp(playing ? Ui.alpha(t.accent, 0.18f) : t.tile, t.tileF, e.f));
        u.stroke(c, e.r, 22, playing ? t.accent : t.line, playing ? 2f : 1.2f);
        u.ring(c, e, 22, t.accent);
        if (playing) u.icon(c, u.plPlaying ? Icons.VOL : Icons.PAUSE, e.r.right - 40, e.cy(), 30, t.accent, 2f);
        else u.text(c, Fmt.digits(e.idx + 1), e.r.right - 40, e.cy() + 10, 26, t.dim, C, Fonts.medium);
        u.text(c, u.fit(e.label, 30, 760, Fonts.medium), e.r.right - 80, e.cy() + 11, 30, playing ? t.accent : t.txt, R, Fonts.medium);
        if (e.on) {
            u.circle(c, e.r.left + 44, e.cy(), 20, t.accent);
            u.icon(c, Icons.CHECK, e.r.left + 44, e.cy(), 24, t.onAccent, 2.4f);
        }
        c.restore();
    }

    // ---------------- قائمة عامة ----------------

    static void list(Canvas c, Ui u) {
        Theme t = u.theme;
        header(c, u, u.listTitle, u.listHint);
        for (Ui.El e : u.listEls) if (e.kind == Ui.K_BACK) backBtn(c, u, e);
        c.save();
        c.clipRect(0, 164, Ui.W, Ui.H);
        c.translate(0, -u.scroll);
        for (int i = 0; i < u.listEls.size(); i++) {
            Ui.El e = u.listEls.get(i);
            if (e.kind != Ui.K_ROW || i == u.lf) continue;
            listRow(c, u, e);
        }
        Ui.El fe = u.currentEl();
        if (fe != null && fe.kind == Ui.K_ROW) listRow(c, u, fe);
        c.restore();
        if (u.lLabel.isEmpty()) u.text(c, "لا يوجد عناصر", Ui.W / 2f, 500, 32, t.dim, C, Fonts.medium);
    }

    private static void listRow(Canvas c, Ui u, Ui.El e) {
        Theme t = u.theme;
        if (e.r.bottom - u.scroll < 160 || e.r.top - u.scroll > Ui.H) return;
        u.pop(c, e, 0.025f);
        u.fill(c, e.r, 24, Ui.lerp(t.tile, t.tileF, e.f));
        u.stroke(c, e.r, 24, t.line, 1.2f);
        u.ring(c, e, 24, t.accent);
        boolean hasSub = e.sub != null && !e.sub.isEmpty();
        float ly = hasSub ? e.r.top + 38 : e.cy() + 11;
        u.text(c, u.fit(e.label, 30, 760, Fonts.bold), e.r.right - 30, ly, 30, t.txt, R, Fonts.bold);
        if (hasSub) u.text(c, u.fit(e.sub, 21, 800, Fonts.regular), e.r.right - 30, e.r.top + 68, 21, t.dim, R, Fonts.regular);
        if (e.val != null && !e.val.isEmpty()) {
            u.text(c, u.fit(e.val, 28, 330, Fonts.medium), e.r.left + 30, e.cy() + 10, 28, t.accent, L, Fonts.medium);
        }
        if (e.on) {
            u.circle(c, e.r.left + 48, e.cy(), 20, t.accent);
            u.icon(c, Icons.CHECK, e.r.left + 48, e.cy(), 24, t.onAccent, 2.4f);
        }
        c.restore();
    }

    // ---------------- الأذكار ----------------

    static void adhkar(Canvas c, Ui u) {
        Theme t = u.theme;
        header(c, u, Content.CAT_NAMES[u.adhCat], Fmt.digits(u.adhIdx + 1) + " / " + Fmt.digits(u.adhList.length));
        for (Ui.El e : u.adhkarEls) {
            if (e.kind == Ui.K_BACK) backBtn(c, u, e);
            else if (e.kind == Ui.K_BTN) btn(c, u, e);
        }
        Ui.El card = u.adhkarEls.get(1);
        u.pop(c, card, 0.012f);
        u.fill(c, card.r, 40, t.panel);
        u.stroke(c, card.r, 40, Ui.lerp(t.line, t.accent, Math.max(card.f, u.doneAnim * 2f)), 2f);
        if (u.adhList.length > 0) {
            Content.Dhikr d = u.adhList[u.adhIdx];
            float size = 56, lh;
            List<String> lines = null;
            for (; size >= 26; size -= 2) {
                lh = size * 1.7f;
                lines = u.wrap(d.text, size, Fonts.quran, 1240);
                if (lines.size() * lh <= 400) break;
            }
            lh = size * 1.7f;
            float totalH = lines.size() * lh;
            float y = card.r.top + 60 + (400 - totalH) / 2f + size;
            u.paragraph(c, lines, card.cx(), y, size, lh, t.txt, C, Fonts.quran);
            u.text(c, d.note, card.cx(), card.r.top + 500, 24, t.dim, C, Fonts.regular);
            int done = u.adhDone[u.adhIdx];
            boolean full = done >= d.count;
            // شريط العدّ
            u.tmp.set(card.cx() - 400, card.r.top + 540, card.cx() + 400, card.r.top + 548);
            u.fill(c, u.tmp, 4, Ui.alpha(t.txt, 0.15f));
            u.tmp.set(card.cx() + 400 - 800f * Math.min(1f, done / (float) d.count), card.r.top + 540, card.cx() + 400, card.r.top + 548);
            u.fill(c, u.tmp, 4, full ? t.good : t.accent);
            u.text(c, Fmt.digits(done) + " / " + Fmt.digits(d.count), card.cx(), card.r.top + 600, 48, full ? t.good : t.accent, C, Fonts.extra);
            if (full) u.icon(c, Icons.CHECK, card.cx() + 190, card.r.top + 585, 40, t.good, 2.6f);
        }
        c.restore();
        u.text(c, "اضغط OK للعدّ  •  يمين/يسار للتنقل بين الأذكار", Ui.W / 2f, 1000, 24, t.dim, C, Fonts.regular);
    }

    // ---------------- المسبحة ----------------

    static void tasbeeh(Canvas c, Ui u) {
        Theme t = u.theme;
        header(c, u, "المسبحة", "اضغط OK للتسبيح");
        for (Ui.El e : u.tasbeehEls) {
            if (e.kind == Ui.K_BACK) backBtn(c, u, e);
            else if (e.kind == Ui.K_BTN) btn(c, u, e);
        }
        Ui.El card = u.tasbeehEls.get(1);
        u.pop(c, card, 0.012f);
        u.fill(c, card.r, 50, t.panel);
        u.stroke(c, card.r, 50, Ui.lerp(t.line, t.accent, card.f), 2f);
        float cx = card.cx(), cy = 430f, r = 200f;
        u.pStroke.setPathEffect(null);
        u.pStroke.setStrokeCap(Paint.Cap.ROUND);
        u.pStroke.setStrokeWidth(16);
        u.pStroke.setColor(u.col(Ui.alpha(t.txt, 0.12f)));
        c.drawCircle(cx, cy, r, u.pStroke);
        u.tmp.set(cx - r, cy - r, cx + r, cy + r);
        u.pStroke.setColor(u.col(t.accent));
        float sw = 360f * Math.min(1f, u.tasCount / (float) Math.max(1, u.tasGoal));
        if (sw > 0.5f) c.drawArc(u.tmp, -90, sw, false, u.pStroke);
        u.pStroke.setStrokeCap(Paint.Cap.BUTT);
        u.text(c, Fmt.digits(u.tasCount), cx, cy + 56, 160, t.txt, C, Fonts.extra);
        u.text(c, "من " + Fmt.digits(u.tasGoal), cx, cy + 112, 30, t.dim, C, Fonts.medium);
        u.text(c, "سُبْحَانَ اللَّهِ  •  الْحَمْدُ لِلَّهِ  •  اللَّهُ أَكْبَرُ", cx, 720, 40, t.accent, C, Fonts.quran);
        u.text(c, "إجمالي تسبيحاتك: " + Fmt.digits(u.tasTotal), cx, 780, 26, t.dim, C, Fonts.regular);
        c.restore();
    }

    // ---------------- ضبط الساعة ----------------

    static void clockset(Canvas c, Ui u) {
        Theme t = u.theme;
        header(c, u, "ضبط الساعة والتاريخ", "من غير إنترنت — اضبطها لو الشاشة نسيت الوقت بعد فصل الكهرباء");
        String[] lab = {"اليوم", "الشهر", "السنة", "الساعة", "الدقيقة"};
        for (Ui.El e : u.clockEls) {
            if (e.kind == Ui.K_BACK) backBtn(c, u, e);
            else if (e.kind == Ui.K_BTN) btn(c, u, e);
            else if (e.kind == Ui.K_FIELD) {
                u.pop(c, e, 0.04f);
                u.fill(c, e.r, 30, Ui.lerp(t.panel, t.tileF, e.f));
                u.stroke(c, e.r, 30, t.line, 1.5f);
                u.ring(c, e, 30, t.accent);
                u.text(c, lab[e.idx], e.cx(), e.r.top + 44, 26, t.dim, C, Fonts.medium);
                int v = u.cs[e.idx];
                String s = e.idx == 2 ? Fmt.digits(v) : (v < 10 ? "٠" : "") + Fmt.digits(v);
                u.text(c, s, e.cx(), e.r.top + 138, 84, t.txt, C, Fonts.extra);
                if (e.f > 0.5f) {
                    u.icon(c, Icons.BACK, e.cx(), e.r.top + 12, 22, t.accent, 2.4f);
                }
                c.restore();
            }
        }
        u.text(c, "اضغط فوق/تحت لتغيير القيمة • يمين/يسار للتنقل بين الخانات", 1700, 590, 26, t.dim, R, Fonts.regular);
        String cur = "الوقت الحالي بالتطبيق: " + u.clockDigits + " " + u.clockSfx + "  •  " + u.dateStr;
        u.text(c, cur, 1700, 800, 30, t.txt, R, Fonts.medium);
        long diff = Clock.offsetMs;
        String d = diff == 0 ? "بتعتمد على ساعة الشاشة كما هي" : "فرق الضبط اليدوي عن ساعة الشاشة: " + Fmt.human(Math.abs(diff)) + (diff > 0 ? " (زيادة)" : " (نقص)");
        u.text(c, d, 1700, 850, 26, t.dim, R, Fonts.regular);
        if (u.clockWarn != 0) u.text(c, u.clockWarn == 1 ? "ساعة الشاشة قديمة جداً (غالباً اتصفّرت)" : "الساعة رجعت لورا عن آخر مرة (غالباً فصل كهرباء)", 1700, 900, 26, t.bad, R, Fonts.medium);
        if (Clock.autoSync) u.text(c, "ملحوظة: الشاشة ضابطة وقتها تلقائياً من الشبكة حالياً", 1700, 950, 24, t.good, R, Fonts.regular);
    }

    // ---------------- الإشعار القانوني ----------------

    static void legal(Canvas c, Ui u) {
        Theme t = u.theme;
        u.pFill.setShader(null);
        u.pFill.setColor(0xD9000000);
        c.drawRect(0, 0, Ui.W, Ui.H, u.pFill);
        u.tmp.set(260, 70, 1660, 1010);
        u.fill(c, u.tmp, 40, Ui.lerp(t.panel, 0xFF000000, 0.15f) | 0xFF000000);
        u.stroke(c, u.tmp, 40, t.accent, 2f);
        u.textSp(c, "ABDOU-LUNCHER", 960, 160, 46, t.accent, C, Fonts.extra, 0.14f);
        u.text(c, "Legal Notice & Terms of Use", 960, 202, 24, t.dim, C, Fonts.regular);
        if (u.legalLines == null) {
            ArrayList<String> out = new ArrayList<>();
            out.add("#This device: " + u.host.deviceInfo());
            String info = u.host.deviceInfo().toLowerCase();
            if (!(info.contains("toshiba") || info.contains("hismart") || info.contains("atv"))) {
                out.add("! This device does not match the permitted device in section 3. You continue at your own risk (section 4).");
            }
            out.add("");
            for (String para : Legal.BODY.split("\n")) {
                if (para.startsWith("# ")) {
                    out.add("");
                    out.add("#" + para.substring(2));
                } else if (para.startsWith("- ")) {
                    for (String l : u.wrap(para.substring(2), 24, Fonts.regular, 1180)) out.add((out.size() > 0 && l == null ? "" : "") + "  " + l);
                } else {
                    out.addAll(u.wrap(para, 24, Fonts.regular, 1240));
                }
            }
            u.legalLines = out;
        }
        float lh = 36f, total = u.legalLines.size() * lh;
        float maxScroll = Math.max(0f, total - 640f);
        if (u.legalScroll > maxScroll) u.legalScroll = maxScroll;
        c.save();
        c.clipRect(300, 236, 1620, 880);
        float y = 270 - u.legalScroll;
        for (String l : u.legalLines) {
            if (l.startsWith("#")) u.text(c, l.substring(1), 330, y, 27, t.accent, L, Fonts.bold);
            else if (l.startsWith("!")) u.text(c, l.substring(1).trim(), 330, y, 24, t.bad, L, Fonts.medium);
            else u.text(c, l, 330, y, 24, t.txt, L, Fonts.regular);
            y += lh;
        }
        c.restore();
        // خط وتذييل
        u.pStroke.setStrokeWidth(1.5f);
        u.pStroke.setColor(Ui.alpha(t.accent, 0.5f));
        c.drawLine(330, 896, 1590, 896, u.pStroke);
        u.text(c, Legal.FOOT + ".", 960, 944, 32, t.accent, C, Fonts.bold);
        u.text(c, "Press OK / Back to close  •  Up / Down to scroll", 960, 986, 18, t.dim, C, Fonts.regular);
    }

    static void toast(Canvas c, Ui u) {
        Theme t = u.theme;
        float w = Math.min(1100f, u.measure(u.toastMsg, 30, Fonts.bold) + 90f);
        u.tmp.set(960 - w / 2f, 930, 960 + w / 2f, 1000);
        u.pFill.setShader(null);
        u.pFill.setColor(0xE6101820);
        c.drawRoundRect(u.tmp, 35, 35, u.pFill);
        u.pStroke.setStrokeWidth(1.5f);
        u.pStroke.setColor(Ui.alpha(t.accent, 0.7f));
        c.drawRoundRect(u.tmp, 35, 35, u.pStroke);
        u.text(c, u.toastMsg, 960, 976, 30, 0xFFFFFFFF, C, Fonts.bold);
    }
}
