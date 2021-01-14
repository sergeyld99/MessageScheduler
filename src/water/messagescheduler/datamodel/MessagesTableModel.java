package water.messagescheduler.datamodel;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import water.messagescheduler.Message;
public class MessagesTableModel implements TableModel {

	private final int colCount = 6;
	private String[] colNames = {"id", "Пользователь", "Дата сообщения", "Время жизни", "Прочитано","Сообщение"};
	//private ConcurrentSkipListSet<TableModelListener> listeners = new ConcurrentSkipListSet<TableModelListener>();
	private Set<TableModelListener> listeners = new HashSet<TableModelListener>();
	private ConcurrentSkipListMap<Integer,Message> lstMessages;
	public MessagesTableModel(ConcurrentSkipListMap<Integer,Message> lstMessages) {
        this.lstMessages = lstMessages;
    }
	@Override
	public void addTableModelListener(TableModelListener listener) {
		// TODO Auto-generated method stub
		synchronized(listeners){
		   listeners.add(listener);
		}
		
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		// TODO Auto-generated method stub
		switch (columnIndex)
		{
			case 0: 
				return Integer.class;
			/*
			case 2: 
				return Timestamp.class;
			case 3: 
				return Date.class;
			*/
		}
		return String.class;
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return colCount;
	}

	@Override
	public String getColumnName(int columnIndex) {
		// TODO Auto-generated method stub
		if (columnIndex>=0 && columnIndex<colCount)
			return colNames[columnIndex];
		return null;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		if (lstMessages!=null)
			return lstMessages.size();
		return 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		
		if (lstMessages!=null && rowIndex>=0 && rowIndex<=lstMessages.size()) {
			//ArrayList<Integer> keys = Collections.list(lstMessages.keys());
			//keys.sort(null);
			Set<Integer> keys = lstMessages.keySet();
			Integer key =  (Integer) keys.toArray()[rowIndex];
			//Integer key =  keys.get(rowIndex);//(Integer) lstMessages.keySet().toArray()[rowIndex];
			//ConcurrentSkipListSet<Integer> keys = (ConcurrentSkipListSet<Integer>) lstMessages.keySet();
			switch (columnIndex)
			{
				case 0: 
					return key;
				case 1:
					return lstMessages.get(key).getLoginKomu();
				case 2:
					return lstMessages.get(key).getTimeCreate();
				case 3:
					return lstMessages.get(key).getTimeEnd();
				case 4:
					return lstMessages.get(key).getTimeRead();
				case 5:
					return lstMessages.get(key).getText();
			}
		}
		
		return "";
	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener listener) {
		// TODO Auto-generated method stub
		synchronized(listeners){
			listeners.remove(listener);
		}
	}

	@Override
	public void setValueAt(Object arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

}
