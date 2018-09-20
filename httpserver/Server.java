package httpserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server {

	public static void main(String[] args) {
		try {
			ServerSocket server = new ServerSocket(8080);
			while(true){
				Socket socket = server.accept();
				System.out.println("建立连接");
				ServerThread thread = new ServerThread(socket);
				thread.start();
			}	
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("连接失败");
		}
	}

}
