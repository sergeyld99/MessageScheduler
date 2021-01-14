package water.messagescheduler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
//import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.jdbc.Statement;

public class DBHelper {

	private String driverName; //Драйвер
	private String user; //Юзер
	private String password; //Пароль
	private String dbName; //База данных
	private String host; //Сервер
	private int port; //порт
	
	private String URL;
	private Connection connection = null;
	private Statement stmt = null;
	protected ResultSet rs = null;
	protected SystemValues systemValues;
	
	/*Логгер*/
	private static final Logger log = LogManager.getLogger(DBHelper.class.getName());
	/*Список процессов на сервере*/
	protected TreeSet<Process> processList = new TreeSet<Process>(new Comparator<Process>() {
        public int compare(Process o1, Process o2) {
            return o1.toString().compareTo(o2.toString());
        }
	
	});
	
	public TreeSet<Process> getProcessList(){
		return processList;
	}

	public void setProcessList() throws SQLException{
		String SQLString = "SHOW PROCESSLIST";
		rs = execSQL(SQLString);
		processList.clear();
		while (rs.next())
		{
			processList.add(new Process(rs));
		}
		rs.close();
	}
	
	public String getIpAddressByUser(String user) {
		if (user == null || dbName == null) return null;
		for (Process p : processList)
			if (user.compareToIgnoreCase(p.getUser())==0 && dbName.compareToIgnoreCase(p.getDb())==0)
				return p.getIpAddress();
		return null;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public DBHelper() throws IOException {
		// TODO Auto-generated constructor stub
		super();
		Settings ini = new Settings();
		//ini.load(new FileReader(file));
		this.driverName = "mysql";
		this.user = ini.getString("MySQLServer", "UserName","");
		this.password = ini.getString("MySQLServer", "PasswordForServer","");
		this.dbName = ini.getString("MySQLServer", "BaseName","");
		this.host = ini.getString("MySQLServer", "ServerName","localhost");
		this.port = ini.getInt("MySQLServer", "ServerPort",3306);
	    URL = "jdbc:"+driverName+"://"+host;
	    if (port>0)
	    	URL += ":"+String.valueOf(port);
	    URL += "/"+dbName;
	}

	public DBHelper(String driverName, String user, String password, String dbName,String host,int port) {
		super();
		this.driverName = driverName;
		this.user = user;
		this.password = password;
		this.dbName = dbName;
		this.host = host;
		if (port<=0)
		   this.port = 3306;
		else
		   this.port = port;
	    URL = "jdbc:"+driverName+"://"+host;
	    if (port>0)
	    	URL += ":"+String.valueOf(port);
	    URL += "/"+dbName+"?autoReconnect=true&characterEncoding=UTF-8";
	}
	
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Connection connect() throws ClassNotFoundException, SQLException {
		connection = DriverManager.getConnection(URL, user, password);
		if (connection!=null)
		{
			execSQL("SET NAMES 'utf8'");
			execSQL("SET CHARACTER SET 'utf8'");
			systemValues = new SystemValues(connection);
		}
		return connection;
	}
	
	/**
	 * @return the connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * @param connection the connection to set
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * @return the systemValues
	 */
	public SystemValues getSystemValues() {
		return systemValues;
	}

	/*Установим автокомит*/
	public void setAutoCommit() throws SQLException {
		if (connection != null)
			connection.setAutoCommit(true);
	}
	/*Сбросим автокомит*/
	public void releaseAutoCommit() throws SQLException {
		if (connection != null)
			connection.setAutoCommit(false);
	}
	/*комит*/
	public void сommit() throws SQLException {
		if (connection != null)
			connection.commit();
	}
	/*ролбэк*/
	public void rollback() throws SQLException {
		if (connection != null)
			connection.rollback();
	}
	
	
	public void close() throws SQLException {
		closeResult();
		if (connection != null)
			connection.close();
		connection = null;
	}

	public void closeResult() throws SQLException {
		if (rs != null)
			rs.close();
		if (stmt != null)
			stmt.close();
		rs = null;
		stmt = null;
	}
	
	public ResultSet execSQL(String sqlString) throws SQLException {
	    stmt = (Statement) connection.createStatement();
	    rs = stmt.executeQuery(sqlString);
		return rs;
	}
	
	public boolean setMessageRead(Message message) throws SQLException{
		String sqlString = "UPDATE system_messages SET time_read=Now(),buser_read='"+message.getLoginKomu()+"@"+message.getIpAddress()+"' WHERE number="+String.valueOf(message.getId());
			Statement stmt = (Statement) connection.createStatement();
			stmt.executeUpdate(sqlString);
			stmt.close();
			message.setTimeRead();
		/*
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Main.getLog().error("Ошибка "+e);
			log.log(Level.ERROR, "Ошибка " + e);
			return false;
		}
		*/
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		closeResult();
		close();
		super.finalize();
	}

}
