package com.shecook.wenyi.essay.adapter;

import android.view.View;
import android.view.ViewGroup;
import com.shecook.wenyi.R;
import com.shecook.wenyi.view.RecyclingPagerAdapter;

public class AdvertImagePagerAdapterDecorator extends RecyclingPagerAdapter {
	private ViewPagerAdapter adapter;

	public AdvertImagePagerAdapterDecorator(ViewPagerAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup container) {
		return adapter.getView(position, convertView, container);
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

}
