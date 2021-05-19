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
    
    public MainServer(JTextArea textToAdd_MS,JTextArea connC )
    {
    	this.cc = connC;
    	this.textToAdd_MS = textToAdd_MS;
        this.port = 12345;
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
    	
    	//REFERENCE- https://www.baeldung.com/java-timer-and-timertask
        ContentRetriever timerTask = new ContentRetriever();
        Timer timer = new Timer(true);
        //Every 60 seconds, poll clients for the status of their queues.
        timer.scheduleAtFixedRate(timerTask,0,60000);
        
//        System.out.println("TimerTask started");

        while(true)
        {	
            try
            {
                System.out.println( "Listening for a connection..." );
                Socket clientSockett = serverSocket.accept();
                Server ServerThread = new Server(clientSockett,textToAdd_MS,cc);
                ServerThread.start();
                
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
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