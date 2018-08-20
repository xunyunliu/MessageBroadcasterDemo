package server.example3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientConnection extends Thread {

	private Socket clientSocket;
	private BufferedReader reader;
	private BufferedWriter writer;
	//This queue holds messages sent by the client or messages intended for the client from other threads
	private BlockingQueue<Message> messageQueue;
	private int clientNum;

	public ClientConnection(Socket clientSocket, int clientNum) {
		try {
			this.clientSocket = clientSocket;
			reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
			writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));	
			messageQueue = new LinkedBlockingQueue<Message>();
			this.clientNum = clientNum;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		
		try {
			
			//Start the client message reader thread. It 'listens' for any
			//incoming messages from the client's socket input stream and places
			//them in a queue (producer)
			ClientMessageReader messageReader = new ClientMessageReader(reader, messageQueue);
			messageReader.setName(this.getName() + "Reader");
			messageReader.start();
			
			System.out.println(Thread.currentThread().getName() 
					+ " - Processing client " + clientNum + "  messages");
			
			//Monitor the queue to process any incoming messages (consumer)
			while(true) {
				
				//This method blocks until there is something to take from the queue
				//(when the messageReader receives a message and places it on the queue
				//or when another thread places a message on this client's queue)
				Message msg = messageQueue.take();
				
				//process the message
				//If the message is "exit" and from a thread then
				//it means the client has closed the connection and we must
				//close the socket and update the server state
				//(See what the message reader does when it reads the end of stream
				//from the client socket)
				if(!msg.isFromClient() && msg.getMessage().equals("exit")) {
					break;
				}
				
				if(msg.isFromClient()) {
					
					//If the message is from the client, then place it on every
					//client connection queue so that it can be processed by the 
					//corresponding client thread and sent to the corresponding client
					
					//Create the message to be placed on the threads queue
					Message msgForThreads = new Message(false, msg.getMessage());
					
					List<ClientConnection> connectedClients = ServerState.getInstance().getConnectedClients();
					for(ClientConnection client : connectedClients) {
						//Place the message on the client's queue
						client.getMessageQueue().add(msgForThreads);
					}
				} else {
					//If the message is from a thread and it isn't exit, then
					//it is a message that needs to be sent to the client
					write(msg.getMessage());
				}
			}
			
			clientSocket.close();
			ServerState.getInstance().clientDisconnected(this);
			System.out.println(Thread.currentThread().getName() 
					+ " - Client " + clientNum + " disconnected");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public BlockingQueue<Message> getMessageQueue() {
		return messageQueue;
	}

	public void write(String msg) {
		try {
			writer.write(msg + "\n");
			writer.flush();
			System.out.println(Thread.currentThread().getName() + " - Message sent to client " + clientNum);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
