package server.example3;

public class Message {

	//True if the message comes from a client, false if it comes from a thread
	private boolean isFromClient;

	private String message;
	
	public Message(boolean isFromClient, String message) {
		super();
		this.isFromClient = isFromClient;
		this.message = message;
	}
	
	public boolean isFromClient() {
		return isFromClient;
	}
	public String getMessage() {
		return message;
	}
	
	
}
