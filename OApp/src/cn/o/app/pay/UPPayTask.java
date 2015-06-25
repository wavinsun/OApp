package cn.o.app.pay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.o.app.event.Listener;
import cn.o.app.event.listener.OnActivityResultListener;
import cn.o.app.ui.core.IActivityResultCatcher;

import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

//银联支付
public class UPPayTask extends OPayTask {

	// 交易流水号
	protected String mTradeNo;

	protected OnActivityResultListener mOnActivityResultListener;

	protected boolean mDebug = true;

	public boolean isDebug() {
		return mDebug;
	}

	public void setDebug(boolean debug) {
		if (mStarted || mStoped) {
			return;
		}
		mDebug = debug;
	}

	public String getTradeNo() {
		return mTradeNo;
	}

	public void setTradeNo(String tradeNo) {
		if (mStarted || mStoped) {
			return;
		}
		mTradeNo = tradeNo;
	}

	@Override
	protected void onStart() {
		String mode = mDebug ? "01" : "00";
		UPPayAssistEx.startPayByJAR((Activity) mContext, PayActivity.class,
				null, null, mTradeNo, mode);
	}

	@Override
	public void addListener(Listener listener) {
		super.addListener(listener);
		attachToContext();
	}

	@Override
	public void setContext(Context context) {
		super.setContext(context);
		attachToContext();
	}

	protected void attachToContext() {
		if (mContext == null) {
			return;
		}
		if (!(mContext instanceof IActivityResultCatcher)) {
			return;
		}
		if (mOnActivityResultListener == null) {
			mOnActivityResultListener = new OnActivityResultListener() {

				@Override
				public void onActivityResult(Context context, int requestCode,
						int resultCode, Intent data) {
					if (data == null) {
						return;
					}
					Bundle extras = data.getExtras();
					if (extras == null) {
						return;
					}
					String payResult = extras.getString("pay_result");
					if (payResult == null) {
						return;
					}
					if (payResult.equalsIgnoreCase("success")) {
						mStatus = STATUS_UP_SUCCESS;
						for (OPayListener listener : getListeners(OPayListener.class)) {
							listener.onComplete(UPPayTask.this);
						}
					} else {
						if (payResult.equalsIgnoreCase("fail")) {
							mStatus = STATUS_UP_FAIL;
						} else if (payResult.equalsIgnoreCase("cancel")) {
							mStatus = STATUS_UP_CANCEL;
						}
						for (OPayListener listener : getListeners(OPayListener.class)) {
							listener.onError(UPPayTask.this, null);
						}
					}
					((IActivityResultCatcher) context)
							.removeOnActivityResultListener(mOnActivityResultListener);
				}
			};
		}
		IActivityResultCatcher catcher = (IActivityResultCatcher) mContext;
		catcher.addOnActivityResultListener(mOnActivityResultListener);
	}
}
