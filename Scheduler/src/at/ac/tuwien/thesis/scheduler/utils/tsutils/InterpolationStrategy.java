package at.ac.tuwien.thesis.scheduler.utils.tsutils;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.thesis.scheduler.enums.InterpolationStrategyType;

public class InterpolationStrategy {

	private InterpolationStrategyType type;

	public InterpolationStrategy(InterpolationStrategyType type){
		this.type = type;
	}
	
	public List<Double> interPolate(List<Double> reduced, Integer dr_Factor){
		if(this.type.equals(InterpolationStrategyType.PAA)){
			return this.interPolatePAA(reduced, dr_Factor);
		}else{
			return this.interPolatePLA(reduced, dr_Factor);
		}
	}
	
	
	private List<Double> interPolatePAA(List<Double> reduced, Integer dr_factor) {
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
	
	private List<Double> interPolatePLA(List<Double> reduced, Integer dr_factor) {
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
}
