package at.ac.tuwien.thesis.scheduler.model;

import java.util.HashMap;
import java.util.Map;

public class TimeSeriesModel {
	
	Map<String,TimeSeriesHolder> tsModelMap;

	public TimeSeriesModel(){
		tsModelMap = new HashMap<String,TimeSeriesHolder>();
	}

	public Map<String, TimeSeriesHolder> getTsModel() {
		return tsModelMap;
	}
	
	public TimeSeriesHolder getTsForName(String name) {
		return tsModelMap.get(name);
	}
	
	public void addNewTimeSeries(String name,TimeSeriesHolder ts){
		this.getTsModel().put(name, ts);
	}
	
	
}
