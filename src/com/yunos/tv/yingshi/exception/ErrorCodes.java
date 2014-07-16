package com.yunos.tv.yingshi.exception;

import com.yunos.tv.yingshi.R;


public enum ErrorCodes {
	NULL(-1, -9999),
	UNKNOWNERROR(0, R.string.error_unknow),
	
	URLILLEGAL(1, R.string.error_urliliegal),
	UNSUPPORTENCODING(2, R.string.error_unsupportencoding),
	IOEXCEPTION(3, R.string.error_ioexcption),
	
	NETWORK_UnknownHostException(4, R.string.error_network_UnknownHostException),
	NETWORK_InterruptedIOException(5, R.string.error_network_InterruptedIOException),
	NETWORK_HttpHostConnectException(6, R.string.error_network_HttpHostConnectException),
	NETWORK_SocketException(7, R.string.error_network_SocketException),
	NETWORK_Other(8, R.string.error_network_other),
	
	SERVERERROR_EERROR(10, R.string.error_server_error),
	SERVERERROR_FAIL(11, R.string.error_server_fail),
	SERVERERROR_SYS(12, R.string.error_server_sys),
	SERVERERROR_INVALID_PARAMS(13, R.string.error_server_invalid_params),
	SERVERERROR_NO_API(14, R.string.error_server_no_api),
	SERVERERROR_UNSUPPORTREQUESTMETHOD(15, R.string.error_server_unsupportrequestmethod),
	
	RES_NOT_EXIST(16, R.string.error_res_not_exist);
	
	private int code;
	private int message;
	
	private ErrorCodes(int code, int message){
		this.code = code;
		this.message = message;
	}
	
	public int getCode(){
		return code;
	}
	
	public int getMessage(){
		return message;
	}
}
