package at.ac.tuwien.thesis.scheduler.model.interfaces;

import javax.swing.table.TableModel;

import at.ac.tuwien.thesis.scheduler.model.TimeSeriesHolder;

public interface ITableModel extends TableModel{

	public void setData(TimeSeriesHolder ts);
	
}
