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
import at.ac.tuwien.thesis.scheduler.controller.ControllerCallbackListener;
import at.ac.tuwien.thesis.scheduler.controller.InputController;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.Component;
import java.awt.Rectangle;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import at.ac.tuwien.thesis.scheduler.enums.Forecasts;
import at.ac.tuwien.thesis.scheduler.gui.ControlPanel;

public class MainWindow implements ControllerCallbackListener {

	private JFrame frame;
	private JTable table;
	JPanel panel_1,panel_2;
	private ChartController chartcontroller;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
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
		frame.setBounds(100, 100, 1024, 768);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmOpenFolder = new JMenuItem("Open Folder");
		mnFile.add(mntmOpenFolder);
		mntmOpenFolder.addActionListener(inputcontroller);
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);
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
		frame.getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		
		inputcontroller.getListModel().addElement("Please Load Directory");
	
		chartcontroller = new ChartController();
		inputcontroller.addChartController(chartcontroller);
	
		
		panel_2 = new JPanel();
		frame.getContentPane().add(panel_2, BorderLayout.EAST);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.Y_AXIS));
		
		
		ControlPanel cp = new ControlPanel(inputcontroller);
		panel.add(cp.getControlPanel());
		
		panel.validate();
			
		
		
		
	}

	@Override
	public void FileLoadReady(TableModel model) {
		// TODO Auto-generated method stub
		table.setModel(model);
		table.repaint();
		panel_1.removeAll();
		panel_1.add(chartcontroller.createChart("CPU"));
		panel_1.add(chartcontroller.createChart("NET"));
		panel_1.add(chartcontroller.createChart("MEM"));
		panel_1.add(chartcontroller.createChart("DISK"));
//		panel_1.add(chartcontroller.createDemoPanel());
//		
//		panel_2.add(chartcontroller.createDemoPanel());
//		panel_2.add(chartcontroller.createDemoPanel());
		panel_1.validate();
		panel_2.validate();
	}
}
