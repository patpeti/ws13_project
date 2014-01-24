package at.ac.tuwien.thesis.scheduler.model.cloudModel;

import java.util.List;

public class Application {
	
	String name;
	List<Double> cpuList;
	List<Double> memList;
	List<Double> diskList;
	List<Double> netList;
	List<Double> forecastedCpuList;
	List<Double> forecastedMemList;
	List<Double> forecastedDiskList;
	List<Double> forecastedNetList;
	int pointer = 0;

	
	public Application(List<Double> cpuSeries,
					   List<Double> memSeries,
					   List<Double> diskSeries,
					   List<Double> netSeries,String name){
		this.cpuList = cpuSeries;
		this.memList = memSeries;
		this.diskList = diskSeries;
		this.netList = netSeries;
		this.name = name;
				
	}
	
	public void setPointer(int i){
		this.pointer = i;
	}
	public int getPointer(){
		return this.pointer;
	}
	public void increasePointer(){
		this.setPointer(this.getPointer()+1);
	}
	
	public Double getActualCPU(){
		return this.cpuList.get(pointer);
	}
	
	public Double getActualMEM(){
		return this.memList.get(pointer);
	}
	public Double getActualDISK(){
		return this.diskList.get(pointer);
	}
	public Double getActualNET(){
		return this.netList.get(pointer);
	}
	public String getName(){
		return name;
	}
	public String toString(){
		return name;
	}
	public int size(){
		return this.cpuList.size();
	}

	public void setForecastedCPU(List<Double> dimension) {
		this.forecastedCpuList = dimension;
		
	}
	
	public void setForecastedMEM(List<Double> dimension) {
		this.forecastedMemList = dimension;
		
	}
	public void setForecastedDISK(List<Double> dimension) {
		this.forecastedDiskList = dimension;
		
	}
	public void setForecastedNET(List<Double> dimension) {
		this.forecastedNetList = dimension;
		
	}

	public double getForecastedMEM(int step) {
		try{
			return this.forecastedMemList.get(this.pointer+step);
		}catch(IndexOutOfBoundsException e){
			return this.forecastedMemList.get(this.pointer);
		}
	}
	
	public double getForecastedCPU(int step) {
		try{
			return this.forecastedCpuList.get(this.pointer+step);
		}catch(IndexOutOfBoundsException e){
			return this.forecastedCpuList.get(this.pointer);
		}
	}
	public double getForecastedDISK(int step) {
		try{
			return this.forecastedDiskList.get(this.pointer+step);
		}catch(IndexOutOfBoundsException e){
			return this.forecastedDiskList.get(this.pointer);
		}
	}
	public double getForecastedNET(int step) {
		try{
			return this.forecastedNetList.get(this.pointer+step);
		}catch(IndexOutOfBoundsException e){
			return this.forecastedNetList.get(this.pointer);
		}
	}
}
