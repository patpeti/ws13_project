package at.ac.tuwien.thesis.scheduler.utils;

import java.util.List;

import at.ac.tuwien.thesis.scheduler.enums.ForecastType;
import at.ac.tuwien.thesis.scheduler.utils.forecasts.MaxForecaster;
import at.ac.tuwien.thesis.scheduler.utils.forecasts.MeanForecaster;
import at.ac.tuwien.thesis.scheduler.utils.forecasts.NNARForecaster;
import at.ac.tuwien.thesis.scheduler.utils.forecasts.NaiveForecaster;

public class Forecaster {

	public List<Double> calculateForecast(List<Double> valueList,ForecastType forecastType, Integer dr_factor) {
		if(forecastType.equals(ForecastType.NAIVE)){
			NaiveForecaster naive = new NaiveForecaster();
			return naive.forecast(valueList,dr_factor);
		}else if(forecastType.equals(ForecastType.NNAR)){
			NNARForecaster nnar = new NNARForecaster();
			return nnar.forecast(valueList,dr_factor);
		}else if(forecastType.equals(ForecastType.MEAN)){
			MeanForecaster mean = new MeanForecaster();
			return mean.forecast(valueList, dr_factor);
		}else if(forecastType.equals(ForecastType.MAX)){
			MaxForecaster max = new MaxForecaster();
			return max.forecast(valueList, dr_factor);
		}else if(forecastType.equals(ForecastType.MEAN)){
			return null;
		}else if(forecastType.equals(ForecastType.MEAN)){
			return null;
		}else if(forecastType.equals(ForecastType.MEAN)){
			return null;
		}else if(forecastType.equals(ForecastType.MEAN)){
			return null;
		}else if(forecastType.equals(ForecastType.MEAN)){
			return null;
		}else{
			return null;
		}
	}

	

}
