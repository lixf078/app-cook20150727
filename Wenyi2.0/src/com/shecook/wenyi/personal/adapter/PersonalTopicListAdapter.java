package com.shecook.wenyi.personal.adapter;

import java.util.LinkedList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shecook.wenyi.R;
import com.shecook.wenyi.common.volley.toolbox.ImageLoader;
import com.shecook.wenyi.common.volley.toolbox.NetworkImageView;
import com.shecook.wenyi.model.personal.PersonalTopicModel;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class PersonalTopicListAdapter extends BaseAdapter {
	
	LruImageCache lruImageCache = null;
	private LinkedList<PersonalTopicModel> mListItems;
	private Context context;
	public PersonalTopicListAdapter() {
		super();
		lruImageCache = LruImageCache.instance();
	}

	public PersonalTopicListAdapter(Context context, LinkedList<PersonalTopicModel> list) {
		super();
		this.context = context;
		mListItems = list;
		lruImageCache = LruImageCache.instance();
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
		PersonalTopicModel eli = mListItems.get(position);
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
     				R.layout.personal_topic_list_item, null);
			holder = new ViewHolder();
			holder.personal_topic_item_title = (TextView) view.findViewById(R.id.personal_topic_item_title);
			holder.personal_topic_item_content = (TextView) view.findViewById(R.id.personal_topic_item_content);
			
			holder.personal_topic_item_image_info = (RelativeLayout) view.findViewById(R.id.personal_topic_item_image_info);
			holder.personal_topic_item_image_info_1 = (NetworkImageView) view.findViewById(R.id.personal_topic_item_image_info_1);
			holder.personal_topic_item_image_info_2 = (NetworkImageView) view.findViewById(R.id.personal_topic_item_image_info_2);
			holder.personal_topic_item_image_info_3 = (NetworkImageView) view.findViewById(R.id.personal_topic_item_image_info_3);
			holder.personal_topic_item_image_info_4 = (NetworkImageView) view.findViewById(R.id.personal_topic_item_image_info_4);
			
			holder.item_img = (NetworkImageView) view.findViewById(R.id.item_img);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.personal_topic_item_image_info_4.setVisibility(View.GONE);
		holder.personal_topic_item_image_info_3.setVisibility(View.GONE);
		holder.personal_topic_item_image_info_2.setVisibility(View.GONE);
		holder.personal_topic_item_image_info_1.setVisibility(View.GONE);
		
		holder.personal_topic_item_image_info.setVisibility(View.GONE);
		if(eli.getImages() != null && eli.getImages().size() > 0){
			holder.personal_topic_item_image_info.setVisibility(View.VISIBLE);

			switch (eli.getImages().size()) {
			case 4:
				holder.personal_topic_item_image_info_4.setVisibility(View.VISIBLE);

			    ImageLoader imageLoader4 = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
			    holder.personal_topic_item_image_info_4.setDefaultImageResId(R.drawable.welcome_homework);
			    holder.personal_topic_item_image_info_4.setErrorImageResId(R.drawable.welcome_homework);
			    holder.personal_topic_item_image_info_4.setImageUrl(eli.getImages().get(3).getImageurl(), imageLoader4);
			case 3:
				holder.personal_topic_item_image_info_3.setVisibility(View.VISIBLE);

			    ImageLoader imageLoader3 = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
			    holder.personal_topic_item_image_info_3.setDefaultImageResId(R.drawable.welcome_homework);
			    holder.personal_topic_item_image_info_3.setErrorImageResId(R.drawable.welcome_homework);
			    holder.personal_topic_item_image_info_3.setImageUrl(eli.getImages().get(2).getImageurl(), imageLoader3);
			case 2:
				holder.personal_topic_item_image_info_2.setVisibility(View.VISIBLE);

			    ImageLoader imageLoader2 = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
			    holder.personal_topic_item_image_info_2.setDefaultImageResId(R.drawable.welcome_homework);
			    holder.personal_topic_item_image_info_2.setErrorImageResId(R.drawable.welcome_homework);
			    holder.personal_topic_item_image_info_2.setImageUrl(eli.getImages().get(1).getImageurl(), imageLoader2);
			case 1:
				holder.personal_topic_item_image_info_1.setVisibility(View.VISIBLE);
			    ImageLoader imageLoader1 = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
			    holder.personal_topic_item_image_info_1.setDefaultImageResId(R.drawable.welcome_homework);
			    holder.personal_topic_item_image_info_1.setErrorImageResId(R.drawable.welcome_homework);
			    holder.personal_topic_item_image_info_1.setImageUrl(eli.getImages().get(0).getImageurl(), imageLoader1);
			default:
				break;
			}
		}

		LruImageCache lruImageCache = LruImageCache.instance();
	    ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
	    holder.item_img.setDefaultImageResId(R.drawable.icon);
	    holder.item_img.setErrorImageResId(R.drawable.icon);
	    
	    holder.item_img.setImageUrl(eli.getUportrait(), imageLoader);
		holder.personal_topic_item_title.setText(eli.getNickname());
		holder.personal_topic_item_content.setText(eli.getBody());
		return view ;
	}

	private static class ViewHolder {
		TextView personal_topic_item_title,personal_topic_item_content;
		NetworkImageView item_img, personal_topic_item_image_info_1,personal_topic_item_image_info_2,personal_topic_item_image_info_3,personal_topic_item_image_info_4;
		RelativeLayout personal_topic_item_image_info;
	}
}
