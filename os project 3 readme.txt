First compile both files with javac
ex:	javac client.java
	javac server.java

Then to begin, run the server.java FIRST with 1 argument being the port number on one machine or instance of putty.
ex:	java server 9090

then run the client.java with two arguments first being the IP and then the port number on a different machine or instance of putty.
ex:	java client 129.110.92.15 9090

after that, run commands from the client and you can watch on both screens what happens.