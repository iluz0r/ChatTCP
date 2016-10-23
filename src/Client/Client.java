package Client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	private int port;
	private Socket socket;

	public Client() throws UnknownHostException, IOException {
		port = 1330;
		socket = new Socket("localhost", port);
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

}
