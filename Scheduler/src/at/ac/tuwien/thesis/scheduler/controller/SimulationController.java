package at.ac.tuwien.thesis.scheduler.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import at.ac.tuwien.thesis.scheduler.gui.SimulationPanel;
import at.ac.tuwien.thesis.scheduler.model.TimeSeriesModel;

public class SimulationController implements ActionListener{

	SimulationPanel view;
	TimeSeriesModel tsModel;
	InputController inputcontroller;
	
	public SimulationController(InputController inputcontroller) {
		this.inputcontroller = inputcontroller;
	}

	public void setView(SimulationPanel simPanel) {
		this.view = simPanel;
	}
	
	public void setData(TimeSeriesModel tsModel) {
		this.tsModel = tsModel;
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() instanceof JButton){
			JButton button = (JButton) e.getSource();

			if(button.getActionCommand().equalsIgnoreCase("Start Simulaion")){
				this.StartForecast();
			}else{
				System.err.println("huh?");
			}
		}
	}
	private void StartForecast() {
		System.out.println("Starting Simulation: "+view.getDimReductionFactor());

	}
}
