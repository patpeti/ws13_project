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

import at.ac.tuwien.thesis.scheduler.controller.ForecastController;
import at.ac.tuwien.thesis.scheduler.enums.ForecastType;

public class ForecastPanel {
	
	private ForecastController sc;
	JCheckBox chckbxNewCheckBox;
	JComboBox comboBox,comboBox1,comboBox2,comboBox3;
	
	
	public ForecastPanel(ForecastController settingscontroller){
		this.sc = settingscontroller;
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
		
		comboBox = new JComboBox();
		panel_6.add(comboBox);
		comboBox.setModel(new DefaultComboBoxModel(ForecastType.values()));
		
		comboBox1 = new JComboBox();
		comboBox1.setModel(new DefaultComboBoxModel(ForecastType.values()));
		panel_6.add(comboBox1);
		
		comboBox2 = new JComboBox();
		comboBox2.setModel(new DefaultComboBoxModel(ForecastType.values()));
		panel_6.add(comboBox2);
		
		comboBox3 = new JComboBox();
		comboBox3.setModel(new DefaultComboBoxModel(ForecastType.values()));
		panel_6.add(comboBox3);


		chckbxNewCheckBox = new JCheckBox("Same for all");
		chckbxNewCheckBox.setSelected(true);
		panel_sub.add(chckbxNewCheckBox, BorderLayout.NORTH);

		sc.setView(this);
		comboBox.addActionListener(sc);
		comboBox1.addActionListener(sc);
		comboBox2.addActionListener(sc);
		comboBox3.addActionListener(sc);
		button1.addActionListener(sc);
		button2.addActionListener(sc);
		
		return panel_sub;
	}
	
	public boolean isAllSameSelected(){
		return chckbxNewCheckBox.isSelected();
	}

	public JComboBox getComboBox() {
		return comboBox;
	}

	public JComboBox getComboBox1() {
		return comboBox1;
	}

	public JComboBox getComboBox2() {
		return comboBox2;
	}

	public JComboBox getComboBox3() {
		return comboBox3;
	}

	
	
}
