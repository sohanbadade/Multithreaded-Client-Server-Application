# Multithreaded-Client-Server-Application

SPELL CHECK SYSTEM

Running Guidelines:
Project consists of two projects: 
1. Client 
2. Server 


This Project makes use of JMS Supplier - ActiveMQ Message broker (5.15.14). 

Client and Server communicate via ActiveMQ server which will be running on localhost in this case. Please download the ActiveMQ Library suitable for your machine operating system, via  following link since assignment says no jar is to be included: 
http://activemq.apache.org/components/classic/download/ (version: 5.15.14)

To start ActiveMQ, please unzip and go to bin folder and start the Message broker. 
(example: “activemq start” to start on windows) 
(default username and password of ActiveMQ is both always “admin” for both)

Next, Start Server:
Start the Server by doing following steps: 
  1. Go to Server/src 
  2. It has following file structure: 
          ServerPage1.java : Responsible for Entry point and GUI. 
          MainServer.java : Keeps listening to incoming connection. 
          ContentRetriever.java : The thread responsible for reading from queue every 60 seconds.
          Server.java : A thread responsible for every client. serverFile.txt: The Lexicon file at the server.
          serverFile.txt: The Lexicon file at the server. 
  3. Compile the files via following command and then start the Server: (Windows OS)
          Compile: 
          
          ```
          javac -cp *.java Run: java -cp .;< path of ActiveMQ library > ServerPage1 
          ```
        for example, in my case, Compile: ``` javac -cp D:\java\apache-activemq-5.15.14\activemq-all-5.15.14.jar *.java ```
          Run: 
          
          ```
          java -cp .;D:\java\apache-activemq-5.15.14\activemq-all-5.15.14.jar ServerPage1
          ```
  
  Next, Start Backup Server:
  Start backup server in similar way above.

Start Client: 

1. Go to Client/src 

2. Compile the files via following command and then start the Client: 
        (Windows OS) 
        Compile: 
        
        ``` javac -cp *.java Run: java -cp .;< path of ActiveMQ library > ClientPage1 ```
        
        
      For example, in my case, Compile: 
      
      ``` javac -cp D:\java\apache-activemq-5.15.14\activemq-all-5.15.14.jar *.java ```
     Run: 
        ``` java -cp .;D:\java\apache-activemq-5.15.14\activemq-all-5.15.14.jar ClientPage1 ```
