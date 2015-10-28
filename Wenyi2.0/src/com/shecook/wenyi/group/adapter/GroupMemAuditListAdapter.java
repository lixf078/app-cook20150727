package com.shecook.wenyi.group.adapter;

import java.util.LinkedList;

import android.content.Context;
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
import com.shecook.wenyi.model.group.GroupListItemSharedModel;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class GroupMemAuditListAdapter extends BaseAdapter {
	
	private LinkedList<GroupListItemSharedModel> mListItems;
	private Context context;
	
	OnSwipeOperator operator;
	
	public GroupMemAuditListAdapter() {
		super();
	}

	public GroupMemAuditListAdapter(Context context, LinkedList<GroupListItemSharedModel> list) {
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
		GroupListItemSharedModel eli = mListItems.get(position);
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
     				R.layout.group_mem_audit_list_item, null);
			holder = new ViewHolder();
			holder.group_mem_audit_item_title = (TextView) view.findViewById(R.id.group_mem_audit_item_title);
			holder.group_mem_audit_item_time = (TextView) view.findViewById(R.id.group_mem_audit_item_time);
			holder.group_mem_audit_item_time2 = (TextView) view.findViewById(R.id.group_mem_audit_item_time2);
			holder.group_mem_audit_item_summary = (TextView) view.findViewById(R.id.group_mem_audit_item_summary);
			holder.group_mem_audit_item_delete = (TextView) view.findViewById(R.id.group_mem_audit_item_delete);
			holder.imageUrl = (NetworkImageView) view.findViewById(R.id.item_img);
			holder.group_mem_audit_item_delete.setTag(R.id.group_mem_audit_item_delete, position);
			holder.group_mem_audit_item_time.setTag(R.id.group_mem_audit_item_time, position);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		LruImageCache lruImageCache = LruImageCache.instance();
	    ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
	    holder.imageUrl.setDefaultImageResId(R.drawable.icon_dialog);
	    holder.imageUrl.setErrorImageResId(R.drawable.icon_dialog);
	    
	    holder.imageUrl.setImageUrl(eli.getUportrait(), imageLoader);
		holder.group_mem_audit_item_title.setText(eli.getNickname());
		String status = "";
		switch (eli.getStatus()) {
		case 10000:
		case 10001:
			status = "已经添加";
			holder.group_mem_audit_item_time2.setText(status);
			holder.group_mem_audit_item_time2.setVisibility(View.VISIBLE);
			holder.group_mem_audit_item_time.setVisibility(View.GONE);
			break;
		case 10002:
		case 10003:
		case 10004:
			status = "添加";
			holder.group_mem_audit_item_time.setText(status);
			holder.group_mem_audit_item_time.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					int position = (Integer) v.getTag(R.id.group_mem_audit_item_time);
					operator.onJoinGroup(position);
				}
			});
			holder.group_mem_audit_item_time.setVisibility(View.VISIBLE);
			holder.group_mem_audit_item_time2.setVisibility(View.GONE);
			break;
		default:
			break;
		}
		holder.group_mem_audit_item_time.setText(status);

		
		holder.group_mem_audit_item_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int position = (Integer) v.getTag(R.id.group_mem_audit_item_delete);
				operator.onDeleteItem(position);
			}
		});
		
		
		return view ;
	}
	
	private static class ViewHolder {
		TextView group_mem_audit_item_title;
		TextView group_mem_audit_item_time,group_mem_audit_item_time2;
		TextView group_mem_audit_item_summary;
		RelativeLayout group_detail_item_image_info;
		TextView group_mem_audit_item_delete;
		NetworkImageView imageUrl;
	}
	
	public void setOnSwipeOperator(OnSwipeOperator operator){
		this.operator = operator;
	}
	public interface OnSwipeOperator{
		void onDeleteItem(int positon);
		void onJoinGroup(int position);
	}
}
