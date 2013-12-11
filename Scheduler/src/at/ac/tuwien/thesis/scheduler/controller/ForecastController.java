package at.ac.tuwien.thesis.scheduler.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import at.ac.tuwien.thesis.scheduler.enums.ForecastType;
import at.ac.tuwien.thesis.scheduler.gui.ControlPanel;
import at.ac.tuwien.thesis.scheduler.model.TimeSeriesModel;

public class ForecastController implements ActionListener{
	ControlPanel view;
	TimeSeriesModel tsModel;
	InputController inputcontroller;
	
	public ForecastController(InputController inputcontroller) {
		this.inputcontroller = inputcontroller;
	}

	public void setView(ControlPanel controlPanel) {
		this.view = controlPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() instanceof JButton){
			JButton button = (JButton) e.getSource();
			
			if(button.getActionCommand().equalsIgnoreCase("Show Forecast")){
				this.ShowForecast();
			}else if(button.getActionCommand().equalsIgnoreCase("Start Simulation")){
				this.startSim();
			}
			
		}else if(e.getSource() instanceof JComboBox){
			JComboBox cb = (JComboBox) e.getSource();
			if(view.isAllSameSelected()){
				ForecastType selection = (ForecastType) cb.getSelectedItem();
				JComboBox cb1 = view.getComboBox();
				JComboBox cb2 = view.getComboBox1();
				JComboBox cb3 = view.getComboBox2();
				JComboBox cb4 = view.getComboBox3();
				cb1.setSelectedItem(selection);
				cb2.setSelectedItem(selection);
				cb3.setSelectedItem(selection);
				cb4.setSelectedItem(selection);
			}
		}
		
		
	}

	private void startSim() {

		System.out.println("Strat Sim");
	}

	private void ShowForecast() {
		System.out.println("Show Forecast");
		this.inputcontroller.getMainWindow().getForecastPanel().removeAll();
		this.inputcontroller.getMainWindow().getForecastPanel().add(this.inputcontroller.getChartController().createForecast("CPU",(ForecastType)view.getComboBox().getSelectedItem()));
		this.inputcontroller.getMainWindow().getForecastPanel().add(this.inputcontroller.getChartController().createForecast("NET",(ForecastType)view.getComboBox().getSelectedItem()));
		this.inputcontroller.getMainWindow().getForecastPanel().add(this.inputcontroller.getChartController().createForecast("MEM",(ForecastType)view.getComboBox().getSelectedItem()));
		this.inputcontroller.getMainWindow().getForecastPanel().add(this.inputcontroller.getChartController().createForecast("DISK",(ForecastType)view.getComboBox().getSelectedItem()));
		this.inputcontroller.getMainWindow().getFrame().validate();
	}

	public void setData(TimeSeriesModel tsModel) {
		this.tsModel = tsModel;
		
	}

}
