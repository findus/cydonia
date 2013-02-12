/**
 * 
 */
package de.findus.cydonia.server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 * @author Findus
 *
 */
@SuppressWarnings("serial")
public class ServerConfigFrame extends JFrame implements ActionListener {

	
	private GameServer server;
	
	private JTextField commandInput;
	
	public ServerConfigFrame(GameServer server) {
		this.server = server;
		
		this.setTitle("Cydonia Server");
		
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.setLocationByPlatform(true);
		initGUI();
	}
	
	private void initGUI() {
		this.setPreferredSize(new Dimension(300, 100));
		
		JButton stopServerButton = new JButton("Stop Server");
		stopServerButton.addActionListener(this);
		stopServerButton.setActionCommand("stopServer");
		this.add(stopServerButton, BorderLayout.NORTH);
		
		commandInput = new JTextField();
		commandInput.addActionListener(this);
		commandInput.setActionCommand("sendCommand");
		commandInput.setPreferredSize(new Dimension(400, 20));
		this.add(commandInput, BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("stopServer".equals(e.getActionCommand())) {
			server.stop(false);
		}else if("sendCommand".equals(e.getActionCommand())) {
			server.handleCommand(commandInput.getText());
		}
	}
}
