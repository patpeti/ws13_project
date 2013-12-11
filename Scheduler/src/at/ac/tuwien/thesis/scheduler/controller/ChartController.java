package at.ac.tuwien.thesis.scheduler.controller;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.List;

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

import at.ac.tuwien.thesis.scheduler.model.TimeSeriesHolder;

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
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
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

//		DateAxis axis = (DateAxis) plot.getDomainAxis();
//		axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));

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
//		         s1.add(new Month(2, 2001), 181.8);
		         		 
		         // ******************************************************************
		         //  More than 150 demo applications are included with the JFreeChart
		         //  Developer Guide...for more information, see:
		         //
		         //  >   http://www.object-refinery.com/jfreechart/guide.html
		         //
		         // ******************************************************************
		 
		         XYSeriesCollection dataset = new XYSeriesCollection();
		         dataset.addSeries(s1);
//		         dataset.addSeries(s2);
		 
		         return dataset;
		 
		     }
	

	public void setData(TimeSeriesHolder timeSeries) {
		this.tsHolder = timeSeries;
	}
	
	public JPanel createChart(String dim){
		JFreeChart chart = createChart(createDataset(dim),dim);
		ChartPanel panel = new ChartPanel(chart);
		panel.setFillZoomRectangle(true);
		panel.setMouseWheelEnabled(true);
		return panel;
	}
	
}
