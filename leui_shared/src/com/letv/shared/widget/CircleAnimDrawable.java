package com.letv.shared.widget;
import android.animation.*;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
// import android.util.Log;


/**
 * Created by dupengtao on 14-8-29.
 */
public class CircleAnimDrawable extends Drawable implements
        ValueAnimator.AnimatorUpdateListener {

    private static final String CIRCLE_FILL_PROPERTY = "circleScale";
    private static final String PAINT_ALPHA_PROPERTY = "paintAlpha";
    private final Paint mPaint;
    private int centerColor,paintAlpha=0;
    private float circleScale;
    private boolean mPressed = false;

    public CircleAnimDrawable(int centerColor) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        circleScale = 0.5f;
        this.centerColor = centerColor;
    }

    @Override
    public void draw(Canvas canvas) {
        final Rect bounds = getBounds();
        int size = bounds.height() > bounds.width() ? bounds.width() : bounds.height();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(centerColor);
        mPaint.setAlpha(paintAlpha);
        canvas.drawCircle(bounds.centerX(), bounds.centerY(), size / 2 * circleScale, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public int getAlpha() {
        return super.getAlpha();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {}

    @Override
    public int getOpacity() {
        return mPaint.getAlpha();
    }

    public float getCircleScale() {
        return circleScale;
    }

    public void setCircleScale(float circleScale) {
        this.circleScale = circleScale;
    }

    public int getCenterColor() {
        return centerColor;
    }

    public void setCenterColor(int centerColor) {
        this.centerColor = centerColor;
    }

    public int getPaintAlpha() {
        return paintAlpha;
    }

    public void setPaintAlpha(int paintAlpha) {
        this.paintAlpha = paintAlpha;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        invalidateSelf();
    }

    // new
    private ObjectAnimator getDownSizeAnim(CircleAnimDrawable animDrawable) {
        PropertyValuesHolder pvSize = PropertyValuesHolder.ofFloat(CIRCLE_FILL_PROPERTY,
                0.5f, 1f);
        ObjectAnimator sizeAnim = ObjectAnimator.ofPropertyValuesHolder(
                animDrawable, pvSize).setDuration(350);
        sizeAnim.setInterpolator(new DecelerateInterpolator());
        sizeAnim.addUpdateListener(this);
        sizeAnim.setAutoCancel(true);
        return sizeAnim;
    }

    private ObjectAnimator getDownPaintAlphaAnim(CircleAnimDrawable animDrawable) {
        PropertyValuesHolder pvAlpha = PropertyValuesHolder.ofInt(PAINT_ALPHA_PROPERTY,
                0, 45);
        ObjectAnimator alphaAnim = ObjectAnimator.ofPropertyValuesHolder(
                animDrawable, pvAlpha).setDuration(350);
        //alphaAnim.setStartDelay(225);
        alphaAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnim.addUpdateListener(this);
        alphaAnim.setAutoCancel(true);
        return alphaAnim;
    }

    public void downAnim() {
        ObjectAnimator downSizeAnim = getDownSizeAnim(this);
        ObjectAnimator paintAlphaAnim = getDownPaintAlphaAnim(this);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(downSizeAnim).with(paintAlphaAnim);
        animatorSet.start();
    }

    private ObjectAnimator getUpSizeAnim(CircleAnimDrawable animDrawable) {
        PropertyValuesHolder pvSize = PropertyValuesHolder.ofFloat(CIRCLE_FILL_PROPERTY,
                1f, 0f);
        ObjectAnimator sizeAnim = ObjectAnimator.ofPropertyValuesHolder(
                animDrawable, pvSize).setDuration(350);
        sizeAnim.setInterpolator(new LinearInterpolator());
        sizeAnim.addUpdateListener(this);
        return sizeAnim;
    }

    private ObjectAnimator getUpPaintAlphaAnim(CircleAnimDrawable animDrawable) {
        PropertyValuesHolder pvAlpha = PropertyValuesHolder.ofInt(PAINT_ALPHA_PROPERTY,
                45, 0);
        ObjectAnimator alphaAnim = ObjectAnimator.ofPropertyValuesHolder(
                animDrawable, pvAlpha).setDuration(350);
        alphaAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnim.addUpdateListener(this);
        alphaAnim.setAutoCancel(true);
        return alphaAnim;
    }

    public void upAnim() {
        ObjectAnimator upPaintAlphaAnim = getUpPaintAlphaAnim(this);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(upPaintAlphaAnim);
        animatorSet.start();
    }

    @Override
    protected boolean onStateChange(int[] state) {
        // Log.d("CircleAnimDrawable","onStateChange(),state:"+StateSet.dump(state));
        boolean pressed = isPressed(state);
        if(mPressed != pressed){
            mPressed = pressed;
            if (mPressed) {
                downAnim();
            } else {
                upAnim();
            }

        }
        return true;
    }

    @Override
    public boolean setState(int[] stateSet) {
        // Log.d("CircleAnimDrawable","setState(),stateSet:"+StateSet.dump(stateSet));
        return super.setState(stateSet);
    }
    @Override
    public boolean isStateful() {
        // Log.d("CircleAnimDrawable","isStateful(),super.isStateful():"+super.isStateful());
        return true;
    }
    private boolean isPressed(int[] state){
        boolean pressed = false;
        for(int i=0,j=state!=null ? state.length:0;i<j;i++){
            if(state[i] == android.R.attr.state_pressed){
                pressed = true;
                break;
            }
        }
        return pressed;
    }
}
