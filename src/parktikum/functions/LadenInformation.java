package parktikum.functions;

/**
 * Created by sicher on 15.05.2017.
 */
public class LadenInformation {
    private String ip;
    private int port;
    public LadenInformation(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
