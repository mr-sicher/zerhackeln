package parktikum.functions;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by sicher on 30.05.2017.
 */
public abstract class MyMqttHandler {
    public final static String AGREE = "agree";
    public void subscribe(MqttClient client, String topic){
        try {
            System.out.println("Subscribe to " + topic);
            client.subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void publish(MqttClient client, String topic, String message){
        try{
            System.out.println("Publish " + message);
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(2);
            client.publish(topic, mqttMessage);
        } catch (MqttPersistenceException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public abstract MqttClient makeMqttClient(String ip, int port);

    public abstract void parseMqttMessage(MqttMessage message);
}
