//----------------------
//NAME: SOHAN BADADE
//ID: 1001729097
//----------------------

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.UnknownHostException;

// This ClientPage1 class is responsible for the first page of the client.
// It has the function to authenticate the client after he/she enters the name in text box.
public class ClientPage1 {
	public JFrame frame;
	public JTextField userNametext;
	public Client cli;
	public JLabel notifyLabel;
	public static int portNumberOfSecondaryServer = 54321;
	public static int portNumberOfPrimaryServer = 12345;

	// Launch point of Client-side.
	// Using the SWING GUI BUILDER - drag and drop feature of Eclipse, following code 
	// was generated.
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			
			public void run() {
				try {
					//this starts the new instance of GUI. and set its visibility.
					
					new ClientPage1(new Client(portNumberOfPrimaryServer)).frame.setVisible(true);
					
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Constructor
	public ClientPage1(Client cli) {
		this.cli = cli;
		initialize();
	}

	
	//Initializing the contents of the frame.
	private void initialize() {
		//MAIN FRAME OF THE ClientPage1 CLASS
		frame = new JFrame();
		frame.setTitle("SPELL CHECK SYSTEM: login"); 
		frame.setBounds(300, 90, 900, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		//Label
		JLabel userNameLabel = new JLabel("ENTER USERNAME:");
		userNameLabel.setFont(new Font("Tahoma", Font.PLAIN, 21));
		userNameLabel.setBounds(314, 13, 191, 54);
		frame.getContentPane().add(userNameLabel);
		
		//Text field
		userNametext = new JTextField();
		userNametext.setFont(new Font("Tahoma", Font.PLAIN, 30));
		userNametext.setBounds(30, 80, 819, 74);
		frame.getContentPane().add(userNametext);
		userNametext.setColumns(10);
		
		//Button
		JButton connectButton = new JButton("CONNECT");
		connectButton.setFont(new Font("Tahoma", Font.PLAIN, 17));
		connectButton.setBounds(294, 219, 220, 44);
		//Event on the button.
		connectButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				boolean valid = (userNametext.getText() != null) && userNametext.getText().matches("[A-Za-z0-9_]+");
				if(!valid) {
					notifyLabel.setText("Username invalid.");
					return;
				}
				String value = cli.authenticate(userNametext.getText());
				if(value.equals("ok")) {
				//Client c = cli; // no more using this, since disposing frame doesn't 
								  // dispose other attributes of the class. 
				frame.dispose();
				frame.setVisible(false);
				
				//Creating a new HOMEFRAME object and starting it's GUI page
				HomeFrame hfobj = new HomeFrame(cli);
				hfobj.frame.setVisible(true);
				
				//This thread keeps listening from the servers if they recieved the lexicon the clinet just sent.
				new ResponseListener(cli,hfobj.notificationsTextField).start();
				
				
				//this is thread that keeps running in the background to see if server is active or not
				try {
					new PingServerPort(cli,hfobj).start();
				} catch (Exception e1) {
					
				}
				
				System.out.println("Success for "+userNametext.getText());
				}
				if(value.equals("notok")) {
					notifyLabel.setText("Username already taken, please choose different.");
					
				}
				if(value.equals("noton")) {
					JOptionPane.showMessageDialog(null, "Server is not currently on.");
					
					//resets the client so that stale client won't keep giving same messages.
					cli = new Client(portNumberOfSecondaryServer);
					
					//need to figure-out how to move from second-click to first click when it shows the message.
				}
			}
		});
		frame.getContentPane().add(connectButton);
		
		//EXIT button.
		JButton exitbutton = new JButton("EXIT");
		exitbutton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
		});
		exitbutton.setFont(new Font("Tahoma", Font.PLAIN, 17));
		exitbutton.setBounds(294, 314, 220, 44);
		frame.getContentPane().add(exitbutton);
		
		//Label to show output
		this.notifyLabel = new JLabel();
		notifyLabel.setForeground(new Color(255, 0, 0));
		notifyLabel.setFont(new Font("Tahoma", Font.PLAIN, 30));
		notifyLabel.setBounds(46, 474, 804, 44);
		frame.getContentPane().add(notifyLabel);
	}
}

//-------------------------------------------------------------------------------------
//REFERENCES - Articles/blogs/videos that I referred to for merely checking syntax 
// or figuring out how something works.
//-------------------------------------------------------------------------------------
// https://www.youtube.com/watch?v=Kmgo00avvEw&t=1854s
// https://www.albany.edu/faculty/jmower/geog/gog692/ImportExportJARFiles.htm
// https://www.programiz.com/java-programming/bufferedwriter
// https://www.codejava.net/java-se/swing/file-picker-component-in-swing
// http://www.java2s.com/Tutorial/Java/0240__Swing/Catalog0240__Swing.htm

