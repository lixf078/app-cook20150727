package com.shecook.wenyi.piazza.adapter;

import java.util.LinkedList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shecook.wenyi.R;
import com.shecook.wenyi.common.volley.toolbox.ImageLoader;
import com.shecook.wenyi.common.volley.toolbox.NetworkImageView;
import com.shecook.wenyi.model.piazza.PiazzaQuestionCommentItem;
import com.shecook.wenyi.model.piazza.PiazzaQuestionItem;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class PiazzaQuestionListDetialAdapter extends BaseAdapter {
	
	private LinkedList<Object> mListItems;
	private Context context;
	public PiazzaQuestionListDetialAdapter() {
		super();
	}

	public PiazzaQuestionListDetialAdapter(Context context, LinkedList<Object> list) {
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
		ViewHolder holder;
		PiazzaQuestionItem pqi = (PiazzaQuestionItem) mListItems.get(position);
		PiazzaQuestionCommentItem pqci = null;
		if(pqi.isComment()){
			pqci = (PiazzaQuestionCommentItem)pqi;
		}
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
     				R.layout.piazza_question_list_item_detail, null);
			holder = new ViewHolder();
			holder.advTitle = (TextView) view.findViewById(R.id.pizza_question_item_title);
			holder.advTime = (TextView) view.findViewById(R.id.pizza_question_item_time);
			holder.pizza_question_item_level = (TextView) view.findViewById(R.id.pizza_question_item_level);
			holder.pizza_question_list_content = (TextView) view.findViewById(R.id.pizza_question_list_content);
			
			holder.imageUrl = (NetworkImageView) view.findViewById(R.id.item_img);
			holder.imageUrl_ = (NetworkImageView) view.findViewById(R.id.item_img_);
			
			holder.relativeLayout = (RelativeLayout) view.findViewById(R.id.pizza_question_item_info);
			holder.catalog_divider = (TextView) view.findViewById(R.id.catalog_divider);
			
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		ImageLoader imageLoader;
		try {
			
//			holder.imageUrl.setDefaultImageResId(R.drawable.icon);
//			holder.imageUrl.setErrorImageResId(R.drawable.icon);

			if(pqi.type == 100){
				LruImageCache lruImageCache = LruImageCache.instance();
				imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(), lruImageCache);
				holder.relativeLayout.setVisibility(View.GONE);
				holder.advTitle.setVisibility(View.GONE);
				holder.advTime.setVisibility(View.GONE);
				holder.pizza_question_list_content.setVisibility(View.GONE);
				holder.pizza_question_item_level.setVisibility(View.GONE);
				holder.imageUrl.setVisibility(View.GONE);
				holder.imageUrl_.setVisibility(View.VISIBLE);
				if(pqi.image.getWidth() != 0){
					RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(Util.getAdapterMetricsWidth(context, 0.8f), Util.getAdapterMetricsHeigh(context, pqi.image.getWidth(), pqi.image.getHeight(), 0.8f));
					rl.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
					holder.imageUrl_.setLayoutParams(rl);
				}
				holder.imageUrl_.setImageUrl(pqi.image.getImageurl(), imageLoader);
				holder.catalog_divider.setVisibility(View.GONE);
			}else{
				LruImageCache lruImageCache = LruImageCache.instance();
				imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(), lruImageCache);
				holder.relativeLayout.setVisibility(View.VISIBLE);
				Log.e("PiazzaQuestionListDetialAdapter", "");
				holder.advTitle.setText(pqci.getNickname());
				holder.advTime.setText(Util.formatTime2Away(pqci.getTimeline()));
				holder.pizza_question_list_content.setText(pqci.getComment());
				
				holder.imageUrl.setImageUrl(pqi.getUportrait(), imageLoader);
				
				holder.imageUrl_.setVisibility(View.GONE);
				holder.imageUrl.setVisibility(View.VISIBLE);
				holder.advTitle.setVisibility(View.VISIBLE);
				holder.advTime.setVisibility(View.VISIBLE);
				holder.pizza_question_list_content.setVisibility(View.VISIBLE);
				holder.catalog_divider.setVisibility(View.VISIBLE);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		if(pqi.isComment()){
//			holder.advTitle.setText(pqci.getNickname());
//			holder.advTime.setText(Util.formatTime2Away(pqci.getTimeline()));
//			holder.pizza_question_list_content.setText(pqci.getComment());
//			holder.pizza_question_item_level.setVisibility(View.GONE);
//		}else{
//			holder.advTitle.setText(pqi.getNickname());
//			holder.advTime.setText(Util.formatTime2Away(pqi.getTimeline()));
//			holder.pizza_question_list_content.setText(pqi.getBody());
//			holder.pizza_question_item_level.setVisibility(View.VISIBLE);
//			holder.pizza_question_item_level.setText(pqi.getNickname());
//		}
		
		return view ;
	}
	
	private static class ViewHolder {
		TextView advTitle, pizza_question_item_level, pizza_question_list_content, catalog_divider;
		TextView advTime;
		NetworkImageView imageUrl, imageUrl_;
		RelativeLayout relativeLayout;
	}
}
