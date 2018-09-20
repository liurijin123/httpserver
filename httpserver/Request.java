package httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class Request {

	private Socket socket;
	private String requestInfo;
	
	private String method ;
	private String url ;
	private String parameter;
	public String getMethod() {
		return method;
	}
	public String getUrl() {
		return url;
	}
	public String getParameter() {
		return parameter;
		
	}
	public Request(){
		method = "";
		url = "";
		parameter = "";
	}
	//构造方法
	public Request(Socket socket) {
		this();
		this.socket = socket;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			requestInfo = br.readLine();	
		} catch (IOException e) {
			e.printStackTrace();
		}
		parseRequestInfo();
	}
	//解析头信息
	private void parseRequestInfo() {
		String[] strs = requestInfo.split(" ");
		method = strs[0];
		url = decode(strs[1],"utf-8");
	}
	//解码解决中文乱码问题
	private String decode(String value,String code){
		try {
			return java.net.URLDecoder.decode(value, code);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
