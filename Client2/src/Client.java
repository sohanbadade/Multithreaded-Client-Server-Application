//----------------------
//NAME: SOHAN BADADE
//ID: 1001729097
//----------------------
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client{
    public DataOutputStream dataOutputStream;
    public DataInputStream dataInputStream;
    public Socket socket;
    public String selfName;
    
    public Client(int portNum) {
    	this.dataOutputStream = null;
        this.dataInputStream = null;
        try {
			this.socket = new Socket("localhost",portNum);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
		}
        }
    
    //authenticate() function responsible for sending the username to the server
    //this function also waits for server's approval/rejection (true/false) of username.
    public String authenticate(String username) {
        try{
        	dataInputStream = new DataInputStream(socket.getInputStream());
        	dataOutputStream = new DataOutputStream(socket.getOutputStream());
        	
        	dataOutputStream.writeUTF(username);
            dataOutputStream.flush();
            
            boolean isAuthenticated =  dataInputStream.readBoolean();
            System.out.println("Server responed "+isAuthenticated+" for username:"+username);
            if(isAuthenticated) {
            	selfName = username;
            	return "ok";
            }
            else {
            	return "notok";
            }
        }catch (Exception e){
        	return "noton";
        }
        //return "unknownerr";
}

    //sends the file to server and right away starts expecting 
    // the result from server in receiveFile() function.
    
    //This function throws exception that is caught by the callling function and determines if server is on or off, 
    //since this function is the point of contact with the server.
    public void runClient(String filename) throws Exception {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            sendFile(filename);
            //filename has the path to selected file and path name.
            // Here, the same filename is passed to receiveFile() function
            // Reason: the file will be replaced by new one received by server.
            receiveFile(filename);
            
            
            dataInputStream.close();
            dataOutputStream.close();   
    }

// REFERENCE - https://medium.com/@HeptaDecane/file-transfer-via-java-sockets-e8d4f30703a5
    public void sendFile(String path) throws Exception  {
    	int bytes = 0;
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        
        this.dataOutputStream.writeLong(file.length());  
        byte[] buffer = new byte[4*1024];
        
        while ((bytes=fileInputStream.read(buffer))!=-1){
            this.dataOutputStream.write(buffer,0,bytes);
            this.dataOutputStream.flush();
        }
        fileInputStream.close();
        
    }

// REFERENCE - https://medium.com/@HeptaDecane/file-transfer-via-java-sockets-e8d4f30703a5
    public void receiveFile(String fileName) throws Exception{
        int bytes = 0;
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        
        long size = dataInputStream.readLong();
        byte[] buffer = new byte[4*1024];
        while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
            fileOutputStream.write(buffer,0,bytes);
            size -= bytes;
        }
        fileOutputStream.close();
    }
}




