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
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;

public class Server implements Runnable {

	private Socket socket;
	private HashSet<LoginUsers> onlineUsersList;
	private BufferedReader br;
	private PrintWriter pw;
	private String req;

	public Server(Socket socket, HashSet<LoginUsers> onlineUsersList) {
		this.socket = socket;
		this.onlineUsersList = onlineUsersList;
		try {
			br = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
			pw = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream(), "UTF-8"));
			req="";
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	@Override
	public void run() {
		try {
			while((req=br.readLine())!=null){
			
			System.out.println("mela");

			String pathUsers = System.getProperty("user.dir") + "/src/Server/users.txt";

			File f = new File(pathUsers);
			f.createNewFile();
			BufferedReader usersReader = new BufferedReader(new FileReader(pathUsers));
			BufferedWriter usersWriter = new BufferedWriter(new FileWriter(pathUsers, true));						
				
			if (req.contains("LOGIN:"))
				processLoginReq(req, usersReader, pw);
			else if (req.contains("REGISTER:"))
				processRegisterReq(req, usersReader, usersWriter, pw);
			else if (req.contains("LOGOUT:"))
				processLogoutReq(req, pw);
			else if (req.contains("LIST:"))
				processListReq();
			else{			
				processMessage(req);
			}
			
			usersWriter.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void processLoginReq(String req, BufferedReader usersReader, PrintWriter pw) throws IOException {
		System.out.println(req);
		String user = req.split(":")[1];
		String password = "";
				
		if(req.split(":").length == 3) 
			password = req.split(":")[2];

		String psw = getUserPassword(usersReader, user);

		if (psw != null) {
			if (!psw.equals(password))
				pw.println("NACK:WrongPassword\n");
			else {
				pw.println("ACK:LoginAsAuthenticated\n");
				onlineUsersList.add(new LoginUsers(user, socket));				
			}
		} else {
			pw.println("ACK:LoginAsGuest\n");
			onlineUsersList.add(new LoginUsers("Guest " + user, socket));
		}
		pw.flush();
		processListReq();		
	}

	private void processRegisterReq(String req, BufferedReader usersReader, BufferedWriter usersWriter, PrintWriter pw)
			throws IOException {
		System.out.println(req);
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

	private void processLogoutReq(String req, PrintWriter pw) throws IOException {
		
		String user = req.split(":")[1];
		String s = "NACK:Logout";
		
		for(LoginUsers lu : onlineUsersList) {			
			if(lu.getUser().contains(user)) {
				onlineUsersList.remove(lu);
				s = "ACK:Logout";
				socket.close();
			}
		}

		pw.println(s+"\n");
		pw.flush();
	}

	private void processListReq() throws UnsupportedEncodingException, IOException {
		String listUsersOnline = "LIST:";
		Socket socket;
		PrintWriter pw;
		
		for(LoginUsers lu : onlineUsersList) {
			listUsersOnline += lu.getUser() + ":";		
		}
		listUsersOnline=listUsersOnline.substring(0, listUsersOnline.length()-1);
		System.out.println(listUsersOnline);
		
		for(LoginUsers lu : onlineUsersList) {			
				socket=lu.getClient();
								
				pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
				pw.println(listUsersOnline+"\n");
				pw.flush();
			
		}
		
	}
	
	private void processMessage(String req) throws IOException{
		
		PrintWriter pw;
		Socket socket;		
		String address;
		int port;
		
		for(LoginUsers lu : onlineUsersList) {			
			if(lu.getUser().contains("Goku")) {
				socket=lu.getClient();
				
				
				pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
				pw.println("MESSAGE:"+req+"\n");
				pw.flush();
				
				
			}
		}
	}

}
