package com.shecook.wenyi.cookbook.adapter;

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
import com.shecook.wenyi.common.volley.toolbox.NetworkImageRoundView;
import com.shecook.wenyi.common.volley.toolbox.NetworkImageView;
import com.shecook.wenyi.model.CookbookHomeworkListItem;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class PiazzaCookbookHomeworkListAdapter extends BaseAdapter {
	
	
	private LinkedList<CookbookHomeworkListItem> mListItems;
	private Context context;
	LruImageCache lruImageCache = null;
	public PiazzaCookbookHomeworkListAdapter() {
		super();
		lruImageCache = LruImageCache.instance();
	}

	public PiazzaCookbookHomeworkListAdapter(Context context, LinkedList<CookbookHomeworkListItem> list) {
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
		CookbookHomeworkListItem chli = mListItems.get(position);
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
     				R.layout.piazza_cookbookhomework_list_item, null);
			holder = new ViewHolder();
			holder.essay_item_title = (TextView) view.findViewById(R.id.essay_item_title);
			holder.essay_item_time = (TextView) view.findViewById(R.id.essay_item_time);
//			holder.discover_gallery_text = (TextView) view.findViewById(R.id.discover_gallery_text);
			holder.essay_item_content = (TextView) view.findViewById(R.id.essay_item_content);
			
			holder.item_img = (NetworkImageRoundView) view.findViewById(R.id.item_img);
			holder.essay_item_image_info_1 = (NetworkImageView) view.findViewById(R.id.essay_item_image_info_1);
			holder.essay_item_image_info_2 = (NetworkImageView) view.findViewById(R.id.essay_item_image_info_2);
			holder.essay_item_image_info_3 = (NetworkImageView) view.findViewById(R.id.essay_item_image_info_3);
			holder.essay_item_image_info_4 = (NetworkImageView) view.findViewById(R.id.essay_item_image_info_4);
//			holder.discover_gallery_image = (NetworkImageView) view.findViewById(R.id.discover_gallery_image);
			
			holder.discover_common_layout = (RelativeLayout) view.findViewById(R.id.discover_common_layout);
//			holder.discover_gallery_layout = (RelativeLayout) view.findViewById(R.id.discover_gallery_layout);
			holder.essay_item_info = (RelativeLayout) view.findViewById(R.id.essay_item_info);
			
			holder.essay_item_image_info = (RelativeLayout) view.findViewById(R.id.essay_item_image_info);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
			holder.essay_item_image_info_4.setVisibility(View.GONE);
			holder.essay_item_image_info_3.setVisibility(View.GONE);
			holder.essay_item_image_info_2.setVisibility(View.GONE);
			holder.essay_item_image_info_1.setVisibility(View.GONE);
			
//			holder.essay_item_image_info.setVisibility(View.VISIBLE);
			
		if(chli.getImageList() != null){
			
			switch (chli.getImageList().size()) {
			case 4:
				holder.essay_item_image_info_4.setVisibility(View.VISIBLE);

			    ImageLoader imageLoader4 = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
//			    holder.essay_item_image_info_4.setDefaultImageResId(R.drawable.welcome_homework);
//			    holder.essay_item_image_info_4.setErrorImageResId(R.drawable.welcome_homework);
			    holder.essay_item_image_info_4.setImageUrl(chli.getImageList().get(3).getImageurl(), imageLoader4);
			case 3:
				holder.essay_item_image_info_3.setVisibility(View.VISIBLE);

			    ImageLoader imageLoader3 = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
//			    holder.essay_item_image_info_3.setDefaultImageResId(R.drawable.welcome_homework);
//			    holder.essay_item_image_info_3.setErrorImageResId(R.drawable.welcome_homework);
			    holder.essay_item_image_info_3.setImageUrl(chli.getImageList().get(2).getImageurl(), imageLoader3);
			case 2:
				holder.essay_item_image_info_2.setVisibility(View.VISIBLE);

			    ImageLoader imageLoader2 = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
//			    holder.essay_item_image_info_2.setDefaultImageResId(R.drawable.welcome_homework);
//			    holder.essay_item_image_info_2.setErrorImageResId(R.drawable.welcome_homework);
			    holder.essay_item_image_info_2.setImageUrl(chli.getImageList().get(1).getImageurl(), imageLoader2);
			case 1:
				holder.essay_item_image_info_1.setVisibility(View.VISIBLE);

			    ImageLoader imageLoader1 = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
//			    holder.essay_item_image_info_1.setDefaultImageResId(R.drawable.welcome_homework);
//			    holder.essay_item_image_info_1.setErrorImageResId(R.drawable.welcome_homework);
			    holder.essay_item_image_info_1.setImageUrl(chli.getImageList().get(0).getImageurl(), imageLoader1);
			default:
				break;
			}
		}
		
		holder.discover_common_layout.setVisibility(View.VISIBLE);
//				holder.discover_gallery_layout.setVisibility(View.GONE);
		holder.item_img.setVisibility(View.VISIBLE);
		
		holder.essay_item_title.setVisibility(View.VISIBLE);
		holder.essay_item_time.setVisibility(View.VISIBLE);
		holder.essay_item_content.setVisibility(View.VISIBLE);
		
		ImageLoader item_img = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
//		holder.item_img.setDefaultImageResId(R.drawable.welcome_homework);
//		holder.item_img.setErrorImageResId(R.drawable.welcome_homework);
		holder.item_img.setImageUrl(chli.getUportrait(), item_img);
		
		holder.essay_item_title.setText(chli.getNickname());
		holder.essay_item_time.setText(Util.formatTime2Away(chli.getTimeline()));
		holder.essay_item_content.setText(chli.getDescription());
		
		return view ;
	}
	
	private static class ViewHolder {
		NetworkImageRoundView item_img;
		NetworkImageView essay_item_image_info_1, essay_item_image_info_2, essay_item_image_info_3, essay_item_image_info_4; // , discover_gallery_image;
		RelativeLayout discover_common_layout, essay_item_info; // discover_gallery_layout, 
		TextView  essay_item_content, essay_item_time, essay_item_title; // discover_gallery_text,
		RelativeLayout essay_item_image_info;
	}
}
