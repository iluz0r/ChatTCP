package Client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

public class PrivateChatWindow {

	private JFrame frame;
	private JTextField messageTextField;
	private JTextArea chatTextArea;

	private String sender;
	private String receiver;
	private PrintWriter senderPw;

	public PrivateChatWindow(String sender, String receiver, PrintWriter senderPw) {
		this.sender = sender;
		this.receiver = receiver;
		this.senderPw = senderPw;
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setTitle(sender + " >> " + receiver);
		frame.setBounds(100, 100, 433, 270);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel chatPanel = new JPanel();
		chatPanel.setBounds(0, 0, 427, 218);
		chatPanel.setLayout(new BorderLayout(0, 0));		
		frame.getContentPane().add(chatPanel);

		chatTextArea = new JTextArea();
		chatTextArea.setColumns(61);
		chatTextArea.setLineWrap(true);
		chatTextArea.setWrapStyleWord(true);
		chatTextArea.setEditable(false);
		
        DefaultCaret caret = (DefaultCaret) chatTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
		JScrollPane chatScrollPane = new JScrollPane(chatTextArea);
		chatPanel.add(chatScrollPane);

		JPanel textPanel = new JPanel();
		textPanel.setBounds(0, 218, 427, 24);
		frame.getContentPane().add(textPanel);
		textPanel.setLayout(new BorderLayout(0, 0));

		messageTextField = new JTextField();
		textPanel.add(messageTextField);
		messageTextField.setColumns(28);
		messageTextField.addKeyListener(new SendTextKeyListener());

		frame.setVisible(true);
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setTextArea(String sender, String message) {
		chatTextArea.append(sender + ": " + message + "\n");
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String from) {
		this.sender = from;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	private class SendTextKeyListener extends KeyAdapter {

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (!messageTextField.getText().equals("")) {
					senderPw.println("PRIVATE:" + sender + ":" + receiver + ":" + messageTextField.getText());
					senderPw.flush();
					setTextArea(sender, messageTextField.getText());
					messageTextField.setText("");
				}
			}
		}

	}

}
