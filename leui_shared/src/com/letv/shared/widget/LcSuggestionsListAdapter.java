package com.letv.shared.widget;

/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.letv.shared.R;

/**
 * Provides the contents for the suggestion drop-down list.in {@link com.letv.shared.widget.LcSearchView}.
 */
public class LcSuggestionsListAdapter extends BaseAdapter implements Filterable {

    private static final boolean DBG = false;
    private static final String LOG_TAG = "SuggestionsAdapter";

    private LcSearchView mSearchView;
    private Context mProviderContext;

    // URL color
    private ColorStateList mUrlColor;

    private ColorStateList mMatchWordsColor;

    private int[] mTo;
    private String[] mFrom;
    private ViewBinder mViewBinder;

    private List<? extends Map<String, ?>> mData;

    private int mResource;
    private int mDropDownResource;
    private LayoutInflater mInflater;

    private SimpleFilter mFilter;
    private ArrayList<Map<String, ?>> mUnfilteredData;
	private boolean mFilterEnabled = true;

    /**
     * Constructor
     * 
     * @param context The context where the View associated with this SimpleAdapter is running
     * @param data A List of Maps. Each entry in the List corresponds to one row in the list. The
     *        Maps contain the data for each row, and should include all the entries specified in
     *        "from"
     * @param resource Resource identifier of a view layout that defines the views for this list
     *        item. The layout file should include at least those named views defined in "to"
     * @param from A list of column names that will be added to the Map associated with each
     *        item.
     * @param to The views that should display column in the "from" parameter. These should all be
     *        TextViews. The first N views in this list are given the values of the first N columns
     *        in the from parameter.
     * @param titleColumn using to indicate getTitle(int position) method to return your custom data, it's 
     * 		  column value type must be String.
     */
    public LcSuggestionsListAdapter(Context context, LcSearchView view, List<? extends Map<String, ?>> data,
            int resource, String[] from, int[] to) {
        mProviderContext = context;
        mData = data;
        mResource = mDropDownResource = resource;
        mFrom = from;
        mTo = to;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSearchView = view;
    }
    
    public LcSuggestionsListAdapter(Context context, LcSearchView view, List<? extends Map<String, ?>> data,
            int resource, String[] from, int[] to, boolean filterEnabled) {
    	this(context, view, data, resource, from, to);
    	mFilterEnabled = filterEnabled;
    }
    
    public boolean isAutoFilterEnabled() {
    	return mFilterEnabled;
    }
    /**
     * @see android.widget.Adapter#getCount()
     */
    public int getCount() {
        return mData.size();
    }

    /**
     * @see android.widget.Adapter#getItem(int)
     */
    public Object getItem(int position) {
        return mData.get(position);
    }

    /**
     * @see android.widget.Adapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mResource);
    }

    private View createViewFromResource(int position, View convertView,
            ViewGroup parent, int resource) {
        View v;
        if (convertView == null) {
            v = mInflater.inflate(resource, parent, false);
        } else {
            v = convertView;
        }

        bindView(position, v);

        return v;
    }

    /**
     * <p>Sets the layout resource to create the drop down views.</p>
     *
     * @param resource the layout resource defining the drop down views
     * @see #getDropDownView(int, android.view.View, android.view.ViewGroup)
     */
    public void setDropDownViewResource(int resource) {
        this.mDropDownResource = resource;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mDropDownResource);
    }

    private void bindView(int position, View view) {
        final Map dataSet = mData.get(position);
        if (dataSet == null) {
            return;
        }

        final ViewBinder binder = mViewBinder;
        final String[] from = mFrom;
        final int[] to = mTo;
        final int count = to.length;

        for (int i = 0; i < count; i++) {
            final View v = view.findViewById(to[i]);
            if (v != null) {
                final Object data = dataSet.get(from[i]);
                String text = data == null ? "" : data.toString();
                if (text == null) {
                    text = "";
                }

                boolean bound = false;
                if (binder != null) {
                    bound = binder.setViewValue(v, data, text);
                }

                if (!bound) {
                    if (v instanceof Checkable) {
                        if (data instanceof Boolean) {
                            ((Checkable) v).setChecked((Boolean) data);
                        } else if (v instanceof TextView) {
                            // Note: keep the instanceof TextView check at the bottom of these
                            // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                            setViewText((TextView) v, formatMatchWords(text));
                        } else {
                            throw new IllegalStateException(v.getClass().getName() +
                                    " should be bound to a Boolean, not a " +
                                    (data == null ? "<unknown type>" : data.getClass()));
                        }
                    } else if (v instanceof TextView) {
                        // Note: keep the instanceof TextView check at the bottom of these
                        // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                        setViewText((TextView) v, formatMatchWords(text));
                    } else if (v instanceof ImageView) {
                        if (data instanceof Integer) {
                            setViewImage((ImageView) v, (Integer) data);                            
                        } else {
                            setViewImage((ImageView) v, text);
                        }
                    } else {
                        throw new IllegalStateException(v.getClass().getName() + " is not a " +
                                " view that can be bounds by this SimpleAdapter");
                    }
                }
            }
        }
    }

    /**
     * Returns the {@link com.letv.shared.widget.LcSuggestionsListAdapter.ViewBinder} used to bind data to views.
     *
     * @return a ViewBinder or null if the binder does not exist
     *
     */
    public ViewBinder getViewBinder() {
        return mViewBinder;
    }

    /**
     * Sets the binder used to bind data to views.
     *
     * @param viewBinder the binder used to bind data to views, can be null to
     *        remove the existing binder
     *
     * @see #getViewBinder()
     */
    public void setViewBinder(ViewBinder viewBinder) {
        mViewBinder = viewBinder;
    }

    /**
     * Called by bindView() to set the image for an ImageView but only if
     * there is no existing ViewBinder or if the existing ViewBinder cannot
     * handle binding to an ImageView.
     *
     * This method is called instead of {@link #setViewImage(android.widget.ImageView, String)}
     * if the supplied data is an int or Integer.
     *
     * @param v ImageView to receive an image
     * @param value the value retrieved from the data set
     *
     * @see #setViewImage(android.widget.ImageView, String)
     */
    public void setViewImage(ImageView v, int value) {
        v.setImageResource(value);
    }

    /**
     * Called by bindView() to set the image for an ImageView but only if
     * there is no existing ViewBinder or if the existing ViewBinder cannot
     * handle binding to an ImageView.
     *
     * By default, the value will be treated as an image resource. If the
     * value cannot be used as an image resource, the value is used as an
     * image Uri.
     *
     * This method is called instead of {@link #setViewImage(android.widget.ImageView, int)}
     * if the supplied data is not an int or Integer.
     *
     * @param v ImageView to receive an image
     * @param value the value retrieved from the data set
     *
     * @see #setViewImage(android.widget.ImageView, int)
     */
    public void setViewImage(ImageView v, String value) {
        try {
            v.setImageResource(Integer.parseInt(value));
        } catch (NumberFormatException nfe) {
            v.setImageURI(Uri.parse(value));
        }
    }

    /**
     * Called by bindView() to set the text for a TextView but only if
     * there is no existing ViewBinder or if the existing ViewBinder cannot
     * handle binding to a TextView.
     *
     * @param v TextView to receive text
     * @param text the text to be set for the TextView
     */
    public void setViewText(TextView v, String text) {
        v.setText(text);
    }

    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new SimpleFilter();
        }
        return mFilter;
    }

    /**
     * This class can be used by external clients of SimpleAdapter to bind
     * values to views.
     *
     * You should use this class to bind values to views that are not
     * directly supported by SimpleAdapter or to change the way binding
     * occurs for views supported by SimpleAdapter.
     *
     * @see android.widget.SimpleAdapter#setViewImage(android.widget.ImageView, int)
     * @see android.widget.SimpleAdapter#setViewImage(android.widget.ImageView, String)
     * @see android.widget.SimpleAdapter#setViewText(android.widget.TextView, String)
     */
    public static interface ViewBinder {
        /**
         * Binds the specified data to the specified view.
         *
         * When binding is handled by this ViewBinder, this method must return true.
         * If this method returns false, SimpleAdapter will attempts to handle
         * the binding on its own.
         *
         * @param view the view to bind the data to
         * @param data the data to bind to the view
         * @param textRepresentation a safe String representation of the supplied data:
         *        it is either the result of data.toString() or an empty String but it
         *        is never null
         *
         * @return true if the data was bound to the view, false otherwise
         */
        boolean setViewValue(View view, Object data, String textRepresentation);
    }

    /**
     * <p>An array filters constrains the content of the array adapter with
     * a prefix. Each item that does not start with the supplied prefix
     * is removed from the list.</p>
     */
    private class SimpleFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (!mFilterEnabled) {
            	results.values = mData;
            	results.count = mData.size();
            } else {
            	if (mUnfilteredData == null) {
            		mUnfilteredData = new ArrayList<Map<String, ?>>(mData);
            	}
            	
            	if (prefix == null || prefix.length() == 0) {
            		ArrayList<Map<String, ?>> list = mUnfilteredData;
            		results.values = list;
            		results.count = list.size();
            	} else {
            		String prefixString = prefix.toString().toLowerCase();
            		
            		ArrayList<Map<String, ?>> unfilteredValues = mUnfilteredData;
            		int count = unfilteredValues.size();
            		
            		ArrayList<Map<String, ?>> newValues = new ArrayList<Map<String, ?>>(count);
            		
            		for (int i = 0; i < count; i++) {
            			Map<String, ?> h = unfilteredValues.get(i);
            			if (h != null) {
            				
            				int len = mTo.length;
            				
            				for (int j=0; j<len; j++) {
            					String str =  (String)h.get(mFrom[j]);
            					
            					String[] words = str.split(" ");
            					int wordCount = words.length;
            					
            					for (int k = 0; k < wordCount; k++) {
            						String word = words[k];
            						
            						if (word.toLowerCase().startsWith(prefixString)) {
            							newValues.add(h);
            							break;
            						}
            					}
            				}
            			}
            		}
            		
            		results.values = newValues;
            		results.count = newValues.size();
            	}
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mData = (List<Map<String, ?>>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    private CharSequence formatUrl(CharSequence url) {
        if (mUrlColor == null) {
            // Lazily get the URL color from the current theme.
            TypedValue colorValue = new TypedValue();
            boolean found = mProviderContext.getTheme().resolveAttribute(R.attr.lcTextColorSearchUrl, colorValue, true);
            if (found) {
                mUrlColor = mProviderContext.getResources().getColorStateList(colorValue.resourceId);
            } else {
                mUrlColor = mProviderContext.getResources().getColorStateList(R.color.lc_search_view_drop_down_match_text_color_light);
            }
        }

        SpannableString text = new SpannableString(url);
        text.setSpan(new TextAppearanceSpan(null, 0, 0, mUrlColor, null),
                0, url.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return text;
    }

    public void setMatchWordsColor(int color) {
        if (mMatchWordsColor == null || mMatchWordsColor.getDefaultColor() != color) {
            mMatchWordsColor = ColorStateList.valueOf(color);
        }
    }

    private CharSequence formatMatchWords(CharSequence t) {
    	if ( t == null) {
    		return null;
    	}
    	if (mMatchWordsColor == null) {
            // Lazily get the URL color from the current theme.
            TypedValue colorValue = new TypedValue();
            boolean found = mProviderContext.getTheme().resolveAttribute(R.attr.lcDropDownItemMatchTextColor, colorValue, true);
            if (found) {
                mMatchWordsColor = mProviderContext.getResources().getColorStateList(colorValue.resourceId);
            } else {
                mMatchWordsColor = mProviderContext.getResources().getColorStateList(R.color.lc_search_view_drop_down_match_text_color_light);
            }
        }
    	
    	String queryText = mSearchView.getQuery().toString();
    	
        SpannableString text = new SpannableString(t);
        int queryLength = queryText.trim().length();
        text.setSpan(new TextAppearanceSpan(null, 0, 0, mMatchWordsColor, null),
                0, queryLength < text.length() ? queryLength : text.length() ,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return text;
    }

    private void setViewText(TextView v, CharSequence text) {
        // Set the text even if it's null, since we need to clear any previous text.
        v.setText(text);

        if (TextUtils.isEmpty(text)) {
            v.setVisibility(View.GONE);
        } else {
            v.setVisibility(View.VISIBLE);
        }
    }

}
