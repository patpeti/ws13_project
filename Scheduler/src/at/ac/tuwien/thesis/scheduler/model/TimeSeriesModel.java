package at.ac.tuwien.thesis.scheduler.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TimeSeriesModel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9213543600525180059L;
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
