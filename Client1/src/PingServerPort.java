//----------------------
//NAME: SOHAN BADADE
//ID: 1001729097
//----------------------

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class PingServerPort extends Thread {

	int portNumberOfPrimaryServer = 12345;
	Client probeClient = new Client(portNumberOfPrimaryServer);
	Client baseClient;
	HomeFrame hfobj;
	int portNumberOfSecondaryServer = 54321;
	
	public PingServerPort(Client baseClient,HomeFrame hfobj){
		super();
		this.baseClient = baseClient;
		this.hfobj = hfobj;
	}
	
	
	//REFERENCE- https://javainsider.wordpress.com/tag/jms-with-activemq-sample-example/
	
	@Override
	public void run()
	{
		//REFERENCE- https://javainsider.wordpress.com/tag/jms-with-activemq-sample-example/
		String baseClientName = baseClient.selfName;
		probeClient.authenticate("probe_"+baseClientName);
		int dontRunAgain=0;
		while(dontRunAgain == 0) {
			try {
				ConnectionCheck();
			} catch (Exception e) {
				if(!HomeFrame.ClientProcessed) {
				dontRunAgain =1;
				System.out.println("HELLLO GUYS INSIDE SPECIAL EXECEPTON");
            	System.out.println("The server is failed or is shutdown");
            	String strToAppend = hfobj.notificationsTextField.getText();
            	
            	hfobj.frame.dispose();
            	hfobj.frame.setVisible(false);
				Client newClientConnection = new Client(portNumberOfSecondaryServer);
				newClientConnection.authenticate(baseClient.selfName);
				HomeFrame hfobj1 = new HomeFrame(newClientConnection);
				hfobj1.frame.setVisible(true);
				
				hfobj1.notificationsTextField.setText( strToAppend );
				
				hfobj1.notificationsTextField.setText(hfobj1.notificationsTextField.getText()+"\n SERVER WAS DOWN. CONNECTED TO BACKUP SEVER. PLEASE CONTINUE.\n ------------------------ \n");
				
				new ResponseListener(newClientConnection,hfobj1.notificationsTextField).start();
				
				}
				
			}			
		}

	}
	
	//background function to keep checking if primary server is active
	public void ConnectionCheck()
            throws UnknownHostException, IOException
		{
        
		 this.probeClient.dataOutputStream.writeUTF("True");
		 
        this.probeClient.dataInputStream.readUTF();
		
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


