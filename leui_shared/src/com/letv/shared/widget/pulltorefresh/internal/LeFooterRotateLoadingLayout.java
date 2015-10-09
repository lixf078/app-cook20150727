package com.letv.shared.widget.pulltorefresh.internal;

import android.content.Context;
import android.content.res.TypedArray;
import com.letv.shared.widget.pulltorefresh.PullToRefreshBase;
import android.view.animation.Animation;

/**
 * Created by liangchao on 14-11-13.
 */
public class LeFooterRotateLoadingLayout extends RotateLoadingLayout{
    public boolean cancelAnimator = false;
    public RefreshCompletedListener refreshCompletedListener = null;
    public void setRefreshCompletedListener(RefreshCompletedListener listener) {
        this.refreshCompletedListener = listener;
    }
    public LeFooterRotateLoadingLayout(Context context, PullToRefreshBase.Mode mode, PullToRefreshBase.Orientation scrollDirection, TypedArray attrs) {
        super(context, mode, scrollDirection, attrs);

    }
    @Override
    protected void refreshingImpl() {
        mRotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {/*
                Log.d("test","cancelAnimator---"+cancelAnimator + " this = " + this);*/

                if (cancelAnimator){
                    if (refreshCompletedListener != null) {
//                        Log.d("test","refreshCompletedListener != null");
                        refreshCompletedListener.refreshFooterCompleteInternal();
                    }
                }
            }
        });
        mHeaderImage.startAnimation(mRotateAnimation);
    }
}
