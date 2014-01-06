package at.ac.tuwien.thesis.scheduler.utils.simulators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.ac.tuwien.thesis.scheduler.Constants;
import at.ac.tuwien.thesis.scheduler.model.TimeSeriesHolder;
import at.ac.tuwien.thesis.scheduler.model.TimeSeriesModel;
import at.ac.tuwien.thesis.scheduler.model.cloudModel.Application;
import at.ac.tuwien.thesis.scheduler.model.cloudModel.Machine;
import at.ac.tuwien.thesis.scheduler.model.cloudModel.ResourceAllocationException;

public class NaiveSimulator {
	TimeSeriesModel tsModel;
	TimeSeriesModel splittedModel1;
	TimeSeriesModel splittedModel2;
	List<Application> appList;
	List<Machine> pmList;
	int splitLength;
	
	
	List<Double> utilisationLog;
	List<Integer> numPMLog;
	int numReschedules = 0;
	
	public NaiveSimulator(TimeSeriesModel tsModel) {
		this.tsModel = tsModel;
		appList = new ArrayList<Application>();
		pmList = new ArrayList<Machine>();
		utilisationLog = new ArrayList<Double>();
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
				addToPM(app);
			
		}
		
		//once we have the initial state, start iterating from i = 0 to splitlength
		//iterate over values -> let PMs adapt changes -> count overall resource utilisation, num of app relocation, SLO violation
		
		for(int i = 0; i < splitLength; i++){
			//set Application pointer++ for each application in each machine
			List<Application> reschedule = new ArrayList<Application>();
			for(Machine m : pmList){
				//if ressourceallocationexcption occurs remove app -> assing to reschedule liste
				//try assign reschedule list to existing PM-s
				reschedule.addAll(m.iterate());
				numReschedules += reschedule.size();
			}
			//assign them to new PM
			if(!reschedule.isEmpty()){
				for(Application app : reschedule){
					addToPM(app);
				}
			}
		
			//CONSOLIDATION
			//calculate utilization
		
			System.out.println("utilisation: "+ calculateUtilization());
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
					Map<Machine,Application> assignmentMap = new HashMap<Machine,Application>();
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
									assignmentMap.put(m, app);
								} catch (ResourceAllocationException e1) {
									System.err.println("there are avaiable resource but app cannot fit to machine2");
								}
							}
						}
					}
					//rollback
					if(rescheduled < tryReschedule.size()){
						//remove new assingments
						for(Machine m : assignmentMap.keySet()){
							m.removeApplication(assignmentMap.get(m));
						}
						pmList.add(lowest);
						System.out.println("rollback");
					}else if(rescheduled == tryReschedule.size()){
						numReschedules += rescheduled;
						System.out.println("rescheduling success");
					}else{
						System.err.println("wtf");
					}
					
					numPMLog.add(pmList.size());
					
					System.out.println("Pms: \t utilisation: \t reschedules: \t");
					System.out.println(pmList.size()+"\t" + utilisationLog.get(i) + " \t "+ numReschedules);
					
				}
			}catch(IndexOutOfBoundsException exception){
					System.out.println("First step ... Do nothing");
				}

			
			
		}
		
		
		
	}
	
	private double calculateUtilization(){
		int numPm = pmList.size();
		double sumCPU = 0,sumMEM = 0,sumNET = 0,sumDISK = 0;
		for(Machine m : pmList){
			sumCPU += m.getAvailableCPU();
			sumMEM += m.getAvailableMEM();
			sumNET += m.getAvailableNET();
			sumDISK += m.getAvailableDISK();
		}
		double utilisation = 100-((sumCPU/numPm*Constants.maxCPU)*100);
		utilisation += 100-((sumMEM/numPm*Constants.maxMEM)*100);
		utilisation += 100-((sumNET/numPm*Constants.maxNET)*100);
		utilisation += 100-((sumDISK/numPm*Constants.maxDISK)*100);
		utilisation = utilisation/4;
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
					} catch (ResourceAllocationException e1) {
						System.err.println("there are avaiable resource but app cannot fit to machine");
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
				System.err.println("new empty machine has not enough capacity to host this app - increase Machine size in Constants");
			}
		}

	}

	private void splitModel(Integer split) {
		int dataLength = tsModel.getTsForName("1.csv").getTs().get("CPU").size(); //TODO do not take fixed "1.csv"
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
		System.out.println("I. Split length: " + splittedModel2.getTsForName("4.csv").getDimension("CPU").size());
		System.out.println("II. Split length: " + splittedModel1.getTsForName("4.csv").getDimension("CPU").size());
		
	}

}
