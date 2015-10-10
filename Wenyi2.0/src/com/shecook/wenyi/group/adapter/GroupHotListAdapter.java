package com.shecook.wenyi.group.adapter;

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
import com.shecook.wenyi.model.group.GroupHotListItem;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class GroupHotListAdapter extends BaseAdapter {
	
	
	private LinkedList<GroupHotListItem> mListItems;
	private Context context;
	
	public GroupHotListAdapter() {
		super();
	}

	public GroupHotListAdapter(Context context, LinkedList<GroupHotListItem> list) {
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
		GroupHotListItem ghli = mListItems.get(position);
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
     				R.layout.group_hot_list_item, null);
			holder = new ViewHolder();
			holder.group_hot_item_title = (TextView) view.findViewById(R.id.group_hot_item_title);
			holder.group_hot_item_class = (TextView) view.findViewById(R.id.group_hot_item_class);
			holder.group_hot_item_shared = (TextView) view.findViewById(R.id.group_hot_item_shared);
			holder.group_hot_tiem_content = (TextView) view.findViewById(R.id.group_hot_tiem_content);
			holder.imageUrl = (NetworkImageView) view.findViewById(R.id.item_img);
			
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		LruImageCache lruImageCache = LruImageCache.instance();
	    ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
	    holder.imageUrl.setDefaultImageResId(R.drawable.icon_dialog);
	    holder.imageUrl.setErrorImageResId(R.drawable.icon_dialog);
	    
	    holder.imageUrl.setImageUrl(ghli.getIconurl(), imageLoader);
	    holder.group_hot_item_title.setText(ghli.getTitle());
		holder.group_hot_item_class.setText("成员：" + ghli.getCurrentnum());
		holder.group_hot_item_shared.setText("分享：" + ghli.getShare());
		holder.group_hot_tiem_content.setText(ghli.getDescription());
		return view ;
	}
	
	private static class ViewHolder {
		TextView group_hot_item_title;
		TextView group_hot_item_class, group_hot_item_shared;
		TextView group_hot_tiem_content;
		NetworkImageView imageUrl;
	}
	
}
