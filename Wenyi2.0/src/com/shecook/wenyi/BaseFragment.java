package com.shecook.wenyi;

import android.support.v4.app.Fragment;
import android.view.MotionEvent;


public abstract class BaseFragment extends Fragment {
	public abstract boolean onBackpress();
	public abstract boolean dispatchTouchEvent(MotionEvent ev);
}
