package Client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import javax.swing.border.LineBorder;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

public class GUI {

	private JFrame frame;
	private JTextField userTextField;
	private JTextField messageTextField;
	private JTextArea chatTextArea;
	private JTextField passwordTextField;
	private JButton loginButton;
	private String loginButtonState;
	private JList<String> list;
	private DefaultListModel<String> listModel;
	
	private Thread t;
	private BufferedReader br;
	private PrintWriter pw;
	private Socket socket;
	private Client client;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("ChatTCP");
		frame.setResizable(false);
		frame.setBounds(100, 100, 497, 260);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel loginPanel = new JPanel();
		loginPanel.setBorder(new LineBorder(SystemColor.controlShadow));
		loginPanel.setBounds(0, 0, 491, 69);
		frame.getContentPane().add(loginPanel);
		loginPanel.setLayout(null);

		userTextField = new JTextField();
		userTextField.setBounds(87, 9, 120, 20);
		loginPanel.add(userTextField);
		userTextField.setColumns(10);

		JLabel loginLabel = new JLabel("Username:");
		loginLabel.setBounds(10, 11, 67, 17);
		loginPanel.add(loginLabel);

		loginButton = new JButton("Login");
		loginButtonState = "LOGIN";
		loginButton.addActionListener(new LoginButtonListener());
		loginButton.setBounds(242, 9, 89, 20);
		loginPanel.add(loginButton);

		JLabel passwordLabel = new JLabel("Password:");
		passwordLabel.setBounds(10, 44, 67, 14);
		loginPanel.add(passwordLabel);

		passwordTextField = new JTextField();
		passwordTextField.setBounds(87, 41, 120, 20);
		loginPanel.add(passwordTextField);
		passwordTextField.setColumns(10);

		JButton registerButton = new JButton("Register");
		registerButton.addActionListener(new RegisterButtonListener());
		registerButton.setBounds(242, 41, 89, 20);
		loginPanel.add(registerButton);

		JButton btnList = new JButton("List");
		btnList.addActionListener(new ListButtonListener());
		btnList.setBounds(382, 9, 97, 23);
		loginPanel.add(btnList);

		JPanel chatPanel = new JPanel();
		chatPanel.setBorder(new LineBorder(SystemColor.controlShadow));
		chatPanel.setBounds(0, 68, 368, 133);
		frame.getContentPane().add(chatPanel);
		chatPanel.setLayout(new BorderLayout(0, 0));

		chatTextArea = new JTextArea();
		chatTextArea.setEditable(false);
		chatPanel.add(chatTextArea);

		JPanel textPanel = new JPanel();
		textPanel.setBorder(new LineBorder(SystemColor.controlShadow));
		textPanel.setBounds(0, 202, 368, 30);
		frame.getContentPane().add(textPanel);
		textPanel.setLayout(new BorderLayout(0, 0));

		messageTextField = new JTextField();
		messageTextField.addKeyListener(new SendTextKeyListener());
		textPanel.add(messageTextField);
		messageTextField.setColumns(10);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(369, 68, 122, 164);
		frame.getContentPane().add(scrollPane);

		listModel = new DefaultListModel<String>();
		list = new JList<String>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(0);
		list.setVisibleRowCount(5);
		scrollPane.setViewportView(list);
		
		list.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt) {
		        JList list = (JList)evt.getSource();
		        if (evt.getClickCount() == 2) {

		            // Double-click detected
		            int index = list.locationToIndex(evt.getPoint());
		            System.out.println("Utente: "+index);
		        }
		    }
		});
		
		try {
			client = new Client();
			socket = client.getSocket();
			pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));		
			br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		thread();
	}
	
	private void thread(){
		
	
		
		t = new Thread(new Runnable(){
			public void run() {				
				
					synchronized(t){
						try {
							
							String answer = "";
							t.wait();
							
						    while ((answer = br.readLine())!=null) 
						    {
						    	System.out.println("Il Thread funziona ed ha ricevuto "+answer);

								String list[];
								if(answer.contains("LIST:")){									
									list = answer.split(":");
									listModel.removeAllElements();
									for (int i = 1; i < list.length; i++){
										System.out.println(list[i]);
										listModel.addElement(list[i]);
									}								
								}else if(answer.contains("MESSAGE:")){
									
									chatTextArea.append(answer.split(":")[1] + "\n");
									messageTextField.setText("");
								}
							}
							
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
						
			}
			
			
		});
		
		t.setDaemon(true);
		t.start();
		
	}

	class LoginButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {			
			try {							
				String loginReq = loginButtonState + ":" + userTextField.getText() + ":" + passwordTextField.getText();

				System.out.println(loginReq);
				pw.println(loginReq);
				pw.flush();
				
					String answer = br.readLine();
					System.out.println("Ho letto qui"+answer);
					if (answer.contains("ACK:Login")) {
						loginButtonState = "LOGOUT";
						loginButton.setText("Logout");
					} else if (answer.contains("ACK:Logout")) {
						loginButtonState = "LOGIN";
						loginButton.setText("Login");
					}					
					synchronized(t) {
						t.notify();
					}				
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	class RegisterButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {		
			try {			
				String answer;
				String registerReq = "REGISTER:" + userTextField.getText() + ":" + passwordTextField.getText();								
				
				pw.println(registerReq);
				pw.flush();
				
				System.out.println(br.readLine());
				
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	class ListButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			try {				
				String listReq = "LIST:";
				String answer="";
				String[] user = null;
				
				pw.println(listReq);
				pw.flush();

				listModel.removeAllElements();
				
				answer = br.readLine();
					System.out.println("readLine ha ricevuto "+answer);
	
					user = answer.split(":");
					
								 
					System.out.println("Ci sono tot people loggati "+user.length);
					 for (int i = 1; i < user.length; i++)
						listModel.addElement(user[i]);
	
					System.out.println("Ho questo numero di elementi: " + user.length);
					System.out.println(answer);										
				
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	class SendTextKeyListener extends KeyAdapter {

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				try {
					Client client = new Client();
					Socket socket = client.getSocket();

					//PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
					pw.println(messageTextField.getText());
					System.out.println(messageTextField.getText());
					pw.flush();

					//BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
					//String answer = br.readLine();
					//chatTextArea.append(answer + "\n");
					//messageTextField.setText("");
					//socket.close();
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

	}
	
	class MouseEventListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getClickCount()==2){
	            PrivateMessage openGui = new PrivateMessage();
	        }
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

	
	}
}
