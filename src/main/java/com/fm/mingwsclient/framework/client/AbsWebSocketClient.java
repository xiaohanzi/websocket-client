package com.fm.mingwsclient.framework.client;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbsWebSocketClient extends WebSocketClient implements Serializable{

	private static final long serialVersionUID = 4603214247444894685L;
	
	protected boolean autoOpen;
	protected int type;
	
	public AbsWebSocketClient(String serverUri) throws URISyntaxException {
        super(new URI(serverUri));
    }
    
    public AbsWebSocketClient(String serverUri,Map headers) throws URISyntaxException {
        super(new URI(serverUri),headers);
    }

    @Override
	public void onOpen(ServerHandshake arg0) {
    	log.info("------ webSocket onOpen ------");
    	connectOnOpen(arg0);
    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
    	log.warn("------ webSocket onClose ------");
    	connectOnClose(arg0,arg1,arg2);
    	if(!this.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
    		if (this.autoOpen) {
    			AbsWebSocketClient client = WebsocketConnectHelper.getInstance().connect(this.type, getHeaderMap());
    			connectOnClose(arg0,arg1,arg2);
    		}
    	}else {
    		connectOnClose(arg0,arg1,arg2);
    	}
    }

    @Override
    public void onError(Exception arg0) {
    	log.error("------ webSocket onError ------");
    	connectOnError(arg0);
    }

    @Override
    public void onMessage(String arg0) {
    	if (arg0.contains(WebsocketConnectHelper.HEARBEAT_CONTENT)) {
    		//如果是心跳的内容，忽略不处理
    	}else {
    		//log.info("-------- recieved websocketserver msg: " + arg0 + "--------");
    		connectOnMessage(arg0);
    		
    		if(!this.autoOpen) {
        		this.close();
        	}
    	}
    }
    
    public void sendMessage(String msg) {
    	if(!this.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
    		if (this.autoOpen) {
    			AbsWebSocketClient client = WebsocketConnectHelper.getInstance().connect(this.type, getHeaderMap());
    			client.send(msg);
    		}
    	}else {
    		super.send(msg);
    	}
    }
    
    
    public boolean isAutoOpen() {
		return autoOpen;
	}

	public void setAutoOpen(boolean autoOpen) {
		this.autoOpen = autoOpen;
	}
	

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
    
    //设置心跳时长
    protected abstract int getHeartBeatMillionSeconds();
    protected abstract void connectOnOpen(ServerHandshake arg0);
    protected abstract void connectOnClose(int arg0, String arg1, boolean arg2);
    protected abstract void connectOnError(Exception arg0);
    protected abstract void connectOnMessage(String arg0);
    protected abstract Map getHeaderMap();
    
}
