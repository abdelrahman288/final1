package com.noor.launcher;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/** قراءة ملفات الصوت من الفولدر اللي المستخدم حدده. */
final class Media {
    private Media() {}

    static final String[] EXT = {".mp3", ".m4a", ".aac", ".ogg", ".opus", ".wav", ".flac", ".wma"};
    static final String KEY_SEL = "selTracks";

    static boolean isAudio(String n) {
        String l = n.toLowerCase();
        for (String e : EXT) if (l.endsWith(e)) return true;
        return false;
    }

    static final Comparator<File> NATURAL = (a, b) -> natural(a.getName(), b.getName());

    static int natural(String a, String b) {
        int i = 0, j = 0;
        while (i < a.length() && j < b.length()) {
            char x = a.charAt(i), y = b.charAt(j);
            if (Character.isDigit(x) && Character.isDigit(y)) {
                int si = i, sj = j;
                while (i < a.length() && Character.isDigit(a.charAt(i))) i++;
                while (j < b.length() && Character.isDigit(b.charAt(j))) j++;
                String na = a.substring(si, i).replaceFirst("^0+(?=.)", ""), nb = b.substring(sj, j).replaceFirst("^0+(?=.)", "");
                if (na.length() != nb.length()) return na.length() - nb.length();
                int c = na.compareTo(nb);
                if (c != 0) return c;
            } else {
                int c = Character.toLowerCase(x) - Character.toLowerCase(y);
                if (c != 0) return c;
                i++;
                j++;
            }
        }
        return (a.length() - i) - (b.length() - j);
    }

    /** كل ملفات الصوت في الفولدر (من غير الفولدرات الفرعية). */
    static List<File> scan(String folder) {
        ArrayList<File> out = new ArrayList<>();
        if (folder == null || folder.isEmpty()) return out;
        try {
            File[] fs = new File(folder).listFiles();
            if (fs == null) return out;
            for (File f : fs) if (f.isFile() && isAudio(f.getName())) out.add(f);
            Collections.sort(out, NATURAL);
        } catch (Exception ignored) {
        }
        return out;
    }

    static HashSet<String> selection() {
        HashSet<String> s = new HashSet<>();
        String v = Pref.s(KEY_SEL, "");
        if (v.isEmpty()) return s;
        s.addAll(Arrays.asList(v.split("\n")));
        return s;
    }

    static void saveSelection(HashSet<String> s) {
        StringBuilder b = new StringBuilder();
        for (String x : s) {
            if (b.length() > 0) b.append('\n');
            b.append(x);
        }
        Pref.set(KEY_SEL, b.toString());
    }

    /** قائمة التشغيل الفعلية: المختارة لو في اختيار، وإلا كل الفولدر. */
    static List<File> playlist(String folder) {
        List<File> all = scan(folder);
        HashSet<String> sel = selection();
        if (sel.isEmpty()) return all;
        ArrayList<File> out = new ArrayList<>();
        for (File f : all) if (sel.contains(f.getName())) out.add(f);
        return out.isEmpty() ? all : out;
    }

    static String nice(String fileName) {
        String n = fileName;
        int d = n.lastIndexOf('.');
        if (d > 0) n = n.substring(0, d);
        return n.replace('_', ' ').trim();
    }

    /** الفولدرات الفرعية + مجلدات التخزين الأساسية. */
    static List<File> dirs(File parent) {
        ArrayList<File> out = new ArrayList<>();
        File[] fs = parent == null ? null : parent.listFiles();
        if (fs != null) for (File f : fs) if (f.isDirectory() && !f.getName().startsWith(".")) out.add(f);
        Collections.sort(out, NATURAL);
        return out;
    }

    static List<File> roots() {
        ArrayList<File> out = new ArrayList<>();
        File emu = new File("/storage/emulated/0");
        if (emu.exists()) out.add(emu);
        File[] vols = new File("/storage").listFiles();
        if (vols != null) {
            for (File v : vols) {
                String n = v.getName();
                if (n.equals("emulated") || n.equals("self")) continue;
                if (v.isDirectory() && !out.contains(v)) out.add(v);
            }
        }
        if (out.isEmpty()) out.add(new File("/sdcard"));
        return out;
    }
}
