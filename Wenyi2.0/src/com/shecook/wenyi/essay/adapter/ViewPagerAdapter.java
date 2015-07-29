package com.shecook.wenyi.essay.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shecook.wenyi.R;
import com.shecook.wenyi.common.volley.toolbox.ImageLoader;
import com.shecook.wenyi.common.volley.toolbox.NetworkImageView;
import com.shecook.wenyi.model.essay.EssayGallery;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;
import com.shecook.wenyi.view.RecyclingPagerAdapter;

/**
 * 
 * @author lixufeng
 * @email ja_lxf@163.com
 * 
 */
public class ViewPagerAdapter extends RecyclingPagerAdapter {

	private Context context;
	private ArrayList<EssayGallery> mPageViews;
	private boolean isInfiniteLoop;
	public ViewPagerAdapter(Context context, ArrayList<EssayGallery> mPageViews) {
		this.context = context;
		this.mPageViews = mPageViews;
		isInfiniteLoop = false;
	}

	@Override
	public int getCount() {
		return mPageViews.size();
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled") @Override
	public View getView(int position, View view, ViewGroup container) {
		ViewHolder holder;
		Log.d("lixufeng","getView position " + position + ", mPageViews.size() " + mPageViews.size());
		if(position >= mPageViews.size()){
			position = position % mPageViews.size();
		}
		if (view == null) {
			EssayGallery essayGallery = mPageViews.get(position);
			view = LayoutInflater.from(context).inflate(
     				R.layout.viewpager_view_item, null);
			holder = new ViewHolder();
			holder.advTitle = essayGallery.getTitle();
			holder.eventUrl = essayGallery.getEvent_content();
			holder.imageUrl = essayGallery.getImgUrl();
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		NetworkImageView imageView = (NetworkImageView) view.findViewById(R.id.item_img);
		LruImageCache lruImageCache = LruImageCache.instance();
	    ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
	    imageView.setDefaultImageResId(R.drawable.wenyi_01);
	    imageView.setErrorImageResId(R.drawable.wenyi_01);
	    
	    imageView.setImageUrl(holder.imageUrl, imageLoader);
	    imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Uri uri = Uri.parse(((ViewHolder) arg0.getTag()).eventUrl);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				context.startActivity(intent);
			}
		});
		TextView textView = (TextView)view.findViewById(R.id.item_title);
		textView.setText(holder.advTitle);
		return view ;
	}

	/**
	 * @return the isInfiniteLoop
	 */
	public boolean isInfiniteLoop() {
		return isInfiniteLoop;
	}

	/**
	 * @param isInfiniteLoop the isInfiniteLoop to set
	 */
	public ViewPagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
		this.isInfiniteLoop = isInfiniteLoop;
		return this;
	}
	
	private static class ViewHolder {
		String advTitle;
		String eventUrl;
		String imageUrl;
	}
}
