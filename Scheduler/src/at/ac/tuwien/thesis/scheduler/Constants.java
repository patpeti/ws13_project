package at.ac.tuwien.thesis.scheduler;

public class Constants {

	public static  Integer DataPerDay = 1440;
	public static  Integer DataPerWeek = DataPerDay*7;
	public static  Integer Horizon = DataPerWeek;
	
	public static final Integer maxCPU = 90*3;
	public static final Integer maxNET = 7000*3;
	public static final Integer maxMEM = 25*3;
	public static final Integer maxDISK = 4*4;
	
	public static final Integer vmCPU = 0;
	public static final Integer vmNET = 0;
	public static final Integer vmMEM = 0;
	public static final Integer vmDISK = 0;
	
}
