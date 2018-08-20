package server.example3;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;



public class Server {

	public static void main(String[] args) {

		ServerSocket listeningSocket = null;
		try {
			
			// Create a server socket listening on port 4444
			listeningSocket = new ServerSocket(4444);
			System.out.println(Thread.currentThread().getName() + 
					" - Server listening on port 4444 for a connection");
			
			int clientNum = 0;

			//Listen for incoming connections for ever
			while (true) {
				
				//Accept an incoming client connection request
				Socket clientSocket = listeningSocket.accept();
				System.out.println(Thread.currentThread().getName() 
						+ " - Client conection accepted");
				clientNum++;

				//Create one thread per connection, each thread will be
				//responsible for listening for messages from the client, placing them
				//in a queue, and creating another thread that processes the messages
				//placed in the queue
				ClientConnection clientConnection = new ClientConnection(clientSocket, clientNum);
				clientConnection.setName("Thread" + clientNum);
				clientConnection.start();
				
				//Register the new connection with the client manager
				ServerState.getInstance().clientConnected(clientConnection);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(listeningSocket != null) {
				try {
					listeningSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
