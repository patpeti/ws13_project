package at.ac.tuwien.thesis.scheduler.model.cloudModel;

public class ResourceAllocationException extends Exception {
	
	String msg;

	public ResourceAllocationException(){
		
	}

	public ResourceAllocationException(String string) {
		this.msg = string;
	}
	
	public String getErrorMsg(){
		return this.msg;
	}
}
