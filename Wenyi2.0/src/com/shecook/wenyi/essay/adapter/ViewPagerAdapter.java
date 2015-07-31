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
		if(position >= mPageViews.size()){
			position = position % mPageViews.size();
		}
		EssayGallery essayGallery = mPageViews.get(position);
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
     				R.layout.viewpager_view_item, null);
			holder = new ViewHolder();
			holder.advTitle = (TextView) view.findViewById(R.id.item_title);
			holder.imageUrl = (NetworkImageView) view.findViewById(R.id.item_img);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		LruImageCache lruImageCache = LruImageCache.instance();
	    ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
	    holder.imageUrl.setDefaultImageResId(R.drawable.wenyi_01);
	    holder.imageUrl.setErrorImageResId(R.drawable.wenyi_01);
	    holder.imageUrl.setImageUrl(essayGallery.getImgUrl(), imageLoader);
	    holder.imageUrl.setTag("" + essayGallery.getEvent_content());
	    holder.imageUrl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Uri uri = Uri.parse((String)arg0.getTag());
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		});
		holder.advTitle.setText(essayGallery.getTitle());
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
		TextView advTitle;
		NetworkImageView imageUrl;
	}
}
