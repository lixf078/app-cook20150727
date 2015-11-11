package com.shecook.wenyi.essay.adapter;

import java.util.LinkedList;

import android.content.Context;
import android.util.Log;
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
import com.shecook.wenyi.model.EssayListItemDetail;
import com.shecook.wenyi.model.essay.EssayListCommentsItemDetail;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class EssayListDetailAdapter extends BaseAdapter {
	private static final String TAG = "EssayListDetailAdapter";
	private LinkedList<EssayListItemDetail> mListItems;
	private Context context;

	public EssayListDetailAdapter() {
		super();
	}

	public EssayListDetailAdapter(Context context,
			LinkedList<EssayListItemDetail> list) {
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
		EssayListItemDetail elid = mListItems.get(position);
		Log.d("lixufeng1", "getview position " + position + ",elid " + elid);
		String rowtype = elid.getRowtype();
		
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.essay_listitem_image, null);
			holder = new ViewHolder();
			holder.imageUrl = (NetworkImageView) view
					.findViewById(R.id.item_img);
			holder.advTitle = (TextView) view
					.findViewById(R.id.essay_listitem_detail_text);
			
			holder.layout = (RelativeLayout) view.findViewById(R.id.essay_listdetail_comment_layout);
			holder.uportraitImage = (NetworkImageView) view.findViewById(R.id.essay_item_comment_uportrait);
			holder.essay_item_comment_nickname = (TextView) view.findViewById(R.id.essay_item_comment_nickname);
			holder.essay_item_comment_timeline = (TextView) view.findViewById(R.id.essay_item_comment_timeline);
			holder.essay_item_comment_comment = (TextView) view.findViewById(R.id.essay_item_comment_comment);
			
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		if ("image".equals(rowtype)) {
			holder.imageUrl.setVisibility(View.VISIBLE);
			holder.advTitle.setVisibility(View.GONE);
			holder.layout.setVisibility(View.GONE);
			
			LruImageCache lruImageCache = LruImageCache.instance();
			ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance()
					.getRequestQueue(), lruImageCache);
			holder.imageUrl.setDefaultImageResId(R.drawable.loadingpic);
			holder.imageUrl.setErrorImageResId(R.drawable.loadingpic);
			
			try {
				if(elid.getWidth() != 0){
					RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(Util.getAdapterMetricsWidth(context, 0.8f), Util.getAdapterMetricsHeigh(context, elid.getWidth(), elid.getHeight(), 0.8f));
					rl.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
					holder.imageUrl.setLayoutParams(rl);
				}
//				Util.getHeight(context, elid.getWidth(), elid.getHeight());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			holder.imageUrl.setImageUrl(elid.getRowcontent(), imageLoader);
			holder.imageUrl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					WenyiLog.logd(TAG, "imageview click");
				}
			});
		} else if("commentOne".equals(rowtype)){
			EssayListCommentsItemDetail elcid = (EssayListCommentsItemDetail) elid;
			holder.imageUrl.setVisibility(View.GONE);
			holder.advTitle.setVisibility(View.GONE);
			holder.layout.setVisibility(View.VISIBLE);
			holder.uportraitImage.setVisibility(View.VISIBLE);
			holder.essay_item_comment_nickname.setVisibility(View.VISIBLE);
			holder.essay_item_comment_timeline.setVisibility(View.VISIBLE);
			
			LruImageCache lruImageCache = LruImageCache.instance();
			ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance()
					.getRequestQueue(), lruImageCache);
			holder.uportraitImage.setDefaultImageResId(R.drawable.icon);
			holder.uportraitImage.setErrorImageResId(R.drawable.icon);
			
			try {
				if(elid.getWidth() != 0){
					// holder.imageUrl.setLayoutParams(new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, Util.getMetricsHeigh(context, elid.getWidth(), elid.getHeight())));
				}
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
			holder.essay_item_comment_nickname.setText(elcid.getNickname());
			holder.essay_item_comment_timeline.setText(Util.formatTime2Away(elcid.getTimeline()));
			holder.essay_item_comment_comment.setText(elcid.getComment());
			
		} else if("commentTwo".equals(rowtype)){
			Log.d(TAG, "commentTwo");
			holder.imageUrl.setVisibility(View.GONE);
			holder.advTitle.setVisibility(View.GONE);
			holder.layout.setVisibility(View.VISIBLE);
			EssayListCommentsItemDetail elcid = (EssayListCommentsItemDetail) elid;
			
			holder.uportraitImage.setVisibility(View.INVISIBLE);
			holder.essay_item_comment_nickname.setVisibility(View.GONE);
			holder.essay_item_comment_timeline.setVisibility(View.GONE);
			holder.essay_item_comment_comment.setTextSize(16);
			holder.essay_item_comment_comment.setText(elcid.getComment());
		}else{
			holder.imageUrl.setVisibility(View.GONE);
			holder.advTitle.setVisibility(View.VISIBLE);
			holder.layout.setVisibility(View.GONE);
			
			if("essayTitleElid".equals(rowtype)){
				holder.advTitle.setTextSize(24);
				holder.advTitle.setGravity(Gravity.CENTER_HORIZONTAL);
			}else{
				holder.advTitle.setTextSize(16);
			}
			holder.advTitle.setText(elid.getRowcontent());
		}
		return view;
	}

	private static class ViewHolder {
		TextView advTitle;
		TextView eventUrl;
		NetworkImageView imageUrl;
		
		// comments view
		RelativeLayout layout;
		NetworkImageView uportraitImage;
		TextView essay_item_comment_nickname;
		TextView essay_item_comment_timeline;
		TextView essay_item_comment_comment;
	}
}
