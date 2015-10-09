package com.letv.shared.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.letv.shared.R;

public class LeScrollStripTabWidget extends BlurLinearLayout implements OnFocusChangeListener {

    private static final int DOUBLE_CLICK_TIME_MS = 350;
    private long mLastClickTime = -1l;

    private OnTabClickListener mTabClickListener;

    // This value will be set to 0 as soon as the first tab is added to TabHost.
    private int mSelectedTab = -1;

    // Scroll strip
    private boolean mScrollStripMoved;
    private Drawable mScrollStripDrawable;
    private int mScrollStripHeight;
    private Rect mScrollStripBoundsRect;
    private float mScrollStripOffset;
    
    private boolean mScrollStripLenChangeable;
    private int mScrollStripLenExtension;

    private boolean mDrawScrollStripWhenTabChangeByClick;

    // Top strip
    private Drawable mTopStripDrawable;
    private int mTopStripHeight;

    // Bottom strip
    private Drawable mBottomStripDrawable;
    private int mBottomStripHeight;

    public LeScrollStripTabWidget(Context context) {
        this(context, null);
    }

    public LeScrollStripTabWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeScrollStripTabWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        /*
         * if (attrs != null) { TypedArray ta =
         * context.obtainStyledAttributes(attrs,
         * R.styleable.LeScrollStripTabWidget); int scrollStripRes =
         * ta.getResourceId(R.styleable.LeScrollStripTabWidget_scrollStrip, -1);
         * if (scrollStripRes != -1) { mScrollStripDrawable =
         * getContext().getResources().getDrawable(scrollStripRes); } int
         * scrollStripHeight = (int) ta.getDimension(
         * R.styleable.LeScrollStripTabWidget_scrollStripHeight, 0); if
         * (scrollStripHeight != 0 && mScrollStripDrawable != null) {
         * mScrollStripHeight = scrollStripHeight; } ta.recycle(); }
         */

        initTabWidget();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mScrollStripMoved = true;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (mSelectedTab == -1) {
            return i;
        } else {
            // Always draw the selected tab last, so that drop shadows are drawn
            // in the correct z-order.
            if (i == childCount - 1) {
                return mSelectedTab;
            } else if (i >= mSelectedTab) {
                return i + 1;
            } else {
                return i;
            }
        }
    }

    private void initTabWidget() {
        setChildrenDrawingOrderEnabled(true);

        this.setClickable(true);

        // Deal with focus, as we don't want the focus to go by default
        // to a tab other than the current tab
        setFocusable(true);
        setOnFocusChangeListener(this);

        mScrollStripBoundsRect = new Rect();

        this.setDividerDrawable(null);
        this.setBlurEnabled(false);
        
        mScrollStripLenExtension = getResources().getDimensionPixelSize(R.dimen.le_scroll_strip_tab_widget_scroll_strip_len_extension);
    }

    /**
     * Returns the tab indicator view at the given index.
     *
     * @param index the zero-based index of the tab indicator view to return
     * @return the tab indicator view at the given index
     */
    public View getChildTabViewAt(int index) {
        return getChildAt(index);
    }

    /**
     * Returns the number of tab indicator views.
     * 
     * @return the number of tab indicator views.
     */
    public int getTabCount() {
        return getChildCount();
    }
    
    public void setScrollStripLenChangeable(boolean stripLenChangeable) {
        mScrollStripLenChangeable = stripLenChangeable;
    }
    
    public boolean getScrollStripLenChangeable() {
        return mScrollStripLenChangeable;
    }
    
    public void setScrollStripLenExtensionPx(int extension) {
        mScrollStripLenExtension = extension;
    }
    
    public int getScrollStripLenExtensionPx() {
        return mScrollStripLenExtension;
    }

    /**
     * Sets the drawable to use as a divider between the tab indicators.
     * 
     * @param drawable the divider drawable
     */
    @Override
    public void setDividerDrawable(Drawable drawable) {
        super.setDividerDrawable(drawable);
    }

    /**
     * Sets the drawable to use as a divider between the tab indicators.
     * 
     * @param resId the resource identifier of the drawable to use as a divider.
     */
    public void setDividerDrawable(int resId) {
        setDividerDrawable(getResources().getDrawable(resId));
    }

    @Override
    public void childDrawableStateChanged(View child) {
        if (getTabCount() > 0 && child == getChildTabViewAt(mSelectedTab)) {
            // To make sure that the bottom strip is redrawn
            invalidate();
        }
        super.childDrawableStateChanged(child);
    }
    
    private void computeStripBoundsRect() {
        if (mScrollStripLenChangeable) {
            computeStripBoundsRect_dynamicLen();
        } else {
            computeStripBoundsRect_fixLen();
        }
    }
    

    private void computeStripBoundsRect_fixLen() {
        final View selectedChild = getChildTabViewAt(mSelectedTab);

        int selectedChildLeft, selectedChildRight, selectedChildWidth;
        int left, right;

        selectedChildLeft = selectedChild.getLeft();
        selectedChildRight = selectedChild.getRight();
        selectedChildWidth = selectedChild.getWidth();

        left = (int) (selectedChildLeft + selectedChildWidth * mScrollStripOffset);
        if (mSelectedTab == 0 && left < selectedChildLeft) {
            left = selectedChildLeft;
        }

        right = left + selectedChildWidth;
        if (mSelectedTab == getChildCount() - 1 && right > selectedChildRight) {
            right = selectedChildRight;
        }

        mScrollStripBoundsRect.set(left, getHeight() - mScrollStripHeight, right, getHeight());
    }
    
    private int getNextTab() {
        int nextTab;
        if (mScrollStripOffset > 0) {
            nextTab = mSelectedTab + 1;
        } else {
            nextTab = mSelectedTab - 1;
        }
        
        nextTab = Math.max(nextTab, 0);
        nextTab = Math.min(nextTab, getChildCount() - 1);
        
        return nextTab;
    }
    
    private void computeStripBoundsRect_dynamicLen() {
        ViewGroup selectedVg, nextVg;
        View selectedText, nextText;
        
        selectedVg = (ViewGroup)getChildTabViewAt(mSelectedTab);
        selectedText = selectedVg.getChildAt(0);
        
        nextVg = (ViewGroup)getChildTabViewAt(getNextTab());
        nextText = nextVg.getChildAt(0);
        
        int selectedVgLeft, selectedVgRight;
        
        selectedVgLeft = selectedVg.getLeft();
        selectedVgRight = selectedVg.getRight();
        
        int selectedTextLeft, selectedTextRight;
        int nextTextLeft, nextTextRight;
        int left, right;

        selectedTextLeft = selectedText.getLeft() + selectedVgLeft;
        selectedTextRight = selectedText.getRight() + selectedVgLeft;
        
        nextTextLeft = nextText.getLeft() + nextVg.getLeft();
        nextTextRight = nextText.getRight() + nextVg.getLeft();
        
        left = (int) (selectedTextLeft + Math.abs(nextTextLeft - selectedTextLeft) * mScrollStripOffset) - mScrollStripLenExtension;
        if (mSelectedTab == 0 && left < selectedVgLeft) {
            left = selectedVgLeft;
        }

        right = (int) (selectedTextRight + Math.abs(nextTextRight - selectedTextRight) * mScrollStripOffset) + mScrollStripLenExtension;
        if (mSelectedTab == getChildCount() - 1 && right > selectedVgRight) {
            right = selectedVgRight;
        }

        mScrollStripBoundsRect.set(left, getHeight() - mScrollStripHeight, right, getHeight());
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        // Draw top strip
        if (mTopStripDrawable != null && mTopStripHeight != 0) {
            mTopStripDrawable
                    .setBounds(getLeft(), getTop(), getRight(), getTop() + mTopStripHeight);
            mTopStripDrawable.draw(canvas);
        }

        // Draw bottom strip
        if (mBottomStripDrawable != null && mBottomStripHeight != 0) {
            mBottomStripDrawable.setBounds(getLeft(), getHeight() - mBottomStripHeight, getRight(),
                    getHeight());
            mBottomStripDrawable.draw(canvas);
        }

        // Do nothing if there are no tabs.
        if (getTabCount() == 0 || mSelectedTab == -1)
            return;
        
        if (mScrollStripDrawable != null && mScrollStripHeight != 0) {
            final View selectedChild = getChildTabViewAt(mSelectedTab);
            final Drawable strip = mScrollStripDrawable;
            strip.setState(selectedChild.getDrawableState());

            if (mScrollStripMoved) {
                computeStripBoundsRect();
                mScrollStripMoved = false;
            }
            strip.setBounds(mScrollStripBoundsRect);
            strip.draw(canvas);
        }
    }

    /**
     * Sets the current tab. This method is used to bring a tab to the front of
     * the Widget, and is used to post to the rest of the UI that a different
     * tab has been brought to the foreground. Note, this is separate from the
     * traditional "focus" that is employed from the view logic. For instance,
     * if we have a list in a tabbed view, a user may be navigating up and down
     * the list, moving the UI focus (orange highlighting) through the list
     * items. The cursor movement does not effect the "selected" tab though,
     * because what is being scrolled through is all on the same tab. The
     * selected tab only changes when we navigate between tabs (moving from the
     * list view to the next tabbed view, in this example). To move both the
     * focus AND the selected tab at once, please use {@link #setCurrentTab}.
     * Normally, the view logic takes care of adjusting the focus, so unless
     * you're circumventing the UI, you'll probably just focus your interest
     * here.
     *
     * @param index The tab that you want to indicate as the selected tab (tab
     *            brought to the front of the widget)
     * @see #focusCurrentTab
     */
    public void setCurrentTab(int index) {
        if (index < 0 || index >= getTabCount() || index == mSelectedTab) {
            return;
        }

        if (mSelectedTab != -1) {
            getChildTabViewAt(mSelectedTab).setSelected(false);
        }
        mSelectedTab = index;
        getChildTabViewAt(mSelectedTab).setSelected(true);
        mScrollStripMoved = true;

        if (isShown()) {
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
        }
    }

    public void setCurrentTabButNotInvalidateScrollStrip(int index) {
        if (index < 0 || index >= getTabCount() || index == mSelectedTab) {
            return;
        }

        if (mSelectedTab != -1) {
            getChildTabViewAt(mSelectedTab).setSelected(false);
        }
        mSelectedTab = index;
        getChildTabViewAt(mSelectedTab).setSelected(true);
        mScrollStripMoved = false;

        if (isShown()) {
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
        }
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        onPopulateAccessibilityEvent(event);
        // Dispatch only to the selected tab.
        if (mSelectedTab != -1) {
            View tabView = getChildTabViewAt(mSelectedTab);
            if (tabView != null && tabView.getVisibility() == VISIBLE) {
                return tabView.dispatchPopulateAccessibilityEvent(event);
            }
        }
        return false;
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(LeScrollStripTabWidget.class.getName());
        event.setItemCount(getTabCount());
        if (mSelectedTab != -1) {
            event.setCurrentItemIndex(mSelectedTab);
        }
    }

    @Override
    public void sendAccessibilityEventUnchecked(AccessibilityEvent event) {
        // this class fires events only when tabs are focused or selected
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_FOCUSED && isFocused()) {
            event.recycle();
            return;
        }
        super.sendAccessibilityEventUnchecked(event);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(LeScrollStripTabWidget.class.getName());
    }

    /**
     * Sets the current tab and focuses the UI on it. This method makes sure
     * that the focused tab matches the selected tab, normally at
     * {@link #setCurrentTab}. Normally this would not be an issue if we go
     * through the UI, since the UI is responsible for calling
     * TabWidget.onFocusChanged(), but in the case where we are selecting the
     * tab programmatically, we'll need to make sure focus keeps up.
     *
     * @param index The tab that you want focused (highlighted in orange) and
     *            selected (tab brought to the front of the widget)
     * @see #setCurrentTab
     */
    public void focusCurrentTab(int index) {
        final int oldTab = mSelectedTab;

        // set the tab
        setCurrentTab(index);

        // change the focus if applicable.
        if (oldTab != index) {
            getChildTabViewAt(index).requestFocus();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        final int count = getTabCount();
        for (int i = 0; i < count; i++) {
            View child = getChildTabViewAt(i);
            child.setEnabled(enabled);
        }
    }

    @Override
    public void addView(View child) {
        if (child.getLayoutParams() == null) {
            final LinearLayout.LayoutParams lp = new LayoutParams(
                    0,
                    ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            lp.setMargins(0, 0, 0, 0);
            child.setLayoutParams(lp);
        }

        // Ensure you can navigate to the tab with the keyboard, and you can
        // touch it
        child.setFocusable(true);
        child.setClickable(true);

        super.addView(child);

        // TODO: detect this via geometry with a tabwidget listener rather
        // than potentially interfere with the view's listener
        child.setOnClickListener(new TabClickListener(getTabCount() - 1));
        child.setOnFocusChangeListener(this);
    }

    @Override
    protected void onFinishInflate() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.setFocusable(true);
            child.setClickable(true);

            child.setOnClickListener(new TabClickListener(i));
            child.setOnFocusChangeListener(this);
        }

        super.onFinishInflate();
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
        mSelectedTab = -1;
    }

    /** {@inheritDoc} */
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == this && hasFocus && getTabCount() > 0) {
            getChildTabViewAt(mSelectedTab).requestFocus();
            return;
        }

        if (hasFocus) {
            int i = 0;
            int numTabs = getTabCount();
            while (i < numTabs) {
                if (getChildTabViewAt(i) == v) {
                    setCurrentTab(i);

                    if (isShown()) {
                        // a tab is focused so send an event to announce the tab
                        // widget state
                        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
                    }
                    break;
                }
                i++;
            }
        }
    }

    // registered with each tab indicator so we can notify tab host
    private class TabClickListener implements OnClickListener {

        private final int mTabIndex;

        private TabClickListener(int tabIndex) {
            mTabIndex = tabIndex;
        }

        public void onClick(View v) {
            int oldSelectedTab = mSelectedTab;

            if (mDrawScrollStripWhenTabChangeByClick) {
                setCurrentTab(mTabIndex);
            } else {
                setCurrentTabButNotInvalidateScrollStrip(mTabIndex);
            }

            if (mTabClickListener != null) {
                if (mTabIndex != oldSelectedTab) {
                    mTabClickListener.onClickTabChanged(mTabIndex);
                    mLastClickTime = -1l;
                } else {
                    mTabClickListener.onClickTabNotChanged(mTabIndex);

                    long currentTimeMs = System.currentTimeMillis();
                    if (currentTimeMs - mLastClickTime <= DOUBLE_CLICK_TIME_MS) {
                        mLastClickTime = -1l;

                        mTabClickListener.onDoubleClickTabNotChanged(mTabIndex);
                    } else {
                        mLastClickTime = currentTimeMs;
                    }
                }
            }
        }
    }

    // -------------- Set listeners --------------
    public interface OnTabClickListener {

        // a new tab is clicked which will cause tab changing
        void onClickTabChanged(int tabIndex);

        // a old tab is clicked
        void onClickTabNotChanged(int tabIndex);

        // a old tab is double clicked in 350ms
        void onDoubleClickTabNotChanged(int tabIndex);
    }

    public void setOnTabClickListener(OnTabClickListener l) {
        mTabClickListener = l;
    }

    // -------------- Scroll strip --------------
    public void setScrollStripDrawable(int resId) {
        setScrollStripDrawable(getContext().getResources().getDrawable(resId));
    }

    public void setScrollStripDrawable(Drawable drawable) {
        mScrollStripDrawable = drawable;
        // requestLayout();
        invalidate();
    }

    public void setScrollStripHeight(int height) {
        if (height < 0)
            return;
        mScrollStripHeight = height;
        invalidate();
    }

    public void setScrollStripHeightResId(int resId) {
        mScrollStripHeight = getResources().getDimensionPixelSize(resId);
        invalidate();
    }

    private void setScrollStripOffset(float offset) {
        if (offset == mScrollStripOffset)
            return;

        mScrollStripOffset = offset;
        mScrollStripMoved = true;

        invalidate();
    }

    public void setScrollStripOffset(int tab, float offset) {
        if (tab < 0 || tab >= getChildCount()) {
            return;
        }

        offset += tab - mSelectedTab;

        setScrollStripOffset(offset);
    }

    // -------------- Top strip --------------
    public void setTopStripDrawable(int resId) {
        setTopStripDrawable(getContext().getResources().getDrawable(resId));
    }

    public void setTopStripDrawable(Drawable drawable) {
        mTopStripDrawable = drawable;
        invalidate();
    }

    public void setTopStripHeight(int height) {
        mTopStripHeight = height;
        invalidate();
    }

    public void setTopStripHeightResId(int resId) {
        mTopStripHeight = getResources().getDimensionPixelSize(resId);
        invalidate();
    }

    // -------------- Bottom strip --------------
    public void setBottomStripDrawable(int resId) {
        setBottomStripDrawable(getContext().getResources().getDrawable(resId));
    }

    public void setBottomStripDrawable(Drawable drawable) {
        mBottomStripDrawable = drawable;
        invalidate();
    }

    public void setBottomStripHeight(int height) {
        mBottomStripHeight = height;
        invalidate();
    }

    public void setBottomStripHeightResId(int resId) {
        mBottomStripHeight = getResources().getDimensionPixelSize(resId);
        invalidate();
    }

    // -------------- When tab is changed by clicking, whether draw scroll strip --------------
    public void setDrawScrollStripWhenTabChangeByClick(boolean drawScrollStripWhenTabChangeByClick) {
        mDrawScrollStripWhenTabChangeByClick = drawScrollStripWhenTabChangeByClick;
    }
    
    public void addView(String title) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ViewGroup vGroup = (ViewGroup)inflater.inflate(R.layout.le_scroll_strip_tab_widget_tab, this, false);
        
        if (vGroup == null) 
            return;
        
        View textView = vGroup.getChildAt(0);
        if (textView != null && textView instanceof TextView) {
            ((TextView)textView).setText(title);
        }
        
        addView(vGroup);   
    }
}
