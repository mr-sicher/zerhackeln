package parktikum.laden;

import org.apache.thrift.TException;
import parktikum.functions.Order;
import parktikum.functions.Ware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sicher on 11.05.2017.
 */
public class LadenHandler implements Laden.Iface {

    private Map<String, Ware> waren;
    private Map<String, ArrayList<Order>> orders;

    public LadenHandler(Ware... waren){
        this.waren = new HashMap<>();
        orders = new HashMap<>();
        for(Ware w : waren){
            this.waren.put(w.getContent(), w);
        }
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

    @Override
    public List<String> getOrders(String costumer) throws TException {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<Order> costumersOrder = orders.get(costumer);
        for(Order order : costumersOrder){
            result.add(order.toString());
        }
        return result;
    }


}
