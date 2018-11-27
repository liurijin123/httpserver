package httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Request {

	private Socket socket;
	private BufferedReader br;
	private String method ;
	private String url ;
	private String parameter;
	private StringBuilder fieldMapStr;
	private Map<String, String> fieldMap;
	public String getMethod() {
		return method;
	}
	public String getUrl() {
		return url;
	}
	public String getParameter() {
		return parameter;
		
	}
	public Map<String, String> getfieldMap() {
		return fieldMap;	
	}
	public Request(){
		method = "";
		url = "";
		parameter = "";
		fieldMapStr = new StringBuilder();
	}
	//构造方法
	public Request(Socket socket) {
		this();
		this.socket = socket;
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//解析头信息
			parseRequestInfo();
			//解析字段
			parseRequestField();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//解析字段
	private void parseRequestField() throws IOException { 
		fieldMap = new HashMap<String, String>();
		String requestField = br.readLine();
		while(requestField != null && !requestField.equals("")){
			fieldMapStr.append(requestField).append("\r\n");
			String[] strs = requestField.split(": ");
			fieldMap.put(strs[0],strs[1]);
			requestField = br.readLine();
		}	
	}
	//解析头信息
	private void parseRequestInfo() throws IOException {
		String requestInfo = br.readLine();
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
	@Override
	public String toString() {
		return fieldMapStr.toString();
	}
	
}
