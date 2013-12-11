package at.ac.tuwien.thesis.scheduler.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import at.ac.tuwien.thesis.scheduler.enums.Forecasts;
import at.ac.tuwien.thesis.scheduler.gui.ControlPanel;

public class SettingsController implements ActionListener{
	ControlPanel view;

	public SettingsController() {
		
	}

	public void setView(ControlPanel controlPanel) {
		this.view = controlPanel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JComboBox cb = (JComboBox) e.getSource();
		if(view.isAllSameSelected()){
			Forecasts selection = (Forecasts) cb.getSelectedItem();
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
