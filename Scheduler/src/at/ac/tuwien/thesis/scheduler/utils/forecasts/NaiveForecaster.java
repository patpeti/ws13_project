package at.ac.tuwien.thesis.scheduler.utils.forecasts;

import java.util.List;

import at.ac.tuwien.thesis.scheduler.enums.DimReductionStrategyType;
import at.ac.tuwien.thesis.scheduler.enums.InterpolationStrategyType;
import at.ac.tuwien.thesis.scheduler.utils.tsutils.DimReductionStrategy;
import at.ac.tuwien.thesis.scheduler.utils.tsutils.InterpolationStrategy;

public class NaiveForecaster {

	/**
	 * *Forecast is the same as the input
	 * @param horizon 
	 */
	
	public List<Double> forecast(List<Double> valueList, Integer dr_factor, Integer horizon) {
		
		DimReductionStrategy dimreduction = new DimReductionStrategy(DimReductionStrategyType.AVG);
		List<Double> reduced = dimreduction.dimReduce(valueList,dr_factor);
		
		InterpolationStrategy interpolation = new InterpolationStrategy(InterpolationStrategyType.PAA);
//		InterpolationStrategy interpolation = new InterpolationStrategy(InterpolationStrategyType.PLA);
		List<Double> interpolated = interpolation.interPolate(reduced,dr_factor);
		return interpolated;
	}

	
	

	

	
}
