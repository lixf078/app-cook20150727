package com.shecook.wenyi.piazza.adapter;

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
import com.shecook.wenyi.model.piazza.PiazzaDiscoverItem;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class PiazzaDiscoverListAdapter extends BaseAdapter {
	
	
	private LinkedList<PiazzaDiscoverItem> mListItems;
	private Context context;
	LruImageCache lruImageCache = null;
	public PiazzaDiscoverListAdapter() {
		super();
		lruImageCache = LruImageCache.instance();
	}

	public PiazzaDiscoverListAdapter(Context context, LinkedList<PiazzaDiscoverItem> list) {
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
		PiazzaDiscoverItem pdi = mListItems.get(position);
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
     				R.layout.piazza_discover_list_item, null);
			holder = new ViewHolder();
			holder.essay_item_title = (TextView) view.findViewById(R.id.essay_item_title);
			holder.essay_item_time = (TextView) view.findViewById(R.id.essay_item_time);
			holder.discover_gallery_text = (TextView) view.findViewById(R.id.discover_gallery_text);
			holder.essay_item_content = (TextView) view.findViewById(R.id.essay_item_content);
			
			holder.item_img = (NetworkImageView) view.findViewById(R.id.item_img);
			holder.essay_item_image_info_1 = (NetworkImageView) view.findViewById(R.id.essay_item_image_info_1);
			holder.essay_item_image_info_2 = (NetworkImageView) view.findViewById(R.id.essay_item_image_info_2);
			holder.essay_item_image_info_3 = (NetworkImageView) view.findViewById(R.id.essay_item_image_info_3);
			holder.essay_item_image_info_4 = (NetworkImageView) view.findViewById(R.id.essay_item_image_info_4);
			holder.discover_gallery_image = (NetworkImageView) view.findViewById(R.id.discover_gallery_image);
			
			holder.discover_common_layout = (RelativeLayout) view.findViewById(R.id.discover_common_layout);
			holder.discover_gallery_layout = (RelativeLayout) view.findViewById(R.id.discover_gallery_layout);
			holder.essay_item_info = (RelativeLayout) view.findViewById(R.id.essay_item_info);
			
			holder.essay_item_image_info = (RelativeLayout) view.findViewById(R.id.essay_item_image_info);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		String template = pdi.getTemplate();
		
		
		if(template.equals("10000")){
			// gallery
			holder.discover_common_layout.setVisibility(View.GONE);
			holder.discover_gallery_layout.setVisibility(View.VISIBLE);

			RelativeLayout.LayoutParams rl = 
					new RelativeLayout.LayoutParams(Util.getAdapterMetricsWidth(context, 1), 
							Util.getAdapterMetricsHeigh(context, pdi.getImg_width(), pdi.getImg_height(), 1));
			rl.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
			holder.discover_gallery_image.setLayoutParams(rl);

		    ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
		    holder.discover_gallery_image.setDefaultImageResId(R.drawable.welcome_homework);
		    holder.discover_gallery_image.setErrorImageResId(R.drawable.welcome_homework);
		    holder.discover_gallery_image.setImageUrl(pdi.getImageurl(), imageLoader);
		    
		    holder.discover_gallery_text.setText(pdi.getDesc());
		}else{
			holder.essay_item_image_info_4.setVisibility(View.GONE);
			holder.essay_item_image_info_3.setVisibility(View.GONE);
			holder.essay_item_image_info_2.setVisibility(View.GONE);
			holder.essay_item_image_info_1.setVisibility(View.GONE);
			
//			holder.essay_item_image_info.setVisibility(View.VISIBLE);
			
			if(pdi.getImage_items() != null){
				
				RelativeLayout.LayoutParams rl = 
						new RelativeLayout.LayoutParams(Util.getAdapterMetricsWidth(context, 0.1f), 
								Util.getAdapterMetricsHeigh(context, pdi.getImg_width(), pdi.getImg_height(), 0.1f));
//				rl.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
				
				switch (pdi.getImage_items().length) {
				case 4:
					holder.essay_item_image_info_4.setVisibility(View.VISIBLE);
//					holder.essay_item_image_info_4.setLayoutParams(rl);

				    ImageLoader imageLoader4 = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
				    holder.essay_item_image_info_4.setDefaultImageResId(R.drawable.welcome_homework);
				    holder.essay_item_image_info_4.setErrorImageResId(R.drawable.welcome_homework);
				    holder.essay_item_image_info_4.setImageUrl(pdi.getImage_items()[3], imageLoader4);
				case 3:
					holder.essay_item_image_info_3.setVisibility(View.VISIBLE);
					rl.addRule(RelativeLayout.LEFT_OF, R.id.essay_item_image_info_4);
//					holder.essay_item_image_info_3.setLayoutParams(rl);

				    ImageLoader imageLoader3 = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
				    holder.essay_item_image_info_3.setDefaultImageResId(R.drawable.welcome_homework);
				    holder.essay_item_image_info_3.setErrorImageResId(R.drawable.welcome_homework);
				    holder.essay_item_image_info_3.setImageUrl(pdi.getImage_items()[2], imageLoader3);
				case 2:
					holder.essay_item_image_info_2.setVisibility(View.VISIBLE);
					rl.addRule(RelativeLayout.LEFT_OF, R.id.essay_item_image_info_3);
//					holder.essay_item_image_info_2.setLayoutParams(rl);

				    ImageLoader imageLoader2 = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
				    holder.essay_item_image_info_2.setDefaultImageResId(R.drawable.welcome_homework);
				    holder.essay_item_image_info_2.setErrorImageResId(R.drawable.welcome_homework);
				    holder.essay_item_image_info_2.setImageUrl(pdi.getImage_items()[1], imageLoader2);
				case 1:
					holder.essay_item_image_info_1.setVisibility(View.VISIBLE);
					rl.addRule(RelativeLayout.LEFT_OF, R.id.essay_item_image_info_2);
//					holder.essay_item_image_info_1.setLayoutParams(rl);

				    ImageLoader imageLoader1 = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
				    holder.essay_item_image_info_1.setDefaultImageResId(R.drawable.welcome_homework);
				    holder.essay_item_image_info_1.setErrorImageResId(R.drawable.welcome_homework);
				    holder.essay_item_image_info_1.setImageUrl(pdi.getImage_items()[0], imageLoader1);
				default:
					break;
				}
			}else{
//				holder.essay_item_image_info.setVisibility(View.GONE);
//				holder.essay_item_image_info.setVisibility(0);
			}
			
			if(template.equals("10001")){
				holder.discover_common_layout.setVisibility(View.VISIBLE);
				holder.discover_gallery_layout.setVisibility(View.GONE);
				holder.item_img.setVisibility(View.VISIBLE);
				
				
				holder.essay_item_title.setVisibility(View.VISIBLE);
				holder.essay_item_time.setVisibility(View.VISIBLE);
				holder.essay_item_content.setVisibility(View.VISIBLE);
				
				ImageLoader item_img = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
				holder.item_img.setDefaultImageResId(R.drawable.welcome_homework);
				holder.item_img.setErrorImageResId(R.drawable.welcome_homework);
				holder.item_img.setImageUrl(pdi.getImageurl(), item_img);

				holder.essay_item_title.setText(pdi.getTitle());
				holder.essay_item_time.setText(pdi.getTimeline());
				holder.essay_item_content.setText(pdi.getDesc());
			}else if(template.equals("10002")){
				holder.discover_common_layout.setVisibility(View.VISIBLE);
				holder.item_img.setVisibility(View.VISIBLE);
				holder.discover_gallery_layout.setVisibility(View.GONE);
				
				holder.essay_item_title.setVisibility(View.VISIBLE);
				holder.essay_item_time.setVisibility(View.VISIBLE);
				holder.essay_item_content.setVisibility(View.VISIBLE);
				
				ImageLoader item_img = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
			    holder.item_img.setDefaultImageResId(R.drawable.welcome_homework);
			    holder.item_img.setErrorImageResId(R.drawable.welcome_homework);
			    holder.item_img.setImageUrl(pdi.getImageurl(), item_img);
				
				holder.essay_item_title.setText(pdi.getTitle());
				holder.essay_item_time.setText(pdi.getTimeline());
				holder.essay_item_content.setText(pdi.getDesc());
			}else if(template.equals("10003")){
				holder.discover_common_layout.setVisibility(View.VISIBLE);
				holder.discover_gallery_layout.setVisibility(View.GONE);
				holder.item_img.setVisibility(View.GONE);
				
				holder.essay_item_title.setVisibility(View.VISIBLE);
				holder.essay_item_time.setVisibility(View.GONE);
				holder.essay_item_content.setVisibility(View.VISIBLE);
				
				holder.essay_item_title.setText(pdi.getTitle());
				holder.essay_item_content.setText(pdi.getDesc());
			}
		} 
		
		return view ;
	}
	
	private static class ViewHolder {
		NetworkImageView item_img, essay_item_image_info_1, essay_item_image_info_2, essay_item_image_info_3, essay_item_image_info_4, discover_gallery_image;
		RelativeLayout discover_common_layout, discover_gallery_layout, essay_item_info;
		TextView discover_gallery_text, essay_item_content, essay_item_time, essay_item_title;
		RelativeLayout essay_item_image_info;
	}
}
