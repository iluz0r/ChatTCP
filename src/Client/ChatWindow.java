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
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Color;

public class ChatWindow {

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
	private ArrayList<PrivateChatWindow> privateChatWindowList;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatWindow window = new ChatWindow();
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
	public ChatWindow() throws UnknownHostException, IOException {
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
		frame.setBounds(100, 100, 497, 283);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.addWindowListener(new CloseWindowListener());

		JPanel loginPanel = new JPanel();
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
		chatPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
		chatPanel.setBounds(0, 70, 368, 156);
		frame.getContentPane().add(chatPanel);
		chatPanel.setLayout(new BorderLayout(0, 0));

		chatTextArea = new JTextArea();
		chatTextArea.setEditable(false);
		chatPanel.add(chatTextArea);

		JPanel textPanel = new JPanel();
		textPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
		textPanel.setForeground(Color.BLACK);
		textPanel.setBounds(0, 225, 368, 30);
		frame.getContentPane().add(textPanel);
		textPanel.setLayout(new BorderLayout(0, 0));

		messageTextField = new JTextField();
		messageTextField.addKeyListener(new SendTextKeyListener());
		textPanel.add(messageTextField);
		messageTextField.setColumns(10);

		listModel = new DefaultListModel<String>();

		JPanel listPanel = new JPanel();
		listPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
		listPanel.setBounds(368, 70, 123, 185);
		frame.getContentPane().add(listPanel);
		listPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		listPanel.add(scrollPane);
		list = new JList<String>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(0);
		list.setVisibleRowCount(5);
		list.addMouseListener(new ListDoubleClickListener());
		scrollPane.setViewportView(list);

		privateChatWindowList = new ArrayList<>();
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
						listModel.removeAllElements();
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
						String sender = answer.split(":")[1];
						String message = answer.split(":")[2];
						chatTextArea.append(sender + ": " + message + "\n");
					} else if (answer.startsWith("PRIVATE:")) {
						String sender = answer.split(":")[1];
						String receiver = answer.split(":")[2];
						String message = answer.split(":")[3];
						String s, r;
						int flagWindow = 0;

						// Controlla se la finestra era stata già aperta e poi
						// chiusa
						for (PrivateChatWindow window : privateChatWindowList) {
							r = window.getReceiver();
							s = window.getSender();
							if (s.equals(receiver) && r.equals(sender)) {
								window.setTextArea(sender, message);
								flagWindow = 1;
								if (window.getFrame().isVisible() == false)
									window.getFrame().setVisible(true);
							}
						}

						// Se la finestra non è mai stata creata, la crea
						if (flagWindow == 0) {
							PrivateChatWindow pm = new PrivateChatWindow(receiver, sender, clientConn.getPrintWriter());
							privateChatWindowList.add(pm);
							pm.setTextArea(sender, message);
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

				if ((clientConn == null || clientConn.isClosed())) {
					try {
						initConnection();
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(frame, "Server non raggiungibile", "Errore", JOptionPane.ERROR_MESSAGE);
					}
				}
				if (clientConn != null && !clientConn.isClosed()) {
					PrintWriter pw = clientConn.getPrintWriter();
					pw.println(loginReq);
					pw.flush();
				}
			} else
				JOptionPane.showMessageDialog(frame, "Username mancante", "Messaggio", JOptionPane.INFORMATION_MESSAGE);
		}

	}

	private class RegisterButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String username = userTextField.getText();
			String password = passwordTextField.getText();

			if (!username.equals("") && !password.equals("")) {
				String registerReq = "REGISTER:" + username + ":" + password;

				if ((clientConn == null || clientConn.isClosed())) {
					try {
						initConnection();
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(frame, "Server non raggiungibile", "Errore", JOptionPane.ERROR_MESSAGE);
					}
				}
				if (clientConn != null && !clientConn.isClosed()) {
					PrintWriter pw = clientConn.getPrintWriter();
					pw.println(registerReq);
					pw.flush();
				}
			} else
				JOptionPane.showMessageDialog(frame, "Username/Password mancante/i", "Messaggio",
						JOptionPane.INFORMATION_MESSAGE);
		}

	}

	private class SendTextKeyListener extends KeyAdapter {

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (!messageTextField.getText().equals("")) {
					PrintWriter pw = clientConn.getPrintWriter();
					pw.println("MESSAGE:" + userTextField.getText() + ":" + messageTextField.getText());
					pw.flush();
					messageTextField.setText("");
				}
			}
		}

	}

	private class ListDoubleClickListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent evt) {
			JList<?> list = (JList<?>) evt.getSource();

			if (evt.getClickCount() == 2) {
				int index = list.locationToIndex(evt.getPoint());
				String receiver = listModel.getElementAt(index);
				String sender = userTextField.getText();
				boolean found = false;

				for (PrivateChatWindow window : privateChatWindowList) {
					if (window.getSender().equals(sender) && window.getReceiver().equals(receiver)) {
						if (window.getFrame().isVisible() == false)
							window.getFrame().setVisible(true);
						found = true;
					}
				}
				if (!found) {
					PrintWriter pw = clientConn.getPrintWriter();

					PrivateChatWindow privateMessage = new PrivateChatWindow(sender, receiver, pw);
					privateChatWindowList.add(privateMessage);
				}
			}
		}

	}

	private class CloseWindowListener extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			if (loginButtonState.equals("LOGOUT")) {
				PrintWriter pw = clientConn.getPrintWriter();
				pw.println(loginButtonState + ":" + userTextField.getText() + ":" + passwordTextField.getText());
				pw.flush();
			}
		}

	}
}