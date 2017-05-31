package parktikum.functions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by sicher on 30.05.2017.
 */
public class Angebot {

    Map<String, Double> anbieter = new HashMap<>();

    public Angebot() {
    }

    public void add(String erzeuger, double preis){
        anbieter.put(erzeuger,preis);
    }

    public String getCheapest(){
        Set<String> keys = anbieter.keySet();
        double minpreis = Integer.MAX_VALUE;
        String billigsterAnbieter = "";
        for (String key: keys) {
            if(anbieter.get(key) < minpreis){
                billigsterAnbieter= key;
                minpreis = anbieter.get(key);
            }
        }
        return billigsterAnbieter;
    }

}
