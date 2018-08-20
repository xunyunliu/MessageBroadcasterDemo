package server.example1;

import java.util.ArrayList;
import java.util.List;

//Singleton object in charge of processing messages received from
//clients and managing the server state
public class ClientManager {
	
	private static ClientManager instance;
	
	private List<ClientConnection> connectedClients;
	
	private ClientManager() {
		connectedClients = new ArrayList<ClientConnection>();
	}
	
	public static synchronized ClientManager getInstance() {
		if(instance == null) {
			instance = new ClientManager();
		}
		return instance;
	}
	
	public synchronized void processMessage(String msg) {
		// Broadcast the client message to all the clients connected
		// to the server
		for(ClientConnection clientConnection : connectedClients) {
			clientConnection.write(msg);
		}
	}
	
	public synchronized void clientConnected(ClientConnection clientConnection) {
		connectedClients.add(clientConnection);
	}
	
	public synchronized void clientDisconnected(ClientConnection clientConnection) {
		connectedClients.remove(clientConnection);
	}

	public synchronized List<ClientConnection> getConnectedClients() {
		return connectedClients;
	}

}
