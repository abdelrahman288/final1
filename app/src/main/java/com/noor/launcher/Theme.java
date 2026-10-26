package com.noor.launcher;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.BitmapShader;

/** تصميم الشاشة الرئيسية. كل تصميم بيحدد الألوان + مكان العناصر + طريقة رسمها. */
abstract class Theme {
    final String name;
    // ألوان تستخدمها باقي الشاشات (التطبيقات، القرآن، الإعدادات…)
    int bg1, bg2, tile, tileF, panel, txt, dim, accent, onAccent, good, bad, line;

    Theme(String name) {
        this.name = name;
    }

    /** خلفية ثابتة (بتترسم مرة واحدة في Bitmap). مساحة التصميم 1920x1080. */
    abstract void base(Canvas c, Ui u);

    /** يضيف عناصر الشاشة الرئيسية (u.addHome). */
    abstract void layout(Ui u);

    /** رسم الشاشة الرئيسية. */
    abstract void home(Canvas c, Ui u);

    String title() {
        return name;
    }

    // ---------------- أدوات مشتركة ----------------

    /** زخرفة هندسية إسلامية خفيفة (بلاطة 120x120 بتتكرر). */
    static void pattern(Canvas c, int color, float alpha) {
        Bitmap t = Bitmap.createBitmap(120, 120, Bitmap.Config.ARGB_8888);
        Canvas tc = new Canvas(t);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(1.5f);
        p.setColor(Ui.alpha(color | 0xFF000000, alpha));
        Path d = new Path();
        d.moveTo(60, 6);
        d.lineTo(114, 60);
        d.lineTo(60, 114);
        d.lineTo(6, 60);
        d.close();
        tc.drawPath(d, p);
        tc.drawCircle(60, 60, 22, p);
        tc.save();
        tc.rotate(45, 60, 60);
        tc.drawRect(44, 44, 76, 76, p);
        tc.restore();
        Paint fp = new Paint();
        fp.setShader(new BitmapShader(t, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
        c.drawRect(0, 0, Ui.W, Ui.H, fp);
    }

    static Shader goldV(float top, float bottom) {
        return new LinearGradient(0, top, 0, bottom, 0xFFF9DE8F, 0xFFC8932F, Shader.TileMode.CLAMP);
    }

    /** أيقونة "مشغل القرآن" الداخلية (مربع ذهبي بنوتة). */
    void quranIcon(Canvas c, Ui u, float l, float t, float s) {
        u.tmp.set(l, t, l + s, t + s);
        u.fillShader(c, u.tmp, s * 0.26f, goldV(t, t + s));
        u.icon(c, Icons.MUSIC, l + s / 2f, t + s / 2f, s * 0.56f, 0xFF1B1305, 2f);
    }

    void appIconIn(Canvas c, Ui u, Ui.El e, float l, float t, float s) {
        if (e.kind == Ui.K_QURAN) {
            quranIcon(c, u, l, t, s);
        } else if (e.app != null) {
            u.tmp.set(l, t, l + s, t + s);
            u.bmp(c, e.app.icon, u.tmp);
        }
    }

    /** التوقيع + سطر الحقوق (أسفل اليسار). */
    void logo(Canvas c, Ui u, Ui.El e) {
        if (e == null) return;
        if (e.f > 0.02f) {
            u.stroke(c, e.r, 16, Ui.alpha(accent, e.f), 2.5f);
        }
        if (u.logo != null) {
            float w = 130f, h = w * u.logo.getHeight() / u.logo.getWidth();
            u.tmp.set(64, 1000, 64 + w, 1000 + h);
            u.pBmp.setAlpha((int) (255 * 0.65f * u.ga));
            c.drawBitmap(u.logo, null, u.tmp, u.pBmp);
        }
        u.text(c, "Abdou-Luncher", 209, 1026, 14, Ui.alpha(txt, 0.75f), Paint.Align.LEFT, Fonts.medium);
        u.text(c, "© All rights reserved to Abdelrahman Haitham", 209, 1046, 14, Ui.alpha(txt, 0.6f), Paint.Align.LEFT, Fonts.regular);
    }

    /** تنسيق شريط "صل على محمد". */
    void salawat(Canvas c, Ui u, float cx, float y, float size, int color) {
        u.text(c, "صَلِّ عَلَى مُحَمَّدٍ", cx, y, size, color, Paint.Align.CENTER, Fonts.quran);
        u.text(c, "صلى الله عليه وسلم", cx, y + size * 0.52f, size * 0.38f, Ui.alpha(dim, 0.9f), Paint.Align.CENTER, Fonts.regular);
    }

    /** أنيميشن نبض خفيف (0..1). */
    static float pulse() {
        return 0.5f + 0.5f * (float) Math.sin(Clock.mono() / 450.0);
    }
}
