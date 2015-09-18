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
import com.shecook.wenyi.common.volley.toolbox.NetworkImageView;
import com.shecook.wenyi.model.EssayListItem;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class EssayListAdapter extends BaseAdapter {
	
	
	private LinkedList<EssayListItem> mListItems;
	private Context context;
	public EssayListAdapter() {
		super();
	}

	public EssayListAdapter(Context context, LinkedList<EssayListItem> list) {
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
		EssayListItem eli = mListItems.get(position);
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
     				R.layout.essay_list_item, null);
			holder = new ViewHolder();
			holder.advTitle = (TextView) view.findViewById(R.id.essay_item_title);
			holder.advTime = (TextView) view.findViewById(R.id.essay_item_time);
			holder.imageUrl = (NetworkImageView) view.findViewById(R.id.item_img);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		LruImageCache lruImageCache = LruImageCache.instance();
	    ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
	    holder.imageUrl.setDefaultImageResId(R.drawable.icon_dialog);
	    holder.imageUrl.setErrorImageResId(R.drawable.icon_dialog);
	    
	    holder.imageUrl.setImageUrl(eli.getIconurl(), imageLoader);
	    holder.imageUrl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			}
		});
		holder.advTitle.setText(eli.getTitle());
		holder.advTime.setText(eli.getTimeline());
		return view ;
	}
	
	private static class ViewHolder {
		TextView advTitle;
		TextView advTime;
		NetworkImageView imageUrl;
	}
}
