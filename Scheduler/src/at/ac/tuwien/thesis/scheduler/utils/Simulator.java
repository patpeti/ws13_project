package at.ac.tuwien.thesis.scheduler.utils;

import at.ac.tuwien.thesis.scheduler.enums.SimType;
import at.ac.tuwien.thesis.scheduler.model.TimeSeriesModel;
import at.ac.tuwien.thesis.scheduler.utils.simulators.NaiveSimulator;

public class Simulator {
	
	private TimeSeriesModel tsModel;
	
	public Simulator(TimeSeriesModel tsModel) {
		this.tsModel = tsModel;
	}

	public void startSimulation(SimType type, Integer confidence, Integer split, Integer dimRed ){
		
		if(type.equals(SimType.NAIVE)){
			
			NaiveSimulator naive = new NaiveSimulator(tsModel);
			naive.simulate(confidence,split,dimRed);
			
		}else{
			//todo
			
		}
		
	}

}
