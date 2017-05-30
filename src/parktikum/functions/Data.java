package parktikum.functions;

/**
 * Created by sicher on 05.04.2017.
 */
public class Data {

	public String inhalt;
	public int nummer;
	public int wert;
	
	public Data(String inhalt, int nummer, int wert){
		this.inhalt = inhalt;
		this.nummer = nummer;
		this.wert = wert;
	}
	public Data(){
	    this(null, 0, 0);
    }

 	public String toString(){
	    return inhalt + " " + nummer + " " + wert;

    }
}
