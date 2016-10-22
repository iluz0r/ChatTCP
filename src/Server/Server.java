package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Server implements Runnable {

	private Socket socket;

	public Server(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			String req = br.readLine();

			String path = System.getProperty("user.dir") + "/src/Server/users.txt";
			File f = new File(path);
			f.createNewFile();
			BufferedReader usersReader = new BufferedReader(new FileReader(path));
			BufferedWriter usersWriter = new BufferedWriter(new FileWriter(path, true));			
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

			if (req.contains("LOGIN:")) 
				processLoginReq(req, usersReader, pw);
			else if (req.contains("REGISTER:")) 
				processRegisterReq(req, usersReader, usersWriter, pw);
			
			usersWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void processLoginReq(String req, BufferedReader usersReader, PrintWriter pw) throws IOException {
		System.out.println(req);
		String user = req.split(":")[1];
		String password = req.split(":")[2];

		String psw  = getUserPassword(usersReader, user);

		if (psw != null) {
			if (!psw.equals(password))
				pw.print("NACK:WrongPassword");
			else
				pw.print("ACK:Authenticated");
		} else
			pw.print("ACK:LoginAsGuest");
		pw.flush();
	}
	
	private void processRegisterReq(String req, BufferedReader usersReader, BufferedWriter usersWriter, PrintWriter pw) throws IOException {
		String user = req.split(":")[1];
		String password = req.split(":")[2];

		String psw = getUserPassword(usersReader, user);

		if (psw != null) 
			pw.print("NACK:AlreadyExistingUser");
		else {
			usersWriter.write(user + ":" + password);
			usersWriter.newLine();
			pw.print("ACK:Registered");
		}
		pw.flush();
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
