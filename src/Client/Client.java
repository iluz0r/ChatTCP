package Client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	
	private int port;
	private Socket socket;
	
	public Client() throws UnknownHostException, IOException {
		port = 1330;
<<<<<<< HEAD
		socket = new Socket("192.168.0.110", port);
=======
		socket = new Socket("192.168.0.143", port);
>>>>>>> branch 'master' of https://github.com/iluz0r/ChatTCP.git
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
}
