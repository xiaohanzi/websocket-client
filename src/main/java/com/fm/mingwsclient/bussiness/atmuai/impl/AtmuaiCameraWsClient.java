package com.fm.mingwsclient.bussiness.atmuai.impl;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.Map;

import com.fm.mingwsclient.bussiness.atmuai.AbstractAtmuaiWsClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AtmuaiCameraWsClient extends AbstractAtmuaiWsClient{

	private static final long serialVersionUID = -8445043886787619932L;
	
	public AtmuaiCameraWsClient(String serverUri) throws URISyntaxException {
		super(serverUri);
	}
	
	public AtmuaiCameraWsClient(String serverUri,Map headers) throws URISyntaxException {
		super(serverUri,headers);
	}

	

	@Override
	protected void connectOnMessage(String arg0) {
		// 从摄像头拿到数据之后的处理,要注意集群的时候，服务端可能会推送多条记录。
		//log.info("on message >>"+arg0);
	}
	
	

}
