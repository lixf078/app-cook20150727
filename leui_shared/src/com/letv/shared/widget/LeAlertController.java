package com.letv.shared.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

/**
 * Created by liangchao on 15-3-16.
 */
public class LeAlertController {
    private final Context mContext;
    private final Window mWindow;

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    private View contentView;


    public LeAlertController(Context context, Window window) {
        mContext = context;
        mWindow = window;

    }

    public void installContent() {
        /* We use a custom title so never request a window title */
        mWindow.requestFeature(Window.FEATURE_NO_TITLE);
        mWindow.setGravity(Gravity.BOTTOM);
        mWindow.setContentView(contentView);

    }


}
