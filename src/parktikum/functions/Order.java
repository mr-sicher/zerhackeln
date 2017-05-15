package parktikum.functions;

/**
 * Created by sicher on 15.05.2017.
 */

public class Order{
    private Ware ware;
    private int amount;
    public Order(Ware ware, int amount){
        this.ware = ware;
        this.amount = amount;
    }

    public Ware getWare() {
        return ware;
    }

    public int getAmount() {
        return amount;
    }
}

