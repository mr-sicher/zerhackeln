package parktikum.zentrale;

import parktikum.functions.BasicFunction;
import parktikum.functions.Data;
import parktikum.functions.DataHistory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

/**
 * Created by sicher on 06.04.2017.
 */
public class BasicZentrale {

	public final static int SEND_BYTES = 1024;
	
	private HashMap<String, DataHistory> speicher;
	private DatagramSocket socket;
	private int port;
	
	public static void main(String args[]) throws SocketException, IOException{
		BasicZentrale zentrale = null;
		try {
			if (args.length == 1) {
				zentrale = new BasicZentrale(Integer.parseInt(args[0]));
			}
		}catch(NumberFormatException e){}
		if(zentrale == null){
			zentrale = new BasicZentrale(47111);
		}
		zentrale.receiveSensorData();
	}
	
	public BasicZentrale(int port) throws SocketException{
		speicher = new HashMap<>();
		socket = new DatagramSocket(port);
		this.port = port;
	}
	
	public void receiveSensorData() throws IOException{
		DatagramPacket packet;
		System.out.println("Zentrale hört auf Port " + port);
		while(true){
			packet = new DatagramPacket(new byte[SEND_BYTES], SEND_BYTES);
			socket.receive(packet);
			byte[] dataBytes = packet.getData();
			Data data = new Data();

			//System.out.println("" + packet.getAddress() + "+" + packet.getPort() + "+" + packet.getLength() + "+" + BasicFunction.getSendData(dataBytes, data) + "+" + data);// + new String(packet.getData(), 0, packet.getLength()));

			if(BasicFunction.getSendData(dataBytes, data) != -1){
				String keyString = packet.getAddress() + ":" + packet.getPort() + " " + data.inhalt;

				System.out.println("putting " + data + " into " + keyString);
				add(keyString, data);
			}else{
				System.err.println("Data couldn't be made for " + packet.getData() + " from " + packet.getAddress() + ":" + packet.getPort());
			}

		}
	}
	
	public void add(String key, Data data){
		if(speicher.get(key) == null){
			speicher.put(key, new DataHistory());
		}
		speicher.get(key).add(data);
	}
}
