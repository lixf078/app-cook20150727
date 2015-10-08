package com.shecook.wenyi.cookbook.adapter;

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
import com.shecook.wenyi.model.cookbook.CookbookHomeworkModel;
import com.shecook.wenyi.model.piazza.PiazzaQuestionCommentItem;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class CookbookHomewrokListItemDetialAdapter extends BaseAdapter {
	
	
	private LinkedList<Object> mListItems;
	private Context context;
	public CookbookHomewrokListItemDetialAdapter() {
		super();
	}

	public CookbookHomewrokListItemDetialAdapter(Context context, LinkedList<Object> list) {
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
		CookbookHomeworkModel pqi = (CookbookHomeworkModel) mListItems.get(position);
		PiazzaQuestionCommentItem pqci = null;
		if(pqi.isComment()){
		}
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
     				R.layout.cookbook_homework_item_detail_item, null);
			holder = new ViewHolder();
			holder.advTitle = (TextView) view.findViewById(R.id.cookbook_homework_item_title);
			holder.advTime = (TextView) view.findViewById(R.id.cookbook_homework_item_time);
			holder.cookbook_homework_item_level = (TextView) view.findViewById(R.id.cookbook_homework_item_level);
			holder.cookbook_homework_list_content = (TextView) view.findViewById(R.id.cookbook_homework_list_content);
			holder.cookbook_homework_from = (TextView) view.findViewById(R.id.cookbook_homework_from);
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
			holder.advTime.setText(pqci.getTimeline());
			holder.cookbook_homework_list_content.setText(pqci.getComment());
			holder.cookbook_homework_from.setVisibility(View.GONE);
			holder.cookbook_homework_item_level.setVisibility(View.GONE);
		}else{
			holder.cookbook_homework_from.setVisibility(View.VISIBLE);
			holder.cookbook_homework_item_level.setVisibility(View.VISIBLE);
			holder.advTitle.setText(pqi.getNickname());
			holder.advTime.setText(pqi.getTimeline());
			holder.cookbook_homework_list_content.setText(pqi.getDescription());
			holder.cookbook_homework_item_level.setText(pqi.getUserlvl());
			holder.cookbook_homework_from.setText("来自菜谱 " + pqi.getRecipename());
		}
		
		return view ;
	}
	
	private static class ViewHolder {
		TextView advTitle, cookbook_homework_item_level, cookbook_homework_list_content, cookbook_homework_from;
		TextView advTime;
		NetworkImageView imageUrl;
	}
}
