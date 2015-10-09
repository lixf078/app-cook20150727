package com.letv.shared.widget.pulltorefresh.internal;

import android.animation.*;
import android.graphics.*;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.widget.ImageView;
import com.letv.shared.widget.pulltorefresh.PullToRefreshBase;
import com.letv.shared.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.letv.shared.widget.pulltorefresh.PullToRefreshBase.Orientation;
import com.letv.shared.widget.LeArrowShape;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class LeRotateLoadingLayout extends RotateLoadingLayout {
    public static enum ColorStyle{
        WHITE,COLOR;
    }

    private static final int WHITE = Color.WHITE;
    private static final int BALLNUM = 6;

    public int[] getBallColor() {
        return ballColor;
    }

    public void setBallColor(int[] ballColor) {
        if(ballColor==null||ballColor.length!=BALLNUM){
            return;
        }
        this.ballColor = ballColor;
    }
    public void setBallColor(int color,int pos) {
        if(pos<0||pos>BALLNUM){
            return;
        }
        this.ballColor[pos] = color;
    }

    public void setColorStyle(ColorStyle colorStyle) {
        this.colorStyle = colorStyle;
    }

    public ColorStyle getColorStyle() {
        return colorStyle;
    }

    private ColorStyle colorStyle = ColorStyle.COLOR;

    private int[] ballColor = new int[]{0xffed1e20,0xff6c24c6,0xff1ab1eb,
            0xff8ad127,0xffffd800,0xffff8a00};
    private float ballRadius;
    private static final float BALL_RADIUS_MAX = 10f;
    private List<PointF> ballCenters;
    private List<PointF> relativeCenters;
    private static final float BALL_RADIUS_FACTOR = 1.2f;

    public boolean cancelAnimator = false;
    public RefreshCompletedListener refreshCompletedListener = null;
    private int duration;
    private static final int DURATION_ROTATE = 500;
    private static final float START_ANGLE = 270f;
    private static final float ANGLE_OFFSET = -90f;
    private static final float ANGLE_MAX_SWEEP = -330f;
    private static final int DURATION_ARROW = 350;
    private static final float CIRCLE_PAINT_WIDTH = 6f;
    private static final float ARROW_PAINT_WIDTH = 2f;
    private static final float BOXSIZE_COEF = 2.2f;
    private static final int HEADERIMAGE_SIZE = 100;
    private static final int DISMISS_DURATION = 250;
    private Paint mPaint;
    private boolean mUseCenter = false;
    private float startAngle = START_ANGLE;
    private Matrix mHeaderImageMatrix = null;
    private boolean isReleaseToRefresh = false;
    private boolean isPullToRefresh = true;
    private Bitmap mBitmap = null;
    private Canvas mCanvas = null;
    private int mWidth;
    private int mHeight;
    private RectF mRectF;
    private float mAngle;
    private AnimatorHolder holder;
    private static final float REFRESH_STOP_ANGLE = 0f;

    private LeArrowShape arrowShape = null;
    private float radius;
    private int paintColor = Color.WHITE;
    private boolean isRefreshing = false;

    public void setRefreshCompletedListener(RefreshCompletedListener listener) {
        this.refreshCompletedListener = listener;
    }
    public void setPaintColor(int paintColor) {
        this.paintColor = paintColor;
        if (mPaint!=null){
            mPaint.setColor(paintColor);
        }
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
    private void setImageViewLayoutParams(){
        ViewGroup.LayoutParams params = mHeaderImage.getLayoutParams();
        params.height = HEADERIMAGE_SIZE;
        params.width = HEADERIMAGE_SIZE;
        mHeaderImage.setLayoutParams(params);
    }

    public LeRotateLoadingLayout(Context context, Mode mode, Orientation scrollDirection, TypedArray attrs, int paintColor) {
        this(context, mode, scrollDirection, attrs);
        this.paintColor = paintColor;
        mPaint.setColor(paintColor);
//        setImageViewLayoutParams();
    }

    public LeRotateLoadingLayout(Context context, Mode mode,
            Orientation scrollDirection, TypedArray attrs) {
        super(context, mode, scrollDirection, attrs);


        duration = DURATION_ROTATE;
        
        mPaint = new Paint();
        mPaint.setColor(paintColor);
//        mPaint.setStrokeWidth(CIRCLE_PAINT_WIDTH);
//        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);
//        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mHeaderImage.setScaleType(ImageView.ScaleType.MATRIX);
        mHeaderImageMatrix = new Matrix();
        mHeaderImage.setImageMatrix(mHeaderImageMatrix);
        holder = new AnimatorHolder();
        setImageViewLayoutParams();
        initVariable();
    }
    private void initVariable(){
        mWidth = HEADERIMAGE_SIZE;
        mHeight = mWidth;
        radius = mWidth>=mHeight?mHeight/BOXSIZE_COEF:mWidth/BOXSIZE_COEF;
        if (mBitmap==null&&mWidth>0&&mHeight>0){

            mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            if (mCanvas==null){
                mCanvas = new Canvas(mBitmap);
                mRectF = new RectF((mWidth-2*radius)/2,(mHeight-2*radius)/2,mWidth/2+radius,mHeight/2+radius);
            }
            if (arrowShape==null){
                arrowShape = new LeArrowShape(mWidth,false,ARROW_PAINT_WIDTH);
            }
        }
        ballCenters = new ArrayList<PointF>();
        relativeCenters = new ArrayList<PointF>();
        float angleUnit = 360f/BALLNUM;
        float drawRadius = radius-BALL_RADIUS_MAX;
        for (int i = 0;i<BALLNUM;i++){
            PointF pointF = new PointF();
            pointF.set((float) (HEADERIMAGE_SIZE / 2 + drawRadius * Math.sin(i * angleUnit * Math.PI / 180)),
                    (float) (HEADERIMAGE_SIZE / 2 - drawRadius * Math.cos(i * angleUnit * Math.PI / 180)));
            ballCenters.add(pointF);
            relativeCenters.add(new PointF((float)(drawRadius * Math.sin(i * angleUnit * Math.PI / 180)),
                    (float)(drawRadius * Math.cos(i * angleUnit * Math.PI / 180))));
        }

    }


    /*@Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = mHeaderImage.getWidth();
        mHeight = mHeaderImage.getHeight();
        radius = mWidth>=mHeight?mHeight/BOXSIZE_COEF:mWidth/BOXSIZE_COEF;
        if (mBitmap==null&&mWidth>0&&mHeight>0){

            mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            if (mCanvas==null){
                mCanvas = new Canvas(mBitmap);
                mRectF = new RectF((mWidth-2*radius)/2,(mHeight-2*radius)/2,mWidth/2+radius,mHeight/2+radius);
            }
            if (arrowShape==null){
                arrowShape = new LeArrowShape(mWidth,false,ARROW_PAINT_WIDTH);
            }
        }
    }*/

    @Override
    protected void onPullImpl(float scaleOfLayout) {
            float scale = scaleOfLayout;
            if (isPullToRefresh) {
                mBitmap.eraseColor(0x00FFFFFF);
                mCanvas.drawColor(0x00FFFFFF);
                if (scale > (1+ PullToRefreshBase.ANIMATION_ENTER_OFFSET)) {
                    scale = 1+ PullToRefreshBase.ANIMATION_ENTER_OFFSET;
                }
                scale = (scale - PullToRefreshBase.ANIMATION_ENTER_OFFSET) * BALLNUM;

                if(scale>0){

                    int scale_int = (int) Math.floor(scale);
                    if (scale_int == 6) {
                        scale_int = 5;
                    }
                    float scale_offset = scale - scale_int;
                    for (int i = 0; i < scale_int; i++) {
                        if(colorStyle==ColorStyle.WHITE){
                            mPaint.setColor(WHITE);
                        }else{
                            mPaint.setColor(ballColor[i]);
                        }

                        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                        mCanvas.drawCircle(ballCenters.get(i).x, ballCenters.get(i).y,
                                BALL_RADIUS_MAX / BALL_RADIUS_FACTOR, mPaint);
                    }
                    float temp = 1 / BALL_RADIUS_FACTOR;
                    if (scale_offset != 0) {
                        if(colorStyle==ColorStyle.WHITE){
                            mPaint.setColor(WHITE);
                        }else{
                            mPaint.setColor(ballColor[scale_int]);
                        }

                        if (scale_offset < temp) {
                            ballRadius = BALL_RADIUS_FACTOR * scale_offset * BALL_RADIUS_MAX;
                        } else {
                            ballRadius = (BALL_RADIUS_FACTOR * scale_offset - 2 * BALL_RADIUS_FACTOR * (scale_offset - temp)) * BALL_RADIUS_MAX;
                        }
                        mCanvas.drawCircle(ballCenters.get(scale_int).x, ballCenters.get(scale_int).y, ballRadius, mPaint);

                    }

                    mHeaderImage.setImageBitmap(mBitmap);
                }else{
                    mHeaderImage.setImageBitmap(mBitmap);
                }
            /*mBitmap.eraseColor(0x00FFFFFF);
            mCanvas.drawColor(0x00FFFFFF);
            mPaint.setColor(paintColor);
            mPaint.setStrokeWidth(CIRCLE_PAINT_WIDTH);
            float sweepAngle = ANGLE_OFFSET+(ANGLE_MAX_SWEEP-ANGLE_OFFSET)*scaleOfLayout;
            mCanvas.drawArc(mRectF, startAngle, sweepAngle, mUseCenter, mPaint);
            mHeaderImage.setImageBitmap(mBitmap);*/
                if (isRefreshing == false && animator != null) {
                    if (!animatorCanbeStarted) {
                        cancelAnimator = true;
                    }
                }
            }
            if (isReleaseToRefresh) {
//            mAngle = -(scaleOfLayout-2)*90f;
//            mHeaderImageMatrix.setRotate(mAngle,mWidth/2f,mHeight/2f);
//            mHeaderImage.setImageMatrix(mHeaderImageMatrix);
                startRotation();
            }



    }
    private void initAnimator(){
        animator.setInterpolator(ANIMATION_INTERPOLATOR);
        animator.setDuration(DURATION_ROTATE);
        animator.setRepeatMode(Animation.RESTART);
        animator.setRepeatCount(Animation.INFINITE);

        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animatorCanbeStarted = true;
                cancelAnimator = false;
                if(isRefreshing){
                    isRefreshing = false;

                    //
                    startDismiss();
                    //
//                    resetImageHeader();
//                    if (refreshCompletedListener != null) {
//                        refreshCompletedListener.refreshHeaderCompleteInternal();
//                    }

                }
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (cancelAnimator&&Math.abs(holder.getAngle()-REFRESH_STOP_ANGLE)<10){
                    holder.setAngle(REFRESH_STOP_ANGLE);
                    stopAngle = holder.getAngle();
                    animator.cancel();
                }

                mHeaderImageMatrix.setRotate(holder.getAngle(), mWidth / 2f, mHeight / 2f);
                mHeaderImage.setImageMatrix(mHeaderImageMatrix);
            }

        });

    }
    private void startDismiss(){
        if(mCanvas!=null){
            ValueAnimator dismissAnimator = ValueAnimator.ofFloat(1f,0f);
            dismissAnimator.setInterpolator(new AnticipateInterpolator());
            dismissAnimator.setDuration(DISMISS_DURATION);
            dismissAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Float value = (Float) (animation.getAnimatedValue());
                    mBitmap.eraseColor(0x00FFFFFF);
                    mCanvas.drawColor(0x00FFFFFF);
                    float radius = value * BALL_RADIUS_MAX / BALL_RADIUS_FACTOR;
                    for (int i = 0; i < BALLNUM; i++) {
                        if (colorStyle == ColorStyle.WHITE) {
                            mPaint.setColor(WHITE);
                        } else {
                            mPaint.setColor(ballColor[i]);
                        }
                        mCanvas.drawCircle(HEADERIMAGE_SIZE / 2 + relativeCenters.get(i).x * value,
                                HEADERIMAGE_SIZE / 2 - relativeCenters.get(i).y * value,
                                radius, mPaint);
                    }
                    mHeaderImage.setImageBitmap(mBitmap);
                }
            });
            dismissAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    resetImageHeader();
                    if (refreshCompletedListener != null) {
                        refreshCompletedListener.refreshHeaderCompleteInternal();
                    }
                }
            });
            dismissAnimator.start();

        }else{
            resetImageHeader();
            if (refreshCompletedListener != null) {
                refreshCompletedListener.refreshHeaderCompleteInternal();
            }
        }
    }
    private void startRotation() {
        if (animatorCanbeStarted) {
            mAngle = 0;
            if (animator==null){
                animator = ObjectAnimator.ofFloat(holder, "angle", mAngle, mAngle + 360);
            }
            initAnimator();

            animatorCanbeStarted = false;
            animator.start();

        }
    }
    @Override
    protected void pullToRefreshImpl() {
        isPullToRefresh = true;
        isReleaseToRefresh = false;
        if (mHeaderImageMatrix!=null&&!mHeaderImageMatrix.isIdentity()){
            mHeaderImageMatrix.reset();
            mHeaderImage.setImageMatrix(mHeaderImageMatrix);
        }
    }
    public ObjectAnimator getAnimator() {
        return animator;
    }
    private ObjectAnimator animator;
    private float stopAngle;

    private void drawBalls(){
        mBitmap.eraseColor(0x00FFFFFF);
        mCanvas.drawColor(0x00FFFFFF);

        for (int i = 0;i<BALLNUM;i++){
            if(colorStyle==ColorStyle.WHITE){
                mPaint.setColor(WHITE);
            }else{
                mPaint.setColor(ballColor[i]);
            }

            mCanvas.drawCircle(ballCenters.get(i).x,ballCenters.get(i).y,
                    BALL_RADIUS_MAX/BALL_RADIUS_FACTOR,mPaint);
        }
        mHeaderImage.setImageBitmap(mBitmap);
        mAngle = 0;
    }
    public void forceRefreshingImpl(){
        drawBalls();

        /*mPaint.setColor(paintColor);
        mPaint.setStrokeWidth(CIRCLE_PAINT_WIDTH);
        float sweepAngle = ANGLE_OFFSET+(ANGLE_MAX_SWEEP-ANGLE_OFFSET);
        mCanvas.drawArc(mRectF, startAngle, sweepAngle, mUseCenter, mPaint);
        mHeaderImage.setImageBitmap(mBitmap);
        mAngle = 0;*/
        refreshingImpl();

    }
    @Override
    protected void refreshingImpl() {
        isRefreshing = true;
        startRotation();
        /*if (animatorCanbeStarted){
            animatorCanbeStarted = false;
            startRotation();

        }*/

        /*animator = ObjectAnimator.ofFloat(holder,"angle",mAngle,mAngle+360);
        animator.setInterpolator(ANIMATION_INTERPOLATOR);
        animator.setDuration(DURATION_ROTATE);
        animator.setRepeatMode(Animation.RESTART);
        animator.setRepeatCount(Animation.INFINITE);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

//                resetImageHeader();
//                if (refreshCompletedListener != null) {
//                    refreshCompletedListener.refreshHeaderCompleteInternal();
//                }
                if(mCanvas!=null){
                    mCanvas.drawCircle(mWidth/2f,mHeight/2f,radius,mPaint);
                    drawLeArrow();
                }
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (cancelAnimator&&Math.abs(holder.getAngle()-REFRESH_STOP_ANGLE)<10){
                    holder.setAngle(REFRESH_STOP_ANGLE);
                    stopAngle = holder.getAngle();
                    animator.cancel();
                }

                mHeaderImageMatrix.setRotate(holder.getAngle(), mWidth / 2f, mHeight / 2f);
                mHeaderImage.setImageMatrix(mHeaderImageMatrix);
            }

        });

        if (animatroStarted){
            animatroStarted = false;
            animator.start();

        }*/


    }
    private void drawLeArrow() {
        final AnimatorHolder arrowHolder = new AnimatorHolder();
        arrowHolder.setAngle(0f);

        ObjectAnimator drawArrowAnimator = ObjectAnimator.ofFloat(arrowHolder,"angle",0f,1f);
        drawArrowAnimator.setDuration(DURATION_ARROW);
        drawArrowAnimator.setInterpolator(ANIMATION_INTERPOLATOR);

        mPaint.setAntiAlias(true);
        mPaint.setColor(paintColor);
        drawArrowAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
//                endRefreshing = true;
                resetImageHeader();
                if (refreshCompletedListener != null) {
                    refreshCompletedListener.refreshHeaderCompleteInternal();
                }
            }
        });
        drawArrowAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (arrowShape != null) {
                    mCanvas.save();
                    mCanvas.rotate(-stopAngle, mWidth / 2f, mHeight / 2f);
                    arrowShape.draw(mCanvas, mPaint, arrowHolder.getAngle());
                    mHeaderImage.setImageBitmap(mBitmap);
                    mCanvas.restore();
                }
            }
        });
        drawArrowAnimator.start();

    }

    private boolean animatorCanbeStarted = true;
    @Override
    protected void resetImpl() {


        if (animator!=null&&animator.isRunning()){
            animator.cancel();

        }
    }
    public void resetImageHeader(){
        isPullToRefresh = true;
        isReleaseToRefresh = false;
        animatorCanbeStarted = true;
        mAngle = 0;

    }

    @Override
    protected void releaseToRefreshImpl() {
        isReleaseToRefresh = true;
        isPullToRefresh = false;
    }



    private class AnimatorHolder {
        private float angle;
        public float getAngle() {
            return angle;
        }

        public void setAngle(float angle) {
            this.angle = angle;
        }
    }


}
