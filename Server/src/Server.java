//----------------------
//NAME: SOHAN BADADE
//ID: 1001729097
//----------------------

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.JTextArea;

public class Server extends Thread {
    // username of self
    public String selfName;
    
    //this is a static variable, hence common between all the threads.
    public static Set<String> clientNames = new HashSet<String>();
    
    // words from vocabulary file available at server.
    public List<String> serverWords;
    
    // words from file sent by client.
    public List<String> clientWords;
    
    //newly Modified words: send this to client in a file.
    public List<String> modifiedClientWords;
    public DataOutputStream dataOutputStream = null;
    public DataInputStream dataInputStream = null;
    public ServerSocket serverSocket;
    public Socket clientSocket;
    public JTextArea textToAdd_S;
    public JTextArea connCli;
    public static HashSet<String> connCliSet = new HashSet<String>();
    
    // constructor
    public Server(Socket clientSocket,JTextArea textToAdd_S, JTextArea connCli) {
    	this.connCli = connCli;
		this.textToAdd_S = textToAdd_S;
    	this.clientSocket = clientSocket;
		this.selfName = null;
    	this.serverWords = new ArrayList<String>();
    	this.clientWords = new ArrayList<String>();
    	this.modifiedClientWords = new ArrayList<String>();
    }
    

@SuppressWarnings("deprecation")
@Override
	public void run()
	{  	
        try{
            System.out.println(clientSocket +" connected.");
            this.dataInputStream = new DataInputStream(clientSocket.getInputStream());
            this.dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
           
            
            
            while(true){
            	if(this.authenticateClient())
            	{
            		break;
            	}
            }
            
            
            if(this.selfName.substring(0, 6).equals("probe_")) {
            	System.out.println("THIS IS PROBE  for "+ this.selfName);
            	
            	//if it is a probe thread , just stick to this while loop.
            	while(true) {
                	ConnectionCheckReply();            		
            	}

            	
            }
            else {            	
            	// reads the files from client and gets the list of words into ArrayList "clientWords"
            	this.clientWords = this.receiveFileAndExtractWords();
            	
            	// read all the words from server's vocabulary file.
            	this.serverWords = this.readFile("serverFile.txt");
            	
            	// give both - clientwords and serverwords to this function
            	// and it gives the Arraylist of words- which needs to be written into a file 
            	// and sent to the client.
            	this.modifiedClientWords = this.modifyClientWords(this.clientWords,this.serverWords);
            	
            	//---------------------------------------------------------------------
            	// Have put two functions in synchronized block for following reason:
            	// getModifiedClientFile() writes the modifiedClientWords into a file.
            	// sendModifiedClientFile() sends that file over to client.
            	
            	//Incase there are multiple threads, only one thread 
            	//should have access to write the file and send the same file to client.
            	
            	// Following synchronized block takes care of that
            	//---------------------------------------------------------------------
            	synchronized (this) {
            		this.getModifiedClientFile(this.modifiedClientWords);
                	this.sendModifiedClientFile("clientFile.txt");
                }           	
            	
            }
            	
            	//the static variable, should be accessed by only one thread at a time.
            	synchronized (this) {
            		 if(!this.selfName.substring(0, 6).equals("probe_")) {
            		//writing to gui of disconnection.
            		textToAdd_S.setText(textToAdd_S.getText() +"\n Client Disconnected: "+selfName);
            		 }
            		clientNames.remove(selfName);
            		connCliSet.remove(selfName);
            		resetConn();
            	}
            	
            this.dataInputStream.close();
            this.dataOutputStream.close();
            this.clientSocket.close();
//            }earlier else ending
            
        }
        catch (Exception e){
        	if(this.selfName == null) {
        		this.stop();
        		return;
        	}
        	
        	if(this.selfName != null || selfName.trim().isEmpty()) {
        		synchronized (this) {
        		clientNames.remove(this.selfName);
        		connCliSet.remove(this.selfName);
        		}
        		System.out.println("ERROR FOR -"+ this.selfName);
        		textToAdd_S.setText(textToAdd_S.getText() +"\n Client Disconnected: "+this.selfName);
        	}
        	
        	//reset the connected clients textarea
        	resetConn();
        	
        	System.out.println("CLIENT DISCONNECTED:"+ this.getName()); //thread name
        	this.stop();
//        	System.out.println("CLIENT DISCONNECTED:"+ this.getName());     
        	
			System.out.println(clientNames);
        	return;
        	
        	
        }
    }
	
	public void ConnectionCheckReply() throws Exception {
//		
//        try {
			this.dataInputStream.readUTF();

			dataOutputStream.writeUTF("True");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			System.out.println("exe is here in conn check reply");
//		}
//        System.out.println("CLIENT SAYSSSSS: " + message);
        
        
		
	}



	//function to reset the text area.
		public void resetConn() {

			if(connCliSet.size() < 1) {
			connCli.setText("");
			}
			connCli.setText("");
			for(String s: connCliSet) {
				System.out.println(s);
				connCli.setText(connCli.getText() +"\n "+s);
			}
			
		}
    
	public boolean authenticateClient() throws IOException {
    	try {
    		String clientName= dataInputStream.readUTF();
    		Boolean nameFlag = false;
    		
    		synchronized (this) {
    			if(clientNames.contains(clientName)) {
    				nameFlag = true;
    			}
    		}
    		
//        	if(clientName.substring(0, 6).equals("probe_")) {
//        		this.selfName = clientName;
//        		dataOutputStream.writeBoolean(true);
//                dataOutputStream.flush();
//                return true;
//        	}
        	
    		if(nameFlag) {
        		System.out.println("Client Already present.");
            	dataOutputStream.writeBoolean(false);
                dataOutputStream.flush();
            	return false;
        	}
        	else {
        		this.selfName = clientName;
        		
        		synchronized (this) {
        				clientNames.add(clientName);
        			}
        		
            	dataOutputStream.writeBoolean(true);
                dataOutputStream.flush();
                
                if(!this.selfName.substring(0, 6).equals("probe_")) {
                	textToAdd_S.setText(textToAdd_S.getText() +"\n Client connected: "+clientName);
                	connCliSet.add(clientName);
                	connCli.setText(connCli.getText() +"\n "+clientName);
                }
                return true;
        	}
    	}
    	catch(Exception e){
    		clientNames.remove(selfName);
//    		label2.setText("Client disconnected: "+selfName);
    	}
    	return true;
    	
    }
    
    public List<String> modifyClientWords(List<String> clientWords,List<String> serverWords) throws Exception{
    	List<String> wordsFound = new ArrayList<String>();
    	boolean flagfound=false;
    	
        for(String cw: clientWords) {
        	for(String sw: serverWords) {
        		if(cw.equals(sw)){
        			wordsFound.add("["+cw+"]");
        			flagfound = true;
        			break;
        		}
        	}
        	if(!flagfound) {
        		wordsFound.add(cw);
        	}
        	flagfound=false;
        }
    	return wordsFound;	
    }

// REFERENCE - https://medium.com/@HeptaDecane/file-transfer-via-java-sockets-e8d4f30703a5
// REFERENCE - https://gist.github.com/HeptaDecane/939ee4691ea102c3837c28edde8dc3b6#file-receiver-java
    public List<String> receiveFileAndExtractWords() throws Exception{
        int bytes = 0;
        
        // byte array to collect the file client's words.
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        
        //FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        long size = dataInputStream.readLong();     
        byte[] buffer = new byte[4*1024];
        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
        	//fileOutputStream.write(buffer,0,bytes);
            byteOut.write(buffer, 0, bytes);
            size -= bytes;
        }
        //convert bytearray to string
        String rawClientFile = byteOut.toString("UTF-8");
        String[] rawClientWords = rawClientFile.split(" ");
        List<String> rawWordsArrayList = new ArrayList<String>();

        for(String t: rawClientWords) {
        	rawWordsArrayList.add(t);
    	}
        return rawWordsArrayList;
    }


    public List<String> readFile(String fileName) throws Exception{
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        
        String line = br.readLine();
        List<String> words = new ArrayList<String>();
        
        while(line != null) {
        	String[] temp = line.split(" ");
        	for(String t: temp) {
        		words.add(t);
        	}
        	line = br.readLine();
        }
        br.close();
        return words;
    }
    
    public void getModifiedClientFile(List<String> words) throws Exception{
    	FileWriter file = new FileWriter("clientFile.txt");
        BufferedWriter output = new BufferedWriter(file);
        
        for(String ew: words) {
        	output.write(ew);
        	output.write(" ");
        }
        output.close();
    }

// REFERENCE - https://medium.com/@HeptaDecane/file-transfer-via-java-sockets-e8d4f30703a5
    public synchronized void sendModifiedClientFile(String path) throws Exception{
        int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        dataOutputStream.writeLong(file.length());
        byte[] buffer = new byte[4*1024];
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }
        fileInputStream.close();
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