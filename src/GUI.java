import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import javax.swing.border.LineBorder;
import java.awt.SystemColor;

public class GUI {

	private JFrame frame;
	private JTextField userTextField;
	private JTextField messageTextField;

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
		frame.setBounds(100, 100, 374, 260);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JPanel loginPanel = new JPanel();
		loginPanel.setBorder(new LineBorder(SystemColor.controlShadow));
		loginPanel.setBounds(0, 0, 368, 69);
		frame.getContentPane().add(loginPanel);
		loginPanel.setLayout(null);
		
		userTextField = new JTextField();
		userTextField.setBounds(87, 25, 120, 20);
		loginPanel.add(userTextField);
		userTextField.setColumns(10);
		
		JLabel loginLabel = new JLabel("Username:");
		loginLabel.setBounds(10, 27, 67, 17);
		loginPanel.add(loginLabel);
		
		JButton loginButton = new JButton("Login");
		loginButton.setBounds(259, 25, 89, 20);
		loginPanel.add(loginButton);
		
		JPanel chatPanel = new JPanel();
		chatPanel.setBorder(new LineBorder(SystemColor.controlShadow));
		chatPanel.setBounds(0, 68, 368, 133);
		frame.getContentPane().add(chatPanel);
		chatPanel.setLayout(new BorderLayout(0, 0));
		
		JTextArea chatTextArea = new JTextArea();
		chatTextArea.setEditable(false);
		chatPanel.add(chatTextArea);
		
		JPanel textPanel = new JPanel();
		textPanel.setBorder(new LineBorder(SystemColor.controlShadow));
		textPanel.setBounds(0, 202, 368, 30);
		frame.getContentPane().add(textPanel);
		textPanel.setLayout(new BorderLayout(0, 0));
		
		messageTextField = new JTextField();
		textPanel.add(messageTextField);
		messageTextField.setColumns(10);
	}
}
