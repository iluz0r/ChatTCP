package Server;

import java.net.Socket;

public class LoginUsers {
	public String user;
	public Socket client;

	public LoginUsers(String user, Socket client) {
		this.user = user;
		this.client = client;
	}

	public Socket getClient() {
		return client;
	}

	public void setClient(Socket client) {
		this.client = client;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}
