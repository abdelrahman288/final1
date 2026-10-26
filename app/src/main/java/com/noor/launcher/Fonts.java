package com.noor.launcher;

import android.graphics.Typeface;

/** الخطوط (Tajawal للواجهة، Amiri للأسماء، Amiri Quran للآيات). كلها جوه التطبيق، من غير إنترنت. */
final class Fonts {
    private Fonts() {}

    interface Loader {
        Typeface load(String file);
    }

    static Typeface light, regular, medium, bold, extra, serif, serifBold, quran;

    static void init(Loader l) {
        light = get(l, "Tajawal-ExtraLight.ttf", Typeface.DEFAULT);
        regular = get(l, "Tajawal-Regular.ttf", Typeface.DEFAULT);
        medium = get(l, "Tajawal-Medium.ttf", regular);
        bold = get(l, "Tajawal-Bold.ttf", Typeface.DEFAULT_BOLD);
        extra = get(l, "Tajawal-ExtraBold.ttf", bold);
        serif = get(l, "Amiri-Regular.ttf", Typeface.SERIF);
        serifBold = get(l, "Amiri-Bold.ttf", Typeface.SERIF);
        quran = get(l, "AmiriQuran-Regular.ttf", serif);
    }

    private static Typeface get(Loader l, String f, Typeface fb) {
        try {
            Typeface t = l.load(f);
            return t != null ? t : fb;
        } catch (Throwable e) {
            return fb;
        }
    }
}
