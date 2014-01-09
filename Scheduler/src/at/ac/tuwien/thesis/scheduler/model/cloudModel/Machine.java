package at.ac.tuwien.thesis.scheduler.model.cloudModel;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.thesis.scheduler.Constants;

public class Machine {
	
	List<Application> appListe;
	double cpu,disk,mem,net;
	double maxCPU,maxNET,maxDISK,maxMEM;
	
	public Machine(){
		this.maxCPU = Constants.maxCPU;
		this.maxDISK = Constants.maxDISK;
		this.maxNET = Constants.maxNET;
		this.maxMEM = Constants.maxMEM;
		cpu = 0; disk = 0; mem = 0; net =0;
		appListe = new ArrayList<Application>();
	}
	
	public void addApplication(Application app) throws ResourceAllocationException{
		this.addCpu(app.getActualCPU());
		this.addDisk(app.getActualDISK());
		this.addMem(app.getActualMEM());
		this.addNet(app.getActualNET());
		this.appListe.add(app);
	}
	
	public void removeApplication(Application app){
		this.appListe.remove(app);
		this.subtractCpu(app.getActualCPU());
		this.subtractDisk(app.getActualDISK());
		this.subtractMem(app.getActualMEM());
		this.subtractNet(app.getActualNET());
	}
	
	public List<Application> getApps(){
		return appListe;
	}

	public double getAvailableCPU() {
		return maxCPU-cpu;
	}

	public double getAvailableDISK() {
		return maxDISK-disk;
	}

	public double getAvailableMEM() {
		return maxMEM-mem;
	}

	public double getAvailableNET() {
		return maxNET-net;
	}
	
	
	public void setCpu(double cpu) throws ResourceAllocationException {
		if(cpu > maxCPU){
			throw new ResourceAllocationException("cpu full");
		}else{
			this.cpu = cpu;
		}
	}

	public void setDisk(double disk) throws ResourceAllocationException {
		if(disk > maxDISK){
			throw new ResourceAllocationException("disk full");
		}else{
			this.disk = disk;
		}
	}

	public void setMem(double mem) throws ResourceAllocationException {
		if(mem > maxMEM){
			throw new ResourceAllocationException("mem full");
		}else{
			this.mem = mem;
		}
	}

	public void setNet(double net) throws ResourceAllocationException {
		if(net > maxNET){
			throw new ResourceAllocationException("net full");
		}else{
			this.net = net;
		}
	}

	public void addCpu(double cpu) throws ResourceAllocationException {
		this.cpu += cpu ;
		if(this.cpu > maxCPU){
			this.cpu -=cpu;
			throw new ResourceAllocationException("cpu full");
		}
	}

	public void addNet(double net) throws ResourceAllocationException {
		this.net += net ;
		if(this.net > maxNET){
			this.net -=net;
			throw new ResourceAllocationException("net full");
		}
	}
	
	public void addDisk(double disk) throws ResourceAllocationException {
		this.disk += disk ;
		if(this.disk > maxDISK){
			this.disk -=disk;
			throw new ResourceAllocationException("disk full");
		}
	}
	
	public void addMem(double mem) throws ResourceAllocationException {
		this.mem += mem;
		if(this.mem > maxMEM){
			this.mem -=mem;
			throw new ResourceAllocationException("mem full");
		}
	}
	
	public void subtractCpu(double cpu){
		this.cpu -= cpu;
		if(cpu < 0){
			System.err.println("cpu less than 0");
		}
}

	public void subtractNet(double net){
		this.net -= net ;
		if(this.net < 0){
			System.err.println("net less than 0");
		}
	}
	
	public void subtractDisk(double disk){
		this.disk -= disk ;
		if(this.disk < 0){
			System.err.println("disk less than 0");
		}
	}
	
	public void subtractMem(double mem){
		this.mem -= mem;
		if(this.mem < 0){
			System.err.println("mem less than 0");
		}
	}

	public List<Application> iterate() {
		List<Application> toReschedule = new ArrayList<Application>();
		List<Application> temp = new ArrayList<Application>(appListe);
		appListe.clear();
		this.cpu = 0; this.disk = 0; this.net = 0; this.mem = 0;
		
		for(Application app : temp){
			//increase pointer
			app.increasePointer();
			try {
				this.addApplication(app);
			} catch (ResourceAllocationException e) {
				System.out.println(e.getErrorMsg());
				toReschedule.add(app);
			}
		}
		return toReschedule;
	}

	public double getUtilization(){
		double utilisation = 100-((getAvailableCPU()/maxCPU)*100);
		utilisation += 100-((getAvailableMEM()/maxMEM)*100);
		utilisation += 100-((getAvailableDISK()/maxDISK)*100);
		utilisation += 100-((getAvailableNET()/maxNET)*100);
		utilisation = utilisation/4;
		return utilisation;
	}


	
}
