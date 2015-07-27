package com.shecook.wenyi.essay.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shecook.wenyi.R;
import com.shecook.wenyi.model.MyAdvView;
import com.shecook.wenyi.view.RecyclingPagerAdapter;

/**
 * 
 * @author lixufeng
 * @email ja_lxf@163.com
 * 
 */
public class ViewPagerAdapter extends RecyclingPagerAdapter {

	private Context context;
	private ArrayList<MyAdvView> mPageViews;
	private int size;
	private boolean isInfiniteLoop;
	public ViewPagerAdapter(Context context, ArrayList<MyAdvView> mPageViews) {
		this.context = context;
		this.mPageViews = mPageViews;
		isInfiniteLoop = false;
	}

	@Override
	public int getCount() {
		return mPageViews.size();
	}

	/**
	 * get really position
	 * 
	 * @param position
	 * @return
	 */
	private int getPosition(int position) {
		return position % size;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled") @Override
	public View getView(int position, View view, ViewGroup container) {
		ViewHolder holder;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(
     				R.layout.viewpager_view_item, null);
			holder = new ViewHolder();
			view.findViewById(R.id.item_img).setBackgroundResource(R.drawable.wenyi_01);
			TextView textView = (TextView)view.findViewById(R.id.item_title);
			textView.setText("测试链接");
			holder.advTitle = "测试链接";
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		
		return view ;
	}

	/**
	 * @return the isInfiniteLoop
	 */
	public boolean isInfiniteLoop() {
		return isInfiniteLoop;
	}

	/**
	 * @param isInfiniteLoop the isInfiniteLoop to set
	 */
	public ViewPagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
		this.isInfiniteLoop = isInfiniteLoop;
		return this;
	}
	
	private static class ViewHolder {
		String advTitle;
		String eventUrl;
		
	}
}
