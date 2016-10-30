package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

public class UserConnection {

	private String username;
	private Socket socket;
	private BufferedReader br;
	private PrintWriter pw;

	public UserConnection(Socket socket) throws UnsupportedEncodingException, IOException {
		this.socket = socket;
		br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
		pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Socket getSocket() {
		return socket;
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
