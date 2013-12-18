package at.ac.tuwien.thesis.scheduler.utils.forecasts;

import java.util.ArrayList;
import java.util.List;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

import at.ac.tuwien.thesis.scheduler.Constants;
import at.ac.tuwien.thesis.scheduler.enums.DimReductionStrategyType;
import at.ac.tuwien.thesis.scheduler.enums.InterpolationStrategyType;
import at.ac.tuwien.thesis.scheduler.utils.forecasts.rutil.REngineSingleton;
import at.ac.tuwien.thesis.scheduler.utils.tsutils.DimReductionStrategy;
import at.ac.tuwien.thesis.scheduler.utils.tsutils.InterpolationStrategy;

public class TBATSForecaster {
	
	private Integer dataSize;
	private Integer dr_factor;

	public List<Double> forecast(List<Double> valueList, Integer dr_factor) {
		this.dataSize = valueList.size();
		this.dr_factor = dr_factor;
		DimReductionStrategy dimreduction = new DimReductionStrategy(DimReductionStrategyType.AVG);
		List<Double> reduced = dimreduction.dimReduce(valueList,dr_factor);
		
		List<Double> forecasted = this.hw(reduced);
		
		InterpolationStrategy interpolation = new InterpolationStrategy(InterpolationStrategyType.PAA);
		List<Double> interpolated = interpolation.interPolate(forecasted,dr_factor);
		return interpolated;
	}

	private List<Double> hw(List<Double> reduced) {
		
			Rengine re= REngineSingleton.getREngine();
		
			Integer frequency = Constants.DataPerDay/dr_factor;
			Integer weekfrequency = frequency*7;
			Integer horizon = (int) Math.ceil(Constants.DataPerWeek/dr_factor);
			Integer reducedSize = (int) Math.ceil(dataSize/dr_factor);
			System.out.println("***********  DEBUG OUTPUT  ***********");
			System.out.println(" frequency: "+ frequency + " horizon: "+ horizon + " reducedSize: "+ reducedSize+ " weekfreq: " + weekfrequency);
			
			double [] reducedArray = new double[reducedSize];
			for(int i = 0; i < reducedSize; i++ ){
				reducedArray[i] = reduced.get(i);
			}
			
			double [] forecastArray = new double[horizon];
			List<Double> forecastListe = new ArrayList<Double>();
			
			try {
				REXP x;
				re.assign("x", reducedArray);
				re.eval("x1 <- data.frame(x)");
				System.out.println("x2 <- msts(x1, seasonal.periods=c("+frequency+","+reducedSize+"), ts.frequency="+reducedSize+", start=0)");
				re.eval("x2 <- msts(x1, seasonal.periods=c("+frequency+","+reducedSize+"), ts.frequency="+reducedSize+", start=0)");
				System.out.println("x3 <- tbats(x2, use.trend=NULL, seasonal.periods=c("+frequency+","+reducedSize+"), bc.lower=0,bc.upper=100)");
				re.eval("x3 <- tbats(x2, use.trend=NULL, seasonal.periods=c("+frequency+","+reducedSize+"), bc.lower=0,bc.upper=100)");
				System.out.println("x4 <- data.frame(forecast(x3,"+horizon+"))");
				re.eval("x4 <- data.frame(forecast(x3,"+horizon+"))");
				
				re.eval("x5 <- x4[,1]");
				System.out.println(x=re.eval("x5"));
				System.out.println("ME \t    RMSE\t      MAE\t MPE\t MAPE\t     MASE \t     ACF1");
				System.out.println(re.eval("accuracy(forecast(x3,h="+horizon+"),test=\"all\")"));
				
				forecastArray = x.asDoubleArray();
			}catch(Exception e){
				System.out.println("EX:"+e);
				e.printStackTrace();
			}
			
			
			for(int i = 0; i < forecastArray.length; i++ ){
				forecastListe.add(forecastArray[i]);
			}
			
			if(!forecastListe.isEmpty())	        
	        return forecastListe;
			else return reduced;
	}

	

}
