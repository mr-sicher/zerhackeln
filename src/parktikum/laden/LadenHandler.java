package parktikum.laden;

import org.apache.thrift.TException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sicher on 11.05.2017.
 */
public class LadenHandler implements Laden.Iface {

    private Map<String, Ware> waren;
    private Map<String, List<Order>> orders;

    public LadenHandler(Ware... waren){
        this.waren = new HashMap<>();
        orders = new HashMap<>();
        for(Ware w : waren){
            this.waren.put(w.content, w);
        }
    }

    @Override
    public double add(String customer, String article, int amount) throws TException {
        if(!orders.containsKey(customer))
            orders.put(customer, new ArrayList<>());
        if(!waren.containsKey(article))
            return -1;
        Ware ware = waren.get(article);
        double price = ware.getPrice(amount);
        orders.get(customer).add(new Order(ware, amount));
        return price;
    }

    @Override
    public double getPrice(String article) throws TException {
        if(!waren.containsKey(article))
            return -1;
        return waren.get(article).getPrice();
    }
    @Override
    public double getPriceFor(String article, int amount) throws TException {
        if(!waren.containsKey(article))
            return -1;
        return waren.get(article).getPrice(amount);
    }

    public class Order{
        public Ware ware;
        public int amount;
        public Order(Ware ware, int amount){
            this.ware = ware;
            this.amount = amount;
        }
    }

    public class Ware {
        public int price;
        public int measurement = 100;
        public String content;

        public double getPrice(){
            return price;
        }
        public double getPrice(int amount){
            return price * (1.0*amount) / measurement;
        }
    }
}
