package com.yunos.tv.yingshi.exception;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.aliyun.base.exception.BaseException;
import com.aliyun.base.net.NetworkManager;
import com.yunos.tv.yingshi.YingshiApplication;

public class SourceException extends BaseException {
	private static final long serialVersionUID = 1L;
	private ErrorCodes errorCode;
	
	public ErrorCodes getErrorCode() {
		return errorCode;
	}
 
	public SourceException(ErrorCodes error, Throwable throwable) {
		super(error.getCode(), "", throwable);
		this.errorCode = error;
	}

	public SourceException(ErrorCodes error) {
		super(error.getCode(), "");
		this.errorCode = error;
	}
	
	public SourceException() {
		super();
	}
	
	@Override
	public boolean handle(Context context) {
		switch (errorCode) {
		case NETWORK_UnknownHostException:
		case NETWORK_InterruptedIOException:
		case NETWORK_HttpHostConnectException:
		case NETWORK_SocketException:
		case NETWORK_Other:
			if (NetworkManager.isNetworkAvailable(context)) {
				Toast.makeText(context, errorCode.getMessage(), Toast.LENGTH_SHORT).show();
			} else {
				YingshiApplication app = (YingshiApplication) YingshiApplication.getApplication();
				app.showNetDialog(context);
			}
			return true;
		case RES_NOT_EXIST: //资源不存在，交给WorkAsyncTask的onError函数处理。
			Log.d("http", "RES_NOT_EXIST unhandle!");
			return false;
		default:
			if (!TextUtils.isEmpty(getErrorMessage())) {
				Toast.makeText(context, getErrorMessage(), Toast.LENGTH_SHORT).show();
			}
			return true;
		}
	}

}
