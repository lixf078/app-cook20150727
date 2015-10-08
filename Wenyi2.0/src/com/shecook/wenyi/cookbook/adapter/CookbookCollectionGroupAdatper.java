package com.shecook.wenyi.cookbook.adapter;

import java.util.LinkedList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shecook.wenyi.R;
import com.shecook.wenyi.model.cookbook.CookbookCollectionGroup;

public class CookbookCollectionGroupAdatper extends BaseAdapter {
	
	
	private LinkedList<CookbookCollectionGroup> mListItems;
	private Context context;
	public CookbookCollectionGroupAdatper() {
		super();
	}

	public CookbookCollectionGroupAdatper(Context context, LinkedList<CookbookCollectionGroup> list) {
		super();
		this.context = context;
		mListItems = list;
	}
	
	@Override
	public int getCount() {
		return mListItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		mListItems.get(arg0);
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		ViewHolder holder;
		CookbookCollectionGroup ccg = mListItems.get(position);
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
     				R.layout.cookbook_collection_group_item, null);
			holder = new ViewHolder();
			holder.advTitle = (TextView) view.findViewById(R.id.cookbook_collection_item_title);
			holder.advTime = (TextView) view.findViewById(R.id.cookbook_collection_item_time);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
	    
		holder.advTitle.setText(ccg.getGroupname());
		holder.advTime.setText(ccg.getTimeline());
		return view ;
	}
	
	private static class ViewHolder {
		TextView advTitle;
		TextView advTime;
	}
}
