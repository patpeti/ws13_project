package at.ac.tuwien.thesis.scheduler.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeSeriesHolder implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8938941879625456580L;
	Map<String,List<Double>> ts;
	
	public TimeSeriesHolder(){
		List<Double> series = new ArrayList<Double>();
		ts = new HashMap<String,List<Double>>();
	}
	
	public void AddSeries(String dimensionName, List<Double> series){
		
		this.ts.put(dimensionName, series);
	}
	
	public Map<String,List<Double>> getTs(){
		return this.ts;
	}
	
	public List<Double> getDimension(String dimensionName){
		return this.ts.get(dimensionName);
	}
	
	public void addDimension(String dim){
		ts.put(dim, new ArrayList<Double>());
	}
	public void addDimValue(String dim, Double value){
		ts.get(dim).add(value);
	}

}
