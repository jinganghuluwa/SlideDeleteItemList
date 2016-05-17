package com.example.tongzhichao.slidedelete;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by tongzhichao on 16-5-16.
 */
public class SlideView extends FrameLayout {

    private final String TAG = getClass().getSimpleName();
    private float x = -1, y = -1;

    private boolean move = false;
    private GestureDetector mGestureDetector;
    private ViewDragHelper mDragger;
    private ViewDragHelper.Callback mCallback;
    private int mDefaultslideWidth;
    private boolean mIsOpen = false;
    private View mContentView;
    private boolean mIsScrolled = false;
    private boolean mIsAutoScrolled = false;
    private OnStateChangedListener mOnStateChangedListener;

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCallback = new DrawerCallback();
        mDragger = ViewDragHelper.create(this, 1.0f, mCallback);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContentView = getChildAt(1);
        if (mContentView == null) {
            throw new NullPointerException("contentview is null");
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildAt(0) != null) {
            mDefaultslideWidth = getChildAt(0).getWidth();
        } else {
            try {
                mDefaultslideWidth = getChildAt(0).getWidth();
            } catch (NullPointerException e) {
                Log.e("DrawerLayout", "Layout has at least one child view!");
            }

        }
    }


    private class DrawerCallback extends ViewDragHelper.Callback {


        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (mIsAutoScrolled) {
                return false;
            }
            return child == mContentView;
        }


        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return Math.max(Math.min(mDefaultslideWidth, left), 0);
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return Math.max(Math.min(mDefaultslideWidth, child.getLeft()), 0);
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return super.getViewVerticalDragRange(child);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return 0;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            switch (state) {
                case ViewDragHelper.STATE_DRAGGING:
                    mIsScrolled = true;
                    break;
                case ViewDragHelper.STATE_IDLE:
                    mIsAutoScrolled = false;
                    mIsScrolled = false;
                    if (mContentView.getLeft() == 0) {
                        mIsOpen = false;
                    } else {
                        mIsOpen = true;
                    }
                    if (mOnStateChangedListener != null) {
                        if (mIsOpen) {
                            mOnStateChangedListener.onOpen(mContentView);
                        } else {
                            mOnStateChangedListener.onClosed(mContentView);
                        }
                    }
                    break;
                case ViewDragHelper.STATE_SETTLING:
                    mIsAutoScrolled = true;
                    break;

            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == mContentView) {
                if (mOnStateChangedListener != null) {
                    mOnStateChangedListener.onScrolled(mContentView, (int) (((float) left / (float) mDefaultslideWidth) * 100));
                }
            }
            super.onViewPositionChanged(changedView, left, top, dx, dy);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (mIsAutoScrolled) {
                return;
            }
            if (releasedChild == mContentView) {
                if (mIsScrolled) {
                    if (xvel <= 0) {
                        mDragger.settleCapturedViewAt(0, 0);
                    } else {
                        mDragger.settleCapturedViewAt(mDefaultslideWidth, 0);
                    }
                } else if (mIsOpen) {
                    if (xvel <= 0) {
                        mDragger.settleCapturedViewAt(0, 0);
                    }
                } else {
                    if (xvel > 0) {
                        mDragger.settleCapturedViewAt(mDefaultslideWidth, 0);
                    }
                }
                invalidate();
            } else {
                super.onViewReleased(releasedChild, xvel, yvel);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
//        if (e.getAction() == MotionEvent.ACTION_CANCEL) {
//        }
//        if (e.getAction() == MotionEvent.ACTION_DOWN) {
//            Log.e(TAG, "onInterceptTouchEvent:ACTION_DOWN");
//            x = e.getX();
//            y = e.getY();
//        }
//        if (e.getAction() == MotionEvent.ACTION_MOVE && foget) {
//            Log.e(TAG, "onInterceptTouchEvent:giveup");
//            x = e.getX();
//            y = e.getY();
//            return false;
//        }
//        if (e.getAction() == MotionEvent.ACTION_MOVE) {
//            Log.e(TAG, "onInterceptTouchEvent:move");
//            if (Math.abs(e.getX() - x) >= Math.abs(e.getY() - y)) {
//                x = e.getX();
//                y = e.getY();
//                Log.e(TAG, "onInterceptTouchEvent: giveup");
//                return false;
//            }
//            x = e.getX();
//            y = e.getY();
//        }
        return mDragger.shouldInterceptTouchEvent(e);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.e(TAG, "onTouchEvent:ACTION_DOWN");
        }
        mDragger.processTouchEvent(event);
        return true;
    }


    @Override
    public void computeScroll() {
        if (mDragger.continueSettling(true)) {
            invalidate();
        }
    }

    public void open() {
        if (!mIsOpen) {
            controlView();
        }
    }

    public void close() {
        if (mIsOpen) {
            controlView();
        }
    }

    public void controlView() {
        if (mIsScrolled || mIsAutoScrolled) {
            return;
        }
        if (mIsOpen) {
            mDragger.smoothSlideViewTo(mContentView, 0, 0);
        } else {
            mDragger.smoothSlideViewTo(mContentView, mDefaultslideWidth, 0);
        }
        invalidate();
    }

    public interface OnStateChangedListener {
        void onOpen(View view);

        void onClosed(View view);

        void onScrolled(View view, int percentage);
    }

    public void setOnStateChangedListener(OnStateChangedListener onStateChangedListener) {
        mOnStateChangedListener = onStateChangedListener;
    }

    public boolean isOpen() {
        return mIsOpen;
    }
}
