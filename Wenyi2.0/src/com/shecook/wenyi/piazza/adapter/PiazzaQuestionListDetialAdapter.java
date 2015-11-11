package com.shecook.wenyi.piazza.adapter;

import java.util.LinkedList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		ImageLoader imageLoader;
		try {
			LruImageCache lruImageCache = LruImageCache.instance();
			imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
			holder.imageUrl.setDefaultImageResId(R.drawable.icon);
			holder.imageUrl.setErrorImageResId(R.drawable.icon);
			holder.imageUrl.setImageUrl(pqi.getUportrait(), imageLoader);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(pqi.isComment()){
			holder.advTitle.setText(pqci.getNickname());
			holder.advTime.setText(Util.formatTime2Away(pqci.getTimeline()));
			holder.pizza_question_list_content.setText(pqci.getComment());
		}else{
			holder.advTitle.setText(pqi.getNickname());
			holder.advTime.setText(Util.formatTime2Away(pqi.getTimeline()));
			holder.pizza_question_list_content.setText(pqi.getBody());
			holder.pizza_question_item_level.setText(pqi.getNickname());
		}
		
		return view ;
	}
	
	private static class ViewHolder {
		TextView advTitle, pizza_question_item_level, pizza_question_list_content;
		TextView advTime;
		NetworkImageView imageUrl;
	}
}
