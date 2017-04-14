package me.roan.maniacentering;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Simple program that can be used
 * to easily calculate the column
 * start value for which the key
 * columns are centred on the
 * screen
 * @author Roan
 */
public class Main {
	/**
	 * The text field that displays the result
	 */
	private static final JTextField val = new JTextField("ColumnStart: ");

	/**
	 * @param args - no valid command line options
	 */
	public static final void main(String[] args){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		JPanel content = new JPanel(new BorderLayout());
		//dimensions
		JSpinner width = new JSpinner(new SpinnerNumberModel(Toolkit.getDefaultToolkit().getScreenSize().width, 1, Integer.MAX_VALUE, 1));
		JSpinner height = new JSpinner(new SpinnerNumberModel(Toolkit.getDefaultToolkit().getScreenSize().height, 1, Integer.MAX_VALUE, 1));
		JPanel dim = new JPanel(new GridLayout(2, 1, 0, 2));
		dim.setBorder(BorderFactory.createTitledBorder("Screen dimensions"));
		JPanel w = new JPanel(new BorderLayout());
		JPanel h = new JPanel(new BorderLayout());
		w.add(new JLabel("Screen width: "), BorderLayout.LINE_START);
		w.add(width, BorderLayout.LINE_END);
		h.add(new JLabel("Screen height: "), BorderLayout.LINE_START);
		h.add(height, BorderLayout.LINE_END);
		dim.add(w);
		dim.add(h);
		
		//sum
		JPanel sum = new JPanel(new GridLayout(3, 1, 0, 2));
		sum.setBorder(BorderFactory.createTitledBorder("Column sum"));
		ButtonGroup group = new ButtonGroup();
		JRadioButton rsum = new JRadioButton();
		JRadioButton rcol = new JRadioButton();
		JRadioButton rcal = new JRadioButton();
		group.add(rsum);
		group.add(rcol);
		group.add(rcal);
		JPanel psum = new JPanel(new BorderLayout());
		psum.add(rsum, BorderLayout.LINE_START);
		JSpinner s = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
		psum.add(new JLabel("Sum: "), BorderLayout.CENTER);
		psum.add(s, BorderLayout.LINE_END);
		
		JPanel pcol = new JPanel(new BorderLayout());
		pcol.add(rcol, BorderLayout.LINE_START);
		pcol.add(new JLabel("Widths: "), BorderLayout.CENTER);
		JTextField values = new JTextField();
		values.setPreferredSize(new Dimension(s.getPreferredSize().width, 0));
		pcol.add(values, BorderLayout.LINE_END);
		
		JPanel pcal = new JPanel(new BorderLayout());
		pcal.add(rcal, BorderLayout.LINE_START);
		pcal.add(new JLabel("Product: "));
		JPanel product = new JPanel(new BorderLayout());
		JSpinner n1 = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
		JSpinner n2 = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
		product.add(n1, BorderLayout.LINE_START);
		JLabel x = new JLabel(" x ");
		product.add(x, BorderLayout.CENTER);
		int maxw = (int) ((n1.getPreferredSize().getWidth() - x.getPreferredSize().getWidth()) / 2.0D);
		n1.setPreferredSize(new Dimension(maxw + 1, 0));
		n2.setPreferredSize(new Dimension(maxw, 0));
		product.add(n2, BorderLayout.LINE_END);
		pcal.add(product, BorderLayout.LINE_END);
		
		sum.add(psum);
		sum.add(pcol);
		sum.add(pcal);
		
		//result
		JPanel pval = new JPanel(new BorderLayout());
		pval.setBorder(BorderFactory.createTitledBorder("Result"));
		pval.add(val);
		val.setEditable(false);
		val.setBorder(null);
		
		content.add(dim, BorderLayout.PAGE_START);
		content.add(sum, BorderLayout.CENTER);
		content.add(pval, BorderLayout.PAGE_END);
		
		Listener l = new Listener(new Runnable(){

			@Override
			public void run() {
				if(rsum.isSelected()){
					calculate((int)height.getValue(), (int)width.getValue(), (int)s.getValue());	
				}else if(rcol.isSelected()){
					try{
						String[] vals = values.getText().replaceAll(" ", "").split(",");
						int sum = 0;
						for(String val : vals){
							sum += Integer.parseInt(val);
						}
						calculate((int)height.getValue(), (int)width.getValue(), sum);	
					}catch(Exception e){
						val.setText("ColumnStart: ");
					}
				}else if(rcal.isSelected()){
					calculate((int)height.getValue(), (int)width.getValue(), (int)n1.getValue() * (int)n2.getValue());	
				}
			}
		});
		
		width.addChangeListener(l);
		height.addChangeListener(l);
		n1.addChangeListener(l);
		n2.addChangeListener(l);
		s.addChangeListener(l);
		rcol.addActionListener(l);
		rcal.addActionListener(l);
		rsum.addActionListener(l);
		values.addKeyListener(l);
		
		Icon ico = null;
		try {
			ico = new Ico();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JOptionPane.showOptionDialog(null, content, "Mania column start calculator", JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, ico, new String[]{"Exit"}, 0);
	}
	
	/**
	 * Calculates the new column start value
	 * @param screenHeight The screen height
	 * @param screenWidth The screen width
	 * @param sum The sum of all column widths
	 */
	private static final void calculate(double screenHeight, double screenWidth, double sum){
		int value = (int) (((480.0D / (screenHeight / screenWidth)) / 2.0D) - (sum / 2.0D));
		val.setText("ColumnStart: " + value);
	}
	
	/**
	 * Change listener
	 * @author Roan
	 */
	private static final class Listener implements ChangeListener, ActionListener, KeyListener{
		/**
		 * The update action
		 */
		private final Runnable action;
		
		/**
		 * Constructs a new listener
		 * @param action The update action
		 */
		private Listener(Runnable action){
			this.action = action;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			action.run();
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			action.run();
		}

		@Override
		public void keyTyped(KeyEvent e) {
			action.run();
		}

		@Override
		public void keyPressed(KeyEvent e) {
			action.run();
		}

		@Override
		public void keyReleased(KeyEvent e) {
			action.run();
		}
	}
	
	/**
	 * Program icon
	 * @author Roan
	 */
	private static final class Ico implements Icon{
		/**
		 * Icon image
		 */
		private static Image ico;
		
		/**
		 * Loads the icon
		 * @throws IOException When an IO Exception occurs
		 */
		private Ico() throws IOException{
			ico = ImageIO.read(ClassLoader.getSystemResource("ManiaCentering_logo.png"));
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.drawImage(ico, 0, 0, 80, 80, 0, 0, 1024, 1024, c);
		}

		@Override
		public int getIconWidth() {
			return 80;
		}

		@Override
		public int getIconHeight() {
			return 80;
		}
	}
}
