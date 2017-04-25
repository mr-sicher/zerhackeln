package parktikum.functions;

import java.util.ArrayList;

/**
 * Created by sicher on 05.04.2017.
 */
public class DataHistory{

	private Data max;
	private ArrayList<Data> datas;
	public DataHistory(){
		max = null;
		datas = new ArrayList<>();
	}
	public void add(Data data){
		if(max == null){
			max = data;
		}
		if(max.nummer < data.nummer)
			max = data;
		datas.add(data);
	}
	public Data getNewest(){
		return max;
	}
	public String dump(){
		return datas.toString();
	}
	public ArrayList<Data> getDatas(){
		return datas;
	}
}
