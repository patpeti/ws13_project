package at.ac.tuwien.thesis.scheduler.utils;

import java.util.List;

public class MathUtil {
	
	public static Double calculateMean(List<Double> temp) {
		Double sum = new Double(0);
		int num = 0;
		for(Double d : temp){
			sum += d;
			num++;
		}
		return (new Double(sum) / new Double(num));
	}
	
	public static Double calculateMean2(List<Integer> temp) {
		Double sum = new Double(0);
		int num = 0;
		for(Integer d : temp){
			sum += d;
			num++;
		}
		return (new Double(sum) / new Double(num));
	}

}
