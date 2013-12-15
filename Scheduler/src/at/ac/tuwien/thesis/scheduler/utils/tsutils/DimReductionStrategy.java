package at.ac.tuwien.thesis.scheduler.utils.tsutils;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.thesis.scheduler.enums.DimReductionStrategyType;

public class DimReductionStrategy {

	private DimReductionStrategyType type;

	public DimReductionStrategy(DimReductionStrategyType type) {

		this.type = type;
	}

	public List<Double> dimReduce(List<Double> valueList, Integer dr_factor) {
		if(this.type.equals(DimReductionStrategyType.AVG)){
			return this.dimReduceAVG(valueList, dr_factor);
		}else{
			//TODO more strategy
			return this.dimReduceAVG(valueList, dr_factor);
		}
	}

	private List<Double> dimReduceAVG(List<Double> valueList, Integer dr_factor) {

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
