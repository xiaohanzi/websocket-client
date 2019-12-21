package com.fm.mingwsclient.framework.client;

import org.java_websocket.WebSocket;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartBeatClientThread extends Thread{

	private AbsWebSocketClient client ;
	
	public HeartBeatClientThread(AbsWebSocketClient client) {
		this.client = client;
	}
	
	@Override
	public void run() {
		while (true) {
       		try {
				Thread.sleep(client.getHeartBeatMillionSeconds());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
       		client.sendMessage(WebsocketConnectHelper.HEARBEAT_CONTENT);
       		if (!client.getReadyState().equals(WebSocket.READYSTATE.OPEN) && (!client.autoOpen)) {
       			break;
       		}
		}
	}
	
}
