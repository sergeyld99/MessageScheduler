package water.messagescheduler;
import java.io.StringWriter;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
//import org.w3c.dom.Node;

public class Message {
	private int id;
	private String subject;    //Тема сообщения
	private String text;       //Текст сообщения
	private String typeForm;   //Тип формы
	private String whereParams;//Параметры сообщения индивидуальные для каждого типа
	private int otKogo;        //От кого сообщение
	private int komu;        //От кого сообщение
	private String ipAddress; //ip адрес получателя сообщений
	private String loginKomu; //логин получателя сообщений
	private String nameKomu; //имя получателя сообщений
	private boolean isButton; //является ли сообщение кнопкой
	private Timestamp timeCreate = new Timestamp((new java.util.Date()).getTime()); //Дата-время создания сообщения
	private Timestamp timeEnd;    //Дата-время до которой актуально сообщение
	private Timestamp timeRead;    //Дата-время прочтения сообщения
	private boolean isSended = false;//Флаг того что сообщение отправлено

	/**
	 * @return the isSended
	 */
	public boolean isSended() {
		return isSended;
	}
	public String getNameKomu() {
		return nameKomu;
	}
	/**
	 * @param isSended the isSended to set
	 */
	public void setSended() {
		this.isSended = true;
	}
	public Message(int id, String subject, String text,String typeForm, String whereParams, int otKogo, int komu,String loginKomu,boolean isButton,Timestamp timeEnd) {
		super();
		this.id = id;
		this.subject = subject;
		this.text = text;
		this.typeForm = typeForm;
		this.whereParams = whereParams;
		this.otKogo = otKogo;
		this.komu = komu;
		this.loginKomu = loginKomu;
		this.isButton = isButton;
		this.timeEnd = timeEnd;
		//timeCreate = 
	}
	public boolean isButton() {
		return isButton;
	}
	public String getTypeForm() {
		return typeForm;
	}
	public void setTypeForm(String typeForm) {
		this.typeForm = typeForm;
	}
	public Timestamp getTimeCreate() {
		return timeCreate;
	}
	public Timestamp getTimeEnd() {
		return timeEnd;
	}
	public Timestamp getTimeRead() {
		return timeRead;
	}
	/**
	 * @param timeRead the timeRead to set
	 */
	public void setTimeRead(Timestamp timeRead) {
		this.timeRead = timeRead;
	}
	public void setTimeRead() {
		this.timeRead = new Timestamp((new java.util.Date()).getTime());
	}
	public void setButton(boolean isButton) {
		this.isButton = isButton;
	}
	public Message(ResultSet rs) throws SQLException {
		super();
		this.id = rs.getInt("number");
		this.subject = rs.getString("caption");
		this.text = rs.getString("message");
		this.typeForm = rs.getString("type_form");
		this.whereParams = rs.getString("where_params");
		this.otKogo = rs.getInt("id_login_otkogo");
		this.komu = rs.getInt("id_login_komu");
		this.loginKomu = rs.getString("login_komu");
		this.nameKomu = rs.getString("name_komu");
		this.isButton = (rs.getInt("is_button")>0);
		this.timeCreate = rs.getTimestamp("time_create");
		this.timeEnd = rs.getTimestamp("time_end");
	}
	
	public String toXML() throws ParserConfigurationException, TransformerException {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document document = builder.newDocument();
	 
	    Element messagePacket = document.createElement("MessagePacket");
	    document.appendChild(messagePacket);
	    //Node messagePacket = root.appendChild(document.createElement("MessagePacket"));
	    messagePacket.setAttribute("is_button", String.valueOf(isButton ? 1 : 0));
	    messagePacket.setAttribute("type_form", typeForm);
	    messagePacket.setAttribute("otkogo", String.valueOf(otKogo));
	    messagePacket.appendChild(document.createElement("id")).setTextContent(String.valueOf(id));
	    messagePacket.appendChild(document.createElement("Message")).setTextContent(text);
	    messagePacket.appendChild(document.createElement("Subject")).setTextContent(subject);
	    messagePacket.appendChild(document.createElement("WhereParams")).setTextContent(whereParams);
	    
	    DOMSource domSource = new DOMSource(document);
	    StringWriter writer = new StringWriter();
	    StreamResult result = new StreamResult(writer);
	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();
	    transformer.transform(domSource, result);	    
	    
	    return writer.toString();
	}
	
	public boolean isActual() {
		//Date timeNow = new Date((new java.util.Date()).getTime());
		//return (timeEnd==null || timeEnd.after(timeNow));
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
		result = prime * result + komu;
		result = prime * result + ((loginKomu == null) ? 0 : loginKomu.hashCode());
		result = prime * result + otKogo;
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((whereParams == null) ? 0 : whereParams.hashCode());
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
		Message other = (Message) obj;
		if (id != other.id) {
			return false;
		}
		if (ipAddress == null) {
			if (other.ipAddress != null) {
				return false;
			}
		} else if (!ipAddress.equals(other.ipAddress)) {
			return false;
		}
		if (komu != other.komu) {
			return false;
		}
		if (loginKomu == null) {
			if (other.loginKomu != null) {
				return false;
			}
		} else if (!loginKomu.equals(other.loginKomu)) {
			return false;
		}
		if (otKogo != other.otKogo) {
			return false;
		}
		if (subject == null) {
			if (other.subject != null) {
				return false;
			}
		} else if (!subject.equals(other.subject)) {
			return false;
		}
		if (text == null) {
			if (other.text != null) {
				return false;
			}
		} else if (!text.equals(other.text)) {
			return false;
		}
		if (whereParams == null) {
			if (other.whereParams != null) {
				return false;
			}
		} else if (!whereParams.equals(other.whereParams)) {
			return false;
		}
		return true;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getWhereParams() {
		return whereParams;
	}
	public void setWhereParams(String whereParams) {
		this.whereParams = whereParams;
	}
	public int getOtKogo() {
		return otKogo;
	}
	public void setOtKogo(int otKogo) {
		this.otKogo = otKogo;
	}
	public int getId() {
		return id;
	}
	public int getKomu() {
		return komu;
	}
	public void setKomu(int komu) {
		this.komu = komu;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	/**
	 * @return the loginKomu
	 */
	public String getLoginKomu() {
		return loginKomu;
	}
	/**
	 * @param loginKomu the loginKomu to set
	 */
	public void setLoginKomu(String loginKomu) {
		this.loginKomu = loginKomu;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String retString = "Message [id=\"" + id + "\", typeForm=\"" + typeForm + "\", subject=\"" + subject + "\", text=\"" + text + "\", whereParams=\"" + whereParams + 
				"\", otKogo=\"" + otKogo + "\", komu=\"" + komu + "\", ipAddress=\"" + ipAddress + "\", loginKomu=\"" + loginKomu + "\"";
		if (timeCreate!=null)
			retString += ", timeCreate=\"" + timeCreate+"\""; 
		if (timeEnd!=null)
			retString += ", timeEnd=\"" + timeEnd+"\""; 
		retString += " ]" ;
		return retString;
	}

}
