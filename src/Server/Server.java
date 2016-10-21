package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
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

			String user = null;
			String password = null;
			String line=null;
			String listUser = System.getProperty("user.dir")+"\\src\\Server\\users.txt";

			if (s.contains("LOGIN:")) {
				user = s.split(":")[1];
				password = s.split(":")[2];
			}
			
			BufferedReader checkUser = new BufferedReader(new FileReader(listUser));
			
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
			
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

}
