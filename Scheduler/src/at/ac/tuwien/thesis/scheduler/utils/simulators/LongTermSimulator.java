package at.ac.tuwien.thesis.scheduler.utils.simulators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.ac.tuwien.thesis.scheduler.Constants;
import at.ac.tuwien.thesis.scheduler.enums.ForecastType;
import at.ac.tuwien.thesis.scheduler.model.TimeSeriesHolder;
import at.ac.tuwien.thesis.scheduler.model.TimeSeriesModel;
import at.ac.tuwien.thesis.scheduler.model.cloudModel.Application;
import at.ac.tuwien.thesis.scheduler.model.cloudModel.Machine;
import at.ac.tuwien.thesis.scheduler.model.cloudModel.ResourceAllocationException;
import at.ac.tuwien.thesis.scheduler.utils.Forecaster;
import at.ac.tuwien.thesis.scheduler.utils.Prediction;

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
	
	public final static Integer steps = 10;
	public List<Prediction> predictions;
	
	List<Double> utilisationLog;
	List<Integer> numPMLog;
	int numReschedules = 0;
	int slaViolations = 0;
	
	int machinepredicted = 0;
	int machineAndDimensionPredicted = 0;
	long forecastTime;
	long forecastTimePerDim;
	

	public LongTermSimulator(TimeSeriesModel tsModel) {
		this.tsModel = tsModel;
		appList = new ArrayList<Application>();
		pmList = new ArrayList<Machine>();
		utilisationLog = new ArrayList<Double>();
		numPMLog = new ArrayList<Integer>();
		predictions = new ArrayList<Prediction>();
	}

	public void simulate(Integer confidence, Integer split, Integer dimRed) {
		//split tsModel
		if(split >= 100 ){
			//calculate forecast on the whole dataset, use the second half as test
			splitModel2(split);
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
		
		long startTime = System.currentTimeMillis();
		
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
		forecastTime = System.currentTimeMillis() - startTime;
		
		
		
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
		
		forecastTimePerDim = forecastTime/(appList.size()*4);
		
		//start assign it to list of machines
		for(Application app : appList){
				addToPM(app);
		}
		System.out.println("Apps Data Size: "+appList.get(0).size());
		System.out.println("Splitlength: " + splitLength);
		//once we have the initial state, start iterating from i = 0 to splitlength
		//iterate over values -> let PMs adapt changes -> count overall resource utilisation, num of app relocation, SLO violation
		
		
		
		for(int i = 0; i < (Constants.Horizon)-1; i++){
			
			//Make Predictions
			//TODO implement confidance level
			for(int j = 0; j< steps;j++){
				//if predictedavailable < 0  -> add machine and dimension to the list
				for(Machine m : pmList){

					if(m.getForecastedAvailableCPU(j) < 0){
						predictions.add(new Prediction(m,"CPU",j));
					}
					if(m.getForecastedAvailableDISK(j) < 0){
						predictions.add(new Prediction(m,"DISK",j));
					}
					if(m.getForecastedAvailableNET(j) < 0){
						predictions.add(new Prediction(m,"NET",j));
					}
					if(m.getForecastedAvailableMEM(j) < 0){
						predictions.add(new Prediction(m,"MEM",j));
					}
				}
			}
			
//			System.out.println("Iteration: "+i);
			//set Application pointer++ for each application in each machine
			List<Application> reschedule = new ArrayList<Application>();
			for(Machine m : pmList){
				//if ressourceallocationexcption occurs remove app -> assing to reschedule liste
				//try assign reschedule list to existing PM-s
				reschedule.addAll(m.iterate());
				numReschedules += reschedule.size();
				slaViolations++;
				if(!reschedule.isEmpty()){
					//find out if it was predicted
					boolean machinepredicted = false;
					boolean machineAndDimensionPredicted = false;
					
					double sumcpu = 0;
					double summem = 0;
					double sumnet = 0;
					double sumdisk= 0;
					for(Application app : reschedule){
						sumcpu += app.getActualCPU();
						summem += app.getActualMEM();
						sumnet += app.getActualNET();
						sumdisk += app.getActualDISK();
					}
					sumcpu = sumcpu - Constants.maxCPU;
					summem = summem - Constants.maxMEM;
					sumnet = sumnet - Constants.maxNET;
					sumdisk = sumdisk - Constants.maxDISK;
					
					for(Prediction p : predictions){
						if(p.getM().equals(m)){
							machinepredicted = true;
							
							if(sumcpu <= 0 && p.getDimension().equals("CPU"))  machineAndDimensionPredicted = true;
							if(summem <= 0 && p.getDimension().equals("MEM"))  machineAndDimensionPredicted = true;
							if(sumnet <= 0 && p.getDimension().equals("NET"))  machineAndDimensionPredicted = true;
							if(sumdisk <= 0 && p.getDimension().equals("DISK"))  machineAndDimensionPredicted = true;
							
						}
					}
					
					if(machinepredicted) this.machinepredicted++;
					if(machineAndDimensionPredicted) this.machineAndDimensionPredicted++;
					
				}
				
			}
			//assign them to new PM
			if(!reschedule.isEmpty()){
				for(Application app : reschedule){
//					System.out.println("rescheduling");
					addToPM(app);
				}
			}
			//CONSOLIDATION
			utilisationLog.add(i,calculateUtilization());
			try{
				//if utilization is low(er) try to migrate one PM-s apps to other PMs
				if(utilisationLog.get(i-1) > utilisationLog.get(i)){
					
					//select Lowest utilized PM
					Machine lowest = null;
					for(Machine m : pmList){
						if(lowest == null) lowest = m;
						if(m.getUtilization() < lowest.getUtilization()) lowest = m;
					}
										
					//try all its app to assign to other PM in the List
					List<Application> tryReschedule = lowest.getApps();
					Map<Machine,List<Application>> assignmentMap = new HashMap<Machine,List<Application>>();
					int rescheduled = 0;
					pmList.remove(lowest);
					for(Application app : tryReschedule){
						for(Machine m : pmList){
							if(m.getAvailableCPU() > app.getActualCPU() && 
									m.getAvailableDISK() > app.getActualDISK() &&
									m.getAvailableMEM() > app.getActualMEM() &&
									m.getAvailableNET() > app.getActualNET()){
								try {
									m.addApplication(app);
									rescheduled++;
									if(assignmentMap.get(m) == null){
										List<Application> apps = new ArrayList<Application>();
										apps.add(app);
										assignmentMap.put(m, apps);
									}else{
										List<Application> apps = assignmentMap.get(m);
										apps.add(app);
									}
									break;
								} catch (ResourceAllocationException e1) {
									System.err.println("FATAL ERROR there are avaiable resource but app cannot fit to machine2");
								}
							}
						}
					}
					//rollback
					if(rescheduled < tryReschedule.size()){
						//remove new assingments
						for(Machine m : assignmentMap.keySet()){
							for(Application app : assignmentMap.get(m)){
								m.removeApplication(app);
							}
						}
						pmList.add(lowest);
					}else if(rescheduled == tryReschedule.size()){
						numReschedules += rescheduled;
						System.out.println("rescheduling success");
					}else{
						System.err.println("wtf");
					}

				}
			}catch(IndexOutOfBoundsException exception){
				System.out.println("First step ... Do nothing");
			}
			numPMLog.add(pmList.size());
		}

		System.out.println("Number of reschedules: " + numReschedules);
		System.out.println("AVG number of Machines: "+ calculateMean2(numPMLog));
		System.out.println("AVG Overall Utilization: "+ calculateMean(utilisationLog));
		System.out.println("SLAViolationsPredicted - MachinesPredicted: "+ machinepredicted);
		System.out.println("SLAViolationsPredicted - MachineAndDimensionPredicted: "+ machineAndDimensionPredicted);
		System.out.println("Forecast Overhead overall (millisec): "+ forecastTime);
		System.out.println("AVG Forecast Overhead per Dimension (millisec) : "+ forecastTimePerDim);
		
		
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
	
	
	private void splitModel2(Integer split) {
		dataLength = tsModel.getTsForName("1.csv").getTs().get("CPU").size(); //TODO do not take fixed "1.csv"
		splitLength = split;

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
	
	private Double calculateMean(List<Double> temp) {
		Double sum = new Double(0);
		int num = 0;
		for(Double d : temp){
			sum += d;
			num++;
		}
		return (new Double(sum) / new Double(num));
	}
	
	private Double calculateMean2(List<Integer> temp) {
		Double sum = new Double(0);
		int num = 0;
		for(Integer d : temp){
			sum += d;
			num++;
		}
		return (new Double(sum) / new Double(num));
	}


}
