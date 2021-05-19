//----------------------
//NAME: SOHAN BADADE
//ID: 1001729097
//----------------------

import java.io.IOException;
import java.net.*;
import javax.swing.JTextArea;
import java.util.Timer;

// The thread that is listening to all incoming connections.
// It also starts a new thread for every incoming connections.
public class MainServer extends Thread {

    private ServerSocket serverSocket;
    private int port;
    public JTextArea textToAdd_MS;
    public JTextArea cc;
//    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
//    public ArrayList<String[]> arriddata = new ArrayList<String[]>();
//    public HashSet<String> newSSet = new HashSet<String>();
//  //reading orignal words present at server.
//    HashSet<String> orignalSSet = new HashSet<String>();
    
    public MainServer(JTextArea textToAdd_MS,JTextArea connC )
    {
    	this.cc = connC;
    	this.textToAdd_MS = textToAdd_MS;
        this.port = 54321;
        try {
			this.serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.print("Probably change the port number");
		}
    }
    
    
    @Override
    public void run()
    {
    	
    	GetFromPrimaryServer getDataIntoReplica =  new GetFromPrimaryServer();
    	getDataIntoReplica.start();
    	
    	
    	//REFERENCE- https://www.baeldung.com/java-timer-and-timertask
        ContentRetriever timerTask = new ContentRetriever();
        Timer timer = new Timer(true);
        //Every 60 seconds, poll clients for the status of their queues.
        //timer.scheduleAtFixedRate(timerTask,0,60000);
        //System.out.println("TimerTask started");

    	int status=0;
    	int countforprint=0;
        while(true)
        {	
            try
            {
                System.out.println( "Listening for a connection..." );
                Socket clientSockett = serverSocket.accept();
                
                if(countforprint == 0) {
                	textToAdd_MS.setText(textToAdd_MS.getText() + "\n------------------------- \n PRIMARY SERVER FAILED. \n BACKUP SERVER ACTIVE \n------------------------- ");
                	countforprint +=1;
                }
                
                Server ServerThread = new Server(clientSockett,textToAdd_MS,cc);
                ServerThread.start();
                
                if(status==0) {
                	timer.scheduleAtFixedRate(timerTask,0,60000);
                	status=status+1;
                }
                
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }


    }
}