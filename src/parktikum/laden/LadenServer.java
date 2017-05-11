package parktikum.laden;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Created by sicher on 11.05.2017.
 */
public class LadenServer {

    public static void main(String[] args){
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
        }catch(Exception e){
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
    }

}
