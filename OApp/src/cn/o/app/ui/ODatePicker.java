package cn.o.app.ui;

import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.o.app.AppUtil;
import cn.o.app.R;
import kankan.wheel.widget.NumericWheelAdapter;
import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;

/**
 * Date picker like iOS
 */
@SuppressLint("InflateParams")
@SuppressWarnings("deprecation")
public class ODatePicker {

	/**
	 * Date picker listener
	 */
	public static abstract class OnPickDateListener {

		/**
		 * Submit date picked
		 * 
		 * @param picker
		 * @param date
		 */
		public abstract void onPicked(ODatePicker picker, Date date);

		/**
		 * Give date while picking
		 * 
		 * @param picker
		 * @param date
		 */
		public void onPicking(ODatePicker picker, Date date) {

		}

		/**
		 * Cancel pick
		 * 
		 * @param picker
		 */
		public void onCancel(ODatePicker picker) {

		}

		/**
		 * Get text for InfoToast when picked date is above maximum date
		 * 
		 * @param picker
		 * @param maxDate
		 * @return
		 */
		public String onPickedAboveMaxDate(ODatePicker picker, Date maxDate) {
			return "无效时间";
		}

		/**
		 * Get text for InfoToast when picked date is down minimum date
		 * 
		 * @param picker
		 * @param minDate
		 * @return
		 */
		public String onPickedDownMinDate(ODatePicker picker, Date minDate) {
			return "无效时间";
		}

	}

	/** Listener */
	protected OnPickDateListener mOnPickDateListener;

	/** Dialog */
	protected ODialog mDialog;

	protected Context mContext;

	/** Whether pick hour minute */
	protected boolean mPickTime;

	/** Start year to pick */
	protected Integer mStartYear;

	/** End year to pick */
	protected Integer mEndYear;

	protected View mContentView;

	/** Year wheel view */
	protected WheelView mYearView;

	/** Month wheel view */
	protected WheelView mMonthView;

	/** Day wheel view */
	protected WheelView mDayView;

	/** Hour wheel view */
	protected WheelView mHourView;

	/** Minute wheel view */
	protected WheelView mMinuteView;

	protected InfoToast mInfoToast;

	/** Date picked */
	protected Date mPickedDate;

	protected Date mMinDate;

	protected Date mMaxDate;

	public ODatePicker(Context context) {
		mContext = context;
	}

	public void setMinDate(Date minDate) {
		mMinDate = minDate;
	}

	public void setMaxDate(Date maxDate) {
		mMaxDate = maxDate;
	}

	public void setStartYear(int startYear) {
		if (mDialog != null) {
			return;
		}
		mStartYear = startYear;
	}

	public void setEndYear(int endYear) {
		if (mDialog != null) {
			return;
		}
		mEndYear = endYear;
	}

	public boolean isPickTime() {
		return mPickTime;
	}

	public void setPickTime(boolean pickTime) {
		if (mDialog != null) {
			return;
		}
		mPickTime = pickTime;
	}

	public void setListener(OnPickDateListener listener) {
		mOnPickDateListener = listener;
	}

	public void ok() {
		if (mDialog == null) {
			return;
		}
		if (mOnPickDateListener != null) {
			int year = mYearView.getCurrentItem() + mStartYear;
			int month = mMonthView.getCurrentItem() + 1;
			int day = mDayView.getCurrentItem() + 1;
			int hour = mPickTime ? mHourView.getCurrentItem() : 0;
			int minute = mPickTime ? mMinuteView.getCurrentItem() : 0;
			mPickedDate = AppUtil.getDate(year, month, day, hour, minute);
			if (mMaxDate != null && mPickedDate.getTime() > mMaxDate.getTime()) {
				if (mOnPickDateListener != null) {
					mInfoToast.show(mOnPickDateListener.onPickedAboveMaxDate(ODatePicker.this, mMaxDate), 3000);
				} else {
					mInfoToast.show("无效时间", 3000);
				}
				return;
			}
			if (mMinDate != null && mPickedDate.getTime() < mMinDate.getTime()) {
				if (mOnPickDateListener != null) {
					mInfoToast.show(mOnPickDateListener.onPickedDownMinDate(ODatePicker.this, mMinDate), 3000);
				} else {
					mInfoToast.show("无效时间", 3000);
				}
				return;
			}
			if (mOnPickDateListener != null) {
				mOnPickDateListener.onPicked(ODatePicker.this, mPickedDate);
			}
		}
		dismiss();
	}

	public void cancel() {
		if (mDialog == null) {
			return;
		}
		if (mOnPickDateListener != null) {
			mOnPickDateListener.onCancel(this);
		}
		mDialog.cancel();
		mDialog = null;
	}

	public void dismiss() {
		if (mDialog == null) {
			return;
		}
		mDialog.dismiss();
		mDialog = null;
	}

	public void show() {
		show(null);
	}

	public void show(Date pickedDate) {
		if (mDialog != null) {
			mDialog.show();
			return;
		}
		Date current = null;
		LayoutInflater inflater = LayoutInflater.from(mContext);
		mContentView = inflater.inflate(R.layout.date_picker, null);
		mContentView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cancel();
			}
		});
		mContentView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cancel();
			}
		});
		mContentView.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ok();
			}
		});
		mInfoToast = (InfoToast) mContentView.findViewById(R.id.info_toast);
		mYearView = (WheelView) mContentView.findViewById(R.id.year);
		if (mStartYear == null) {
			current = new Date();
			mStartYear = AppUtil.getYear(current) - 50;
		}
		if (mEndYear == null) {
			current = current != null ? current : new Date();
			mEndYear = AppUtil.getYear(current) + 50;
		}
		if (pickedDate == null) {
			current = current != null ? current : new Date();
			pickedDate = current;
		}
		mYearView.setAdapter(new NumericWheelAdapter(mStartYear, mEndYear));
		mYearView.setCyclic(true);
		mYearView.setLabel("年");
		mYearView.setCurrentItem(AppUtil.getYear(pickedDate) - mStartYear);
		mMonthView = (WheelView) mContentView.findViewById(R.id.month);
		mMonthView.setAdapter(new NumericWheelAdapter(1, 12));
		mMonthView.setCyclic(true);
		mMonthView.setLabel("月");
		mMonthView.setCurrentItem(AppUtil.getMonth(pickedDate) - 1);
		mDayView = (WheelView) mContentView.findViewById(R.id.day);
		mDayView.setCyclic(true);
		mDayView.setAdapter(new NumericWheelAdapter(1, AppUtil.getDaysOfMonth(pickedDate)));
		mDayView.setLabel("日");
		mDayView.setCurrentItem(AppUtil.getDay(pickedDate) - 1);
		mHourView = (WheelView) mContentView.findViewById(R.id.hour);
		mMinuteView = (WheelView) mContentView.findViewById(R.id.min);
		if (mPickTime) {
			mHourView.setVisibility(View.VISIBLE);
			mMinuteView.setVisibility(View.VISIBLE);
			mHourView.setAdapter(new NumericWheelAdapter(0, 23));
			mHourView.setCyclic(true);
			mHourView.setLabel("时");
			mHourView.setCurrentItem(pickedDate.getHours());
			mMinuteView.setAdapter(new NumericWheelAdapter(0, 59));
			mMinuteView.setCyclic(true);
			mMinuteView.setLabel("分");
			mMinuteView.setCurrentItem(pickedDate.getMinutes());
		} else {
			mHourView.setVisibility(View.GONE);
			mMinuteView.setVisibility(View.GONE);
		}

		OnWheelChangedListener onWheelChangedListener4Leap = new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year = mYearView.getCurrentItem() + mStartYear;
				int month = mMonthView.getCurrentItem() + 1;
				int dayIndex = mDayView.getCurrentItem();
				int dayCount = AppUtil.getDaysOfMonth(year, month);
				mDayView.setAdapter(new NumericWheelAdapter(1, dayCount));
				if (dayIndex >= dayCount) {
					mDayView.setCurrentItem(0);
				}
			}
		};
		mYearView.addChangingListener(onWheelChangedListener4Leap);
		mMonthView.addChangingListener(onWheelChangedListener4Leap);

		int screenHeight = mContext.getResources().getDisplayMetrics().heightPixels;
		int textSize = (int) ((screenHeight / 100.0) * (mPickTime ? 3 : 4));
		mYearView.TEXT_SIZE = textSize;
		mMonthView.TEXT_SIZE = textSize;
		mDayView.TEXT_SIZE = textSize;
		mHourView.TEXT_SIZE = textSize;
		mMinuteView.TEXT_SIZE = textSize;

		OnWheelChangedListener onWheelChangedListener = new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year = mYearView.getCurrentItem() + mStartYear;
				int month = mMonthView.getCurrentItem() + 1;
				int day = mDayView.getCurrentItem() + 1;
				int hour = mPickTime ? mHourView.getCurrentItem() : 0;
				int minute = mPickTime ? mMinuteView.getCurrentItem() : 0;
				mPickedDate = AppUtil.getDate(year, month, day, hour, minute);
				if (mOnPickDateListener != null) {
					mOnPickDateListener.onPicking(ODatePicker.this, mPickedDate);
				}
			}

		};
		mYearView.addChangingListener(onWheelChangedListener);
		mMonthView.addChangingListener(onWheelChangedListener);
		mDayView.addChangingListener(onWheelChangedListener);
		mHourView.addChangingListener(onWheelChangedListener);
		mMinuteView.addChangingListener(onWheelChangedListener);

		mDialog = new ODialog(mContext);
		mDialog.setWindowAnimations(R.style.DatePickerAnim);
		mDialog.setContentView(mContentView,
				new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				cancel();
			}

		});
		mDialog.requestHFill();
		mDialog.show();
	}
}