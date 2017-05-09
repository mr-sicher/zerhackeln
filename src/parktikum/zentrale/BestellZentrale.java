package parktikum.zentrale;

import parktikum.functions.AmountException;
import parktikum.functions.BasicFunction;
import parktikum.functions.Data;
import parktikum.functions.DataHistory;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by sicher on 24.04.2017.
 */
public class BestellZentrale {


    public final static int SEND_BYTES = 1024;
    public final static String HTTP_SENSOR = "/sensor";
    public final static String HTTP_BESTELLUNG = "/bestellung";

    private HashMap<String, DataHistory> speicher;
    private DatagramSocket socket;
    private ServerSocket server ;
    private int udpPort;
    private int tcpPort;

    public static void main(String args[]) throws SocketException, IOException {
        BestellZentrale zentrale = null;
        try {
            if (args.length == 2) {
                zentrale = new BestellZentrale(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
            }
        }catch(NumberFormatException e){}
        if(zentrale == null){
            zentrale = new BestellZentrale(8880, 47111);
        }
        zentrale.start();

    }

    public BestellZentrale(int tcpPort, int udpPort) throws SocketException{
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
                final Socket client = server.accept();
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
            String host = "";
            while(!(read = in.nextLine()).equals("")){
                if(read.contains("GET")){
                    get = read;
                }
                if(read.contains("Host:")){
                    host = read.split(" ")[1];
                }
                System.out.println(read);
            }//*/
            String send;
            //System.out.println("GET: \"" + get.split(" ")[1] + "\"");
            if(!get.contains(".ico")) {
                try {
                     send = "<html><head><base href=\"/\"><meta charset=\"utf-8\"><meta http-equiv=\"refresh\" content=\"5;\"></head><h1>Cooler Kühlschrank</h1><a href=\"/\">Home</a>";
                    String key = get.split(" ")[1];
                    if (key.contains(HTTP_SENSOR)) {
                        key = key.substring(HTTP_SENSOR.length());
                        send += generateHtmlDataHistory(key);
                    } else if(key.contains(HTTP_BESTELLUNG)){
                        key = key.substring(HTTP_BESTELLUNG.length()+1);
                        order(key.split("/")[0], Integer.parseInt(key.split("/")[1]));
                        send = "<html><head><meta charset=\"utf-8\"><meta http-equiv=\"refresh\" content=\"1; URL=http://" + host + "\"></head><body></body>";
                    } else {
                        if (key.equals("/"))
                            send += generateGeneralHtmlSensorData();
                        else
                            throw new IllegalArgumentException(key + " not found");
                    }
                    send += "</html>";
                    out.println("HTTP/1.1 200 Ok");
                    out.println("Content-type: text/html");
                    out.println("Content-length: " + send.length());
                    out.println("");
                    out.println(send);
                } catch (IllegalArgumentException e) {
                    out.println("HTTP/1.1 451 Unavailable For Legal Reasons");
                    out.println("Content-type: text/html");
                    out.println("Content-length: " + 0);
                }
            }else{
                //send favicon.ico
                out.println("HTTP/1.1 200 Ok");
                out.println("Content-type: image/vnd.microsoft.icon");
                out.println("Content-length: 0");
                out.println("");
                out.println("");
            }//*/
            out.close();
            in.close();
            client.close();
            System.out.println("Client close");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateGeneralHtmlSensorData(){
        StringBuilder builder = new StringBuilder();
        builder.append("<table border=1>");
        builder.append("<tr><th>Inhalt</th><th>Sequenznummer</th><th>Wert</th><th>Bestellen</th></tr>");
        for(String s : speicher.keySet()){
            Data data = speicher.get(s).getNewest();
            builder.append("<tr>");
            builder.append("<td>");
            builder.append("<a href=\"" + HTTP_SENSOR + s + "\">" + data.inhalt + "</a>");
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
            builder.append("<td>");
            builder.append("<a href=\"" + HTTP_BESTELLUNG + s + "/100" + "\">" + "100 Bestellen" + "</a>");
            builder.append("</td>");
            builder.append("</tr>");
        }
        builder.append("</table>");
        return builder.toString();
    }

    private String generateHtmlDataHistory(String key){
        DataHistory history = speicher.get(key);
        if(history == null) {
            throw new IllegalArgumentException(key + " not found");
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

    private void order(String sensorname, int amount) {
        if(amount > 127) {
            order(sensorname, amount -127);
            amount = 127;
        }
        String ip, port;
        String name = sensorname.split("-")[0];
        ip = name.split(":")[0];
        port = name.split(":")[1];
        try {
            byte[] data = {(byte)amount};
            DatagramPacket p = new DatagramPacket(data, data.length,InetAddress.getByName(ip), Integer.parseInt(port));
            socket.send(p);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
