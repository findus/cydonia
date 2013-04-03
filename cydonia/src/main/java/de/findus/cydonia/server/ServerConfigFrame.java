/**
 * 
 */
package de.findus.cydonia.server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import de.findus.cydonia.server.GameServer.ServerStateListener;

/**
 * @author Findus
 *
 */
@SuppressWarnings("serial")
public class ServerConfigFrame extends JFrame implements ActionListener, ServerStateListener, Console {

	
	private GameServer server;
	
	private JTextArea consoleOutput;
	private JTextField commandInput;
	private JButton serverButton;
	
	JLabel nameLabel;
	JLabel mapLabel;
	JLabel stateLabel;
	
	private DefaultListModel<String> listmodel;
	private JList<String> maplist;
	
	public ServerConfigFrame(GameServer server) {
		this.server = server;
		
		this.setTitle("Cydonia Server");
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationByPlatform(true);
		initGUI();
		loadMapNames();
		
		server.registerStateListener(this);
	}
	
	private void initGUI() {
		JTabbedPane tabbedPane = new JTabbedPane();
		this.add(tabbedPane, BorderLayout.CENTER);
		
		// Main panel
		JPanel mainPanel = new JPanel(new BorderLayout());
		tabbedPane.addTab("Main", mainPanel);
		
		serverButton = new JButton("Start Server");
		mainPanel.add(serverButton, BorderLayout.NORTH);
		serverButton.addActionListener(this);
		serverButton.setActionCommand("server");
		
		listmodel = new DefaultListModel<String>();
		maplist = new JList<String>(listmodel);
		mainPanel.add(maplist, BorderLayout.CENTER);
		maplist.setMinimumSize(new Dimension(400, 300));
		maplist.setPreferredSize(new Dimension(400, 300));
		
		// Info panel
		JPanel infoPanel = new JPanel(new GridBagLayout());
		tabbedPane.addTab("Info", infoPanel);
		nameLabel = new JLabel("My Server");
		mapLabel = new JLabel();
		stateLabel = new JLabel("Stopped");
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		infoPanel.add(new JLabel("Server Infos:"), c);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		infoPanel.add(new JLabel("Servername: "), c);
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		infoPanel.add(nameLabel, c);
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		infoPanel.add(new JLabel("Map: "), c);
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		infoPanel.add(mapLabel, c);
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		infoPanel.add(new JLabel("State: "), c);
		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		infoPanel.add(stateLabel, c);
		
		// Console panel
		JPanel consolePanel = new JPanel(new BorderLayout());
		tabbedPane.addTab("Console", consolePanel);
		
		consoleOutput = new JTextArea("Console Output");
		consolePanel.add(consoleOutput, BorderLayout.CENTER);
		consoleOutput.setMinimumSize(new Dimension(400, 300));
		consoleOutput.setPreferredSize(new Dimension(400, 300));
		
		commandInput = new JTextField();
		consolePanel.add(commandInput, BorderLayout.SOUTH);
		commandInput.addActionListener(this);
		commandInput.setActionCommand("sendCommand");
		commandInput.setMinimumSize(new Dimension(400, 20));
	}

	private void loadMapNames() {
		InputStream is = this.getClass().getResourceAsStream("/de/findus/cydonia/level/levels.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line;
		try {
			while ((line = br.readLine()) != null) {
				listmodel.addElement(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if ("server".equals(e.getActionCommand())) {
			if(server.isRunning()) {
				server.stop(false);
				this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}else {
				if(maplist.getSelectedValue() != null) {
					this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					server.start(maplist.getSelectedValue());
				}
			}
		}else if("sendCommand".equals(e.getActionCommand())) {
			server.handleCommand(commandInput.getText());
		}
	}

	@Override
	public void stateChanged() {
		if(server.isRunning()) {
			serverButton.setText("Stop Server");
		}else {
			serverButton.setText("StartServer");
		}
		if(server.getWorldController() != null && server.getWorldController().getMap() != null) { 
			mapLabel.setText(server.getWorldController().getMap().getName());
		}
	}

	@Override
	public void writeLine(String line) {
		this.consoleOutput.append("\n" + line);
	}
}
