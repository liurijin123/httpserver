package httpserver;

import java.io.IOException;
import java.net.Socket;

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
		System.out.println(request.getUrl()); 
		String url = request.getUrl();
		if(url.equals("/favicon.ico")){
			return;
		}
		response.fun(root, url);
		response.send(code);
	}
}
