package com.cyandev.panelslider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.ListView;

/**
 * Created by unixzii on 16/1/9.
 */
public class PanelSlider extends FrameLayout {

    private float mScaledTouchSlop;
    private float mScaledDensity;

    /**
     * The initial height when the panel is collapsed.
     */
    private float mInitialHeight;

    /**
     * Used for determining the progress and dummy height.
     */
    private float mProgress;
    private float mCurrentTranslateY;

    private float mLastY;

    private boolean mIsCaptured = false;
    private boolean mCanInterceptChildTouchEvent = false;

    /**
     * The progress listener
     */
    private OnProgressChangeListener mListener;

    /**
     * The velocity independent animator.
     */
    private AnimationRunnable mAnimationRunnable;

    /**
     * Used for determining whether to open or close the panel.
     */
    private VelocityTracker mVelocityTracker;

    public PanelSlider(Context context) {
        this(context, null);
    }

    public PanelSlider(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PanelSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Get scaled density and touch slop
        mScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        // Listen the first onPreDraw event
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                onFirstPreDraw();
                getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });

        mInitialHeight = px(100); // TODO: This value should be adjustable in layout xml file.

        mAnimationRunnable = new AnimationRunnable();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.drawColor(Color.argb((int) (150 - 150 * (1.f - mProgress)), 0, 0, 0)); // Draw shade

        super.dispatchDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mAnimationRunnable.forceStop();

        if (!mIsCaptured && event.getY() < mCurrentTranslateY) {
            return false; // Ignore the touch event when touched outside.
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = event.getY();
                mVelocityTracker.addMovement(event);

                break;
            case MotionEvent.ACTION_MOVE:
                if (!mIsCaptured) {
                    if (Math.abs(mLastY - event.getY()) >= mScaledTouchSlop) {
                        mIsCaptured = true;
                        mLastY = event.getY();
                    }
                } else {
                    // TODO: Suck, do the refactor soon.
                    float calcTranslateY = mCurrentTranslateY + event.getY() - mLastY;
                    mLastY = event.getY();
                    setProgress(1 - calcTranslateY / (getHeight() - mInitialHeight));
                }

                mVelocityTracker.addMovement(event);

                break;
            case MotionEvent.ACTION_UP:
                mIsCaptured = false;
                mCanInterceptChildTouchEvent = false;

                mVelocityTracker.computeCurrentVelocity(1000);

                if (mProgress > 0.5) {
                    if (mVelocityTracker.getYVelocity() > 1500) {
                        animateToProgress(0);
                    } else {
                        animateToProgress(1);
                    }
                } else {
                    if (mVelocityTracker.getYVelocity() < -3000) {
                        animateToProgress(1);
                    } else {
                        animateToProgress(0);
                    }
                }

                mVelocityTracker.recycle();
                mVelocityTracker = null;

                break;
        }

        return mIsCaptured;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO: I think there may be some issue making scrolling lagged sometimes.
        if (mProgress != 1 || mCanInterceptChildTouchEvent) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastY = ev.getY();
                case MotionEvent.ACTION_MOVE:
                    if (Math.abs(mLastY - ev.getY()) > mScaledTouchSlop) {
                        return true;
                    }
            }
        }

        if (ev.getAction() == MotionEvent.ACTION_UP) {
            mCanInterceptChildTouchEvent = false;
        }

        return super.onInterceptTouchEvent(ev);
    }

    public void allowInterceptChildTouchEvent() {
        mCanInterceptChildTouchEvent = true;
    }

    public void disallowInterceptChildTouchEvent() {
        mCanInterceptChildTouchEvent = false;
    }

    public void setInitialHeight(float h) {
        mInitialHeight = h;

        if (mProgress == 0) {
            setProgress(0);
        }
    }

    public float getInitialHeight() {
        return mInitialHeight;
    }

    public void setProgress(float progress) {
        mProgress = Math.max(Math.min(1, progress), 0);

        mCurrentTranslateY = (getHeight() - mInitialHeight) * (1.f - mProgress);
        setChildrenTranslate(0, mCurrentTranslateY);

        if (mListener != null) {
            mListener.onProgressChange(mProgress);
        }

        postInvalidate();
    }

    public float getProgress() {
        return mProgress;
    }

    public void animateToProgress(float progress) {
        mAnimationRunnable.setTargetProgress(progress);
        postOnAnimationCompat(mAnimationRunnable);
    }

    /**
     * Register a callback to be invoked when the progress change.
     * <p>
     *
     * @param l The listener to notify when the progress change.
     */
    public void setOnProgressListener(OnProgressChangeListener l) {
        mListener = l;
    }

    public OnProgressChangeListener getOnProgressListener() {
        return mListener;
    }

    private void setChildrenTranslate(float x, float y) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);

            view.setTranslationX(x);
            view.setTranslationY(y);
        }
    }

    private void onFirstPreDraw() {
        setProgress(0);
    }

    private void postOnAnimationCompat(Runnable r) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            postOnAnimation(r);
        } else {
            postDelayed(r, 16);
        }
    }

    private float px(float dp) {
        return dp * mScaledDensity;
    }

    private class AnimationRunnable implements Runnable {

        private float mTargetProgress;

        @Override
        public void run() {
            if (Math.abs(mTargetProgress - mProgress) < 0.001) {
                setProgress(mTargetProgress);
                return;
            }

            setProgress(mProgress + (mTargetProgress - mProgress) / 5.f);

            postOnAnimationCompat(this);
        }

        public void setTargetProgress(float progress) {
            mTargetProgress = progress;
        }

        public void forceStop() {
            mTargetProgress = mProgress;
        }
    }

    public interface OnProgressChangeListener {
        void onProgressChange(float progress);
    }
}
