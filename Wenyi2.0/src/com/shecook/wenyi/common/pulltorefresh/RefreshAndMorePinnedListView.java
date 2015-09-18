package com.shecook.wenyi.common.pulltorefresh;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.letv.shared.widget.PinnedHeaderListView;
import com.letv.shared.widget.pulltorefresh.OverscrollHelper;
import com.letv.shared.widget.pulltorefresh.PullToRefreshAdapterViewBase;
import com.shecook.wenyi.common.pulltorefresh.internal.EmptyViewMethodAccessor;

//PullToRefreshAdapterViewBase<PinnedHeaderListView>
public class RefreshAndMorePinnedListView extends PullToRefreshAdapterViewBase<PinnedHeaderListView> {

    public RefreshAndMorePinnedListView(Context context) {
        super(context);
    }

    public RefreshAndMorePinnedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshAndMorePinnedListView(Context context, Mode mode) {
        super(context, mode);
    }

    public RefreshAndMorePinnedListView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
    }

    @Override
    public final Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }


    protected PinnedHeaderListView createListView(Context context, AttributeSet attrs) {
        final PinnedHeaderListView lv;
        if (VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD) {
            lv = new InternalExpandableListViewSDK9(context, attrs);
        } else {
            lv = new InternalExpandableListView(context, attrs);
        }
        return lv;
    }

    @Override
    protected PinnedHeaderListView createRefreshableView(Context context, AttributeSet attrs) {
        if (mRefreshableViewLayout == -1) {
            PinnedHeaderListView lv = createListView(context, attrs);
            // Set it to this so it can be used in ListActivity/ListFragment
            lv.setId(android.R.id.list);
            return lv;
        } else {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = (ViewGroup) inflater.inflate(mRefreshableViewLayout, null);
            if (view instanceof PinnedHeaderListView) {
                view.setId(android.R.id.list);
                return (PinnedHeaderListView) view;
            } else {
                throw new UnsupportedOperationException("Refreshable View is not a PinnedHeaderListView");
            }
        }
    }

    class InternalExpandableListView extends PinnedHeaderListView implements EmptyViewMethodAccessor {

        public InternalExpandableListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void setEmptyView(View emptyView) {
            RefreshAndMorePinnedListView.this.setEmptyView(emptyView);
        }

        @Override
        public void setEmptyViewInternal(View emptyView) {
            super.setEmptyView(emptyView);
        }
    }

    @TargetApi(9)
    final class InternalExpandableListViewSDK9 extends InternalExpandableListView {

        public InternalExpandableListViewSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
                int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

            final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                    scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

            // Does all of the hard work...
            OverscrollHelper.overScrollBy(RefreshAndMorePinnedListView.this, deltaX, scrollX, deltaY, scrollY,
                    isTouchEvent);

            return returnValue;
        }
    }
}
