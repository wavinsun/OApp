package cn.o.app.ui;

import java.util.ArrayList;
import java.util.List;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import cn.jpush.android.api.JPushInterface;
import cn.o.app.App;
import cn.o.app.R;
import cn.o.app.core.event.listener.VersionUpdateListener;

@SuppressLint("InflateParams")
public class ActivityX extends OActivity {

	private static final int NEW_VERSION_STATE_UNKNOWN = -1;

	private static final int NEW_VERSION_STATE_NO = 0;

	private static final int NEW_VERSION_STATE_YES = 1;

	protected FeedbackAgent mFeedbackAgent;

	protected List<VersionUpdateListener> mVersionUpdateListeners;

	protected static Object sSync = new Object();

	protected static int sNewVersionState = NEW_VERSION_STATE_UNKNOWN;

	protected UmengUpdateListener mUmengUpdateListener;

	protected UmengDialogButtonListener mUmengDialogButtonListener;

	@Override
	protected void onResume() {
		super.onResume();
		if (App.getApp() != null) {
			if (App.getApp().isUmengEnabled()) {
				MobclickAgent.onResume(this);
			}
			if (App.getApp().isJPushEneabled()) {
				JPushInterface.onResume(this);
			}
		}
	}

	@Override
	protected void onPause() {
		if (App.getApp() != null) {
			if (App.getApp().isUmengEnabled()) {
				MobclickAgent.onPause(this);
			}
			if (App.getApp().isJPushEneabled()) {
				JPushInterface.onPause(this);
			}
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (!mFinished) {
			if (mVersionUpdateListeners != null) {
				mVersionUpdateListeners.clear();
			}
		}
		super.onDestroy();
	}

	@Override
	public void finish() {
		if (mVersionUpdateListeners != null) {
			mVersionUpdateListeners.clear();
		}
		super.finish();
	}

	public boolean hasNewVersion() {
		if (sNewVersionState == NEW_VERSION_STATE_UNKNOWN) {
			checkNewVersion(null);
		}
		return sNewVersionState == NEW_VERSION_STATE_YES;
	}

	public void checkNewVersion(VersionUpdateListener listener) {
		if (App.getApp() == null || !App.getApp().isUmengEnabled()) {
			return;
		}
		if (listener != null) {
			if (mVersionUpdateListeners == null) {
				mVersionUpdateListeners = new ArrayList<VersionUpdateListener>();
			}
			mVersionUpdateListeners.add(listener);
		}
		UmengUpdateAgent.setDialogListener(null);
		if (this.mUmengUpdateListener == null) {
			this.mUmengUpdateListener = new UmengUpdateListener() {

				@Override
				public void onUpdateReturned(int statusCode, final UpdateResponse updateInfo) {
					boolean hasNewVersion = statusCode == UpdateStatus.Yes;
					synchronized (sSync) {
						if (hasNewVersion) {
							sNewVersionState = NEW_VERSION_STATE_YES;
						} else {
							sNewVersionState = NEW_VERSION_STATE_NO;
						}
					}
					if (mFinished) {
						return;
					}
					if (mVersionUpdateListeners != null) {
						if (mVersionUpdateListeners.size() != 0) {
							boolean interceptDialog = false;
							for (VersionUpdateListener listener : mVersionUpdateListeners) {
								if (hasNewVersion) {
									boolean intercept = listener.onYes(updateInfo.version);
									interceptDialog = interceptDialog ? true : intercept;
								} else {
									listener.onNo();
								}
							}
							if (hasNewVersion && !interceptDialog) {
								if (mUmengDialogButtonListener == null) {
									mUmengDialogButtonListener = new UmengDialogButtonListener() {

										@Override
										public void onClick(int status) {
											if (mVersionUpdateListeners != null) {
												for (VersionUpdateListener listener : mVersionUpdateListeners) {
													switch (status) {
													case UpdateStatus.Update:
														listener.onUpdate(updateInfo.version);
														break;
													case UpdateStatus.Ignore:
													case UpdateStatus.NotNow:
														listener.onUpdateCancel(updateInfo.version);
														break;
													}
												}
												mVersionUpdateListeners.clear();
											}
										}
									};
								}
								UmengUpdateAgent.setDialogListener(mUmengDialogButtonListener);
								UmengUpdateAgent.showUpdateDialog(ActivityX.this, updateInfo);
							}
						}
					}
				}
			};
		}
		UmengUpdateAgent.setUpdateListener(this.mUmengUpdateListener);
		UmengUpdateAgent.forceUpdate(this);
	}

	@Override
	protected View getWaitingViewLayout() {
		return new WaitingView(this);
	}

	public void feedback() {
		if (App.getApp() == null || !App.getApp().isUmengEnabled()) {
			return;
		}
		if (mFeedbackAgent == null) {
			mFeedbackAgent = new FeedbackAgent(this);
			mFeedbackAgent.sync();
		}
		mFeedbackAgent.startFeedbackActivity();
	}

	static class WaitingView extends OView {

		protected ImageView mWaitingProgressView;

		protected Animation mWaitingProgressAnim;

		public WaitingView(Context context) {
			super(context);
		}

		@Override
		protected void init() {
			super.init();
			this.setContentView(R.layout.view_waiting);

			mWaitingProgressView = findViewById(R.id.waiting_progress, ImageView.class);
			mWaitingProgressAnim = AnimationUtils.loadAnimation(getContext(), R.anim.waiting_progress);
		}

		@Override
		protected void onAttachedToWindow() {
			super.onAttachedToWindow();
			mWaitingProgressView.startAnimation(mWaitingProgressAnim);
		}

	}

}
