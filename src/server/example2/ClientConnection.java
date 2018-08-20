package server.example2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

public class ClientConnection extends Thread {

	private Socket clientSocket;
	private BufferedReader reader; 
	private BufferedWriter writer; 
	private int clientNum;

	public ClientConnection(Socket clientSocket, int clientNum) {
		try {
			this.clientSocket = clientSocket;
			reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
			writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
			this.clientNum = clientNum;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		try {
			
			System.out.println(Thread.currentThread().getName() 
					+ " - Reading messages from client's " + clientNum + " connection");
			
			String clientMsg = null;
			while ((clientMsg = reader.readLine()) != null) {
				System.out.println(Thread.currentThread().getName() 
						+ " - Message from client " + clientNum + " received: " + clientMsg);

				//Broadcast the client message to all other clients connected
				//to the server.
				List<ClientConnection> clients = ServerState.getInstance().getConnectedClients();
				for(ClientConnection client : clients) {
					client.write(clientMsg);
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
	
	//Needs to be synchronized because multiple threads can me invoking this method at the same
	//time
	public synchronized void write(String msg) {
		try {
			writer.write(msg + "\n");
			writer.flush();
			System.out.println(Thread.currentThread().getName() + " - Message sent to client " + clientNum);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
