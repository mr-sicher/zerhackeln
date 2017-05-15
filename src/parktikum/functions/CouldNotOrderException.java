package parktikum.functions;

/**
 * Created by sicher on 15.05.2017.
 */
public class CouldNotOrderException extends RuntimeException {
    public CouldNotOrderException(String e){
        super(e);
    }
}
