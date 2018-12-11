package classExamples.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

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
	private static List <Integer> primenum = Collections.synchronizedList(new ArrayList<Integer>());
	private static final int processors = Runtime.getRuntime().availableProcessors();
	private static long starttime;
	
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
						new Thread(new Processing(max)).start();
					}
				}});
		}
	
	private class FindPrime implements Runnable{
		private final int begin;
		private final int max;
		private final Semaphore sem;
		
		private FindPrime(int begin, int max, Semaphore s) {
			this.begin = begin;
			this.max = max;
			this.sem = s;
		}
		
		public void run() {
			long last = System.currentTimeMillis();
			for(int x = begin; x < max && !close; x = x + processors) {
				if(isPrime(x)) {
					primenum.add(x);
					if(System.currentTimeMillis() - last > 500) {
						float time = (System.currentTimeMillis() - starttime) /1000f;
						final String output = "Found" + primenum.size() + "prime numbers in" + x + "seconds.";
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								aTextField.setText(output);
							}
						});
						last = System.currentTimeMillis();
					}
				}
			}
			sem.release();
		}
		
	}
	
	private void updateInput() {
		final StringBuffer string = new StringBuffer();
		synchronized(primenum) {
			Collections.sort(primenum);
			for(Integer z : primenum) {
				string.append(z + "\n");
			}
		}
		if(close) {
			string.append("Closed\n");
		}
		float time = (System.currentTimeMillis() - starttime) / 1000f;
		string.append("Time: " + time + "seconds.");
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				close = false;
				start.setEnabled(true);
				cancel.setEnabled(true);
				aTextField.setText((close ? "Closed" : "")+string.toString());
			}
		});
	}
	
	private boolean isPrime(int i){
		for( int x=2; x < i -1; x++)
			if( i % x == 0  )
				return false;
		
		return true;
	}
	
	private class Processing implements Runnable{
		 private final int max;
		 private Semaphore sema = new Semaphore(processors);
		 private Processing(int max) {
			 this.max = max;
		 }
		 public void run() {
			 starttime = System.currentTimeMillis();
			 for(int i = 0; i < processors; i ++) {
				 try {
					 sema.acquire();
				 }
				 catch(InterruptedException exception) {
					 exception.printStackTrace();
				 }
			 FindPrime findPrime = new FindPrime(i+1, max, sema);
			 new Thread(findPrime).start();
		 }
		 for(int j = 0; j < processors; j++) {
			 try {
				 sema.acquire();
			 }
			 catch(InterruptedException exception) {
				 exception.printStackTrace();
			 }
		 }
		 updateInput();
		
	}

}
}

