package parktikum.sensor;

import parktikum.functions.BasicFunction;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by sicher on 05.04.2017.
 */
public class BasicSensor implements Runnable{
	public final static int MIN_WAIT = 100;
	public final static int MAX_WAIT = 5000;
	public final static int MIN_WERT = 1000;
	public final static int MAX_WERT = 5000;

	private Random rand;
	private String inhalt;
	private int nummer;
	private int wert;
	private String ip;//zentrale
	private int port;//zentrale
	private DatagramSocket socket;
	
	public static void main(String args[]) throws NumberFormatException, SocketException, UnknownHostException{
		
		if(args.length == 3){
			new Thread(new BasicSensor(args[0], args[1], Integer.parseInt(args[2]))).start();
		}else{
			new Thread(new BasicSensor("Bier", "141.100.42.138", 47111)).start();
		}
	}
	
	public BasicSensor(String inhalt, String ip, int port) throws SocketException, UnknownHostException {
		rand = new Random();
		this.inhalt = inhalt;
		this.nummer = 1;//starte bei 1 sonst macht der log fehler mit INT_MIN+1
		this.wert = rand.nextInt(MAX_WERT-MIN_WERT) + MIN_WERT;
		this.ip = ip;
		this.port = port;
		socket = new DatagramSocket();
		send();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int alterWert = -1;
		while(true){
			nummer ++;
			alterWert = wert;
			wert -= rand.nextInt(10);
			if(wert < 0)
				wert = 0;
			if(wert != alterWert)
				send();
			try {
				Thread.sleep(rand.nextInt(MAX_WAIT-MIN_WAIT) + MIN_WAIT);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException("Error with sleep in Sensor");
			}
		}
	}
	
	public void init() throws IOException{
		//TODO create Socket
		System.out.println(inhalt + ": " + ip + ":" + port);
	}
	public void send(){
		try {
			String send = inhalt + " " + nummer + " " + wert;
			System.out.println("send " + send);
			byte[] data = BasicFunction.buildSendData(inhalt, wert, nummer);
			DatagramPacket p = new DatagramPacket(data, data.length, InetAddress.getByName(ip), port);
			socket.send(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void add(){
		//TODO implement im einkauf
	}


	
}
