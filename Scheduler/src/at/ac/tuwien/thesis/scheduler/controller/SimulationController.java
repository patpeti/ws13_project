package at.ac.tuwien.thesis.scheduler.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import at.ac.tuwien.thesis.scheduler.gui.SimulationPanel;
import at.ac.tuwien.thesis.scheduler.model.TimeSeriesModel;
import at.ac.tuwien.thesis.scheduler.utils.Simulator;

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
				this.startSimulation();
			}else{
				System.err.println("huh?");
			}
		}
	}
	private void startSimulation() {
		System.out.println("View Params: ");
		System.out.println("DimreductionFactor: "+view.getDimReductionFactor());
		System.out.println("TestTrainSplit: "+view.getTestTrainSplit());
		System.out.println("ConfidenceLevel: "+view.getConfidenceLevel());
		System.out.println("SimType: "+view.getSimulationType());
		
		Simulator sim = new Simulator(tsModel);
		sim.startSimulation(view.getSimulationType(), view.getConfidenceLevel(), view.getTestTrainSplit(), view.getDimReductionFactor());

	}
}
