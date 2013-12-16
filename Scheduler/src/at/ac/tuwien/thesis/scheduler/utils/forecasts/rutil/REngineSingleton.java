package at.ac.tuwien.thesis.scheduler.utils.forecasts.rutil;

import org.rosuda.JRI.Rengine;

public class REngineSingleton {

	private static Rengine re;
	
	private REngineSingleton(){
		re=new Rengine(null, false, new TextConsole());
        System.out.println("Rengine created, waiting for R");
		// the engine creates R is a new thread, so we should wait until it's ready
        if (!re.waitForR()) {
            System.out.println("Cannot load R");
        }
        
	}
	
	public static Rengine getREngine(){
		if(re == null){
			new REngineSingleton();
			return re;
		}else{
			return re;
		}
	}
	
}
