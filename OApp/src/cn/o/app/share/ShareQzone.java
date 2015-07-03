package cn.o.app.share;

import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import cn.o.app.OUtil;

public class ShareQzone extends ShareBase {

	protected static String sAppId;

	public ShareQzone(Context context) {
		setContext(context);
	}

	@Override
	public void share() {
		if (sAppId == null) {
			if (mListener != null) {
				mListener.onError(this);
			}
		}
		Bundle bundle = new Bundle();
		bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
		bundle.putString(QzoneShare.SHARE_TO_QQ_TITLE, mTitle);
		bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, mText);
		bundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, mUrl);
		bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, OUtil.asArrayList(new String[] { mImageUrl }));
		Tencent.createInstance(sAppId, mContext).shareToQzone((Activity) mContext, bundle, new IUiListener() {

			@Override
			public void onError(UiError arg0) {
				if (mListener != null) {
					mListener.onError(ShareQzone.this);
				}
			}

			@Override
			public void onComplete(Object arg0) {
				if (mListener != null) {
					mListener.onComplete(ShareQzone.this);
				}
			}

			@Override
			public void onCancel() {
				if (mListener != null) {
					mListener.onCancel(ShareQzone.this);
				}
			}
		});
	}

	@Override
	public int getPlatform() {
		return IShare.PLATFORM_QZONE;
	}

	public static void setAppId(String appId) {
		sAppId = appId;
	}

	public static String getAppId() {
		return sAppId;
	}

}
