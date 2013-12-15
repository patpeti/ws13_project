package at.ac.tuwien.thesis.scheduler.utils;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.thesis.scheduler.enums.ForecastType;

public class Forecaster {

	public List<Double> calculateForecast(List<Double> valueList,ForecastType forecastType, Integer dr_factor) {
		if(forecastType.equals(ForecastType.NAIVE)){
			return naiveForecast(valueList,dr_factor);
		}else if(forecastType.equals(ForecastType.NNAR)){
			return null;
		}else if(forecastType.equals(ForecastType.HOLTWINTERS)){
			return null;
		}else{
			return null;
		}
	}

	private List<Double> naiveForecast(List<Double> valueList, Integer dr_factor) {
		List<Double> reduced = dimReduce(valueList,dr_factor);
		List<Double> interpolated = interPolate2(reduced,dr_factor);
		return interpolated;
	}
	
	private List<Double> interPolate(List<Double> reduced, Integer dr_factor) {
		List<Double> interpolated = new ArrayList<Double>();
		
		int i=0;
		for(Double d : reduced){
			for(int j = 0; j < dr_factor; j++){
				interpolated.add(d);
				
			}
			i++;
		}
		
		return interpolated;
	}

	private List<Double> interPolate2(List<Double> reduced, Integer dr_factor) {
		List<Double> interpolated = new ArrayList<Double>();
		
		int i=0;
		for(Double d : reduced){
			for(int j = 0; j < dr_factor; j++){
				double first = reduced.get(i);
				double next = 5;
				
				 try {
					next = reduced.get(i+1);
				} catch (IndexOutOfBoundsException e) {
					next= reduced.get(i);
				}
				double step = (first-next)/(double)dr_factor;
				interpolated.add(first+j*step);
			}
			i++;
		}
		
		return interpolated;
	}

	private List<Double> dimReduce(List<Double> valueList, Integer dr_factor) {
		// TODO More methods.. this is PPA

		List<Double> reduced = new ArrayList<Double>();
		int i = 0;
		while(i < valueList.size()){
			List<Double> temp = new ArrayList<Double>();
			for(int j = 0; j < dr_factor; j++){
				try{
				temp.add(valueList.get(i+j));
				}catch(IndexOutOfBoundsException e){
					//no problem actually
				}
			}
			reduced.add(calculateMean(temp));
			i=i+dr_factor;
		}
		return reduced;
		
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
