import java.io.*;
import java.net.*;
import java.util.*;

public class client {

//global variables
Socket s = null;
PrintWriter out = null;
BufferedReader input = null;
BufferedReader inputa = null;

public void listenSocket(String host, int port)
{
	try
	{
		//create new socket with input
		s = new Socket(host, port);
		input = new BufferedReader(new InputStreamReader(s.getInputStream()));
		out = new PrintWriter(s.getOutputStream(), true);
	}
	
		//error catching
	catch (UnknownHostException e)
	{
		System.out.println("Uknown host, exiting program");
		System.exit(1);
	}
	catch (IOException e)
	{
		System.out.println("No I/O, exiting program");
		System.exit(1);
	}	

}

public void communicate()
{
	boolean exitflag = false;
	OutputStream os = null;


while(exitflag == false){

		//menu and ask for input
	System.out.println("Please choose a command:");
	System.out.println("A.	Display the names of all files.");
	System.out.println("B.	Get a particular file.");
	System.out.println("C.	Remove a file from the list.");
	System.out.println("D.	Add a new file to the server.");
	System.out.println("E. 	Exit");

	Scanner in = new Scanner(System.in);
	
try {



switch(in.nextLine().toUpperCase()) {
	case "A":
		//Initialize variable
		String answer;
		
		
		
		out.println("A");	//send request for file list
		System.out.println("Displaying file lists");
				//read in files until you reach end
				answer = input.readLine();
				
				while (!answer.equals(""))
				{
				System.out.println(answer);
				answer = input.readLine();
				
				}						
		break;
	case "B":
		//initialize variables
		int bytesRead = 0;
		int current = 0;
		int counter = 0;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		InputStream is = s.getInputStream();
		
		//send B command to server
		out.println("B");
		System.out.println("Please enter the name of the file you would like to get");

		
		String FILENAME = in.nextLine();	 
		out.println(FILENAME); 	//send the file name to the server
	
		String call3;
		call3 = input.readLine();

		if (!call3.equals("bad"))
		{
		//receive file
		int FILE_SIZE = Integer.parseInt(input.readLine());
		System.out.println("got file size: " + FILE_SIZE);
		byte[] bytearray = new byte [FILE_SIZE ];
		fos = new FileOutputStream(FILENAME);
		bos = new BufferedOutputStream(fos);
		bytesRead = is.read(bytearray,0,bytearray.length);
		current = bytesRead;
		System.out.println(Arrays.toString(bytearray));
		System.out.println(bytearray.length);
		counter = 0;
		System.out.println(current);
		
		//Read byte by byte
		do{
		bytesRead = is.read(bytearray, current, (bytearray.length - current));
		
		if(bytesRead >= 0)
		{
		current += bytesRead;
		}
		if(bytearray.length - current == 0){
			break;
		}
		}			
		while(bytesRead > -1);
		System.out.println(Arrays.toString(bytearray));
	
		//write file
		bos.write(bytearray, 0 , current);
		bos.flush();
		System.out.println("File " + FILENAME + " downloaded (" + current + " bytes read)");
		
		//close and flush
		if (fos != null)
		fos.close();
		if (bos != null)
		bos.close();
		
		break;
		}
		else
		System.out.println("Invalid file name please try again!");
		break;
	case "C":
		//initialize variable
		String call;
		out.println("C");
		System.out.println("Please enter the name of the file you wish to delete");
		 
		out.println(in.nextLine()); 	//send the file name to the server
		call = input.readLine();
		System.out.println(call);

		break;
	case "D":
		//initialize variables
		FileInputStream sfis = null;
		BufferedInputStream sbis = null;
		
		//send D to server
		out.println("D");

		System.out.println("Please enter the file name you want to send");
		String SENDFILE = in.nextLine();
		File f  = new File(SENDFILE);	//obtain file name and search for it
		
		//send file name to be sent
		byte [] bytea = new byte [(int)f.length()];
		out.println(bytea.length);		
		out.println(SENDFILE);

		//write file BYTE by BYTE and send
		sfis = new FileInputStream(f);
		sbis = new BufferedInputStream(sfis);
		sbis.read(bytea,0,bytea.length);
		os = s.getOutputStream();
		System.out.println("Sending " + SENDFILE + "(" + bytea.length  + " bytes)");
		os.write(bytea,0,bytea.length);
		os.flush();
		System.out.println("File Sent");
		
		//close and flush
		if (sbis != null)
		{sbis.close();		}
		String call2;
		
		call2 = input.readLine();
		System.out.println(call2);

		break;
	case "E":
		out.println("E");
		exitflag = true;			//exit program
		System.out.println("Exiting Program");
		System.exit(0);

		break;
	default:
		//if they put an invalid choice ask again
	System.out.println("Please enter a valid choice!(A,B,C,D,E)");


break;
}
}

catch (IOException e)
{
	System.out.println("ERROR" + e);
}

}
}


public static void main(String[] args) throws IOException 
{
	
	System.out.println("Welcome to the client");

	//check if there are two arguments to make program run
	if(args.length != 2)
	{
	System.out.println("Usage:	client hostname port");
	System.exit(1);
	}

//intialize variables
int port = Integer.parseInt(args[1]);
String serverAdd = args[0]; 
client c = new client();

//call methods to run program
c.listenSocket(serverAdd, port);
c.communicate();


//exit
System.exit(0);
}
}
