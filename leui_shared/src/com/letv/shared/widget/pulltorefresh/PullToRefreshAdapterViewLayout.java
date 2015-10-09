/*******************************************************************************
 * Copyright 2011, 2012.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.letv.shared.widget.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.letv.shared.widget.pulltorefresh.internal.EmptyViewMethodAccessor;
import com.letv.shared.widget.pulltorefresh.internal.LoadingLayout;

@SuppressWarnings("rawtypes")
public class PullToRefreshAdapterViewLayout extends PullToRefreshBase {
    
    private static FrameLayout.LayoutParams convertEmptyViewLayoutParams(ViewGroup.LayoutParams lp) {
        FrameLayout.LayoutParams newLp = null;

        if (null != lp) {
            newLp = new FrameLayout.LayoutParams(lp);

            if (lp instanceof LinearLayout.LayoutParams) {
                newLp.gravity = ((LinearLayout.LayoutParams) lp).gravity;
            } else {
                newLp.gravity = Gravity.CENTER;
            }
        }

        return newLp;
    }
    
    private class AdapterOnScrollListener implements OnScrollListener {

        public final void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount,
                final int totalItemCount) {

            if (DEBUG) {
                Log.d(LOG_TAG, "First Visible: " + firstVisibleItem + ". Visible Count: " + visibleItemCount
                        + ". Total Items:" + totalItemCount);
            }

            /**
             * Set whether the Last Item is Visible. lastVisibleItemIndex is a
             * zero-based index, so we minus one totalItemCount to check
             */
            if (null != mOnLastItemVisibleListener) {
                mLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount - 1);
            }

            // Finally call OnScrollListener if we have one
            if (null != mOnScrollListener) {
                mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }

        public final void onScrollStateChanged(final AbsListView view, final int state) {
            /**
             * Check that the scrolling has stopped, and that the last item is
             * visible.
             */
            if (state == OnScrollListener.SCROLL_STATE_IDLE && null != mOnLastItemVisibleListener && mLastItemVisible) {
                mOnLastItemVisibleListener.onLastItemVisible();
            }

            if (null != mOnScrollListener) {
                mOnScrollListener.onScrollStateChanged(view, state);
            }
        }
        
    }

    private boolean mLastItemVisible;
    private OnScrollListener mOnScrollListener;
    private OnLastItemVisibleListener mOnLastItemVisibleListener;
    private View mEmptyView;
    
    private AbsListView mAbsListView;

    private boolean mScrollEmptyView = true;

    public PullToRefreshAdapterViewLayout(Context context) {
        super(context);
    }

    public PullToRefreshAdapterViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshAdapterViewLayout(Context context, Mode mode) {
        super(context, mode);
    }

    public PullToRefreshAdapterViewLayout(Context context, Mode mode, AnimationStyle animStyle) {
        super(context, mode, animStyle);
    }

    /**
     * Pass-through method for {@link PullToRefreshBase#getRefreshableView()
     * getRefreshableView()}.
     * {@link AdapterView#setAdapter(android.widget.Adapter)}
     * setAdapter(adapter)}. This is just for convenience!
     * 
     * @param adapter - Adapter to set
     */
    public void setAdapter(ListAdapter adapter) {
        if (mAbsListView != null) {
            mAbsListView.setAdapter(adapter);
        }
    }

    /**
     * Sets the Empty View to be used by the Adapter View.
     * <p/>
     * We need it handle it ourselves so that we can Pull-to-Refresh when the
     * Empty View is shown.
     * <p/>
     * Please note, you do <strong>not</strong> usually need to call this method
     * yourself. Calling setEmptyView on the AdapterView will automatically call
     * this method and set everything up. This includes when the Android
     * Framework automatically sets the Empty View based on it's ID.
     * 
     * @param newEmptyView - Empty View to be used
     */
    public final void setEmptyView(View newEmptyView) {
        if (mAbsListView == null) {
            return;
        }
        
        FrameLayout refreshableViewWrapper = getRefreshableViewWrapper();

        if (null != newEmptyView) {
            // New view needs to be clickable so that Android recognizes it as a
            // target for Touch Events
            newEmptyView.setClickable(true);

            ViewParent newEmptyViewParent = newEmptyView.getParent();
            if (null != newEmptyViewParent && newEmptyViewParent instanceof ViewGroup) {
                ((ViewGroup) newEmptyViewParent).removeView(newEmptyView);
            }

            // We need to convert any LayoutParams so that it works in our
            // FrameLayout
            FrameLayout.LayoutParams lp = convertEmptyViewLayoutParams(newEmptyView.getLayoutParams());
            if (null != lp) {
                refreshableViewWrapper.addView(newEmptyView, lp);
            } else {
                refreshableViewWrapper.addView(newEmptyView);
            }
        }
        
        if (mAbsListView instanceof EmptyViewMethodAccessor) {
            ((EmptyViewMethodAccessor) mAbsListView).setEmptyViewInternal(newEmptyView);
        } else {
            mAbsListView.setEmptyView(newEmptyView);
        }
        mEmptyView = newEmptyView;
    }

    /**
     * Pass-through method for {@link PullToRefreshBase#getRefreshableView()
     * getRefreshableView()}.
     * {@link AdapterView#setOnItemClickListener(OnItemClickListener)
     * setOnItemClickListener(listener)}. This is just for convenience!
     * 
     * @param listener - OnItemClickListener to use
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        if (mAbsListView != null) {
            mAbsListView.setOnItemClickListener(listener);
        }
    }

    public final void setOnLastItemVisibleListener(OnLastItemVisibleListener listener) {
        mOnLastItemVisibleListener = listener;
    }

    public final void setOnScrollListener(OnScrollListener listener) {
        mOnScrollListener = listener;
    }

    public final void setScrollEmptyView(boolean doScroll) {
        mScrollEmptyView = doScroll;
    }

    protected boolean isReadyForPullStart() {
        return isFirstItemVisible();
    }

    protected boolean isReadyForPullEnd() {
        return isLastItemVisible();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (null != mEmptyView && !mScrollEmptyView) {
            mEmptyView.scrollTo(-l, -t);
        }
    }


    private boolean isFirstItemVisible() {
        if (mAbsListView == null) {
            return false;
        }
        
        final Adapter adapter = mAbsListView.getAdapter();

        if (null == adapter || adapter.isEmpty()) {
            if (DEBUG) {
                Log.d(LOG_TAG, "isFirstItemVisible. Empty View.");
            }
            return true;

        } else {

            /**
             * This check should really just be:
             * mRefreshableView.getFirstVisiblePosition() == 0, but PtRListView
             * internally use a HeaderView which messes the positions up. For
             * now we'll just add one to account for it and rely on the inner
             * condition which checks getTop().
             */
            if (mAbsListView.getFirstVisiblePosition() <= 1) {
                final View firstVisibleChild = mAbsListView.getChildAt(0);
                if (firstVisibleChild != null) {
                    return firstVisibleChild.getTop() >= mRefreshableView.getTop();
                }
            }
        }

        return false;
    }

    private boolean isLastItemVisible() {
        if (mAbsListView == null) {
            return false;
        }
        
        final Adapter adapter = mAbsListView.getAdapter();

        if (null == adapter || adapter.isEmpty()) {
            if (DEBUG) {
                Log.d(LOG_TAG, "isLastItemVisible. Empty View.");
            }
            return true;
        } else {
            final int lastItemPosition = mAbsListView.getCount() - 1;
            final int lastVisiblePosition = mAbsListView.getLastVisiblePosition();

            if (DEBUG) {
                Log.d(LOG_TAG, "isLastItemVisible. Last Item Position: " + lastItemPosition + " Last Visible Pos: "
                        + lastVisiblePosition);
            }

            /**
             * This check should really just be: lastVisiblePosition ==
             * lastItemPosition, but PtRListView internally uses a FooterView
             * which messes the positions up. For me we'll just subtract one to
             * account for it and rely on the inner condition which checks
             * getBottom().
             */
            if (lastVisiblePosition >= lastItemPosition - 1) {
                final int childIndex = lastVisiblePosition - mAbsListView.getFirstVisiblePosition();
                final View lastVisibleChild = mAbsListView.getChildAt(childIndex);
                if (lastVisibleChild != null) {
                    return lastVisibleChild.getBottom() <= mRefreshableView.getBottom();
                }
            }
        }

        return false;
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }
    
    public AbsListView getRefreshableAbsListView() {
        return mAbsListView;
    }

    @Override
    protected final FrameLayout createRefreshableView(Context context, AttributeSet attrs) {
        final RefeshFrameLayout fl = new RefeshFrameLayout(context, attrs);
        return fl;
    }

    class RefeshFrameLayout extends FrameLayout implements EmptyViewMethodAccessor {

        public RefeshFrameLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void setEmptyView(View emptyView) {
            PullToRefreshAdapterViewLayout.this.setEmptyView(emptyView);
        }

        @Override
        public void setEmptyViewInternal(View emptyView) {
            setEmptyView(emptyView);
        }
    }
    
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = null;
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (childView instanceof LoadingLayout) {
                continue;
            }
            // 通过是AbsListView的子类进行判断，且第一个子View是AbsListVew，没有用加入指定id的做法
            if (childView instanceof RefeshFrameLayout) {
                view = ((RefeshFrameLayout) childView).getChildAt(0);
            }
        }
        
        if (view != null && view instanceof AbsListView) {
            mAbsListView = (AbsListView) view;
            // Set it to this so it can be used in ListActivity/ListFragment
            mAbsListView.setId(android.R.id.list);
            mAbsListView.setOnScrollListener(new AdapterOnScrollListener());
        } else {
            throw new UnsupportedOperationException("Refreshable View is not a AbsListView's subclass, or the number of Refreshable View more than one");
        }
    }

}
