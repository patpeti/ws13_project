package at.ac.tuwien.thesis.scheduler.model.interfaces;

import javax.swing.ListModel;

public interface IListModel extends ListModel<String>{

	public void addElement(String a);
	public void clear();
	public String getElementAt(int index);
	
}
