package client;

import java.io.BufferedReader;
import java.net.SocketException;

public class MessageListener extends Thread {

	private BufferedReader reader;
	
	public MessageListener(BufferedReader reader) {
		this.reader = reader;
	}
	
	@Override
	public void run() {
		
		try {
			
			String msg = null;
			//Read messages from the server while the end of the stream is not reached
			while((msg = reader.readLine()) != null) {
				//Print the messages to the console
				System.out.println(msg);
			}
		} catch (SocketException e) {
			System.out.println("Socket closed because the user typed exit");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
}
