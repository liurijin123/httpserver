package httpserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Date;

public class Response {

	private Socket socket;
	BufferedOutputStream bos;
	
	StringBuilder headInfo;
	StringBuilder context;
	
	int contextlen;
	String contentType;
	public Response(){
		headInfo = new StringBuilder();
		context = new StringBuilder();
	}
	public Response(Socket socket){
		this();
		this.socket = socket;
		try {
			bos = new BufferedOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//构造头信息
	private void creatHeadInfo(int code){
		headInfo.append("HTTP/1.1 ").append(code).append(" ");
		switch (code) {
		case 200:
			headInfo.append("OK");
			break;
		case 404:
			headInfo.append("NOT FOUND");
			break;
		case 500:
			headInfo.append("SERVER ERROR");
			break;
		}
		headInfo.append("\r\n");
		headInfo.append("Server:Server\r\n");
		headInfo.append("Data:").append(new Date()).append("\r\n");
		headInfo.append("Content-type:").append(contentType).append(";charset=UTF-8").append("\r\n");
		headInfo.append("Content-Length:").append(contextlen).append("\r\n");
		headInfo.append("\r\n");
	}
	public void fun(String root, String url) {
		if(url.endsWith(".txt/")){
			contentType = "text/paint";
			viewText(root, url);
		}else if(url.endsWith(".jpg/")){
			contentType = "image/jpeg";
			viewPicture(root, url);
		}else {
			contentType = "text/html";
			dirFile(root, url);
		}
		
	}
	//显示图片
	private void viewPicture(String root, String url) {
		
//		context.append("<HTML>");
//		context.append("<head>");
//		context.append("<meta charset=\"utf-8\"/>");
//		context.append("</head>");
//		context.append("<img src=\"").append(root+url.substring(0, url.length()-1)).append("\" width=\"500\" height=\"500\">");
//		context.append("</HTML>");
//		contextlen = context.toString().getBytes().length;
			
	}
	//显示文字
	private void viewText(String root, String url) {
		
		File file = new File(root+url.substring(0, url.length()-1));
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			byte[] bytes = new byte[1024];
			int len = -1;
			while((len = bis.read(bytes))!=-1){
				context.append(new String(bytes,0,len,"gb2312"));
			}
			contextlen = context.toString().getBytes().length;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//构造正文_文件目录
	public void dirFile(String root,String url){
		File file = new File(root+url);
		String[] dirfilenames = file.list();
		context.append("<HTML>");
		context.append("<head>");
		context.append("<meta charset=\"utf-8\"/>");
		context.append("</head>");
		for(String str : dirfilenames){
			context.append("<li><a href=\" ..").append(url).append(str).append("/\"").append(">").append(str).append("</a></li>");
		}
		context.append("</HTML>");
		contextlen = context.toString().getBytes().length;
		send(200);
	}
	//发送到浏览器
	public void send(int code){
		try {
			if(headInfo==null){
				code = 500;
			}
			creatHeadInfo(code);
			bos.write(headInfo.toString().getBytes());
			
			bos.write(context.toString().getBytes());
			bos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
