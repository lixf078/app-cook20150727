package com.shecook.wenyi.cookbook.adapter;

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
import com.shecook.wenyi.model.cookbook.CookbookHomeworkModel;
import com.shecook.wenyi.model.piazza.PiazzaQuestionCommentItem;
import com.shecook.wenyi.util.Util;
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
     				R.layout.cookbook_homework_item_detail_item_new, null);
			holder = new ViewHolder();
			holder.advTitle = (TextView) view.findViewById(R.id.cookbook_homework_item_title);
			holder.advTime = (TextView) view.findViewById(R.id.cookbook_homework_item_time);
			holder.cookbook_homework_item_level = (TextView) view.findViewById(R.id.cookbook_homework_item_level);
			holder.cookbook_homework_list_content = (TextView) view.findViewById(R.id.cookbook_homework_list_content);
			holder.cookbook_homework_from = (TextView) view.findViewById(R.id.cookbook_homework_from);
			holder.imageUrl = (NetworkImageView) view.findViewById(R.id.item_img);
			holder.item_img_ = (NetworkImageView) view.findViewById(R.id.item_img_);
			holder.cookbook_homework_list_item_info = (RelativeLayout) view.findViewById(R.id.cookbook_homework_list_item_info);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		ImageLoader imageLoader = null;
		try {
			LruImageCache lruImageCache = LruImageCache.instance();
			imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
//			holder.imageUrl.setDefaultImageResId(R.drawable.icon);
//			holder.imageUrl.setErrorImageResId(R.drawable.icon);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(pqi.isComment()){
			holder.item_img_.setVisibility(View.GONE);
			if(imageLoader != null){
				holder.imageUrl.setImageUrl(pqi.getUportrait(), imageLoader);
			}
			
			holder.cookbook_homework_list_item_info.setVisibility(View.VISIBLE);
			holder.cookbook_homework_list_content.setVisibility(View.VISIBLE);
			holder.advTitle.setText(pqci.getNickname());
			holder.advTime.setText(Util.formatTime2Away(pqci.getTimeline()));
			holder.cookbook_homework_list_content.setText(pqci.getComment());
			holder.cookbook_homework_from.setVisibility(View.GONE);
			holder.cookbook_homework_item_level.setVisibility(View.GONE);
		}else{
			holder.cookbook_homework_list_item_info.setVisibility(View.GONE);
			
			if(pqi.type == 1){
				// 图片
				holder.item_img_.setVisibility(View.VISIBLE);
				holder.cookbook_homework_list_content.setVisibility(View.GONE);
				holder.cookbook_homework_from.setVisibility(View.GONE);
				
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
			}else if(pqi.type == 2){
				// 来自
				holder.item_img_.setVisibility(View.GONE);
				holder.cookbook_homework_list_content.setVisibility(View.GONE);
				
				if(pqi.getRecipeid() != null && pqi.getRecipeid() != "0"){
					holder.cookbook_homework_from.setVisibility(View.VISIBLE);
				}
				holder.cookbook_homework_from.setText("来自菜谱 " + pqi.getRecipename());
			}
			
//			holder.cookbook_homework_item_level.setVisibility(View.VISIBLE);
//			holder.advTitle.setText(pqi.getNickname());
//			holder.advTime.setText(Util.formatTime2Away(pqi.getTimeline()));
//			holder.cookbook_homework_list_content.setText(pqi.getDescription());
//			holder.cookbook_homework_item_level.setText(pqi.getUserlvl());
//			holder.cookbook_homework_from.setText("来自菜谱 " + pqi.getRecipename());
		}
		
		return view ;
	}
	
	private static class ViewHolder {
		TextView advTitle, cookbook_homework_item_level, cookbook_homework_list_content, cookbook_homework_from;
		TextView advTime;
		NetworkImageView imageUrl, item_img_;
		RelativeLayout cookbook_homework_list_item_info;
	}
}
