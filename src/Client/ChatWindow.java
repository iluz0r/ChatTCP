package Client;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultCaret;
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
	private JPasswordField passwordField;
	private JButton loginButton;
	private String loginButtonState;
	private JButton registerButton;
	private JList<String> list;

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

		passwordField = new JPasswordField();
		passwordField.setBounds(87, 41, 120, 20);
		loginPanel.add(passwordField);
		passwordField.setColumns(10);

		registerButton = new JButton("Register");
		registerButton.addActionListener(new RegisterButtonListener());
		registerButton.setBounds(242, 41, 89, 20);
		loginPanel.add(registerButton);

		JPanel chatPanel = new JPanel();
		chatPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
		chatPanel.setBounds(0, 70, 368, 156);
		chatPanel.setLayout(new BorderLayout(0, 0));
		frame.getContentPane().add(chatPanel);

		chatTextArea = new JTextArea();
		chatTextArea.setColumns(51);
		chatTextArea.setLineWrap(true);
		chatTextArea.setWrapStyleWord(true);
		chatTextArea.setEditable(false);

		DefaultCaret caret = (DefaultCaret) chatTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		JScrollPane chatScrollPane = new JScrollPane(chatTextArea);
		chatPanel.add(chatScrollPane);

		JPanel textPanel = new JPanel();
		textPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
		textPanel.setForeground(Color.BLACK);
		textPanel.setBounds(0, 225, 368, 30);
		textPanel.setLayout(new BorderLayout(0, 0));
		frame.getContentPane().add(textPanel);

		messageTextField = new JTextField();
		messageTextField.setEnabled(false);
		messageTextField.addKeyListener(new SendTextKeyListener());
		textPanel.add(messageTextField);

		JPanel listPanel = new JPanel();
		listPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
		listPanel.setBounds(368, 70, 123, 185);
		frame.getContentPane().add(listPanel);
		listPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		listPanel.add(scrollPane);
		list = new JList<>(new DefaultListModel<String>());
		list.setEnabled(false);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
				String resp;
				BufferedReader br = clientConn.getBufferedReader();

				while (!clientConn.isClosed() && (resp = br.readLine()) != null) {
					System.out.println("Il server listener ha ricevuto " + resp);

					if (resp.startsWith("ACK:Login"))
						processLoginResp();
					else if (resp.equals("NACK:WrongPassword"))
						showMessageResp("Password errata", JOptionPane.ERROR_MESSAGE);
					else if (resp.equals("NACK:UserAlreadyOnline"))
						showMessageResp("L'utente è già connesso", JOptionPane.ERROR_MESSAGE);
					else if (resp.equals("NACK:NotExistingUser"))
						showMessageResp("Lo username scelto non è esistente", JOptionPane.ERROR_MESSAGE);
					else if (resp.equals("ACK:Logout"))
						processLogoutResp();
					else if (resp.equals("ACK:Registered"))
						showMessageResp("Lo username è ora registrato", JOptionPane.INFORMATION_MESSAGE);
					else if (resp.equals("NACK:UsernameNotAvailable"))
						showMessageResp("Lo username scelto è già registrato", JOptionPane.ERROR_MESSAGE);
					else if (resp.startsWith("LIST:"))
						processListResp(resp);
					else if (resp.startsWith("MESSAGE:"))
						processMessageResp(resp);
					else if (resp.startsWith("PRIVATE:"))
						processPrivateResp(resp);
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void processLoginResp() {
			loginButtonState = "LOGOUT";
			loginButton.setText("Logout");
			messageTextField.setEnabled(true);
			list.setEnabled(true);
			userTextField.setEnabled(false);
			passwordField.setEnabled(false);
			registerButton.setEnabled(false);
		}

		private void processLogoutResp() throws IOException {
			loginButtonState = "LOGIN";
			loginButton.setText("Login");
			messageTextField.setEnabled(false);
			userTextField.setEnabled(true);
			passwordField.setEnabled(true);
			registerButton.setEnabled(true);
			list.setModel(new DefaultListModel<String>());	
			list.setEnabled(false);
			privateChatWindowList.clear();
			clientConn.closeSocket();
		}

		private void processListResp(String resp) {
			String[] users = resp.split(":");
			DefaultListModel<String> m = new DefaultListModel<>();
			
			for (int i = 1; i < users.length; i++) {
				System.out.print(users[i]);
				m.addElement(users[i]);
				System.out.println(" " + m.size());
			}
			list.setModel(m);
		}

		private void processMessageResp(String resp) {
			int messageType = Integer.valueOf(resp.split(":", 4)[1]);
			String sender = resp.split(":", 4)[2];
			String message = resp.split(":", 4)[3];

			if (messageType == 0)
				chatTextArea.append(sender + ": " + message + "\n");
			else
				chatTextArea.append(sender + " " + message + "\n");
		}

		private void processPrivateResp(String resp) {
			String sender = resp.split(":", 4)[1];
			String receiver = resp.split(":", 4)[2];
			String message = resp.split(":", 4)[3];
			boolean found = false;

			for (PrivateChatWindow window : privateChatWindowList) {
				if (window.getSender().equals(receiver) && window.getReceiver().equals(sender)) {
					window.setTextArea(sender, message);
					if (window.getFrame().isVisible() == false)
						window.getFrame().setVisible(true);
					found = true;
				}
			}
			if (!found) {
				PrivateChatWindow window = new PrivateChatWindow(receiver, sender, clientConn.getPrintWriter());
				privateChatWindowList.add(window);
				window.setTextArea(sender, message);
			}
		}

		private void showMessageResp(String resp, int messageType) {
			JOptionPane.showMessageDialog(frame, resp, "Messaggio", messageType);
		}

	}

	private class LoginButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String username = userTextField.getText();

			if (!username.equals("")) {
				String loginReq = loginButtonState + ":" + username + ":" + String.valueOf(passwordField.getPassword());

				if (clientConn == null || clientConn.isClosed()) {
					try {
						initConnection();
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(frame, "Server non raggiungibile", "Errore",
								JOptionPane.ERROR_MESSAGE);
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
			String password = String.valueOf(passwordField.getPassword());

			if (!username.equals("") && !password.equals("")) {
				String registerReq = "REGISTER:" + username + ":" + password;

				if (clientConn == null || clientConn.isClosed()) {
					try {
						initConnection();
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(frame, "Server non raggiungibile", "Errore",
								JOptionPane.ERROR_MESSAGE);
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
					pw.println("MESSAGE:" + "0:" + userTextField.getText() + ":" + messageTextField.getText());
					pw.flush();
					messageTextField.setText("");
				}
			}
		}

	}

	private class ListDoubleClickListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent evt) {
			JList<?> l = (JList<?>) evt.getSource();

			if (l.isEnabled() && evt.getClickCount() == 2) {
				int index = l.locationToIndex(evt.getPoint());
				DefaultListModel<String> listModel = (DefaultListModel<String>)list.getModel();
				String receiver = (String) listModel.getElementAt(index);
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
					PrivateChatWindow window = new PrivateChatWindow(sender, receiver, clientConn.getPrintWriter());
					privateChatWindowList.add(window);
				}
			}
		}

	}

	private class CloseWindowListener extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			if (loginButtonState.equals("LOGOUT")) {
				PrintWriter pw = clientConn.getPrintWriter();
				pw.println(loginButtonState + ":" + userTextField.getText() + ":"
						+ String.valueOf(passwordField.getPassword()));
				pw.flush();
			}
		}

	}

}