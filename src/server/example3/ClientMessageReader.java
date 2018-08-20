package server.example3;

import java.io.BufferedReader;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

public class ClientMessageReader extends Thread {

	private BufferedReader reader; 
	private BlockingQueue<Message> messageQueue;
	
	public ClientMessageReader(BufferedReader reader, BlockingQueue<Message> messageQueue) {
		this.reader = reader;
		this.messageQueue = messageQueue;
	}
	
	@Override
	//This thread reads messages from the client's socket input stream
	public void run() {
		try {
			
			System.out.println(Thread.currentThread().getName() 
					+ " - Reading messages from client connection");
			
			String clientMsg = null;
			while ((clientMsg = reader.readLine()) != null) {
				System.out.println(Thread.currentThread().getName() 
						+ " - Message from client received: " + clientMsg);
				//place the message in the queue for the client connection thread to process
				Message msg = new Message(true, clientMsg);
				messageQueue.add(msg);
			}
			
			//If the end of the stream was reached, the client closed the connection
			//Put the exit message in the queue to allow the client connection thread to 
			//close the socket
			Message exit = new Message(false, "exit");
			messageQueue.add(exit);
			
		} catch (SocketException e) {
			//In some platforms like windows, when the end of stream is reached, instead
			//of returning null, the readLine method throws a SocketException, so 
			//do whatever you do when the while loop ends here as well
			Message exit = new Message(false, "exit");
			messageQueue.add(exit);		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
