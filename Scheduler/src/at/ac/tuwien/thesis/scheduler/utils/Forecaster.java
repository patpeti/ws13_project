package at.ac.tuwien.thesis.scheduler.utils;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.thesis.scheduler.enums.ForecastType;

public class Forecaster {

	public List<Double> calculateForecast(List<Double> dimension,ForecastType forecastType) {
		if(forecastType.equals(ForecastType.NONE)){
			List<Double> liste = new ArrayList<Double>();
			liste.add(new Double(3));
			liste.add(new Double(3));
			liste.add(new Double(3));
			liste.add(new Double(3));
			liste.add(new Double(3));
			liste.add(new Double(3));
			liste.add(new Double(3));
			liste.add(new Double(3));
			liste.add(new Double(3));
			liste.add(new Double(3));
			liste.add(new Double(3));
			liste.add(new Double(3));
			liste.add(new Double(3));
			liste.add(new Double(3));
			liste.add(new Double(3));
			liste.add(new Double(3));
			liste.add(new Double(3));
			liste.add(new Double(3));
			
			return liste;
		}else{
			return null;
		}
	}

}
