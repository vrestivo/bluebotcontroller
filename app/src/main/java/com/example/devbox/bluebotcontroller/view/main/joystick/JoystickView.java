package com.example.devbox.bluebotcontroller.view.main.joystick;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Joystick object to be used as a controller
 */

public class JoystickView extends View {

    private int mViewHeight;
    private int mViewWidth;
    private long mViewPaddingX;
    private long mViewPaddingY;
    private boolean mIsActive;
    private float mXDistanceToCenter;
    private float mYDistanceToCenter;
    private float mNormalizedResultant;

    private Paint mJoystickBoundsPaint;
    private Paint mHandlePaint;
    private Circle mHandle;
    private Circle mBounds;

    //FIXME default values
    private int mHandleColor = 0xCC6F6F6F;
    private int mBackgroundColor = 0xCC51FF1F;
    private int mPadding = 16;
    private int mHandleRadius = 124;

    private MyScrollListener mMyScrollListener;
    private GestureDetector mScrollDetector;

    private OnJoystickDragListener mJoystickDragListener;
    private StopSendingJoystickDataListener mStopSendingListener;

    public interface OnJoystickDragListener {
        void onJoystickUpdate(float x, float y, float resultant, boolean keepSending);
    }

    public interface StopSendingJoystickDataListener {
        void onStopSending();
    }


    private void initJoystick(Context context) {
        mJoystickBoundsPaint = new Paint();
        mJoystickBoundsPaint.setColor(mBackgroundColor);

        mHandlePaint = new Paint();
        mHandlePaint.setColor(mHandleColor);
        mHandle = new Circle();
        mBounds = new Circle();

        mMyScrollListener = new MyScrollListener();
        mScrollDetector = new GestureDetector(context, mMyScrollListener);
    }

    public JoystickView(Context context) {
        super(context);
        initJoystick(context);
    }

    public JoystickView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initJoystick(context);
    }

    public JoystickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initJoystick(context);
    }

    public void setJoystickDragListener(OnJoystickDragListener listener) {
        mJoystickDragListener = listener;
    }

    public void setStopSendingListener(StopSendingJoystickDataListener listener) {
        mStopSendingListener = listener;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewHeight = h;
        mViewWidth = w;

        mBounds.setCenterY(mViewHeight / 2);
        mBounds.setCenterX(mViewWidth / 2);
        mBounds.setRadius((Math.min(h, w) - mPadding * 2) / 2);

        mHandle.init(mBounds.getCenterX(), mBounds.getCenterY(), mHandleRadius);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        drawJoystickBound(canvas);
        drawJoystickHandle(canvas);
    }


    private void drawJoystickBound(Canvas canvas) {
        canvas.drawCircle(
                mBounds.getCenterX(),
                mBounds.getCenterY(),
                mBounds.getRadius(),
                mJoystickBoundsPaint
        );
    }


    private void drawJoystickHandle(Canvas canvas) {
        canvas.drawCircle(
                mHandle.getCenterX(),
                mHandle.getCenterY(),
                mHandle.getRadius(),
                mHandlePaint
        );
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mIsActive = isInsideCircle(
                        mHandle.getCenterX(),
                        mHandle.getCenterY(),
                        mHandle.getRadius(),
                        event.getX(),
                        event.getY()
                );
                if (mIsActive) {
                    normalizeOffsets(mHandle.getCenterX(), mHandle.getCenterY());
                    //mStopSendingListener.onStopSending();
                    if (mJoystickDragListener != null) {
                        mJoystickDragListener.onJoystickUpdate(mXDistanceToCenter, mYDistanceToCenter, mNormalizedResultant, true);
                    }
                }
                invalidate();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                if(mStopSendingListener!=null) mStopSendingListener.onStopSending();
                boolean result = mScrollDetector.onTouchEvent(event);
                return result;
            }
            case MotionEvent.ACTION_UP: {
                mIsActive = false;
                mHandle.setCenterX(mBounds.getCenterX());
                mHandle.setCenterY(mBounds.getCenterY());
                normalizeOffsets(mHandle.getCenterX(), mHandle.getCenterY());
                if(mStopSendingListener!=null) mStopSendingListener.onStopSending();
                invalidate();
                return true;
            }
        }
        return false;
    }

    public boolean isInsideCircle(float circleCenterX, float circleCenterY, float circleRadius, float pointToCheckX, float pointToCheckY) {
        float distance = (float) Math.sqrt((pointToCheckX - circleCenterX) * (pointToCheckX - circleCenterX) + (pointToCheckY - circleCenterY) * (pointToCheckY - circleCenterY));
        return distance < circleRadius;
    }

    public void normalizeOffsets(float x, float y) {
        //need to multiply S value by -1 IOT reflect traditional
        //cartesian X coordinate values
        //0.0f is added to avoid a negative zero
        mXDistanceToCenter = (mBounds.getCenterX() - x) / mBounds.getRadius() * 100 * (-1) + 0.0f;
        mYDistanceToCenter = (mBounds.getCenterY() - y) / mBounds.getRadius() * 100 + 0.0f;

        if (mXDistanceToCenter < 0) {
            mXDistanceToCenter = Math.max(mXDistanceToCenter, -100);
        } else if (mXDistanceToCenter > 0) {
            mXDistanceToCenter = Math.min(mXDistanceToCenter, 100);
        } else if (mYDistanceToCenter < 0) {
            mYDistanceToCenter = Math.max(mYDistanceToCenter, -100);
        } else if (mYDistanceToCenter > 0) {
            mYDistanceToCenter = Math.min(mYDistanceToCenter, 100);
        }

        calculateNormalizedResultant();
    }

    public void calculateNormalizedResultant() {
        float distanceSquared = (mXDistanceToCenter * mXDistanceToCenter) + (mYDistanceToCenter * mYDistanceToCenter);
        float actualResultant = (float) (Math.sqrt((double) distanceSquared));
        mNormalizedResultant = Math.min(actualResultant, 100);
    }


    class MyScrollListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mIsActive) {
                mHandle.setCenterX(e2.getX());
                mHandle.setCenterY(e2.getY());
                normalizeOffsets(mHandle.getCenterX(), mHandle.getCenterY());
                if(mJoystickDragListener!=null) {
                    mJoystickDragListener.onJoystickUpdate(mXDistanceToCenter, mYDistanceToCenter, mNormalizedResultant, true);
                }
                invalidate();

                return true;
            }
            return false;
        }
    }

}
