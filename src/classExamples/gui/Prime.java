package classExamples.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


public class Prime extends JFrame {
	
	private final JTextArea aTextField = new JTextArea();
	private final JButton start = new JButton("Start");
	private final JButton cancel = new JButton("Cancel");
	private volatile boolean close = false;
	private final Prime thisFrame;
	private final int processors = Runtime.getRuntime().availableProcessors();
	private static List<Integer> primeList = Collections.synchronizedList(new ArrayList<Integer>());

	public static void main(String[] args) {
		Prime prime = new Prime("Prime Number Lister");
		prime.addActionListeners();
		prime.setVisible(true);
	}
	
	private Prime(String title) {
		super(title);
		this.thisFrame = this;
		cancel.setEnabled(false);
		aTextField.setEditable(false);
		setSize(400, 200);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(start, BorderLayout.SOUTH);
		getContentPane().add(cancel, BorderLayout.EAST);
		getContentPane().add( new JScrollPane(aTextField),  BorderLayout.CENTER);
	}	
	
	private class CancelOption implements ActionListener{
		public void actionPerformed(ActionEvent arg0){
			close = true;
		}
	}
	
	private void addActionListeners(){
		cancel.addActionListener(new CancelOption());
	
		start.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					
					String num = JOptionPane.showInputDialog("Enter a large integer");
					Integer max =null;
					
					try{
						max = Integer.parseInt(num);
					}
					catch(Exception ex){
						JOptionPane.showMessageDialog(
								thisFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						ex.printStackTrace();
					}
					
					if( max != null){
						aTextField.setText("");
						start.setEnabled(false);
						cancel.setEnabled(true);
						close = false;
						new Thread(new UserInput(max)).start();
					}
				}});
		}
	
	private boolean isPrime( int i){
		for( int x=2; x < i -1; x++)
			if( i % x == 0  )
				return false;
		
		return true;
	}
	private class UserInput implements Runnable{
		private final int max;
		private final long startTime;
		
		private UserInput(int num){
			this.max = num;
			this.startTime = System.currentTimeMillis();
		}
		
		public void run(){
			long lastUpdate = System.currentTimeMillis();
			List<Integer> list = new ArrayList<Integer>();
			for (int i = 1; i < max && ! close; i++) {
				if( isPrime(i)){
					list.add(i);
						
					if( System.currentTimeMillis() - lastUpdate > 500){
						float time = (System.currentTimeMillis() -startTime )/1000f;
						final String outString= "Found " + list.size() + " in " + i + " of " + max + " " 
									+ time + " seconds ";
						
						SwingUtilities.invokeLater( new Runnable(){
							@Override
							public void run(){
								aTextField.setText(outString);
							}
						});
						
						lastUpdate = System.currentTimeMillis();	
					}
				}
			}
			
			final StringBuffer buff = new StringBuffer();
			
			for( Integer i2 : list)
				buff.append(i2 + "\n");
			
			if( close)
				buff.append("cancelled\n");
			
			float time = (System.currentTimeMillis() - startTime )/1000f;
			buff.append("Time = " + time + " seconds " );
			
			SwingUtilities.invokeLater( new Runnable(){
				@Override
				public void run(){
					
					close = false;
					start.setEnabled(true);
					cancel.setEnabled(false);
					aTextField.setText( (close ? "cancelled " : "") +  buff.toString());
					
				}
			});
		}
	}
	
	private class Processing implements Runnable{
		 final int max;
		 private Semaphore sema = new Semaphore(processors);
		 private Processing(int max) {
			 this.max = max;
		 }
		 public void run() {
			 for(int i = 0; i < processors; i++) {
				 try {
					 sema.acquire();
					 }
				 catch (InterruptedException exception) {
					 exception.printStackTrace();
				 }
			 }
			 
		 }
		
	}
}
