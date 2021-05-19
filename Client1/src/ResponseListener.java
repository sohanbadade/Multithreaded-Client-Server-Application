//----------------------
//NAME: SOHAN BADADE
//ID: 1001729097
//----------------------

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.swing.JTextArea;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

//this class is reponsible for listening from ack queue created by the server to send the ack to client.
public class ResponseListener extends Thread {
	Client cobj;
	public JTextArea textf;
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	
	public ResponseListener(Client cobj,JTextArea textf){
		this.cobj = cobj;
		this.textf = textf;
	}
	
	@Override
		public void run()
	{
		this.getResponse();
	}
	
	//Keep listening to the ack send by the server on received data.
	//https://javainsider.wordpress.com/tag/jms-with-activemq-sample-example/
	public void getResponse(){
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection;
		try {
			connection = connectionFactory.createConnection();
			connection.start();
			Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(cobj.selfName);
			MessageConsumer consumer = session.createConsumer(destination);
			boolean checkflag = true;
			
			while(true)
			{
//				System.out.println("Entering !!!");
				Message message = consumer.receive();
//				System.out.println("PASSED !!!");
				if (message instanceof TextMessage){
					TextMessage textMessage = (TextMessage) message;
					String user = message.getJMSCorrelationID();
					String data = textMessage.getText();
					
					System.out.println("ACK for DATA RECEIVED!!! ===== "+data );
					
					textf.setText(textf.getText()+"Data Polled by Server!!! => " +data);
					textf.setText(textf.getText()+"\n------------------------\n");
				}
				
//				if(message == null){
//					System.out.println("Queue Empty"); 
//					checkflag = false;
//				}
			}
//		connection.close();
		} catch (JMSException e) {
			e.printStackTrace();
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
