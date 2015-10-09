package com.letv.shared.widget;

import com.letv.shared.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LeBottomWidget extends BlurLinearLayout implements OnClickListener, OnLongClickListener {

    private static final String TAG = "LeBottomWidget";
    public static int MODE_ICON_ONLY = 1;
    public static int MODE_ICON_TEXT = 2;

    protected static int MIN_TAB_CNT = 1;
    protected static int MAX_TAB_CNT = 5;

    protected int mMode = -1;
    protected int mTabCnt = -1;
    protected int mLayoutResId = -1;
    protected ArrayList<String> mTags = new ArrayList<String>();

    protected Drawable mTopStrip = null;
    protected int mTopStripHeight = 0;

    protected OnClickAndLongClickListener mListener;

    public static final int MSG_SET_MODE_AND_TAB_COUNT = 0;
    public static final int MSG_ADD_TAB = 1;

    public static Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            TabInfo tabInfo = (TabInfo) msg.obj;
            if (tabInfo == null)
                return;

            LeBottomWidget bw = tabInfo.bw.get();
            if (bw == null)
                return;

            switch (msg.what) {
                case MSG_SET_MODE_AND_TAB_COUNT:
                    bw.setModeAndTabCount(msg.arg1, msg.arg2);
                    break;

                case MSG_ADD_TAB:
                    bw.addTab(tabInfo.sequence, tabInfo.tag, tabInfo.enabledIconId,
                            tabInfo.disabledIconId, tabInfo.title);
                    break;
            }
        }
    };

    public LeBottomWidget(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        this.setBlurEnabled(false);
    }

    public LeBottomWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setBlurEnabled(false);
    }

    public LeBottomWidget(Context context) {
        super(context);
        this.setBlurEnabled(false);
    }

    public void setTopStripDrawable(int resId) {
        setTopStripDrawable(getContext().getResources().getDrawable(resId));
    }

    public void setTopStripDrawable(Drawable drawable) {
        mTopStrip = drawable;
        // requestLayout();
        invalidate();
    }

    public void setTopStripHeight(int height) {
        mTopStripHeight = height;
        invalidate();
    }

    /*
     * mode: LeBottomWidget.MODE_ICON_ONLY or LeBottomWidget.MODE_ICON_TEXT
     * tabCnt: 1-5
     */
    public void setModeAndTabCount(int mode, int tabCnt) {
        if (mode != MODE_ICON_ONLY && mode != MODE_ICON_TEXT) {
            throw new IllegalArgumentException("mode must be MODE_ICON_ONLY or MODE_ICON_TEXT");
        }

        if (tabCnt < MIN_TAB_CNT || tabCnt > MAX_TAB_CNT) {
            throw new IllegalArgumentException("tabCnt must be between 1 and 5");
        }

        if (mMode == mode && mTabCnt == tabCnt)
            return;

        setEnableAnimation(false);
        clear();

        mMode = mode;
        mTabCnt = tabCnt;

        for (int i = 0; i < tabCnt; i++) {
            mTags.add(new String(""));
        }

        if (mMode == MODE_ICON_ONLY) {
            switch (mTabCnt) {
                case 1:
                    mLayoutResId = R.layout.le_bottom_widget_1_tab_with_icon_only;
                    break;
                case 2:
                    mLayoutResId = R.layout.le_bottom_widget_2_tabs_with_icon_only;
                    break;
                case 3:
                    mLayoutResId = R.layout.le_bottom_widget_3_tabs_with_icon_only;
                    break;
                case 4:
                    mLayoutResId = R.layout.le_bottom_widget_4_tabs_with_icon_only;
                    break;
                case 5:
                    mLayoutResId = R.layout.le_bottom_widget_5_tabs_with_icon_only;
                    break;
            }

        } else {
            switch (mTabCnt) {
                case 1:
                    mLayoutResId = R.layout.le_bottom_widget_1_tab_with_icon_title;
                    break;
                case 2:
                    mLayoutResId = R.layout.le_bottom_widget_2_tabs_with_icon_title;
                    break;
                case 3:
                    mLayoutResId = R.layout.le_bottom_widget_3_tabs_with_icon_title;
                    break;
                case 4:
                    mLayoutResId = R.layout.le_bottom_widget_4_tabs_with_icon_title;
                    break;
                case 5:
                    mLayoutResId = R.layout.le_bottom_widget_5_tabs_with_icon_title;
                    break;
            }
        }

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View bw = inflater.inflate(mLayoutResId, this);
        if (bw != null && bw instanceof ViewGroup) {
            ((ViewGroup) bw).setClipChildren(false);
        }

        layoutTabs();

        setTabListener();
        setEnableAnimation(true);
    }

    public void setModeAndTabCountAsync(int mode, int tabCnt) {
        TabInfo tabInfo = new TabInfo();
        tabInfo.bw = new WeakReference<LeBottomWidget>(this);
        Message msg = mHandler.obtainMessage(MSG_SET_MODE_AND_TAB_COUNT, mode, tabCnt, tabInfo);
        msg.sendToTarget();
    }

    public void addTab(int sequence, String tag, View v, RelativeLayout.LayoutParams params) {
        if (sequence < 0 || sequence >= mTabCnt) {
            throw new IllegalArgumentException("sequence must be between 0 and mTabCnt-1");
        }

        if (mMode != MODE_ICON_ONLY && mMode != MODE_ICON_TEXT) {
            throw new IllegalArgumentException("please set mMode at first");
        }

        if (v == null || v.getParent() != null) {
            throw new IllegalArgumentException("v is null or has a parent");
        }

        if (tag != null && !tag.isEmpty()) {
            mTags.set(sequence, tag);
        }

        RelativeLayout vg = (RelativeLayout) getTabView(sequence);
        vg.removeAllViews();

        if (params == null) {
            vg.addView(v);
        } else {
            vg.addView(v, params);
        }
    }

    public void addTab(int sequence, int enabledIconId, String title) {
        addTab(sequence, null, enabledIconId, -1, title);
    }

    public void addTab(int sequence, String tag, int enabledIconId, int disabledIconId, String title) {
        if (sequence < 0 || sequence >= mTabCnt) {
            throw new IllegalArgumentException("sequence must be between 0 and mTabCnt-1");
        }

        if (mMode != MODE_ICON_ONLY && mMode != MODE_ICON_TEXT) {
            throw new IllegalArgumentException("please set mMode at first");
        }

        if (enabledIconId < 0) {
            throw new IllegalArgumentException("enabledIconId must be positive");
        }

        if (tag != null && !tag.isEmpty()) {
            mTags.set(sequence, tag);
        }

        if (mMode == MODE_ICON_ONLY) {
            addIcon(sequence, enabledIconId, disabledIconId);
        } else if (mMode == MODE_ICON_TEXT) {
            addIconTitle(sequence, enabledIconId, disabledIconId, title);
        }
    }

    private class TabInfo {
        public WeakReference<LeBottomWidget> bw;

        public int sequence;
        public String tag;
        public int enabledIconId;
        public int disabledIconId;
        public String title;

        public TabInfo() {
        }
    }

    public void addTabAsync(int sequence, String tag, int enabledIconId, int disabledIconId,
            String title) {
        TabInfo tabInfo = new TabInfo();
        tabInfo.bw = new WeakReference<LeBottomWidget>(this);
        tabInfo.sequence = sequence;
        tabInfo.tag = tag;
        tabInfo.enabledIconId = enabledIconId;
        tabInfo.disabledIconId = disabledIconId;
        tabInfo.title = title;

        Message msg = mHandler.obtainMessage(MSG_ADD_TAB, tabInfo);
        msg.sendToTarget();
    }

    private void addIcon(int sequence, int enabledIconId, int disabledIconId) {
        ImageView img = getTabIcon(sequence);
        if (img == null)
            return;

        if (disabledIconId < 0) {
            img.setImageDrawable(getContext().getResources().getDrawable(enabledIconId));
        } else {
            img.setImageDrawable(this.getResources().getDrawable(enabledIconId));
        }
    }

    private void addIconTitle(int sequence, int enabledIconId, int disabledIconId, String title) {
        ImageView img = getTabIcon(sequence);
        TextView tv = getTabTitle(sequence);

        if (img != null) {
            if (disabledIconId < 0) {
                img.setImageDrawable(getContext().getResources().getDrawable(enabledIconId));

            } else {
                img.setImageDrawable(this.getResources().getDrawable(enabledIconId));
            }
        }

        if (tv != null && title != null && !title.isEmpty()) {
            tv.setText(title);
            tv.setTextColor(this.getResources().getColor(R.color.le_bottom_tab_title_textcolor));
        }
    }


    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        int left = getLeft();
        int top = 0;
        int right = getRight();
        int bottom = mTopStripHeight;

        final Drawable topStrip = mTopStrip;
        if (topStrip != null && mTopStripHeight != 0) {
            topStrip.setBounds(left, top, right, bottom);
            topStrip.draw(canvas);
        }
    }

    private int getChildPos(View v) {
        for (int pos = 0; pos < mTabCnt; pos++) {
            if (v == getTabView(pos))
                return pos;
        }

        return -1;
    }

    @Override
    public void onClick(View view) {
        if (mListener != null) {
            int pos = getChildPos(view);
            if (pos >= 0)
                mListener.onClick(pos, mTags.get(pos));
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (mListener != null) {
            int pos = getChildPos(view);
            if (pos >= 0)
                return mListener.onLongClick(pos, mTags.get(pos));
        }

        return false;
    }

    public void setOnClickAndLongClickListener(OnClickAndLongClickListener l) {
        mListener = l;
    }

    private boolean isValid() {
        if (mMode != MODE_ICON_ONLY && mMode != MODE_ICON_TEXT) {
            return false;
        }

        if (mTabCnt < MIN_TAB_CNT || mTabCnt > MAX_TAB_CNT) {
            return false;
        }

        return true;
    }

    public ImageView getTabIcon(int pos) {
        View tab = getTabView(pos);
        if (tab == null)
            return null;

        ImageView img = (ImageView) tab.findViewById(R.id.icon);
        return img;
    }

    public ImageView getTabIcon(String tag) {
        View tab = getTabView(tag);
        if (tab == null)
            return null;

        ImageView img = (ImageView) tab.findViewById(R.id.icon);
        return img;
    }

    public TextView getTabTitle(int pos) {
        View tab = getTabView(pos);
        if (tab == null)
            return null;

        TextView tv = (TextView) tab.findViewById(R.id.title);
        return tv;
    }

    public TextView getTabTitle(String tag) {
        View tab = getTabView(tag);
        if (tab == null)
            return null;

        TextView tv = (TextView) tab.findViewById(R.id.title);
        return tv;
    }

    public void setTitleTextColor(int color) {
        for (int i = 0; i < mTabCnt; i++) {
            TextView tv = getTabTitle(i);
            if (tv != null)
                tv.setTextColor(color);
        }
    }

    /* return a RelativeLayout object
     * if mode is MODE_ICON_ONLY, RelativeLayout object contains a icon
     * if mode is MODE_ICON_TEXT, RelativeLayout object contains a icon and a text view
     */
    public View getTabView(int pos) {
        if (!isValid())
            return null;

        if (pos < 0 || pos >= mTabCnt)
            return null;

        return this.getChildAt(pos);
    }

    public View getTabView(String tag) {
        if (!isValid())
            return null;

        int pos = getTabPos(tag);
        if (pos < 0)
            return null;

        return getTabView(pos);
    }

    private int getTabPos(String tag) {
        int pos = -1;
        for (int i = 0; i < mTabCnt; i++) {
            if (tag.equals(mTags.get(i))) {
                pos = i;
                break;
            }
        }
        return pos;
    }


    public void setEnable(int pos, boolean enabled) {
    	View tab = getTabView(pos);
    	if (tab == null)
    		return;
    	
    	
        if (mMode == MODE_ICON_ONLY || mMode == MODE_ICON_TEXT) {
            ViewGroup vg = (ViewGroup) tab;

            float alpha = 0.0f;
            if (enabled) {
                alpha = this.getResources().getInteger(R.integer.le_view_enabled_alpha) * 0.01f;
            } else {
                alpha = this.getResources().getInteger(R.integer.le_view_disabled_alpha) * 0.01f;
            }

            for (int i = 0; i < vg.getChildCount(); i++) {
                vg.getChildAt(i).setEnabled(enabled);
                vg.getChildAt(i).setAlpha(alpha);
            }
            vg.setEnabled(enabled);
        }
    }

    public void setEnable(String tag, boolean enabled) {
        int pos = getTabPos(tag);
        if (pos < 0)
            return;

        setEnable(pos, enabled);
    }

    public boolean getEnable(int pos) {
        View v = getTabView(pos);
        if (v == null)
            return false;

        return v.isEnabled();
    }

    public boolean getEnable(String tag) {
        int pos = getTabPos(tag);
        if (pos < 0)
            return false;

        return getEnable(pos);
    }

    public int getMode() {
        return mMode;
    }

    public int getTabCount() {
        return mTabCnt;
    }

    public void clear() {
        this.removeAllViews();

        mMode = -1;
        mTabCnt = -1;
        mLayoutResId = -1;
        mTags.clear();
    }

    /**
     * Interface definition for a callback to be invoked when tab clicked
     */
    public interface OnClickAndLongClickListener {
        void onClick(int pos, String tag);

        boolean onLongClick(int pos, String tag);
    }

    public void setEnableAnimation(boolean enabled) {
        if (!isValid())
            return;

        Log.d(TAG, "setEnableAnimation " + enabled);

        for (int pos = 0; pos <= mTabCnt - 1; pos++) {
            View tab = getTabView(pos);
            if (tab != null && tab instanceof LeGlowRelativeLayout) {
                ((LeGlowRelativeLayout) tab).setEnabledAnimation(enabled);
            }
        }
    }

    /**
     * set the press color of tab
     * @param color
     */
    public void setPressColor(int color) {
        for (int pos = 0; pos <= mTabCnt - 1; pos++) {
            View tab = getTabView(pos);
            if (tab != null && tab instanceof LeGlowRelativeLayout) {
                ((LeGlowRelativeLayout) tab).setPressColor(color);
            }
        }
    }
    
    private void setTabListener() {
        for (int pos = 0; pos <= mTabCnt - 1; pos++) {
            View tab = getTabView(pos);
            if (tab != null) {
                tab.setOnClickListener(this);
                tab.setOnLongClickListener(this);
            }
        }
    }

    private void layoutTabs() {
        if (!isValid())
            return;

        boolean hasTitle = false;
        if (mMode == MODE_ICON_TEXT) {
            hasTitle = true;
        }

        LeTabWidgetUtils.setTabWidgetLayout(this.getContext(), this, hasTitle, false);
    }

    public static void addBottomWidgetToPreference(Activity activity, LeBottomWidget bw) {
        if (activity == null)
            return;

        // Obtain decor view and remove root view
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView()
                .findViewById(android.R.id.content);
        if (decorView == null || decorView.getChildCount() < 1)
            return;

        ViewGroup oldRootView = (ViewGroup) decorView.getChildAt(0);
        decorView.removeView(oldRootView);

        // create an new root view and add it to decor view
        // LinearLayout.LayoutParams p0 = new LinearLayout.LayoutParams
        // (LinearLayout.LayoutParams.MATCH_PARENT,
        // LinearLayout.LayoutParams.MATCH_PARENT);

        LinearLayout newRootView = new LinearLayout(activity);
        newRootView.setOrientation(LinearLayout.VERTICAL);

        ViewGroup.LayoutParams lp = oldRootView.getLayoutParams();
        newRootView.setLayoutParams(lp);

        decorView.addView(newRootView);

        // Add old root view to new root view as the frist child
        LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        p1.weight = 1;
        // oldRootView.setLayoutParams(p1);
        newRootView.addView(oldRootView, p1);

        // Add bottom widget to new root view
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        newRootView.addView(bw, params);
    }

}
