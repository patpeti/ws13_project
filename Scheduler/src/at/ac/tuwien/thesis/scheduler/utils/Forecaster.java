package at.ac.tuwien.thesis.scheduler.utils;

import java.util.List;

import at.ac.tuwien.thesis.scheduler.Constants;
import at.ac.tuwien.thesis.scheduler.enums.ForecastType;
import at.ac.tuwien.thesis.scheduler.utils.forecasts.DECOMPForecaster;
import at.ac.tuwien.thesis.scheduler.utils.forecasts.DSHWForecaster;
import at.ac.tuwien.thesis.scheduler.utils.forecasts.FourierForecaster;
import at.ac.tuwien.thesis.scheduler.utils.forecasts.HWForecaster;
import at.ac.tuwien.thesis.scheduler.utils.forecasts.HourlyDSHWForecaster;
import at.ac.tuwien.thesis.scheduler.utils.forecasts.HourlyTBATSForecaster;
import at.ac.tuwien.thesis.scheduler.utils.forecasts.MaxForecaster;
import at.ac.tuwien.thesis.scheduler.utils.forecasts.MeanForecaster;
import at.ac.tuwien.thesis.scheduler.utils.forecasts.NNARForecaster;
import at.ac.tuwien.thesis.scheduler.utils.forecasts.NaiveForecaster;
import at.ac.tuwien.thesis.scheduler.utils.forecasts.TBATSForecaster;

public class Forecaster {

	public List<Double> calculateForecast(List<Double> valueList,ForecastType forecastType, Integer dr_factor, Integer horizon) {
		
		if(horizon == null){
			horizon = Constants.Horizon;
			horizon = (int) Math.ceil(Constants.Horizon/dr_factor);
		}
		
		if(forecastType.equals(ForecastType.NAIVE)){
			NaiveForecaster naive = new NaiveForecaster();
			return naive.forecast(valueList,dr_factor,horizon);
		}else if(forecastType.equals(ForecastType.NNAR)){
			NNARForecaster nnar = new NNARForecaster();
			return nnar.forecast(valueList,dr_factor,horizon);
		}else if(forecastType.equals(ForecastType.MEAN)){
			MeanForecaster mean = new MeanForecaster();
			return mean.forecast(valueList, dr_factor,horizon);
		}else if(forecastType.equals(ForecastType.MAX)){
			MaxForecaster max = new MaxForecaster();
			return max.forecast(valueList, dr_factor,horizon);
		}else if(forecastType.equals(ForecastType.DECOMP)){
			DECOMPForecaster dec = new DECOMPForecaster();
			return dec.forecast(valueList, dr_factor,horizon);
		}else if(forecastType.equals(ForecastType.HW)){
			HWForecaster hw = new HWForecaster();
			return hw.forecast(valueList, dr_factor,horizon);
		}else if(forecastType.equals(ForecastType.TBATS)){
			TBATSForecaster tbats = new TBATSForecaster();
			return tbats.forecast(valueList, dr_factor,horizon);
		}else if(forecastType.equals(ForecastType.FOURIER)){
			FourierForecaster fourier = new FourierForecaster();
			return fourier.forecast(valueList, dr_factor,horizon);
		}else if(forecastType.equals(ForecastType.DSHW)){
			DSHWForecaster dswh = new DSHWForecaster();
			return dswh.forecast(valueList, dr_factor, horizon);
		}else if(forecastType.equals(ForecastType.HOURLYDSWH)){
			HourlyDSHWForecaster dswh = new HourlyDSHWForecaster();
			return dswh.forecast(valueList, dr_factor, horizon);
		}else if(forecastType.equals(ForecastType.HOURLYTBATS)){
				HourlyTBATSForecaster tbats = new HourlyTBATSForecaster();
				return tbats.forecast(valueList, dr_factor, horizon);
		}else{
			return null;
		}
	}

	

}
