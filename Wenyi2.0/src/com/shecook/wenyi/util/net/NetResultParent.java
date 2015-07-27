package com.shecook.wenyi.util.net;

import com.shecook.wenyi.R;


/**
 * 实现{@link NetResult} ,对一些不必要的抽象方法进行屏蔽
 * */
public abstract class NetResultParent implements NetResult {

	ResultType resultType;
	private Object tag;

	@Override
	public void setResultType(ResultType resultType) {
		this.resultType = resultType;
	}

	@Override
	public ResultType getResultType() {
		return resultType;
	}

	@Override
	public boolean checkResultLegitimacy() {
		return true;
	}

	@Override
	public void setReqeustDeliveredTag(Object tag) {
		this.tag = tag;

	}

	@Override
	public Object getReqeustDeliveredTag() {
		return tag;
	}

	// @Override
	// public String updateLocalData() {
	// return null;
	// }

}
