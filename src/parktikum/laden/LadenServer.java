package parktikum.laden;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import parktikum.functions.Ware;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * Created by sicher on 11.05.2017.
 */
public class LadenServer {


    public final static int MAX_PRiCE = 10000;
    public final static int MIN_PRICE = 10;
    /*public static void main(String[] args){
        LadenHandler handler;
        Laden.Processor processor;
        try{
            handler = new LadenHandler();
            processor = new Laden.Processor(handler);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    doSomething(processor);
                }
            }).start();
        }catch(Exception e) {
            //fange alles
            e.printStackTrace();
        }
    }
    public static void doSomething(Laden.Processor processor){
        try{
            TServerTransport serverTransport = new TServerSocket(9090);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

            System.out.println("Starting the simple server...");
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }*/

    public static void main(String[] args){
        final LadenServer server;
        Random rand = new Random();
        if(args.length == 0)
            server = new LadenServer("localhost", 1883,9091, new Ware("Schnelles Bier", rand.nextInt(MAX_PRiCE-MIN_PRICE) + MIN_PRICE),
                    new Ware("Bier", rand.nextInt(MAX_PRiCE-MIN_PRICE) + MIN_PRICE),
                    new Ware("Wurst", rand.nextInt(MAX_PRiCE-MIN_PRICE) + MIN_PRICE));
        else
            server = new LadenServer(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]),  Ware.FAST_BEER, Ware.BEER, Ware.HAM);
        new Thread(new Runnable() {
            @Override
            public void run() {
                server.start();
            }
        }).start();
        //notify of the new shop ??
    }

    private int port;
    private LadenHandler handler;
    private Laden.Processor processor;
    public LadenServer(String brokerIp, int brokerPort, int port, Ware... waren){
        for(Ware w : waren){
            System.out.println(w);
        }
        this.port = port;
        handler = new LadenHandler(brokerIp, brokerPort, waren);
        processor = new Laden.Processor(handler);
    }
    public void start(){
        try {
            TServerTransport serverTransport = new TServerSocket(port);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            try {
                System.out.println("Starting Laden on " + InetAddress.getLocalHost() + ":" + port + "...");
            }catch(UnknownHostException e){
                e.printStackTrace();
            }
            server.serve();
        }catch(TTransportException e){
            e.printStackTrace();
        }
    }

}
