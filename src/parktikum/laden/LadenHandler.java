package parktikum.laden;

import org.apache.thrift.TException;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import parktikum.functions.Angebot;
import parktikum.functions.MyMqttHandler;
import parktikum.functions.Order;
import parktikum.functions.Ware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sicher on 11.05.2017.
 */
public class LadenHandler extends MyMqttHandler implements Laden.Iface {

    private HashMap<String, Ware> waren;
    private Map<String, ArrayList<Order>> orders;
    private Map<String, Angebot> angebote;
    private MqttClient mqttClient;

    public LadenHandler(String ip, int port, Ware... warens){
        this.waren = new HashMap<>();
        this.angebote = new HashMap<>();
        orders = new HashMap<>();
        for(Ware w : warens){
            this.waren.put(w.getContent(), w);
        }

        mqttClient = makeMqttClient(ip, port);
        subscribe(mqttClient, "Angebot");
        subscribe(mqttClient, "Bestellung");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true){
                        Thread.sleep((int)(Math.random() * 10000));
                        for (String art : waren.keySet()) {
                            reorder(art, 10);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public double add(String customer, String article, int amount) throws TException {
        if(!orders.containsKey(customer))
            orders.put(customer, new ArrayList<Order>());
        if(!waren.containsKey(article))
            return -1;
        Ware ware = waren.get(article);
        double price = ware.getPrice(amount);
        Order order = new Order(ware, amount);
        orders.get(customer).add(order);
        System.out.println(customer + " bought " + order.getWare().getContent() + " for " + order.getWare().getPrice(order.getAmount()) + "€. ");
        return price;
    }

    @Override
    public double getPrice(String article) throws TException {
        System.out.println("somebody asked for " + article);
        if(!waren.containsKey(article))
            return -1;
        return waren.get(article).getPrice();
    }
    @Override
    public double getPriceFor(String article, int amount) throws TException {
        System.out.println("somebody asked for " + article + " " + amount);
        if(!waren.containsKey(article))
            return -1;
        return waren.get(article).getPrice(amount);
    }

    @Override
    public List<String> getOrders(String costumer) throws TException {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<Order> costumersOrder = orders.get(costumer);
        for(Order order : costumersOrder){
            result.add(order.toString());
        }
        return result;
    }


    @Override
    public MqttClient makeMqttClient(String ip, int port) {
        String broker = "tcp://"+ip+":" + port;
        MemoryPersistence persistence = new MemoryPersistence();


        try{
            MqttClient sampleClient = new MqttClient(broker, this.toString(), persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            sampleClient.connect(connOpts);
            sampleClient.setCallback(new MqttCallback() {
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
            return sampleClient;
        } catch(MqttException me) {
            me.printStackTrace();
        }
        return null;
    }

    @Override
    public void parseMqttMessage(MqttMessage message) {
        final String toParse = new String(message.getPayload());

        String[] split = toParse.split(";");
        String typ = split[0];
        String erzeuger = split[1];
        String produkt = split[2];
        double preis = Double.parseDouble(split[3]);

        switch (typ){
            case "sonderangebot":
            case "angebot":
                System.out.println("Bekommen: " + toParse);
                if(!angebote.containsKey(produkt)){
                    angebote.put(produkt, new Angebot());
                }
                angebote.get(produkt).add(erzeuger,preis);
                break;
            case AGREE:
                String markt = erzeuger;
                int menge = Integer.parseInt(split[4]);
                if(markt.equals(this.toString())){
                    System.out.println("bekommen: " + toParse);
                    //int neueMenge = waren.get(produkt).getMenge() + menge;
                    //waren.get(produkt).setMenge(neueMenge);
                    System.err.println("Preis int " + (preis*menge));
                    waren.get(produkt).setPrice((int)(preis*menge) + 10);
                    System.out.println("Bestellung durchgeführt und Bestand aufgefüllt.");
                }

            default:
                break;
        }
    }
    public void reorder(String ware, int amount){
        System.out.println("reorder " + ware);
        if(angebote.containsKey(ware)) {
            String erzeuger = angebote.get(ware).getCheapest();
            String content = "bestellung;" + this.toString() + ";" + ware + ";" + amount + ";" + erzeuger;

            publish(mqttClient, "Bestellung", content);
        }else{
            System.out.println("couldn't reorder " + ware);
        }
    }
}
