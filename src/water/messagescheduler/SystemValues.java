package water.messagescheduler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;

import com.mysql.jdbc.Statement;
/*Класс системных переменных из таблицы waterparams*/
public class SystemValues {
	private Connection connection;
	private TreeMap<String,String> systemvalues = new TreeMap<String,String>();

	public SystemValues(Connection connection) throws SQLException {
		super();
		this.connection = connection;
		reload();
	}
	
	public void reload() throws SQLException {
		String SQLString = "SELECT * FROM waterparams";
	    Statement stmt = (Statement) connection.createStatement();
	    ResultSet rs = stmt.executeQuery(SQLString);
	    systemvalues.clear();
		while (rs.next())
	 	{
			systemvalues.put(rs.getString("name_param"),rs.getString("value"));
		}
		rs.close();
		
	}
	
	public String getString(String key,String defaultValue) {
		String value =  systemvalues.get(key);
		if (value == null) value =  defaultValue;
		return value;
	}
	public Integer getInteger(String key,Integer defaultValue) {
		Integer value =  null;
		try {
		   value =  Integer.valueOf(systemvalues.get(key));
		}
		catch(NumberFormatException e)
		{}
		if (value == null) value =  defaultValue;
		return value;
	}
	

}
