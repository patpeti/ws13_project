package at.ac.tuwien.thesis.scheduler.utils;

import at.ac.tuwien.thesis.scheduler.enums.ForecastType;
import at.ac.tuwien.thesis.scheduler.enums.SimType;
import at.ac.tuwien.thesis.scheduler.model.TimeSeriesModel;
import at.ac.tuwien.thesis.scheduler.utils.simulators.LongTermSimulator;
import at.ac.tuwien.thesis.scheduler.utils.simulators.NaiveSimulator;
import at.ac.tuwien.thesis.scheduler.utils.simulators.ShortTermSimulator;

public class Simulator {
	
	private TimeSeriesModel tsModel;
	private ForecastType diskForecast;
	private ForecastType netForecast;
	private ForecastType memForecast;
	private ForecastType cpuForecast;
	
	public Simulator(TimeSeriesModel tsModel) {
		this.tsModel = tsModel;
	}

	public void startSimulation(SimType type, Integer confidence, Integer split, Integer dimRed ){
		
		if(type.equals(SimType.NAIVE)){
			
			NaiveSimulator naive = new NaiveSimulator(tsModel);
			naive.simulate(confidence,split,dimRed);
			
		}else if(type.equals(SimType.LONGTERM)){

			LongTermSimulator longterm = new LongTermSimulator(tsModel);
			longterm.setForecastTypes(cpuForecast,netForecast,memForecast,diskForecast);
			longterm.simulate(confidence,split,dimRed);
			
		}else if(type.equals(SimType.SHORTTERM)){

			ShortTermSimulator shortTerm = new ShortTermSimulator(tsModel);
			
			shortTerm.simulate(confidence,split,dimRed);
			
		}
		
	}

	public void setForecastTypes(ForecastType cpuForecast,
			ForecastType netForecast, ForecastType memForecast,
			ForecastType diskForecast) {
		this.cpuForecast = cpuForecast;
		this.memForecast = memForecast;
		this.netForecast = netForecast;
		this.diskForecast = diskForecast;

	}

}
