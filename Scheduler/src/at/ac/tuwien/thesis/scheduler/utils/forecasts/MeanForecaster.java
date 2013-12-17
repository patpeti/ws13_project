package at.ac.tuwien.thesis.scheduler.utils.forecasts;

import java.util.ArrayList;
import java.util.List;

public class MeanForecaster {

	
	public List<Double> forecast(List<Double> valueList, Integer dr_factor) {
		
		Double mean = this.calculateMean(valueList);
		
	
		List<Double> interpolated = new ArrayList<Double>();
		for(int i = 0; i<valueList.size();i++){
			interpolated.add(mean);
		}
		return interpolated;
	}


	private Double calculateMean(List<Double> temp) {
		Double sum = new Double(0);
		int num = 0;
		for(Double d : temp){
			sum += d;
			num++;
		}
		return (new Double(sum) / new Double(num));
	}

	
}
