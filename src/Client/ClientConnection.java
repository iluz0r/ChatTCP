package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientConnection {

	private int port;
	private Socket socket;
	private BufferedReader br;
	private PrintWriter pw;

	public ClientConnection() throws UnknownHostException, IOException {
		port = 1330;
		socket = new Socket("localhost", port);
		pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
		br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
	}

	public BufferedReader getBufferedReader() {
		return br;
	}

	public PrintWriter getPrintWriter() {
		return pw;
	}

	public boolean isClosed() {
		return socket.isClosed();
	}

	public void closeSocket() throws IOException {
		socket.close();
	}

}
