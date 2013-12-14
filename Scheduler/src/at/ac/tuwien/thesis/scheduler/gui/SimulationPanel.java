package at.ac.tuwien.thesis.scheduler.gui;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.border.EmptyBorder;

import at.ac.tuwien.thesis.scheduler.controller.SimulationController;
import at.ac.tuwien.thesis.scheduler.enums.DimReductionSelectionType;
import at.ac.tuwien.thesis.scheduler.enums.SimType;

public class SimulationPanel {

	private SimulationController sc;
	private JComboBox comboBox;
	private JComboBox comboBox_dimred;

	public SimulationPanel(SimulationController simController){
		this.sc = simController;
	}
	
	
	public JPanel getSimulationPanel(){
		
		JPanel simPanel = new JPanel();
		simPanel.setLayout(new BorderLayout(0, 0));
		
		
		JPanel left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		simPanel.add(left, BorderLayout.WEST);
		
		JLabel label1 = new JLabel("Confidance Level");
		label1.setBorder(new EmptyBorder(0, 0, 10, 0));
		left.add(label1);
		JLabel label2 = new JLabel("Sim Type");
		label2.setBorder(new EmptyBorder(0, 0, 10, 0));
		left.add(label2);
		JLabel label3 = new JLabel("Train/Test split");
		label3.setBorder(new EmptyBorder(0, 0, 10, 0));
		left.add(label3);
		JLabel label4 = new JLabel("Dim Reduction");
		label4.setBorder(new EmptyBorder(0, 0, 10, 0));
		left.add(label4);
		
		
		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		simPanel.add(right,BorderLayout.EAST);
		
		JSpinner spinner = new JSpinner();
		spinner.setValue(new Integer(90));
		right.add(spinner);
		
		comboBox = new JComboBox();
		right.add(comboBox);
		comboBox.setModel(new DefaultComboBoxModel(SimType.values()));
		
		JSpinner spinner2 = new JSpinner();
		spinner2.setValue(new Integer(50));
		right.add(spinner2);
		
		comboBox_dimred = new JComboBox();
		right.add(comboBox_dimred);
		comboBox_dimred.setModel(new DefaultComboBoxModel(DimReductionSelectionType.values()));
		
		
		JButton simStart = new JButton("Start Simulaion");
		simStart.addActionListener(sc);
		simPanel.add(simStart, BorderLayout.SOUTH);
		
		sc.setView(this);
		
		return simPanel;
	}
	
	public Integer getDimReductionFactor(){
		if(comboBox_dimred.equals(DimReductionSelectionType._2x)){
			return 2;
		}else if(comboBox_dimred.equals(DimReductionSelectionType._3x)){
			return 3;
		}else if(comboBox_dimred.equals(DimReductionSelectionType._4x)){
			return 4;
		}else if(comboBox_dimred.equals(DimReductionSelectionType._6x)){
			return 6;
		}else if(comboBox_dimred.equals(DimReductionSelectionType._8x)){
			return 8;
		}else if(comboBox_dimred.equals(DimReductionSelectionType._16x)){
			return 16;
		}else if(comboBox_dimred.equals(DimReductionSelectionType._32x)){
			return 32;
		}else if(comboBox_dimred.equals(DimReductionSelectionType._64x)){
			return 64;
		}else{
			return 1;
		}
	}
}
