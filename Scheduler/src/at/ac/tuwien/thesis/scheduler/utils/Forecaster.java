package at.ac.tuwien.thesis.scheduler.utils;

import java.util.List;

import at.ac.tuwien.thesis.scheduler.enums.ForecastType;
import at.ac.tuwien.thesis.scheduler.utils.forecasts.NaiveForecaster;

public class Forecaster {

	public List<Double> calculateForecast(List<Double> valueList,ForecastType forecastType, Integer dr_factor) {
		if(forecastType.equals(ForecastType.NAIVE)){
			NaiveForecaster naive = new NaiveForecaster();
			return naive.forecast(valueList,dr_factor);
		}else if(forecastType.equals(ForecastType.NNAR)){
			return null;
		}else if(forecastType.equals(ForecastType.HOLTWINTERS)){
			return null;
		}else{
			return null;
		}
	}

	

}
