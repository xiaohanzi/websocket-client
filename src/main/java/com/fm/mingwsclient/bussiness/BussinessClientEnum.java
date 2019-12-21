package com.fm.mingwsclient.bussiness;

import com.fm.mingwsclient.bussiness.atmuai.impl.AtmuaiCameraWsClient;
import com.fm.mingwsclient.framework.client.AbsWebSocketClient;

public enum BussinessClientEnum {
	
	
	ATMUAI(1,"格林深瞳-摄像头捕捉人脸-websocket客户端","ws://localhost:8102/socket/lili",true,true,AtmuaiCameraWsClient.class);
	
	
	BussinessClientEnum(int id,String name,String url,boolean singleTon,boolean alwaysOnline,Class clientClazz) {
		this.id = id;
		this.name = name;
		this.url = url;
		this.singleTon = singleTon;
		this.alwaysOnline = alwaysOnline;
		this.clientClazz = clientClazz;
	}
	
	private int id;
	private String name;
	//访问的URL地址
	private String url;
	//是否始终只启动一个
	private boolean singleTon;
	//是否永久连接
	private boolean alwaysOnline;
	private Class<AbsWebSocketClient> clientClazz;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Class<AbsWebSocketClient> getClientClazz() {
		return clientClazz;
	}
	public void setClientClazz(Class<AbsWebSocketClient> clientClazz) {
		this.clientClazz = clientClazz;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isSingleTon() {
		return singleTon;
	}
	public void setSingleTon(boolean singleTon) {
		this.singleTon = singleTon;
	}
	public boolean isAlwaysOnline() {
		return alwaysOnline;
	}
	public void setAlwaysOnline(boolean alwaysOnline) {
		this.alwaysOnline = alwaysOnline;
	}
	public static BussinessClientEnum  getClientByType(int type) {
		for (BussinessClientEnum e: BussinessClientEnum.values()) {
			if (e.getId() == type) {
				return e;
			}
		}
		return null;
	}
}
