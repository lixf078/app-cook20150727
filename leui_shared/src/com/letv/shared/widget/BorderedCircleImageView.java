package com.letv.shared.widget;

import com.letv.shared.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class BorderedCircleImageView extends ImageView {

    public static final String TAG = "BorderedCircleImageView";

    public static final int DEFAULT_RADIUS = 0;
    public static final int DEFAULT_BORDER = 0;
    public static final int DEFAULT_BORDER_COLOR = Color.BLACK;

    private int mCircleRadius;
    private int mBorderWidth;
    private int mBorderColor;

    private boolean mCustomizedXmlParamsLoaded; 
    private boolean mRoundBackground;
    private boolean mRoundImage = true; 

    private Drawable mImageDrawable;
    private Bitmap mImageBitmap;
    private Drawable mBackgroundDrawable;

    private ScaleType mScaleType;

    private static final ScaleType[] sScaleTypeArray = {
            ScaleType.MATRIX,
            ScaleType.FIT_XY,
            ScaleType.FIT_START,
            ScaleType.FIT_CENTER,
            ScaleType.FIT_END,
            ScaleType.CENTER,
            ScaleType.CENTER_CROP,
            ScaleType.CENTER_INSIDE
    };

    public BorderedCircleImageView(Context context) {
        super(context);
        mCircleRadius = DEFAULT_RADIUS;
        mBorderWidth = DEFAULT_BORDER;
        mBorderColor = DEFAULT_BORDER_COLOR;

        mCustomizedXmlParamsLoaded = true;
    }

    public BorderedCircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BorderedCircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LeCustomizedImageView,
                defStyle, 0);

        mCircleRadius = a
                .getDimensionPixelSize(R.styleable.LeCustomizedImageView_circle_radius, -1);
        mBorderWidth = a.getDimensionPixelSize(R.styleable.LeCustomizedImageView_border_width, -1);

        // don't allow negative values for radius and border
        if (mCircleRadius < 0) {
            mCircleRadius = DEFAULT_RADIUS;
        }
        if (mBorderWidth < 0) {
            mBorderWidth = DEFAULT_BORDER;
        }

        mBorderColor = a.getColor(R.styleable.LeCustomizedImageView_border_color,
                DEFAULT_BORDER_COLOR);

        mRoundBackground = a.getBoolean(R.styleable.LeCustomizedImageView_round_background, false);
        mRoundImage = a.getBoolean(R.styleable.LeCustomizedImageView_round_image, true);

        a.recycle();

        mCustomizedXmlParamsLoaded = true;

        if (mImageDrawable != null) {
            this.setImageDrawable(mImageDrawable);
        } else if (mImageBitmap != null) {
            this.setImageBitmap(mImageBitmap);
        }

        if (mBackgroundDrawable != null) {
            this.setBackground(mBackgroundDrawable);
        }

        if (mScaleType != null) {
            this.setScaleType(mScaleType);
        }
    }

    /**
     * Controls how the image should be resized or moved to match the size of
     * this ImageView.
     * 
     * @param scaleType The desired scaling mode. s
     * @attr ref android.R.styleable#ImageView_scaleType
     */
    @Override
    public void setScaleType(ScaleType scaleType) {
        if (!mCustomizedXmlParamsLoaded) {
            mScaleType = scaleType;
            return;
        }

        if (scaleType == null) {
            throw new NullPointerException();
        }

        mScaleType = scaleType;

        switch (scaleType) {
            case CENTER:
            case CENTER_CROP:
            case CENTER_INSIDE:
            case FIT_CENTER:
            case FIT_START:
            case FIT_END:
            case FIT_XY:
                if (mCustomizedXmlParamsLoaded && mRoundImage) {
                    super.setScaleType(ScaleType.FIT_XY);
                } else {
                    super.setScaleType(scaleType);
                }
                break;
            default:
                super.setScaleType(scaleType);
                break;
        }

        if (mImageDrawable instanceof BorderedCircleBitmapDrawable
                && ((BorderedCircleBitmapDrawable) mImageDrawable).getScaleType() != scaleType) {
            ((BorderedCircleBitmapDrawable) mImageDrawable).setScaleType(scaleType);
        }

        if (mBackgroundDrawable instanceof BorderedCircleBitmapDrawable
                && ((BorderedCircleBitmapDrawable) mBackgroundDrawable).getScaleType() != scaleType) {
            ((BorderedCircleBitmapDrawable) mBackgroundDrawable).setScaleType(scaleType);
        }
        setWillNotCacheDrawing(true);
        requestLayout();
        invalidate();
    }

    /**
     * Return the current scale type in use by this ImageView.
     *
     * @see android.widget.ImageView.ScaleType
     * @attr ref android.R.styleable#ImageView_scaleType
     */
    @Override
    public ScaleType getScaleType() {
        return mScaleType;
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        if (!mCustomizedXmlParamsLoaded) {
            mImageDrawable = drawable;
            mImageBitmap = null;
            return;
        }

        if (mRoundImage && drawable != null && mCircleRadius != 0) {
            mImageDrawable = BorderedCircleBitmapDrawable.fromDrawable(drawable, mCircleRadius,
                    mBorderWidth, mBorderColor, mScaleType);
        } else {
            mImageDrawable = drawable;
        }
        super.setImageDrawable(mImageDrawable);
    }

    @Override
    public void setImageResource(int resId) {
        setImageDrawable(getContext().getResources().getDrawable(resId));
    }

    public void setImageBitmap(Bitmap bm) {
        if (!mCustomizedXmlParamsLoaded) {
            mImageBitmap = bm;
            mImageDrawable = null;
            return;
        }

        if (mRoundImage && bm != null) {
            mImageDrawable = new BorderedCircleBitmapDrawable(bm, mCircleRadius, mBorderWidth,
                    mBorderColor, mScaleType);

        } else {
            mImageDrawable = null;
        }
        super.setImageDrawable(mImageDrawable);
    }

    @Override
    public void setBackground(Drawable background) {
        setBackgroundDrawable(background);
    }

    @Override
    @Deprecated
    public void setBackgroundDrawable(Drawable background) {
        if (!mCustomizedXmlParamsLoaded) {
            mBackgroundDrawable = background;
            return;
        }

        if (mRoundBackground && background != null) {
            if (background instanceof ColorDrawable) {
                int width = getWidth();
                int height = getHeight();
                
                if (width < getMeasuredWidth()) {
                    width = getMeasuredWidth();
                }
                
                if (height < getMeasuredHeight()) {
                    height = getMeasuredHeight();
                }
                
                if (getLayoutParams() != null) {
                    if (width < getLayoutParams().width) {
                        width = getLayoutParams().width;
                    }
                    
                    if (height < getLayoutParams().height) {
                        height = getLayoutParams().height;
                    }
                }
                
                BorderedCircleBitmapDrawable drawable =
                        new BorderedCircleBitmapDrawable((ColorDrawable) background, mCircleRadius,
                                mBorderWidth, mBorderColor,
                                width, height, mScaleType);
                mBackgroundDrawable = drawable;
            } else {
                mBackgroundDrawable = BorderedCircleBitmapDrawable.fromDrawable(background,
                        mCircleRadius, mBorderWidth, mBorderColor, mScaleType);
            }
        } else {
            mBackgroundDrawable = background;
        }
        super.setBackgroundDrawable(mBackgroundDrawable);
    }

    @Override
    public void setBackgroundColor(int color) {
        setBackground(new ColorDrawable(color));
    }

    @Override
    public void setBackgroundResource(int resid) {
        Drawable d = null;
        if (resid != 0) {
            d = getContext().getResources().getDrawable(resid);
        }
        setBackground(d);
    }

    public int getCircleRadius() {
        return mCircleRadius;
    }

    public int getBorder() {
        return mBorderWidth;
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setCircleRadius(int radius) {
        if (mCircleRadius == radius) {
            return;
        }

        this.mCircleRadius = radius;
        if (mImageDrawable instanceof BorderedCircleBitmapDrawable) {
            ((BorderedCircleBitmapDrawable) mImageDrawable).setCircleRadius(radius);
        }
        if (mRoundBackground && mBackgroundDrawable instanceof BorderedCircleBitmapDrawable) {
            ((BorderedCircleBitmapDrawable) mBackgroundDrawable).setCircleRadius(radius);
        }
    }

    public void setBorderWidth(int width) {
        if (mBorderWidth == width) {
            return;
        }

        this.mBorderWidth = width;
        if (mImageDrawable instanceof BorderedCircleBitmapDrawable) {
            ((BorderedCircleBitmapDrawable) mImageDrawable).setBorderWidth(width);
        }
        if (mRoundBackground && mBackgroundDrawable instanceof BorderedCircleBitmapDrawable) {
            ((BorderedCircleBitmapDrawable) mBackgroundDrawable).setBorderWidth(width);
        }
        invalidate();
    }

    public void setBorderColor(int color) {
        if (mBorderColor == color) {
            return;
        }

        this.mBorderColor = color;
        if (mImageDrawable instanceof BorderedCircleBitmapDrawable) {
            ((BorderedCircleBitmapDrawable) mImageDrawable).setBorderColor(color);
        }
        if (mRoundBackground && mBackgroundDrawable instanceof BorderedCircleBitmapDrawable) {
            ((BorderedCircleBitmapDrawable) mBackgroundDrawable).setBorderColor(color);
        }
        if (mBorderWidth > 0) {
            invalidate();
        }
    }

    public boolean isRoundBackground() {
        return mRoundBackground;
    }

    public void setRoundBackground(boolean roundBackground) {
        if (this.mRoundBackground == roundBackground) {
            return;
        }

        this.mRoundBackground = roundBackground;
        if (roundBackground) {
            if (mBackgroundDrawable instanceof BorderedCircleBitmapDrawable) {
                ((BorderedCircleBitmapDrawable) mBackgroundDrawable).setScaleType(mScaleType);
                ((BorderedCircleBitmapDrawable) mBackgroundDrawable).setCircleRadius(mCircleRadius);
                ((BorderedCircleBitmapDrawable) mBackgroundDrawable).setBorderWidth(mBorderWidth);
                ((BorderedCircleBitmapDrawable) mBackgroundDrawable).setBorderColor(mBorderColor);
            } else {
                setBackgroundDrawable(mBackgroundDrawable);
            }
        } else if (mBackgroundDrawable instanceof BorderedCircleBitmapDrawable) {
            ((BorderedCircleBitmapDrawable) mBackgroundDrawable).setBorderWidth(0);
            ((BorderedCircleBitmapDrawable) mBackgroundDrawable).setCircleRadius(0);
        }

        invalidate();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        boolean changed = false;

        if (width < 2 * mCircleRadius && widthMode == MeasureSpec.AT_MOST) {
            width = 2 * mCircleRadius;
            changed = true;
        }

        if (height < 2 * mCircleRadius && heightMode == MeasureSpec.AT_MOST) {
            height = 2 * mCircleRadius;
            changed = true;
        }

        if (changed) {
            // setMeasuredDimension(MeasureSpec.makeMeasureSpec(width,
            // widthMode),
            // MeasureSpec.makeMeasureSpec(height, heightMode));
            setMeasuredDimension(resolveSize(width, widthMeasureSpec),
                    resolveSize(height, heightMeasureSpec));
        }
    }
}
