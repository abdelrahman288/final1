package com.noor.launcher;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;

/** إنترو فتح الشاشة: نجمة إسلامية بتترسم + البسملة + التوقيع، وبعدها بيتلاشى على الشاشة الرئيسية. */
final class Intro {
    private Intro() {}

    private static Path sq1, sq2;
    private static RadialGradient glow;
    private static final RectF R = new RectF();

    private static void init() {
        if (sq1 != null) return;
        float r = 190f;
        sq1 = new Path();
        sq2 = new Path();
        for (int i = 0; i < 4; i++) {
            double a = Math.PI / 4 + i * Math.PI / 2, b = Math.PI / 4 * 0 + i * Math.PI / 2;
            float x1 = (float) Math.cos(a) * r, y1 = (float) Math.sin(a) * r;
            float x2 = (float) Math.cos(b) * r, y2 = (float) Math.sin(b) * r;
            if (i == 0) {
                sq1.moveTo(x1, y1);
                sq2.moveTo(x2, y2);
            } else {
                sq1.lineTo(x1, y1);
                sq2.lineTo(x2, y2);
            }
        }
        sq1.close();
        sq2.close();
        glow = new RadialGradient(0, 0, 560, new int[]{0x55F2C14E, 0x22F2C14E, 0x00F2C14E}, new float[]{0f, 0.45f, 1f}, Shader.TileMode.CLAMP);
    }

    static float seg(float t, float a, float b) {
        return Ui.ease((t - a) / (b - a));
    }

    static final String BISM = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ";
    static final int GOLD = 0xFFF2C14E;
    private static Path moon, arch;

    static void draw(Canvas c, Ui u, float prog) {
        int st = Pref.i(Pref.INTRO_STYLE, 0);
        if (st == 1) crescent(c, u);
        else if (st == 2) mihrab(c, u);
        else classic(c, u);
    }

    /** خلفية الإنترو وقيمة الاختفاء. بترجّع -1 لو الإنترو خلص. */
    private static float begin(Canvas c, Ui u) {
        float fade = 1f - seg(u.tAnim, u.introLen - 0.7f, u.introLen);
        if (fade <= 0f) return -1f;
        c.save();
        float z = 1f + (1f - fade) * 0.08f;
        c.scale(z, z, 960, 540);
        u.ga = fade;
        u.pFill.setShader(null);
        u.pFill.setColor(u.col(u.theme.bg2 | 0xFF000000));
        c.drawRect(-200, -200, Ui.W + 200, Ui.H + 200, u.pFill);
        return fade;
    }

    private static void sparkle(Canvas c, Ui u, float x, float y, float r, int col, float a) {
        if (a <= 0f) return;
        u.pFill.setShader(null);
        u.pFill.setColor(u.col(Ui.alpha(col, a)));
        Path p = new Path();
        p.moveTo(x, y - r);
        p.quadTo(x, y, x + r, y);
        p.quadTo(x, y, x, y + r);
        p.quadTo(x, y, x - r, y);
        p.quadTo(x, y, x, y - r);
        p.close();
        c.drawPath(p, u.pFill);
    }

    /** إنترو 1: هلال بيطلع من تحت وسط نجوم بتلمع وأشعة، والبسملة بتتكشف. */
    private static void crescent(Canvas c, Ui u) {
        init();
        float fade = begin(c, u);
        if (fade < 0f) return;
        float t = u.tAnim;
        if (moon == null) {
            moon = new Path();
            float R = 120f, r = 100f, d = 45f;
            float x = (R * R - r * r + d * d) / (2 * d), y = (float) Math.sqrt(R * R - x * x);
            double th = Math.atan2(y, x), ph = Math.atan2(y, x - d);
            for (int i = 0; i <= 40; i++) {
                double a = th + (2 * Math.PI - 2 * th) * i / 40.0;
                float px = (float) Math.cos(a) * R, py = (float) Math.sin(a) * R;
                if (i == 0) moon.moveTo(px, py);
                else moon.lineTo(px, py);
            }
            for (int i = 0; i <= 40; i++) {
                double a = (2 * Math.PI - ph) - (i / 40.0) * (2 * Math.PI - 2 * ph);
                moon.lineTo(d + (float) Math.cos(a) * r, (float) Math.sin(a) * r);
            }
            moon.close();
        }
        // أشعة
        float gp = seg(t, 0f, 1.2f);
        c.save();
        c.translate(960, 400);
        c.rotate(t * 6f);
        for (int i = 0; i < 12; i++) {
            c.save();
            c.rotate(i * 30f);
            u.pFill.setShader(null);
            u.pFill.setColor(u.col(Ui.alpha(GOLD, 0.07f * gp * fade)));
            Path ray = new Path();
            ray.moveTo(0, 0);
            ray.lineTo(-26, -520);
            ray.lineTo(26, -520);
            ray.close();
            c.drawPath(ray, u.pFill);
            c.restore();
        }
        c.restore();
        u.pFill.setShader(glow);
        c.save();
        c.translate(960, 400);
        u.pFill.setAlpha((int) (255 * gp * fade));
        c.drawCircle(0, 0, 560, u.pFill);
        u.pFill.setShader(null);
        c.restore();
        // نجوم
        for (int i = 0; i < 26; i++) {
            float fx = ((i * 7919) % 100) / 100f, fy = ((i * 104729) % 100) / 100f;
            float tw = 0.5f + 0.5f * (float) Math.sin(t * 3f + i * 1.7f);
            float appear = seg(t, 0.1f + (i % 9) * 0.07f, 0.6f + (i % 9) * 0.07f);
            sparkle(c, u, 140 + fx * 1640, 70 + fy * 700, 5f + (i % 4) * 2.2f, i % 3 == 0 ? 0xFFFFFFFF : GOLD, appear * tw * fade);
        }
        // الهلال
        float mp = seg(t, 0.1f, 1.3f);
        float bob = (float) Math.sin(t * 1.6f) * 4f;
        c.save();
        c.translate(960, 400 + (1f - mp) * 260f + bob);
        c.rotate(-22f + (1f - mp) * -40f);
        float ms = 0.5f + 0.5f * mp;
        c.scale(ms, ms);
        u.pFill.setColor(u.col(Ui.alpha(GOLD, mp * fade)));
        c.drawPath(moon, u.pFill);
        c.restore();
        // نجمة جنب الهلال
        sparkle(c, u, 1040, 340 + bob, 22f * seg(t, 0.9f, 1.4f), 0xFFFFFFFF, fade);
        // البسملة (بتتكشف من النص)
        float bp = seg(t, 0.8f, 1.7f);
        if (bp > 0f) {
            float half = 560f * bp;
            c.save();
            c.clipRect(960 - half, 560, 960 + half, 720);
            u.ga = fade;
            u.text(c, BISM, 960, 650, 78, 0xFFF6E7B4, Paint.Align.CENTER, Fonts.quran);
            c.restore();
        }
        u.ga = 1f;
        c.restore();
    }

    /** إنترو 2: محراب بيترسم + موجات نور + لمعة بتعدّي على البسملة. */
    private static void mihrab(Canvas c, Ui u) {
        init();
        float fade = begin(c, u);
        if (fade < 0f) return;
        float t = u.tAnim;
        if (arch == null) {
            arch = new Path();
            arch.moveTo(-190, 250);
            arch.lineTo(-190, 20);
            arch.quadTo(-190, -170, 0, -290);
            arch.quadTo(190, -170, 190, 20);
            arch.lineTo(190, 250);
        }
        float gp = seg(t, 0f, 1.1f);
        // موجات نور من المركز
        for (int i = 0; i < 4; i++) {
            float ph = ((t * 0.5f) + i * 0.25f) % 1f;
            float rad = 60f + ph * 520f;
            u.pStroke.setStrokeWidth(2f);
            u.pStroke.setColor(u.col(Ui.alpha(GOLD, (1f - ph) * 0.35f * gp * fade)));
            c.drawCircle(960, 560, rad, u.pStroke);
        }
        // نور جوه المحراب
        c.save();
        c.translate(960, 560);
        u.pFill.setShader(glow);
        u.pFill.setAlpha((int) (255 * gp * fade));
        c.drawCircle(0, 0, 560, u.pFill);
        u.pFill.setShader(null);
        // المحراب بيترسم
        float ap = seg(t, 0.05f, 1.25f);
        float per = 1500f;
        u.pStroke.setStrokeWidth(4f);
        u.pStroke.setColor(u.col(GOLD));
        u.pStroke.setPathEffect(new DashPathEffect(new float[]{per, per}, per * (1f - ap)));
        c.scale(1.45f, 1.45f);
        c.drawPath(arch, u.pStroke);
        u.pStroke.setPathEffect(null);
        float ap2 = seg(t, 0.35f, 1.4f);
        c.scale(0.9f, 0.9f);
        u.pStroke.setStrokeWidth(1.4f);
        u.pStroke.setColor(u.col(Ui.alpha(GOLD, 0.45f * ap2)));
        c.drawPath(arch, u.pStroke);
        c.restore();
        // جزيئات بتطلع
        for (int i = 0; i < 18; i++) {
            float ph = (t * 0.4f + i * 0.17f) % 1f;
            float x = 960 + (((i * 37) % 21) - 10) * 15f + (float) Math.sin(ph * 6 + i) * 14f;
            float y = 760 - ph * 520f;
            u.circle(c, x, y, 2f + (i % 3), Ui.alpha(GOLD, (float) Math.sin(ph * Math.PI) * 0.7f * gp * fade));
        }
        // البسملة بتطلع من تحت ولمعة بتعدي
        float bp = seg(t, 0.7f, 1.6f);
        if (bp > 0f) {
            c.save();
            c.clipRect(300, 500, 1620, 640);
            u.ga = fade * bp;
            u.text(c, BISM, 960, 590 + (1f - bp) * 60f, 78, 0xFFF6E7B4, Paint.Align.CENTER, Fonts.quran);
            float sh = seg(t, 1.2f, 2.1f);
            if (sh > 0f && sh < 1f) {
                float sx = 300 + sh * 1320f;
                c.save();
                c.clipRect(sx - 60, 500, sx + 60, 640);
                u.ga = fade * 0.9f;
                u.text(c, BISM, 960, 590, 78, 0xFFFFFFFF, Paint.Align.CENTER, Fonts.quran);
                c.restore();
            }
            c.restore();
        }
        u.ga = 1f;
        c.restore();
    }

    private static void classic(Canvas c, Ui u) {
        init();
        Theme th = u.theme;
        float t = u.tAnim;               // ثواني
        float len = u.introLen;
        float fade = 1f - seg(t, len - 0.7f, len);     // اختفاء الإنترو
        if (fade <= 0f) return;
        int gold = 0xFFF2C14E;
        c.save();
        // زووم خفيف أثناء الاختفاء
        float z = 1f + (1f - fade) * 0.08f;
        c.scale(z, z, 960, 520);
        u.ga = fade;
        u.pFill.setShader(null);
        u.pFill.setColor(u.col(th.bg2 | 0xFF000000));
        c.drawRect(-200, -200, Ui.W + 200, Ui.H + 200, u.pFill);

        // توهّج
        float gp = seg(t, 0f, 1.0f);
        c.save();
        c.translate(960, 470);
        c.scale(0.4f + 0.6f * gp, 0.4f + 0.6f * gp);
        u.pFill.setShader(glow);
        u.pFill.setAlpha((int) (255 * Math.min(1f, gp) * fade));
        c.drawCircle(0, 0, 560, u.pFill);
        u.pFill.setShader(null);
        c.restore();

        // النجمة (بترسم نفسها)
        float sp = seg(t, 0.05f, 1.15f);
        c.save();
        c.translate(960, 470);
        c.rotate(t * 14f);
        u.pStroke.setStrokeWidth(3.2f);
        u.pStroke.setColor(u.col(gold));
        float per = 190f * 1.4142f * 4f;
        u.pStroke.setPathEffect(new DashPathEffect(new float[]{per, per}, per * (1f - sp)));
        c.drawPath(sq1, u.pStroke);
        c.drawPath(sq2, u.pStroke);
        u.pStroke.setPathEffect(null);
        float rp = seg(t, 0.3f, 1.3f);
        u.pStroke.setStrokeWidth(2f);
        u.pStroke.setColor(u.col(Ui.alpha(gold, 0.6f * rp)));
        c.drawCircle(0, 0, 150 * rp, u.pStroke);
        u.pStroke.setColor(u.col(Ui.alpha(gold, 0.35f * rp)));
        c.drawCircle(0, 0, 232 * rp, u.pStroke);
        c.restore();

        // نقاط بتلمع
        for (int i = 0; i < 16; i++) {
            float ph = (t * 0.55f + i * 0.137f) % 1f;
            float ang = i * 0.39f + t * 0.3f;
            float rr = 120f + ph * 330f;
            float x = 960 + (float) Math.cos(ang) * rr, y = 470 + (float) Math.sin(ang) * rr * 0.8f - ph * 30f;
            float a = (float) Math.sin(ph * Math.PI) * 0.8f * gp;
            u.circle(c, x, y, 2.2f + (i % 3), Ui.alpha(gold, a));
        }

        // البسملة
        float bp = seg(t, 0.55f, 1.35f);
        if (bp > 0f) {
            c.save();
            c.translate(0, (1f - bp) * 26f);
            float s = 0.94f + 0.06f * bp;
            c.scale(s, s, 960, 480);
            u.ga = fade * bp;
            u.text(c, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", 960, 492, 78, 0xFFF6E7B4, Paint.Align.CENTER, Fonts.quran);
            c.restore();
        }
        // التوقيع والاسم
        float lp = seg(t, 1.15f, 1.85f);
        if (lp > 0f) {
            u.ga = fade * lp;
            if (u.logo != null) {
                float w = 340f, h = w * u.logo.getHeight() / u.logo.getWidth();
                R.set(960 - w / 2f, 640 - h / 2f + (1f - lp) * 16f, 960 + w / 2f, 640 + h / 2f + (1f - lp) * 16f);
                u.pBmp.setAlpha((int) (255 * u.ga));
                c.drawBitmap(u.logo, null, R, u.pBmp);
            }
            u.textSp(c, "ABDOU-LUNCHER", 960, 758 + (1f - lp) * 16f, 30, gold, Paint.Align.CENTER, Fonts.bold, 0.3f);
        }
        u.ga = 1f;
        c.restore();
    }
}
