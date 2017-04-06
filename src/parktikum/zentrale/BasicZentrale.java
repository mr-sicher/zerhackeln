package parktikum.zentrale;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

public class BasicZentrale {

	public final static int SEND_BYTES = 1024;
	
	private HashMap<String, DataHistory> speicher;
	private DatagramSocket socket;
	
	public static void main(String args[]) throws SocketException, IOException{
		new BasicZentrale(4711).receive();
	}
	
	public BasicZentrale(int port) throws SocketException{
		speicher = new HashMap<>();
		socket = new DatagramSocket(port);
	}
	
	public void receive() throws IOException{
		DatagramPacket packet;
		System.out.println("Zentrale hört");
		while(true){
			packet = new DatagramPacket(new byte[SEND_BYTES], SEND_BYTES);
			socket.receive(packet);
			System.out.println("" + packet.getAddress() + packet.getPort() + packet.getLength() + packet.getData());
			//add(packet.getAddress() + ":" + packet.getPort(), new Data());
		}
	}
	
	public void add(String key, Data data){
		if(speicher.get(key) == null){
			speicher.put(key, new DataHistory());
		}
		speicher.get(key).add(data);
	}
}
