package com.shecook.wenyi.cookbook.adapter;

import java.util.LinkedList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.shecook.wenyi.R;
import com.shecook.wenyi.common.volley.toolbox.ImageLoader;
import com.shecook.wenyi.common.volley.toolbox.NetworkImageView;
import com.shecook.wenyi.model.CookbookHomeworkListItem;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class CookbookHomeworkListAdapter extends BaseAdapter {

	private static String TAG = "CookbookHomeworkListAdapter";
	
	private LinkedList<CookbookHomeworkListItem> mListItems;
	private Context context;

	public CookbookHomeworkListAdapter() {
		super();
	}

	public CookbookHomeworkListAdapter(Context context,
			LinkedList<CookbookHomeworkListItem> list) {
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
		return mListItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// Log.e(TAG, "getView " + position + ", mListItems size " + mListItems.size());
		ViewHolder holder;
		CookbookHomeworkListItem item = mListItems.get(position);
		if (view == null) {
			holder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.cookbook_homework_list_item, null);
			ImageView back = (ImageView) view.findViewById(R.id.background_imageview);
			LayoutParams lp = new RelativeLayout.LayoutParams(8, Util.getWidth(context) * 2 / 3);
			lp.setMargins(30, 0, 0, 0);
			back.setLayoutParams(lp);
			holder.homeworkImage = (NetworkImageView) view.findViewById(R.id.middle_image_cookbook);
			LayoutParams imlp = new RelativeLayout.LayoutParams(Util.getWidth(context) - 100, Util.getWidth(context) / 2);
			imlp.setMargins(5, 10, 0, 10);
			imlp.addRule(RelativeLayout.BELOW, R.id.cookbook_homework_item_nickname);
			holder.homeworkImage.setLayoutParams(imlp);
			holder.nickname = (TextView) view.findViewById(R.id.cookbook_homework_item_nickname);
			holder.timeline = (TextView) view.findViewById(R.id.cookbook_homework_item_time);
			holder.comments = (TextView) view.findViewById(R.id.cookbook_homework_item_comments);
			holder.uportraitImage = (NetworkImageView) view.findViewById(R.id.cookbook_homework_item_userimg);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		try {
			Log.e(TAG, "getView tiem is " + position + ",item " + item + ", holder " + holder);
			holder.nickname.setText(item.getNickname());
			holder.timeline.setText(item.getTimeline());
			holder.comments.setText(" " + item.getComments());
			
			
			LruImageCache lruImageCache = LruImageCache.instance();
			ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
			holder.uportraitImage.setDefaultImageResId(R.drawable.icon_dialog);
			holder.uportraitImage.setErrorImageResId(R.drawable.icon_dialog);
			
			holder.uportraitImage.setImageUrl(item.getUportrait(), imageLoader);
			holder.uportraitImage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
				}
			});
			
			
			ImageLoader homeworkImageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
			holder.homeworkImage.setDefaultImageResId(R.drawable.c_130);
			holder.homeworkImage.setErrorImageResId(R.drawable.c_130);
			
			if(item.getImageList().size() > 0){
				holder.homeworkImage.setImageUrl(item.getImageList().get(0).getImageurl(), homeworkImageLoader);
				holder.homeworkImage.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
		return view;
	}

	private static class ViewHolder {
		TextView nickname;
		TextView timeline;
		TextView comments;
		NetworkImageView uportraitImage;
		NetworkImageView homeworkImage;
		@Override
		public String toString() {
			return "ViewHolder [nickname=" + nickname + ", timeline="
					+ timeline + ", comments=" + comments + ", uportraitImage="
					+ uportraitImage + ", homeworkImage=" + homeworkImage + "]";
		}
	}
}
