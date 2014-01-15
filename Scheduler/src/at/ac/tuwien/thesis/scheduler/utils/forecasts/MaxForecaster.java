package at.ac.tuwien.thesis.scheduler.utils.forecasts;

import java.util.ArrayList;
import java.util.List;

public class MaxForecaster {

	
	public List<Double> forecast(List<Double> valueList, Integer dr_factor, Integer horizon) {
		
		Double max = this.calculateMax(valueList);
		List<Double> interpolated = new ArrayList<Double>();
		for(int i = 0; i<valueList.size();i++){
			interpolated.add(max);
		}
		return interpolated;
	}

	private Double calculateMax(List<Double> temp) {
		Double max = new Double(0);
		
		for(Double d : temp){
			if(d>max) max=d;
		}
		return max;
	}

	
}
