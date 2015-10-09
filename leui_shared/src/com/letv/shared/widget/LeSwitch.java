/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.letv.shared.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Switch;

import com.letv.shared.R;

public class LeSwitch extends Switch implements LeCheckable, ValueAnimator.AnimatorUpdateListener,
        Animator.AnimatorListener {
    private static final int DISABLE_ALPHA = (int) (255 * 0.3);
    private static final int TOUCH_MODE_IDLE = 0;
    private static final int TOUCH_MODE_DOWN = 1;
    private static final int TOUCH_MODE_DRAGGING = 2;
    private final int mTrackRadius;
    private ObjectAnimator mCurrentAnimator;
    private ObjectAnimator mThumbOnAnimator;
    private ObjectAnimator mThumbOffAnimator;
    private int mThumbAnimateTime;
    private int mTrackColor;

    private Drawable mThumbDrawable;
    private Drawable mTrackDrawable;

    private int mTouchMode;
    private int mTouchSlop;
    private float mTouchX;
    private float mTouchY;
    private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
    private int mMinFlingVelocity;

    private float mThumbPosition;
    private int mSwitchWidth;
    private int mSwitchHeight;
    private int mThumbWidth; // Does not include padding

    private int mSwitchLeft;
    private int mSwitchTop;
    private int mSwitchRight;
    private int mSwitchBottom;

    private final Rect mTempRect = new Rect();
    private int mSwitchPivotX;
    private int mSwitchPivotY;
    private int mThumbHeight;
    private RectF mTempRectF = new RectF();
    private TextPaint mPaint;
    
    private int mAlpha = 255;
    RectF mSaveLayerRectF = new RectF();

    public LeSwitch(Context context) {
        this(context, null);
    }

    public LeSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.checkedTextViewStyle);
    }

    public LeSwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle, 0);

        Resources res = getResources();

//        setTextOff("");
//        setTextOn("");
        setShowText(false);
        
        mTrackColor = res.getColor(R.color.le_color_default_switch_on);
        mThumbDrawable = res.getDrawable(R.drawable.le_switch_thumb);
        mTrackDrawable = res.getDrawable(R.drawable.le_switch_track);
        mThumbAnimateTime = res.getInteger(R.integer.le_default_switch_animate_time);

        TypedValue outValue = new TypedValue();
        final Resources.Theme theme = context.getTheme();
        if(theme.resolveAttribute(android.R.attr.colorControlActivated, outValue, true)) {
            mTrackColor = outValue.data;
        }
        
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.leSwitch, defStyle, 0);
        if (a != null) {
            int n = a.getIndexCount();
            if (a.hasValue(R.styleable.leSwitch_leSwitchTrackBg)) {
                mThumbDrawable = a.getDrawable(R.styleable.leSwitch_leSwitchTrackBg);
            }
            mTrackColor = a.getColor(R.styleable.leSwitch_leSwitchTrackColor, mTrackColor);
            mThumbAnimateTime = a.getDimensionPixelSize(R.styleable.leSwitch_leSwitchThumbAnimateTime, mThumbAnimateTime);
/*            for (int i = 0; i < n; i ++) {
                int attr = a.getIndex(i);
                switch (attr) {
                    case R.styleable.leSwitch_leSwitchTrackBg:
                        mThumbDrawable = a.getDrawable(attr);
                        break;
                    case R.styleable.leSwitch_leSwitchTrackColor:
                        mTrackColor = a.getColor(attr, mTrackColor);
                        break;
                    case R.styleable.leSwitch_leSwitchThumbAnimateTime:
                        mThumbAnimateTime = a.getDimensionPixelSize(attr, mThumbAnimateTime);
                        break;
                }
            }*/
        }
        a.recycle();
        
        boolean clickable = true;
        a = context.obtainStyledAttributes(attrs,
                R.styleable.View,
                defStyle, 0);

//        clickable = a.getBoolean(com.android.internal.R.styleable.View_clickable, clickable);
        a.recycle();
        setClickable(clickable);

        mThumbWidth = mThumbDrawable.getIntrinsicWidth();
        mThumbHeight = mThumbDrawable.getIntrinsicHeight();
        mTrackRadius = mThumbHeight / 2;
        mPaint = getPaint();

        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        mMinFlingVelocity = config.getScaledMinimumFlingVelocity();

        if (isEnabled()) {
            mAlpha = 255;
        } else {
            mAlpha = DISABLE_ALPHA;
        }
        
        final int switchWidth = mTrackDrawable.getIntrinsicWidth();
        final int switchHeight = mTrackDrawable.getIntrinsicHeight();

        mSwitchWidth = switchWidth;
        mSwitchHeight = switchHeight;

        mThumbOnAnimator = ObjectAnimator.ofFloat(this, "ThumbPosition", 0, getThumbScrollRange());
        mThumbOnAnimator.setDuration(mThumbAnimateTime);
        mThumbOnAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mThumbOnAnimator.addUpdateListener(this);
        mThumbOnAnimator.addListener(this);

        mThumbOffAnimator = ObjectAnimator.ofFloat(this, "ThumbPosition", getThumbScrollRange(), 0);
        mThumbOffAnimator.setDuration(mThumbAnimateTime);
        mThumbOffAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mThumbOffAnimator.addUpdateListener(this);
        mThumbOffAnimator.addListener(this);
        
        // reset the parent's useless parameters.
        super.setThumbDrawable(new ColorDrawable(Color.TRANSPARENT));
        super.setThumbTextPadding(0);
        
        // Refresh display with current params
        refreshDrawableState();
        setChecked(isChecked());
        setThumbPosition(isChecked());
    }


    @Override
    public void setSwitchTextAppearance(Context context, int resid) {
    }


    @Override
    public void setSwitchTypeface(Typeface tf, int style) {
    }

    @Override
    public void setSwitchTypeface(Typeface tf) {
    }

    @Override
    public void setThumbTextPadding(int pixels) {
    }

    @Override
    public void setTextOn(CharSequence textOn) {
    }

    @Override
    public void setTextOff(CharSequence textOff) {
    }

    public void setThumbPosition(float position) {
        final int thumbScrollRange = getThumbScrollRange();
        if (isTheLayoutRtl()) {
            mThumbPosition = thumbScrollRange - position;
        } else {
            mThumbPosition = position;
        }
        invalidate();
    }

    public void setTrackDrawable(Drawable track) {
        if (mTrackDrawable != null) {
            mTrackDrawable.setCallback(null);
        }
        mTrackDrawable = track;
        if (track != null) {
            track.setCallback(this);
        }
        requestLayout();
    }

    public void setTrackColor(int color) {
        if (mTrackColor != color) {
            mTrackColor = color;
        }
    }
    
    public void setTrackColorOff(int color) {
        if (mTrackDrawable != null ) {
            mTrackDrawable.setTintList(ColorStateList.valueOf(color));
        }
    }

    public void setTrackResource(int resId) {
        setTrackDrawable(getContext().getResources().getDrawable(resId));
    }

    public Drawable getTrackDrawable() {
        return mTrackDrawable;
    }

    public void setThumbDrawable(Drawable thumb) {
        if (mThumbDrawable != null) {
            mThumbDrawable.setCallback(null);
        }
        mThumbDrawable = thumb;
        if (thumb != null) {
            thumb.setCallback(this);
        }
        requestLayout();
    }

    public void setThumbResource(int resId) {
        setThumbDrawable(getContext().getResources().getDrawable(resId));
    }

    public Drawable getThumbDrawable() {
        return mThumbDrawable;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int switchWidth = mTrackDrawable.getIntrinsicWidth();
        final int switchHeight = mTrackDrawable.getIntrinsicHeight();

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int measuredHeight = getMeasuredHeight();
        if (measuredHeight < switchHeight) {
            setMeasuredDimension(getMeasuredWidthAndState(), switchHeight);
        }

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int switchRight;
        int switchLeft;

        if (isTheLayoutRtl()) {
            switchLeft = getPaddingLeft();
            switchRight = switchLeft + mSwitchWidth;
        } else {
            switchRight = getWidth() - getPaddingRight();
            switchLeft = switchRight - mSwitchWidth;
        }

        int switchTop = 0;
        int switchBottom = 0;
        switch (getGravity() & Gravity.VERTICAL_GRAVITY_MASK) {
            default:
            case Gravity.TOP:
                switchTop = getPaddingTop();
                switchBottom = switchTop + mSwitchHeight;
                break;

            case Gravity.CENTER_VERTICAL:
                switchTop = (getPaddingTop() + getHeight() - getPaddingBottom()) / 2 -
                        mSwitchHeight / 2;
                switchBottom = switchTop + mSwitchHeight;
                break;

            case Gravity.BOTTOM:
                switchBottom = getHeight() - getPaddingBottom();
                switchTop = switchBottom - mSwitchHeight;
                break;
        }

        mSwitchLeft = switchLeft;
        mSwitchTop = switchTop;
        mSwitchBottom = switchBottom;
        mSwitchRight = switchRight;

        mSwitchPivotX = (switchLeft + switchRight) / 2;
        mSwitchPivotY = (switchTop + switchBottom) / 2;

        mSaveLayerRectF.left = switchLeft;
        mSaveLayerRectF.top = switchTop;
        mSaveLayerRectF.right = switchRight;
        mSaveLayerRectF.bottom = switchBottom;
    }

    private boolean hitThumb(float x, float y) {
        mThumbDrawable.getPadding(mTempRect);
        final int thumbTop = mSwitchTop - mTouchSlop;
        final int thumbLeft = mSwitchLeft + (int) (mThumbPosition + 0.5f) - mTouchSlop;
        final int thumbRight = thumbLeft + mThumbWidth +
                mTempRect.left + mTempRect.right + mTouchSlop;
        final int thumbBottom = mSwitchBottom + mTouchSlop;
        return x > thumbLeft && x < thumbRight && y > thumbTop && y < thumbBottom;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mVelocityTracker.addMovement(ev);
        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();
                if (isEnabled() && hitThumb(x, y)) {
                    mTouchMode = TOUCH_MODE_DOWN;
                    mTouchX = x;
                    mTouchY = y;
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                switch (mTouchMode) {
                    case TOUCH_MODE_IDLE:
                        // Didn't target the thumb, treat normally.
                        break;
                    case TOUCH_MODE_DOWN: {
                        final float x = ev.getX();
                        final float y = ev.getY();
                        if (Math.abs(x - mTouchX) > mTouchSlop ||
                                Math.abs(y - mTouchY) > mTouchSlop) {
                            mTouchMode = TOUCH_MODE_DRAGGING;
                            getParent().requestDisallowInterceptTouchEvent(true);
                            mTouchX = x;
                            mTouchY = y;
                            return true;
                        }
                        break;
                    }
                    case TOUCH_MODE_DRAGGING: {
//                        final float x = ev.getX();
//                        final int thumbScrollRange = getThumbScrollRange();
//                        final float thumbScrollOffset = x - mTouchX;
//                        float dPos;
//                        if (thumbScrollRange != 0) {
//                            dPos = thumbScrollOffset / thumbScrollRange;
//                        } else {
//                            // If the thumb scroll range is empty, just use the
//                            // movement direction to snap on or off.
//                            dPos = thumbScrollOffset > 0 ? 1 : -1;
//                        }
//                        if (isLayoutRtl()) {
//                            dPos = -dPos;
//                        }
//                        final float newPos = MathUtils.constrain(mThumbPosition + dPos, 0, 1);
//                        if (newPos != mThumbPosition) {
//                            mTouchX = x;
//                            setThumbPosition(newPos);
//                        }
                        return true;
                    }
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mTouchMode == TOUCH_MODE_DRAGGING) {
                    stopDrag(ev);
                    return true;
                }
                mTouchMode = TOUCH_MODE_IDLE;
                mVelocityTracker.clear();
                break;
            }
        }

        return super.onTouchEvent(ev);
    }

    private void cancelSuperTouch(MotionEvent ev) {
        MotionEvent cancel = MotionEvent.obtain(ev);
        cancel.setAction(MotionEvent.ACTION_CANCEL);
        super.onTouchEvent(cancel);
        cancel.recycle();
    }

    private void stopDrag(MotionEvent ev) {
        mTouchMode = TOUCH_MODE_IDLE;

        // Commit the change if the event is up and not canceled and the switch
        // has not been disabled during the drag.
        final boolean commitChange = ev.getAction() == MotionEvent.ACTION_UP && isEnabled();
        final boolean newState;
        if (commitChange) {
            mVelocityTracker.computeCurrentVelocity(1000);
            final float xvel = mVelocityTracker.getXVelocity();
            if (Math.abs(xvel) > mMinFlingVelocity) {
//zhangyd remove for method isLayoutRtl is hidden
//                newState = isLayoutRtl() ? (xvel < 0) : (xvel > 0);
//zhangyd add
                newState =  (xvel > 0);
            } else {
                newState = getTargetCheckedState();
            }
        } else {
            newState = isChecked();
        }

        setChecked(newState);
        cancelSuperTouch(ev);
    }

    private void animateThumbToCheckedState(boolean newCheckedState) {
        // TODO animate!
        //float targetPos = newCheckedState ? 0 : getThumbScrollRange();
        //mThumbPosition = targetPos;
        super.setChecked(newCheckedState);
        setThumbPosition(isChecked());
        invalidate();
    }

    private boolean getTargetCheckedState() {
        if (isTheLayoutRtl()) {
            return mThumbPosition <= getThumbScrollRange() / 2;
        } else {
            return mThumbPosition >= getThumbScrollRange() / 2;
        }
    }

    private void setThumbPosition(boolean checked) {
        if (isTheLayoutRtl()) {
            mThumbPosition = checked ? 0 : getThumbScrollRange();
        } else {
            mThumbPosition = checked ? getThumbScrollRange() : 0;
        }
    }

    @Override
    public void toggle() {
        final boolean checked = isChecked();
        setChecked(!checked, true);
    }

    @Override
    public void setChecked(boolean checked) {
        setChecked(checked, false);
    }

    @Override
    public void setChecked(boolean checked, boolean playAnimation) {
        final boolean oldChecked = isChecked();

        if (oldChecked == checked) {
            return;
        }

        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
            mCurrentAnimator = null;
        }

        if (isAttachedToWindow() && isLaidOut()) {
            if (checked && mThumbOnAnimator != null) {
                mThumbOnAnimator.start();
                mCurrentAnimator = mThumbOnAnimator;
            } else if (!checked && mThumbOffAnimator != null) {
                mThumbOffAnimator.start();
                mCurrentAnimator = mThumbOffAnimator;
            }
        } else {
            // Immediately move the thumb to the new position.
            jumpDrawablesToCurrentState();
            setThumbPosition(checked);
        }
        super.setChecked(checked);
    }


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            mAlpha = 255;
        } else {
            mAlpha = DISABLE_ALPHA;
        }
    }

    private int[] location = new int[2];
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        getLocationInWindow(location);
        // Draw the switch
        int switchLeft = mSwitchLeft;
        int switchTop = mSwitchTop;
        int switchRight = mSwitchRight;
        int switchBottom = mSwitchBottom;
        int switchPivotX = mSwitchPivotX;
        int switchPivotY = mSwitchPivotY;

        if (!isEnabled()) {
            canvas.saveLayerAlpha(mSaveLayerRectF, mAlpha, Canvas.MATRIX_SAVE_FLAG
                    | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                    | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
        } else {
            canvas.save();
        }


        final Paint paint = mPaint;
        final int originColor = paint.getColor();
        paint.setColor(mTrackColor);
        final int trackRadius = mTrackRadius;
        mTempRectF.left = switchLeft +1;
        mTempRectF.top = switchTop + 1;
        mTempRectF.right = switchRight - 1;
        mTempRectF.bottom = switchBottom - 1;

        canvas.drawRoundRect(mTempRectF, trackRadius, trackRadius, paint);

        final int thumbScrollRange = getThumbScrollRange();
        final int offset;
        if (isTheLayoutRtl()) {
            offset = -(int) ((thumbScrollRange - mThumbPosition) * 0.2 + 0.5f);
        } else {
            offset = (int) (mThumbPosition * 0.2 + 0.5f);
        }

        int trackPivotX = switchPivotX + offset;
        final int trackPivotY = switchPivotY;

        final double scaleRate = (1 - Math.pow((isTheLayoutRtl() ?
                thumbScrollRange - mThumbPosition : mThumbPosition) / thumbScrollRange, 2));
        int switchOffWidth = (int) ((switchRight - switchLeft) * scaleRate);
        int switchOffHeight = (int) ((switchBottom - switchTop) * scaleRate);
        int switchOffLeft = trackPivotX - switchOffWidth / 2;
        int switchOffRight = switchOffLeft + switchOffWidth;
        int switchOffTop = trackPivotY - switchOffHeight / 2;
        int switchOffBottom = switchOffTop + switchOffHeight;

        mTrackDrawable.setBounds(switchOffLeft, switchOffTop, switchOffRight, switchOffBottom);
        mTrackDrawable.draw(canvas);

        mTrackDrawable.getPadding(mTempRect);
        int switchInnerLeft = switchLeft + mTempRect.left;
        int switchInnerRight = switchRight - mTempRect.right;
        mThumbDrawable.getPadding(mTempRect);
        final int thumbPos = (int) (mThumbPosition + 0.5f);
        int thumbLeft = switchInnerLeft - mTempRect.left + thumbPos;
        int thumbRight = switchInnerLeft + thumbPos + mThumbWidth + mTempRect.right;

        canvas.clipRect(switchInnerLeft, switchTop, switchInnerRight, switchBottom);

        mThumbDrawable.setBounds(thumbLeft, switchTop, thumbRight, switchBottom);
        mThumbDrawable.draw(canvas);

        paint.setColor(originColor);
        canvas.restore();
    }

    @Override
    public int getCompoundPaddingLeft() {
        if (!isTheLayoutRtl()) {
            return super.getCompoundPaddingLeft();
        }
        int padding = super.getCompoundPaddingLeft() + mSwitchWidth;

        return padding;
    }

    private boolean isTheLayoutRtl() {
        return (getLayoutDirection() == LAYOUT_DIRECTION_RTL);
    }

    @Override
    public int getCompoundPaddingRight() {
        if (isTheLayoutRtl()) {
            return super.getCompoundPaddingRight();
        }
        int padding = super.getCompoundPaddingRight() + mSwitchWidth;

        return padding;
    }

    private int getThumbScrollRange() {
        if (mTrackDrawable == null) {
            return 0;
        }
        mTrackDrawable.getPadding(mTempRect);
        return mSwitchWidth - mThumbWidth - mTempRect.left - mTempRect.right;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        int[] myDrawableState = getDrawableState();

        // Set the state of the Drawable
        // Drawable may be null when checked state is set from XML, from super constructor
        if (mThumbDrawable != null)
            mThumbDrawable.setState(myDrawableState);
        if (mTrackDrawable != null)
            mTrackDrawable.setState(myDrawableState);

        invalidate();
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == mThumbDrawable || who == mTrackDrawable;
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (mThumbDrawable != null)
            mThumbDrawable.jumpToCurrentState();
        if (mTrackDrawable != null)
            mTrackDrawable.jumpToCurrentState();

        if (mCurrentAnimator != null ) {
            mCurrentAnimator.cancel();
            mCurrentAnimator = null;
        }
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(LeSwitch.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(LeSwitch.class.getName());
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        invalidate();
    }

    @Override
    public void onAnimationStart(Animator animation) {
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (animation == mThumbOnAnimator) {
            setThumbPosition(true);
        } else if (animation == mThumbOffAnimator) {
            setThumbPosition(false);
        }
        invalidate();

        mCurrentAnimator = null;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

/*    private static final FloatProperty<LeSwitch> LE_THUMB_POS = new FloatProperty<LeSwitch>("lethumbPos") {
        @Override
        public Float get(LeSwitch object) {
            return object.mThumbPosition;
        }

        @Override
        public void setValue(LeSwitch object, float value) {
            object.setThumbPosition(value);
        }
    };*/
}
