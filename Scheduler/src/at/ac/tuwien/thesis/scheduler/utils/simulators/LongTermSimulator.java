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
	int machineViolations = 0;
	int dimensionViolations = 0;
	
	int CPUDimensionViolated = 0;
	int MEMDimensionViolated = 0;
	int DISKDimensionViolated = 0;
	int NETDimensionViolated = 0;
	
	int machinePredicted = 0;
	int missedPrediction = 0;
	int missedDimensions = 0;
	
	int CPUDimensionPredicted = 0;
	int MEMDimensionPredicted = 0;
	int DISKDimensionPredicted = 0;
	int NETDimensionPredicted = 0;
	int dimensionPredicted = 0;
	
	int CPUDimensionMissed = 0;
	int MEMDimensionMissed = 0;
	int DISKDimensionMissed = 0;
	int NETDimensionMissed = 0;
	
	int falsePositiveMachines = 0;
	int falsePositiveDimensions = 0;
	int falsePositiveCPU = 0;
	int falsePositiveNET = 0;
	int falsePositiveMEM = 0;
	int falsePositiveDISK = 0;
	
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
				List<Application> appsThatDontFit = m.iterate();
				reschedule.addAll(appsThatDontFit);
				numReschedules += appsThatDontFit.size();
				
				if(!appsThatDontFit.isEmpty()){
					machineViolations++;
					//find out if it was predicted
					boolean machinepredicted = false;
					boolean CPUDimensionPredicted = false;
					boolean DISKDimensionPredicted = false;
					boolean NETDimensionPredicted = false;
					boolean MEMDimensionPredicted = false;
					
					boolean CPUViolation = false;
					boolean NETViolation = false;
					boolean MEMViolation = false;
					boolean DISKViolation = false;
					
					double sumcpu = 0;
					double summem = 0;
					double sumnet = 0;
					double sumdisk= 0;
					//find violation
					List<Application> tempAppList = new ArrayList<Application>(m.getApps());
					tempAppList.addAll(appsThatDontFit);
					
					for(Application app : tempAppList){
						sumcpu += app.getActualCPU();
						summem += app.getActualMEM();
						sumnet += app.getActualNET();
						sumdisk += app.getActualDISK();
					}
					sumcpu = sumcpu - Constants.maxCPU;
					summem = summem - Constants.maxMEM;
					sumnet = sumnet - Constants.maxNET;
					sumdisk = sumdisk - Constants.maxDISK;
					if(sumcpu <=  0) {
						CPUViolation = true;
						CPUDimensionViolated++;
						dimensionViolations++;
					}
					if(summem <=  0){
						MEMViolation = true;
						MEMDimensionViolated++;
						dimensionViolations++;
					}
					if(sumnet <=  0){
						NETViolation = true;
						NETDimensionViolated++;
						dimensionViolations++;
					}
					if(sumdisk <= 0){
						DISKViolation = true;
						DISKDimensionViolated++;
						dimensionViolations++;
					}
					
					for(Prediction p : predictions){
						if(p.getM().equals(m)){
							machinepredicted = true;
							if(p.getDimension().equals("CPU"))  CPUDimensionPredicted = true;
							if(p.getDimension().equals("MEM"))  MEMDimensionPredicted = true;
							if(p.getDimension().equals("NET"))  NETDimensionPredicted = true;
							if(p.getDimension().equals("DISK"))  DISKDimensionPredicted = true;
						}
					}
					
					if(machinepredicted) this.machinePredicted++;
					else				 this.missedPrediction++;
					
					//count predictions
					if(CPUViolation && CPUDimensionPredicted){
						this.CPUDimensionPredicted++;
						this.dimensionPredicted++;
					}
					if(MEMViolation && MEMDimensionPredicted){
						this.MEMDimensionPredicted++;
						this.dimensionPredicted++;
					}
					if(NETViolation && NETDimensionPredicted){
						this.NETDimensionPredicted++;
						this.dimensionPredicted++;
					}
					if(DISKViolation && DISKDimensionPredicted){
						this.DISKDimensionPredicted++;
						this.dimensionPredicted++;
					}
					
					// count false positives
					if(!CPUViolation && CPUDimensionPredicted){
						this.falsePositiveCPU++;
						this.falsePositiveDimensions++;
					}
					if(!MEMViolation && MEMDimensionPredicted){
						this.falsePositiveMEM++;
						this.falsePositiveDimensions++;
					}
					if(!NETViolation && NETDimensionPredicted){
						this.falsePositiveNET++;
						falsePositiveDimensions++;
					}
					if(!DISKViolation && DISKDimensionPredicted){
						this.falsePositiveDISK++;
						this.falsePositiveDimensions++;
					}
					//count misses
					if(CPUViolation && !CPUDimensionPredicted){
						this.CPUDimensionMissed++;
						this.missedDimensions++;
					}
					if(MEMViolation && !MEMDimensionPredicted){
						this.MEMDimensionMissed++;
						this.missedDimensions++;
					}
					if(NETViolation && !NETDimensionPredicted){
						this.NETDimensionMissed++;
						this.missedDimensions++;
					}
					if(DISKViolation && !DISKDimensionPredicted){
						this.DISKDimensionMissed++;
						this.missedDimensions++;
					}
					
					//flush predictions on this machine
					List<Prediction> newList = new ArrayList<Prediction>();
					for(Prediction p: predictions){
						if(!p.getM().equals(m)){
							newList.add(p);
						}
					}
					predictions.clear();
					predictions.addAll(newList);
					
				}
				//no rescheduling needed on the machine -> aging of predictions and count false positives
				if(appsThatDontFit.isEmpty()){
					//select predictions that refer to the machine m
					List<Prediction> temp = new ArrayList<Prediction>();
					boolean falsepositiveMachine = false;
					boolean falsePositiveCPU = false;
					boolean falsePositiveNET = false;
					boolean falsePositiveDISK = false;
					boolean falsePositiveMEM = false;
					for(Prediction p: predictions){
						if(p.getM().equals(m)){
							temp.add(p);
						}
					}
					//increase age
					if(!temp.isEmpty()){
						for(Prediction p : temp){
							//delete from predictions
							predictions.remove(p);
							//increase age
							p.increaseAge();
							if(p.getAge() >= steps){
								//find falsepositives
								falsepositiveMachine = true;
								p.setToDelete(true);
								if(p.getDimension().equals("CPU")) falsePositiveCPU = true;
								if(p.getDimension().equals("NET")) falsePositiveNET = true;
								if(p.getDimension().equals("MEM")) falsePositiveMEM = true;
								if(p.getDimension().equals("DISK")) falsePositiveDISK = true;
							}
						}
						if(falsepositiveMachine) this.falsePositiveMachines++;
						if(falsePositiveCPU){
							this.falsePositiveCPU++;
							this.falsePositiveDimensions++;
						}
						if(falsePositiveNET){
							this.falsePositiveNET++;
							this.falsePositiveDimensions++;
						}
						if(falsePositiveDISK){
							this.falsePositiveDISK++;
							this.falsePositiveDimensions++;
						}
						if(falsePositiveMEM) {
							this.falsePositiveMEM++;
							this.falsePositiveDimensions++;
						}
						//put back in the list those predictions that are not aged
						for(Prediction p : temp){
							if(!p.isToDelete()){
								predictions.add(p);
							}
						}
					}
				}
			}

			
			//assign them to new PM
			if(!reschedule.isEmpty()){
				for(Application app : reschedule){
//					System.out.println("rescheduling");
					addToPM(app);
				}
			}
//			//CONSOLIDATION
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
						//delete predictions on machine "lowest" from predictions
						List<Prediction> newList = new ArrayList<Prediction>();
						for(Prediction p: predictions){
							if(!p.getM().equals(lowest)){
								newList.add(p);
							}
						}
						predictions.clear();
						predictions.addAll(newList);
						
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
		System.out.println("Forecast time (ms): " + forecastTime);
		System.out.println("AVG Forecast time per Dimension (ms): " + forecastTimePerDim);
		System.out.println();
		
		System.out.println("Per Machine");
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println("Violated \t Predicted \t Missed \t FalsePositive");
		System.out.println("" + machineViolations +" \t " + machinePredicted  +" \t " + missedPrediction +" \t " + falsePositiveMachines);
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println();
		
		System.out.println("Per Dimension");
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println("Violated \t Predicted \t Missed \t FalsePositive");
		System.out.println("" + dimensionViolations +" \t " + dimensionPredicted  +" \t " + missedDimensions +" \t " + falsePositiveDimensions);
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println();
		
		System.out.println("Per CPU");
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println("Violated \t Predicted \t Missed \t FalsePositive");
		System.out.println("" + CPUDimensionViolated +" \t " + CPUDimensionPredicted  +" \t " + CPUDimensionMissed +" \t " + falsePositiveCPU);
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println();
		
		System.out.println("Per MEM");
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println("Violated \t Predicted \t Missed \t FalsePositive");
		System.out.println("" + MEMDimensionViolated +" \t " + MEMDimensionPredicted  +" \t " + MEMDimensionMissed +" \t " + falsePositiveMEM);
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println();
		
		System.out.println("Per NET");
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println("Violated \t Predicted \t Missed \t FalsePositive");
		System.out.println("" + NETDimensionViolated +" \t " + NETDimensionPredicted  +" \t " + NETDimensionMissed +" \t " + falsePositiveNET);
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println();
		
		System.out.println("Per NET");
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println("Violated \t Predicted \t Missed \t FalsePositive");
		System.out.println("" + NETDimensionViolated +" \t " + NETDimensionPredicted  +" \t " + NETDimensionMissed +" \t " + falsePositiveNET);
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println();
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
