package com.example.tongzhichao.slidedelete;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by tongzhichao on 16-5-17.
 */
public class Recyler extends RecyclerView {

    private final String TAG = getClass().getSimpleName();

    private float x = -1, y = -1;


    private boolean foget = false;


    public Recyler(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_CANCEL) {
            foget = false;
        }
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            Log.e(TAG, "onInterceptTouchEvent:ACTION_DOWN");
            x = e.getX();
            y = e.getY();
            foget = false;
        }
        if (e.getAction() == MotionEvent.ACTION_MOVE && foget) {
            Log.e(TAG, "onInterceptTouchEvent:giveup");
            x = e.getX();
            y = e.getY();
            return false;
        }
        if (e.getAction() == MotionEvent.ACTION_MOVE) {
            Log.e(TAG, "onInterceptTouchEvent:move");
            if (Math.abs(e.getX() - x) >= Math.abs(e.getY() - y)) {
                foget = true;
                x = e.getX();
                y = e.getY();
                Log.e(TAG, "onInterceptTouchEvent: giveup");
                return false;
            }
            x = e.getX();
            y = e.getY();
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            Log.e(TAG, "onTouchEvent:ACTION_DOWN");
        }
        return super.onTouchEvent(e);
    }


}
