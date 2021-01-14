/*Форма контроля работы спуллера сообщений*/
package water.messagescheduler;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import water.messagescheduler.datamodel.MainTableModel;

public class PanelControl extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTabbedPane tabbedPanel = new JTabbedPane(JTabbedPane.TOP);
	//private ConcurrentHashMap<String,ThreadSendMessages> lstThreads;
	private JTable table = null;

	/**
	 * @return the tabbedPanel
	 */
	public JTabbedPane getTabbedPanel() {
		return tabbedPanel;
	}
	public void updateTable() {
		if (table!=null) table.updateUI();
	}
	final private JPanel jp = new JPanel(new BorderLayout());
	public PanelControl(ConcurrentHashMap<String,ThreadSendMessages> lstThreads) {
		super("Форма контроля работы спуллера сообщений");
		
		//this.lstThreads = lstThreads;
		tabbedPanel.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPanel.addTab("Установки", null, jp, "1");
		setSize(1024, 768);
		add(tabbedPanel);
		table = new JTable(new MainTableModel(lstThreads));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		jp.add(new JScrollPane(table),BorderLayout.CENTER);
		/*При закрытии фрейма, закроем все приложение*/
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public JPanel getJp() {
		return jp;
	}

	
}
