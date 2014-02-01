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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import at.ac.tuwien.thesis.scheduler.Constants;
import at.ac.tuwien.thesis.scheduler.controller.SimulationController;
import at.ac.tuwien.thesis.scheduler.enums.DimReductionSelectionType;
import at.ac.tuwien.thesis.scheduler.enums.SimType;

public class SimulationPanel {

	private SimulationController sc;
	private JComboBox comboBox;
	private JComboBox comboBox_dimred;
	private JSpinner spinner,spinner2,spinner3,spinner4;

	public SimulationPanel(SimulationController simController){
		this.sc = simController;
	}
	
	
	public JPanel getSimulationPanel(){
		
		JPanel simPanel = new JPanel();
		simPanel.setLayout(new BorderLayout(0, 0));
		
		
		JPanel left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		simPanel.add(left, BorderLayout.WEST);
		
		JLabel label1 = new JLabel("Confidence Level");
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
		JLabel label5 = new JLabel("DataPerDay");
		label5.setBorder(new EmptyBorder(0, 0, 10, 0));
		left.add(label5);
		JLabel label6 = new JLabel("Horizon");
		label6.setBorder(new EmptyBorder(0, 0, 10, 0));
		left.add(label6);
		
		
		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		simPanel.add(right,BorderLayout.EAST);
		
		spinner = new JSpinner();
		spinner.setValue(new Integer(90));
		right.add(spinner);
		
		comboBox = new JComboBox();
		right.add(comboBox);
		comboBox.setModel(new DefaultComboBoxModel(SimType.values()));
		
		spinner2 = new JSpinner();
		spinner2.setValue(new Integer(50));
		right.add(spinner2);
		
		comboBox_dimred = new JComboBox();
		right.add(comboBox_dimred);
		comboBox_dimred.setModel(new DefaultComboBoxModel(DimReductionSelectionType.values()));
		
		spinner3 = new JSpinner();
		spinner3.setValue(new Integer(Constants.DataPerDay));
		right.add(spinner3);
		
		
		spinner4 = new JSpinner();
		spinner4.setValue(new Integer(Constants.Horizon));
		right.add(spinner4);
		
		spinner3.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Constants.DataPerDay = (Integer) spinner3.getValue();
				System.out.println("changed " + Constants.DataPerDay);
			}
		});
		
		spinner4.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Constants.Horizon = (Integer) spinner4.getValue();
				System.out.println("changed " + Constants.Horizon);
			}
		});
		
		JButton simStart = new JButton("Start Simulaion");
		simStart.addActionListener(sc);
		simPanel.add(simStart, BorderLayout.SOUTH);
		
		sc.setView(this);
		
		return simPanel;
	}
	
	public Integer getDimReductionFactor(){

		if(comboBox_dimred.getSelectedItem().equals(DimReductionSelectionType._2x)){
			return 2;
		}else if(comboBox_dimred.getSelectedItem().equals(DimReductionSelectionType._3x)){
			return 3;
		}else if(comboBox_dimred.getSelectedItem().equals(DimReductionSelectionType._4x)){
			return 4;
		}else if(comboBox_dimred.getSelectedItem().equals(DimReductionSelectionType._6x)){
			return 6;
		}else if(comboBox_dimred.getSelectedItem().equals(DimReductionSelectionType._8x)){
			return 8;
		}else if(comboBox_dimred.getSelectedItem().equals(DimReductionSelectionType._16x)){
			return 16;
		}else if(comboBox_dimred.getSelectedItem().equals(DimReductionSelectionType._32x)){
			return 32;
		}else if(comboBox_dimred.getSelectedItem().equals(DimReductionSelectionType._64x)){
			return 64;
		}else if(comboBox_dimred.getSelectedItem().equals(DimReductionSelectionType._128x)){
			return 128;
		}else if(comboBox_dimred.getSelectedItem().equals(DimReductionSelectionType._256x)){
			return 256;
		}else if(comboBox_dimred.getSelectedItem().equals(DimReductionSelectionType._1024x)){
			return 1024;
		}else{
			return 1;
		}
	}
	
	public SimType getSimulationType(){
		if(comboBox.getSelectedItem().equals(SimType.NAIVE)){
			return SimType.NAIVE;
		}else if(comboBox.getSelectedItem().equals(SimType.SHORTTERM)){
			return SimType.SHORTTERM;
		}else if(comboBox.getSelectedItem().equals(SimType.LONGTERM)){
			return SimType.LONGTERM;
		}else if(comboBox.getSelectedItem().equals(SimType.LONGTERM2)){
				return SimType.LONGTERM2;
			}
		return null;
	}
	
	public Integer getTestTrainSplit(){
		return (Integer) spinner2.getValue();
	}
	
	public Integer getConfidenceLevel(){
		return (Integer) spinner.getValue();
	}
}
