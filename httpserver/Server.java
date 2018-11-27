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
			//指定一个目录
			String root = "D:";
			ServerSocket server = new ServerSocket(8080);
			int count = 0 ;
			while(true){
				Socket socket = server.accept();
				ServerThread thread = new ServerThread(socket,root);
				count++;
				System.out.println("线程："+count);
				thread.start();
			}	
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("连接失败");
		}
	}

}
