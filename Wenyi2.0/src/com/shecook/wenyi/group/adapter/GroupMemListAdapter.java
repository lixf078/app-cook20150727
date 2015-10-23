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

public class GroupMemListAdapter extends BaseAdapter {
	
	private LinkedList<GroupListItemSharedModel> mListItems;
	private Context context;
	
	OnSwipeOperator operator;
	
	public GroupMemListAdapter() {
		super();
	}

	public GroupMemListAdapter(Context context, LinkedList<GroupListItemSharedModel> list) {
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
     				R.layout.group_detail_mem_list_item, null);
			holder = new ViewHolder();
			holder.group_shared_item_title = (TextView) view.findViewById(R.id.group_shared_item_title);
			holder.group_shared_item_time = (TextView) view.findViewById(R.id.group_shared_item_time);
			holder.group_shared_item_summary = (TextView) view.findViewById(R.id.group_shared_item_summary);
			holder.group_shared_item_delete = (TextView) view.findViewById(R.id.group_shared_item_delete);
			holder.imageUrl = (NetworkImageView) view.findViewById(R.id.item_img);
			holder.group_shared_item_delete.setTag(R.id.group_shared_item_delete, position);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		LruImageCache lruImageCache = LruImageCache.instance();
	    ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
	    holder.imageUrl.setDefaultImageResId(R.drawable.icon_dialog);
	    holder.imageUrl.setErrorImageResId(R.drawable.icon_dialog);
	    
	    holder.imageUrl.setImageUrl(eli.getUportrait(), imageLoader);
		holder.group_shared_item_title.setText(eli.getNickname());
//		holder.group_shared_item_time.setText(eli.getComments());
		String status = "";
		switch (eli.getStatus()) {
		case 10000:
			status = "创建人";
			break;
		case 10001:
			status = "管理员";
			break;
		case 10002:
			status = "圈子成员";
			break;
		case 10003:
			status = "已申请";
		case 10004:
			status = "非圈子成员";
			break;
		default:
			break;
		}
		holder.group_shared_item_summary.setText(status);

		
		holder.group_shared_item_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int position = (Integer) v.getTag(R.id.group_shared_item_delete);
				operator.onDeleteItem(position);
			}
		});
		
		
		return view ;
	}
	
	private static class ViewHolder {
		TextView group_shared_item_title;
		TextView group_shared_item_time;
		TextView group_shared_item_summary;
		RelativeLayout group_detail_item_image_info;
		TextView group_shared_item_delete;
		NetworkImageView imageUrl;
	}
	
	public void setOnSwipeOperator(OnSwipeOperator operator){
		this.operator = operator;
	}
	public interface OnSwipeOperator{
		void onDeleteItem(int positon);
	}
}
