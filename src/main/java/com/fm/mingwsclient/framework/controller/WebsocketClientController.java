package com.fm.mingwsclient.framework.controller;

import java.net.URISyntaxException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fm.mingwsclient.framework.client.AbsWebSocketClient;
import com.fm.mingwsclient.framework.client.WebsocketConnectHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/wsclient")
public class WebsocketClientController {

    @GetMapping("/connect")
    public String connect(int type) {
    		//根据type实例化AbsWebSocketClient
    		try {
    			AbsWebSocketClient client = WebsocketConnectHelper.getInstance().connect(type);
    			log.info(client.toString());
    			return "success";
			} catch (URISyntaxException e) {
				e.printStackTrace();
				return "error";
			}
    		
    }
    
}
