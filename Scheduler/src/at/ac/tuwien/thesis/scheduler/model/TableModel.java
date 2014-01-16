package at.ac.tuwien.thesis.scheduler.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import at.ac.tuwien.thesis.scheduler.model.interfaces.ITableModel;

public class TableModel  extends AbstractTableModel implements ITableModel{

	
	private Map<String, List<Double>> tsMap = null;

	public TableModel() {
//		keys = data.keySet().toArray(new String[data.size()]);
	}

	@Override
	public int getRowCount() {
		if(tsMap == null){
			return 1;
		}else{
			
			return tsMap.get(tsMap.keySet().iterator().next()).size(); //TODO this is not really elegant
		}
//		return data.size();
	}

	@Override
	public int getColumnCount() {
		if(tsMap == null) return 1;
		else{
			return tsMap.keySet().size();
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		if(tsMap == null){
			return null;
		}
		int i = 0;
		for(String key : tsMap.keySet()){
			if(i==col) {
				return tsMap.get(key).get(row);
			}
			i++;
		}
		return null;
		
//		if (col == 0) {
//			return keys[row];
//		} else {
//			return data.get(keys[row]);
//		}
	}

	@Override
	public String getColumnName(int col) {
		if(tsMap == null){
			return "Empty";
		}else{
			
			return tsMap.keySet().toArray()[col].toString();
		}
	}
	
	public void setData(TimeSeriesHolder ts){
		if(ts != null)
		this.tsMap = ts.getTs();
//		keys = (String[]) tsMap.keySet().toArray();
//		ts.getTs() //Map<Dim,List<Double>>
	}
	
	public Set<String> getDims(){
		return tsMap.keySet();
	}

}
