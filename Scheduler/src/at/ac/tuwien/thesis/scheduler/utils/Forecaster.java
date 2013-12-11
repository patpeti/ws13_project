package at.ac.tuwien.thesis.scheduler.utils;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.thesis.scheduler.enums.ForecastType;

public class Forecaster {

	public List<Double> calculateForecast(List<Double> valueList,ForecastType forecastType) {
		if(forecastType.equals(ForecastType.NAIVE)){
			return valueList;
		}else if(forecastType.equals(ForecastType.NNAR)){
			return null;
		}else if(forecastType.equals(ForecastType.HOLTWINTERS)){
			return null;
		}else{
			return null;
		}
	}

}
