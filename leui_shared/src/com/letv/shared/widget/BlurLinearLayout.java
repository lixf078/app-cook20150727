
package com.letv.shared.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.letv.shared.R;

/**
 * LinearLayout which applies blur effect into its background
 */
public class BlurLinearLayout extends LinearLayout {

    // Blur renderer instance
    private GaussianBlurRenderer mBlurRenderer;

    /**
     * Default constructor
     */
    public BlurLinearLayout(Context context) {
        super(context);
        init(null);
    }

    public BlurLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BlurLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    /**
     * Initialize layout to handle background blur effect
     */
    private void init(AttributeSet attrs) {
        mBlurRenderer = new GaussianBlurRenderer(this);

        if (attrs != null) {
            // Read blur radius from layout variables
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.LeBlurView);
            int radius = a.getDimensionPixelSize(R.styleable.LeBlurView_le_blur_radius, 0);
            mBlurRenderer.setBlurRadius(radius);
            a.recycle();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mBlurRenderer.getBlurEnabled()) {
            mBlurRenderer.onAttachedToWindow();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBlurRenderer.getBlurEnabled()) {
            mBlurRenderer.onDetachedFromWindow();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // If this is off-screen pass apply blur only
        if (mBlurRenderer.isOffscreenCanvas(canvas)) {
            mBlurRenderer.applyBlur();
        }
        // Otherwise draw blurred background image and continue to child views
        else {
            mBlurRenderer.drawToCanvas(canvas);
            super.dispatchDraw(canvas);
        }
    }

    /**
     * Set blur radius in pixels, default value is 0
     */
    public void setBlurRadius(int radius) {
        mBlurRenderer.setBlurRadius(radius);
        invalidate();
    }
    
    public int getBlurRadius() {
        return mBlurRenderer.getBlurRadius();
    }
    
    /**
     * Enable blur, default value is true
     */
    public void setBlurEnabled(boolean blurEnabled) {
        if (blurEnabled == mBlurRenderer.getBlurEnabled())
            return;
        
        mBlurRenderer.setBlurEnabled(blurEnabled);
        
        if (blurEnabled && this.isAttachedToWindow()) {
            mBlurRenderer.onAttachedToWindow();
            invalidate();
        } if (!blurEnabled && this.isAttachedToWindow()) {
            mBlurRenderer.onDetachedFromWindow();
            invalidate();
        }
    }
    
    public boolean getBlurEnabled() {
        return mBlurRenderer.getBlurEnabled();
    }
    
    public void setBlurAfterView(View view) {
        mBlurRenderer.setBlurAfterView(view);
    }
}
