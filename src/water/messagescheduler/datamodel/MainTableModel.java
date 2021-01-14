package water.messagescheduler.datamodel;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.table.AbstractTableModel;

import water.messagescheduler.ThreadSendMessages;
/*Класс модели данных таблицы на главной странице*/
public class MainTableModel extends AbstractTableModel {
	/*Данные*/
	private ConcurrentHashMap<String,ThreadSendMessages> lstThreads;
	public MainTableModel(ConcurrentHashMap<String,ThreadSendMessages> lstThreads){
		this.lstThreads = lstThreads;
	}
	
	
	private String[]   columnNames = {   //Названия колонок
            "#"
           ,"IP адрес"
           ,"Пользователь"
           ,"Всего сообщений"
           ,"Отправлено сообщений"
          };  	
	
	public String getColumnName(int col){    //Выдает название колонки
	    return columnNames[col];
	  }
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return lstThreads.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		Set<String> keys = lstThreads.keySet();
		String key =  (String) keys.toArray()[rowIndex];
		switch (columnIndex)
		{
			case 0: 
				return rowIndex + 1;
			case 1:
				return key;
			case 2:
				return lstThreads.get(key).getUserName();
			case 3:
				return new Integer(lstThreads.get(key).getTotalMessages());
			case 4:
				return new Integer(lstThreads.get(key).getTotalSendingMessages());
		}
		
		return "";
	}

}
