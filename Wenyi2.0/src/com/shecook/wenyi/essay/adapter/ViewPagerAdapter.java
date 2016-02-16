package com.shecook.wenyi.essay.adapter;

import java.util.ArrayList;

import com.shecook.wenyi.HttpStatus;
import com.shecook.wenyi.R;
import com.shecook.wenyi.common.WebViewActivity;
import com.shecook.wenyi.common.volley.toolbox.ImageLoader;
import com.shecook.wenyi.common.volley.toolbox.NetworkImageView;
import com.shecook.wenyi.cookbook.CookbookHomeworkList;
import com.shecook.wenyi.cookbook.CookbookItemDeatilActivity;
import com.shecook.wenyi.essay.EssayItemDeatilActivity;
import com.shecook.wenyi.model.WenyiGallery;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;
import com.shecook.wenyi.view.RecyclingPagerAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 
 * @author lixufeng
 * @email ja_lxf@163.com
 * 
 */
public class ViewPagerAdapter extends RecyclingPagerAdapter {

	private int mChildCount = 0;
	
	private Context context;
	private ArrayList<WenyiGallery> mPageViews;
	private boolean isInfiniteLoop;
	public ViewPagerAdapter(Context context, ArrayList<WenyiGallery> mPageViews) {
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
		WenyiGallery wenyiGallery = mPageViews.get(position);
		
		Log.e("lixufeng", "wenyiGallery " + wenyiGallery);
		Log.e("lixufeng", wenyiGallery.toString());
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

		if(!TextUtils.isEmpty(wenyiGallery.getImgUrl())){
			LruImageCache lruImageCache = LruImageCache.instance();
			ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
			holder.imageUrl.setImageUrl(wenyiGallery.getImgUrl(), imageLoader);
		}
	    holder.imageUrl.setTag(R.id.wenyi_common_tag_id1, "" + wenyiGallery.getEvent_type());
	    holder.imageUrl.setTag(R.id.wenyi_common_tag_id2, "" + wenyiGallery.getEvent_content());
	    holder.imageUrl.setTag(R.id.wenyi_common_tag_id3, "" + wenyiGallery.getId());
	    holder.imageUrl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Log.e("lixufeng", "wenyiGallery type " + Integer.parseInt(arg0.getTag(R.id.wenyi_common_tag_id1) + ""));
				switch(Integer.parseInt(arg0.getTag(R.id.wenyi_common_tag_id1) + "")){
					case HttpStatus.REQUEST_TARGET_COOKBOOK:{
						Intent intent = new Intent(context, CookbookItemDeatilActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS); 
						intent.putExtra("recipeid", "" + arg0.getTag(R.id.wenyi_common_tag_id2));
						context.startActivity(intent);
						break;
					}
					case HttpStatus.REQUEST_TARGET_ESSAY:{
						Intent intent = new Intent(context, EssayItemDeatilActivity.class);
						intent.putExtra("articleid", "" + arg0.getTag(R.id.wenyi_common_tag_id2));
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS); 
						context.startActivity(intent);
						break;
					}
//					case HttpStatus.REQUEST_CODE_TOPIC:{
//						Intent intent = new Intent(context, PizzaQuestionDeatilActivity.class);
//						intent.putExtra("topicid", "" + arg0.getTag(R.id.wenyi_common_tag_id2));
//						context.startActivity(intent);
//						break;
//					}
//					case HttpStatus.REQUEST_CODE_CIRCLE:{
//						Intent intent = new Intent(context, GroupItemDetailActivity.class);
//						intent.putExtra("circleid", "" + arg0.getTag(R.id.wenyi_common_tag_id2));
//						context.startActivity(intent);
//						break;
//					}
					case HttpStatus.REQUEST_TARGET_EXTERNAL_LINK :{
						Uri uri = Uri.parse((String)arg0.getTag(R.id.wenyi_common_tag_id2));
						Intent intent = new Intent(Intent.ACTION_VIEW, uri);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS); 
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						try {
							context.startActivity(intent);
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					}
					case HttpStatus.REQUEST_TARGET_INTERNAL_LINK:{
						Intent intent = new Intent(context, WebViewActivity.class);
						intent.putExtra("weburl", "" + arg0.getTag(R.id.wenyi_common_tag_id2));
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS); 
						context.startActivity(intent);
						break;
					}
					case HttpStatus.REQUEST_TARGET_COOKBOOK_HOMEWORKLIST:{
						Intent intent = new Intent(context, CookbookHomeworkList.class);
						intent.putExtra("recipeid", "" + arg0.getTag(R.id.wenyi_common_tag_id2));
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS); 
						context.startActivity(intent);
						break;
					}
					
				}
				
			}
		});
		holder.advTitle.setText(wenyiGallery.getTitle() + "");
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
	
	@Override
	public void notifyDataSetChanged() {
//		mChildCount = getCount();
		super.notifyDataSetChanged();
	}
	
/*	@Override
	public int getItemPosition(Object object) {
		if(mChildCount > 0){
			mChildCount--;
		}
		return POSITION_NONE;
//		return super.getItemPosition(object);
		
	}*/
}
