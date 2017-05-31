package parktikum.functions;

/**
 * Created by sicher on 15.05.2017.
 */
public class Ware {
    public static final Ware BEER = new Ware("Bier", 99, 500);
    public static final Ware FAST_BEER = new Ware("Schnelles Bier", 99, 500);
    public static final Ware HAM = new Ware("Wurst", 250, 250);
    public static final Ware COKE = new Ware("Cola", 99, 330);
    public static final Ware BUTTER = new Ware("Butter", 100);



    private int price;
    private final int firstPrice;
    private int measurement = 100;
    private String content;
    public Ware(String content, int price){
        this.content = content;
        this.price = price;
        this.firstPrice = price;
    }
    public Ware(String content, int price, int measurement){
        this.content = content;
        this.price = price;
        this.measurement = measurement;
        this.firstPrice = price;
    }

    public int getPrice(){
        return price;
    }
    public double getPrice(int amount){
        return price * (1.0*amount) / measurement;
    }
    public String getContent(){
        return content;
    }

    @Override
    public String toString() {
        return "Ware{" +
                "price=" + price +
                ", measurement=" + measurement +
                ", content='" + content + '\'' +
                '}';
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getMeasurement() {
        return measurement;
    }

    public int getFirstPrice() {
        return firstPrice;
    }
}