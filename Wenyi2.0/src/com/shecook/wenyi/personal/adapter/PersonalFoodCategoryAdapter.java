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
import com.shecook.wenyi.model.personal.FoodBean;

public class PersonalFoodCategoryAdapter extends BaseAdapter {


	private Context context;
	private OnCatalogClickListener oncatalogClickListener;
	private LinkedList<FoodBean> itemList;

	public PersonalFoodCategoryAdapter(Context context,
			OnCatalogClickListener oncatalogClickListener, LinkedList<FoodBean> itemList) {
		this.context = context;
		this.oncatalogClickListener = oncatalogClickListener;
		this.itemList = itemList;
	}

	@Override
	public int getCount() {
		return itemList.size();
	}

	@Override
	public Object getItem(int position) {
		return itemList.get(position);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		if(position > itemList.size()){
			return null;
		}
		View view = LayoutInflater.from(context).inflate(R.layout.leibie, null);
		TextView leibieText = (TextView) view.findViewById(R.id.leibieImage);
		leibieText.setText(itemList.get(position).getName());
		
		leibieText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				oncatalogClickListener.refreshCatalog((TextView)arg0, position);
			}

		});
		return view;
	}

	public interface OnCatalogClickListener {
		void refreshCatalog(TextView button, int position);
	}

	@Override
	public long getItemId(int arg0) {
		return Long.parseLong(itemList.get(arg0).getId());
	}

}
