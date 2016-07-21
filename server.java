import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Semaphore;
	
public class server{
	ServerSocket s = null;
	byte[][] fileBytes;
	String[] fileNames;
	int pos;
	int len;
	Semaphore sem = new Semaphore(1);

	public void listenSocket(int port){ //get new connections
		try{
			s = new ServerSocket(port);
		}catch(IOException e){
		}
		while(true){
			serverThread w;
			try{
				w = new serverThread(s.accept());
				Thread t = new Thread(w);
				t.start();
			}catch (IOException e){
			}
		}
	}

	protected void finalize(){
		try{
			s.close();
		}catch (IOException e){
		}
	}

	public void readInFiles() { //read in the initial files
		try{
		File folder = new File("files");
		File[] listofFiles = folder.listFiles();
		pos = 0;
		len = listofFiles.length*2;
		fileBytes = new byte[len*2][];
		fileNames = new String[len*2];
			for (int i = 0; i < listofFiles.length; i++){
				File temp = listofFiles[i];
				FileInputStream fis = new FileInputStream(temp);
				BufferedInputStream bis = new BufferedInputStream(fis);
				fileBytes[i] = new byte[(int)temp.length()];
				fileNames[i] = temp.getName();
				bis.read(fileBytes[i], 0, fileBytes[i].length);
				pos++;
			}
		} catch(Exception e){
		}
		for(int i = 0; i < pos; i++){
			
		}
	}


	public static void main(String[] args){
		server s = new server();
		System.out.println("Server started");
		s.readInFiles();
		int port = Integer.parseInt(args[0]);
		s.listenSocket(port);
	}


class serverThread implements Runnable{ //server threas
	
	private Socket client;

	serverThread(Socket client){ //get client
		this.client = client;
	}

	public void run(){ //run
		System.out.println("Server thread started. Connected to: " + client.getRemoteSocketAddress());
		BufferedReader in = null;
		PrintWriter out = null;
		OutputStream os = null;
		try{
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		out = new PrintWriter(client.getOutputStream(), true);
			while(true){
				String line = in.readLine();
				System.out.println("Recieved input: " + line);
				switch(line){
					//sends the list of files
					case "A":System.out.println("Sending file names"); 
						for(int i = 0; i < pos; i++){
							out.println(fileNames[i]);
							System.out.println("Sent: " + fileNames[i]);
						}
						out.println("");
						break;
					//sends a file to the client
					case "B":line = in.readLine();
						boolean check = false;
						for(int i = 0; i < pos; i++){
							if(fileNames[i].equals(line)){
								out.println("good");
								out.println(fileBytes[i].length);
								os = client.getOutputStream();
								os.write(fileBytes[i], 0, fileBytes[i].length);
								System.out.println("Sending: " 
									+ fileNames[i] 
									+ " (Bytes: " 
									+ fileBytes[i].length 
									+ ")");
								check = true;
								break;
							}
						}
						if (check == false){
							out.println("bad");
							System.out.println("The requested file does not exist");
						}
						break;
					//remove a file
					case "C": line = in.readLine();
						check = false;
						for(int i = 0; i < pos; i++){
							if(fileNames[i].equals(line)){
								try{
									sem.acquire();
								}catch(Exception e){
								}
								for(int j = i+1; j < pos; j++){
									fileNames[j-1] = fileNames[j];
									fileBytes[j-1] = fileBytes[j];
								}
								pos--;
								sem.release();
								System.out.println("Removed: " + line);
								out.println(line + " was successfully removed");
								check = true;
								break;
							}

						}
						if (check == false){
							out.println(line + " was not found");
							System.out.println("The requested file does not exist");
						}
						break;
					//recieve a new file form the client
					case "D":
						try{
							sem.acquire();
						}catch(Exception e){
						}
						if(pos == len){
							len *= 2;
							byte[][] temp = new byte[len*2][];
							String[] temp2 = new String[len*2];
							for(int i = 0; i < pos; i++){
								temp[i] = fileBytes[i];
								temp2[i] = fileNames[i];
							}
							fileBytes = temp;
							fileNames = temp2;
						}
						fileBytes[pos] = new byte[Integer.parseInt(in.readLine())];
						InputStream is = client.getInputStream();
						fileNames[pos] = in.readLine();
						int read = is.read(fileBytes[pos], 0, fileBytes[pos].length);
						int current = read;
						do{
							read = is.read(fileBytes[pos], 0, (fileBytes[pos].length - current));
							if(read >= 0) current += read;
							if(fileBytes[pos].length - current == 0) break;
						}while(read > -1);
						System.out.println("Recieved new file: " 
							+ fileNames[pos]
							+ "(Bytes: "
							+ fileBytes[pos].length
							+ ")");
						pos++;
						sem.release();
						out.println("file recieved");
						break;
						case "E":
							System.out.println("Closing client: " + client.getRemoteSocketAddress()); 
							client.close();
							break;
				}
			}
		}catch (IOException e){
		}
	}
}


}
