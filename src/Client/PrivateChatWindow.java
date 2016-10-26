package Client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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
		chatPanel.setBounds(0, 0, 434, 218);
		frame.getContentPane().add(chatPanel);
		chatPanel.setLayout(new BorderLayout(0, 0));

		chatTextArea = new JTextArea();
		chatTextArea.setEditable(false);
		chatTextArea.setRows(12);
		chatPanel.add(chatTextArea);

		JPanel textPanel = new JPanel();
		textPanel.setBounds(0, 218, 434, 27);
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
