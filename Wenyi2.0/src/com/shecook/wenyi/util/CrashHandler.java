package com.shecook.wenyi.util;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.shecook.wenyi.R;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

	public static final boolean DEBUG = false;

	private Thread.UncaughtExceptionHandler mDefaultHandler;

	private static CrashHandler mInstance;

	private Context mContext;

	private CrashHandler() {

	}

	public static CrashHandler getInstance() {
		if (null == mInstance) {
			mInstance = new CrashHandler();
		}
		return mInstance;
	}

	public void init(Context context) {
		mContext = context;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(CrashHandler.this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		try {
			throwable.printStackTrace();
			// handleException(thread, throwable);
			Log.d("lixufeng", "uncaughtException " + throwable.getMessage());
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(10);
		} catch (Throwable e) {
		}
	}

	private boolean handleException(Thread thread, Throwable throwable) {
		final String msg = throwable.getLocalizedMessage();
		final StackTraceElement[] stack = throwable.getStackTrace();
		final String message = throwable.getMessage();
		Throwable throwableCause = throwable.getCause();
		StackTraceElement[] cause = null;
		if(throwableCause != null){
			cause = throwableCause.getStackTrace();
		}
		

		// 可以只创建一个文件，以后全部往里面append然后发送，这样就会有重复的信息，个人不推荐
		String fileName = "crash-" + System.currentTimeMillis()
				+ ".log";
		File file = new File(Environment.getExternalStorageDirectory(),
				fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file, true);
			fos.write(message.getBytes());
			String exe = "Caused by:  " + throwable.getMessage();
			fos.write(exe.getBytes());
			for (int i = 0; i < stack.length; i++) {
				fos.write("\n".getBytes());
				fos.write(stack[i].toString().getBytes());
			}
			fos.write("\n\r".getBytes());
			if(cause != null){
				String cau = "Caused by:  " + throwableCause.getMessage();
				fos.write(cau.getBytes());
				for (int i = 0; i < cause.length; i++) {
					fos.write("\n".getBytes());
					fos.write(cause[i].toString().getBytes());
				}
			}
			fos.flush();
			fos.close();
		} catch (Exception e) {
		}
	
		/*new Thread() {
			@Override
			public void run() {}

		}.start();*/
		return false;
	}

}
