package cn.o.app.demo.net;

import android.content.Context;
import cn.o.app.net.NetTask;

public class GetTNTask extends NetTask<String, String> {

	@Override
	public void setContext(Context context) {
		super.setContext(context);

		setUrl("http://202.101.25.178:8080/sim/gettn");
	}

}
