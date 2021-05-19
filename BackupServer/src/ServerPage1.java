//----------------------
//NAME: SOHAN BADADE
//ID: 1001729097
//----------------------

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JScrollPane;
import javax.swing.JLabel;

//This ServerFrame class is responsible for the Server's GUI.
//Most of the code for GUI is auto-generated.
public class ServerPage1 {

	private JFrame frame1;
	public JFrame frame;
	public JScrollPane scrollPane;
	public JTextArea infoDisplay;
	public JButton stopServer;
	public JTextArea connectedC;         

	// *****************************************
	// *****************************************
	// SERVER APPLICATION ENTRY POINT **********
	// *****************************************
	// *****************************************
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					// Sequence of actions:
					// 1. get/create the object of ServerPage1 class. -the GUI class
					// 2. get/create the object of MainServer class. -responsible for listening for connections.
					// 3. pass the textfields to functions in Server class, through MainServer class.
						// 3.1 MainServer class is merely a message passer to the incase of "infoDisplay" and "connectedC".
					// 4. Start() on ServerPage1 object triggers the run() method of that class.
					// 5. Set the frame to visible.
					
					ServerPage1 server_frame_obj = new ServerPage1();
					MainServer main_server = new MainServer(server_frame_obj.infoDisplay,server_frame_obj.connectedC);
					main_server.start();
					server_frame_obj.frame1.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ServerPage1() {
		initialize();
	}
	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame1 = new JFrame();
		frame1.setTitle("Spell Check System: BACKUP SERVER !!!!!!! ");
		frame1.setBounds(300, 90, 900, 600);
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame1.getContentPane().setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 36, 529, 441);
		frame1.getContentPane().add(scrollPane);
		
		infoDisplay = new JTextArea();
		scrollPane.setViewportView(infoDisplay);
		infoDisplay.setEditable(false);
		infoDisplay = new JTextArea();
		
		infoDisplay.setFont(new Font("Monospaced", Font.BOLD, 20));
		scrollPane.setViewportView(infoDisplay);
		infoDisplay.setText(" Server Started.");
		infoDisplay.setText(infoDisplay.getText() + "\n Server running.");
		infoDisplay.setEditable(false);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(584, 36, 269, 441);
		frame1.getContentPane().add(scrollPane_1);
		
		connectedC = new JTextArea();
		connectedC.setFont(new Font("Monospaced", Font.BOLD, 20));
		connectedC.setEditable(false);
		scrollPane_1.setViewportView(connectedC);
		
		JButton button = new JButton("STOP SERVER");
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
		});
		button.setForeground(Color.BLACK);
		button.setFont(new Font("Courier New", Font.BOLD, 22));
		button.setBackground(Color.WHITE);
		button.setBounds(309, 503, 263, 37);
		frame1.getContentPane().add(button);
		
		JLabel lblCoonnectedClients = new JLabel("CONNECTED CLIENTS");
		lblCoonnectedClients.setFont(new Font("Tahoma", Font.BOLD, 17));
		lblCoonnectedClients.setBounds(612, 0, 241, 44);
		frame1.getContentPane().add(lblCoonnectedClients);
		
		JLabel lblLogs = new JLabel("LOGS");
		lblLogs.setFont(new Font("Tahoma", Font.BOLD, 17));
		lblLogs.setBounds(186, 16, 56, 16);
		frame1.getContentPane().add(lblLogs);
	}
}

//-------------------------------------------------------------------------------------
//REFERENCES - Articles/blogs/videos that I referred to for merely checking syntax 
//or figuring out how something works.
//-------------------------------------------------------------------------------------
//https://www.youtube.com/watch?v=Kmgo00avvEw&t=1854s
//https://www.albany.edu/faculty/jmower/geog/gog692/ImportExportJARFiles.htm
//https://www.programiz.com/java-programming/bufferedwriter
//https://www.codejava.net/java-se/swing/file-picker-component-in-swing
//http://www.java2s.com/Tutorial/Java/0240__Swing/Catalog0240__Swing.htm
