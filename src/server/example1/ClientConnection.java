package server.example1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

//Represents a connection with a client. This class is not only used to determine the code
//executed by a Thread (the run method), but it is also
//a 'normal' object, with properties and methods that can be accessed by other objects/threads.
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
	//Any code executed within this method will be part of the same execution thread, even
	//if that means invoking methods declared on a class that extends the Thread class.
	public void run() {
		try {

			System.out.println(Thread.currentThread().getName() 
					+ " - Reading messages from client's " + clientNum + " connection");

			String clientMsg = null;
			while ((clientMsg = reader.readLine()) != null) {

				System.out.println(Thread.currentThread().getName() 
						+ " - Message from client " + clientNum + " received: " + clientMsg);

				// Let the client manager process the message received from the
				// client
				ClientManager.getInstance().processMessage(clientMsg);
			}

			clientSocket.close();
			
			//Update the server state to reflect the client disconnection
			ClientManager.getInstance().clientDisconnected(this);
			System.out.println(Thread.currentThread().getName() 
						+ " - Client " + clientNum + " disconnected");

		} catch (Exception e) {
			e.printStackTrace();
		}
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
