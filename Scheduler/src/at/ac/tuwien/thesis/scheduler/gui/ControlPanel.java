package at.ac.tuwien.thesis.scheduler.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import at.ac.tuwien.thesis.scheduler.controller.InputController;
import at.ac.tuwien.thesis.scheduler.enums.Forecasts;

public class ControlPanel {
	
	private InputController ic;

	
	
	public ControlPanel(InputController inputcontroller){
		this.ic = inputcontroller;
	}
	
	public JPanel getControlPanel(){
		JPanel panel_sub = new JPanel();
		panel_sub.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_3 = new JPanel();
		panel_sub.add(panel_3, BorderLayout.CENTER);
		
		JPanel panel_4 = new JPanel();
		panel_sub.add(panel_4, BorderLayout.SOUTH);
		
		JButton button1 = new JButton("Show Forecast");
		panel_4.add(button1);
		
		JButton button2 = new JButton("Start Simulation");
		panel_4.add(button2);
		
		JPanel panel_5 = new JPanel();
		panel_3.add(panel_5);
		panel_5.setLayout(new BoxLayout(panel_5, BoxLayout.Y_AXIS));
		
		JLabel lab1 = new JLabel("CPU");
		lab1.setBorder(new EmptyBorder(0, 0, 10, 0));
		lab1.setVerticalAlignment(SwingConstants.TOP);
		lab1.setAlignmentY(Component.TOP_ALIGNMENT);
		lab1.setBounds(new Rectangle(0, 10, 0, 0));
		panel_5.add(lab1);
		JLabel lab2 = new JLabel("NET");
		lab2.setBorder(new EmptyBorder(0, 0, 10, 0));
		panel_5.add(lab2);
		JLabel lab3 = new JLabel("MEM");
		lab3.setBorder(new EmptyBorder(0, 0, 10, 0));
		panel_5.add(lab3);
		JLabel lab4 = new JLabel("DISK");
		panel_5.add(lab4);
		
		JPanel panel_6 = new JPanel();
		panel_3.add(panel_6);
		panel_6.setLayout(new BoxLayout(panel_6, BoxLayout.Y_AXIS));
		
		JComboBox comboBox = new JComboBox();
		panel_6.add(comboBox);
		comboBox.setModel(new DefaultComboBoxModel(Forecasts.values()));
		
		JComboBox comboBox1 = new JComboBox();
		comboBox1.setModel(new DefaultComboBoxModel(Forecasts.values()));
		panel_6.add(comboBox1);
		
		JComboBox comboBox2 = new JComboBox();
		comboBox2.setModel(new DefaultComboBoxModel(Forecasts.values()));
		panel_6.add(comboBox2);
		
		JComboBox comboBox3 = new JComboBox();
		comboBox3.setModel(new DefaultComboBoxModel(Forecasts.values()));
		panel_6.add(comboBox3);
		
		
	JCheckBox chckbxNewCheckBox = new JCheckBox("Same for all");
	chckbxNewCheckBox.setSelected(true);
		panel_sub.add(chckbxNewCheckBox, BorderLayout.NORTH);
		
		return panel_sub;
		
	}
	
}
