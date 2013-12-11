package at.ac.tuwien.thesis.scheduler.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import at.ac.tuwien.thesis.scheduler.controller.listeners.ControllerCallbackListener;
import at.ac.tuwien.thesis.scheduler.model.ListModel;
import at.ac.tuwien.thesis.scheduler.model.TableModel;
import at.ac.tuwien.thesis.scheduler.model.TimeSeriesModel;
import at.ac.tuwien.thesis.scheduler.model.interfaces.IListModel;
import at.ac.tuwien.thesis.scheduler.model.interfaces.ITableModel;
import at.ac.tuwien.thesis.scheduler.utils.CSVReader;

public class InputController extends JFrame implements ActionListener, ListSelectionListener {
	
	private JFileChooser chooser;
	private String choosertitle = "Choose Directory of Time Series";
	private IListModel listmodel;
	private ITableModel tableModel;
	private TimeSeriesModel tsModel;
	ControllerCallbackListener mainWin;
	private ChartController chartcontroller;
	private SettingsController settingsController;

	public InputController(ControllerCallbackListener mainWindow) {
		listmodel = new ListModel();
		tableModel = new TableModel();
		settingsController = new SettingsController();
		mainWin = mainWindow;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getActionCommand().equalsIgnoreCase("Open Folder")){
			
			chooser = new JFileChooser(); 
		    chooser.setCurrentDirectory(new File(".//inputs"));
		    chooser.setDialogTitle(choosertitle);
		    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    // disable the "All files" option.
		    chooser.setAcceptAllFileFilterUsed(false);
		   
		    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
		      System.out.println("getCurrentDirectory(): "  +  chooser.getCurrentDirectory());
		      System.out.println("getSelectedFile() : "     +  chooser.getSelectedFile());
		      //1.Read File In
		      // TimeSeriesMOdel <- CsvReader.readAll(dir)
		      tsModel = new CSVReader().readCSVs(chooser.getSelectedFile());
		      //2.) fill list: TsModel.getkeys
		      this.getListModel().clear();
		      for (String key : tsModel.getTsModel().keySet()){
		    	  this.getListModel().addElement(key);
		      }
		      //3.) fill table (select first element)
		      String firstElemName = this.getListModel().getElementAt(0);
		      this.getTableModel().setData(tsModel.getTsModel().get(firstElemName));
		      
		      //3.b Create charts
		      chartcontroller.setData(tsModel.getTsForName(firstElemName));
		      
		      this.mainWin.FileLoadReady(this.getTableModel());
		   
		} else {
		      System.out.println("No Selection ");
		      }
		}
	}
	
	public IListModel getListModel(){
		return this.listmodel;
	}
	
	public ITableModel getTableModel(){
		return this.tableModel;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		System.out.println("ValueChanged");
		JList<String> list = (JList<String>) e.getSource();
		String elem = list.getSelectedValue();
		this.getTableModel().setData(tsModel.getTsModel().get(elem));
		
		//todo update chartcontroller
		if(tsModel != null){
			chartcontroller.setData(tsModel.getTsModel().get(elem));
			this.mainWin.FileLoadReady(this.getTableModel());
		}
	}

	public void addChartController(ChartController chartcontroller) {
		this.chartcontroller = chartcontroller;
	}

	public SettingsController getSettingsController(){
		return this.settingsController;
	}

}
