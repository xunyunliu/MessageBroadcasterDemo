package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

import client.config.Config;
import client.config.ConfigLoader;

public class Client {

	public static void main(String[] args) {

		try {
			
			// Create a stream socket and connect it to the server as specified in the config file
			Config config = ConfigLoader.loadConfig();
			Socket socket = new Socket(config.getServerAddress(), config.getServerPort());
			System.out.println("Connection with server established");

			//Get the input/output streams for reading/writing data from/to the socket
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

			//Launch a new thread in charge of listening for any messages
			//that arrive through the socket's input stream (any data sent by the server)
			MessageListener ml = new MessageListener(reader);
			ml.start();
			
			//Use a scanner to read input from the console
			Scanner scanner = new Scanner(System.in);
			String inputStr = null;

			//While the user input differs from "exit"
			while (!(inputStr = scanner.nextLine()).equals("exit")) {
				
				// Send the input string to the server by writing to the socket output stream
				writer.write(inputStr + "\n");
				writer.flush();
			}
			
			scanner.close();
			socket.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} 

	
	}
}
