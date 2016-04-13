package com.shecook.wenyi.group.adapter;

import java.util.LinkedList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.letv.shared.widget.StaggeredGridView.LayoutParams;
import com.shecook.wenyi.R;
import com.shecook.wenyi.common.volley.toolbox.ImageLoader;
import com.shecook.wenyi.common.volley.toolbox.NetworkImageView;
import com.shecook.wenyi.model.group.GroupShareCommentItem;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class GroupShareItemDetialAdapter extends BaseAdapter {
	
	private LinkedList<Object> mListItems;
	private Context context;
	public GroupShareItemDetialAdapter() {
		super();
	}

	public GroupShareItemDetialAdapter(Context context, LinkedList<Object> list) {
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
		GroupShareCommentItem pqi = (GroupShareCommentItem) mListItems.get(position);
		
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
     				R.layout.group_share_item_detail_item, null);
			holder = new ViewHolder();
			holder.group_share_list_item_info = (RelativeLayout) view.findViewById(R.id.group_share_list_item_info);
			holder.advTitle = (TextView) view.findViewById(R.id.group_share_item_title);
			holder.advTime = (TextView) view.findViewById(R.id.group_share_item_time);
			holder.group_share_item_level = (TextView) view.findViewById(R.id.group_share_item_level);
			holder.group_share_list_content = (TextView) view.findViewById(R.id.group_share_list_content);
			
			holder.imageUrl = (NetworkImageView) view.findViewById(R.id.item_img);
			holder.item_img_ = (NetworkImageView) view.findViewById(R.id.item_img_);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		ImageLoader imageLoader = null;
		try {
			LruImageCache lruImageCache = LruImageCache.instance();
			imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(pqi.isComment()){
			holder.item_img_.setVisibility(View.GONE);
			holder.imageUrl.setDefaultImageResId(R.drawable.icon);
			holder.imageUrl.setErrorImageResId(R.drawable.icon);
			holder.imageUrl.setImageUrl(pqi.getUportrait(), imageLoader);
			holder.group_share_list_item_info.setVisibility(View.GONE);
			holder.group_share_list_content.setVisibility(View.VISIBLE);
			holder.group_share_list_content.setText(pqi.getComment());
		}else{
			
			if(pqi.type == 1){
				// 图片
				holder.group_share_list_item_info.setVisibility(View.GONE);
				holder.group_share_list_content.setVisibility(View.GONE);
				holder.item_img_.setVisibility(View.VISIBLE);
				
				if(pqi.width != 0){
					RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(Util.getAdapterMetricsWidth(context, 1f), Util.getAdapterMetricsHeigh(context, pqi.width, pqi.height, 1f));
					rl.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
					rl.setMargins(20, 20, 20, 20);
					holder.item_img_.setLayoutParams(rl);
				}
				if(imageLoader != null){
					Log.e("CookbookHomewrokListItemDetialAdapter", "getView imgUrl " + pqi.imgUrl);
					holder.item_img_.setImageUrl(pqi.imgUrl, imageLoader);
				}
			}else{
				holder.item_img_.setVisibility(View.GONE);
				holder.imageUrl.setDefaultImageResId(R.drawable.icon);
				holder.imageUrl.setErrorImageResId(R.drawable.icon);
				holder.imageUrl.setImageUrl(pqi.getUportrait(), imageLoader);
				holder.group_share_list_content.setVisibility(View.GONE);
				holder.group_share_list_item_info.setVisibility(View.VISIBLE);
				holder.advTitle.setText(Util.formatTime2Away(pqi.getNickname()));
				holder.advTime.setText(Util.formatTime2Away(pqi.getTimeline()));
				holder.group_share_item_level.setText(pqi.getComment());
			}
		}
		
		return view ;
	}
	
	private static class ViewHolder {
		TextView advTitle, group_share_item_level, group_share_list_content/*, group_share_from*/;
		TextView advTime;
		NetworkImageView imageUrl, item_img_;
		RelativeLayout group_share_list_item_info;
	}
}
