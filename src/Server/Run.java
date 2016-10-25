package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Run {

	public static void main(String[] args) throws IOException {
		int port = 1330;
		ServerSocket serverSocket = new ServerSocket(port);
		ArrayList<User> onlineUsersList = new ArrayList<>();

		while (true) {
			System.out.println("Waiting connection....");
			Socket socket = serverSocket.accept();
			System.out.println("Accepted connection from " + socket.getRemoteSocketAddress().toString());
			Thread t = new Thread(new ClientHandler(socket, onlineUsersList));
			t.start();
		}
	}
}
