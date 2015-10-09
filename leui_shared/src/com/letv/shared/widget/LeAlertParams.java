package com.letv.shared.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.letv.shared.R;

/**
 * Created by liangchao on 15-3-16.
 */
public class LeAlertParams {

    public final Context mContext;
    public final LayoutInflater mInflater;

    public CompoundButton.OnCheckedChangeListener checkbox_listener;
    public String[] btn_text;
    public String titleText;
    public String contentText;
    public String checkboxText;
    public int[] btnColors;
    public View customView;
    public DialogInterface.OnClickListener onClickListener;

    private ViewGroup menuContainer;
    private View mMenuView;
    private TextView titleView;
    private TextView contentView;

    private ImageView gapLine_1;
    private ImageView gapLine_2;
    public Button btn_confirm;
    public Button btn_cancel;
    private LinearLayout layoutForDiy;
    private ImageView diyLine1;
    private ImageView diyLine2;
    private LinearLayout checkbox_ctn;
    private CheckBox checkBox;
    public static final int BTN_CFM_COLOR_BLUE = 0xff2395ee;
    public static final int BTN_CFM_COLOR_RED = 0xffeb3e2e;
    public static final int BTN_CFM_COLOR_BLACK = 0xff000000;

    public LeAlertParams(Context context) {
        this.mContext = context;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void apply(LeAlertController controller){
        menuContainer = (ViewGroup)mInflater.inflate(R.layout.le_bottomsheet, null);
        getWidget();
        setStyle(onClickListener, checkbox_listener, btn_text,
                titleText, contentText, checkboxText, btnColors, customView);

        controller.setContentView(menuContainer);

    }

    private void getWidget(){
        menuContainer.removeAllViews();
        mMenuView = mInflater.inflate(R.layout.le_bottomsheet_btn_default_activity,null);
        titleView = (TextView)mMenuView.findViewById(R.id.le_bottomsheet_default_title);
        contentView= (TextView)mMenuView.findViewById(R.id.le_bottomsheet_default_content);


        checkBox = (CheckBox)mMenuView.findViewById(R.id.le_bottomsheet_default_checkbox);
        checkbox_ctn = (LinearLayout)mMenuView.findViewById(R.id.le_bottomsheet_default_chk_ctn);
        gapLine_1 = (ImageView)mMenuView.findViewById(R.id.le_bottomsheet_default_gapline1);
        gapLine_2 = (ImageView)mMenuView.findViewById(R.id.le_bottomsheet_default_gapline2);
        btn_confirm = (Button)mMenuView.findViewById(R.id.le_bottomsheet_default_confirm);
        btn_cancel = (Button)mMenuView.findViewById(R.id.le_bottomsheet_default_cancel);
        layoutForDiy = (LinearLayout) mMenuView.findViewById(R.id.le_bottomsheet_default_layout_diy);
        diyLine1 = (ImageView)mMenuView.findViewById(R.id.le_bottomsheet_default_gaplinediy1);
        diyLine2 = (ImageView)mMenuView.findViewById(R.id.le_bottomsheet_default_gaplinediy2);
    }

    private void setStyle(final DialogInterface.OnClickListener onClickListener,

                          CompoundButton.OnCheckedChangeListener checkbox_listener,
                          String[] btn_text,
                          String titleText,
                          String contentText,
                          String checkboxText,int[] btnColors,View customView
    ){



        if((checkbox_listener==null||checkboxText==null)&&titleText==null&&contentText==null&&customView==null){
            gapLine_1.setVisibility(View.GONE);
        }
        if(titleText==null&&contentText==null&&customView!=null){
            diyLine1.setVisibility(View.GONE);
        }
        if(customView==null){
            layoutForDiy.setVisibility(View.GONE);
            diyLine1.setVisibility(View.GONE);
            diyLine2.setVisibility(View.GONE);
        }else{
            layoutForDiy.addView(customView);
        }

        if(titleText==null||titleText.isEmpty()){
            titleView.setVisibility(View.GONE);
        }else{
            titleView.setText(titleText);
        }

        if(contentText==null||contentText.isEmpty()){
            contentView.setVisibility(View.GONE);
        }else{
            contentView.setText(contentText);
        }

        if(checkbox_listener==null||checkboxText==null||checkboxText.isEmpty()){
            checkBox.setVisibility(View.GONE);
            checkbox_ctn.setVisibility(View.GONE);
            if(customView!=null){
                gapLine_1.setVisibility(View.GONE);
            }


        }else{
            checkBox.setText(checkboxText);
            if(checkbox_listener!=null){
                checkBox.setOnCheckedChangeListener(checkbox_listener);
            }

            checkbox_ctn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!checkBox.isChecked()){
                        checkBox.setChecked(true);
                    }else{
                        checkBox.setChecked(false);
                    }


                }
            });

        }

        if (btn_text!=null) {
            if(btn_text.length>=1){
                btn_confirm.setText(btn_text[0]);
                btn_confirm.setTextColor(btnColors[0]);
                if(onClickListener!=null){
                    btn_confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onClickListener.onClick(null,DialogInterface.BUTTON_POSITIVE);
                        }
                    });
                }
            }
            if (btn_text.length==1){
                btn_cancel.setVisibility(View.GONE);
                gapLine_2.setVisibility(View.GONE);

            }else if(btn_text.length==2){
                btn_cancel.setText(btn_text[1]);
                btn_cancel.setTextColor(btnColors[1]);
                if(onClickListener!=null){
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onClickListener.onClick(null,DialogInterface.BUTTON_NEGATIVE);
                        }
                    });
                }
            }
        }

        menuContainer.addView(mMenuView);

    }

}
