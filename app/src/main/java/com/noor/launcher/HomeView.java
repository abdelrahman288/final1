package com.noor.launcher;

import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

/** غلاف خفيف: بيرسم Ui على Canvas ويمرّر الريموت واللمس والماوس. */
final class HomeView extends View {
    private final Ui ui;
    private boolean running;

    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            if (!running) return;
            ui.tick();
            invalidate();
            postDelayed(this, 1000 - (System.currentTimeMillis() % 1000) + 5);
        }
    };

    HomeView(Context c, Ui ui) {
        super(c);
        this.ui = ui;
        setFocusable(true);
        setFocusableInTouchMode(true);
        setDefaultFocusHighlightEnabled(false);
        setLayerType(LAYER_TYPE_NONE, null);
    }

    void start() {
        running = true;
        removeCallbacks(ticker);
        ui.kick();
        post(ticker);
    }

    void stop() {
        running = false;
        removeCallbacks(ticker);
    }

    @Override
    protected void onDraw(android.graphics.Canvas c) {
        if (ui.draw(c, getWidth(), getHeight())) postInvalidateOnAnimation();
    }

    @Override
    public boolean onKeyDown(int k, KeyEvent e) {
        if (k == KeyEvent.KEYCODE_BACK) {
            e.startTracking();
            return true;
        }
        if (Ui.isCenter(k)) {
            if (e.getRepeatCount() == 0) e.startTracking();
            return true;
        }
        if (k == KeyEvent.KEYCODE_MENU) {
            ui.openSettings();
            ui.kick();
            return true;
        }
        if (ui.key(k)) {
            ui.kick();
            return true;
        }
        return super.onKeyDown(k, e);
    }

    @Override
    public boolean onKeyLongPress(int k, KeyEvent e) {
        if (Ui.isCenter(k)) {
            ui.longPress();
            ui.kick();
            return true;
        }
        return super.onKeyLongPress(k, e);
    }

    @Override
    public boolean onKeyUp(int k, KeyEvent e) {
        boolean ok = e.isTracking() && !e.isCanceled();
        if (k == KeyEvent.KEYCODE_BACK) {
            if (ok) ui.back();
            return true;
        }
        if (Ui.isCenter(k)) {
            if (ok) ui.activate();
            return true;
        }
        return super.onKeyUp(k, e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int a = e.getActionMasked();
        ui.touch(a == MotionEvent.ACTION_DOWN ? 0 : (a == MotionEvent.ACTION_UP ? 1 : (a == MotionEvent.ACTION_MOVE ? 2 : 3)),
                e.getX(), e.getY());
        return true;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_HOVER_MOVE) {
            ui.hover(e.getX(), e.getY());
            return true;
        }
        if (e.getAction() == MotionEvent.ACTION_SCROLL) {
            ui.scrollBy(e.getAxisValue(MotionEvent.AXIS_VSCROLL));
            return true;
        }
        return super.onGenericMotionEvent(e);
    }
}
