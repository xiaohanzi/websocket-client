package com.fm.mingwsclient.framework.exception;

public class WebsocketClientException extends RuntimeException{

	private static final long serialVersionUID = 4729488170955671060L;
	
	public WebsocketClientException(String msg) {
		super(msg);
	}
	
	public WebsocketClientException(String msg,Exception e) {
		super(msg,e);
	}

}
