package com.shecook.wenyi.cookbook.adapter;

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
import com.shecook.wenyi.common.volley.toolbox.NetworkImageRoundView;
import com.shecook.wenyi.model.CookbookListItem;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class CookbookListAdapter extends BaseAdapter {
	
	
	private LinkedList<CookbookListItem> mListItems;
	private Context context;
	public CookbookListAdapter() {
		super();
	}

	public CookbookListAdapter(Context context, LinkedList<CookbookListItem> list) {
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
		return mListItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		ViewHolder holder;
		CookbookListItem cbli = mListItems.get(position);
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
     				R.layout.cookbook_list_item, null);
			holder = new ViewHolder();
			holder.title = (TextView) view.findViewById(R.id.cookbook_item_title);
			holder.set_on_top = (TextView) view.findViewById(R.id.set_on_top);
			holder.summary = (TextView) view.findViewById(R.id.cookbook_item_summary);
			holder.imageUrl = (NetworkImageRoundView) view.findViewById(R.id.item_img);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		LruImageCache lruImageCache = LruImageCache.instance();
	    ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(),lruImageCache);
	    holder.imageUrl.setLayoutParams(new RelativeLayout.LayoutParams(Util.getWidth(context) / 4, Util.getWidth(context) / 4));
//	    holder.imageUrl.setDefaultImageResId(R.drawable.bg_color_while);
//	    holder.imageUrl.setErrorImageResId(R.drawable.bg_color_while);
	    
	    holder.imageUrl.setImageUrl(cbli.getImgthumbnail(), imageLoader);
	    holder.imageUrl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			}
		});
		holder.title.setText(cbli.getRecipename());
		holder.set_on_top.setText(cbli.getTag());
		if(!"".equals(cbli.getSummary())){
			holder.summary.setText(cbli.getSummary());
		}
		return view ;
	}
	
	private static class ViewHolder {
		TextView title, set_on_top;
		TextView summary;
		NetworkImageRoundView imageUrl;
	}
}
