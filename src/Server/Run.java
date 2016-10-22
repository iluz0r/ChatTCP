package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Run {

	public static void main(String[] args) throws IOException {
		int port = 1330;
		ServerSocket serverSocket = new ServerSocket(port);
		
		while(true) {
			System.out.println("Waiting connection...");
			// Una chiamata bloccante che aspetta fin quando non viene richiesta una connessione
			Socket socket = serverSocket.accept();
			System.out.println("Accepted connection from " + socket.getRemoteSocketAddress().toString());
			Thread t = new Thread(new Server(socket));
			t.start();		
		}
	}

}
