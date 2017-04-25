package parktikum.zentrale;

import parktikum.functions.BasicFunction;
import parktikum.functions.Data;
import parktikum.functions.DataHistory;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by sicher on 24.04.2017.
 */
public class NextZentrale {


    public final static int SEND_BYTES = 1024;

    private HashMap<String, DataHistory> speicher;
    private DatagramSocket socket;
    private ServerSocket server ;
    private int udpPort;
    private int tcpPort;

    public static void main(String args[]) throws SocketException, IOException {
        NextZentrale zentrale = null;
        try {
            if (args.length == 2) {
                zentrale = new NextZentrale(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
            }
        }catch(NumberFormatException e){}
        if(zentrale == null){
            zentrale = new NextZentrale(8880, 47111);
        }
        zentrale.start();

    }

    public NextZentrale(int tcpPort, int udpPort) throws SocketException{
        speicher = new HashMap<>();
        socket = new DatagramSocket(udpPort);
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
    }

    public void start() throws IOException{
        new Thread(new Runnable(){
            @Override
            public void run() {
                acceptTcpConnection();
            }
        }).start();
        receiveSensorData();
    }

    public void receiveSensorData() throws IOException {
        DatagramPacket packet;
        System.out.println("Zentrale hört UDP auf Port " + udpPort);
        while(true){
            packet = new DatagramPacket(new byte[SEND_BYTES], SEND_BYTES);
            socket.receive(packet);
            byte[] dataBytes = packet.getData();
            Data data = new Data();

            //System.out.println("" + packet.getAddress() + "+" + packet.getPort() + "+" + packet.getLength() + "+" + BasicFunction.getSendData(dataBytes, data) + "+" + data);// + new String(packet.getData(), 0, packet.getLength()));

            if(BasicFunction.getSendData(dataBytes, data) != -1){
                String keyString = packet.getAddress() + ":" + packet.getPort() + "-" + data.inhalt.replaceAll("\\s+", "");

                System.out.println("putting " + data + " into " + keyString);
                add(keyString, data);
            }else{
                System.err.println("Data couldn't be made for " + packet.getData() + " from " + packet.getAddress() + ":" + packet.getPort());
            }

        }
    }
    public void acceptTcpConnection(){
        System.out.println("TCP Server started on Port " + tcpPort);
        try{
            //server.setSoTimeout( 60000 ); // Timeout nach 1 Minute
            server = new ServerSocket(tcpPort);
            while(true) {
                Socket client = server.accept();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handleTcpConnection(client);
                    }
                }).start();
                System.out.println("Successful");
            }
        }
        catch ( InterruptedIOException e)  {
            System.err.println( "Timeout nach einer Minute!" );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleTcpConnection(Socket client){
        Scanner in  = null;
        PrintWriter out = null;
        try {
            in = new Scanner( client.getInputStream() );
            out = new PrintWriter( client.getOutputStream(), true );

            String read;
            String get = "";
            while(!(read = in.nextLine()).equals("")){
                if(read.contains("GET")){
                    get = read;
                }
                System.out.println(read);
            }//*/
            //System.out.println("GET: \"" + get.split(" ")[1] + "\"");
            String send = "<html><head><base href=\"/\"><meta charset=\"utf-8\"><meta http-equiv=\"refresh\" content=\"5\"></head><h1>Cooler Kühlschrank</h1><a href=\"/\">Home</a>";
            String key = get.split(" ")[1];
                send += generateHtmlDataHistory(key);
            send +=  "</html>";
            out.println("HTTP/1.1 200 Ok");
            out.println("Content-type: text/html");
            out.println("Content-length: " + send.length());
            out.println("");
            out.println(send);
            client.close();
            System.out.println("Client close");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateGeneralHtmlSensorData(){
        StringBuilder builder = new StringBuilder();
        builder.append("<table border=1>");
        builder.append("<tr><th>Inhalt</th><th>Sequenznummer</th><th>Wert</th></tr>");
        for(String s : speicher.keySet()){
            Data data = speicher.get(s).getNewest();
            builder.append("<tr>");
            builder.append("<td>");
            builder.append("<a href=\"" + s + "\">" + data.inhalt + "</a>");
            builder.append("</td>");
            builder.append("<td>");
            builder.append(data.nummer);
            builder.append("</td>");
            if(data.wert < 50) {
                builder.append("<td bgcolor=\"#FF0000\">");
            }else{
                builder.append("<td>");
            }
            builder.append(data.wert);
            builder.append("</td>");
            builder.append("</tr>");
        }
        builder.append("</table>");
        return builder.toString();
    }

    private String generateHtmlDataHistory(String key){
        DataHistory history = speicher.get(key);
        if(history == null) {
            System.err.println(key + " not found");
            return generateGeneralHtmlSensorData();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("<br/>Sensor: <b>" + key + "</b><br>");
        builder.append("Aktueller Wert: <b>" + history.getNewest() + "</b><br>");
        builder.append("<table border=1>");
        builder.append("<tr><th>Inhalt</th><th>Sequenznummer</th><th>Wert</th></tr>");
        for(Data data : history.getDatas()){
            builder.append("<tr>");
            builder.append("<td>");
            builder.append("" + data.inhalt + "");
            builder.append("</td>");
            builder.append("<td>");
            builder.append(data.nummer);
            builder.append("</td>");
            builder.append("<td>");
            builder.append(data.wert);
            builder.append("</td>");
            builder.append("</tr>");
        }
        builder.append("</table>");
        return builder.toString();
    }


    private void add(String key, Data data){
        if(speicher.get(key) == null){
            speicher.put(key, new DataHistory());
        }
        speicher.get(key).add(data);
    }
}
