package httpserver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;

public class Response {

	private Socket socket;
	BufferedWriter bw;
	
	StringBuilder headInfo;
	StringBuilder context;
	int contextlen;
	public Response(){
		headInfo = new StringBuilder();
		context = new StringBuilder();
	}
	public Response(Socket socket){
		this();
		this.socket = socket;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
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
		headInfo.append("Content-type:text/html;charset=UTF-8").append("\r\n");
		headInfo.append("Content-Length:").append(contextlen).append("\r\n");
		headInfo.append("\r\n");
	}
	//构造正文_文件目录
	public void dirfile(String root,String url){
		File file = new File(root+url);
		String[] dirfilenames = file.list();
		context.append("<HTML>");
		context.append("<head>");
		context.append("<meta charset=\"utf-8\"/>");
		context.append("</head>");
		for(String str : dirfilenames){
			context.append("<li><a href=\"").append(url).append(str).append("/\"").append(">").append(str).append("</a></li>");
		}
		context.append("</HTML>");
		contextlen = context.length();
	}
	//发送到浏览器
	public void send(int code){
		try {
			if(headInfo==null){
				code = 500;
			}
			creatHeadInfo(code);
			bw.append(headInfo.toString());
			bw.append(context.toString());
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
