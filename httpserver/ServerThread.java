package httpserver;

import java.io.IOException;
import java.net.Socket;

public class ServerThread extends Thread {

	private Socket socket;
	private Request request;
	private Response response;
	private int code = 200;
	
	public ServerThread(Socket socket) {
		this.socket = socket;
		request = new Request(socket);
		response = new Response(socket);
		if(response == null){
			code = 404;
		}
	}
	
	public void run(){
		System.out.println(request.getUrl()); 
		if(request.getUrl().equals("/favicon.ico")){
			return;
		}
		response.dirfile("D:", request.getUrl());
		response.send(code);
	}
}
