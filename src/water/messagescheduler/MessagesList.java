package water.messagescheduler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
//import java.util.Map.Entry;

public class MessagesList extends DBHelper {
	private LinkedHashMap<Integer,Message> lstMessages = new LinkedHashMap<Integer,Message>();
	
	public MessagesList() throws IOException {
		super();
	}
	public MessagesList(String driverName, String user, String password, String dbName, String host, int port) {
		super(driverName, user, password, dbName, host, port);
		// TODO Auto-generated constructor stub
	}
	public HashMap<Integer,Message> getMessages(ArrayList<Integer> noIncludeList/*Те ктого не надо включать в список*/) throws SQLException {
		setProcessList();
		String SQLString = "SELECT s_m.*";
		SQLString += ",(SELECT real_name FROM loginkassanumber l WHERE number=s_m.id_login_otkogo) AS name_otkogo";
		SQLString += ",(SELECT real_name FROM loginkassanumber l WHERE number=s_m.id_login_komu) AS name_komu";
		SQLString += ",(SELECT login_name FROM loginkassanumber l WHERE number=s_m.id_login_komu) AS login_komu";
		SQLString += " FROM system_messages s_m WHERE time_read IS NULL AND (time_end IS NULL OR time_end>=Now())";
		SQLString += " AND (time_start IS NULL OR time_start<=Now())";
		if (noIncludeList!=null && noIncludeList.size()>0)
			SQLString += " AND NOT s_m.number IN ("+noIncludeList.toString().replace("[", "").replace("]", "")+")";
		SQLString += " ORDER BY s_m.number";
		//ResultSet res = 
		rs = execSQL(SQLString);
		lstMessages.clear();
		while (rs.next()) {
			Message message = new Message(rs);
			//getIpAddressReceiverMessage(message);
			message.setIpAddress(getIpAddressByUser(message.getLoginKomu()));
			lstMessages.put(message.getId(), message);
		}
		rs.close();
		//for( Entry<Integer, Message> entry : lstMessages.entrySet() ) getIpAddressReceiverMessage(entry.getValue());
		return lstMessages;

	}
	public HashMap<Integer,Message> getMessages(HashSet<Integer> noIncludeList/*Те ктого не надо включать в список*/) throws SQLException {
		return getMessages(new ArrayList<Integer>(noIncludeList));
	}
	public HashMap<Integer, Message> getLstMessages() {
		return lstMessages;
	}
	/*Определим IP адрес по логину*/
	private String getIpAddressByLogin(String loginName) throws SQLException {
		// TODO Auto-generated method stub
		String ipAddress = "";
		String SQLString = "SHOW PROCESSLIST";
		rs = execSQL(SQLString);		
		while (rs.next() && ipAddress.isEmpty())
		{
			String user = rs.getString("user");
			String dbName = rs.getString("db");
			if (user.compareToIgnoreCase(loginName)==0 && dbName.compareToIgnoreCase(this.getDbName())==0)
				ipAddress = rs.getString("host");
		}
		rs.close();
		return ipAddress;
	}
	/*Определим IP адрес получателя сообщения*/
	public String getIpAddressReceiverMessage(Message message) throws SQLException {
		String ipAddress = "";
		String loginName = "";
		String SQLString = "SELECT login_name FROM loginkassanumber WHERE number = "+String.valueOf(message.getKomu());
		rs = execSQL(SQLString);		
		if (rs.next())	loginName = rs.getString("login_name");
		rs.close();
		if (loginName!=null && !loginName.isEmpty())
			ipAddress = getIpAddressByLogin(loginName);
		message.setIpAddress(ipAddress);
		return ipAddress;
	}

}
