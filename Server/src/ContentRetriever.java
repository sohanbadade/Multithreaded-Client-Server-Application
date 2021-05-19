//----------------------
//NAME: SOHAN BADADE
//ID: 1001729097
//----------------------
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class ContentRetriever extends TimerTask{

//using hashset so automatically removes the duplicates.
//reading new words from client.
public HashSet<String> newStringSet = new HashSet<String>();
//reading orignal words present at server.
public HashSet<String> orignalStringSet = new HashSet<String>();

public int initialCount = 0;

//setting up the localhost
private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
//defining the queue name
private static String subject = "SPELLCHECKSYSTEM";
//stores the username and data received to give back the response.
public ArrayList<String[]> arriddata = new ArrayList<String[]>();

@Override
	public void run(){
	
	//this takes care of the first time when backup server starts, to send the already present contents.
	if(initialCount ==0) {
		try {
			
			this.readFile("serverFile.txt");
			this.SendtoBackupServer();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		initialCount +=1;
	}
	
	
	//this takes all the words from the queue and stores inside newStringSet
	this.getWordsOutOfTexts();
	
	try {
		//reads all the words currently present on the server
		this.readFile("serverFile.txt");
	} catch (Exception e) {
		e.printStackTrace();
	}
	
	//a temporary hashset to store all the orignal words to later compare if there is any new addition to lexicon
	HashSet<String> temprSet = new HashSet<String>();
	temprSet.addAll(orignalStringSet);
	
	//compares all the words earlier present and adds the new one. did this using hashset so it removes all the duplicates
	orignalStringSet.addAll(newStringSet);
	
	try {
		if(!temprSet.containsAll(orignalStringSet)) {
			
			//updates the file on server
			this.updateServerFile();
			
			//send to another queue where backup server is listening
			this.SendtoBackupServer();
			
		}
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	//in a for loop, send response to all the clients.
	for (Iterator<String[]> it = arriddata.iterator(); it.hasNext();) {
		String[] array = it.next();
		System.out.println(array[0]);
		System.out.println(array[1]);
		this.sendClientAck(array[0],array[1]);
	    it.remove();
	}
	
}



//send ack to client that data was recieved
//https://javainsider.wordpress.com/tag/jms-with-activemq-sample-example/
	public void sendClientAck(String corrId, String data) {
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection;
		try {
			connection = connectionFactory.createConnection();
			connection.start();
			Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(corrId);
			MessageProducer producer = session.createProducer(destination);
			TextMessage message = session.createTextMessage(data);
			producer.send(message);
			connection.close();
		} catch (JMSException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

//this function is responsible for getting the words from queue and removing the duplicates.
//https://javainsider.wordpress.com/tag/jms-with-activemq-sample-example/
	public void getWordsOutOfTexts(){
			ArrayList<String> rawStringList = new ArrayList<String>();
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
			Connection connection;
			try {
				connection = connectionFactory.createConnection();
				connection.start();
				Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
				Destination destination = session.createQueue(subject);
				MessageConsumer consumer = session.createConsumer(destination);
				boolean checkflag = true;
				
				while(checkflag)
				{
					Message message = consumer.receive(3000);
					if (message instanceof TextMessage){
						TextMessage textMessage = (TextMessage) message;
						String user = message.getJMSCorrelationID();
						String data = textMessage.getText();
						String val[] = {user, data};
						arriddata.add(val);
						
						rawStringList.addAll(Arrays.asList(textMessage.getText().split(" ")));
						}
					if(message == null){
//						System.out.println("Queue is empty.");
						checkflag = false;
					}
				}
			connection.close();
			} catch (JMSException e) {
				e.printStackTrace();
			}
			newStringSet.addAll(rawStringList);
		}

	public void readFile(String fileName) throws Exception{
	BufferedReader br = new BufferedReader(new FileReader(fileName));
		    String line = br.readLine();
		    ArrayList<String> words = new ArrayList<String>();
		    while(line != null) {
		    	String[] temp = line.split(" ");
		    	for(String t: temp) {
		    		words.add(t);
		    	}
		    	line = br.readLine();
		    }
		    br.close();
//		    System.out.println(words);
		    orignalStringSet.addAll(words);
		}

	public void updateServerFile() throws Exception{
    	FileWriter file = new FileWriter("serverFile.txt");
        BufferedWriter output = new BufferedWriter(file);
        
        for(String ew: orignalStringSet) {
        	output.write(ew);
        	output.write(" ");
        }
        output.close();
    }
	
	public void SendtoBackupServer() {
		
			String wordss="";
			for(String each : orignalStringSet) {
				wordss+=each+" ";
			}
				
			//setting up connection to localhost
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
			Connection connection;
			try {
				connection = connectionFactory.createConnection();
				connection.start();
				Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
				Destination destination = session.createQueue("BACKUPQUEUE");
				MessageProducer producer = session.createProducer(destination);
				TextMessage message = session.createTextMessage(wordss);
				
				producer.send(message);
				
//				System.out.println("Sent to backup Server: " + message.getText());
//				connection.close();
			} catch (JMSException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}

}


//-------------------------------------------------------------------------------------
//REFERENCES - Articles/blogs/videos that I referred to for merely checking syntax 
//or figuring out how something works.
//-------------------------------------------------------------------------------------
//https://javainsider.wordpress.com/tag/jms-with-activemq-sample-example/
//https://www.baeldung.com/java-timer-and-timertask
//https://www.youtube.com/watch?v=Kmgo00avvEw&t=1854s
//https://www.albany.edu/faculty/jmower/geog/gog692/ImportExportJARFiles.htm
//https://www.programiz.com/java-programming/bufferedwriter
//https://www.codejava.net/java-se/swing/file-picker-component-in-swing
//http://www.java2s.com/Tutorial/Java/0240__Swing/Catalog0240__Swing.htm
