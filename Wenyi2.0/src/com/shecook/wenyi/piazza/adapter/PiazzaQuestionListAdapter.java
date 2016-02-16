package com.shecook.wenyi.piazza.adapter;

import java.util.LinkedList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shecook.wenyi.R;
import com.shecook.wenyi.common.volley.toolbox.ImageLoader;
import com.shecook.wenyi.common.volley.toolbox.NetworkImageRoundView;
import com.shecook.wenyi.common.volley.toolbox.NetworkImageView;
import com.shecook.wenyi.model.piazza.PiazzaQuestionItem;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class PiazzaQuestionListAdapter extends BaseAdapter {

	private LinkedList<PiazzaQuestionItem> mListItems;
	private Context context;

	public PiazzaQuestionListAdapter() {
		super();
	}

	public PiazzaQuestionListAdapter(Context context, LinkedList<PiazzaQuestionItem> list) {
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
		PiazzaQuestionItem pdi = mListItems.get(position);
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.piazza_question_list_item, null);
			holder = new ViewHolder();
			holder.advTitle = (TextView) view.findViewById(R.id.essay_item_title);
			holder.advTime = (TextView) view.findViewById(R.id.essay_item_time);
			holder.imageUrl = (NetworkImageRoundView) view.findViewById(R.id.item_img);
			holder.question_item_image_info_1 = (NetworkImageView) view.findViewById(R.id.question_item_image_info_1);
			holder.question_item_image_info_2 = (NetworkImageView) view.findViewById(R.id.question_item_image_info_2);
			holder.question_item_image_info_3 = (NetworkImageView) view.findViewById(R.id.question_item_image_info_3);
			holder.question_item_image_info_4 = (NetworkImageView) view.findViewById(R.id.question_item_image_info_4);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		LruImageCache lruImageCache = LruImageCache.instance();
		ImageLoader imageLoader = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(), lruImageCache);
		// holder.imageUrl.setDefaultImageResId(R.drawable.icon);
		// holder.imageUrl.setErrorImageResId(R.drawable.icon);

		holder.imageUrl.setImageUrl(pdi.getUportrait(), imageLoader);
		holder.advTitle.setText(pdi.getNickname());
		holder.advTime.setText(pdi.getBody());

		holder.question_item_image_info_4.setVisibility(View.GONE);
		holder.question_item_image_info_3.setVisibility(View.GONE);
		holder.question_item_image_info_2.setVisibility(View.GONE);
		holder.question_item_image_info_1.setVisibility(View.GONE);

		// holder.essay_item_image_info.setVisibility(View.VISIBLE);

		if (pdi.getImages() != null) {

			switch (pdi.getImages().size()) {
			case 4:
				holder.question_item_image_info_4.setVisibility(View.VISIBLE);

				ImageLoader imageLoader4 = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(), lruImageCache);
				holder.question_item_image_info_4.setImageUrl(pdi.getImages().get(3).getImageurl(), imageLoader4);
			case 3:
				holder.question_item_image_info_3.setVisibility(View.VISIBLE);

				ImageLoader imageLoader3 = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(), lruImageCache);
				holder.question_item_image_info_3.setImageUrl(pdi.getImages().get(2).getImageurl(), imageLoader3);
			case 2:
				holder.question_item_image_info_2.setVisibility(View.VISIBLE);

				ImageLoader imageLoader2 = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(), lruImageCache);
				holder.question_item_image_info_2.setImageUrl(pdi.getImages().get(1).getImageurl(), imageLoader2);
			case 1:
				holder.question_item_image_info_1.setVisibility(View.VISIBLE);

				ImageLoader imageLoader1 = new ImageLoader(VolleyUtils.getInstance().getRequestQueue(), lruImageCache);
				holder.question_item_image_info_1.setImageUrl(pdi.getImages().get(0).getImageurl(), imageLoader1);
			default:
				break;
			}
		}
		return view;
	}

	private static class ViewHolder {
		TextView advTitle;
		TextView advTime;
		NetworkImageRoundView imageUrl;
		NetworkImageView question_item_image_info_1, question_item_image_info_2, question_item_image_info_3,
				question_item_image_info_4;
	}
}
