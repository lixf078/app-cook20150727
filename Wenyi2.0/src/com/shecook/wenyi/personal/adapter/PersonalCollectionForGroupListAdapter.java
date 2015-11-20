package com.shecook.wenyi.personal.adapter;

import java.util.LinkedList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shecook.wenyi.R;
import com.shecook.wenyi.common.volley.toolbox.ImageLoader;
import com.shecook.wenyi.common.volley.toolbox.NetworkImageRoundView;
import com.shecook.wenyi.model.personal.PersonalCollectionModel;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class PersonalCollectionForGroupListAdapter extends BaseAdapter {
	
	
	private LinkedList<PersonalCollectionModel> mListItems;
	private Context context;
	
	OnSwipeOperator operator;
	
	public PersonalCollectionForGroupListAdapter() {
		super();
	}

	public PersonalCollectionForGroupListAdapter(Context context, LinkedList<PersonalCollectionModel> list) {
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
		PersonalCollectionModel eli = mListItems.get(position);
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
     				R.layout.personal_collection_forgroup_list_item, null);
			holder = new ViewHolder();
			holder.advTitle = (TextView) view.findViewById(R.id.personal_collection_item_title);
			holder.advTime = (TextView) view.findViewById(R.id.personal_collection_item_time);
			holder.personal_collection_item_summary = (TextView) view.findViewById(R.id.personal_collection_item_summary);
			holder.collection_change_group = (TextView) view.findViewById(R.id.collection_change_group);
			holder.collection_delete = (TextView) view.findViewById(R.id.collection_delete);
			holder.imageUrl = (NetworkImageRoundView) view.findViewById(R.id.item_img);
			
			holder.collection_change_group.setTag(R.id.personal_collection_item_title, position);
			holder.collection_delete.setTag(R.id.personal_collection_item_title, position);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		LruImageCache lruImageCache = LruImageCache.instance();
	    ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
//	    holder.imageUrl.setDefaultImageResId(R.drawable.icon_dialog);
//	    holder.imageUrl.setErrorImageResId(R.drawable.icon_dialog);
	    
	    holder.imageUrl.setImageUrl(eli.getImgthumbnail(), imageLoader);
		holder.advTitle.setText(eli.getRecipename());
		holder.advTime.setText(eli.getTag());
		holder.personal_collection_item_summary.setText(eli.getSummary());

		holder.collection_change_group.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int position = (Integer) v.getTag(R.id.personal_collection_item_title);
				operator.onChangeGroup(position);
			}
		});
		
		holder.collection_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int position = (Integer) v.getTag(R.id.personal_collection_item_title);
				operator.onDeleteItem(position);
			}
		});
		
		return view ;
	}
	
	private static class ViewHolder {
		TextView advTitle;
		TextView advTime;
		TextView personal_collection_item_summary;
		TextView collection_change_group, collection_delete;
		NetworkImageRoundView imageUrl;
	}
	
	public void setOnSwipeOperator(OnSwipeOperator operator){
		this.operator = operator;
	}
	public interface OnSwipeOperator{
		void onChangeGroup(int positon);
		void onDeleteItem(int positon);
	}
}
