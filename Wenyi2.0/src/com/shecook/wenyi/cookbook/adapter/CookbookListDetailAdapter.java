package com.shecook.wenyi.cookbook.adapter;

import java.util.LinkedList;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shecook.wenyi.R;
import com.shecook.wenyi.common.volley.toolbox.ImageLoader;
import com.shecook.wenyi.common.volley.toolbox.NetworkImageView;
import com.shecook.wenyi.model.cookbook.CookBookModel;
import com.shecook.wenyi.model.cookbook.CookbookComment;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class CookbookListDetailAdapter extends BaseAdapter {
	private static final String TAG = "CookbookListDetailAdapter";
	private LinkedList<CookBookModel> mListItems;
	private Context context;

	public CookbookListDetailAdapter() {
		super();
	}

	public CookbookListDetailAdapter(Context context,
			LinkedList<CookBookModel> list) {
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
		ViewHolder holder = null;
		CookBookModel elid = mListItems.get(position);
		String rowtype = elid.getRowType();
		
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.cookbook_itemdetail_info, null);
			holder = new ViewHolder();
			holder.imageUrl = (NetworkImageView) view
					.findViewById(R.id.item_img);
			holder.advTitle = (TextView) view
					.findViewById(R.id.cookbook_listitem_detail_text);
			holder.cookbook_listitem_detail_text2 = (TextView) view
					.findViewById(R.id.cookbook_listitem_detail_text2);
			holder.cookbook_listitem_detail_text3 = (TextView) view
					.findViewById(R.id.cookbook_listitem_detail_text3);
			
			
			holder.layout = (RelativeLayout) view.findViewById(R.id.cookbook_listdetail_comment_layout);
			holder.uportraitImage = (NetworkImageView) view.findViewById(R.id.cookbook_item_comment_uportrait);
			holder.cookbook_item_comment_nickname = (TextView) view.findViewById(R.id.cookbook_item_comment_nickname);
			holder.cookbook_item_comment_timeline = (TextView) view.findViewById(R.id.cookbook_item_comment_timeline);
			holder.cookbook_item_comment_comment = (TextView) view.findViewById(R.id.cookbook_item_comment_comment);
			
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		if ("imgoriginal".equals(rowtype)) {
			holder.imageUrl.setVisibility(View.VISIBLE);
			holder.advTitle.setVisibility(View.GONE);
			holder.cookbook_listitem_detail_text2.setVisibility(View.GONE);
			holder.cookbook_listitem_detail_text3.setVisibility(View.GONE);
			
			holder.layout.setVisibility(View.GONE);
			
			LruImageCache lruImageCache = LruImageCache.instance();
			ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance()
					.getRequestQueue(), lruImageCache);
//			holder.imageUrl.setDefaultImageResId(R.drawable.bg_color_while);
//			holder.imageUrl.setErrorImageResId(R.drawable.bg_color_while);
			
			try {
//				holder.layout.setLayoutParams(new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, 0));
				RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(Util.getAdapterMetricsWidth(context, 0.8f), Util.getAdapterMetricsHeigh(context, elid.getWidth(), elid.getHeight(), 0.8f));
				rl.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
				holder.imageUrl.setLayoutParams(rl);
//				if(elid.getWidth() != 0){
//				}
//				Util.getHeight(context, elid.getWidth(), elid.getHeight());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			holder.imageUrl.setImageUrl(elid.getRowContent(), imageLoader);
			holder.imageUrl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					WenyiLog.logd(TAG, "imageview click");
				}
			});
		} else if("comments".equals(rowtype)){
			CookbookComment elcid = (CookbookComment) elid;
			holder.imageUrl.setVisibility(View.GONE);
			holder.advTitle.setVisibility(View.GONE);
			holder.cookbook_listitem_detail_text2.setVisibility(View.GONE);
			holder.cookbook_listitem_detail_text3.setVisibility(View.GONE);
			holder.layout.setVisibility(View.VISIBLE);
			holder.uportraitImage.setVisibility(View.VISIBLE);
			holder.cookbook_item_comment_nickname.setVisibility(View.VISIBLE);
			holder.cookbook_item_comment_timeline.setVisibility(View.VISIBLE);
			
			LruImageCache lruImageCache = LruImageCache.instance();
			ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance()
					.getRequestQueue(), lruImageCache);
//			holder.uportraitImage.setDefaultImageResId(R.drawable.bg_color_while);
//			holder.uportraitImage.setErrorImageResId(R.drawable.bg_color_while);
			
			try {
				// holder.imageUrl.setLayoutParams(new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, Util.getMetricsHeigh(context, 500, 750)));
//				holder.layout.setLayoutParams(new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT));
				//if(elid.getWidth() != 0){
					// holder.imageUrl.setLayoutParams(new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, Util.getMetricsHeigh(context, elid.getWidth(), elid.getHeight())));
				//}
//				Util.getHeight(context, elid.getWidth(), elid.getHeight());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			holder.uportraitImage.setImageUrl(elcid.getUportrait(), imageLoader);
			holder.uportraitImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					WenyiLog.logd(TAG, "uportraitImage imageview click");
				}
			});
			holder.cookbook_item_comment_nickname.setText(elcid.getNickname());
			holder.cookbook_item_comment_timeline.setText(Util.formatTime2Away(elcid.getTimeline()));
			holder.cookbook_item_comment_comment.setText(elcid.getComment());
			
		} else if("image".equals(rowtype)){
			holder.imageUrl.setVisibility(View.VISIBLE);
			holder.advTitle.setVisibility(View.GONE);
			holder.cookbook_listitem_detail_text2.setVisibility(View.GONE);
			holder.cookbook_listitem_detail_text3.setVisibility(View.GONE);
			holder.layout.setVisibility(View.GONE);
			
			LruImageCache lruImageCache = LruImageCache.instance();
			ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance()
					.getRequestQueue(), lruImageCache);
//			holder.imageUrl.setDefaultImageResId(R.drawable.bg_color_while);
//			holder.imageUrl.setErrorImageResId(R.drawable.bg_color_while);
			
			try {
//				holder.layout.setLayoutParams(new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, 0));
				holder.imageUrl.setLayoutParams(new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, Util.getMetricsHeigh(context, 500, 750)));
				/*if(elid.getWidth() != 0){
					holder.imageUrl.setLayoutParams(new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, Util.getMetricsHeigh(context, elid.getWidth(), elid.getHeight())));
				}*/
//				Util.getHeight(context, elid.getWidth(), elid.getHeight());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			holder.imageUrl.setImageUrl(elid.getRowContent(), imageLoader);
			holder.imageUrl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					WenyiLog.logd(TAG, "imageview click");
				}
			});
		}else{
			holder.imageUrl.setVisibility(View.GONE);
			holder.advTitle.setVisibility(View.VISIBLE);
			holder.advTitle.setTextSize(14);
			holder.advTitle.setBackgroundResource(R.color.white);
			holder.advTitle.setTextColor(context.getResources().getColor(R.color.black));
			holder.advTitle.setText("");
			holder.layout.setVisibility(View.GONE);
//			holder.layout.setLayoutParams(new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, 0));
			if("title".equals(rowtype)){
				holder.advTitle.setTextSize(24);
				holder.advTitle.setGravity(Gravity.CENTER_HORIZONTAL);
				holder.cookbook_listitem_detail_text2.setVisibility(View.GONE);
				holder.cookbook_listitem_detail_text3.setVisibility(View.GONE);
				holder.advTitle.setText(elid.getRowContent());
			}else if("ingredients".equals(rowtype) || "seasonings".equals(rowtype)){
				holder.advTitle.setTextSize(16);
				holder.advTitle.setGravity(Gravity.LEFT);
				String[] rows = elid.getRowContent().split(";");
				if(rows.length == 2){
					holder.cookbook_listitem_detail_text2.setVisibility(View.VISIBLE);
					holder.cookbook_listitem_detail_text3.setTextSize(16);
					holder.cookbook_listitem_detail_text3.setVisibility(View.VISIBLE);
					holder.cookbook_listitem_detail_text2.setGravity(Gravity.CENTER_HORIZONTAL);
					holder.cookbook_listitem_detail_text3.setGravity(Gravity.RIGHT);
					holder.advTitle.setText(elid.getRowContent().split(";")[0]);
					holder.cookbook_listitem_detail_text3.setText(elid.getRowContent().split(";")[1]);
				}else{
					holder.advTitle.setBackgroundColor(context.getResources().getColor(R.color.personal_comments_divider));
					holder.advTitle.setText(elid.getRowContent().split(";")[0]);
				}
			}else if("space".equals(rowtype)){
				holder.advTitle.setTextSize(16);
				holder.advTitle.setBackgroundResource(R.color.personal_comments_divider);
				holder.cookbook_listitem_detail_text2.setVisibility(View.GONE);
				holder.cookbook_listitem_detail_text3.setVisibility(View.GONE);
			}else if("catalog".equals(rowtype)){
				holder.advTitle.setText(elid.getRowContent());
				holder.advTitle.setTextSize(18);
				holder.advTitle.setTextColor(context.getResources().getColor(R.color.orange));
				holder.cookbook_listitem_detail_text2.setVisibility(View.GONE);
				holder.cookbook_listitem_detail_text3.setVisibility(View.GONE);
			}else{
				holder.advTitle.setTextSize(16);
				holder.advTitle.setGravity(Gravity.LEFT);
				holder.cookbook_listitem_detail_text2.setVisibility(View.GONE);
				holder.cookbook_listitem_detail_text3.setVisibility(View.GONE);
				holder.advTitle.setText(elid.getRowContent());
			}
//			holder.advTitle.setBackgroundColor(context.getResources().getColor(R.color.blue));
		}
		return view;
	}

	private static class ViewHolder {
		TextView advTitle, cookbook_listitem_detail_text2, cookbook_listitem_detail_text3;
		TextView eventUrl;
		NetworkImageView imageUrl;
		
		// comments view
		RelativeLayout layout;
		NetworkImageView uportraitImage;
		TextView cookbook_item_comment_nickname;
		TextView cookbook_item_comment_timeline;
		TextView cookbook_item_comment_comment;
	}
}
