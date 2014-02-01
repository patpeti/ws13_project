package at.ac.tuwien.thesis.scheduler.utils.simulators;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import at.ac.tuwien.thesis.scheduler.Constants;
import at.ac.tuwien.thesis.scheduler.enums.ForecastType;
import at.ac.tuwien.thesis.scheduler.model.TimeSeriesHolder;
import at.ac.tuwien.thesis.scheduler.model.TimeSeriesModel;
import at.ac.tuwien.thesis.scheduler.model.cloudModel.Application;
import at.ac.tuwien.thesis.scheduler.model.cloudModel.Machine;
import at.ac.tuwien.thesis.scheduler.model.cloudModel.ResourceAllocationException;
import at.ac.tuwien.thesis.scheduler.utils.Forecaster;
import at.ac.tuwien.thesis.scheduler.utils.Prediction;

public class LongTermSimulator2 {
	
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
	
	public final static Integer offset = 0;
	public final static Integer window = 20;
	
	List<Double> utilisationLog;
	List<Integer> numPMLog;
	int numReschedules = 0;
	int machineViolations = 0;
	int dimensionViolations = 0;
	
	int predictedViolations = 0;
	
	long forecastTime;
	long forecastTimePerDim;
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
	private int falsePositiveDimensions = 0;
	int falsePositiveCPU = 0;
	int falsePositiveNET = 0;
	int falsePositiveMEM = 0;
	int falsePositiveDISK = 0;
	

	public LongTermSimulator2(TimeSeriesModel tsModel) {
		this.tsModel = tsModel;
		appList = new ArrayList<Application>();
		pmList = new ArrayList<Machine>();
		utilisationLog = new ArrayList<Double>();
		numPMLog = new ArrayList<Integer>();
	}

	public void simulate(Integer confidence, Integer split, Integer dimRed) {
		
		double confidenceFactor = new Double(confidence)/new Double(100);
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
		int horizon = (int) Math.ceil(Constants.Horizon/dimRed) + offset+window;
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
			
			List<Double> cpuForecastListe = forecaster.calculateForecast(cpuSeries, cpuForecast, dimRed,horizon);
			List<Double> memForecastListe = forecaster.calculateForecast(memSeries, memForecast, dimRed,horizon);
			List<Double> diskForecastListe = forecaster.calculateForecast(diskSeries, diskForecast, dimRed,horizon);
			List<Double> netForecastListe = forecaster.calculateForecast(netSeries, netForecast, dimRed,horizon);

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
					boolean CPUViolation = false;
					boolean NETViolation = false;
					boolean MEMViolation = false;
					boolean DISKViolation = false;
					
					double sumcpu = 0;
					double summem = 0;
					double sumnet = 0;
					double sumdisk= 0;
					double realcpu = 0;
					double realmem = 0;
					double realnet = 0;
					double realdisk= 0;
					//find violation
					List<Application> tempAppList = new ArrayList<Application>(m.getApps());
					tempAppList.addAll(appsThatDontFit);
					
					for(Application app : tempAppList){
						realcpu += app.getActualCPU();
						realmem += app.getActualMEM();
						realnet += app.getActualNET();
						realdisk += app.getActualDISK();
					}
					
					sumcpu = Constants.maxCPU - realcpu;
					summem = Constants.maxMEM - realmem;
					sumnet = Constants.maxNET - realnet;
					sumdisk =Constants.maxDISK - realdisk;
					if(sumcpu <  0) {
						CPUViolation = true;
						CPUDimensionViolated++;
						dimensionViolations++;
					}
					if(summem < 0){
						MEMViolation = true;
						MEMDimensionViolated++;
						dimensionViolations++;
					}
					if(sumnet <  0){
						NETViolation = true;
						NETDimensionViolated++;
						dimensionViolations++;
					}
					if(sumdisk < 0){
						DISKViolation = true;
						DISKDimensionViolated++;
						dimensionViolations++;
					}
					if(CPUViolation){
						Map<String,List<Double>> windowValueMap = m.getForecastWindow(i,window,"CPU",confidenceFactor, appsThatDontFit);
						List<Double> lowerBound = windowValueMap.get("LOWER");
						List<Double> upperBound = windowValueMap.get("UPPER");
						boolean predicted = false;
						for(Double d : upperBound){
							if(d > realcpu) predicted = true;
						}
						if(predicted){
							CPUDimensionPredicted++;
							dimensionPredicted++;
						}else{
							CPUDimensionMissed++;
							missedDimensions++;
						}
					}
					if(DISKViolation){
						Map<String,List<Double>> windowValueMap = m.getForecastWindow(i,window,"DISK",confidenceFactor, appsThatDontFit);
						List<Double> lowerBound = windowValueMap.get("LOWER");
						List<Double> upperBound = windowValueMap.get("UPPER");
						boolean predicted = false;
						for(Double d : upperBound){
							if(d > realdisk) predicted = true;
						}
						if(predicted){
							DISKDimensionPredicted++;
							dimensionPredicted++;
						}else{
							DISKDimensionMissed++;
							missedDimensions++;
						}
					}
					if(NETViolation){
						Map<String,List<Double>> windowValueMap = m.getForecastWindow(i,window,"NET",confidenceFactor, appsThatDontFit);
						List<Double> lowerBound = windowValueMap.get("LOWER");
						List<Double> upperBound = windowValueMap.get("UPPER");
						boolean predicted = false;
						for(Double d : upperBound){
							if(d > realnet) predicted = true;
						}
						if(predicted){
							NETDimensionPredicted++;
							dimensionPredicted++;
						}else{
							NETDimensionMissed++;
							missedDimensions++;
						}
					}
					if(MEMViolation){
						Map<String,List<Double>> windowValueMap = m.getForecastWindow(i,window,"MEM",confidenceFactor, appsThatDontFit);
						List<Double> lowerBound = windowValueMap.get("LOWER");
						List<Double> upperBound = windowValueMap.get("UPPER");
						boolean predicted = false;
						for(Double d : upperBound){
							if(d > realmem) predicted = true;
						}
						if(predicted){
							MEMDimensionPredicted++;
							dimensionPredicted++;
						}else{
							MEMDimensionMissed++;
							missedDimensions++;
						}
					}
					//TODO machinePredicted/Missed
				}
				//no rescheduling needed on the machine -> aging of predictions and count false positives
				if(appsThatDontFit.isEmpty()){
					
					double realcpu = Constants.maxCPU;
					double realmem = Constants.maxMEM;
					double realnet = Constants.maxNET;
					double realdisk = Constants.maxDISK;
					
					Map<String,List<Double>> windowValueMap = m.getForecastWindow(i,window,"CPU",confidenceFactor,null);
					List<Double> lowerBound = windowValueMap.get("LOWER");
					boolean falsePositiveCPU = false;
					for(Double d : lowerBound){
						if(d > realcpu) falsePositiveCPU = true;
					}
					windowValueMap.clear();
					windowValueMap = m.getForecastWindow(i,window,"MEM",confidenceFactor,null);
					lowerBound = windowValueMap.get("LOWER");
					boolean falsePositiveMEM = false;
					for(Double d : lowerBound){
						if(d > realmem) falsePositiveMEM = true;
					}
					windowValueMap.clear();
					windowValueMap = m.getForecastWindow(i,window,"DISK",confidenceFactor,null);
					lowerBound = windowValueMap.get("LOWER");
					boolean falsePositiveDISK = false;
					for(Double d : lowerBound){
						if(d > realdisk) falsePositiveDISK = true;
					}
					windowValueMap.clear();
					windowValueMap = m.getForecastWindow(i,window,"NET",confidenceFactor,null);
					lowerBound = windowValueMap.get("LOWER");
					boolean falsePositiveNET = false;
					for(Double d : lowerBound){
						if(d > realnet) falsePositiveNET = true;
					}
					if(falsePositiveCPU){
						this.falsePositiveCPU++;
						this.falsePositiveDimensions++;
					}
					if(falsePositiveNET){
						this.falsePositiveNET++;
						this.falsePositiveDimensions++;
					}
					if(falsePositiveMEM){
						this.falsePositiveMEM++;
						this.falsePositiveDimensions++;
					}
					if(falsePositiveDISK){
						this.falsePositiveDISK++;
						this.falsePositiveDimensions++;
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
		System.out.println("" + machineViolations +" \t\t " + machinePredicted  +" \t\t " + missedPrediction +" \t\t " + falsePositiveMachines);
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println();
		
		System.out.println("Per Dimension");
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println("Violated \t Predicted \t Missed \t FalsePositive");
		System.out.println("" + dimensionViolations +" \t\t " + dimensionPredicted  +" \t\t " + missedDimensions +" \t\t " + falsePositiveDimensions);
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println();
		
		System.out.println("Per CPU");
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println("Violated \t Predicted \t Missed \t FalsePositive");
		System.out.println("" + CPUDimensionViolated +" \t\t " + CPUDimensionPredicted  +" \t\t " + CPUDimensionMissed +" \t\t " + falsePositiveCPU);
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println();
		
		System.out.println("Per MEM");
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println("Violated \t Predicted \t Missed \t FalsePositive");
		System.out.println("" + MEMDimensionViolated +" \t\t " + MEMDimensionPredicted  +" \t\t " + MEMDimensionMissed +" \t\t " + falsePositiveMEM);
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println();
		
		System.out.println("Per NET");
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println("Violated \t Predicted \t Missed \t FalsePositive");
		System.out.println("" + NETDimensionViolated +" \t\t " + NETDimensionPredicted  +" \t\t " + NETDimensionMissed +" \t\t " + falsePositiveNET);
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println();
		
		System.out.println("Per DISK");
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println("Violated \t Predicted \t Missed \t FalsePositive");
		System.out.println("" + DISKDimensionViolated +" \t\t " + DISKDimensionPredicted  +" \t\t " + DISKDimensionMissed +" \t\t " + falsePositiveDISK);
		System.out.println("-------------------------------------------------------------------------------------------------------");
		System.out.println();
		
//		XYSeries s1 = new XYSeries("RealCPU");
//		XYSeries s2 = new XYSeries("Forecast");
//		int i=0;
//		for(Double value : app1CPUList){
//			s1.add(i,value);
//			s2.add(i,forecastCPUList.get(i));
//			i++;
//		}
//		XYSeriesCollection dataset = new XYSeriesCollection();
//		dataset.addSeries(s1);
//		dataset.addSeries(s2);
//
//		JFreeChart chart = createChart(dataset, "firstmachine");
//		chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.RED);
//		chart.getXYPlot().getRenderer().setSeriesPaint(1, Color.BLUE);
//		ChartPanel panel = new ChartPanel(chart);
//		
//		JFrame frame = new JFrame();
//		frame.setSize(400, 300);
//		frame.setContentPane(panel);
//		frame.setVisible(true);
	
		
		
	}
	
	
	private JFreeChart createChart(XYDataset dataset,String dim) {

		JFreeChart chart = ChartFactory.createXYLineChart(
				dim,  // title
				"Time",             // x-axis label
				"Usage",   // y-axis label
				dataset,            // data
				PlotOrientation.VERTICAL,
				false,               // create legend?
				false,               // generate tooltips?
				false               // generate URLs?
				);

		chart.setBackgroundPaint(Color.white);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);
		plot.setAxisOffset(new RectangleInsets(2.0, 2.0, 2.0, 2.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(false);
			renderer.setBaseShapesFilled(true);
			renderer.setDrawSeriesLineAsPath(true);
			
		}
		return chart;

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
