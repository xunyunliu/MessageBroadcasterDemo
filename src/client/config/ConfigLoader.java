package client.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConfigLoader {

	public static Config loadConfig() {
		
		Config config = null;
		try {
			
			//Open the file reader
			//I am using the getResourceAsStream method because I am embedding
			//the config file in the executable jar file and an reading it from there.
			//This is NOT what you have to do on your projects...
			InputStream is = ConfigLoader.class.getResourceAsStream("config");
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader fileReader = new BufferedReader(isr);
			
			//This is similar to what you have to do on your project but replace "config"
			//with the config file path given as a program argument to your server
			//BufferedReader fileReader = new BufferedReader(new FileReader("config"));
			
			//Read the only line from the file
			String configLine = fileReader.readLine();
			
	
			if(configLine != null) {
				
				//Split the string into substrings delimited by tabs
				String[] configParams = configLine.split("\t");
				
				//We should have two substrings, one for the IP and one for the port
				if(configParams.length == 2) {
					//the server is the first parameter in the line
					String serverAddress = configParams[0];
					//the port is the second parameter in the line
					int serverPort = Integer.parseInt(configParams[1]);
					config = new Config(serverPort, serverAddress);
					System.out.println("Config loaded, server: " + serverAddress + ", port: " + serverPort);
				}
			}
			fileReader.close();		
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return config;
	}
	
}
