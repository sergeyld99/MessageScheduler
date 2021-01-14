/*Главный модуль*/
package water.messagescheduler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
//import java.util.HashSet;
import java.util.Map.Entry;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.ArrayUtils;
import javax.swing.ButtonGroup;


//import org.apache.logging.log4j

public class Main {
	/*Интервал опроса таймера*/
	private static int timerInterval = 100;
	/*Очередь сообщений*/
	private static ConcurrentHashMap<String,ThreadSendMessages> lstThreads = new ConcurrentHashMap<String,ThreadSendMessages>();
	/*Логгер*/
	private static final Logger log = LogManager.getLogger(Main.class.getName());

	private static PanelControl panel = null;

	public static PanelControl getPanel() {
		return panel;
	}
	/*Все id сообщений в потоках*/
	private static ArrayList<Integer> getIdMessagesString(){
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		Iterator<ThreadSendMessages> it = lstThreads.values().iterator();
		while (it.hasNext()){
		   ThreadSendMessages thread =  it.next();
		   numbers.addAll(thread.getIdMessgesArray());		   		   
		}
		return numbers;
	}
	private static HashSet<Integer> getIdMessagesSet(){
		HashSet<Integer> numbers = new HashSet();
		Iterator<ThreadSendMessages> it = lstThreads.values().iterator();
		while (it.hasNext()){
		   ThreadSendMessages thread =  it.next();
		   numbers.addAll(thread.getIdMessgesSet());		   		   
		}
		return numbers;
	}
	/*Удаляем мертвые потоки*/
	private static void deleteTerminatedthreads() {
		Iterator<ConcurrentHashMap.Entry<String, ThreadSendMessages>> iterator = lstThreads.entrySet().iterator();
		boolean isUpdate = false;
		while (iterator.hasNext()) {
			ConcurrentHashMap.Entry<String, ThreadSendMessages> entry = iterator.next();
			ThreadSendMessages thread = entry.getValue();
			if (!thread.isAlive() || thread.getState() == Thread.State.TERMINATED) {
				lstThreads.remove(entry.getKey());
				isUpdate = true;
			}
		}
		if (panel!=null && isUpdate) panel.updateTable();

	}
	private static void getAndSendMessages() throws IOException {
		MessagesList db = new MessagesList();
		try {
			db.connect();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			log.log(Level.ERROR, "Ошибка " + e);
		}
		HashMap<Integer, Message> messages = null;
		try {
			messages = db.getMessages(getIdMessagesSet());
		} catch (SQLException e) {
			e.printStackTrace();
			log.log(Level.ERROR, "Ошибка " + e);
		}
		if (messages!=null)
		for(Entry<Integer, Message> entry : messages.entrySet() ) {
			Message message = entry.getValue();
			String ipAddress = message.getIpAddress();
			if (ipAddress == null || ipAddress.isEmpty()) continue;
			ThreadSendMessages thread = lstThreads.get(ipAddress);
			if (thread == null) {
				try {
					thread = new ThreadSendMessages(ipAddress);
					thread.start();
				} catch (ClassNotFoundException | SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.log(Level.ERROR, "Ошибка " + e);
				}
				lstThreads.put(ipAddress, thread);
				if (panel!=null) panel.updateTable();
			}
			thread.addOrUpdateMessage(message);
		}
		try {
			db.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the log
	 */
	public static Logger getLog() {
		return log;
	}

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub

		Settings ini = null;
		try {
			ini = new Settings();
		} catch (IOException e) {
			e.printStackTrace();
			log.log(Level.ERROR, "Ошибка " + e);
		}
		/*Панель для отображения работы*/
		try {
		   panel = new PanelControl(lstThreads);
		   panel.setVisible(true);
		   String capt = ini.getString("Water_PARAMS", "Caption", "Спуллер сообщений");
		   panel.setTitle(capt);
		}
		catch(Exception e)
		{
			panel = null;
		}

		timerInterval = ini.getInt("Water_PARAMS", "TimerInterval", 100);
		while (true) {
			try {
				deleteTerminatedthreads();
				getAndSendMessages();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.log(Level.ERROR, "Ошибка " + e);
			}
			Thread.sleep(timerInterval);
		}

	}

}
