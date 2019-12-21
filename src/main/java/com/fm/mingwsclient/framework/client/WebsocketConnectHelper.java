package com.fm.mingwsclient.framework.client;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.java_websocket.WebSocket;
import org.springframework.util.ReflectionUtils;

import com.fm.mingwsclient.bussiness.BussinessClientEnum;
import com.fm.mingwsclient.framework.exception.WebsocketClientException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebsocketConnectHelper {
	
	public static final String HEARBEAT_CONTENT = "send heartbeat to websocket server.";
	
	private WebsocketConnectHelper() {
		
	}
	
	private static final WebsocketConnectHelper INSTANCE = new WebsocketConnectHelper();
	
	
	public static WebsocketConnectHelper getInstance() {
		return INSTANCE;
	}
	
	private Map<String,AbsWebSocketClient> clientMap = new HashMap<String,AbsWebSocketClient>();
	
	public  AbsWebSocketClient connect(int type) throws URISyntaxException {
		BussinessClientEnum clientEnum = BussinessClientEnum.getClientByType(type);
		AbsWebSocketClient simpleclient = generateSimpleClient(clientEnum, null);
		Map headers = simpleclient.getHeaderMap();
		AbsWebSocketClient real = getConnectClient(clientEnum,headers,simpleclient);
		return clientConnect(real,clientEnum,headers,false);
	}
	
	public  AbsWebSocketClient asynConnect(int type) throws URISyntaxException {
		BussinessClientEnum clientEnum = BussinessClientEnum.getClientByType(type);
		AbsWebSocketClient simpleclient = generateSimpleClient(clientEnum, null);
		Map headers = simpleclient.getHeaderMap();
		AbsWebSocketClient real = getConnectClient(clientEnum,headers,simpleclient);
		return clientConnect(real,clientEnum,headers,true);
	}
	
	public AbsWebSocketClient connect(int type, Map headers) {
		BussinessClientEnum clientEnum = BussinessClientEnum.getClientByType(type);
		AbsWebSocketClient real = getConnectClient(clientEnum,headers,null);
		return clientConnect(real,clientEnum,headers,false);
	}
	
	public AbsWebSocketClient asynConnect(int type, Map headers) {
		BussinessClientEnum clientEnum = BussinessClientEnum.getClientByType(type);
		AbsWebSocketClient real = getConnectClient(clientEnum,headers,null);
		return clientConnect(real,clientEnum,headers,true);
	}
	
	private AbsWebSocketClient clientConnect(AbsWebSocketClient client,BussinessClientEnum clientEnum,Map headers,boolean asyn) {
		if (null == client) {
			log.error("websocket client is null.");
			throw new WebsocketClientException("websocket client is null.");
		}
		log.info("connecting...");
		if (!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
			if (!asyn) {
				client.connect();
				while (!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
					//log.warn("waiting for websocket server connect...");
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				log.warn("websocket server connect success !");
				
				client.sendMessage(WebsocketConnectHelper.HEARBEAT_CONTENT);
			    //单开线程去维持心跳
				new HeartBeatClientThread(client).start();
			}else {
				new Thread() {

					@Override
					public void run() {
						client.connect();
						while (!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
							//log.warn("waiting for websocket server connect...");
							try {
								Thread.sleep(200);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						log.warn("websocket server connect success !");
						
						client.sendMessage(WebsocketConnectHelper.HEARBEAT_CONTENT);
					    //单开线程去维持心跳
						new HeartBeatClientThread(client).start();
					}
					
				}.start();
			}
			clientMap.put(String.valueOf(clientEnum.getId()), client);
		}
		return client;
	}
	
	/**
	 * 获取客户端
	 * @param clientEnum
	 * @param headers
	 * @param simpleclient
	 * @return
	 */
	private  AbsWebSocketClient getConnectClient(BussinessClientEnum clientEnum,Map headers,AbsWebSocketClient simpleclient) {
		if (null == clientEnum) {
			log.error("websocket client type "+ clientEnum.getId()+" not defined. go BussinessClientEnum and do config.");
			throw new WebsocketClientException("websocket client type "+ clientEnum.getId()+" not defined. go BussinessClientEnum and do config.");
		}
		//如果该链接是单例的，则判断是否有链接，如果链接存在并且是链接着的，则不产生新的连接
		boolean singleTon = clientEnum.isSingleTon();
		String type = String.valueOf(clientEnum.getId());
		if (singleTon) {
			AbsWebSocketClient oldclient = clientMap.get(type);
			if (null != oldclient && oldclient.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
				return oldclient;
			}
		}
		
		//simpleclient是用url反射出来的，如果client就不用设置header,则使用这个实例，不用再用反射生成
		if (null == simpleclient) {
			AbsWebSocketClient client = generateSimpleClient(clientEnum,headers);
			clientMap.put(type, client);
			return client;
		}else {
			if (null == headers) {
				clientMap.put(type, simpleclient);
				return simpleclient;
			}else {
				AbsWebSocketClient client = generateSimpleClient(clientEnum,headers);
				clientMap.put(type, client);
				return client;
			}
		}
	}
	
	/**
	 * 反射实例化client
	 * @param clientEnum
	 * @param headers
	 * @return
	 */
	private AbsWebSocketClient generateSimpleClient(BussinessClientEnum clientEnum,Map headers) {
		if (null == headers) {
			Constructor simple;
			try {
				simple = ReflectionUtils.accessibleConstructor(clientEnum.getClientClazz(), String.class);
				Method setTypeMethod = ReflectionUtils.findMethod(clientEnum.getClass(), "setType");
				AbsWebSocketClient client = (AbsWebSocketClient) simple.newInstance(clientEnum.getUrl());
				client.setType(clientEnum.getId());
				client.setAutoOpen(clientEnum.isAlwaysOnline());
				return client;
			} catch (NoSuchMethodException e1) {
				e1.printStackTrace();
				log.error("websocket client constructor(String) error.",e1);
				throw new WebsocketClientException("websocket client constructor(String) error.");
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			
		}else {
			Constructor c = null;
			try {
				c = ReflectionUtils.accessibleConstructor(clientEnum.getClientClazz(), String.class,Map.class);
				AbsWebSocketClient client = (AbsWebSocketClient) c.newInstance(clientEnum.getUrl(),headers);
				client.setType(clientEnum.getId());
				client.setAutoOpen(clientEnum.isAlwaysOnline());
				return client;
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				log.error("websocket client  "+ clientEnum.getClientClazz()+" must has constructor(string,map).");
				throw new WebsocketClientException("websocket client  "+ clientEnum.getClientClazz()+" must has constructor(string,map).");
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
