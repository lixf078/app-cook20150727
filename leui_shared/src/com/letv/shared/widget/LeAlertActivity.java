package com.letv.shared.widget;

import android.app.Activity;
import android.os.Bundle;


/**
 * Created by liangchao on 15-3-13.
 */
public abstract class LeAlertActivity extends Activity{

    protected LeAlertController mAlert;
    protected LeAlertParams mAlertParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAlert = new LeAlertController(this, getWindow());
        mAlertParams = new LeAlertParams(this);

    }

    public void cancel() {
        finish();
    }

    public void dismiss() {
        // This is called after the click, since we finish when handling the
        // click, don't do that again here.
        if (!isFinishing()) {
            finish();
        }
    }

    /**
     * Sets up the alert, including applying the parameters to the alert model,
     * and installing the alert's content.
     *
     * @see #mAlert
     * @see #mAlertParams
     */
    protected void setupAlert() {
        mAlertParams.apply(mAlert);
        mAlert.installContent();
    }

}
