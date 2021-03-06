package com.letv.shared.widget;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import android.widget.RelativeLayout.LayoutParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.widget.AdapterView;
import com.letv.shared.R;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Created by liangchao on 14-12-1.
 * LeBottomSheet is a Dialog which can pop either from bottom or top of the screen,
 * there are four types at present,including Button style/ListView style/GridView 
 * style and Checkbox style,the Sheet choose different style based on different input 
 * parameters of setStyle method. you can also use your own style by calling 
 *              public void setStyle(View view) 
 * method.for more information please visit http://wiki.letv.cn
 */
public class LeBottomSheet extends Dialog/*implements GestureDetector.OnGestureListener*/{
    private static final String TAG = "LeBottomSheet";
    public static final int BUTTON_VERTILCAL = 0;
    public static final int BUTTON_HORIZONTAL_TITLE_CONTENT = 1;
    public static final int BUTTON_VERTICAL_TITLE_CONTENT_GAPLINE = 2;
    public static final int BUTTON_VERTICAL_TITLE_CONTENT = 3;
    public static final int SWITCH_BUTTON_STYLE_DIY = 4;
    public static final int BUTTON_DEFAULT_STYLE = 5;
    public static final int BUTTON_PROGRESS = 6;

    public static final int LISTVIEW = 0;
    public static final int LISTVIEW_TITLE = 1;
    public static final float DEFAULT_GRIDVIEW_ITEM_DELAY = 0.05f;
    public static final int BTN_CFM_COLOR_BLUE = 0xff2395ee;
    public static final int BTN_CFM_COLOR_RED = 0xffeb3e2e;

    private Button btn_confirm, btn_cancel, btn_info;

    private TextView title,content;
    private View mMenuView;
    private ViewGroup menuContainer;
    private LayoutInflater mInflater;
    private ListView listView;
    private ImageView iconView;
    private LeCheckBox checkBox;
    private boolean isSinglebox = true;//是否为单选框
    private boolean itemLimit = true;
    private LeBotOnClickListener mLeBotOnClickListener;
    private String[] btnStr;
    private int[] btnColor;
    private Map<Integer, Integer> itemColorMap;//Each color value of listview

    public ImageView getIconView() {
        return iconView;
    }

    public void setSinglebox(boolean isSinglebox) {
        this.isSinglebox = isSinglebox;
        animateToTextViewColor = isSinglebox;
    }

    public void setItemLimit(boolean itemLimit) {
        this.itemLimit = itemLimit;
    }

    public void setTailTextColor(int tailTextColor) {
        this.tailTextColor = tailTextColor;
    }

    private int tailTextColor = Color.WHITE;

    /* to change the listview item`s color */
    private int ListItemColor = BTN_CFM_COLOR_BLUE;

    public void setCheckBoxItemColor(int color) {
        ListItemColor = color;
    }

    public int getCheckBoxItemColor() {
        return ListItemColor;
    }

    public void setContentAtCenter(boolean contentAtCenter) {
        this.contentAtCenter = contentAtCenter;
        if (contentAtCenter && mDialog != null && content != null && content.getVisibility() == View.VISIBLE) {
            content.setGravity(Gravity.CENTER_HORIZONTAL);
        }
    }

    private boolean contentAtCenter = false;

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public LinearLayout getLayoutForDiy() {
        return layoutForDiy;
    }

    private LinearLayout layoutForDiy;

    public GridView getGridView() {
        return gridView;
    }

    private GridView gridView;
    private View.OnLayoutChangeListener onLayoutChangeListener;
    private boolean moreItem = false;

    //    private static int listitem_height;
    //    private static int listview_bottom_height;
    private static float density;
    private int style;
    private int itemNum;
    private boolean[] isSelected;
    private boolean slideFromTop = false;
    private LinearLayout checkbox_ctn;

    public Dialog getmDialog() {
        return mDialog;
    }

    private LeBottomSheet mDialog;

    private boolean mLinkMovementEnabled = false;

    public boolean isLinkMovementEnabled() {
        return mLinkMovementEnabled;
    }

    /**
     * Set whether show hyperlinks
     * @param linkMovementEnabled
     */
    public void setLinkMovementEnabled(boolean linkMovementEnabled) {
        this.mLinkMovementEnabled = linkMovementEnabled;
    }

    public ImageView getListviewTitle_tailImg() {
        return listviewTitle_tailImg;
    }

    public TextView getListviewTitle_tailText() {
        return listviewTitle_tailText;
    }

    private TextView listviewTitle_tailText;
    private ImageView listviewTitle_tailImg;

    public TextView getSubTitle() {
        return subTitle;
    }

    private TextView subTitle;

    public int getMaxHeightInPixel() {
        return maxHeightInPixel;
    }

    public void setMaxHeightInPixel(int maxHeightInPixel) {
        this.maxHeightInPixel = maxHeightInPixel;
    }

    private int maxHeightInPixel = -1;
    private Context mContext;

    public ListView getListView() {
        return listView;
    }

    public Button getBtn_confirm() {
        return btn_confirm;
    }

    public Button getBtn_cancel() {
        return btn_cancel;
    }

    public TextView getTitle() {
        return title;
    }

    public TextView getContent() {
        return content;
    }

    public View getmMenuView() {
        return mMenuView;
    }

    private static int dip2px(float dp) {

        return (int) (dp * density + 0.5f);
    }

    public LeBottomSheet(Context context, int theme) {
        super(context, theme);
        if (theme == R.style.leBottomSheetThemeTop) {
            this.slideFromTop = true;
        }
        mDialog = this;
        init(context, this.slideFromTop);
    }

    public LeBottomSheet(Context context) {
        super(context, R.style.leLicenceDialogTheme);
        mDialog = this;
        init(context, false);
    }

    public LeBottomSheet(Context context, boolean b) {
        this(context);
    }

    private int MaxItemNum = 6;

    public int getMaxItemNum() {
        return MaxItemNum;
    }

    public void setMaxItemNum(int maxItemNum) {
        MaxItemNum = maxItemNum;
    }

    private int checkPos = -1;

    public void setCheckPos(int checkPos) {
        this.checkPos = checkPos;
        //Determine if you are a check box and initialize the associated variable
        if (!isSinglebox) {
            if (checkedIndexList == null) {
                checkedIndexList = new ArrayList<Integer>();
            }
            if (!checkedIndexList.contains(checkPos)) {
                checkedIndexList.add(checkPos);
            }
        }
        //end
        if (mDialog != null && mDialog.isShowing() && adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public int getCheckPos() {
        return checkPos;
    }

    private ListViewAdapter adapter;

    public void setWenyiStyle(View.OnClickListener listener_1, View.OnClickListener listener_2, View.OnClickListener listener_3, String[] btn_text, Drawable drawable,
            CharSequence permissionText, int btnCfmColor) {
    										
			menuContainer.removeAllViews();
			inflateCustomLayout(R.layout.wenyi_bottomsheet_btn_checkbox);
			btn_confirm = (Button) mMenuView.findViewById(R.id.le_bottomsheet_btn_confirm_5);
			btn_cancel = (Button) mMenuView.findViewById(R.id.le_bottomsheet_btn_cancel_5);
			content = (TextView) mMenuView.findViewById(R.id.le_bottomsheet_switchbutton_text);
			content.setText(permissionText);
			btn_info = (Button) mMenuView.findViewById(R.id.le_bottomsheet_btn_confirm_5_info);
			 
			if (mLinkMovementEnabled) {
			    content.setMovementMethod(LinkMovementMethod.getInstance());
			}
			iconView = (ImageView) mMenuView.findViewById(R.id.le_bottomsheet_switchbutton_icon);
			if (drawable == null) {
			    iconView.setVisibility(View.GONE);
			
			} else {
			    iconView.setImageDrawable(drawable);
			}
			content.setGravity(Gravity.CENTER);
			
			if (listener_1 != null) {
			    btn_confirm.setOnClickListener(listener_1);
			}
			if (listener_2 != null) {
			    btn_cancel.setOnClickListener(listener_2);
			}
			if (listener_3 != null) {
				btn_info.setOnClickListener(listener_3);
			}
			
			btn_confirm.setText(btn_text[0]);
			btn_confirm.setTextColor(btnCfmColor);
			btn_cancel.setText(btn_text[1]);
			btn_info.setText(btn_text[2]);
			setContentView();
//			setWenyiContentView();
		}
    
    public void setStyle(View.OnClickListener listener_1, View.OnClickListener listener_2,
                    CompoundButton.OnCheckedChangeListener checkbox_listener, String[] btn_text, Drawable drawable,
                    CharSequence permissionText, int btnCfmColor) {

        menuContainer.removeAllViews();
        inflateCustomLayout(R.layout.le_bottomsheet_btn_checkbox);
        btn_confirm = (Button) mMenuView.findViewById(R.id.le_bottomsheet_btn_confirm_5);
        btn_cancel = (Button) mMenuView.findViewById(R.id.le_bottomsheet_btn_cancel_5);
        content = (TextView) mMenuView.findViewById(R.id.le_bottomsheet_switchbutton_text);
        content.setText(permissionText);

        if (mLinkMovementEnabled) {
            content.setMovementMethod(LinkMovementMethod.getInstance());
        }
        iconView = (ImageView) mMenuView.findViewById(R.id.le_bottomsheet_switchbutton_icon);
        if (drawable == null) {
            iconView.setVisibility(View.GONE);

        } else {
            iconView.setImageDrawable(drawable);
        }
        content.setGravity(Gravity.CENTER);

        checkBox = (LeCheckBox) mMenuView.findViewById(R.id.le_bottomsheet_switchbutton_checkbox);
        checkBox.setOnCheckedChangeListener(checkbox_listener);
        checkbox_ctn = (LinearLayout) mMenuView.findViewById(R.id.le_bottomsheet_btn_chk_ctn);
        checkbox_ctn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox.isChecked()) {
                    checkBox.setChecked(true, true);
                } else {
                    checkBox.setChecked(false, true);
                }
            }
        });
        if (listener_1 != null) {
            btn_confirm.setOnClickListener(listener_1);
        }
        if (listener_2 != null) {
            btn_cancel.setOnClickListener(listener_2);
        }
        btn_confirm.setText(btn_text[0]);
        btn_confirm.setTextColor(btnCfmColor);
        btn_cancel.setText(btn_text[1]);
        setContentView();
    }

    public void setStyle(View.OnClickListener listener_1, View.OnClickListener listener_2,
                    CompoundButton.OnCheckedChangeListener checkbox_listener, String[] btn_text, Drawable drawable,
                    CharSequence permissionText) {

        this.setStyle(listener_1, listener_2, checkbox_listener, btn_text, drawable, permissionText, BTN_CFM_COLOR_BLUE);
    }

    /*
     * set layout style for switch button with checkbox
     */
    public void setStyle(View.OnClickListener listener_1, View.OnClickListener listener_2,
                    CompoundButton.OnCheckedChangeListener checkbox_listener, String[] btn_text, int imgResource,
                    CharSequence permissionText) {

        menuContainer.removeAllViews();
        inflateCustomLayout(R.layout.le_bottomsheet_btn_checkbox);
        btn_confirm = (Button) mMenuView.findViewById(R.id.le_bottomsheet_btn_confirm_5);
        btn_cancel = (Button) mMenuView.findViewById(R.id.le_bottomsheet_btn_cancel_5);
        content = (TextView) mMenuView.findViewById(R.id.le_bottomsheet_switchbutton_text);
        content.setText(permissionText);
        if (mLinkMovementEnabled) {
            content.setMovementMethod(LinkMovementMethod.getInstance());
        }
        iconView = (ImageView) mMenuView.findViewById(R.id.le_bottomsheet_switchbutton_icon);
        iconView.setImageResource(imgResource);
        btn_confirm.setSelected(true);
        checkBox = (LeCheckBox) mMenuView.findViewById(R.id.le_bottomsheet_switchbutton_checkbox);
        checkBox.setOnCheckedChangeListener(checkbox_listener);
        checkbox_ctn = (LinearLayout) mMenuView.findViewById(R.id.le_bottomsheet_btn_chk_ctn);
        checkbox_ctn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox.isChecked()) {
                    checkBox.setChecked(true, true);
                } else {
                    checkBox.setChecked(false, true);
                }
            }
        });

        if (listener_1 != null) {
            btn_confirm.setOnClickListener(listener_1);

        }
        if (listener_2 != null) {
            btn_cancel.setOnClickListener(listener_2);
        }
        btn_confirm.setText(btn_text[0]);
        btn_cancel.setText(btn_text[1]);
        setContentView();
    }

    /*
     * set layout style for gridview layout
     */
    public void setStyle(Context context, List<Map<String, Object>> data, String[] keyName,
                    OnItemClickListener listener, View.OnClickListener btn_listener,
                    CompoundButton.OnCheckedChangeListener checkbox_listener, boolean noCheckbox) {

        itemNum = data.size();
        menuContainer.removeAllViews();
        inflateCustomLayout(R.layout.le_bottomsheet_grid_view);
        btn_cancel = (Button) mMenuView.findViewById(R.id.le_bottomsheet_btn_cancel_4);
        gridView = (GridView) mMenuView.findViewById(R.id.le_bottomsheet_gridview);
        title = (TextView) mMenuView.findViewById(R.id.le_bottomsheet_gridview_title);
        checkBox = (LeCheckBox) mMenuView.findViewById(R.id.le_bottomsheet_grid_checkbox);

        btn_cancel.setSelected(true);
        if (itemNum >= 3) {
            gridView.setNumColumns(3);
        }
        if (itemNum <= 6) {
            gridView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        ViewGroup.LayoutParams lp = gridView.getLayoutParams();

        if (itemNum <= 3) {
            lp.height = dip2px(72);
        } else if (itemNum > 3 && itemNum <= 6) {
            lp.height = dip2px(144 + 30);
        } else if (itemNum > 6) {
            lp.height = dip2px(144 + 60 + 36);
        }

        gridView.setLayoutParams(lp);
        if (itemNum <= 9) {
            gridView.setAdapter(new LeBottomSheetImageAdapter(context, data, keyName));
        } else {
            SimpleAdapter adapter = new SimpleAdapter(context, data, R.layout.le_bottomsheet_grid_item, keyName,
                            new int[] { R.id.le_bottomsheet_gridview_img, R.id.le_bottomsheet_gridview_text });

            gridView.setAdapter(adapter);

            adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                public boolean setViewValue(View view, Object data, String textRepresentation) {
                    if (view instanceof ImageView) {
                        ImageView iv = (ImageView) view;
                        if (data instanceof Drawable) {
                            iv.setImageDrawable((Drawable) data);
                        } else if (data instanceof Integer) {
                            iv.setImageResource((Integer) data);
                        }
                        return true;
                    } else
                        return false;
                }
            });
            Animation animation = (Animation) AnimationUtils.loadAnimation(context, R.anim.le_licence_slide_bottom_in);
            LayoutAnimationController lac = new LayoutAnimationController(animation);
            lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
            lac.setDelay(DEFAULT_GRIDVIEW_ITEM_DELAY);
            gridView.setLayoutAnimation(lac);
        }

        gridView.setVerticalFadingEdgeEnabled(true);
        gridView.setFadingEdgeLength(dip2px(40f));
        if (listener != null) {
            gridView.setOnItemClickListener(listener);
        }
        if (btn_listener != null) {
            btn_cancel.setOnClickListener(btn_listener);
        }
        if (noCheckbox) {
            checkBox.setVisibility(View.GONE);
        }
        if (checkbox_listener != null) {
            checkBox.setOnCheckedChangeListener(checkbox_listener);
            checkbox_ctn = (LinearLayout) mMenuView.findViewById(R.id.le_bottomsheet_grid_chk_ctn);
            checkbox_ctn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!checkBox.isChecked()) {
                        checkBox.setChecked(true, true);
                    } else {
                        checkBox.setChecked(false, true);
                    }
                }
            });
        }
        setContentView();
    }

    public void setStyle(Context context, List<Map<String, Object>> data, String[] keyName,
                    OnItemClickListener listener, View.OnClickListener btn_listener,
                    CompoundButton.OnCheckedChangeListener checkbox_listener) {

        setStyle(context, data, keyName, listener, btn_listener, checkbox_listener, false);
    }

    /*
     *the real setStyle function for listview
     */
    public void setStyle(Context context, List<Map<String, Object>> data, String[] keyName,
                    final OnItemClickListener listener, final boolean hasLogo, CharSequence titleText, String btnText,
                    boolean WithTailImg, View.OnClickListener btnListener) {

        if (TextUtils.isEmpty(titleText)) {
            setStyle(context, data, keyName, listener, hasLogo, LISTVIEW, WithTailImg, false, btnListener);
        } else {
            setStyle(context, data, keyName, listener, hasLogo, LISTVIEW_TITLE, WithTailImg, false, btnListener);
        }
        if (!TextUtils.isEmpty(titleText)) {
            title.setText(titleText);
            if (mLinkMovementEnabled) {
                title.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
        if (!TextUtils.isEmpty(btnText)) {
            btn_cancel.setText(btnText);
        }
    }

    private List<Integer> checkList;

    public void setStyleForTopSlideinListView(Context context, List<Map<String, Object>> data, final String[] keyName,
                    String[] actionString, String btnString, View.OnClickListener btnListener,
                    View.OnClickListener exitListner, final OnItemClickListener itemListener, int actionBarColor,
                    int btnTextColor, int actionBarTextColor) {

        itemNum = data.size();
        if (itemNum >= 6) {
            moreItem = true;
        } else {
            moreItem = false;
        }
        if (keyName.length > 1) {
            if (checkList != null) {
                checkList.clear();
            } else {
                checkList = new ArrayList<Integer>();
            }
            for (int i = 0; i < data.size(); i++) {
                checkList.add(0);
            }
        }
        inflateCustomLayout(R.layout.le_bottomsheet_listview_topslide);
        title = (TextView) mMenuView.findViewById(R.id.le_bottomsheet_list_title);
        listView = (ListView) mMenuView.findViewById(R.id.le_bottomsheet_list_1);
        btn_cancel = (Button) mMenuView.findViewById(R.id.le_bottomsheet_listview_title_btn);
        listviewTitle_tailText = (TextView) mMenuView.findViewById(R.id.le_bottomsheet_list_title_tail);
        gapLine_1 = (ImageView) mMenuView.findViewById(R.id.le_bottomsheet_listview_title_gap);
        LinearLayout actionBar = (LinearLayout) title.getParent();

        actionBar.setBackgroundColor(actionBarColor);
        listviewTitle_tailText.setClickable(true);
        if (exitListner != null) {
            listviewTitle_tailText.setOnClickListener(exitListner);
        }
        if (TextUtils.isEmpty(btnString) || btnListener == null) {
            btn_cancel.setVisibility(View.GONE);
            ((LinearLayout) gapLine_1.getParent()).setPadding(0, 0, 0, dip2px(14));
        } else {
            btn_cancel.setText(btnString);
            btn_cancel.setTextColor(btnTextColor);
            if (btnListener != null) {
                btn_cancel.setOnClickListener(btnListener);
            }
        }

        if (actionString != null) {
            if (actionString.length == 1) {
                title.setText(actionString[0]);
                title.setTextColor(actionBarTextColor);

            } else if (actionString.length == 2) {
                title.setText(actionString[0]);
                title.setTextColor(actionBarTextColor);
                listviewTitle_tailText.setText(actionString[1]);
                listviewTitle_tailText.setTextColor(actionBarTextColor);
            }

        }
        if (!moreItem) {
            listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        adapter = new ListViewAdapter(context, data, keyName, actionBarColor);
        listView.setAdapter(adapter);

        if (itemListener != null) {
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (keyName.length == 1) {
                        checkPos = position;
                        adapter.notifyDataSetChanged();
                    } else {
                        if (checkList != null && !checkList.isEmpty()) {
                            checkList.set(position, 1 - checkList.get(position));
                        }
                        adapter.notifyDataSetChanged();
                    }
                    itemListener.onItemClick(parent, view, position, id);
                }
            });
        }
        if (itemLimit) {
            final ViewTreeObserver vto = mMenuView.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mMenuView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    if (moreItem) {
                        View mView = listView.getAdapter().getView(0, null, listView);
                        mView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                        float height = mView.getMeasuredHeight();
                        ViewGroup.LayoutParams lp = listView.getLayoutParams();
                        lp.height = (int) (height * 5.5f);
                        listView.setLayoutParams(lp);
                    }
                }
            });
        }
        setContentView();
    }

    //Change the color of the TextView when you are selected,
    //and setArrowColorWithoutBorder the same color
    private boolean isChangeItemColor = true;
    //Is the color of TextView after the checkbox animation is finished
    private boolean animateToTextViewColor = true;

    public void setOnItemClickChangeItemColor(boolean isChange) {
        isChangeItemColor = isChange;
        animateToTextViewColor = isChange;
    }

    public void setStyle(Context context, List<Map<String, Object>> data, String[] keyName,
                    final OnItemClickListener listener, final boolean hasLogo, final int style, boolean WithTailImg,
                    boolean WithTeTail, View.OnClickListener btnListener) {

        itemNum = data.size();
        isSelected = new boolean[itemNum];
        for (int i = 0; i < itemNum; i++) {
            isSelected[i] = false;
        }
        this.style = style;
        menuContainer.removeAllViews();
        ImageView gapline, gapline1;

        switch (style) {
        case LISTVIEW_TITLE:
            inflateCustomLayout(R.layout.le_bottomsheet_listview_title);
            gapline = (ImageView) mMenuView.findViewById(R.id.le_bottomsheet_listview_title_gap);
            btn_cancel = (Button) mMenuView.findViewById(R.id.le_bottomsheet_listview_title_btn);
            btn_confirm = (Button) mMenuView.findViewById(R.id.le_bottomsheet_listview_title_btn_2);
            title = (TextView) mMenuView.findViewById(R.id.le_bottomsheet_list_title);
            listView = (ListView) mMenuView.findViewById(R.id.le_bottomsheet_list_1);
            listviewTitle_tailText = (TextView) mMenuView.findViewById(R.id.le_bottomsheet_list_title_tail);
            listviewTitle_tailImg = (ImageView) mMenuView.findViewById(R.id.le_bottomsheet_list_title_tail_arrow);
            if (data.size() > MaxItemNum) {
                moreItem = true;
            } else {
                listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            }
            listviewTitle_tailText.setVisibility(View.GONE);
            listviewTitle_tailImg.setVisibility(View.GONE);
            break;

        default:
        case LISTVIEW:
            inflateCustomLayout(R.layout.le_bottomsheet_listview);
            gapline = (ImageView) mMenuView.findViewById(R.id.le_bottomsheet_listview_gap);
            gapline1 = (ImageView) mMenuView.findViewById(R.id.le_bottomsheet_listview_gap1);
            btn_cancel = (Button) mMenuView.findViewById(R.id.le_bottomsheet_listview_btn);
            btn_confirm = (Button) mMenuView.findViewById(R.id.le_bottomsheet_listview_title_btn_2);
            listView = (ListView) mMenuView.findViewById(R.id.le_bottomsheet_list_0);
            if (mLeBotOnClickListener != null) {
                gapline1.setVisibility(View.VISIBLE);
            }
            if (data.size() > MaxItemNum) {
                moreItem = true;
            } else {
                listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            }
            break;
        }

        btn_confirm.setVisibility(View.GONE);
        adapter = new ListViewAdapter(context, data, keyName, hasLogo, WithTailImg);
        listView.setAdapter(adapter);

        if (listener != null) {
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    checkPos = position;
                    adapter.notifyDataSetChanged();
                    listener.onItemClick(parent, view, position, id);
                }
            });
        }

        if (btnListener != null) {
            btn_cancel.setOnClickListener(btnListener);
            btn_cancel.setSelected(true);
        } else {
            gapline.setVisibility(View.GONE);
            btn_cancel.setVisibility(View.GONE);
        }

        //add by zhangzhao
        if (mLeBotOnClickListener != null) {
            gapline.setVisibility(View.VISIBLE);
            setBottomButton();
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    checkPos = position;
                    if (!isSinglebox) {
                        if (checkedIndexList == null) {
                            checkedIndexList = new ArrayList<Integer>();
                        }
                        if (!checkedIndexList.contains(position)) {
                            checkedIndexList.add(position);
                        } else {
                            checkedIndexList.remove((Integer) position);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        }
        //end
        listView.setVerticalFadingEdgeEnabled(true);
        listView.setFadingEdgeLength(dip2px(14f));
        final ViewTreeObserver vto = mMenuView.getViewTreeObserver();

        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mMenuView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                if (moreItem) {
                    View mView = listView.getAdapter().getView(0, null, listView);
                    mView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                    float height = mView.getMeasuredHeight();
                    ViewGroup.LayoutParams lp = listView.getLayoutParams();
                    lp.height = (int) (height * 5.5f);
                    listView.setLayoutParams(lp);
                }
            }
        });
        setContentView();
    }

    /**
     * Set bottom button than contains the checkbox of listView 
     */
    private void setBottomButton() {
        btn_confirm.setVisibility(View.VISIBLE);
        btn_cancel.setVisibility(View.VISIBLE);
        btn_cancel.setSelected(true);

        if (btnColor != null && btnColor.length == 2) {
            btn_cancel.setTextColor(btnColor[0]);
            btn_confirm.setTextColor(btnColor[1]);
        }
        if (btnStr != null && btnStr.length == 2) {
            btn_cancel.setText(btnStr[0]);
            btn_confirm.setText(btnStr[1]);
        } else {
            btn_cancel.setText(R.string.le_force_close);
            btn_confirm.setText(R.string.cancel_btn_v2);
        }
        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mLeBotOnClickListener != null) {
                    mLeBotOnClickListener.confirmOnClickListener();
                }
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mLeBotOnClickListener != null) {
                    mLeBotOnClickListener.cancleOnClickListener();
                }
            }
        });
    }

    /**
     * checkboxList
     * @param context
     * @param data
     * @param keyName
     * @param hasLogo
     * @param WithTailImg
     * @param style
     * @param WithTitleTail
     * @param botOnClickListener checkbox, confirmButton and cancleButton  callback
     * @param btnStr[]  length is 2,The first value is the button above, which is confirm by default.
     *                   and the second value is the following button, which is cancel by default. 
     * @param btnColor[] The length is 2, the color and the button position corresponds, the default is black 
     */
    public void setStyle(Context context, List<Map<String, Object>> data, String[] keyName, final boolean hasLogo,
                    int style, boolean WithTailImg, boolean WithTitleTail, LeBotOnClickListener botOnClickListener,
                    String[] btnStr, int[] btnColor) {
        isSinglebox = false;
        animateToTextViewColor = false;
        if (checkedIndexList == null) {
            checkedIndexList = new ArrayList<Integer>();
        }
        mLeBotOnClickListener = botOnClickListener;
        this.btnStr = btnStr;
        this.btnColor = btnColor;
        setStyle(context, data, keyName, null, hasLogo, style, WithTailImg, WithTitleTail);
    }

    /**
     * checkbox, confirmButton and cancleButton  callback
     * @author zhangzhao
     */
    public interface LeBotOnClickListener {
        //confirmButton callback
        public void confirmOnClickListener();

        //cancleButton callback
        public void cancleOnClickListener();

        //checkbox callback
        public void onCheckedClick(int which, boolean isChecked);
    }

    public void setStyle(Context context, List<Map<String, Object>> data, String[] keyName,
                    final OnItemClickListener listener, final boolean hasLogo, int style, boolean WithTailImg,
                    boolean WithTitleTail) {

        setStyle(context, data, keyName, listener, hasLogo, style, WithTailImg, WithTitleTail, null);
    }

    /*
     * set layout style for listview Layout
     */
    @Deprecated
    public void setStyle(Context context, List<Map<String, Object>> data, String[] keyName,
                    final OnItemClickListener listener, final boolean hasLogo, int style, boolean WithTailImg) {

        setStyle(context, data, keyName, listener, hasLogo, style, WithTailImg, false);
    }

    /*
     *default: setStyle for listview,without tail image
     */
    @Deprecated
    public void setStyle(Context context, List<Map<String, Object>> data, String[] keyName,
                    final OnItemClickListener listener, final boolean hasLogo, int style) {

        setStyle(context, data, keyName, listener, hasLogo, style, false);
    }

    /*
     *set layout style for switch button layout
     */
    @Deprecated
    public void setStyle(int style, final View.OnClickListener listener_cfm, final View.OnClickListener listener_cle,
                    String[] btn_text) {

        setStyle(style, listener_cfm, listener_cle, btn_text, false);
    }

    @Deprecated
    public void setStyle(int style, final View.OnClickListener listener_cfm, final View.OnClickListener listener_cle,
                    String[] btn_text, boolean WithSubTitle/*this parameter only used for SWITCH_BUTTON_STYLE_DIY*/) {

        if (style == SWITCH_BUTTON_STYLE_DIY) {
            setStyle(BUTTON_DEFAULT_STYLE, listener_cfm, listener_cle, null, btn_text, TAG, TAG, null,
                            BTN_CFM_COLOR_RED, true);
        } else {
            setStyle(BUTTON_DEFAULT_STYLE, listener_cfm, listener_cle, null, btn_text, null, null, null);
        }

        return;
    }

    /*
     * three_button_style
     */
    public void setThreeBtn(final ArrayList<View.OnClickListener> onClickListeners, ArrayList<String> btn_texts,
                    ArrayList<Integer> btn_colors, CharSequence titleText, CharSequence contentText) {

        getWidgetForThreeBtn();
        if (TextUtils.isEmpty(titleText)) {
            title.setVisibility(View.GONE);
        } else {
            title.setText(titleText);
            if (mLinkMovementEnabled) {
                title.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }

        if (TextUtils.isEmpty(contentText)) {
            content.setVisibility(View.GONE);
        } else {
            content.setText(contentText);
            if (mLinkMovementEnabled) {
                content.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }

        if ((titleText == null) && (contentText == null)) {
            gapLine_1.setVisibility(View.GONE);
        }

        Button temp;
        for (int i = 0; i < btn_texts.size(); i++) {
            temp = buttons.get(i);
            temp.setText(btn_texts.get(i));
            temp.setTextColor(btn_colors.get(i));
            temp.setOnClickListener(onClickListeners.get(i));
        }
        setContentView();
    }

    /*
     * button_default_style
     */
    public void setStyle(int style, final View.OnClickListener listener_cfm, final View.OnClickListener listener_cle,
                    CompoundButton.OnCheckedChangeListener checkbox_listener, String[] btn_text,
                    CharSequence titleText, CharSequence contentText, String checkboxText) {

        this.setStyle(style, listener_cfm, listener_cle, checkbox_listener, btn_text, titleText, contentText,
                        checkboxText, BTN_CFM_COLOR_RED, false);
    }

    /*
     * the only inferface to use
     */
    private ImageView gapLine_1, gapLine_2, diyLine1, diyLine2;

    public void setStyle(int style, final View.OnClickListener listener_cfm, final View.OnClickListener listener_cle,
                    CompoundButton.OnCheckedChangeListener checkbox_listener, String[] btn_text,
                    CharSequence titleText, CharSequence contentText, String checkboxText, int[] btnColors,
                    boolean useDiyLayout) {

        this.style = style;
        if (this.style == BUTTON_PROGRESS) {
            setProgress(titleText, btn_text, btnColors[0], listener_cfm);
            return;
        }
        getWidget();

        if ((checkbox_listener == null || checkboxText == null) && titleText == null && contentText == null
                        && !useDiyLayout) {
            gapLine_1.setVisibility(View.GONE);
        }
        if (titleText == null && contentText == null && useDiyLayout) {
            diyLine1.setVisibility(View.GONE);
        }
        if (!useDiyLayout) {
            layoutForDiy.setVisibility(View.GONE);
            diyLine1.setVisibility(View.GONE);
            diyLine2.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(titleText)) {
            title.setVisibility(View.GONE);
        } else {
            title.setText(titleText);
            if (mLinkMovementEnabled) {
                title.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }

        if (TextUtils.isEmpty(contentText)) {
            content.setVisibility(View.GONE);
        } else {
            content.setText(contentText);
            if (mLinkMovementEnabled) {
                content.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }

        if (checkbox_listener == null || checkboxText == null || checkboxText.isEmpty()) {
            checkBox.setVisibility(View.GONE);
            checkbox_ctn.setVisibility(View.GONE);
            if (useDiyLayout) {
                gapLine_1.setVisibility(View.GONE);
            }
        } else {
            checkBox.setText(checkboxText);
            if (checkbox_listener != null) {
                checkBox.setOnCheckedChangeListener(checkbox_listener);
            }

            checkbox_ctn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!checkBox.isChecked()) {
                        checkBox.setChecked(true, true);
                    } else {
                        checkBox.setChecked(false, true);
                    }
                }
            });
        }

        if (listener_cfm != null) {
            btn_confirm.setOnClickListener(listener_cfm);
        }
        if (listener_cle != null) {
            btn_cancel.setOnClickListener(listener_cle);
        }
        if (btn_text != null && btn_text.length >= 1 && btnColors.length >= 1) {
            btn_confirm.setText(btn_text[0]);
            btn_confirm.setTextColor(btnColors[0]);
        }
        if (btn_text.length == 1) {
            btn_cancel.setVisibility(View.GONE);
            gapLine_2.setVisibility(View.GONE);
        } else if (btn_text.length == 2 && btnColors.length == 2) {
            btn_cancel.setText(btn_text[1]);
            btn_cancel.setTextColor(btnColors[1]);
        }
        setContentView();
    }

    public void setStyle(int style, final View.OnClickListener listener_cfm, final View.OnClickListener listener_cle,
                    CompoundButton.OnCheckedChangeListener checkbox_listener, String[] btn_text,
                    CharSequence titleText, CharSequence contentText, String checkboxText, int btnCfmColor,
                    boolean useDiyLayout) {

        setStyle(style, listener_cfm, listener_cle, checkbox_listener, btn_text, titleText, contentText, checkboxText,
                        new int[] { btnCfmColor, Color.BLACK }, useDiyLayout);

    }

    private void getWidget() {
        menuContainer.removeAllViews();
        inflateCustomLayout(R.layout.le_bottomsheet_btn_default);
        title = (TextView) mMenuView.findViewById(R.id.le_bottomsheet_default_title);
        content = (TextView) mMenuView.findViewById(R.id.le_bottomsheet_default_content);

        subTitle = content;
        checkBox = (LeCheckBox) mMenuView.findViewById(R.id.le_bottomsheet_default_checkbox);
        checkbox_ctn = (LinearLayout) mMenuView.findViewById(R.id.le_bottomsheet_default_chk_ctn);
        gapLine_1 = (ImageView) mMenuView.findViewById(R.id.le_bottomsheet_default_gapline1);
        gapLine_2 = (ImageView) mMenuView.findViewById(R.id.le_bottomsheet_default_gapline2);
        btn_confirm = (Button) mMenuView.findViewById(R.id.le_bottomsheet_default_confirm);
        btn_cancel = (Button) mMenuView.findViewById(R.id.le_bottomsheet_default_cancel);
        layoutForDiy = (LinearLayout) mMenuView.findViewById(R.id.le_bottomsheet_default_layout_diy);
        diyLine1 = (ImageView) mMenuView.findViewById(R.id.le_bottomsheet_default_gaplinediy1);
        diyLine2 = (ImageView) mMenuView.findViewById(R.id.le_bottomsheet_default_gaplinediy2);
    }

    private ArrayList<Button> buttons;

    private void getWidgetForThreeBtn() {
        menuContainer.removeAllViews();
        inflateCustomLayout(R.layout.le_bottomsheet_btn_three);
        title = (TextView) mMenuView.findViewById(R.id.le_bottomsheet_default_title);
        content = (TextView) mMenuView.findViewById(R.id.le_bottomsheet_default_content);
        gapLine_1 = (ImageView) mMenuView.findViewById(R.id.le_bottomsheet_default_gapline1);
        if (buttons == null) {
            buttons = new ArrayList<Button>();
        }
        buttons.add((Button) mMenuView.findViewById(R.id.le_bottomsheet_btn_1));
        buttons.add((Button) mMenuView.findViewById(R.id.le_bottomsheet_btn_2));
        buttons.add((Button) mMenuView.findViewById(R.id.le_bottomsheet_btn_3));
    }

    private void setProgress(CharSequence titleText, String[] btn_text, int btnCfmColor,
                    View.OnClickListener listener_cfm) {
        getWidget();
        if (!TextUtils.isEmpty(titleText)) {
            title.setText(titleText);
        }

        content.setVisibility(View.GONE);
        checkBox.setVisibility(View.GONE);
        checkbox_ctn.setVisibility(View.GONE);
        diyLine2.setVisibility(View.GONE);
        diyLine1.setVisibility(View.GONE);
        btn_cancel.setVisibility(View.GONE);
        gapLine_2.setVisibility(View.GONE);

        if (btn_text != null) {
            btn_confirm.setText(btn_text[0]);
            btn_confirm.setTextColor(btnCfmColor);
        }
        if (listener_cfm != null) {
            btn_confirm.setOnClickListener(listener_cfm);
        }
        layoutForDiy.setPadding(dip2px(16), dip2px(18), dip2px(16), 0);
        if (progressBar == null) {
            progressBar = (ProgressBar) LayoutInflater.from(mContext).inflate(R.layout.le_bottomsheet_progress, null);
            progressBar.setBackgroundColor(0xffececec);
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.le_bottomesheet_progressbar);
            progressBar.setProgressDrawable(drawable);
            progressBar.setVisibility(View.VISIBLE);
        }
        layoutForDiy.addView(progressBar);
        setContentView();
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    private ProgressBar progressBar;

    /*
     * set your own layout
     */
    public void setStyle(int layoutResource) {
        menuContainer.removeAllViews();
        inflateCustomLayout(layoutResource);
        setContentView();
    }

    public void setStyle(View view) {
        menuContainer.removeAllViews();
        inflateCustomLayout(R.layout.le_bottomsheet_blank);
        layoutForDiy = (LinearLayout) mMenuView.findViewById(R.id.le_bottomsheet_layout_blank);
        layoutForDiy.addView(view);
        setContentView();
    }

    public void setCustomView(View view) {
        if (layoutForDiy != null) {
            layoutForDiy.addView(view);
        }
    }

    public void disappear() {
        super.dismiss();
    }

    private void setDialogWidth() {
        if (mDialog != null) {
            Window dialogWindow = mDialog.getWindow();
            DisplayMetrics dm = new DisplayMetrics();
            dialogWindow.getWindowManager().getDefaultDisplay().getMetrics(dm);
            if (menuContainer != null) {
                menuContainer.setMinimumWidth(Math.min(dm.widthPixels, dm.heightPixels));
            }
        }
    }

    public void show() {
        setDialogWidth();
        super.show();
    }

    public void appear() {
        show();
    }

    private void setWenyiContentView() {
        menuContainer.addView(mMenuView);
        if (mDialog != null) {
            mDialog.setContentView(menuContainer, new LinearLayout.LayoutParams(((Activity) mContext).getWindowManager().getDefaultDisplay()
    				.getWidth() - 80,
    				LayoutParams.WRAP_CONTENT));
        }
        final ViewTreeObserver vto = menuContainer.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (maxHeightInPixel != -1) {
                    int height = menuContainer.getMeasuredHeight();
                    ViewGroup.LayoutParams lp = menuContainer.getLayoutParams();
                    if (height > maxHeightInPixel) {
                        lp.height = maxHeightInPixel;
                        menuContainer.setLayoutParams(lp);
                    }
                }
            }
        });
    }
    
    private void setContentView() {
        menuContainer.addView(mMenuView);
        if (mDialog != null) {
            mDialog.setContentView(menuContainer);
        }
        final ViewTreeObserver vto = menuContainer.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (maxHeightInPixel != -1) {
                    int height = menuContainer.getMeasuredHeight();
                    ViewGroup.LayoutParams lp = menuContainer.getLayoutParams();
                    if (height > maxHeightInPixel) {
                        lp.height = maxHeightInPixel;
                        menuContainer.setLayoutParams(lp);
                    }
                }
            }
        });
    }

    private void init(Context context, boolean slideFromTop) {
        mContext = context;
        density = context.getResources().getDisplayMetrics().density;
        //        listitem_height = dip2px(56f);
        //        listview_bottom_height = dip2px(16f);

        if (slideFromTop) {
            mDialog.getWindow().setGravity(Gravity.TOP);
        } else {
            mDialog.getWindow().setGravity(Gravity.BOTTOM);
        }
        Window dialogWindow = mDialog.getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        dialogWindow.getWindowManager().getDefaultDisplay().getMetrics(dm);

        if (mInflater == null) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (menuContainer == null) {
            menuContainer = (ViewGroup) mInflater.inflate(R.layout.le_bottomsheet, null);
            if (dm.widthPixels > dm.heightPixels) {
                menuContainer.setMinimumWidth(dm.heightPixels);
            }
        }
    }

    private void inflateCustomLayout(int resource) {
        mMenuView = mInflater.inflate(resource, null);
    }

    public void setCheckIsOn(boolean checkIsOn) {
        this.checkIsOn = checkIsOn;
    }

    public boolean isCheckIsOn() {
        return checkIsOn;
    }

    /* use checkbox listview or not*/
    private boolean checkIsOn = true;

    public void setUnableItemIndex(ArrayList<Integer> unableItemIndex) {
        this.unableItemIndex = unableItemIndex;
        if (mDialog != null && mDialog.isShowing() && adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void setCloseOnTouchOutside(boolean close) {
        setCanceledOnTouchOutside(close);
    }

    public List<Map<String, Object>> getListviewDatas() {
        if (listView.getAdapter() != null) {
            return adapter.data;
        }
        return null;
    }

    private ArrayList<Integer> unableItemIndex;
    private boolean usePressEffect = true;
    private ArrayList<Integer> checkedIndexList;

    public void setUsePressEffect(boolean usePressEffect) {
        this.usePressEffect = usePressEffect;
    }

    /**
     * set item color 
     * @param position 
     * @param color
     */
    public void setItemColor(int position, int color) {
        if (itemColorMap == null) {
            itemColorMap = new HashMap<Integer, Integer>();
        }
        animateToTextViewColor = false;
        itemColorMap.put(position, color);
    }

    /* ListView Adapter */
    private class ListViewAdapter extends BaseAdapter implements DividerFilter {
        private class GridTemp {
            ImageView icon/*,tailImg*/;
            TextView textView;
            TextView tailImg;
            LeCheckBox leCheckBox;
        }

        private class ViewHolder {
            ImageView icon;
            TextView mainText, subText;
            LeCheckBox leCheckBox;
        }

        private boolean hasIcon;
        private List<Map<String, Object>> data;
        private String[] key;
        private LayoutInflater inflater;
        private int actionBarColor;
        /* useless parameter */
        private boolean WithTailImg;

        public ListViewAdapter(Context c, List<Map<String, Object>> data, String[] key, boolean hasIcon,
                        boolean WithTailImg) {
            this.key = key;
            this.data = data;
            this.hasIcon = hasIcon;
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.WithTailImg = WithTailImg;
        }

        public ListViewAdapter(Context c, List<Map<String, Object>> data, String[] key, int actionBarColor) {
            this.key = key;
            this.data = data;
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.actionBarColor = actionBarColor;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (slideFromTop) {
                ViewHolder holder;
                if (convertView == null) {
                    holder = new ViewHolder();
                    if (key.length == 1) {
                        convertView = inflater.inflate(R.layout.le_bottomsheet_slidetop_simple, null);
                    } else {
                        convertView = inflater.inflate(R.layout.le_bottomsheet_slidetop, null);
                        holder.icon = (ImageView) convertView.findViewById(R.id.le_bottomsheet_slidetop_icon);
                        holder.subText = (TextView) convertView.findViewById(R.id.le_bottomsheet_subtext);
                    }
                    holder.mainText = (TextView) convertView.findViewById(R.id.le_bottomsheet_text);
                    holder.leCheckBox = (LeCheckBox) convertView.findViewById(R.id.le_bottomsheet_listview_item_chkbox);
                    holder.leCheckBox.setClickable(false);
                    if (key.length > 1 && usePressEffect) {
                        holder.leCheckBox.setTrackBoxColor(actionBarColor, Color.WHITE);
                    }
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                if (key.length == 1) {
                    holder.mainText.setText((String) data.get(position).get(key[0]));
                    if (checkPos == position) {
                        if (usePressEffect) {
                            holder.leCheckBox.setChecked(true);
                            holder.mainText.setTextColor(BTN_CFM_COLOR_BLUE);
                        }
                    } else if (checkPos != position) {
                        holder.leCheckBox.setChecked(false);
                        holder.mainText.setTextColor(0xff414141);
                    }
                } else {
                    if (checkList.get(position) == 1) {
                        holder.leCheckBox.setChecked(true);
                    } else {
                        holder.leCheckBox.setChecked(false);
                    }
                    holder.icon.setImageDrawable((Drawable) data.get(position).get(key[0]));
                    holder.mainText.setText((String) data.get(position).get(key[1]));
                    holder.subText.setText((String) data.get(position).get(key[2]));
                }
                return convertView;
            } else {
                final GridTemp temp;
                if (convertView == null) {
                    temp = new GridTemp();
                    if (this.hasIcon) {
                        convertView = inflater.inflate(R.layout.le_bottomsheet_list_item_logo, null);
                        temp.icon = (ImageView) convertView.findViewById(R.id.le_bottomsheet_img_logo);
                        temp.textView = (TextView) convertView.findViewById(R.id.le_bottomsheet_text_logo);
                        temp.tailImg = (TextView) convertView.findViewById(R.id.le_bottomsheet_img_logo_tail);
                        temp.leCheckBox = (LeCheckBox) convertView
                                        .findViewById(R.id.le_bottomsheet_listview_item_logo_chkbox);
                    } else {
                        convertView = inflater.inflate(R.layout.le_bottomsheet_list_item, null);
                        temp.textView = (TextView) convertView.findViewById(R.id.le_bottomsheet_text);
                        temp.tailImg = (TextView) convertView.findViewById(R.id.le_bottomsheet_img_tail);
                        temp.leCheckBox = (LeCheckBox) convertView
                                        .findViewById(R.id.le_bottomsheet_listview_item_chkbox);
                    }

                    temp.leCheckBox.setClickable(false);
                    /* set the textview checking color before checkbox */
                    //if (itemColorMap != null && itemColorMap.containsKey(position)) {
                    //temp.textView.setTextColor(itemColorMap.get(position));
                    //}
                    if (animateToTextViewColor) {
                        temp.leCheckBox.attachAnimateToTextViewColor(temp.textView, ListItemColor);
                    }
                    convertView.setTag(temp);
                } else {
                    temp = (GridTemp) convertView.getTag();
                }

                /* Item has icon */
                if (this.hasIcon) {
                    temp.icon.setImageResource((Integer) data.get(position).get(key[0]));
                    //temp.icon.setImageDrawable((Drawable) data.get(position).get(key[0]));
                    temp.textView.setText((String) data.get(position).get(key[1]));
                    if (key.length == 4) {
                        checkIsOn = false;
                        Object obj = data.get(position).get(key[2]);
                        if (obj != null) {
                            temp.tailImg.setVisibility(View.VISIBLE);
                            temp.tailImg.setText((String) (obj));
                            temp.tailImg.setTextColor(tailTextColor);
                            temp.tailImg.setBackground((Drawable) (data.get(position).get(key[3])));
                        } else {
                            temp.tailImg.setVisibility(View.GONE);
                        }
                    }
                } else {
                    temp.textView.setText((String) data.get(position).get(key[0]));

                    if (key.length == 3) {
                        checkIsOn = false;
                        Object obj = data.get(position).get(key[1]);
                        if (obj != null) {
                            temp.tailImg.setVisibility(View.VISIBLE);
                            temp.tailImg.setText((String) (obj));
                            temp.tailImg.setTextColor(tailTextColor);
                            temp.tailImg.setBackground((Drawable) (data.get(position).get(key[2])));
                        } else {
                            temp.tailImg.setVisibility(View.GONE);
                        }
                    }
                }

                boolean isUnableItem = false;
                if (unableItemIndex != null && unableItemIndex.contains(position)) {
                    temp.textView.setTextColor(0x4a000000);
                    isUnableItem = true;
                    //[+LEUI][BUG][MOBILEP-12956][wangziming] unableItem should be 0xff000000. and checkedItem should be BTN_CFM_COLOR_BLUE.
                } else {
                    temp.textView.setTextColor(0xff000000);
                    isUnableItem = false;
                }
                if (position == checkPos && !isUnableItem && true == checkIsOn && isChangeItemColor) {
                    temp.textView.setTextColor(BTN_CFM_COLOR_BLUE);
                }
                //[-LEUI][BUG][MOBILEP-12956][wangziming] end.

                if (itemColorMap != null && itemColorMap.containsKey(position)) {
                    if (isSinglebox) {
                        temp.textView.setTextColor(itemColorMap.get(position));
                    }
                }

                /* Listview without CheckBox */
                if (false == checkIsOn) {
                    temp.leCheckBox.setVisibility(View.GONE);
                    return convertView;
                }

                /* Listview has CheckBox */
                if (true == checkIsOn && checkPos != -1 && isSinglebox) {
                    if (checkPos != position && temp.leCheckBox.isChecked()) {
                        temp.leCheckBox.setChecked(false);
                    } else if (position == checkPos && !temp.leCheckBox.isChecked()) {
                        if (isUnableItem) {
                            temp.leCheckBox.setChecked(false);
                        } else {
                            temp.leCheckBox.setChecked(true);
                            if (temp.textView.getCurrentTextColor() != BTN_CFM_COLOR_BLUE && isChangeItemColor) {
                                /* set checkbox`s checking color */
                                temp.leCheckBox.setArrowColorWithoutBorder(ListItemColor);
                            } else if (!isChangeItemColor) {
                                temp.leCheckBox.setArrowColorWithoutBorder(temp.textView.getCurrentTextColor());
                            }
                        }
                    }
                }
                if (checkIsOn && !isSinglebox) {
                    if (checkedIndexList.contains(position)) {
                        temp.leCheckBox.setChecked(true);
                        temp.textView.setTextColor(BTN_CFM_COLOR_BLUE);
                        //temp.leCheckBox.setArrowColorWithoutBorder(ListItemColor);
                    } else {
                        temp.leCheckBox.setChecked(false);
                        if (itemColorMap != null && itemColorMap.containsKey(position)) {
                            temp.textView.setTextColor(itemColorMap.get(position));
                        } else {
                            temp.textView.setTextColor(0xff000000);
                        }
                    }
                    if (mLeBotOnClickListener != null) {
                        mLeBotOnClickListener.onCheckedClick(position, temp.leCheckBox.isChecked());
                    }
                }
            }
            return convertView;
        }

        @Override
        public boolean isEnabled(int position) {
            if (unableItemIndex != null && unableItemIndex.size() > 0) {
                for (int i = 0; i < unableItemIndex.size(); i++) {
                    if (position == unableItemIndex.get(i)) {
                        return false;
                    }
                }
            }
            return super.isEnabled(position);
        }

        @Override
        public boolean topDividerEnabled() {
            // top gapline
            return false;
        }

        @Override
        public boolean bottomDividerEnabled() {
            return false;
        }

        @Override
        public boolean dividerEnabled(int position) {
            return false;
        }

        @Override
        public int leftDividerMargin(int position) {
            return 43;
        }

        @Override
        public int rightDividerMargin(int position) {
            return 43;
        }

        @Override
        public boolean forceDrawDivider(int position) {
            if (position == data.size() - 1) {
                return false;
            }
            return true;
        }
    }

    /* change item text */
    public void setItemText(int position, String str) {
        if (str == null || position < 0 || position > adapter.data.size()) {
            return;
        }

        String adapterStr = (String) adapter.data.get(position).get(adapter.key[0]);
        int index = 0;
        if (adapter.hasIcon) {
            index = 1;
        }

        if (!str.equals(adapterStr)) {
            adapter.data.get(position).put(adapter.key[index], str);
        }
        if (listView.getFirstVisiblePosition() <= position && position <= listView.getLastVisiblePosition()) {
            View ChildView = listView.getChildAt(position - listView.getFirstVisiblePosition());
            if (ChildView != null) {
                TextView tv = (TextView) ChildView.findViewById(R.id.le_bottomsheet_text);
                tv.setText(adapterStr);
            }
        }
        if (mDialog != null && mDialog.isShowing() && adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

}