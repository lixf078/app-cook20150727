package com.shecook.wenyi;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import com.shecook.wenyi.util.CrashHandler;
import com.shecook.wenyi.util.volleybox.VolleyUtils;

public class MainApplication extends Application {
	
	// 单例模式
	private static MainApplication appContext;
	VolleyUtils mVolleyUtils = null;
	
	
    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        mVolleyUtils = VolleyUtils.getInstance();
        mVolleyUtils.initVolley(this);
    }
    
	public static MainApplication getInstance() {
		return appContext;
	}
	
	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

	/** 获取版本号 */
	public String getAppVerName() {
		return getPackageInfo().versionName;
	}
	
}
