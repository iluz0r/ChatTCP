package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

	private User user;
	private BufferedReader br;
	private PrintWriter pw;
	private ArrayList<User> onlineUsersList;

	public ClientHandler(Socket socket, ArrayList<User> onlineUsersList)
			throws UnsupportedEncodingException, IOException {
		user = new User(socket);
		br = user.getBufferedReader();
		pw = user.getPrintWriter();
		this.onlineUsersList = onlineUsersList;
	}

	@Override
	public void run() {
		try {
			String req;
			Socket socket = user.getSocket();

			while (!socket.isClosed() && (req = br.readLine()) != null) {
				String pathUsers = System.getProperty("user.dir") + "/src/Server/users.txt";

				File f = new File(pathUsers);
				f.createNewFile();
				BufferedReader usersReader = new BufferedReader(new FileReader(pathUsers));
				BufferedWriter usersWriter = new BufferedWriter(new FileWriter(pathUsers, true));

				if (req.contains("LOGIN:"))
					processLoginReq(req, usersReader);
				else if (req.contains("REGISTER:"))
					processRegisterReq(req, usersReader, usersWriter);
				else if (req.contains("LOGOUT:"))
					processLogoutReq(req);
				else {
					processMessage(req);
				}

				usersWriter.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void processLoginReq(String req, BufferedReader usersReader) throws IOException {
		String username = req.split(":")[1];
		String password = "";

		if (req.split(":").length == 3)
			password = req.split(":")[2];

		String psw = getUserPassword(usersReader, username);

		if (psw != null) {
			if (!psw.equals(password))
				pw.println("NACK:WrongPassword\n");
			else {
				user.setUsername(username);
				onlineUsersList.add(user);
				pw.println("ACK:LoginAsAuthenticated\n");
			}
		} else {
			user.setUsername(username);
			onlineUsersList.add(user);
			pw.println("ACK:LoginAsGuest\n");
		}
		pw.flush();

		sendUsersList();
	}

	private void processLogoutReq(String req) throws IOException {
		onlineUsersList.remove(user);

		pw.println("ACK:Logout");
		pw.flush();

		sendUsersList();
		user.closeSocket();
	}

	private void processRegisterReq(String req, BufferedReader usersReader, BufferedWriter usersWriter)
			throws IOException {
		String user = req.split(":")[1];
		String password = req.split(":")[2];

		String psw = getUserPassword(usersReader, user);

		if (psw != null)
			pw.println("NACK:AlreadyExistingUser\n");
		else {
			usersWriter.write(user + ":" + password);
			usersWriter.newLine();
			pw.println("ACK:Registered\n");
		}
		pw.flush();
	}

	private void sendUsersList() throws UnsupportedEncodingException, IOException {
		String onlineUsers = "LIST:";
		PrintWriter p;

		for (User u : onlineUsersList) {
			onlineUsers += u.getUsername() + ":";
		}

		onlineUsers = onlineUsers.substring(0, onlineUsers.length() - 1);

		for (User u : onlineUsersList) {
			p = u.getPrintWriter();
			p.println(onlineUsers + "\n");
			p.flush();
		}
	}

	private void processMessage(String req) throws IOException {
		PrintWriter p;

		for (User u : onlineUsersList) {
			if (u.getUsername().contains("Goku")) {
				p = u.getPrintWriter();
				p.println("MESSAGE:" + req + "\n");
				p.flush();

			}
		}
	}

	private String getUserPassword(BufferedReader usersReader, String user) throws IOException {
		String line;
		String password = null;

		while ((line = usersReader.readLine()) != null) {
			String s = line.split(":")[0];
			if (s.equals(user))
				password = line.split(":")[1];
		}

		return password;
	}

}
