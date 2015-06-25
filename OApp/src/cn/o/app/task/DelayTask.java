package cn.o.app.task;

import android.os.Handler;

public class DelayTask implements IStopable {
	protected boolean mStoped;

	protected Handler mHandler;

	protected Runnable mRunnable;

	protected Runnable mRunnableWrapper;

	protected long mDelay;

	public DelayTask(Runnable runnable, long delay) {
		mHandler = new Handler();
		mRunnable = runnable;
		mRunnableWrapper = new Runnable() {

			@Override
			public void run() {
				mStoped = true;
				mRunnable.run();
			}
		};
		mDelay = delay;
		mStoped = true;
	}

	@Override
	public boolean isRunInBackground() {
		return true;
	}

	@Override
	public void setRunInBackground(boolean runInBackground) {

	}

	@Override
	public boolean isStoped() {
		return mStoped;
	}

	@Override
	public boolean stop() {
		if (mStoped) {
			return false;
		}
		mHandler.removeCallbacksAndMessages(null);
		mStoped = true;
		return true;
	}

	// 可以重复执行start
	public DelayTask start() {
		stop();
		mHandler.postDelayed(mRunnableWrapper, mDelay);
		mStoped = false;
		return this;
	}

	public DelayTask start(long delay) {
		mDelay = delay;
		return start();
	}

}
