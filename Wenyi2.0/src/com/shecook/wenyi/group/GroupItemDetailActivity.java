package com.shecook.wenyi.group;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.shared.widget.LeBottomSheet;
import com.shecook.wenyi.BaseActivity;
import com.shecook.wenyi.HttpStatus;
import com.shecook.wenyi.HttpUrls;
import com.shecook.wenyi.R;
import com.shecook.wenyi.common.CreatePersonalInfoActivity;
import com.shecook.wenyi.common.volley.Request.Method;
import com.shecook.wenyi.common.volley.Response;
import com.shecook.wenyi.common.volley.Response.ErrorListener;
import com.shecook.wenyi.common.volley.Response.Listener;
import com.shecook.wenyi.common.volley.VolleyError;
import com.shecook.wenyi.common.volley.toolbox.ImageLoader;
import com.shecook.wenyi.common.volley.toolbox.JsonObjectRequest;
import com.shecook.wenyi.common.volley.toolbox.NetworkImageView;
import com.shecook.wenyi.mainpackage.FragmentTabAdapter;
import com.shecook.wenyi.model.group.GroupHotListItem;
import com.shecook.wenyi.util.AppException;
import com.shecook.wenyi.util.Util;
import com.shecook.wenyi.util.volleybox.LruImageCache;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class GroupItemDetailActivity extends BaseActivity implements
		OnClickListener {

	private RadioGroup rgs;
	public List<Fragment> fragments = new ArrayList<Fragment>();
	public ImageView return_img, right_img;
	public TextView middle_title;

	GroupItemDetailSharedFragment sharedFragment;
	GroupItemDetailMemFragment memFragment;
	private TextView group_hot_item_title, group_hot_item_class,
			group_hot_item_shared, group_hot_tiem_content;
	private NetworkImageView item_img;
	private String circleid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_item_detail_2);
		circleid = getIntent().getStringExtra("circleid");
		initView();
		processParam();
	}

	public void initView() {
		right_img = (ImageView) findViewById(R.id.right_img);
		right_img.setVisibility(View.VISIBLE);
		right_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getBottomDialog();
			}
		});

		return_img = (ImageView) findViewById(R.id.return_img);
		return_img.setOnClickListener(GroupItemDetailActivity.this);

		item_img = (NetworkImageView) findViewById(R.id.item_img);
		group_hot_item_title = (TextView) findViewById(R.id.group_hot_item_title);
		group_hot_item_class = (TextView) findViewById(R.id.group_hot_item_class);
		group_hot_item_shared = (TextView) findViewById(R.id.group_hot_item_shared);
		group_hot_tiem_content = (TextView) findViewById(R.id.group_hot_tiem_content);

		sharedFragment = new GroupItemDetailSharedFragment();
		memFragment = new GroupItemDetailMemFragment();
		fragments.add(sharedFragment);
		fragments.add(memFragment);

		rgs = (RadioGroup) findViewById(R.id.group_tabs_rg);

		FragmentTabAdapter tabAdapter = new FragmentTabAdapter(this, fragments,
				R.id.group_personal_edition_content, rgs);
		tabAdapter
				.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
					@Override
					public void OnRgsExtraCheckedChanged(RadioGroup radioGroup,
							int checkedId, int index) {
						Log.d(TAG, "OnRgsExtraCheckedChanged -> " + index);
					}
				});
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.return_img:
			GroupItemDetailActivity.this.onBackPressed();
			break;

		default:
			break;
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (what) {
			case HttpStatus.STATUS_OK:
				LruImageCache lruImageCache = LruImageCache.instance();
				ImageLoader imageLoader = new ImageLoader(VolleyUtils
						.getInstance().getRequestQueue(), lruImageCache);
				item_img.setDefaultImageResId(R.drawable.icon_dialog);
				item_img.setErrorImageResId(R.drawable.icon_dialog);

				item_img.setImageUrl(pdi.getUportrait(), imageLoader);
				group_hot_item_title.setText(pdi.getUfounder());
				group_hot_item_class.setText("成员：" + pdi.getCurrentnum());
				group_hot_item_shared.setText("分享：" + pdi.getShare());
				group_hot_tiem_content.setText(pdi.getDescription());

				break;
			case STATUS_DISBAND:
				getDisbandDialog();
				break;
			case STATUS_DISBAND_OK:
				if(alertdialog.isShowing()){
					alertdialog.dismiss();
				}
				break;
			default:
				break;
			}
		};
	};

	private static final int STATUS_DISBAND = 100;
	private static final int STATUS_DISBAND_OK = 101;
	AlertDialog alertdialog;
	private void getDisbandDialog(){
		alertdialog = Util.showDialog(GroupItemDetailActivity.this, null, "确定要解散圈子吗?",new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				disbandGroup();
			}
		}, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
			}
		});
		alertdialog.show();
	}
	
	Dialog dialog = null;
	private void getBottomDialog() {
		dialog = Util.getBottomDialog(GroupItemDetailActivity.this,
				R.layout.a_common_bottom_dialog_layout);
		ImageView img3 = null;
		Button button3 = null;
		ImageView img4 = null;
		Button button4 = null;
		ImageView img5 = null;
		Button button5 = null;

		switch (status) {
		case 10000: // 创建人
			img3 = (ImageView) dialog
					.findViewById(R.id.wenyi_bottomsheet_img_3);
			button3 = (Button) dialog
					.findViewById(R.id.wenyi_bottomsheet_btn_3);
			img3.setVisibility(View.VISIBLE);
			button3.setVisibility(View.VISIBLE);
			button3.setText("解散圈子");
			img4 = (ImageView) dialog
					.findViewById(R.id.wenyi_bottomsheet_img_4);
			button4 = (Button) dialog
					.findViewById(R.id.wenyi_bottomsheet_btn_4);
			img4.setVisibility(View.VISIBLE);
			button4.setVisibility(View.VISIBLE);
			button4.setText("修改群信息");
			img5 = (ImageView) dialog
					.findViewById(R.id.wenyi_bottomsheet_img_5);
			button5 = (Button) dialog
					.findViewById(R.id.wenyi_bottomsheet_btn_5);
			img5.setVisibility(View.VISIBLE);
			button5.setVisibility(View.VISIBLE);
			button5.setText("发布群分享");
			button3.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
					handler.sendEmptyMessage(STATUS_DISBAND);
				}
			});
			button4.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(GroupItemDetailActivity.this, GroupCreateActivity.class);
					intent.putExtra("circleid", circleid);
					intent.putExtra("title", pdi.getTitle());
					intent.putExtra("desc", pdi.getDescription());
					intent.putExtra("image", pdi.getUfounder());
					startActivity(intent);
				}
			});

			button5.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(GroupItemDetailActivity.this, CreatePersonalInfoActivity.class);
					startActivity(intent);
					dialog.dismiss();
				}
			});
			break;
		case 10001:// 管理员
			img3 = (ImageView) dialog
					.findViewById(R.id.wenyi_bottomsheet_img_3);
			button3 = (Button) dialog
					.findViewById(R.id.wenyi_bottomsheet_btn_3);
			img3.setVisibility(View.VISIBLE);
			button3.setVisibility(View.VISIBLE);
			button3.setText("修改群信息");
			img4 = (ImageView) dialog
					.findViewById(R.id.wenyi_bottomsheet_img_4);
			button4 = (Button) dialog
					.findViewById(R.id.wenyi_bottomsheet_btn_4);
			img4.setVisibility(View.VISIBLE);
			button4.setVisibility(View.VISIBLE);
			button4.setText("发布群分享");
			img5 = (ImageView) dialog
					.findViewById(R.id.wenyi_bottomsheet_img_5);
			button5 = (Button) dialog
					.findViewById(R.id.wenyi_bottomsheet_btn_5);
			img5.setVisibility(View.VISIBLE);
			button5.setVisibility(View.VISIBLE);
			button5.setText("退出圈子");
			break;
		case 10002:// 圈子成员
			img4 = (ImageView) dialog
					.findViewById(R.id.wenyi_bottomsheet_img_4);
			button4 = (Button) dialog
					.findViewById(R.id.wenyi_bottomsheet_btn_4);
			img4.setVisibility(View.VISIBLE);
			button4.setVisibility(View.VISIBLE);
			button4.setText("发布群分享");
			
			img5 = (ImageView) dialog
					.findViewById(R.id.wenyi_bottomsheet_img_5);
			button5 = (Button) dialog
					.findViewById(R.id.wenyi_bottomsheet_btn_5);
			img5.setVisibility(View.VISIBLE);
			button5.setVisibility(View.VISIBLE);
			button5.setText("退出圈子");
			break;
		case 10004:// 非圈子成员
			img5 = (ImageView) dialog
					.findViewById(R.id.wenyi_bottomsheet_img_5);
			button5 = (Button) dialog
					.findViewById(R.id.wenyi_bottomsheet_btn_5);
			img5.setVisibility(View.VISIBLE);
			button5.setVisibility(View.VISIBLE);
			button5.setText("申请加入");
			break;
		default:
			break;
		}
		dialog.show();
	}

	LeBottomSheet mBottomSheet;

	public void disbandGroup() {
		JSONObject paramObject = new JSONObject();
		try {
			paramObject.put("circleid", circleid);
			getGroupInfo(HttpUrls.GROUP_CIRCLE_DISBAND, paramObject,
					disbandResultListener, listErrorListener);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	Listener<JSONObject> disbandResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			Log.d(TAG,
					"disbandResultListener onResponse -> " + result.toString());
			
		}
	};
	
	public void processParam() {
		JSONObject paramObject = new JSONObject();
		try {
			paramObject.put("circleid", circleid);
			getGroupInfo(HttpUrls.GROUP_ITEM_DETAIL, paramObject,
					listResultListener, listErrorListener);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getGroupInfo(String url, JSONObject paramsub,
			Listener<JSONObject> resultListener, ErrorListener errorListener) {
		JSONObject jsonObject = new JSONObject();
		JSONObject commonsub = Util
				.getCommonParam(GroupItemDetailActivity.this);
		try {
			if (paramsub != null) {
				jsonObject.put("param", paramsub);
			}
			commonsub.put("mid", "5A1469CD-4819-4863-A934-8871CA1A0281");
			commonsub.put("token", "591f3c51eca2483b932ed1a64b896a63");
			jsonObject.put("common", commonsub);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JsonObjectRequest wenyiRequest = new JsonObjectRequest(Method.POST,
				url, jsonObject, resultListener, errorListener);

		try {
			VolleyUtils.getInstance().addReequest(wenyiRequest);
		} catch (AppException e) {
			e.printStackTrace();
		}
	}

	Listener<JSONObject> listResultListener = new Listener<JSONObject>() {

		@Override
		public void onResponse(JSONObject result) {
			Log.d(TAG,
					"catalogResultListener onResponse -> " + result.toString());
			initData(result, 0);
		}
	};

	ErrorListener listErrorListener = new Response.ErrorListener() {
		@Override
		public void onErrorResponse(VolleyError error) {
			Log.e(TAG, error.getMessage(), error);
		}
	};

	GroupHotListItem pdi = null;
	public int status;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	private void initData(JSONObject jsonObject, int flag) {
		if (jsonObject != null) {
			try {
				if (!jsonObject.isNull("statuscode")
						&& 200 == jsonObject.getInt("statuscode")) {
					if (!jsonObject.isNull("data")) {
						JSONObject data = jsonObject.getJSONObject("data");
						if (data.has("detail")) {
							JSONObject jb = data.getJSONObject("detail");
							pdi = new GroupHotListItem();
							pdi.setId(jb.getString("id"));
							pdi.setUid(jb.getString("uid"));
							pdi.setUfounder(jb.getString("ufounder"));
							pdi.setUportrait(jb.getString("uportrait"));
							pdi.setTitle(jb.getString("title"));
							pdi.setDescription(jb.getString("description"));
							pdi.setIconurl(jb.getString("iconurl"));
							pdi.setTotalnum(jb.getString("totalnum"));
							pdi.setShare(jb.getString("share"));
							pdi.setCurrentnum(jb.getString("currentnum"));
							pdi.setDatecreated(jb.getString("datecreated"));
							pdi.setDateupd(jb.getString("dateupd"));
						}
						if (pdi != null) {
							status = data.getInt("circle_status");
							pdi.setStatus(status);
						}
					}
				} else {
					Toast.makeText(GroupItemDetailActivity.this,
							"" + jsonObject.getString("errmsg"),
							Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		handler.sendEmptyMessage(HttpStatus.STATUS_OK);
	}

}
