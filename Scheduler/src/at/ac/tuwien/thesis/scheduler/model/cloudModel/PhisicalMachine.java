package at.ac.tuwien.thesis.scheduler.model.cloudModel;

import at.ac.tuwien.thesis.scheduler.Constants;

public class PhisicalMachine {
	
	double cpu = 0;
	double mem = 0;
	double net = 0;
	double disk = 0; 
	double maxCpu,maxMem,maxNet,maxDisk; 
	private VirtualMachine vm;
	
	public PhisicalMachine(){
		this.maxCpu = Constants.maxCPU;
		this.maxDisk = Constants.maxDISK;
		this.maxNet = Constants.maxNET;
		this.maxMem = Constants.maxMEM;
		
		this.setVM(new VirtualMachine(this));
	}
	
	public void addApplication(Application app) throws ResourceAllocationException{
		this.getVm().addApplication(app);
	}

	private void setVM(VirtualMachine virtualMachine) {
		this.vm = virtualMachine;
//		this.addCpu(vm.getMinCpu());
//		this.addMem(vm.getMinMem());
//		this.addDisk(vm.getMinDisk());
//		this.addNet(vm.getMinNet());
		
	}

	public VirtualMachine getVm(){
		return this.vm;
	}

	public double getCpu() {
		return vm.getCpu();
	}

	public void setCpu(double cpu) {
		this.cpu = cpu;
	}

	public void addCpu(double cpu) {
		this.setCpu(this.getCpu()+cpu);
	}
	
	public double getMem() {
		return mem;
	}

	public void setMem(double mem) {
		this.mem = mem;
	}

	public void addMem(double mem) {
		this.setMem(this.getMem()+mem);
	}
	public double getNet() {
		return net;
	}

	public void setNet(double net) {
		this.net = net;
	}
	
	public void addNet(double net) {
		this.setNet(this.getNet()+net);
	}

	public double getDisk() {
		return disk;
	}

	public void setDisk(double disk) {
		this.disk = disk;
	}
	
	public void addDisk(double disk) {
		this.setDisk(this.getDisk()+disk);
	}

	public double getMaxCpu() {
		return maxCpu;
	}

	public double getMaxMem() {
		return maxMem;
	}

	public double getMaxNet() {
		return maxNet;
	}

	public double getMaxDisk() {
		return maxDisk;
	}

	public double getAvailableCPU(){
		return getMaxCpu()-getCpu();
	}
	
	public double getAvailableDISK(){
		return getMaxDisk()-getDisk();
	}
	
	public double getAvailableNET(){
		return getMaxNet()-getNet();
	}
	public double getAvailableMEM(){
		return getMaxMem()-getMem();
	}
}
