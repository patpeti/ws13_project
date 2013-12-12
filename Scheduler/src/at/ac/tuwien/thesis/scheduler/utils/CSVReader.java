package at.ac.tuwien.thesis.scheduler.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import at.ac.tuwien.thesis.scheduler.model.TimeSeriesHolder;
import at.ac.tuwien.thesis.scheduler.model.TimeSeriesModel;

public class CSVReader {

	public TimeSeriesModel readCSVs(File dir){
		
		TimeSeriesModel tsModel = new TimeSeriesModel();
		System.out.println("reading files...");
		for(File f : dir.listFiles()){
			if(!f.getName().endsWith("csv")) continue;
			BufferedReader br = null;
			String line;
			String cvsSplitBy = ";";
			boolean firstline = true;
			String tsName = f.getName();
			TimeSeriesHolder ts = new TimeSeriesHolder();
			String [] dimensions = null;
			try {
				 
				br = new BufferedReader(new FileReader(f));
				
				while ((line = br.readLine()) != null) {
		 
					String[] values = line.split(cvsSplitBy);
					if(firstline){
						for(int i = 0; i < values.length; i++){
							ts.addDimension(values[i]);
							dimensions = values;
						}
						firstline = false;
					}else{
						for(int i = 0; i < values.length; i++){
							ts.addDimValue(dimensions[i], Double.valueOf(values[i]));
						}
					}
		 
				}
				
				tsModel.addNewTimeSeries(tsName, ts);
		 
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
						System.out.println("File Reading finished.");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			
			
			
		}
		
		return tsModel;
	}
	
}
