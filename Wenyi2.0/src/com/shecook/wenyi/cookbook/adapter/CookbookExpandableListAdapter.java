package com.shecook.wenyi.cookbook.adapter;

import java.util.LinkedList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.shecook.wenyi.R;
import com.shecook.wenyi.model.CookbookCatalog;

public class CookbookExpandableListAdapter extends BaseExpandableListAdapter {
       
	LinkedList<CookbookCatalog> catalogs ;
	private Context mContext;
	
	public CookbookExpandableListAdapter(Context context, LinkedList<CookbookCatalog> catalogs) {
		super();
		mContext = context;
		this.catalogs = catalogs;
	}

	public CookbookExpandableListAdapter() {
		super();
	}

	@Override
    public Object getChild(int groupPosition, int childPosition) {
		CookbookCatalog cbc = catalogs.get(groupPosition).getCata_items().get(childPosition);
		return cbc;
	}

	@Override
    public long getChildId(int groupPosition, int childPosition) {
		CookbookCatalog cbc = catalogs.get(groupPosition).getCata_items().get(childPosition);
		return Long.parseLong(cbc.getId());
	}

	@Override
    public View getChildView(int groupPosition, int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
		CookbookCatalog cbc = catalogs.get(groupPosition).getCata_items().get(childPosition);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
     				R.layout.cookbook_extendlistview_item, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.cookbook_listitem_detail_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.text.setText(cbc.getCataname());
		holder.text.setBackgroundColor(mContext.getResources().getColor(R.color.brown));
		return convertView;
	}

	@Override
    public int getChildrenCount(int groupPosition) {
		return catalogs.get(groupPosition).getCata_items().size();
	}

	@Override
    public Object getGroup(int groupPosition) {
		return catalogs.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return catalogs.size();
	}

	@Override
    public long getGroupId(int groupPosition) {
		return Long.parseLong(catalogs.get(groupPosition).getId());
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		CookbookCatalog cbc = catalogs.get(groupPosition);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
     				R.layout.cookbook_extendlistview_item, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.cookbook_listitem_detail_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.text.setText(cbc.getCataname());
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
    public boolean isChildSelectable(int groupPosition,
            int childPosition) {
        return true;
    }
	private static class ViewHolder {
		TextView text;
	}
}
