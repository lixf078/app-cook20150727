package com.shecook.wenyi.personal.adapter;

import java.util.LinkedList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shecook.wenyi.R;
import com.shecook.wenyi.common.volley.toolbox.ImageLoader;
import com.shecook.wenyi.common.volley.toolbox.NetworkImageRoundView;
import com.shecook.wenyi.model.personal.PersonalCommentModel;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class PersonalCommentListAdapter extends BaseAdapter {
	
	
	private LinkedList<PersonalCommentModel> mListItems;
	private Context context;
	
	OnSwipeOperator operator;
	
	public PersonalCommentListAdapter() {
		super();
	}

	public PersonalCommentListAdapter(Context context, LinkedList<PersonalCommentModel> list) {
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
		PersonalCommentModel eli = mListItems.get(position);
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
     				R.layout.personal_comment_list_item, null);
			holder = new ViewHolder();
			holder.personal_comment_item_title_1 = (TextView) view.findViewById(R.id.personal_comment_item_title_1);
			holder.personal_comment_item_title = (TextView) view.findViewById(R.id.personal_comment_item_title);
			holder.personal_comment_item_content = (TextView) view.findViewById(R.id.personal_comment_item_content);
			holder.item_img_1 = (NetworkImageRoundView) view.findViewById(R.id.item_img_1);
			holder.item_img = (NetworkImageRoundView) view.findViewById(R.id.item_img);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		LruImageCache lruImageCache = LruImageCache.instance();
	    ImageLoader imageLoader1 = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
//	    holder.item_img_1.setDefaultImageResId(R.drawable.icon_dialog);
//	    holder.item_img_1.setErrorImageResId(R.drawable.icon_dialog);
	    
	    holder.item_img_1.setImageUrl(eli.getTop_imageurl(), imageLoader1);
	    
	    ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
//	    holder.item_img.setDefaultImageResId(R.drawable.icon_dialog);
//	    holder.item_img.setErrorImageResId(R.drawable.icon_dialog);
	    
	    holder.item_img.setImageUrl(eli.getBottom_imageurl(), imageLoader);
	    
	    holder.personal_comment_item_title_1.setText(eli.getTop_desc());
		holder.personal_comment_item_title.setText(eli.getBottom_nickname());
		holder.personal_comment_item_content.setText(eli.getBottom_desc());

		return view ;
	}
	
	private static class ViewHolder {
		TextView personal_comment_item_title_1, personal_comment_item_title;
		TextView personal_comment_item_content;
		NetworkImageRoundView item_img_1,item_img;
	}
	
	public void setOnSwipeOperator(OnSwipeOperator operator){
		this.operator = operator;
	}
	public interface OnSwipeOperator{
		void onChangeGroup(int positon);
		void onDeleteItem(int positon);
	}
}
