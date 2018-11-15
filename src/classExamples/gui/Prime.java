package classExamples.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Prime extends JFrame {
	
	private final JTextArea aTextField = new JTextArea();
	private final JButton start = new JButton("Start");
	private final JButton cancel = new JButton("Cancel");
	private volatile boolean close = false;
	private final Prime thisFrame;
	
	private void setup() {
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(start, BorderLayout.SOUTH);
		getContentPane().add(cancel, BorderLayout.EAST);
		getContentPane().add( new JScrollPane(aTextField),  BorderLayout.CENTER);
	}
		
	
	public Prime() {
		super("Prime Number Lister");
		this.thisFrame = this;
		cancel.setEnabled(false);
		aTextField.setEditable(false);
		setSize(400, 200);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setup();
		checkPrime();
		setVisible(true);
}
	
	private void time() {
		float start = System.currentTimeMillis()/1000f;
		
	}
	
	private String[] checkPrime() {
		String [] primelist;
		start.addActionListener(new ActionListener() {
			public synchronized void actionPerformed(ActionEvent e){
				String num = JOptionPane.showInputDialog("Enter a large integer");
				Integer i = null;
			}
		});
		return null;
		
	}
	
	public static void main(String[] args) {
		new Prime();
	}
	
}
