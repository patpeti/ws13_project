package at.ac.tuwien.thesis.scheduler;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import at.ac.tuwien.thesis.scheduler.controller.ChartController;
import at.ac.tuwien.thesis.scheduler.controller.InputController;
import at.ac.tuwien.thesis.scheduler.controller.ForecastController;
import at.ac.tuwien.thesis.scheduler.controller.SimulationController;
import at.ac.tuwien.thesis.scheduler.gui.ForecastPanel;
import at.ac.tuwien.thesis.scheduler.gui.SimulationPanel;

public class MainWindow{

	private JFrame frame;
	private JTable table;
	JPanel panel_1,panel_2;
	private ChartController chartcontroller;
	private ForecastController settingscontroller;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.getFrame().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		InputController inputcontroller = new InputController(this);
		
		frame = new JFrame();
		getFrame().setBounds(100, 100, 1200, 768);
		getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		getFrame().setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmOpenFolder = new JMenuItem("Open Folder");
		mnFile.add(mntmOpenFolder);
		mntmOpenFolder.addActionListener(inputcontroller);
		JPanel panel = new JPanel();
		getFrame().getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(200, 200));
		panel.add(scrollPane);
		
		JList<String> list = new JList<String>(inputcontroller.getListModel());
		scrollPane.setViewportView(list);
		list.addListSelectionListener(inputcontroller);
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setPreferredSize(new Dimension(500, 200));
		panel.add(scrollPane_1);
		
		table = new JTable();
		
		scrollPane_1.setViewportView(table);
		

		panel_1 = new JPanel();
		getFrame().getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		
		inputcontroller.getListModel().addElement("Please Load Directory");
	
		//******** CONTROLLER BINDINGS ********
		
		chartcontroller = new ChartController();
		inputcontroller.addChartController(chartcontroller);
	
		settingscontroller = new ForecastController(inputcontroller);
		inputcontroller.addSettingsController(settingscontroller);
		
		SimulationController simController = new SimulationController(inputcontroller);
		inputcontroller.addSimulationController(simController);
		
		panel_2 = new JPanel();
		getFrame().getContentPane().add(panel_2, BorderLayout.EAST);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.Y_AXIS));
		
		
		ForecastPanel cp = new ForecastPanel(inputcontroller.getSettingsController());
		panel.add(cp.getControlPanel());
		
		SimulationPanel sp = new SimulationPanel(simController);
		panel.add(sp.getSimulationPanel());
		
		panel.validate();
			
		
		
		
	}

	public void FileLoadReady(TableModel model) {
		table.setModel(model);
		table.repaint();
		panel_1.removeAll();
		panel_2.removeAll();
		panel_1.add(chartcontroller.createChart("CPU"));
		panel_1.add(chartcontroller.createChart("NET"));
		panel_1.add(chartcontroller.createChart("MEM"));
		panel_1.add(chartcontroller.createChart("DISK"));
//		
//		panel_2.add(chartcontroller.createDemoPanel());
		panel_1.validate();
		panel_2.validate();
		this.getFrame().validate();
	}
	
	public JPanel getForecastPanel(){
		return this.panel_2;
	}

	public JFrame getFrame() {
		return frame;
	}

}
