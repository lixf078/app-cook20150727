package com.shecook.wenyi.essay.adapter;

import java.util.LinkedList;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
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
import com.shecook.wenyi.model.EssayListItemDetail;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.WenyiLog;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class EssayListDetailAdapter extends BaseAdapter {
	private static final String TAG = "EssayListDetailAdapter";
	private LinkedList<EssayListItemDetail> mListItems;
	private Context context;

	public EssayListDetailAdapter() {
		super();
	}

	public EssayListDetailAdapter(Context context,
			LinkedList<EssayListItemDetail> list) {
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
		ViewHolder holder = null;
		EssayListItemDetail elid = mListItems.get(position);
		Log.d("lixufeng1", "getview position " + position + ",elid " + elid);
		String rowtype = elid.getRowtype();
		
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.essay_listitem_image, null);
			holder = new ViewHolder();
			holder.imageUrl = (NetworkImageView) view
					.findViewById(R.id.item_img);
			holder.advTitle = (TextView) view
					.findViewById(R.id.essay_listitem_detail_text);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		if ("image".equals(rowtype)) {
			holder.imageUrl.setVisibility(View.VISIBLE);
			holder.advTitle.setVisibility(View.GONE);
			LruImageCache lruImageCache = LruImageCache.instance();
			ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance()
					.getRequestQueue(), lruImageCache);
			holder.imageUrl.setDefaultImageResId(R.drawable.loadingpic);
			holder.imageUrl.setErrorImageResId(R.drawable.loadingpic);
			
			try {
				if(elid.getWidth() != 0){
					holder.imageUrl.setLayoutParams(new RelativeLayout.LayoutParams(android.widget.RelativeLayout.LayoutParams.MATCH_PARENT, Util.getMetricsHeigh(context, elid.getWidth(), elid.getHeight())));
				}
//				Util.getHeight(context, elid.getWidth(), elid.getHeight());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			holder.imageUrl.setImageUrl(elid.getRowcontent(), imageLoader);
			holder.imageUrl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					WenyiLog.logd(TAG, "imageview click");
				}
			});
		} else{
			holder.imageUrl.setVisibility(View.GONE);
			holder.advTitle.setVisibility(View.VISIBLE);
			if("essayTitleElid".equals(rowtype)){
				holder.advTitle.setTextSize(24);
				holder.advTitle.setGravity(Gravity.CENTER_HORIZONTAL);
			}else{
				holder.advTitle.setTextSize(16);
			}
			holder.advTitle.setText(elid.getRowcontent());
		}
		return view;
	}

	private static class ViewHolder {
		TextView advTitle;
		TextView eventUrl;
		NetworkImageView imageUrl;
	}
}
