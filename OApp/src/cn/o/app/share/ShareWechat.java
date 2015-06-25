package cn.o.app.share;

import android.content.Context;
import cn.o.app.OUtil;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class ShareWechat extends ShareBase {

	public static final String TRANSACTION = "webpage";

	protected static String sAppId;

	public ShareWechat(Context context) {
		setContext(context);
	}

	@Override
	public void share() {
		if (sAppId == null) {
			if (mListener != null) {
				mListener.onError(this);
			}
		}
		IWXAPI api = WXAPIFactory.createWXAPI(mContext, sAppId, false);
		if (!api.isWXAppInstalled()) {
			if (mListener != null) {
				mListener.onError(this);
			}
			return;
		}
		WXWebpageObject web = new WXWebpageObject();
		web.webpageUrl = mUrl;
		WXMediaMessage msg = new WXMediaMessage(web);
		msg.title = mTitle;
		msg.description = mText;
		msg.thumbData = OUtil.bitmap2ByteArray(OUtil.getAppIcon(mContext));
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = TRANSACTION + System.currentTimeMillis();
		req.message = msg;
		req.scene = getScene();
		api.sendReq(req);
	}

	protected int getScene() {
		return SendMessageToWX.Req.WXSceneSession;
	}

	@Override
	public int getPlatform() {
		return IShare.PLATFORM_WECHAT;
	}

	public static void setAppId(String appId) {
		sAppId = appId;
	}

	public static String getAppId() {
		return sAppId;
	}
}
