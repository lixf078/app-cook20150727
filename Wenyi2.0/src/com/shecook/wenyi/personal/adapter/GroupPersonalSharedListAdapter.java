package com.shecook.wenyi.personal.adapter;

import java.util.LinkedList;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shecook.wenyi.R;
import com.shecook.wenyi.common.volley.toolbox.ImageLoader;
import com.shecook.wenyi.common.volley.toolbox.NetworkImageView;
import com.shecook.wenyi.model.group.GroupListItemSharedModel;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class GroupPersonalSharedListAdapter extends BaseAdapter {
	
	private LinkedList<GroupListItemSharedModel> mListItems;
	private Context context;
	
	OnSwipeOperator operator;
	
	public GroupPersonalSharedListAdapter() {
		super();
	}

	public GroupPersonalSharedListAdapter(Context context, LinkedList<GroupListItemSharedModel> list) {
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
		GroupListItemSharedModel eli = mListItems.get(position);
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
     				R.layout.group_personal_shared_list_item, null);
			holder = new ViewHolder();
			holder.group_shared_item_title = (TextView) view.findViewById(R.id.group_shared_item_title);
			holder.group_shared_item_time = (TextView) view.findViewById(R.id.group_shared_item_time);
			holder.group_shared_item_summary = (TextView) view.findViewById(R.id.group_shared_item_summary);
			holder.group_shared_item_delete = (TextView) view.findViewById(R.id.group_shared_item_delete);
			holder.imageUrl = (NetworkImageView) view.findViewById(R.id.item_img);
			
			holder.group_shared_item_delete.setTag(R.id.group_shared_item_delete, position);

			holder.group_detail_item_image_info_1 = (NetworkImageView) view.findViewById(R.id.group_detail_item_image_info_1);
			holder.group_detail_item_image_info_2 = (NetworkImageView) view.findViewById(R.id.group_detail_item_image_info_2);
			holder.group_detail_item_image_info_3 = (NetworkImageView) view.findViewById(R.id.group_detail_item_image_info_3);
			holder.group_detail_item_image_info_4 = (NetworkImageView) view.findViewById(R.id.group_detail_item_image_info_4);
			
			
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		LruImageCache lruImageCache = LruImageCache.instance();
	    ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
	    holder.imageUrl.setDefaultImageResId(R.drawable.icon_dialog);
	    holder.imageUrl.setErrorImageResId(R.drawable.icon_dialog);

	    holder.imageUrl.setImageUrl(eli.getUportrait(), imageLoader);
		holder.group_shared_item_title.setText(eli.getNickname());
		if(!"0".equals(eli.getComments()) && TextUtils.isEmpty(eli.getComments())){
		}
		holder.group_shared_item_time.setText(Util.formatTime2Away(eli.getTimeline()));
		holder.group_shared_item_summary.setText(eli.getBody());

		holder.group_shared_item_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int position = (Integer) v.getTag(R.id.group_shared_item_delete);
				operator.onDeleteItem(position);
			}
		});

		holder.group_detail_item_image_info_4.setVisibility(View.GONE);
		holder.group_detail_item_image_info_3.setVisibility(View.GONE);
		holder.group_detail_item_image_info_2.setVisibility(View.GONE);
		holder.group_detail_item_image_info_1.setVisibility(View.GONE);
		
		if(eli.getImages() != null){
			switch (eli.getImages().size()) {
			case 4:
				holder.group_detail_item_image_info_4.setVisibility(View.VISIBLE);
				
				ImageLoader imageLoader4 = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
				holder.group_detail_item_image_info_4.setDefaultImageResId(R.drawable.welcome_homework);
				holder.group_detail_item_image_info_4.setErrorImageResId(R.drawable.welcome_homework);
				holder.group_detail_item_image_info_4.setImageUrl(eli.getImages().get(3).getImageurl(), imageLoader4);
			case 3:
				holder.group_detail_item_image_info_3.setVisibility(View.VISIBLE);
				
				ImageLoader imageLoader3 = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
				holder.group_detail_item_image_info_3.setDefaultImageResId(R.drawable.welcome_homework);
				holder.group_detail_item_image_info_3.setErrorImageResId(R.drawable.welcome_homework);
				holder.group_detail_item_image_info_3.setImageUrl(eli.getImages().get(2).getImageurl(), imageLoader3);
			case 2:
				holder.group_detail_item_image_info_2.setVisibility(View.VISIBLE);
				
				ImageLoader imageLoader2 = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
				holder.group_detail_item_image_info_2.setDefaultImageResId(R.drawable.welcome_homework);
				holder.group_detail_item_image_info_2.setErrorImageResId(R.drawable.welcome_homework);
				holder.group_detail_item_image_info_2.setImageUrl(eli.getImages().get(1).getImageurl(), imageLoader2);
			case 1:
				holder.group_detail_item_image_info_1.setVisibility(View.VISIBLE);
				
				ImageLoader imageLoader1 = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
				holder.group_detail_item_image_info_1.setDefaultImageResId(R.drawable.welcome_homework);
				holder.group_detail_item_image_info_1.setErrorImageResId(R.drawable.welcome_homework);
				holder.group_detail_item_image_info_1.setImageUrl(eli.getImages().get(0).getImageurl(), imageLoader1);
			default:
				break;
			}
		}
		
		return view ;
	}
	
	private static class ViewHolder {
		TextView group_shared_item_title;
		TextView group_shared_item_time;
		TextView group_shared_item_summary;
		TextView group_shared_item_delete;
		NetworkImageView imageUrl;
		NetworkImageView group_detail_item_image_info_1, group_detail_item_image_info_2, group_detail_item_image_info_3, group_detail_item_image_info_4;
	}
	
	public void setOnSwipeOperator(OnSwipeOperator operator){
		this.operator = operator;
	}
	public interface OnSwipeOperator{
		void onDeleteItem(int positon);
	}
}
