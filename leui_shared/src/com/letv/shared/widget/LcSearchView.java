package com.letv.shared.widget;

import static com.letv.shared.widget.LcSuggestionsAdapter.getColumnString;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.letv.shared.R;

public class LcSearchView extends LinearLayout {

    private static final boolean DBG = true;
    private static final String LOG_TAG = LcSearchView.class.getSimpleName();
    private final TextView mCancelTextView;
    private int mPopupItemLayoutResId;
    private int mDropDownBlurRadius;
    private Drawable mDropDownBg;
    private int mCursorResId;
    private int mMatchColor;
    private float unfocusAlpha = 1;
    private int mTrackMarginRight;

    private OnClearListener mOnClearListener;
    private OnCancelListener mOnCancelListener;
    private OnEditorActionListener mOnQueryEditorActionListener;
    private View.OnFocusChangeListener mOnTextFocusChangeListener;
    private OnSuggestionListener mOnSuggestionListener;
    private TextWatcher mTextChangerListener;
    private View mSearchTrack;
    private View mDropDownAnchor;

    private boolean mQueryRefinement;
    private ListAdapter mSuggestionsAdapter;
    private SearchableInfo mSearchable;
    private ImageView mTitleIcon;
    private ImageView mClearBtn;
    private SearchAutoComplete mEditorTextView;
    private CharSequence mQueryHint;
    private boolean mClearingFocus;
    private int mMaxWidth;

    // A weak map of drawables we've gotten from other packages, so we don't
    // load them
    // more than once.
    private final WeakHashMap<String, Drawable.ConstantState> mOutsideDrawablesCache = new WeakHashMap<String, Drawable.ConstantState>();

    private final HiddenReflector HIDDEN_METHOD = new HiddenReflector();

    private Runnable mUpdateDrawableStateRunnable = new Runnable() {
        public void run() {
            updateFocusedState();
        }
    };

    private Runnable mReleaseCursorRunnable = new Runnable() {
        public void run() {
            if (mSuggestionsAdapter != null
                    && mSuggestionsAdapter instanceof LcSuggestionsAdapter) {
                ((LcSuggestionsAdapter) mSuggestionsAdapter).changeCursor(null);
            }
        }
    };
    private boolean mAlwaysShowCancel = true;
    private CharSequence mUserQuery;
    private Bundle mAppSearchData;

    /**
     * Callback interface for selection events on suggestions. These callbacks
     * are only relevant when a SearchableInfo has been specified by
     * {@link #setSearchableInfo}.
     */
    public interface OnSuggestionListener {

        /**
         * Called when a suggestion was clicked.
         *
         * @param position the absolute position of the clicked item in the list of
         *                 suggestions.
         * @return true if the listener handles the event and wants to override
         * the default behavior of launching any intent or submitting a
         * search query specified on that item. Return false otherwise.
         */
        boolean onSuggestionClick(int position);
    }

    public interface OnClearListener {
        boolean onClear();
    }

    public interface OnCancelListener {
        boolean onCancel();
    }

    public LcSearchView(Context context) {
        this(context, null);
    }

    public LcSearchView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.Leui_SearchViewStyle);
    }

    public LcSearchView(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0);
    }

    public LcSearchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.lc_search_view, this, true);
        mSearchTrack = findViewById(R.id.lc_search_plate);
        mTitleIcon = (ImageView) findViewById(R.id.lc_search_icon);
        mEditorTextView = (SearchAutoComplete) findViewById(R.id.lc_search_src_text);
        mClearBtn = (ImageView) findViewById(R.id.lc_search_clear_btn);
        mCancelTextView = (TextView) findViewById(R.id.lc_search_text_cancel);

        mTitleIcon.setOnClickListener(mOnClickListener);
        mClearBtn.setOnClickListener(mOnClickListener);
        mCancelTextView.setOnClickListener(mOnClickListener);
        mEditorTextView.setOnClickListener(mOnClickListener);
        mEditorTextView.setOnEditorActionListener(mOnEditorActionListener);
        mEditorTextView.setOnItemClickListener(mOnItemClickListener);
        mEditorTextView.addTextChangedListener(mTextWatcher);
        mEditorTextView.setSearchView(this);
        mEditorTextView.ensureImeVisible(true);
        // Inform any listener of focus changes
        mEditorTextView.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mTitleIcon.setAlpha(unfocusAlpha);
                    mEditorTextView.setAlpha(unfocusAlpha);
                } else {
                    mTitleIcon.setAlpha(1.0f);
                    mEditorTextView.setAlpha(1.0f);
                }

                if (mOnTextFocusChangeListener != null) {
                    mOnTextFocusChangeListener.onFocusChange(LcSearchView.this,
                            hasFocus);
                }
            }
        });

        mDropDownAnchor = findViewById(mEditorTextView.getDropDownAnchor());
        if (mDropDownAnchor != null) {
            mDropDownAnchor.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top,
                        int right, int bottom, int oldLeft, int oldTop,
                        int oldRight, int oldBottom) {
                    adjustDropDownSizeAndPosition();
                }
            });
        }

        int maxWidth = -1;
        CharSequence cancelText = null;
        CharSequence queryHintText = null;
        int clearBtnResId = R.drawable.lc_icon_search_view_clear_light;
        int titleIconId = R.drawable.lc_icon_search_view_search_light;
        int searchBgId = R.drawable.lc_selector_search_view_frame_light;
        int imeOptions = -1;
        int inputType = -1;
        boolean focusable = true;

        final Resources res = getResources();

        TypedValue out = new TypedValue();
        res.getValue(R.dimen.lc_search_view_default_text_alpha, out, true);
        unfocusAlpha = out.getFloat();

        int queryTextSize = res.getDimensionPixelSize(
                R.dimen.lc_default_search_query_text_size);
        ColorStateList queryTextColor = res.getColorStateList(
                R.color.lc_search_view_query_text_color_light);
        ColorStateList queryHintTextColor = res.getColorStateList(
                R.color.lc_search_view_query_hint_text_color_light);
        int cancelTextSize = res.getDimensionPixelSize(R.dimen.lc_default_search_query_text_size);
        ColorStateList cancelTextColor = null;

        int mTrackMarginLeft = res.getDimensionPixelSize(
                R.dimen.lc_search_track_margin_left);
        mTrackMarginRight = res.getDimensionPixelSize(
                R.dimen.lc_search_track_margin_right);
        int mTrackMarginTop = res.getDimensionPixelSize(
                R.dimen.lc_search_track_margin_top);
        int mTrackMarginBottom = res.getDimensionPixelSize(R.dimen.lc_search_track_margin_bottom);
        int mTrackPaddingLeft = res.getDimensionPixelSize(R.dimen.lc_search_track_padding_left);
        int mTrackPaddingRight = res.getDimensionPixelSize(R.dimen.lc_search_track_padding_right);

        mMatchColor = res.getColor(R.color.lc_search_view_drop_down_match_text_color_light);
        mCursorResId = R.drawable.lc_shape_search_view_cursor_light;
        mDropDownBg = res.getDrawable(R.drawable.lc_search_drop_down_bg);
        mDropDownBlurRadius = 10;
        mPopupItemLayoutResId = R.layout.lc_search_dropdown_item_icons_2line_light;
        mAlwaysShowCancel = true;
        Drawable popupDivider = res.getDrawable(R.drawable.lc_search_drop_down_divider);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.LcSearchView, defStyleAttr, defStyleRes);

        a.getValue(R.styleable.LcSearchView_lcSearchUnfocusAlpha, out);
        unfocusAlpha = out.getFloat();

        queryTextSize = a.getDimensionPixelSize(R.styleable.LcSearchView_lcQueryTextSize,
                queryTextSize);
        cancelTextSize = a.getDimensionPixelSize(R.styleable.LcSearchView_lcCancelTextSize,
                cancelTextSize);
        if (a.hasValue(R.styleable.LcSearchView_lcQueryTextColor)) {
            queryTextColor = a.getColorStateList(R.styleable.LcSearchView_lcQueryTextColor);
        }
        
        if (a.hasValue(R.styleable.LcSearchView_lcQueryHintColor)) {
            queryHintTextColor = a.getColorStateList(R.styleable.LcSearchView_lcQueryHintColor);
        }
        if (a.hasValue(R.styleable.LcSearchView_lcCancelTextColor)) {
            cancelTextColor = a.getColorStateList(R.styleable.LcSearchView_lcCancelTextColor);
        } else {
            cancelTextColor = queryTextColor;
        }

        mTrackMarginBottom = a.getDimensionPixelSize(R.styleable.LcSearchView_lcInputMarginBottom,
                mTrackMarginBottom);
        mTrackMarginLeft = a.getDimensionPixelSize(R.styleable.LcSearchView_lcInputMarginLeft,
                mTrackMarginLeft);
        mTrackMarginRight = a.getDimensionPixelSize(R.styleable.LcSearchView_lcInputMarginRight,
                mTrackMarginRight);
        mTrackMarginTop = a.getDimensionPixelSize(R.styleable.LcSearchView_lcInputMarginTop,
                mTrackMarginTop);

        maxWidth = a.getDimensionPixelSize(R.styleable.LcSearchView_android_maxWidth, maxWidth);
        queryHintText = a.getText(R.styleable.LcSearchView_lcQueryHint);
        clearBtnResId = a.getResourceId(R.styleable.LcSearchView_lcClearIcon, clearBtnResId);
        titleIconId = a.getResourceId(R.styleable.LcSearchView_lcSearchIcon, titleIconId);
        searchBgId = a.getResourceId(R.styleable.LcSearchView_lcBackground, searchBgId);
        imeOptions = a.getInt(R.styleable.LcSearchView_android_imeOptions, imeOptions);
        inputType = a.getInt(R.styleable.LcSearchView_android_inputType, inputType);
        focusable = a.getBoolean(R.styleable.LcSearchView_android_focusable, focusable);

        mMatchColor = a.getColor(R.styleable.LcSearchView_lcMatchColor, mMatchColor);
        mCursorResId = a.getResourceId(R.styleable.LcSearchView_android_textCursorDrawable,
                mCursorResId);

        if (a.hasValue(R.styleable.LcSearchView_lcCancelText)) {
            cancelText = a.getString(R.styleable.LcSearchView_lcCancelText);
        }
        
        if (a.hasValue(R.styleable.LcSearchView_lcPopupBackground)) {
            mDropDownBg = a.getDrawable(R.styleable.LcSearchView_lcPopupBackground);
        }

        if (a.hasValue(R.styleable.LcSearchView_lcPopupListDivider)) {
            popupDivider = a.getDrawable(R.styleable.LcSearchView_lcPopupListDivider);
        }
        mDropDownBlurRadius = a.getDimensionPixelSize(R.styleable.LcSearchView_lcPopupBlurRadius, mDropDownBlurRadius);
        mPopupItemLayoutResId = a.getResourceId(R.styleable.LcSearchView_lcPopupItemLayout,
                mPopupItemLayoutResId);

        final int completeThreshold = a.getInt(
                R.styleable.LcSearchView_android_completionThreshold, 2);

        mAlwaysShowCancel = a.getBoolean(R.styleable.LcSearchView_lcAlwaysShowCancel, mAlwaysShowCancel);
        a.recycle();

        mEditorTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, queryTextSize);
        mEditorTextView.setTextColor(queryTextColor);
        mEditorTextView.setHintTextColor(queryHintTextColor);
        HIDDEN_METHOD.setCursorDrawableRes(mEditorTextView, mCursorResId);
        mEditorTextView.setPopupBackground(mDropDownBg);
        mEditorTextView.setPopupListDivider(popupDivider);
        mEditorTextView.setPopupBlurRadius(mDropDownBlurRadius);
        mEditorTextView.setThreshold(completeThreshold);
        
        if (mEditorTextView.hasFocus()) {
            mTitleIcon.setAlpha(1.0f);
            mEditorTextView.setAlpha(1.0f);
        } else {
            mTitleIcon.setAlpha(unfocusAlpha);
            mEditorTextView.setAlpha(unfocusAlpha);
        }

        mCancelTextView.setTextColor(cancelTextColor);
        mCancelTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, cancelTextSize);
        if (mAlwaysShowCancel) {
            mCancelTextView.setVisibility(View.VISIBLE);
        }
        mClearBtn.setImageResource(clearBtnResId);
        mTitleIcon.setImageResource(titleIconId);

        mSearchTrack.setBackgroundResource(searchBgId);
        LinearLayout.LayoutParams plateParams = (LinearLayout.LayoutParams) mSearchTrack.getLayoutParams();
        plateParams.topMargin = mTrackMarginTop;
        plateParams.bottomMargin = mTrackMarginBottom;
        plateParams.leftMargin = mTrackMarginLeft;
        plateParams.rightMargin = mAlwaysShowCancel ? 0 : mTrackMarginRight;

        LinearLayout.LayoutParams params = (LayoutParams) mTitleIcon.getLayoutParams();
        params.leftMargin = mTrackPaddingLeft;
        params = (LayoutParams) mClearBtn.getLayoutParams();
        params.rightMargin = mTrackPaddingRight;

        mCancelTextView.setPadding(mTrackMarginRight, 0, mTrackMarginRight, 0);

        if (maxWidth != -1) {
            setMaxWidth(maxWidth);
        }

        if (!TextUtils.isEmpty(queryHintText)) {
            setQueryHint(queryHintText);
        }

        if (imeOptions != -1) {
            setImeOptions(imeOptions);
        }
        if (inputType != -1) {
            setInputType(inputType);
        }
        
        if (cancelText != null) {
            mCancelTextView.setText(cancelText);
        }

        setFocusable(focusable);

        updateQueryHint();

    }

    /**
     * @hide
     */
    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        // Don't accept focus if in the middle of clearing focus
        if (mClearingFocus)
            return false;
        // Check if SearchView is focusable.
        if (!isFocusable())
            return false;
        boolean result = mEditorTextView.requestFocus(direction, previouslyFocusedRect);
        return result;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        postUpdateFocusedState();
    }

    @Override
    protected void onDetachedFromWindow() {
        removeCallbacks(mUpdateDrawableStateRunnable);
        post(mReleaseCursorRunnable);
        super.onDetachedFromWindow();
    }

    /**
     * clear query text field focus and hide keyboard
     */
    @Override
    public void clearFocus() {
        mClearingFocus = true;
        setImeVisibility(false);
        super.clearFocus();
        mEditorTextView.clearFocus();
        mClearingFocus = false;
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(LcSearchView.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(LcSearchView.class.getName());
    }

    private void adjustDropDownSizeAndPosition() {
        if (mDropDownAnchor.getWidth() > 1) {
            int anchorPadding = mSearchTrack.getPaddingLeft();
            Rect dropDownPadding = new Rect();

            if (mEditorTextView.getDropDownBackground() != null) {
                mEditorTextView.getDropDownBackground().getPadding(dropDownPadding);
            }
            int offset = anchorPadding - (dropDownPadding.left);
            mEditorTextView.setDropDownHorizontalOffset(offset);
            final int width = mDropDownAnchor.getWidth() + dropDownPadding.left
                    + dropDownPadding.right - anchorPadding;
            mEditorTextView.setDropDownWidth(width);
        }
    }

    /**
     * Sets a listener to inform when the user click the clear button.
     *
     * @param listener the listener to call when the user click the clear button.
     */
    public void setOnClearListener(OnClearListener listener) {
        mOnClearListener = listener;
    }

    /**
     * Sets a listener to inform when the user click the cancel button.
     *
     * @param listener the listener to call when the user click the cencel button.
     */
    public void setOnCancelListener(OnCancelListener listener) {
        mOnCancelListener = listener;
    }

    /**
     * Sets a listener to inform when the query text field changed.
     *
     * @param textWatcher the listener to call when the query text field changed.
     */
    public void setOnTextChangedListener(TextWatcher textWatcher) {
        mTextChangerListener = textWatcher;
    }

    /**
     * Sets a listener to inform when the focus of the query text field changes.
     *
     * @param listener the listener to inform of focus changes.
     */
    public void setOnTextFocusChangeListener(OnFocusChangeListener listener) {
        mOnTextFocusChangeListener = listener;
    }

    /**
     * Sets a listener to inform an action is performed on the query text.
     *
     * @param listener the listener to call when a action is performed on the query
     *                 text.
     */
    public void setOnEditorActionListener(OnEditorActionListener listener) {
        mOnQueryEditorActionListener = listener;
    }

    /**
     * Sets a listener to inform when a suggestion is focused or clicked.
     *
     * @param listener the listener to inform of suggestion selection events.
     */
    public void setOnSuggestionListener(OnSuggestionListener listener) {
        mOnSuggestionListener = listener;
    }
    
    public Bundle getInputExtras(boolean create) {
        return mEditorTextView.getInputExtras(create);
    }

    /**
     * Sets the IME options on the query text field.
     *
     * @param imeOptions the options to set on the query text field
     * @attr ref R.styleable#SearchView_imeOptions
     * @see android.widget.TextView#setImeOptions(int)
     */
    public void setImeOptions(int imeOptions) {
        mEditorTextView.setImeOptions(imeOptions);
    }

    /**
     * Returns the IME options set on the query text field.
     *
     * @return the ime options
     * @attr ref android.R.styleable#TextView_imeOptions
     * @see android.widget.TextView#setImeOptions(int)
     */
    public int getImeOptions() {
        return mEditorTextView.getImeOptions();
    }

    /**
     * Sets the input type on the query text field.
     *
     * @param inputType the input type to set on the query text field
     * @attr ref android.R.styleable#TextView_inputType
     * @see android.widget.TextView#setInputType(int)
     */
    public void setInputType(int inputType) {
        mEditorTextView.setInputType(inputType);
    }

    /**
     * Returns the input type set on the query text field.
     *
     * @return the input type
     * @attr ref android.R.styleable#TextView_inputType
     */
    public int getInputType() {
        return mEditorTextView.getInputType();
    }

    /**
     * Sets the text in the query box.
     */
    public void setQuery(CharSequence query) {
        mEditorTextView.setText(query);
        if (query != null) {
            mUserQuery = query;
        }
        // Move the cursor to the end
        mEditorTextView.setSelection(TextUtils.isEmpty(query) ? 0 : query.length());

    }

    /**
     * Returns the query string currently in the text field.
     *
     * @return the query string
     */
    public CharSequence getQuery() {
        return mEditorTextView.getText();
    }

    /**
     * Sets the hint text to display in the query text field.
     *
     * @param hint the hint text to display
     * @attr ref R.styleable#SearchView_queryHint
     */
    public void setQueryHint(CharSequence hint) {
        mQueryHint = hint;
        updateQueryHint();
    }

    /**
     * Gets the hint text to display in the query text field.
     *
     * @return the query hint text, if specified, null otherwise.
     * @attr ref R.styleable#SearchView_queryHint
     */
    public CharSequence getQueryHint() {
        if (mQueryHint != null) {
            return mQueryHint;
        }
        return null;
    }

    /**
     * Makes the view at most this many pixels wide
     *
     * @attr ref R.styleable#SearchView_maxWidth
     */
    public void setMaxWidth(int maxpixels) {
        mMaxWidth = maxpixels;
        requestLayout();
    }

    /**
     * Gets the specified maximum width in pixels, if set. Returns zero if no
     * maximum width was specified.
     *
     * @return the maximum width of the view
     * @attr ref R.styleable#SearchView_maxWidth
     */
    public int getMaxWidth() {
        return mMaxWidth;
    }

    /**
     * You can set a custom adapter if you wish. Otherwise the default adapter is used to
     * display the suggestions from the suggestions provider associated with the SearchableInfo.
     *
     * @see #setSearchableInfo(android.app.SearchableInfo)
     */
    public <T extends ListAdapter & Filterable> void setSuggestionsAdapter(T adapter) {
        mSuggestionsAdapter = adapter;
        mEditorTextView.setAdapter(adapter);
    }

    /**
     * Returns the adapter used for suggestions, if any.
     *
     * @return the suggestions adapter
     */
    public ListAdapter getSuggestionsAdapter() {
        return mSuggestionsAdapter;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                // If there is an upper limit, don't exceed maximum width (explicit
                // or implicit)
                if (mMaxWidth > 0) {
                    width = Math.min(mMaxWidth, width);
                } else {
                    width = Math.min(getPreferredWidth(), width);
                }
                break;
            case MeasureSpec.EXACTLY:
                // If an exact width is specified, still don't exceed any specified
                // maximum width
                if (mMaxWidth > 0) {
                    width = Math.min(mMaxWidth, width);
                }
                break;
            case MeasureSpec.UNSPECIFIED:
                // Use maximum width, if specified, else preferred width
                width = mMaxWidth > 0 ? mMaxWidth : getPreferredWidth();
                break;
        }
        widthMode = MeasureSpec.EXACTLY;
        super.onMeasure(MeasureSpec.makeMeasureSpec(width, widthMode),
                heightMeasureSpec);
    }

    private int getPreferredWidth() {
        return getContext().getResources().getDimensionPixelSize(
                R.dimen.lc_search_view_preferred_width);
    }

    public void setImeVisibility(final boolean visible) {
        if (!visible) {
            InputMethodManager imm = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null) {
                imm.hideSoftInputFromWindow(getWindowToken(), 0);
            }
        }
    }

    private boolean onItemClicked(int position) {
        if (mOnSuggestionListener == null
                || !mOnSuggestionListener.onSuggestionClick(position)) {
            dismissSuggestions();
            if (mSuggestionsAdapter instanceof CursorAdapter) {
                launchSuggestion((CursorAdapter) mSuggestionsAdapter, position, KeyEvent.KEYCODE_UNKNOWN, null);
                rewriteQueryFromSuggestion(position, (CursorAdapter) mSuggestionsAdapter);
            }
            return true;
        }
        return false;
    }

    private boolean launchSuggestion(CursorAdapter adapter, int position, int actionKey, String actionMsg) {
        Cursor c = adapter.getCursor();
        if ((c != null) && c.moveToPosition(position)) {
            Intent intent = createIntentFromSuggestion(c, actionKey, actionMsg);

            // launch the intent
            launchIntent(intent);

            return true;
        }
        return false;
    }
    
    private void launchIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        try {
            // If the intent was created from a suggestion, it will always have an explicit
            // component here.
            getContext().startActivity(intent);
        } catch (RuntimeException ex) {
            Log.e(LOG_TAG, "Failed launch activity: " + intent, ex);
        }
    }
    
    private Intent createIntentFromSuggestion(Cursor c, int actionKey, String actionMsg) {
        try {
            // use specific action if supplied, or default action if supplied, or fixed default
            String action = getColumnString(c, SearchManager.SUGGEST_COLUMN_INTENT_ACTION);

            if (action == null) {
                action = mSearchable.getSuggestIntentAction();
            }
            if (action == null) {
                action = Intent.ACTION_SEARCH;
            }

            // use specific data if supplied, or default data if supplied
            String data = getColumnString(c, SearchManager.SUGGEST_COLUMN_INTENT_DATA);
            if (data == null) {
                data = mSearchable.getSuggestIntentData();
            }
            // then, if an ID was provided, append it.
            if (data != null) {
                String id = getColumnString(c, SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
                if (id != null) {
                    data = data + "/" + Uri.encode(id);
                }
            }
            Uri dataUri = (data == null) ? null : Uri.parse(data);

            String query = getColumnString(c, SearchManager.SUGGEST_COLUMN_QUERY);
            String extraData = getColumnString(c, SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA);

            return createIntent(action, dataUri, extraData, query, actionKey, actionMsg);
        } catch (RuntimeException e ) {
            int rowNum;
            try {                       // be really paranoid now
                rowNum = c.getPosition();
            } catch (RuntimeException e2 ) {
                rowNum = -1;
            }
            Log.w(LOG_TAG, "Search suggestions cursor at row " + rowNum +
                            " returned exception.", e);
            return null;
        }
    }
    
    public void setAppSearchData(Bundle appSearchData) {
        mAppSearchData = appSearchData;
    }
    
    private Intent createIntent(String action, Uri data, String extraData, String query,
            int actionKey, String actionMsg) {
        // Now build the Intent
        Intent intent = new Intent(action);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // We need CLEAR_TOP to avoid reusing an old task that has other activities
        // on top of the one we want. We don't want to do this in in-app search though,
        // as it can be destructive to the activity stack.
        if (data != null) {
            intent.setData(data);
        }
        intent.putExtra(SearchManager.USER_QUERY, mUserQuery);
        if (query != null) {
            intent.putExtra(SearchManager.QUERY, query);
        }
        if (extraData != null) {
            intent.putExtra(SearchManager.EXTRA_DATA_KEY, extraData);
        }
        if (mAppSearchData != null) {
            intent.putExtra(SearchManager.APP_DATA, mAppSearchData);
        }
        if (actionKey != KeyEvent.KEYCODE_UNKNOWN) {
            intent.putExtra(SearchManager.ACTION_KEY, actionKey);
            intent.putExtra(SearchManager.ACTION_MSG, actionMsg);
        }
        intent.setComponent(mSearchable.getSearchActivity());
        return intent;
    }
    
    private void dismissSuggestions() {
        mEditorTextView.dismissDropDown();
    }

    /**
     * Query rewriting. show selection in edittext
     */
    private void rewriteQueryFromSuggestion(int position, CursorAdapter adapter) {
        CharSequence oldQuery = mEditorTextView.getText();

        Cursor c = adapter.getCursor();
        if (c == null) {
            return;
        }

        if (c.moveToPosition(position)) {
            // Get the new query from the suggestion.
            CharSequence newQuery = adapter.convertToString(c);
            if (newQuery != null) {
                // The suggestion rewrites the query.
                // Update the text field, without getting new suggestions.
                setQuery(newQuery);
            } else {
                // The suggestion does not rewrite the query, restore the user's
                // query.
                setQuery(oldQuery);
            }
        } else {
            // We got a bad position, restore the user's query.
            setQuery(oldQuery);
        }
    }

    public void setSearchableInfo(SearchableInfo searchable) {
        mSearchable = searchable;
        if (mSearchable != null) {
            updateSearchAutoComplete();
            updateQueryHint();
        }
    }

    /**
     * A method to wrap your data with a default layout and return a LcSuggestionsListAdapter.
     *
     * @param data A List of Maps. Each entry in the List corresponds to one row in the list. The
     *             Maps contain the data for each row, and should include all the entries specified in
     *             "from"
     * @param from A list of column names that will be added to the Map associated with each
     *             item.
     * @param to   The views that should display column in the "from" parameter. These should all be
     *             TextViews. The first N views in this list are given the values of the first N columns
     *             in the from parameter.(android.R.id.text1, android.R.id.text2, android.R.id.icon1,
     *             android.R.id.icon2 are avaliable items).
     */
    public LcSuggestionsListAdapter setSearchableInfo(List<? extends Map<String, ?>> data,
            String[] from, int[] to, boolean filterEnabled) {
        final LcSuggestionsListAdapter adapter = new LcSuggestionsListAdapter(getContext(), this,
                data,
                mPopupItemLayoutResId,
                from, to, filterEnabled);
        adapter.setMatchWordsColor(mMatchColor);
        setSuggestionsAdapter(adapter);

        return adapter;
    }

    /**
     * Updates the auto-complete text view.
     */
    private void updateSearchAutoComplete() {
        mEditorTextView.setThreshold(mSearchable.getSuggestThreshold());
        mEditorTextView.setImeOptions(mSearchable.getImeOptions());
        int inputType = mSearchable.getInputType();
        // We only touch this if the input type is set up for text (which it
        // almost certainly
        // should be, in the case of search!)
        if ((inputType & InputType.TYPE_MASK_CLASS) == InputType.TYPE_CLASS_TEXT) {
            // The existence of a suggestions authority is the proxy for
            // "suggestions
            // are available here"
            inputType &= ~InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE;
            if (mSearchable.getSuggestAuthority() != null) {
                inputType |= InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE;
                // TYPE_TEXT_FLAG_AUTO_COMPLETE means that the text editor is
                // performing
                // auto-completion based on its own semantics, which it will
                // present to the user
                // as they type. This generally means that the input method
                // should not show its
                // own candidates, and the spell checker should not be in
                // action. The text editor
                // supplies its candidates by calling
                // InputMethodManager.displayCompletions(),
                // which in turn will call
                // InputMethodSession.displayCompletions().
                inputType |= InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
            }
        }
        mEditorTextView.setInputType(inputType);
        if (mSuggestionsAdapter != null && mSuggestionsAdapter instanceof CursorAdapter) {
            ((CursorAdapter) mSuggestionsAdapter).changeCursor(null);
        }
        // attach the suggestions adapter, if suggestions are available
        // The existence of a suggestions authority is the proxy for
        // "suggestions available here"
        if (mSearchable.getSuggestAuthority() != null) {
            LcSuggestionsAdapter adapter = new LcSuggestionsAdapter(getContext(),
                    mPopupItemLayoutResId, this,
                    mSearchable, mOutsideDrawablesCache);
            mSuggestionsAdapter = adapter;
            adapter.setMatchWordsColor(mMatchColor);
            mEditorTextView.setAdapter((LcSuggestionsAdapter) mSuggestionsAdapter);
            ((LcSuggestionsAdapter) mSuggestionsAdapter)
                    .setQueryRefinement(mQueryRefinement ? LcSuggestionsAdapter.REFINE_ALL
                            : LcSuggestionsAdapter.REFINE_BY_ENTRY);
        }
    }

    /**
     * Specifies if a query refinement button should be displayed alongside each
     * suggestion or if it should depend on the flags set in the individual
     * items retrieved from the suggestions provider. Clicking on the query
     * refinement button will replace the text in the query text field with the
     * text from the suggestion. This flag only takes effect if a SearchableInfo
     * has been specified with {@link #setSearchableInfo(android.app.SearchableInfo)} and
     * not when using a custom adapter.
     *
     * @param enable true if all items should have a query refinement button, false
     *               if only those items that have a query refinement flag set
     *               should have the button.
     * @see android.app.SearchManager#SUGGEST_COLUMN_FLAGS
     * @see android.app.SearchManager#FLAG_QUERY_REFINEMENT
     */
    public void setQueryRefinementEnabled(boolean enable) {
        mQueryRefinement = enable;
        if (mSuggestionsAdapter instanceof LcSuggestionsAdapter) {
            ((LcSuggestionsAdapter) mSuggestionsAdapter)
                    .setQueryRefinement(enable ? LcSuggestionsAdapter.REFINE_ALL
                            : LcSuggestionsAdapter.REFINE_BY_ENTRY);
        }
    }

    /**
     * Returns whether query refinement is enabled for all items or only
     * specific ones.
     *
     * @return true if enabled for all items, false otherwise.
     */
    public boolean isQueryRefinementEnabled() {
        return mQueryRefinement;
    }

    /**
     * Called by the SuggestionsAdapter
     *
     * @hide
     */
    /* package */void onQueryRefine(CharSequence queryText) {
        setQuery(queryText);
    }

    private void postUpdateFocusedState() {
        post(mUpdateDrawableStateRunnable);
    }

    private void updateFocusedState() {
        boolean focused = mEditorTextView.hasFocus();
        mSearchTrack.getBackground().setState(
                focused ? FOCUSED_STATE_SET : EMPTY_STATE_SET);
        invalidate();
    }

    void onTextFocusChanged() {
        updateButtonState();
        // Delayed update to make sure that the focus has settled down and
        // window focus changes
        // don't affect it. A synchronous update was not working.
        postUpdateFocusedState();
        if (mEditorTextView.hasFocus()) {
            forceSuggestionQuery();
        }
    }

    private void forceSuggestionQuery() {
        mEditorTextView.doBeforeTextChanged();
        mEditorTextView.doAfterTextChanged();
    }
    
    public boolean requestQueryTextFocus() {
        return mEditorTextView.requestFocus();
    }

    private void updateQueryHint() {
        if (mQueryHint != null) {
            mEditorTextView.setHint(mQueryHint);
        } else {
            mEditorTextView.setHint("");
        }
    }

    private void onTextChanged(CharSequence newText) {
        CharSequence text = mEditorTextView.getText();
        mUserQuery = text;
        updateButtonState();
    }

    private void updateButtonState() {
        final boolean hasText = !TextUtils.isEmpty(mEditorTextView.getText());
        mClearBtn.setVisibility(hasText ? VISIBLE : GONE);
        final CharSequence cancelText = mCancelTextView.getText();
        final boolean hasCancelText = !TextUtils.isEmpty(cancelText);
        mCancelTextView.setVisibility((hasText || mAlwaysShowCancel) 
                && hasCancelText ? VISIBLE : GONE);

        LinearLayout.LayoutParams plateParams = (LinearLayout.LayoutParams) mSearchTrack
                .getLayoutParams();
        if (hasText) {
            mTitleIcon.setAlpha(1.0f);
            mEditorTextView.setAlpha(1.0f);
            if (!mAlwaysShowCancel && hasCancelText) {
                plateParams.rightMargin = 0;
            }
        } else {
            mTitleIcon.setAlpha(unfocusAlpha);
            mEditorTextView.setAlpha(unfocusAlpha);
            if (!mAlwaysShowCancel && hasCancelText) {
                plateParams.rightMargin = mTrackMarginRight;
            }
        }
    }

    private void onClearClicked() {
        mEditorTextView.setText("");

        if (mOnClearListener != null) {
            mOnClearListener.onClear();
        }
    }

    private void onCancelClicked() {

        boolean hanled = false;
        if (mOnCancelListener != null) {
            hanled = mOnCancelListener.onCancel();
        }

        if (!hanled) {
            mEditorTextView.setText("");
            clearFocus();
        }
    }
    
    public void setThreshold(int threshold) {
        mEditorTextView.setThreshold(threshold);
    }

    private final OnClickListener mOnClickListener = new OnClickListener() {

        public void onClick(final View v) {
            if (v == mClearBtn) {
                onClearClicked();
            } else if (v == mEditorTextView) {
                forceSuggestionQuery();
            } else if (v == mCancelTextView) {
                onCancelClicked();
            }
        }
    };

    private final OnEditorActionListener mOnEditorActionListener = new OnEditorActionListener() {

        /**
         * Called when the input method default action key is pressed.
         */
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            clearFocus();
            if (mOnQueryEditorActionListener != null) {
                return mOnQueryEditorActionListener.onEditorAction(v, actionId,
                        event);
            }

            return true;
        }
    };

    static boolean isLandscapeMode(Context context) {
        return context.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE;
    }

    private final OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        /**
         * Implements OnItemClickListener
         */
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            if (DBG)
                Log.d(LOG_TAG, "onItemClick() position " + position);
            LcSearchView.this.onItemClicked(position);
        }
    };

    /**
     * Callback to watch the text field for empty/non-empty
     */
    private TextWatcher mTextWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int before,
                int after) {
            if (mTextChangerListener != null) {
                mTextChangerListener.beforeTextChanged(s, start, before, after);
            }
        }

        public void onTextChanged(CharSequence s, int start, int before,
                int after) {
            LcSearchView.this.onTextChanged(s);

            if (mTextChangerListener != null) {
                mTextChangerListener.onTextChanged(s, start, before, after);
            }
        }

        public void afterTextChanged(Editable s) {
            if (mTextChangerListener != null) {
                mTextChangerListener.afterTextChanged(s);
            }
        }
    };

    /**
     * Local subclass for AutoCompleteTextView.
     *
     * @hide
     */
    public static class SearchAutoComplete extends LcAutoCompleteTextView {

        private int mThreshold;
        private LcSearchView mSearchView;

        public SearchAutoComplete(Context context) {
            super(context);
            mThreshold = getThreshold();
        }

        public SearchAutoComplete(Context context, AttributeSet attrs) {
            super(context, attrs);
            mThreshold = getThreshold();
        }

        public SearchAutoComplete(Context context, AttributeSet attrs,
                int defStyle) {
            super(context, attrs, defStyle);
            mThreshold = getThreshold();
        }

        void setSearchView(LcSearchView searchView) {
            mSearchView = searchView;
        }

        @Override
        public void setThreshold(int threshold) {
            super.setThreshold(threshold);
            mThreshold = threshold;
        }

        /**
         * Returns true if the text field is empty, or contains only whitespace.
         */
        private boolean isEmpty() {
            return TextUtils.getTrimmedLength(getText()) == 0;
        }

        /**
         * We override this method to avoid replacing the query box text when a
         * suggestion is clicked.
         */
        @Override
        protected void replaceText(CharSequence text) {
        }

        /**
         * We override this method to avoid an extra onItemClick being called on
         * the drop-down's OnItemClickListener by
         * {@link LcAutoCompleteTextView#onKeyUp(int, android.view.KeyEvent)} when an item is
         * clicked with the trackball.
         */
        @Override
        public void performCompletion() {
        }

        /**
         * We override this method to be sure and show the soft keyboard if
         * appropriate when the TextView has focus.
         */
        @Override
        public void onWindowFocusChanged(boolean hasWindowFocus) {
            super.onWindowFocusChanged(hasWindowFocus);

            if (hasWindowFocus && mSearchView.hasFocus()
                    && getVisibility() == VISIBLE) {
                InputMethodManager inputManager = (InputMethodManager) getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(this, 0);
                // If in landscape mode, then make sure that
                // the ime is in front of the dropdown.
                if (isLandscapeMode(getContext())) {
                    ensureImeVisible(true);
                }
            }
        }

        @Override
        protected void onFocusChanged(boolean focused, int direction,
                Rect previouslyFocusedRect) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
            mSearchView.onTextFocusChanged();
        }

        /**
         * We override this method so that we can allow a threshold of zero,
         * which ACTV does not.
         */
        @Override
        public boolean enoughToFilter() {
            return mThreshold <= 0 || super.enoughToFilter();
        }

        @Override
        public boolean onKeyPreIme(int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                // special case for the back key, we do not even try to send it
                // to the drop down list but instead, consume it immediately
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getRepeatCount() == 0) {
                    KeyEvent.DispatcherState state = getKeyDispatcherState();
                    if (state != null) {
                        state.startTracking(event, this);
                    }
                    return true;
                } else if (event.getAction() == KeyEvent.ACTION_UP) {
                    KeyEvent.DispatcherState state = getKeyDispatcherState();
                    if (state != null) {
                        state.handleUpEvent(event);
                    }
                    if (event.isTracking() && !event.isCanceled()) {
                        mSearchView.clearFocus();
                        mSearchView.setImeVisibility(false);
                        return true;
                    }
                }
            }
            return super.onKeyPreIme(keyCode, event);
        }
    }

    private class HiddenReflector {
        Field mCursorDrawableRes;

        public HiddenReflector() {

            try {
                mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
                mCursorDrawableRes.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        void setCursorDrawableRes(TextView textView, int resId) {
            try {
                mCursorDrawableRes.set(textView, resId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
