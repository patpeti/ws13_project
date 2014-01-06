package at.ac.tuwien.thesis.scheduler.model.cloudModel;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.thesis.scheduler.Constants;


public class VirtualMachine {

	double cpu,mem,disk,net;
	double minCpu,minMem,minDisk,minNet;
	PhisicalMachine pm;
	List<Application> appListe;
	
	public VirtualMachine(PhisicalMachine pm){
		
		this.minCpu = Constants.vmCPU;
		this.minMem = Constants.vmMEM;
		this.minDisk = Constants.vmDISK;
		this.minNet = Constants.vmNET;
		appListe = new ArrayList<Application>();
		this.pm = pm;
	}
	
	public void addApplication(Application app) throws ResourceAllocationException {
		this.appListe.add(app);
		
		this.addCpu(app.getActualCPU());
		this.addMem(app.getActualMEM());
		this.addDisk(app.getActualDISK());
		this.addNet(app.getActualNET());
		
	}
	
	public void addCpu(double cpu) throws ResourceAllocationException {
		this.setCpu(this.getCpu()+cpu);
		this.pm.addCpu(cpu);
		if(this.getMaxCPU() < this.getCpu()){
			this.setCpu(this.getCpu()-cpu);
			throw new ResourceAllocationException();
		}
	}
	
	private void addNet(Double net) throws ResourceAllocationException {
		this.setNet(this.getNet()+net);
		this.pm.addNet(net);
		if(this.getMaxNet() < this.getNet()){
			this.setNet(this.getNet()-net);
			throw new ResourceAllocationException();
		}
	}

	private void addDisk(Double disk) throws ResourceAllocationException {
		this.setDisk(this.getDisk()+disk);
		this.pm.addDisk(disk);
		if(this.getMaxDISK() < this.getDisk()){
			this.setDisk(this.getDisk()-disk);
			throw new ResourceAllocationException();
		}
	}

	private void addMem(Double mem) throws ResourceAllocationException {
		this.setMem(this.getMem()+mem);
		this.pm.addMem(mem);
		if(this.getMaxMEM() < this.getMem()){
			this.setMem(this.getMem()-mem);
			throw new ResourceAllocationException();
		}		
	}

	public double getMaxCPU(){
		return this.pm.getMaxCpu();
	}
	
	public double getMaxMEM(){
		return this.pm.getMaxMem();
	}
	
	public double getMaxDISK(){
		return this.pm.getMaxDisk();
	}
	
	public double getMaxNet(){
		return this.pm.getMaxNet();
	}
	////////////////////////////////////////////////////
	public double getCpu() {
		return cpu;
	}

	public void setCpu(double cpu) {
		this.cpu = cpu;
	}
	

	public double getMem() {
		return mem;
	}

	public void setMem(double mem) {
		this.mem = mem;
	}

	public double getDisk() {
		return disk;
	}

	public void setDisk(double disk) {
		this.disk = disk;
	}

	public double getNet() {
		return net;
	}

	public void setNet(double net) {
		this.net = net;
	}
	///////////////////////////////////////////////////////
	public double getMinCpu() {
		return minCpu;
	}

	public double getMinMem() {
		return minMem;
	}

	public double getMinDisk() {
		return minDisk;
	}

	public double getMinNet() {
		return minNet;
	}

	
	
	
}
