package at.ac.tuwien.thesis.scheduler.utils.simulators;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.thesis.scheduler.model.TimeSeriesHolder;
import at.ac.tuwien.thesis.scheduler.model.TimeSeriesModel;
import at.ac.tuwien.thesis.scheduler.model.cloudModel.Application;
import at.ac.tuwien.thesis.scheduler.model.cloudModel.Machine;

public class NaiveSimulator {
	TimeSeriesModel tsModel;
	TimeSeriesModel splittedModel1;
	TimeSeriesModel splittedModel2;
	List<Application> appList;
	List<Machine> pmList;
	
	public NaiveSimulator(TimeSeriesModel tsModel) {
		this.tsModel = tsModel;
		appList = new ArrayList<Application>();
		pmList = new ArrayList<Machine>();
	}

	public void simulate(Integer confidence, Integer split, Integer dimRed) {
		
		//split tsModel
		splitModel(split);
		
		//init PMs/ fill them
		
		//create list of applications
		
		for(String key : splittedModel2.getTsModel().keySet()){
			
			List<Double> cpuSeries = splittedModel2.getTsForName(key).getDimension("CPU");
			List<Double> memSeries = splittedModel2.getTsForName(key).getDimension("MEM");
			List<Double> diskSeries = splittedModel2.getTsForName(key).getDimension("DISK");
			List<Double> netSeries = splittedModel2.getTsForName(key).getDimension("NET");
			Application app = new Application(cpuSeries, memSeries, diskSeries, netSeries, key);
			appList.add(app);
		}
		
		//start assign it to list of machines

		for(Application app : appList){
			
		}
		
		//once we have the initial state, start iterating from i = 0 to splitlength
			//set Application pointer++ for each application in each machine
			//if ressourceallocationexcption occurs remove app -> assing to reschedule liste
			//try assign reschedule list to existing PM-s
			//assign them to new PM
		
			//calculate utilization
			//if utilization is low(er) try to migrate one PM-s apps to other PMs
			
		
		//iterate over values -> let PMs adapt changes -> count overall resource utilisation, num of app relocation, SLO violation
		
	}

	private void splitModel(Integer split) {
		int dataLength = tsModel.getTsForName("1.csv").getTs().get("CPU").size(); //TODO do not take fixed "1.csv"
		int splitLength = dataLength * split/100;

		splittedModel1 = new TimeSeriesModel();
		splittedModel2 = new TimeSeriesModel();
		
		for(String key : tsModel.getTsModel().keySet()){
			TimeSeriesHolder tsHolder1 = new TimeSeriesHolder();
			TimeSeriesHolder tsHolder2 = new TimeSeriesHolder();
			for(String dim : tsModel.getTsForName(key).getTs().keySet()){
				List<Double> temp = tsModel.getTsForName(key).getDimension(dim);
				ArrayList<Double> newSeries1 = new ArrayList<Double>();
				ArrayList<Double> newSeries2 = new ArrayList<Double>();
				int i = 0;
				for(Double d : temp){
					if(i < splitLength){
						newSeries1.add(d);
					}else{
						newSeries2.add(d);
					}
					i++;
				}
				tsHolder1.AddSeries(dim, newSeries1);
				tsHolder2.AddSeries(dim, newSeries2);
			}
			splittedModel1.addNewTimeSeries(key,tsHolder1 );
			splittedModel2.addNewTimeSeries(key,tsHolder2 );
		}
		
		
		System.out.println("Splitting finished");
		System.out.println("I. Split length: " + splittedModel2.getTsForName("4.csv").getDimension("CPU").size());
		System.out.println("II. Split length: " + splittedModel1.getTsForName("4.csv").getDimension("CPU").size());
		
	}

}
