package com.fm.mingwsclient.bussiness.atmuai;


import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.java_websocket.handshake.ServerHandshake;

import com.fm.mingwsclient.framework.client.AbsWebSocketClient;
import com.fm.mingwsclient.framework.util.Base64;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractAtmuaiWsClient extends AbsWebSocketClient{
	
	private static final long serialVersionUID = 8124570503439105524L;
	
	public AbstractAtmuaiWsClient(String serverUri) throws URISyntaxException {
		super(serverUri);
	}

	public AbstractAtmuaiWsClient(String serverUri,Map headers) throws URISyntaxException {
		super(serverUri,headers);
	}

	@Override
	protected int getHeartBeatMillionSeconds() {
		return 1000;
	}

	@Override
	protected void connectOnOpen(ServerHandshake arg0) {
		// TODO Auto-generated method stub
		//log.info(">>>>connectOnOpen");
	}

	@Override
	protected void connectOnClose(int arg0, String arg1, boolean arg2) {
		// TODO Auto-generated method stub
		//log.info(">>>>connectOnClose");
	}

	@Override
	protected void connectOnError(Exception arg0) {
		// TODO Auto-generated method stub
		//log.info(">>>>connectOnError");
	}

	@Override
	protected Map getHeaderMap() {
		String marketId = "93999758-ad48-4567-aa06-179f6e87ad0f";
    	String secretId="AKIDGnX2KNm9Lce3sbsu5afXM8krJMblE46EJZ2q";
    	String secretKey = "H6QT3klEN3kgg2r2EXo2286aF2HgIJ1vgrXvJEKa";
		
    	Calendar cd = Calendar.getInstance();  
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);  
        sdf.setTimeZone(TimeZone.getTimeZone("GMT")); 
        String timeStr = sdf.format(cd.getTime());  
        String sig="";
		try {
			sig = sign(secretKey,marketId,timeStr);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        String authen = "hmac id=\""+secretId+"\", algorithm=\"hmac-sha1\", headers=\"date source\", signature=\""+sig+"\"";
        log.info("authen --->" + authen);
        
        Map<String,String> headers=new HashMap<String, String>();
        headers.put("Source",marketId);
        headers.put("Date",timeStr);
        headers.put("Authorization",authen);
		return headers;
	}
	
	private static final String CONTENT_CHARSET = "UTF-8";
    private static final String HMAC_ALGORITHM = "HmacSHA1"; 
    public static String sign(String secret,String marketId, String timeStr) 
    		throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException 
    {
        //get signStr
        String signStr = "date: "+timeStr+"\n"+"source: "+marketId;
        //get sig
        String sig = null;
        Mac mac1 = Mac.getInstance(HMAC_ALGORITHM);
        byte[] hash;
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(CONTENT_CHARSET), mac1.getAlgorithm());
        mac1.init(secretKey);
        hash = mac1.doFinal(signStr.getBytes(CONTENT_CHARSET));
        sig = new String(Base64.encode(hash));
        return sig;
    }
}
