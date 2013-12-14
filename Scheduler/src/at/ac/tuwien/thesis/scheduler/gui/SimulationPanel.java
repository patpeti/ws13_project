package at.ac.tuwien.thesis.scheduler.gui;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import at.ac.tuwien.thesis.scheduler.controller.SimulationController;

public class SimulationPanel {

	private SimulationController sc;

	public SimulationPanel(SimulationController simController){
		this.sc = simController;
	}
	
	
	public JPanel getSimulationPanel(){
		
		JPanel simPanel = new JPanel();
		simPanel.setLayout(new BoxLayout(simPanel, BoxLayout.Y_AXIS));
		JButton simStart = new JButton("Start Simulaion");
		simPanel.add(simStart);
		return simPanel;
	}
}
