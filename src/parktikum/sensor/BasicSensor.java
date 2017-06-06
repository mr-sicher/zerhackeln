package parktikum.sensor;

import parktikum.functions.BasicFunction;

import java.io.IOException;
import java.net.*;
import java.util.Random;

/**
 * Created by sicher on 05.04.2017.
 *
 */
public class BasicSensor implements Runnable{
	public final static int MIN_WAIT = 100;
	public final static int MAX_WAIT = 5000;
	public final static int MIN_WERT = 1000;
	public final static int MAX_WERT = 1001;
	public final static int SEND_BYTES = 1024;

	private Random rand;
	private String inhalt;
	private int nummer;
	private volatile int wert;
	private String ip;//zentrale
	private int port;//zentrale
	private DatagramSocket socket;
	private DatagramSocket reciveSocket;
	
	public static void main(String args[]) throws NumberFormatException, SocketException, UnknownHostException{
		
		if(args.length == 3){
			new Thread(new BasicSensor(args[0], args[1], Integer.parseInt(args[2]))).start();
		}else{
			new Thread(new BasicSensor("Bier", "localhost", 47111)).start();
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
		new Thread(new Runnable() {
			@Override
			public void run() {
				recieveChange();
			}
		}).start();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int alterWert = -1;
		while(true){
			alterWert = wert;
			changeWert(wert - rand.nextInt(10));
			if(wert < 0) {
				changeWert(0);
			}
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

	public synchronized void changeWert(int value) {
		if(value - wert > 0 && value > 1)
		System.err.println("Change to " + value);
		this.wert = value;
	}
	
	public void recieveChange() {
		//TODO create Socket

		System.out.println("Sensor hört UDP auf Port " + socket.getLocalPort());
		DatagramPacket packet;
		while(true) {
			packet = new DatagramPacket(new byte[SEND_BYTES], SEND_BYTES);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			byte[] dataBytes = packet.getData();
			int result = (int) dataBytes[0];
			changeWert(wert + result);
		}
	}
	public void send(){
		try {
			int sendWert = wert;
			String send = inhalt + " " + nummer + " " + sendWert;
			System.out.println("send " + send);
			byte[] data = BasicFunction.buildSendData(inhalt, sendWert, nummer);
			DatagramPacket p = new DatagramPacket(data, data.length, InetAddress.getByName(ip), port);
			socket.send(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//successfully sended
		nummer ++;
	}



	
}
