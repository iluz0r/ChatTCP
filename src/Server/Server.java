package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
<<<<<<< HEAD
import java.io.File;
=======
import java.io.FileOutputStream;
>>>>>>> branch 'master' of https://github.com/iluz0r/ChatTCP.git
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
			String s = br.readLine();

<<<<<<< HEAD
			String user = null, password = null;
			String path = System.getProperty("user.dir") + "/src/Server/users.txt";
			File f = new File(path);
			f.createNewFile();
			BufferedReader usersReader = new BufferedReader(new FileReader(path));
			BufferedWriter usersWriter = new BufferedWriter(new FileWriter(path, true));
=======
			String user = null;
			String password = null;
			String line=null;
			String listUser = System.getProperty("user.dir")+"\\src\\Server\\users.txt";

			if (s.contains("LOGIN:")) {
				user = s.split(":")[1];
				password = s.split(":")[2];
			}
			
			BufferedReader checkUser = new BufferedReader(new FileReader(listUser));
			
>>>>>>> branch 'master' of https://github.com/iluz0r/ChatTCP.git
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
<<<<<<< HEAD

			if (s.contains("LOGIN:")) {
				user = s.split(":")[1];
				password = s.split(":")[2];

				String psw  = getUserPassword(usersReader, user);

				if (psw != null) {
					if (!psw.equals(password))
						pw.print("NACK:InvalidPassword");
					else
						pw.print("ACK:Authenticated");
				} else
					pw.print("ACK:LoginAsGuest");
				pw.flush();
			}
			if (s.contains("REGISTER:")) {
				user = s.split(":")[1];
				password = s.split(":")[2];

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
			
			usersWriter.close();
=======
			
			OutputStreamWriter users = new OutputStreamWriter(new FileOutputStream(listUser,true),"UTF-8");					
			users.write(user+":"+password+"\n");					
			users.close();
			/*
			while((line=checkUser.readLine())!=null){		
				if(line.split(":")[0].equals(user)){
					pw.print("NACK:Invalid User");
					pw.flush();
				}else{
					
				}
			}*/
					
			
			
			
			
			System.out.println(user + " " + password);

			
			pw.println(s);
 
			// Svuota i buffer, forzando l'invio dei dati
			pw.flush();
>>>>>>> branch 'master' of https://github.com/iluz0r/ChatTCP.git
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
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
