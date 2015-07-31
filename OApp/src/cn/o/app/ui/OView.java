package cn.o.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import cn.o.app.ui.core.IContentViewOwner;
import cn.o.app.ui.core.IView;
import cn.o.app.ui.core.IViewFinder;
import cn.o.app.ui.core.UICore;

/**
 * View of framework
 */
public class OView extends RelativeLayout implements IView, IViewFinder, IContentViewOwner {

	public OView(Context context) {
		super(context);
		init();
	}

	public OView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public OView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	protected void init() {
		UICore.injectContentView(this);
	}

	public void setContentView(View view) {
		this.removeAllViews();
		this.addView(view);
		UICore.injectResources(this);
		UICore.injectEvents(this);
	}

	public void setContentView(int layoutResID) {
		this.removeAllViews();
		LayoutInflater.from(getContext()).inflate(layoutResID, this);
		UICore.injectResources(this);
		UICore.injectEvents(this);
	}

	@Override
	public <T extends View> T findViewById(int id, Class<T> viewClass) {
		return UICore.findViewById(this, id, viewClass);
	}

	@Override
	public View toView() {
		return this;
	}

}
