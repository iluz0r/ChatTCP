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
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JOptionPane;

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

	private ClientConnection clientConn;
	private Thread serverListener;
	private ArrayList<PrivateMessage> listPrivateUsers;

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
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public GUI() throws UnknownHostException, IOException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	private void initialize() throws UnknownHostException, IOException {
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
		list.addMouseListener(new ListDoubleClickListener());
		scrollPane.setViewportView(list);
		
		list.addMouseListener(new MouseEventListener());
		listPrivateUsers=new ArrayList<PrivateMessage>();

		initConnection();
	}

	private void initConnection() throws UnknownHostException, IOException {
		clientConn = new ClientConnection();

		serverListener = new Thread(new ServerListener());
		serverListener.start();
	}

	private class ServerListener implements Runnable {

		@Override
		public void run() {
			try {
				String answer;
				BufferedReader br = clientConn.getBufferedReader();

				while (!clientConn.isClosed() && (answer = br.readLine()) != null) {
					System.out.println("Il server listener ha ricevuto " + answer);

					if (answer.startsWith("ACK:Login")) {
						loginButtonState = "LOGOUT";
						loginButton.setText("Logout");
					} else if (answer.equals("NACK:WrongPassword")) {
						JOptionPane.showMessageDialog(frame, "Password errata", "Errore", JOptionPane.ERROR_MESSAGE);
					} else if (answer.equals("NACK:UserAlreadyOnline")) {
						JOptionPane.showMessageDialog(frame, "L'utente è già connesso", "Errore",
								JOptionPane.ERROR_MESSAGE);
					} else if (answer.equals("NACK:NotExistingUser")) {
						JOptionPane.showMessageDialog(frame, "Lo username scelto non è esistente", "Errore",
								JOptionPane.ERROR_MESSAGE);
					} else if (answer.equals("ACK:Logout")) {
						loginButtonState = "LOGIN";
						loginButton.setText("Login");
						clientConn.closeSocket();
					} else if (answer.equals("ACK:Registered")) {
						JOptionPane.showMessageDialog(frame, "Lo username è ora registrato", "Messaggio",
								JOptionPane.INFORMATION_MESSAGE);
					} else if (answer.equals("NACK:UsernameNotAvailable")) {
						JOptionPane.showMessageDialog(frame, "Lo username scelto è già registrato", "Errore",
								JOptionPane.ERROR_MESSAGE);
					} else if (answer.startsWith("LIST:")) {
						String[] list = answer.split(":");
						listModel.removeAllElements();

						for (int i = 1; i < list.length; i++)
							listModel.addElement(list[i]);
					} else if (answer.startsWith("MESSAGE:")) {
						String from=answer.split(":")[1];
						String message=answer.split(":")[2];
						chatTextArea.append(from + ":" + message +"\n");
						messageTextField.setText("");
					} else if (answer.contains("PRIVATEFROM:")){
						String from=answer.split(":")[1];
						String to=answer.split(":")[2];								
						String message=answer.split(":")[3];
						String temp;
						System.out.println(answer);
						System.out.println("Ci sono "+listPrivateUsers.size()+" elementi");
						int flagWindow=0;
						for (PrivateMessage pm : listPrivateUsers) {
							temp=pm.getTo();
							System.out.println("Cosa c'è in questo piripicchio "+temp);
							if (temp.contains(from)) {
								System.out.println("Messaggio da "+from);
								pm.setTextArea(from, message);
								flagWindow=1;
							}
						}
						System.out.println("flagWindow vale "+flagWindow);
						if(flagWindow==0){
							PrivateMessage pm = new PrivateMessage(from,to,clientConn.getPrintWriter());
							listPrivateUsers.add(pm);
							pm.setTextArea(from, message);
						}
					}
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private class LoginButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String username = userTextField.getText();

			if (!username.equals("")) {
				String loginReq = loginButtonState + ":" + username + ":" + passwordTextField.getText();

				if (clientConn.isClosed() && !serverListener.isAlive()) {
					try {
						initConnection();
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

				PrintWriter pw = clientConn.getPrintWriter();
				pw.println(loginReq);
				pw.flush();
			} else {
				JOptionPane.showMessageDialog(frame, "Username mancante", "Messaggio", JOptionPane.INFORMATION_MESSAGE);
			}
		}

	}

	private class RegisterButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String username = userTextField.getText();
			String password = passwordTextField.getText();

			if (!username.equals("") && !password.equals("")) {
				String registerReq = "REGISTER:" + username + ":" + password;

				if (clientConn.isClosed() && !serverListener.isAlive()) {
					try {
						initConnection();
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				PrintWriter pw = clientConn.getPrintWriter();
				pw.println(registerReq);
				pw.flush();
			} else {
				JOptionPane.showMessageDialog(frame, "Username/Password mancante/i", "Messaggio",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}

	}

	private class ListDoubleClickListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent evt) {
			JList<?> list = (JList<?>) evt.getSource();

			if (evt.getClickCount() == 2) {
				int index = list.locationToIndex(evt.getPoint());
				String user = listModel.getElementAt(index);
				System.out.println("Utente: " + user);
			}
		}

	}

	class SendTextKeyListener extends KeyAdapter {

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				
				PrintWriter pw = clientConn.getPrintWriter();
				pw.println("MESSAGE:"+userTextField.getText()+":"+messageTextField.getText());
				System.out.println("MESSAGE:"+userTextField.getText()+":"+messageTextField.getText());
				pw.flush();

			}
		}

	}

	class MouseEventListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent evt) {
			JList<String> list = (JList) evt.getSource();
			if (evt.getClickCount() == 2) {

				// Double-click detected
								
				int index = list.locationToIndex(evt.getPoint());
				String to = listModel.getElementAt(index);
				String from = userTextField.getText();
				PrintWriter pw = clientConn.getPrintWriter();
				
				PrivateMessage privateMessage = new PrivateMessage(from, to, pw);
				listPrivateUsers.add(privateMessage);
				
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