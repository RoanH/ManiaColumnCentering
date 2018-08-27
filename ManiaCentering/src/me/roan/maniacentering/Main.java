package me.roan.maniacentering;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

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
		JPanel sum = new JPanel(new GridLayout(4, 1, 0, 2));
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
		
		JButton file = new JButton("Directly apply to file");
		file.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileNameExtensionFilter("INI files", "ini"));
				chooser.setMultiSelectionEnabled(false);
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					try {
						parse(chooser.getSelectedFile(), (int)height.getValue(), (int)width.getValue());
						JOptionPane.showMessageDialog(null, "ColumnStart values succesfully added!", "Mania Centering", JOptionPane.INFORMATION_MESSAGE);
					} catch (IOException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, "An error occured!", "Mania Centering", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		
		sum.add(psum);
		sum.add(pcol);
		sum.add(pcal);
		sum.add(file);
		
		content.add(dim, BorderLayout.PAGE_START);
		content.add(sum, BorderLayout.CENTER);
		
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
		
		JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
		JLabel ver = new JLabel("<html><center><i>Version: v1.2, latest version: <font color=gray>loading</font></i></center></html>", SwingConstants.CENTER);
		info.add(ver);
		new Thread(()->{
			String version = checkVersion();//XXX the version number 
			ver.setText("<html><center><i>Version: v1.2, latest version: " + (version == null ? "unknown :(" : version) + "</i></center></html>");
		}, "Version Checker").start();
		JPanel links = new JPanel(new GridLayout(1, 2, -2, 0));
		JLabel forum = new JLabel("<html><font color=blue><u>Forums</u></font> -</html>", SwingConstants.RIGHT);
		JLabel git = new JLabel("<html>- <font color=blue><u>GitHub</u></font></html>", SwingConstants.LEFT);
		links.add(forum);
		links.add(git);
		forum.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				if(Desktop.isDesktopSupported()){
					try {
						Desktop.getDesktop().browse(new URL("https://osu.ppy.sh/community/forums/topics/581972").toURI());
					} catch (IOException | URISyntaxException e1) {
						//pity
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		git.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				if(Desktop.isDesktopSupported()){
					try {
						Desktop.getDesktop().browse(new URL("https://github.com/RoanH/ManiaColumnCentering").toURI());
					} catch (IOException | URISyntaxException e1) {
						//pity
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		info.add(links);
		
		//result
		JPanel pval = new JPanel(new BorderLayout());
		pval.setBorder(BorderFactory.createTitledBorder("Result"));
		pval.add(val);
		val.setEditable(false);
		val.setBorder(null);
		
		JPanel end = new JPanel(new BorderLayout());
		content.add(end, BorderLayout.PAGE_END);
		end.add(pval, BorderLayout.PAGE_START);
		end.add(info, BorderLayout.PAGE_END);
		
		JOptionPane.showOptionDialog(null, content, "osu!mania ColumnStart calculator", JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"Exit"}, 0);
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
	 * Parses a file and applies the column start value
	 * @param ini The file to modify
	 * @param screenHeight The screen height
	 * @param screenWidth The screen width
	 * @throws IOException When an IOException occurs
	 */
	private static final void parse(File ini, double screenHeight, double screenWidth) throws IOException{
		File tmp = Files.createTempFile(null, null).toFile();
		PrintWriter out = new PrintWriter(new FileOutputStream(tmp));
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(ini)));
		
		String line;
		while((line = in.readLine()) != null){
			if(line.startsWith("ColumnStart:")){
				out.println("//" + line);
			}else if(line.startsWith("ColumnWidth:")){
				try{
					String[] vals = line.replaceAll("ColumnWidth:", "").replaceAll(" ", "").split(",");
					int sum = 0;
					for(String val : vals){
						sum += Integer.parseInt(val);
					}
					out.println(line);
					out.println("ColumnStart: " + (int) (((480.0D / (screenHeight / screenWidth)) / 2.0D) - (sum / 2.0D)));
				}catch(Exception e){
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "An error occured!", "Mania Centering", JOptionPane.ERROR_MESSAGE);
					out.close();
					in.close();
					tmp.delete();
					tmp.deleteOnExit();
					return;
				}
			}else{
				out.println(line);
			}
		}
		
		out.flush();
		out.close();
		in.close();
		
		Files.move(tmp.toPath(), ini.toPath(), StandardCopyOption.REPLACE_EXISTING);
		
		tmp.delete();
		tmp.deleteOnExit();
	}
	
	/**
	 * Checks the KeysPerSecond version to see
	 * if we are running the latest version
	 * @return The latest version
	 */
	private static final String checkVersion(){
		try{ 			
			HttpURLConnection con = (HttpURLConnection) new URL("https://api.github.com/repos/RoanH/ManiaColumnCentering/tags").openConnection(); 			
			con.setRequestMethod("GET"); 		
			con.setConnectTimeout(10000); 					   
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream())); 	
			String line = reader.readLine(); 		
			reader.close(); 	
			String[] versions = line.split("\"name\":\"v");
			int max_main = 1;
			int max_sub = 0;
			String[] tmp;
			for(int i = 1; i < versions.length; i++){
				tmp = versions[i].split("\",\"")[0].split("\\.");
				if(Integer.parseInt(tmp[0]) > max_main){
					max_main = Integer.parseInt(tmp[0]);
					max_sub = Integer.parseInt(tmp[1]);
				}else if(Integer.parseInt(tmp[0]) < max_main){
					continue;
				}else{
					if(Integer.parseInt(tmp[1]) > max_sub){
						max_sub = Integer.parseInt(tmp[1]);
					}
				}
			}
			return "v" + max_main + "." + max_sub;
		}catch(Exception e){ 	
			return null;
			//No Internet access or something else is wrong,
			//No problem though since this isn't a critical function
		}
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
}
