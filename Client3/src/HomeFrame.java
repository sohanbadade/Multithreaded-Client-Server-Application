//----------------------
//NAME: SOHAN BADADE
//ID: 1001729097
//----------------------

import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.swing.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import java.awt.ScrollPane;

//@SuppressWarnings("serial")
@SuppressWarnings("serial")
public class HomeFrame 
			extends JFrame 
			implements ActionListener {
	//these are the UI components
	public JFrame frame;
	public JLabel label,fndisplay;
	private JButton upload;
	private JButton exit;
	public Client cli;
	public Container c;
	private JLabel label_1;
	private JTextField lexTextField;
	private JButton sendToQueuebutton;
	public JTextArea notificationsTextField;
	public JScrollPane scrollPane;
	private JSeparator separator;
	private JLabel lblWordsInQueue;
	private JLabel lblspaceSeperated;
	public static int portNumberOfSecondaryServer = 54321;
	public static int portNumberOfPrimaryServer = 12345;
	public static boolean ClientProcessed = false;
	
	// DEFAULT_BROKER_URL provides the the url of localhost:61616 where activeMQ broker is running
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	// name of the queue where server will read from.
	private static String subject = "SPELLCHECKSYSTEM";
	
	
	//constructor - also initializes all the components of the frame.
	HomeFrame(Client cli){
		this.cli = cli;
		String titleStr = "SPELL CHECK SYSTEM: home | USER: "+cli.selfName;
		frame = new JFrame();
		label = new JLabel();
		frame.setTitle(titleStr); 
		frame.setBounds(300, 90, 913, 610);
		frame.getContentPane().setLayout(null);
		
		label = new JLabel("Upload File to the Server:"); 
		label.setFont(new Font("Arial", Font.PLAIN, 23)); 
		label.setSize(272, 76); 
		label.setLocation(31, 29); 
		frame.getContentPane().add(label);
		
		fndisplay = new JLabel(); 
		fndisplay.setForeground(new Color(255, 0, 0));
		fndisplay.setFont(new Font("Tahoma", Font.PLAIN, 30)); 
		fndisplay.setBounds(55, 475, 585, 44);
		
		fndisplay.setLocation(52, 475); 
		frame.getContentPane().add(fndisplay);
		
		
		upload = new JButton("UPLOAD");
		upload.setFont(new Font("Arial", Font.PLAIN, 15)); 
		upload.setSize(173, 52); 
		upload.setLocation(70, 106); 
		upload.addActionListener(this); 
		frame.getContentPane().add(upload);
		
		exit = new JButton("EXIT");
		exit.setFont(new Font("Arial", Font.PLAIN, 15)); 
		exit.setSize(118, 76); 
		exit.setLocation(752, 466); 
		exit.addActionListener(this); 
		frame.getContentPane().add(exit);
		
		label_1 = new JLabel("Add to Server's Lexicon :");
		label_1.setFont(new Font("Arial", Font.BOLD, 23));
		label_1.setBounds(422, 13, 336, 52);
		frame.getContentPane().add(label_1);
		
		lexTextField = new JTextField();
		lexTextField.setColumns(10);
		lexTextField.setBounds(332, 89, 538, 53);
		frame.getContentPane().add(lexTextField);
		
		sendToQueuebutton = new JButton("ADD TO QUEUE");
		
		// Following function is responsible for sending data to ActiveMQ brokers where server will read it.
		sendToQueuebutton.addActionListener(new ActionListener() {
			//REFERENCE- https://javainsider.wordpress.com/tag/jms-with-activemq-sample-example/
			public void actionPerformed(ActionEvent e) {
				boolean notvalid = lexTextField.getText().length() < 1;
				if(!notvalid) {
					String words = lexTextField.getText();
					//setting up connection to localhost
					ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
					Connection connection;
					try {
						connection = connectionFactory.createConnection();
						connection.start();
						Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
						Destination destination = session.createQueue(subject);
						MessageProducer producer = session.createProducer(destination);
						TextMessage message = session.createTextMessage(words);
						message.setJMSCorrelationID(cli.selfName);
						producer.send(message);
						notificationsTextField.setText(notificationsTextField.getText()+"Data added to Queue => " + message.getText());
						notificationsTextField.setText(notificationsTextField.getText()+"\n-------------------\n");
						System.out.println("Sent message: " + message.getText());
						connection.close();
					} catch (JMSException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else {
					fndisplay.setText("Please add words. Textfield empty.");
					return;
				}
			}
		});
		//following are the methods for initializing the UI
		sendToQueuebutton.setFont(new Font("Tahoma", Font.PLAIN, 21));
		sendToQueuebutton.setBounds(486, 155, 214, 64);
		frame.getContentPane().add(sendToQueuebutton);
		
		separator = new JSeparator();
		separator.setBounds(12, 244, 858, 2);
		frame.getContentPane().add(separator);
		
		lblWordsInQueue = new JLabel("Notifications - ");
		lblWordsInQueue.setFont(new Font("Arial", Font.PLAIN, 23));
		lblWordsInQueue.setBounds(346, 259, 253, 28);
		frame.getContentPane().add(lblWordsInQueue);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(31, 300, 839, 157);
		frame.getContentPane().add(scrollPane);
		
		notificationsTextField = new JTextArea();
		notificationsTextField.setFont(new Font("Monospaced", Font.BOLD, 19));
		scrollPane.setViewportView(notificationsTextField);
		notificationsTextField.setEditable(false);
		
		lblspaceSeperated = new JLabel("(Space Seperated)");
		lblspaceSeperated.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblspaceSeperated.setBounds(505, 54, 142, 28);
		frame.getContentPane().add(lblspaceSeperated);

		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String filename = null;
		if(e.getSource() == upload) {
			try {
				
//this is earlier logic which im not using due to lack of it's scope. it woorks but not as it should.				
//this basically checks if Server is Up and Running before Uploading the file.				
//				if(!cli.ConnectionCheck()) {
//					
//					frame.dispose();
//					frame.setVisible(false);
//					Client newClientConnection = new Client(portNumberOfSecondaryServer);
//					newClientConnection.authenticate(cli.selfName);
//					HomeFrame hfobj = new HomeFrame(newClientConnection);
//					hfobj.frame.setVisible(true);
//					
//					new ResponseListener(newClientConnection,hfobj.notificationsTextField).start();
//					
//				}
//				else {
					
					JFileChooser fd=new JFileChooser();
		             fd.showOpenDialog(fd);
		             filename=fd.getSelectedFile().getAbsolutePath();
//		             fndisplay.setText("File Status: File Sent. Server is processing.");
					
//				}
			}
			catch(Exception E) {
				System.out.println("File not selected.");
				return;
			}
			 
			int successVariable=1;
             
             //the OG Client function resonsible for both sending file and receiving the result.
            try {
    			cli.runClient(filename);
            }catch (Exception specialExe) {
           	
			}
			

             
             if(successVariable == 1) {
            	 
                 frame.dispose();
                 frame.setVisible(false);
                 
                 System.out.println("IM HERE HELLO.  SERVER HAS PROCESSED.");
                 
                 // starting the fresh session for the new client.
                 new ClientPage1(new Client(12345)).frame.setVisible(true);

    //REFERENCE- https://stackoverflow.com/questions/17979438/how-to-perform-action-on-ok-of-joptionpane-showmessagedialog
                 int input = JOptionPane.showOptionDialog(null, 
                		 							"Click OK to check file:", 
                		 							"File Downloaded", 
                		 							JOptionPane.OK_CANCEL_OPTION, 
                		 							JOptionPane.INFORMATION_MESSAGE, 
                		 							null,null,null);
                 
                 
                 
                 // a VERY IMPORTANT thing to do to indicate the probe thread if it should connect to backup server.
                 HomeFrame.ClientProcessed = true;
                 
                 //if ok, show the newly edited file
                 //NOTE: orignal file is going to be replced by new one!!!
                 if(input == JOptionPane.OK_OPTION)
                 {
                	 try {
    					Desktop.getDesktop().open(new File(filename));
    				} catch (IOException e1) {
    					e1.printStackTrace();
    				}
                 }
            	 
             }
             
             
	}
		// if exit button pressed, close the JVM.
		if(e.getSource() == exit) {
			System.exit(0);
		}
		
		
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

