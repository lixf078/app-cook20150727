package com.shecook.wenyi.personal.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shecook.wenyi.R;
import com.shecook.wenyi.model.personal.FoodBean;
import com.umeng.socom.Log;

public class PersonalShiCaiAdaptre extends BaseAdapter {

    private static final String TAG = "ShiCaiAdaptre";
    private OnShiCaiClickListener shicaiClickListener;

    private Context mContext;
    private List<FoodBean> itemList;

    public PersonalShiCaiAdaptre(Context context, List<FoodBean> itemList, OnShiCaiClickListener shicaiClickListener) {
        this.mContext = context;
        this.itemList = itemList;
        this.shicaiClickListener = shicaiClickListener;
    }
    
    @Override
    public int getCount() {
    	if(itemList.size() == 0){
    		return 0;
    	}
        return itemList.get(catalog).getSubList().size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
		
		View view = LayoutInflater.from(mContext).inflate(R.layout.leibie, null);
		TextView leibieText = (TextView) view.findViewById(R.id.leibieImage);
		Log.e("lixufeng", "getView " + catalog);
		Log.e("lixufeng", "getView " + itemList.get(catalog));
		leibieText.setText(itemList.get(catalog).getSubList().get(position).getName());
		
		leibieText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				shicaiClickListener.refreshFood((TextView)arg0, position);
			}

		});
		return view;
	}

    private class ViewHolder {
        TextView shicaiName;
    }

	public interface OnShiCaiClickListener {
		void refreshFood(TextView button, int position);
	}
	
	private int catalog = 0;
	public void updateCatalog(int catalog){
		this.catalog = catalog;
	}
}
