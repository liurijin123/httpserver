package httpserver;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Logger;

public class ServerThread extends Thread {

	private Socket socket;
	private Request request;
	private Response response;
	private int code = 200;
	private String root ;
	
	public ServerThread(Socket socket,String root) {
		this.socket = socket;
		request = new Request(socket);
		response = new Response(socket);
		this.root = root;
		if(response == null){
			code = 404;
		}
	}
	
	public void run(){
		while(true){
			Logger log = Logger.getLogger("logServerThread"); 
			request.init();
			String url = request.getUrl();
			if(url.equals("/favicon.ico")){	
				log.info("/favicon.ico");		
				continue; 
			}
			log.info("请求地址：" + url + "\r\n" +
					"请求字段" + "\r\n" + request);
			Map<String, String> fieldMap = request.getfieldMap();
			response.init(root, url, fieldMap);
			log.info(response.toString());
			response.send(code);
		}
//		Logger log = Logger.getLogger("logServerThread"); 
//		request.init();
//		String url = request.getUrl();
//		if(url.equals("/favicon.ico")){	
//			log.info("/favicon.ico");
//			try {
//				socket.close();		
//				return;
//			} catch (IOException e) {
//				e.printStackTrace();
//			}	
//		}
//		log.info("请求地址：" + url + "\r\n" +
//				"请求字段" + "\r\n" + request);
//		Map<String, String> fieldMap = request.getfieldMap();
//		response.init(root, url, fieldMap);
//		log.info(response.toString());
//		response.send(code);
//		try {
//			socket.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}
