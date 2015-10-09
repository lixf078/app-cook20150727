package com.letv.shared.widget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.letv.shared.R;
/**
 * Created by liangchao on 15-2-2.
 */
public class LeTopSlideToastHelper {
    public static final int LENGTH_LONG = 3500;
    public static final int LENGTH_SHORT = 2000;
    public static final int TOAST_HEIGTH_DP_HIGH = 72;
    public static final int TOAST_HEIGTH_DP_LOW = 64;
    private static final float CONTENT_TEXT_SINGlELINE_WIDTH = 0.7f;
    private static final float CONTENT_TEXT_DOUBLELINE_WIDTH = 0.8f;
    private static int screenWidth;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    private View toastView;
    private Context mContext;
    private Handler mHandler;
    private int duration = 0;
    private int animStyleId = android.R.style.Animation_Toast;
    private static float density;

    public void setCallback(LeTopSlideToastCallback callback) {
        this.callback = callback;
    }

    private LeTopSlideToastCallback callback;

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            removeView();
        }
    };

    private LeTopSlideToastHelper(Context context) {
        // Notice: we should get application context
        // otherwise we will get error
        // "Activity has leaked window that was originally added"
        Context ctx = context.getApplicationContext();
        if (ctx == null) {
            ctx = context;
        }
        this.mContext = ctx;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        density = mContext.getResources().getDisplayMetrics().density;
        screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
        init();
    }

    private void init() {
        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowParams.alpha = 1.0f;
        mWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mWindowParams.height = dip2px(TOAST_HEIGTH_DP_HIGH);
        mWindowParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mWindowParams.setTitle("ToastHelper");
        mWindowParams.packageName = mContext.getPackageName();
        mWindowParams.windowAnimations = animStyleId;
    }

    public void show() {
        removeView();
        if (toastView == null) {
            return;
        }
        mWindowParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        //zhangyd remove for port
/*        if (mShowWhenLocked)
            mWindowParams.leuiFlags |= WindowManager.LayoutParams.LEUI_FLAG_TOAST_SHOW_WHEN_LOCKED;
        else
            mWindowParams.leuiFlags &= ~WindowManager.LayoutParams.LEUI_FLAG_TOAST_SHOW_WHEN_LOCKED;*/

        toastView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        mWindowManager.addView(toastView, mWindowParams);
        if (mHandler == null) {
            mHandler = new Handler();
        }
        mHandler.postDelayed(timerRunnable, duration);
        if (callback != null) {
            callback.onShow();
        }
    }

    public void removeView() {
        if (toastView != null && toastView.getParent() != null) {
            mWindowManager.removeView(toastView);
            mHandler.removeCallbacks(timerRunnable);
            if (callback != null) {
                callback.onDismiss();
            }
        }
    }

    /**
     * @param context
     * @param duration
     * @return
     */
    public enum ContentTextAlign{
        LEFT, CENTER;
    }

    public static LeTopSlideToastHelper getToastHelper(
            Context context, int duration,
            String content, Drawable drawable,
            String btn_text, View.OnClickListener listener,
            LeTopSlideToastCallback callback, ContentTextAlign contentAlign) {
        if (content == null) {
            return null;
        }
        LeTopSlideToastHelper helper = new LeTopSlideToastHelper(context);
        View toast = LayoutInflater.from(context).inflate(R.layout.le_topslide_toast, null);
        ImageView toast_img = (ImageView) toast.findViewById(R.id.le_topslide_toast_img);
        TextView toast_text = (TextView) toast.findViewById(R.id.le_topslide_toast_text);
        TextView toast_btn = (TextView) toast.findViewById(R.id.le_topslide_toast_btn);
        ImageView toast_divider = (ImageView) toast.findViewById(R.id.le_topslide_toast_divider);
        if (drawable == null) {
            toast_img.setVisibility(View.GONE);
        } else {
            toast_img.setImageDrawable(drawable);
        }

        /* 对toast长度进行限制 */
        Paint paint = new Paint();
        float TextWidth = paint.measureText(content);

        if (TextWidth > screenWidth * CONTENT_TEXT_SINGlELINE_WIDTH) {
            toast_text.setMaxWidth((int) (screenWidth * CONTENT_TEXT_DOUBLELINE_WIDTH));
        } else {
            toast_text.setMaxWidth((int) (screenWidth * CONTENT_TEXT_SINGlELINE_WIDTH));
        }

        toast_text.setText(content);

        if (contentAlign == ContentTextAlign.CENTER) {
            toast_text.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        if (btn_text == null) {
            toast_divider.setVisibility(View.GONE);
            toast_btn.setVisibility(View.GONE);
        } else {
            toast_btn.setText(btn_text);
            if (listener != null) {
                toast_btn.setOnClickListener(listener);
            }
        }
        helper.setCallback(callback);
        helper.setView(toast);
        helper.setDuration(duration);
        helper.setAnimation(R.style.leTopSlideToast);
        return helper;
    }

    public static LeTopSlideToastHelper getToastHelper(
            Context context, int duration,
            String content, Drawable drawable,
            String btn_text, View.OnClickListener listener,
            LeTopSlideToastCallback callback) {
        return getToastHelper(context, duration, content, drawable, btn_text, listener, callback, ContentTextAlign.LEFT);
    }

    public static LeTopSlideToastHelper getToastHelper(
            Context context, int duration, View view, LeTopSlideToastCallback callback) {
        if (view == null || context == null) {
            return null;
        }
        LeTopSlideToastHelper helper = new LeTopSlideToastHelper(context);
        helper.setCallback(callback);
        helper.setView(view);
        helper.setDuration(duration);
        helper.setAnimation(R.style.leTopSlideToast);
        return helper;
    }

    public LeTopSlideToastHelper setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public LeTopSlideToastHelper setAnimation(int animStyleId) {
        this.animStyleId = animStyleId;
        mWindowParams.windowAnimations = this.animStyleId;
        return this;
    }

    /**
     * set toast by dip
     *
     * @param heightDip
     * @return LeTopSlideToastHelper
     */
    public LeTopSlideToastHelper setToastHeight(int heightDip) {
        mWindowParams.height = dip2px(heightDip);
        return this;
    }

    /**
     * custom view
     *
     * @param view
     */
    public LeTopSlideToastHelper setView(View view) {
        this.toastView = view;
        return this;
    }

    private boolean mShowWhenLocked;

    /**
     * @param showWhenLocked
     * @hide
     */
    public LeTopSlideToastHelper setShowWhenLocked(boolean showWhenLocked) {
        mShowWhenLocked = showWhenLocked;
        return this;
    }

    private static int dip2px(float dp) {
        return (int) (dp * density + 0.5f);
    }
}
