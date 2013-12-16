package at.ac.tuwien.thesis.scheduler.utils.forecasts;

import java.util.Enumeration;
import java.util.List;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.Rengine;

import at.ac.tuwien.thesis.scheduler.enums.DimReductionStrategyType;
import at.ac.tuwien.thesis.scheduler.enums.InterpolationStrategyType;
import at.ac.tuwien.thesis.scheduler.utils.forecasts.rutil.REngineSingleton;
import at.ac.tuwien.thesis.scheduler.utils.forecasts.rutil.TextConsole;
import at.ac.tuwien.thesis.scheduler.utils.tsutils.DimReductionStrategy;
import at.ac.tuwien.thesis.scheduler.utils.tsutils.InterpolationStrategy;

public class NNARForecaster {

	public List<Double> forecast(List<Double> valueList, Integer dr_factor) {
		DimReductionStrategy dimreduction = new DimReductionStrategy(DimReductionStrategyType.AVG);
		List<Double> reduced = dimreduction.dimReduce(valueList,dr_factor);
		
		List<Double> forecasted = this.nnar(reduced);
		
		InterpolationStrategy interpolation = new InterpolationStrategy(InterpolationStrategyType.PAA);
		List<Double> interpolated = interpolation.interPolate(forecasted,dr_factor);
		return interpolated;
	}

	private List<Double> nnar(List<Double> reduced) {
		
			Rengine re= REngineSingleton.getREngine();
		
	        
			try {
				System.out.println (re.eval ("runif(1)").asDouble ());
		        
			}catch(Exception e){
				System.out.println("EX:"+e);
				e.printStackTrace();
			}
	        
	        return reduced;
	}

	

}
