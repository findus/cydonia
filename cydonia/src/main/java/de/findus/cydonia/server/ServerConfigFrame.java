/**
 * 
 */
package de.findus.cydonia.server;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * @author Findus
 *
 */
@SuppressWarnings("serial")
public class ServerConfigFrame extends JFrame implements ActionListener {

	
	private GameServer server;
	
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
		this.add(stopServerButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("stopServer".equals(e.getActionCommand())) {
			server.stop(false);
		}
	}
}
