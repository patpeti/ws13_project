package at.ac.tuwien.thesis.scheduler.model.cloudModel;

import java.util.List;

public class Application {
	
	String name;
	List<Double> cpuList;
	List<Double> memList;
	List<Double> diskList;
	List<Double> netList;
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
	
}
