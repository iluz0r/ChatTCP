package Client;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import Client.GUI.SendTextKeyListener;

import javax.swing.JButton;

public class PrivateMessage {

	private JFrame frame;
	private JTextField textField;
	private JTextArea textArea;
	private JButton btnNewButton;
	
	private String from;
	private String to;	
	private PrintWriter pw;
	
	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PrivateMessage window = new PrivateMessage();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	public JTextArea getTextArea() {
		return textArea;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public void setTextArea(String from, String message) {
		textArea.append(from+": "+message+"\n");
	}

	public JButton getBtnNewButton() {
		return btnNewButton;
	}

	public void setBtnNewButton(JButton btnNewButton) {
		this.btnNewButton = btnNewButton;
	}

	/**
	 * Create the application.
	 */
	 
	public PrivateMessage(String from, String to, PrintWriter pw) {		
		this.pw=pw;
		this.from=from;
		this.to=to;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle(from + " >> "+to);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(2, 4, 428, 214);
		frame.getContentPane().add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		textArea = new JTextArea();
		textArea.setRows(12);
		panel.add(textArea);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(0, 222, 432, 25);
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(12, 0, 314, 25);
		panel_1.add(textField);
		textField.setColumns(28);
		textField.addKeyListener(new SendTextKeyListener());
		
		btnNewButton = new JButton("Invia");
		btnNewButton.setBounds(330, 0, 97, 25);
		panel_1.add(btnNewButton);
		
		frame.setVisible(true);
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public String getTextField() {
		return textField.getText();
	}

	public void setTextField() {
		textField.setText("");
	}
	
	class SendTextKeyListener extends KeyAdapter {

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					
					System.out.println(getTextField());
					pw.println("PRIVATETO:"+to+":"+from+":"+textField.getText());					
					pw.flush();
					setTextArea(from,textField.getText());
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

