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

	private UserConnection userConnection;
	private ArrayList<UserConnection> onlineUsersList;

	public ClientHandler(Socket socket, ArrayList<UserConnection> onlineUsersList)
			throws UnsupportedEncodingException, IOException {
		userConnection = new UserConnection(socket);
		this.onlineUsersList = onlineUsersList;
	}

	@Override
	public void run() {
		try {
			String req;
			BufferedReader br = userConnection.getBufferedReader();

			while (!userConnection.isClosed() && (req = br.readLine()) != null) {
				String pathUsers = System.getProperty("user.dir") + "/src/Server/users.txt";

				File f = new File(pathUsers);
				f.createNewFile();
				BufferedReader usersReader = new BufferedReader(new FileReader(pathUsers));
				BufferedWriter usersWriter = new BufferedWriter(new FileWriter(pathUsers, true));

				if (req.startsWith("LOGIN:"))
					processLoginReq(req, usersReader);
				else if (req.startsWith("REGISTER:"))
					processRegisterReq(req, usersReader, usersWriter);
				else if (req.startsWith("LOGOUT:"))
					processLogoutReq(req);
				else if (req.startsWith("PRIVATE:"))
					processPrivateReq(req);
				else if (req.startsWith("MESSAGE:"))
					processMessageReq(req);

				usersWriter.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void processLoginReq(String req, BufferedReader usersReader) throws IOException {
		String username = req.split(":")[1];
		String reqPsw = null;
		String resp = "";

		if (isUserOnline(username))
			resp = "NACK:UserAlreadyOnline";
		else {
			String psw = getUserPassword(usersReader, username);
			if (req.split(":").length == 3)
				reqPsw = req.split(":")[2];

			if (psw != null) {
				if (reqPsw != null) {
					if (psw.equals(reqPsw))
						resp = "ACK:LoginAsAuthenticated";
					else
						resp = "NACK:WrongPassword";
				} else
					resp = "NACK:WrongPassword";
			} else if (reqPsw == null)
				resp = "ACK:LoginAsGuest";
			else
				resp = "NACK:NotExistingUser";
		}
		PrintWriter pw = userConnection.getPrintWriter();
		pw.println(resp);
		pw.flush();

		if (resp.startsWith("ACK:")) {
			userConnection.setUsername(username);
			onlineUsersList.add(userConnection);
			sendUsersList();
			processMessageReq("MESSAGE:" + "1:" + username + ":" + "si è connesso");
		}
	}

	private void processLogoutReq(String req) throws IOException {
		onlineUsersList.remove(userConnection);

		PrintWriter pw = userConnection.getPrintWriter();
		pw.println("ACK:Logout");
		pw.flush();

		sendUsersList();
		processMessageReq("MESSAGE:" + "1:" + userConnection.getUsername() + ":" + "si è disconnesso");
		userConnection.closeSocket();
	}

	private void processRegisterReq(String req, BufferedReader usersReader, BufferedWriter usersWriter)
			throws IOException {
		String username = req.split(":")[1];
		String password = req.split(":")[2];
		PrintWriter pw = userConnection.getPrintWriter();
		String psw = getUserPassword(usersReader, username);

		if (psw != null)
			pw.println("NACK:UsernameNotAvailable");
		else {
			usersWriter.write(username + ":" + password);
			usersWriter.newLine();
			pw.println("ACK:Registered");
		}
		pw.flush();
	}

	private void processMessageReq(String req) throws IOException {
		int messageType = Integer.valueOf(req.split(":", 4)[1]);
		String sender = req.split(":", 4)[2];
		String message = req.split(":", 4)[3];

		PrintWriter pw;
		for (UserConnection userConnection : onlineUsersList) {
			pw = userConnection.getPrintWriter();
			pw.println("MESSAGE:" + messageType + ":" + sender + ":" + message);
			pw.flush();
		}
	}

	private void processPrivateReq(String req) throws IOException {
		String sender = req.split(":", 4)[1];
		String receiver = req.split(":", 4)[2];
		String message = req.split(":", 4)[3];
		
		PrintWriter pw;
		for (UserConnection userConnection : onlineUsersList) {
			if (userConnection.getUsername().equals(receiver)) {
				pw = userConnection.getPrintWriter();
				pw.println("PRIVATE:" + sender + ":" + receiver + ":" + message);
				pw.flush();
			}
		}
	}

	private void sendUsersList() throws UnsupportedEncodingException, IOException {
		String onlineUsers = "LIST:";
		PrintWriter pw;

		for (UserConnection userConnection : onlineUsersList) {
			onlineUsers += userConnection.getUsername() + ":";
		}

		onlineUsers = onlineUsers.substring(0, onlineUsers.length() - 1);

		for (UserConnection userConnection : onlineUsersList) {
			pw = userConnection.getPrintWriter();
			pw.println(onlineUsers);
			pw.flush();
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

	private boolean isUserOnline(String username) {
		boolean found = false;

		for (UserConnection userConnection : onlineUsersList) 
			if (userConnection.getUsername().equals(username))
				found = true;
		
		return found;
	}

}
