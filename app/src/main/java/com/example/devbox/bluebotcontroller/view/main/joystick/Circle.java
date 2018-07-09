package com.example.devbox.bluebotcontroller.view.main.joystick;

/**
 * Object to store circle data
 */

public class Circle {
    private float mCenterX;
    private float mCenterY;
    private float mRadius;

    public Circle() {
        super();
        init(0,0,0);
    }

    public float getCenterX() {
        return mCenterX;
    }

    public float getCenterY() {
        return mCenterY;
    }

    public float getRadius() {
        return mRadius;
    }

    public void setCenterX(float mCenterX) {
        this.mCenterX = mCenterX;
    }

    public void setCenterY(float mCenterY) {
        this.mCenterY = mCenterY;
    }

    public void setRadius(float mRadius) {
        this.mRadius = mRadius;
    }

    public void init(float centerX, float centerY, float radius){
        mCenterX = centerX;
        mCenterY = centerY;
        mRadius = radius;
    }

}
