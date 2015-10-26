package cn.o.app.demo.ui;

import android.content.Context;
import android.util.AttributeSet;
import cn.o.app.core.annotation.res.SetContentView;
import cn.o.app.demo.R;
import cn.o.app.ui.StateView;

@SetContentView(R.layout.view_surface_viewer)
public class SurfaceViewerDemoView extends StateView {

	public SurfaceViewerDemoView(Context context) {
		super(context);
	}

	public SurfaceViewerDemoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SurfaceViewerDemoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

}
