package com.shecook.wenyi;

import java.util.List;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WenyiService extends Service {
	
	private static final String DOWNLOAD_ADV = "com.shecook.wenyi.wenyisrevice.downloadadv";
	
	private IBinder binder = new IWenyiServiceBinder();
	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	/**
	 * 创建服务的时候做初始化操作
	 */
	@Override
	public void onCreate() {
		super.onCreate();
	}

	/**
	 * 关闭服务的时候，释放资源
	 */
	@Override
	public void onDestroy() {
	}

	public void downloadAdvService(List list, int position) {
	}
	
	
	/**
	 * cn.roco.aidl.StudentQuery.Stub 由系统根据StudentQuery.aidl自动生成
	 */
	private final class IWenyiServiceBinder extends
			com.shecook.wenyi.IWenyiService.Stub {
		public void downloadAdv(List list, int position) {
			downloadAdvService(list, position);
		}
	}

}
