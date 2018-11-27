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
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

public class Response {

	private Socket socket;
	BufferedOutputStream bos;
	
	StringBuilder headInfo;
	StringBuilder context;
	byte[] contextByte;
	byte[] headInfoByte;
	
	int contextLen;
	String contentType;
	String contentRange;
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
		case 206:
			headInfo.append("Partial Content");
			break;
		case 404:
			headInfo.append("NOT FOUND");
			break;
		case 500:
			headInfo.append("SERVER ERROR");
			break;
		}
		headInfo.append("\r\n");
		headInfo.append("Server: Server\r\n");
		headInfo.append("Data: ").append(new Date()).append("\r\n");
		
		if(contentRange != null){	
			headInfo.append("Accept-Ranges: bytes\r\n");
			headInfo.append("Content-Range: ").append(contentRange).append("\r\n");
		}
		headInfo.append("Content-Type: ").append(contentType).append(";charset=UTF-8").append("\r\n");
		headInfo.append("Content-Length: ").append(contextLen).append("\r\n");
//		headInfo.append("Connection: close").append("\r\n");
		headInfo.append("\r\n");
		headInfoByte = headInfo.toString().getBytes();
	}
	//初始化
	public void init(String root, String url, Map<String, String> fieldMap) {

		//判断文件类型
		File file = new File(root+url);
		if(file.isFile()){
			if(url.endsWith(".txt/")){
				contentType = "text/paint";
				viewText(file);
			}else if(url.endsWith(".jpg/")){
				contentType = "image/jpeg";
				responseMedia(file);
			}else if(url.endsWith(".PNG/")){
				contentType = "image/png";
				responseMedia(file);
			}else if(url.endsWith(".mp3/")){
				contentType = "audio/mp3";
				if(fieldMap.get("Range") != null){
					int start;
					int end;
					String str = fieldMap.get("Range");
					String[] range = str.split("=")[1].split("-");
					if(range.length == 2){
						start = Integer.parseInt(range[0]);
						end = Integer.parseInt(range[1]);
					}else{
						start = Integer.parseInt(range[0]);
						end = (int) file.length() - 1;
					}
					contentRange = "bytes " + start + "-" + end + "/" + (int) file.length() ;
					responseMedia(file, start, end);
				}else{
					responseMedia(file);
				}		
			}else if(url.endsWith(".avi/")){
				contentType = "video/avi";
				responseMedia(file);
			}else{
				contentType = "text/paint";
				responseError();
			}
		}else{
			dirFile(file,url);
		}
		contextLen = contextByte.length;
	}
	//响应未知文件
	private void responseError() {
		context.append("无法响应此文件类型(目前支持mp3，jpg，PNG，txt，avi)");
		contextByte = context.toString().getBytes();
	}
	//响应图片音乐视频
	private void responseMedia(File file) {
		
		byte[] bytes = new byte[(int) file.length()];
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			bis.read(bytes);
			contextByte = bytes;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void responseMedia(File file, int start, int end) {

		System.out.println("开始位置" + start);
		System.out.println("结束位置" + end);
		byte[] bytes = new byte[(int) file.length()];
		try {
			RandomAccessFile re = new RandomAccessFile(file,"r");
			re.seek(start);
			contextByte = new byte[end - start + 1];
			re.read(contextByte);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	//显示文字
	private void viewText(File file) {
		
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			byte[] bytes = new byte[1024];
			int len = -1;
			while((len = bis.read(bytes))!=-1){
				context.append(new String(bytes,0,len,"gb2312"));
			}
			contextByte = context.toString().getBytes();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//构造文件目录
	public void dirFile(File file, String url){
		String[] dirfilenames = file.list();
		context.append("<HTML>");
		context.append("<head>");
		context.append("<meta charset=\"utf-8\"/>");
		context.append("</head>");
		if(dirfilenames.length != 0){
			for(String str : dirfilenames){
				context.append("<li><a href=\"").append(url).append(str).append("/\"").append(">").append(str).append("</a></li>");
			}			
		}else{
			context.append("<h1>文件夹为空，请返回上层<br></h1>");
		}
		context.append("</HTML>");
		contextByte = context.toString().getBytes();
	}
	//发送到浏览器
	public void send(int code){
		try {
			if(contentRange != null){
				code = 206;
			}
			creatHeadInfo(code);
			if(headInfoByte!=null && contextByte != null){
				bos.write(headInfoByte);
				bos.write(contextByte);
				bos.flush();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public String toString() {
		return "返回实体字节数" + contextByte.length;
	}
	
}
