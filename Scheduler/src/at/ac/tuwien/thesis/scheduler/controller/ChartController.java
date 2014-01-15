package at.ac.tuwien.thesis.scheduler.controller;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import at.ac.tuwien.thesis.scheduler.enums.ForecastType;
import at.ac.tuwien.thesis.scheduler.model.TimeSeriesHolder;
import at.ac.tuwien.thesis.scheduler.utils.Forecaster;

public class ChartController {
	
	private TimeSeriesHolder tsHolder;

	private JFreeChart createChart(XYDataset dataset,String dim) {

		JFreeChart chart = ChartFactory.createXYLineChart(
				dim,  // title
				"Time",             // x-axis label
				"Usage",   // y-axis label
				dataset,            // data
				PlotOrientation.VERTICAL,
				false,               // create legend?
				false,               // generate tooltips?
				false               // generate URLs?
				);

		chart.setBackgroundPaint(Color.white);
		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);
		plot.setAxisOffset(new RectangleInsets(2.0, 2.0, 2.0, 2.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		XYItemRenderer r = plot.getRenderer();
		if (r instanceof XYLineAndShapeRenderer) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
			renderer.setBaseShapesVisible(false);
			renderer.setBaseShapesFilled(true);
			renderer.setDrawSeriesLineAsPath(true);
			
		}

		return chart;

	}

	private XYDataset createDataset(String dim) {


		List<Double> valueListe = tsHolder.getDimension(dim);

		XYSeries s1 = new XYSeries(dim);
		int i=1;
		for(Double value : valueListe){
			s1.add(i,value);
			i++;
		}
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(s1);

		return dataset;

	}


	public void setData(TimeSeriesHolder timeSeries) {
		this.tsHolder = timeSeries;
	}
	
	public JPanel createChart(String dim){
		JFreeChart chart = createChart(createDataset(dim),dim);
		if(dim.equals("CPU")) {
			chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.MAGENTA);
		}else if(dim.equals("NET")){
			chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.ORANGE);
		}else if(dim.equals("DISK")){
			chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.BLUE);
		}else{
			chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.GREEN);
		}
		ChartPanel panel = new ChartPanel(chart);
		panel.setFillZoomRectangle(true);
		panel.setMouseWheelEnabled(true);
		return panel;
	}
	
	public JPanel createForecast(String dim, ForecastType forecastType, Integer dr_factor){
		JFreeChart chart = createChart(createForecastData(dim,forecastType,dr_factor),dim);
		chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.RED);
		ChartPanel panel = new ChartPanel(chart);
		panel.setFillZoomRectangle(true);
		panel.setMouseWheelEnabled(true);
		return panel;
	}

	private XYDataset createForecastData(String dim, ForecastType forecastType, Integer dr_factor) {
		Forecaster f = new Forecaster();
		List<Double> values = f.calculateForecast(tsHolder.getDimension(dim),forecastType,dr_factor,null);
		if(values == null) return null;
		XYSeries s1 = new XYSeries(dim);
		int i = tsHolder.getDimension(dim).size();
		for(Double value : values){
			s1.add(i,value);
			i++;
		}
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(s1);

		return dataset;
	}

}
