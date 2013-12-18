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

public class FourierForecaster {
	
	private Integer dataSize;
	private Integer dr_factor;
	
	/**
	 * 			x1 <- ts(oneweek,start=0,frequency=1440)
	 * 			X <- fourier(x1,5)
	 *			lm  <- tslm(x1 ~ X)
	 *			fcast <- forecast(lm, data.frame(X=I(fourierf(x1,5,10080))))
	 *			plot(fcast)
	 *
	 */

	public List<Double> forecast(List<Double> valueList, Integer dr_factor) {
		this.dataSize = valueList.size();
		this.dr_factor = dr_factor;
		DimReductionStrategy dimreduction = new DimReductionStrategy(DimReductionStrategyType.AVG);
		List<Double> reduced = dimreduction.dimReduce(valueList,dr_factor);
		
		List<Double> forecasted = this.fourierforecast(reduced);
		
		InterpolationStrategy interpolation = new InterpolationStrategy(InterpolationStrategyType.PAA);
		List<Double> interpolated = interpolation.interPolate(forecasted,dr_factor);
		return interpolated;
	}

	private List<Double> fourierforecast(List<Double> reduced) {
		
			Rengine re= REngineSingleton.getREngine();
		
			Integer frequency = Constants.DataPerDay/dr_factor;
			Integer horizon = (int) Math.ceil(Constants.DataPerWeek/dr_factor);
			Integer reducedSize = (int) Math.ceil(dataSize/dr_factor);
			System.out.println("***********  DEBUG OUTPUT  ***********");
			System.out.println(" frequency: "+ frequency + " horizon: "+ horizon + " reducedSize: "+ reducedSize);
			
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
				
				re.eval("x2 <- ts(x1,start=0,frequency="+horizon+")");
				re.eval("X <- fourier(x2,10)");  // 5:  Maximum order of Fourier terms
				re.eval("lm  <- tslm(x2 ~ X)");
				re.eval("fcast <- forecast(lm, data.frame(X=I(fourierf(x2,10,"+horizon+"))))");
				re.eval("x4 <- data.frame(fcast)");
							
									
				re.eval("x5 <- x4[,1]");
				System.out.println(x=re.eval("x5"));
				System.out.println("ME \t    RMSE\t      MAE\t MPE\t MAPE\t     MASE \t     ACF1");
				System.out.println(re.eval("accuracy(forecast(lm, data.frame(X=I(fourierf(x2,10,"+horizon+")))),test=\"all\")"));
				
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
