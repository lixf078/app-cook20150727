package com.shecook.wenyi.personal.adapter;

import java.util.LinkedList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shecook.wenyi.R;
import com.shecook.wenyi.common.volley.toolbox.ImageLoader;
import com.shecook.wenyi.common.volley.toolbox.NetworkImageRoundView;
import com.shecook.wenyi.model.personal.PersonalMessageModel;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class PersonalMessageListAdapter extends BaseAdapter {
	
	private LinkedList<PersonalMessageModel> mListItems;
	private Context context;
	
	OnSwipeOperator operator;
	
	public PersonalMessageListAdapter() {
		super();
	}

	public PersonalMessageListAdapter(Context context, LinkedList<PersonalMessageModel> list) {
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
		PersonalMessageModel eli = mListItems.get(position);
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
     				R.layout.personal_message_list_item, null);
			holder = new ViewHolder();
			holder.personal_message_item_nickname = (TextView) view.findViewById(R.id.personal_message_item_nickname);
			holder.personal_message_item_messageto = (TextView) view.findViewById(R.id.personal_message_item_messageto);
			holder.personal_message_item_timeline = (TextView) view.findViewById(R.id.personal_message_item_timeline);
			holder.personal_message_item_desc = (TextView) view.findViewById(R.id.personal_message_item_desc);
			holder.personal_message_item_img = (NetworkImageRoundView) view.findViewById(R.id.personal_message_item_img);
			holder.message_delete = (TextView) view.findViewById(R.id.message_delete);
			holder.message_delete.setTag(R.id.message_delete, position);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		LruImageCache lruImageCache = LruImageCache.instance();
	    
	    ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
//	    holder.personal_message_item_img.setDefaultImageResId(R.drawable.icon_dialog);
//	    holder.personal_message_item_img.setErrorImageResId(R.drawable.icon_dialog);
	    
	    holder.personal_message_item_img.setImageUrl(eli.getFrom_imageurl(), imageLoader);
	    
		holder.personal_message_item_nickname.setText(eli.getFrom_nickname());
		holder.personal_message_item_desc.setText(eli.getFrom_desc());
		
		holder.message_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int position = (Integer) v.getTag(R.id.message_delete);
				operator.onDeleteItem(position);
			}
		});
		return view ;
	}
	
	private static class ViewHolder {
		TextView personal_message_item_nickname, personal_message_item_messageto, personal_message_item_timeline, personal_message_item_desc, message_delete;
		NetworkImageRoundView personal_message_item_img;
	}
	
	public void setOnSwipeOperator(OnSwipeOperator operator){
		this.operator = operator;
	}
	public interface OnSwipeOperator{
		void onChangeGroup(int positon);
		void onDeleteItem(int positon);
	}
}
