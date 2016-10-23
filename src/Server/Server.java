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
import java.util.HashSet;
import java.util.Iterator;

public class Server implements Runnable {

	private Socket socket;
	private HashSet<LoginUsers> listLoginUsers;
	private LoginUsers userLogin;

	public Server(Socket socket, HashSet<LoginUsers> listLoginUsers) {
		this.socket = socket;
		this.listLoginUsers = listLoginUsers;
	}

	@Override
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			String req = br.readLine();

			String pathUsers = System.getProperty("user.dir") + "/src/Server/users.txt";			
			
			File f = new File(pathUsers);
			f.createNewFile();
			BufferedReader usersReader = new BufferedReader(new FileReader(pathUsers));
			BufferedWriter usersWriter = new BufferedWriter(new FileWriter(pathUsers, true));
				
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

			if (req.contains("LOGIN:"))
				processLoginReq(req, usersReader, listLoginUsers, pw);
			else if (req.contains("REGISTER:")) 
				processRegisterReq(req, usersReader, usersWriter, pw);
			else if(req.contains("LOGOUT:"))
				processLogoutReq(req, listLoginUsers, pw);
			else if(req.contains("LIST:"))
				processListReq(listLoginUsers, pw);
			
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
	
	private void processLoginReq(String req, BufferedReader usersReader, HashSet<LoginUsers> listLoginUser, PrintWriter pw) throws IOException {
		System.out.println(req);
		String user = req.split(":")[1];
		String password = req.split(":")[2];

		String psw  = getUserPassword(usersReader, user);

		if (psw != null) {
			if (!psw.equals(password))
				pw.print("NACK:WrongPassword");
			else{
				pw.print("ACK:LoginAsAuthenticated");				
				listLoginUser.add(new LoginUsers(user));
			}
		} else {
			pw.print("ACK:LoginAsGuest");
			listLoginUser.add(new LoginUsers("Guest "+user));
		}
		pw.flush();
	}
	
	private void processRegisterReq(String req, BufferedReader usersReader, BufferedWriter usersWriter, PrintWriter pw) throws IOException {
		System.out.println(req);
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
	
	public synchronized void processLogoutReq(String req, HashSet<LoginUsers> listLoginUser, PrintWriter pw) throws IOException{
		
	/*	while(req.contains("LOGOUT")==false)			
			wait();
			
			notify();
	*/
		String user = req.split(":")[1];
		int remove = 0;
		
		Iterator<LoginUsers> itr = listLoginUser.iterator();
        while(itr.hasNext()){
        	if(itr.next().getUser().contains(user)){
        		itr.remove();
        		pw.print("ACK:Logout");
        		remove=1;
        	}
        }        
        
		if( remove == 0)		
			pw.print("NACK:Logout");
		
		pw.flush();
			
	}
	
	private void processListReq(HashSet<LoginUsers> listLoginUser, PrintWriter pw) {
		String listUsersOnline = null;
		Iterator<LoginUsers> itr = listLoginUser.iterator();
		int i=0;
		
        while(itr.hasNext()){
        	listUsersOnline +=itr.next().getUser()+":";
        	i++;
        }
        
        System.out.println(i+"  "+listUsersOnline);
        
        listUsersOnline=listUsersOnline.substring(4, listUsersOnline.length()-1);        
        
        System.out.println(i+"  "+listUsersOnline);
        
        pw.print(listUsersOnline);
        pw.flush();
	}

}
