package com.letv.shared.widget;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.letv.shared.R;

public class WifiConnectView extends LinearLayout {

    public WifiConnectView(Context context) {
        this(context, null);
    }
    public WifiConnectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public WifiConnectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void startAnimation(boolean hasConnect) {
        View view = new View(getContext());
        if(hasConnect) {
//            view.setBackgroundResource(R.drawable.animated_has_wifi_connect);
        } else {
//            view.setBackgroundResource(R.drawable.animated_has_no_wifi_connect);
        }
        addView(view);
        Animatable animatable = (AnimatedVectorDrawable) view.getBackground();
        if(null != animatable) {
            animatable.start();
        }
    }

    public void cancelAnimation() {
        View view = getChildAt(0);
        if(null != view) {
            Animatable animatable = (Animatable) view.getBackground();
            if (null != animatable) {
                animatable.stop();
            }
        }
        removeAllViews();
        invalidate();
    }

}
