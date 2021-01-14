package water.messagescheduler;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
//import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
//import java.util.HashSet;
//import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.LinkedHashMap;
import java.util.Vector;
//import java.util.Set;
//import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;
//import javax.swing.JTabbedPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import javax.swing.event.TableModelListener;
//import javax.swing.table.TableModel;

import water.messagescheduler.DBHelper;
import water.messagescheduler.Main;
import water.messagescheduler.Message;
import water.messagescheduler.datamodel.MessagesTableModel;

/*Класс потока который будет отправлять сообщения клиентам */
public class ThreadSendMessages extends Thread {
	/*Объект для блокировки*/
	//private final ReentrantLock lock = new ReentrantLock();
	
	private String ipAddress;
    /*Для какого логина создан поток*/
	private int idLogin = 0;
	public int getTotalMessages() {
		return totalMessages;
	}
	public int getTotalSendingMessages() {
		return totalSendingMessages;
	}

	/*Панель контроля*/
	private JPanel jpControl = null;
	/*Флаг прерывания потока*/
	private boolean flagTerminated = false;
	/*Имя пользователя в потоке*/
	private String userName = "";
	/*Общее количество сообщений*/
	private int totalMessages = 0;
	/*Общее количество отправленных сообщений*/
	private int totalSendingMessages = 0;

	public String getUserName() {
		return userName;
	}

	/*Очередь сообщений*/
	private ConcurrentSkipListMap<Integer,Message> lstMessages = new ConcurrentSkipListMap<Integer,Message>();
	/*История очереди сообщений*/
	private ConcurrentSkipListMap<Integer,Message> lstMessagesALL = new ConcurrentSkipListMap<Integer,Message>();
	private DBHelper dbHelper;
	int portSocket;
	private  Socket socket;
    JTable table = null;
	private final int _SLEEP_TIMEOUT_FOR_READ_SOCKET = 70;
	private final int _TIME_OUT_SOCKET_MSECONDS = 1000;
	private final int _MAX_COUNT_MESSAGES_IN_ARCHIVE = 25;
	

	/*Логгер*/
	private static final Logger log = LogManager.getLogger(ThreadSendMessages.class.getName());

	/*Вернем массив кеев*/
	public ArrayList<Integer> getIdMessgesArray() {
		//return Collections.list(lstMessages.keys());
		return new ArrayList<Integer>(lstMessages.keySet());
	}
	public Set<Integer> getIdMessgesSet() {
		//return Collections.list(lstMessages.keys());
		return lstMessages.keySet();
	}
	
	public ThreadSendMessages(String ipAddress) throws IOException, SQLException, ClassNotFoundException {
		super();
		this.ipAddress = ipAddress;
		dbHelper = new DBHelper();
		dbHelper.connect();
		dbHelper.setAutoCommit();
		portSocket = dbHelper.getSystemValues().getInteger("SERVER_MESSAGES_PORT", 11111);
		setPanelControl();
	}
	
	/*Установка вкладки контроля*/
	private void setPanelControl() {
		if (Main.getPanel() == null)
			return;
		jpControl = new JPanel(new BorderLayout());
		synchronized(Main.getPanel()) {
			Main.getPanel().getTabbedPanel().addTab(ipAddress, null, jpControl, "");
		    table = new JTable(new MessagesTableModel(lstMessagesALL));
		    table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		   
		    TableColumnModel tcm = table.getColumnModel();
		    tcm.getColumn(0).setPreferredWidth(40);
		    tcm.getColumn(1).setPreferredWidth(80);
		    tcm.getColumn(2).setPreferredWidth(160);
		    tcm.getColumn(3).setPreferredWidth(160);
		    tcm.getColumn(4).setPreferredWidth(160);
		    tcm.getColumn(5).setPreferredWidth(300);
		    jpControl.add(new JScrollPane(table),BorderLayout.CENTER);
		    /*
		    table.getModel().addTableModelListener(new TableModelListener() {
				@Override
				public void tableChanged(TableModelEvent e) {
					// TODO Auto-generated method stub
					System.out.println(e);
				}
		    });
		    */
		    
		    
		}
	}
	/*Удаление вкладки контроля*/
	private void removePanelControl() {
		if (Main.getPanel() == null)
			return;
		synchronized(Main.getPanel()) {
			Main.getPanel().getTabbedPanel().remove(jpControl);
		}
	}
	
	public int getIdLogin() {
		return idLogin;
	}

	private boolean connectToClient(Message message /*Для логов*/) {
 		if (socket != null && socket.isConnected() && !socket.isClosed())
			return true;
		try {
			socket = new Socket(ipAddress, portSocket);
			socket.setSoTimeout(_TIME_OUT_SOCKET_MSECONDS);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			String login = message.getLoginKomu();
			if (login == null) login = "";
			log.log(Level.ERROR, "Ошибкa, IP адрес " + ipAddress + ",login " + login + ",id " + String.valueOf(message.getId()) + " " + e);
			flagTerminated = true;
			this.interrupt();
			return false;
		}
		return true;
	}
	

	public String getIpAddress() {
		return ipAddress;
	}
	/*Сверяем IpАдрес с адресом потока*/
	public boolean isIp(String ipAddress) {
		return this.ipAddress.equals(ipAddress);
	}
	
	public void addOrUpdateMessage(Message message) {
		synchronized(lstMessages) {
			//lock.lock();
			try {
				if (!lstMessages.containsKey(message.getId()))
				{
					lstMessages.put(message.getId(), message);
					totalMessages++;
				}
				idLogin = message.getKomu();
				userName = message.getNameKomu();
			}
			catch(ConcurrentModificationException e)
			{
				e.printStackTrace();
				log.log(Level.ERROR, "Ошибка крнкуренции " + e);							
			}
			finally {
				//lock.unlock();
			}
			
		}
		/*Добавим в историю сообщений*/
		synchronized(lstMessagesALL) {
			//lock.lock();
			try {
				while(lstMessagesALL.size() > _MAX_COUNT_MESSAGES_IN_ARCHIVE - 1)
				{
					lstMessagesALL.remove(lstMessagesALL.entrySet().iterator().next().getKey());				
				}
				lstMessagesALL.put(message.getId(), message);
				updateTableUI();
			}
			catch(ConcurrentModificationException e)
			{
				e.printStackTrace();
				log.log(Level.ERROR, "Ошибка крнкуренции " + e);							
			}
			finally {
				//lock.unlock();
			}
			
		}
	}
	/*Обновим данные в таблице*/
	private void updateTableUI() {
		if (table!=null)
		synchronized(table) {
			table.updateUI();
			//table.getModel().fireTableDataChanged();
		}

	}
	
	private boolean sendMessage(Message message) throws IOException, InterruptedException {
		if (!connectToClient(message))
			return false;
		byte[] body;
		try {
			body = message.toXML().getBytes();
		} catch (ParserConfigurationException | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.log(Level.ERROR, "Ошибка " + e);
			return false;
		}
		Packet packet = new Packet(PacketType.FIELD_TYPE_STRING_,body.length,body);
        InputStream sin = socket.getInputStream();
        OutputStream sout = socket.getOutputStream();
        sout.write(packet.toBytes());
        log.info("Sending packet "+packet+", message "+message);
        Thread.sleep(_SLEEP_TIMEOUT_FOR_READ_SOCKET);
        byte[] buffer_read = new byte[64*1024];
        int len_read = sin.read(buffer_read);
        if (len_read<=0)
        {
            log.info("Receiving packet (id="+String.valueOf(message.getId())+") is false");
        	return false;
        }
        Packet readPacket = new Packet(buffer_read);
        log.info("Receiving packet (id="+String.valueOf(message.getId())+")"+readPacket);
		socket.close();
		return (readPacket!=null && readPacket.getPacketType() == PacketType.ACK);
	}
	
	private void sendMessages() throws ParserConfigurationException, TransformerException, InterruptedException, SQLException {
		//for(Entry<Integer, Message> entry : lstMessages.entrySet()) {
		Iterator<Entry<Integer, Message>> it = lstMessages.entrySet().iterator();
		while (it.hasNext() && !flagTerminated) {
			Entry<Integer, Message>  entry = it.next();
			Message message = (Message) entry.getValue();
			//if (!message.isSended()) //Если собщение отослано
			try {
				if (!message.isActual() /*|| message.isSended()*/ || sendMessage(message)) {
					if (message.isActual())
					{
					   dbHelper.setMessageRead(message);
					   totalSendingMessages++;
					   updateTableUI();
					}
					synchronized(lstMessages) {
						try {it.remove();} 
						catch(ConcurrentModificationException e)
						{
							e.printStackTrace();
							log.log(Level.ERROR, "Ошибка крнкуренции " + e);							
						}
						//lstMessages.remove(entry.getKey());
					}
				}
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.log(Level.ERROR, "Ошибка " + e);
				flagTerminated = true;
				this.interrupt();
				//break;
			}
		}
	}
	
	public void run(){
		while (!Thread.interrupted() && !flagTerminated){
			
				try {
					sendMessages();
				} catch (ParserConfigurationException | TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.log(Level.ERROR, "Ошибка " + e);
					break;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.log(Level.ERROR, "Ошибка " + e);
					break;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.log(Level.ERROR, "Ошибка " + e);
					break;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.log(Level.ERROR, "Ошибка " + e);
				}//Поспим 
		}
		removePanelControl();
		
	}
	
	
}
