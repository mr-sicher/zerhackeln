package parktikum.zentrale;

import java.util.ArrayList;

public class DataHistory{

	private Data max;
	private ArrayList<Data> datas;
	public DataHistory(){
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
}
