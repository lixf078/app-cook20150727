package com.shecook.wenyi.common;


import org.apache.http.util.TextUtils;

import com.shecook.wenyi.HttpStatus;
import com.shecook.wenyi.R;
import com.shecook.wenyi.cookbook.CookbookCollectionActivity;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.exception.SocializeException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.Toast;


/**
 * 
 */
public class CustomShareBoard extends PopupWindow implements OnClickListener {
	
	private String cookbook;
    private UMSocialService mController;
    private Activity mActivity;
    public CustomShareBoard(Activity activity,UMSocialService umSocialService) {
        super(activity);
        cookbook = "";
        this.mActivity = activity;
        this.mController = umSocialService;
        initView(activity);
    }

    public CustomShareBoard(Activity activity,UMSocialService umSocialService, String cookbook) {
        super(activity);
        this.cookbook = cookbook;
        this.mActivity = activity;
        this.mController = umSocialService;
        initView(activity);
    }
    
    @SuppressWarnings("deprecation")
    private void initView(Context context) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.custom_board, null);
        rootView.findViewById(R.id.wechat).setOnClickListener(this);
        rootView.findViewById(R.id.wechat_circle).setOnClickListener(this);
        rootView.findViewById(R.id.sinaweibo).setOnClickListener(this);
        rootView.findViewById(R.id.share_cannel).setOnClickListener(this);
        if(TextUtils.isEmpty(cookbook)){
        	rootView.findViewById(R.id.sinaweibo_bottom).setVisibility(View.GONE);
        	rootView.findViewById(R.id.collection_bottom).setVisibility(View.GONE);
        	rootView.findViewById(R.id.share_collection).setVisibility(View.GONE);
        	rootView.findViewById(R.id.share_homework).setVisibility(View.GONE);
        }else{
        	rootView.findViewById(R.id.sinaweibo_bottom).setVisibility(View.VISIBLE);
        	rootView.findViewById(R.id.collection_bottom).setVisibility(View.VISIBLE);
        	rootView.findViewById(R.id.share_collection).setVisibility(View.VISIBLE);
        	rootView.findViewById(R.id.share_homework).setVisibility(View.VISIBLE);
        }
        rootView.findViewById(R.id.share_collection).setOnClickListener(this);
        rootView.findViewById(R.id.share_homework).setOnClickListener(this);
        rootView.findViewById(R.id.dissmiss).setOnClickListener(this);
        setContentView(rootView);
        setAnimationStyle(R.style.umeng_socialize_shareboard_animation);
        setWidth(LayoutParams.MATCH_PARENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.wechat:
                performShare(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.wechat_circle:
                performShare(SHARE_MEDIA.WEIXIN_CIRCLE);
                break;
            case R.id.share_collection:
            	Intent intent = new Intent(mActivity, CookbookCollectionActivity.class);
				intent.putExtra("recipeid", cookbook);
				intent.putExtra("event", "add");
				mActivity.startActivity(intent);
                break;
            case R.id.share_homework:
            	Intent homework = new Intent(mActivity, CreatePersonalInfoActivity.class);
            	homework.putExtra("ententId", cookbook);
            	homework.putExtra("flag", HttpStatus.PUBLIC_FOR_COOKBOOK);
    			mActivity.startActivity(homework);
                break;
            case  R.id.sinaweibo:
                performShare(SHARE_MEDIA.SINA);
                break;
            case R.id.share_cannel:
                if (this.isShowing()){
                    dismiss();
                }
                break;
            case R.id.dissmiss:{
                if (isShowing()){
                    dismiss();
                }
                break;
            }
            default:
                break;
        }
    }

    public  void doOauthVerify()
    {
        mController.doOauthVerify(mActivity, SHARE_MEDIA.SINA, new SocializeListeners.UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

                Toast.makeText(mActivity, "授权开始", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(Bundle bundle, SHARE_MEDIA share_media) {
                Toast.makeText(mActivity, "授权完成", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA share_media) {

                Toast.makeText(mActivity, "授权错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                Toast.makeText(mActivity, "取消授权", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void performShare(SHARE_MEDIA platform) {
        mController.postShare(mActivity, platform, new SnsPostListener() {
            @Override
            public void onStart() {
                Toast.makeText(mActivity, "开始分享.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                dismiss();
            }
        });
    }
}
