/*Класс процесса на SQL сервере*/
package water.messagescheduler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Process {
	private int id;
	private String user;
	private String db;
	private String ipAddress;
	private int port;
	private String command;
	private int time;
	private String state;
	private String info;
	
	public Process(ResultSet rs) throws SQLException {
		super();
		this.id = rs.getInt("id");
		this.user = rs.getString("user");
		this.db = rs.getString("db");
		this.ipAddress = "";
		this.port = 0;
		String[] host = rs.getString("host").split(":");
		if (host.length>0)
		   this.ipAddress = host[0];
		if (host.length>1)
			try{this.port = Integer.valueOf(host[1]);}catch(NumberFormatException e) {this.port = 0;}
		this.command = rs.getString("command");
		this.time = rs.getInt("time");
		this.state = rs.getString("state");
		this.info = rs.getString("info");
		
	}
	
	public int compareWithUserDb(Process p) {
		if (!this.getUser().equals(p.getUser())) return this.getUser().compareTo(p.getUser());
		if (!this.getDb().equals(p.getDb())) return this.getDb().compareTo(p.getDb());
		return 0;
	}
	
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}


	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}


	/**
	 * @return the db
	 */
	public String getDb() {
		return db;
	}


	/**
	 * @param db the db to set
	 */
	public void setDb(String db) {
		this.db = db;
	}


	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}


	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}


	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}


	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}


	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}


	/**
	 * @param command the command to set
	 */
	public void setCommand(String command) {
		this.command = command;
	}


	/**
	 * @return the time
	 */
	public int getTime() {
		return time;
	}


	/**
	 * @param time the time to set
	 */
	public void setTime(int time) {
		this.time = time;
	}


	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}


	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}


	/**
	 * @return the info
	 */
	public String getInfo() {
		return info;
	}


	/**
	 * @param info the info to set
	 */
	public void setInfo(String info) {
		this.info = info;
	}


	public Process(String user, String db, String ipAddress, String command, int time, String state, String info) {
		super();
		this.user = user;
		this.db = db;
		this.ipAddress = ipAddress;
		this.command = command;
		this.time = time;
		this.state = state;
		this.info = info;
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((command == null) ? 0 : command.hashCode());
		result = prime * result + ((db == null) ? 0 : db.hashCode());
		result = prime * result + id;
		result = prime * result + ((info == null) ? 0 : info.hashCode());
		result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
		result = prime * result + port;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + time;
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Process other = (Process) obj;
		if (command == null) {
			if (other.command != null) {
				return false;
			}
		} else if (!command.equals(other.command)) {
			return false;
		}
		if (db == null) {
			if (other.db != null) {
				return false;
			}
		} else if (!db.equals(other.db)) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (info == null) {
			if (other.info != null) {
				return false;
			}
		} else if (!info.equals(other.info)) {
			return false;
		}
		if (ipAddress == null) {
			if (other.ipAddress != null) {
				return false;
			}
		} else if (!ipAddress.equals(other.ipAddress)) {
			return false;
		}
		if (port != other.port) {
			return false;
		}
		if (state == null) {
			if (other.state != null) {
				return false;
			}
		} else if (!state.equals(other.state)) {
			return false;
		}
		if (time != other.time) {
			return false;
		}
		if (user == null) {
			if (other.user != null) {
				return false;
			}
		} else if (!user.equals(other.user)) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Process [id=" + id + ", user=" + user + ", db=" + db + ", ipAddress=" + ipAddress + ", port=" + port
				+ ", command=" + command + ", time=" + time + ", state=" + state + ", info=" + info + "]";
	}

}
