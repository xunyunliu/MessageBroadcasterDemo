package client.config;

public class Config {

	private int serverPort;
	private String serverAddress;
	
	public Config(int serverPort, String serverAddress) {
		super();
		this.serverPort = serverPort;
		this.serverAddress = serverAddress;
	}

	public int getServerPort() {
		return serverPort;
	}

	public String getServerAddress() {
		return serverAddress;
	}
	
	
}
