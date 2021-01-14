package water.messagescheduler.test;

//import java.io.FileNotFoundException;
import java.io.IOException;

//import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
//import java.util.Iterator;
import java.util.Map.Entry;

//import org.ini4j.Ini;
//import org.ini4j.InvalidFileFormatException;
import org.junit.Test;

import water.messagescheduler.Message;
//import water.messagescheduler.DBHelper;
import water.messagescheduler.MessagesList;
import water.messagescheduler.Settings;

public class DBHelperTest {

	@Test
	public void test() throws IOException, SQLException {
		//fail("Not yet implemented");
		Settings ini = new Settings();
		//ini.load(new FileReader(file));
		MessagesList db = new MessagesList("mysql", ini.getString("MySQLServer", "UserName",""), ini.getString("MySQLServer", "PasswordForServer",""), ini.getString("MySQLServer", "BaseName",""),ini.getString("MySQLServer", "ServerName","localhost"),ini.getInt("MySQLServer", "ServerPort",3306));
		try {
			db.connect();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashMap<Integer, Message> messages = db.getMessages(new ArrayList<Integer>());
		for( Entry<Integer, Message> entry : messages.entrySet() ) {
			//String ipAddress = entry.getValue().getIpAddress(); //db.getIpAddressReceiverMessage(entry.getValue());
			System.out.println(entry.getValue());
		}
		System.out.println(db.getProcessList());
	}

}
