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
import java.util.HashSet;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class GetFromPrimaryServer extends Thread {
	

    public ArrayList<String[]> arriddata = new ArrayList<String[]>();
    public HashSet<String> newSSet = new HashSet<String>();
  //reading orignal words present at server.
    HashSet<String> orignalSSet = new HashSet<String>();
    
    @Override
    public void run()
    {
    	while(true) {
    		getWordsFromPrimaryServerAndSaveToLexicon();
    	}
    }

    
    public void readFile_CopyFun(String fileName) throws Exception{
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
//    		    System.out.println(words);
    		    orignalSSet.addAll(words);
    		}

    public void updateServerFile_CopyFun() throws Exception{
        	FileWriter file = new FileWriter("serverFile.txt");
            BufferedWriter output = new BufferedWriter(file);
            
            for(String ew: orignalSSet) {
            	output.write(ew);
            	output.write(" ");
            }
            output.close();
        }
    
    public void getWordsFromPrimaryServerAndSaveToLexicon(){
    	ArrayList<String> rawStringList = new ArrayList<String>();
    	ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
		Connection connection;
		try {
			connection = connectionFactory.createConnection();
			connection.start();
			Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue("BACKUPQUEUE");
			MessageConsumer consumer = session.createConsumer(destination);
//			boolean checkflag = true;
//			
//			while(checkflag)
//			{
				Message message = consumer.receive();
				if (message instanceof TextMessage){
					TextMessage textMessage = (TextMessage) message;					
					rawStringList.addAll(Arrays.asList(textMessage.getText().split(" ")));
					}
//				if(message == null){
//					System.out.println("Queue is empty.");
//					checkflag = false;
//				}
//			}
		connection.close();
		
		
		
		
		} catch (JMSException e) {
			e.printStackTrace();
		}
		//new set of words received in this iteration are stored in newSSet Hashset
		newSSet.addAll(rawStringList);
		
		try {
			readFile_CopyFun("serverFile.txt");
			
			//a temporary hashset to store all the orignal words to later compare if there is any new addition to lexicon
			HashSet<String> tempSet = new HashSet<String>();
			tempSet.addAll(orignalSSet);
			
			//newSSet Hashset is added in orignal Hashset so that duplicated are removed
			orignalSSet.addAll(newSSet);
			
			if(!tempSet.containsAll(orignalSSet)) {
				//this function writes everything to the the file if there is anything additional added to orignalset of words that are already present
				System.out.println("\n UPDATED LEXICON :\n "+orignalSSet);
				updateServerFile_CopyFun();
			}

					
//			System.out.println(orignalSSet);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
    
}
