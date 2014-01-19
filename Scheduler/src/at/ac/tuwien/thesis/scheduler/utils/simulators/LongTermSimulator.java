package at.ac.tuwien.thesis.scheduler.utils.simulators;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.thesis.scheduler.Constants;
import at.ac.tuwien.thesis.scheduler.enums.ForecastType;
import at.ac.tuwien.thesis.scheduler.model.TimeSeriesHolder;
import at.ac.tuwien.thesis.scheduler.model.TimeSeriesModel;
import at.ac.tuwien.thesis.scheduler.model.cloudModel.Application;
import at.ac.tuwien.thesis.scheduler.model.cloudModel.Machine;
import at.ac.tuwien.thesis.scheduler.model.cloudModel.ResourceAllocationException;
import at.ac.tuwien.thesis.scheduler.utils.Forecaster;

public class LongTermSimulator {
	
	private ForecastType diskForecast;
	private ForecastType netForecast;
	private ForecastType memForecast;
	private ForecastType cpuForecast;
	TimeSeriesModel tsModel;
	TimeSeriesModel splittedModel1;
	TimeSeriesModel splittedModel2;
	TimeSeriesModel forecast;
	List<Application> appList;
	List<Machine> pmList;
	int splitLength;
	int dataLength;
	
	
	List<Double> utilisationLog;
	List<Integer> numPMLog;
	int numReschedules = 0;

	public LongTermSimulator(TimeSeriesModel tsModel) {
		this.tsModel = tsModel;
		appList = new ArrayList<Application>();
		pmList = new ArrayList<Machine>();
		utilisationLog = new ArrayList<Double>();
		numPMLog = new ArrayList<Integer>();
	}

	public void simulate(Integer confidence, Integer split, Integer dimRed) {
		//split tsModel
		if(split >= 100 ){
			//calculate forecast on the whole dataset, use the second half as test
			splitModel(50);
			splittedModel1 = tsModel;
		}else if(split <= 0){
			splittedModel1 = tsModel;
			splittedModel2 = tsModel;
		}else{
			splitModel(split);
		}
		//create Forecast tsModel
		Forecaster forecaster = new Forecaster();
		forecast = new TimeSeriesModel();
		
		for(String key : splittedModel1.getTsModel().keySet()){
			List<Double> cpuSeries = splittedModel1.getTsForName(key).getDimension("CPU");
			List<Double> memSeries = splittedModel1.getTsForName(key).getDimension("MEM");
			List<Double> diskSeries = splittedModel1.getTsForName(key).getDimension("DISK");
			List<Double> netSeries = splittedModel1.getTsForName(key).getDimension("NET");
			
			TimeSeriesHolder tsHolder = new TimeSeriesHolder();
			tsHolder.addDimension("CPU");
			tsHolder.addDimension("MEM");
			tsHolder.addDimension("DISK");
			tsHolder.addDimension("NET");
			List<Double> cpuForecastListe = forecaster.calculateForecast(cpuSeries, cpuForecast, dimRed,null);
			List<Double> memForecastListe = forecaster.calculateForecast(memSeries, memForecast, dimRed,null);
			List<Double> diskForecastListe = forecaster.calculateForecast(diskSeries, diskForecast, dimRed,null);
			List<Double> netForecastListe = forecaster.calculateForecast(netSeries, netForecast, dimRed,null);

			tsHolder.AddSeries("CPU", cpuForecastListe);
			tsHolder.AddSeries("MEM", memForecastListe);
			tsHolder.AddSeries("DISK", diskForecastListe);
			tsHolder.AddSeries("NET", netForecastListe);
			
			forecast.addNewTimeSeries(key, tsHolder);
		}
		
		
		//create list of applications
		for(String key : splittedModel2.getTsModel().keySet()){
			List<Double> cpuSeries = splittedModel2.getTsForName(key).getDimension("CPU");
			List<Double> memSeries = splittedModel2.getTsForName(key).getDimension("MEM");
			List<Double> diskSeries = splittedModel2.getTsForName(key).getDimension("DISK");
			List<Double> netSeries = splittedModel2.getTsForName(key).getDimension("NET");
			Application app = new Application(cpuSeries, memSeries, diskSeries, netSeries, key);
			
			app.setForecastedCPU(forecast.getTsForName(key).getDimension("CPU"));
			app.setForecastedMEM(forecast.getTsForName(key).getDimension("MEM"));
			app.setForecastedDISK(forecast.getTsForName(key).getDimension("DISK"));
			app.setForecastedNET(forecast.getTsForName(key).getDimension("NET"));
			
			appList.add(app);
		}
		//start assign it to list of machines
//		for(Application app : appList){
//				addToPM(app);
//		}
		System.out.println("Apps Data Size: "+appList.get(0).size());
		System.out.println("Splitlength: " + splitLength);
		//once we have the initial state, start iterating from i = 0 to splitlength
		//iterate over values -> let PMs adapt changes -> count overall resource utilisation, num of app relocation, SLO violation
		
//		for(int i = 0; i < (dataLength-splitLength)-1; i++){
//			
//		}
		
	}
	
	private double calculateUtilization(){
		double numPm = (double) pmList.size();
		double sumCPU = 0,sumMEM = 0,sumNET = 0,sumDISK = 0;
		for(Machine m : pmList){
			sumCPU += m.getAvailableCPU();
			sumMEM += m.getAvailableMEM();
			sumNET += m.getAvailableNET();
			sumDISK += m.getAvailableDISK();
		}
		double utilisation = 100.0 - ((sumCPU/(numPm*Constants.maxCPU))*100.0);
		utilisation += 100.0 - ((sumMEM/(numPm*Constants.maxMEM))*100.0);
		utilisation += 100.0 - ((sumNET/(numPm*Constants.maxNET))*100.0);
		utilisation += 100.0 -((sumDISK/(numPm*Constants.maxDISK))*100.0);
		utilisation = utilisation/4.0;
		return utilisation;
	}

	private void addToPM(Application app) {
		boolean assigned = false;
		if(!pmList.isEmpty()){
			for(Machine m : pmList){
				if(m.getAvailableCPU() > app.getActualCPU() && 
						m.getAvailableDISK() > app.getActualDISK() &&
						m.getAvailableMEM() > app.getActualMEM() &&
						m.getAvailableNET() > app.getActualNET()){
					try {
						m.addApplication(app);
						assigned = true;
						break;
					} catch (ResourceAllocationException e1) {
						System.err.println("FATAL ERROR there are avaiable resource but app cannot fit to machine");
					}
				}
			}
		}
		//2.) if there is no assignment possible to existing PM than make a new PM
		if(!assigned){
			//create new PM and assign to that
			Machine pm = new Machine();
			pmList.add(pm);
			try {
				pm.addApplication(app);
			} catch (ResourceAllocationException e) {
				System.err.println("FATAL ERROR new empty machine has not enough capacity to host this app - increase Machine size in Constants");
			}
		}
	}

	private void splitModel(Integer split) {
		dataLength = tsModel.getTsForName("1.csv").getTs().get("CPU").size(); //TODO do not take fixed "1.csv"
		splitLength = dataLength * split/100;

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
		System.out.println("I. Split length: " + splittedModel1.getTsForName("4.csv").getDimension("CPU").size());
		System.out.println("II. Split length: " + splittedModel2.getTsForName("4.csv").getDimension("CPU").size());
		
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
