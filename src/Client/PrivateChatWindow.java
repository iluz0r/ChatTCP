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

	private String from;
	private String to;
	private PrintWriter pw;

	public JTextArea getTextArea() {
		return chatTextArea;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public void setTextArea(String from, String message) {
		chatTextArea.append(from + ": " + message + "\n");
	}

	public PrivateChatWindow(String from, String to, PrintWriter pw) {
		this.pw = pw;
		this.from = from;
		this.to = to;
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setTitle(from + " >> " + to);
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

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public String getTextField() {
		return messageTextField.getText();
	}

	public void setTextField() {
		messageTextField.setText("");
	}

	class SendTextKeyListener extends KeyAdapter {

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				System.out.println(getTextField());
				pw.println("PRIVATETO:" + to + ":" + from + ":" + messageTextField.getText());
				pw.flush();
				setTextArea(from, messageTextField.getText());
				setTextField();
			}
		}

	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

}
