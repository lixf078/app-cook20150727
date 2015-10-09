package com.letv.shared.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dupengtao on 15-2-5.
 */
public class LeLoadingDialog extends Dialog {
    private final int mCurTheme;
    private Context mContext;
    private LeLoadingView mLeLoadingView;
    private TextView mTitle, mContent;

    public LeLoadingDialog(Context context, int contentViewSizeDp) {
        this(context, 0, contentViewSizeDp);
    }

    public LeLoadingDialog(Context context, int theme, int contentViewSizeDp) {
        this(context, theme, contentViewSizeDp, null, null);
    }

    public LeLoadingDialog(Context context, int theme, int contentViewSizeDp, String title, String content) {
        this(context, theme, contentViewSizeDp, title, content, null);
    }

    public LeLoadingDialog(Context context, int theme, int contentViewSizeDp, String title, String content, Runnable dismissCallBack) {
        super(context, theme);
        mCurTheme = theme;
        mContext = context;
        initDialog(dismissCallBack);
        initView(contentViewSizeDp, title, content);
    }


    private void initDialog(final Runnable dismissCallBack) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ColorDrawable colorDrawable;
        if (mCurTheme == 0) {
            colorDrawable = new ColorDrawable(Color.TRANSPARENT);
        } else {
            colorDrawable = new ColorDrawable(Color.parseColor("#F2FFFFFF"));
        }
        this.getWindow().setBackgroundDrawable(colorDrawable);
        this.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                mLeLoadingView.appearAnim(new Runnable() {
                    @Override
                    public void run() {
                        LeLoadingDialog.super.dismiss();
                        if (dismissCallBack != null) {
                            dismissCallBack.run();
                        }
                    }
                });
            }
        });
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (dismissCallBack != null) {
                    dismissCallBack.run();
                }
            }
        });
        //setCancelable(false);
        setCanceledOnTouchOutside(false);
    }


    private void initView(int contentViewSizeDp, String title, String content) {
        RelativeLayout rl = new RelativeLayout(mContext);
        rl.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        rl.setGravity(Gravity.CENTER);
        LinearLayout innerLl = new LinearLayout(mContext);
        innerLl.setGravity(Gravity.CENTER);
        innerLl.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams innerRlLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        innerLl.setLayoutParams(innerRlLp);
        rl.addView(innerLl);

        mLeLoadingView = new LeLoadingView(mContext);
        int v = (int) dipToPixels(mContext, contentViewSizeDp);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(v, v);
        layoutParams.gravity = Gravity.CENTER;
        mLeLoadingView.setLayoutParams(layoutParams);
        innerLl.addView(mLeLoadingView);

        LinearLayout l1 = addLinearLayout1(title);
        innerLl.addView(l1);
        if (TextUtils.isEmpty(title)) {
            l1.setVisibility(View.GONE);
        }
        LinearLayout l2 = addLinearLayout2(content);
        innerLl.addView(l2);
        if (TextUtils.isEmpty(content)) {
            l2.setVisibility(View.GONE);
        }

        setContentView(rl);
        if (mCurTheme == 0) {
            ArrayList<Integer> colorList = new ArrayList<Integer>();
            colorList.add(Color.WHITE);
            colorList.add(Color.WHITE);
            colorList.add(Color.WHITE);
            colorList.add(Color.WHITE);
            colorList.add(Color.WHITE);
            colorList.add(Color.WHITE);
            mLeLoadingView.setColorList(colorList);
        }
    }

    private LinearLayout addLinearLayout1(String title) {
        LinearLayout l1 = new LinearLayout(mContext);

        l1.setGravity(Gravity.CENTER);
        l1.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams l1Lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        l1Lp.setMargins(0, (int) dipToPixels(mContext, 9f), 0, 0);
        l1.setLayoutParams(l1Lp);

        TextView v1 = new TextView(mContext);
        LinearLayout.LayoutParams l1Item1Lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        l1Item1Lp.weight = 1;
        v1.setLayoutParams(l1Item1Lp);
        l1.addView(v1);
        //
        mTitle = new TextView(mContext);
        LinearLayout.LayoutParams l1Item2Lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        l1Item2Lp.weight = 8;
        mTitle.setGravity(Gravity.CENTER);
        mTitle.setLayoutParams(l1Item2Lp);
        mTitle.setSingleLine(true);
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        if (mCurTheme == 0) {
            mTitle.setTextColor(Color.WHITE);
        } else {
            mTitle.setTextColor(Color.parseColor("#575757"));
        }
        mTitle.setText(title);
        l1.addView(mTitle);
        //
        TextView v3 = new TextView(mContext);
        LinearLayout.LayoutParams l1Item3Lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        l1Item3Lp.weight = 1;
        v3.setLayoutParams(l1Item3Lp);
        l1.addView(v3);
        return l1;
    }


    private LinearLayout addLinearLayout2(String content) {
        LinearLayout l1 = new LinearLayout(mContext);

        l1.setGravity(Gravity.CENTER);
        l1.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams l1Lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        l1Lp.setMargins(0, (int) dipToPixels(mContext, 7f), 0, 0);
        l1.setLayoutParams(l1Lp);

        TextView v1 = new TextView(mContext);
        LinearLayout.LayoutParams l1Item1Lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        l1Item1Lp.weight = 1;
        v1.setLayoutParams(l1Item1Lp);
        l1.addView(v1);
        //
        mContent = new TextView(mContext);
        LinearLayout.LayoutParams l1Item2Lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        l1Item2Lp.weight = 8;
        mContent.setGravity(Gravity.CENTER);
        mContent.setLayoutParams(l1Item2Lp);
        mContent.setSingleLine(true);
        mContent.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        if (mCurTheme == 0) {
            mContent.setTextColor(Color.parseColor("#99ffffff"));
        } else {
            mContent.setTextColor(Color.parseColor("#717171"));
        }
        mContent.setText(content);
        l1.addView(mContent);
        //
        TextView v3 = new TextView(mContext);
        LinearLayout.LayoutParams l1Item3Lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        l1Item3Lp.weight = 1;
        v3.setLayoutParams(l1Item3Lp);
        l1.addView(v3);
        return l1;
    }


    public float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    @Override
    public void dismiss() {
        //super.dismiss();
        mLeLoadingView.setCancelAnim(true);
        mLeLoadingView.disappearAnim(new Runnable() {
            @Override
            public void run() {
                LeLoadingDialog.super.dismiss();
            }
        });
    }

    public void dismissNoAnim() {
        mLeLoadingView.disappearAnim(null);
        super.dismiss();
    }

    /**
     * eg.
     *
     * @ Override
     * protected void onDestroy() {
     *    onDismissDialog4DestroyContext();
     *    super.onDestroy();
     * }
     */
    public void onDismissDialog4DestroyContext() {
        if (isShowing()) {
            dismissNoAnim();
        }
    }


    public LeLoadingView getLeLoadingView() {
        return mLeLoadingView;
    }

    /**
     * get title TextView
     */
    public TextView getTitle() {
        return mTitle;
    }

    /**
     * get content TextView
     */
    public TextView getContent() {
        return mContent;
    }

    public void setTitleStr(String title) {
        ViewGroup group = (ViewGroup) mTitle.getParent();
        if (group.getVisibility() == View.GONE) {
            group.setVisibility(View.VISIBLE);
        }
        mTitle.setText(title);
    }

    public void setContentStr(String content) {
        ViewGroup group = (ViewGroup) mContent.getParent();
        if (group.getVisibility() == View.GONE) {
            group.setVisibility(View.VISIBLE);
        }
        mContent.setText(content);
    }

}
