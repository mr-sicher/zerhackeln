package parktikum.produzent;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import parktikum.functions.MyMqttHandler;
import parktikum.functions.Ware;

import java.util.Random;

/**
 * Created by sicher on 30.05.2017.
 */
public class Produzent extends MyMqttHandler {
    private MqttClient mqttClient;
    private Ware ware;

    public static void main(String args[]){
        final Produzent p;
        if(args.length == 1)
            p = new Produzent(Ware.BEER, args[0], 1883);
        else
            p = new Produzent(Ware.BEER, "localhost", 1883);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    p.generatePrice();
                }
            }
        }).start();

    }


    public Produzent(Ware ware, String ip, int port){
        this.ware = ware;
        System.out.println("Starte MQTT for" + ip + ":" + port);
        mqttClient = makeMqttClient(ip, port);
        subscribe(mqttClient, "Bestellung");
    }

    @Override
    public MqttClient makeMqttClient(String ip, int port){
        try{
            String broker = "tcp://" + ip + ":" + port;
            MemoryPersistence persistence = new MemoryPersistence();
            MqttClient client = new MqttClient(broker, this.toString(), persistence);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            client.connect(options);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    throwable.printStackTrace();
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    parseMqttMessage(mqttMessage);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            return client;

        } catch (MqttSecurityException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void parseMqttMessage(MqttMessage message){
        String parseble = new String(message.getPayload());
        System.out.println("bekam: " + parseble);
        String[] split = parseble.split(";");
        String typ = split[0];
        //Ware ware = new Ware(split[1], Integer.parseInt(split[2]), Integer.parseInt(split[3]));
        String markt = split[1];
        if(typ.equals("bestellung")){
            String produktname = split[2];
            int menge = Integer.parseInt(split[3]);
            String client = split[4];
            if(client.equals(this.toString())){
                if(produktname.equals(ware.getContent())) {
                    String content = AGREE + ";" + markt + ";"
                            + produktname + ";" + ware.getPrice(menge) + ";" + menge + ";" + this.toString();
                    publish(mqttClient, "Bestellung", content);
                }else{
                    System.out.println("I only have: " + ware.getContent());
                }
            }else{
                System.out.println("me: " + this.toString());
            }
        }
    }

    public void generatePrice(){
        String type = "angebot";
        int preis = ware.getFirstPrice();
        Random rand = new Random();
        if(Math.random() < 0.05){
            type = "sonderangebot";
            preis *= 1 + (rand.nextInt(25)-25.0)/100.0;//25% runter
        }else{
            type = "angebot";
            preis *= 1 + (rand.nextInt(10)-5)/200.0;// 5% runter bis 5% rauf
        }
        ware.setPrice(preis);
        String content = type+";" + this.toString() + ";" + ware.getContent() + ";" + ware.getPrice() + ";" + ware.getMeasurement();
        System.out.println("Angebot: " + content);
        publish(mqttClient,"Angebot",content);
    }
}
