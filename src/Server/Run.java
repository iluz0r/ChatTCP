package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Run {

	public static void main(String[] args) throws IOException {
		int port = 1330;
		HashSet<LoginUsers> listLoginUsers = new HashSet<LoginUsers>(0);
		ServerSocket serverSocket = new ServerSocket(port);
		
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();

		for (Thread thread: threadSet) {
		 System.out.println(thread.getId()+ " "+ thread.getState());
		 
		while(true) {
			System.out.println("Waiting connection....");
			// Una chiamata bloccante che aspetta fin quando non viene richiesta una connessione
			Socket socket = serverSocket.accept();
			System.out.println("Accepted connection from " + socket.getRemoteSocketAddress().toString());
			Thread t = new Thread(new Server(socket, listLoginUsers));
			t.start();	
			 
			}
		}
	}

}