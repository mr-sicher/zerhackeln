package parktikum.laden;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import parktikum.functions.Ware;

/**
 * Created by sicher on 11.05.2017.
 */
public class LadenServer {

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
        final LadenServer server = new LadenServer(9090, Ware.FAST_BEER, Ware.BEER, Ware.HAM);
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
    public LadenServer(int port, Ware... waren){
        this.port = port;
        handler = new LadenHandler(waren);
        processor = new Laden.Processor(handler);
    }
    public void start(){
        try {
            TServerTransport serverTransport = new TServerSocket(port);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            System.out.println("Starting Laden on " + ":" + port + "...");
            server.serve();
        }catch(TTransportException e){
            e.printStackTrace();
        }
    }

}
