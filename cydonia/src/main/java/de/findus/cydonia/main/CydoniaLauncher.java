package de.findus.cydonia.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.findus.cydonia.server.GameServer;

/**
 * Game Launcher of Cydonia.
 *
 */
public class CydoniaLauncher extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 4828567160092774297L;

	private GameServer server;
	
	private JButton serverbtn;
	
	private JCheckBox closeCB;

	/**
	 * Starts the game.
	 * @param args commands (unused)
	 */
	public static void main(String[] args) {
        CydoniaLauncher launcher = new CydoniaLauncher();
		launcher.setVisible(true);
        launcher.toFront();
    }
	
	
	public CydoniaLauncher() {
		setTitle("Cydonia Launcher");
		
		serverbtn = new JButton("Start Server");
		serverbtn.setActionCommand("server_start");
		serverbtn.addActionListener(this);
		
		JButton clientbtn = new JButton("Start Game");
		clientbtn.setActionCommand("client_start");
		clientbtn.addActionListener(this);
		
		closeCB = new JCheckBox("close after choosing one option");
		closeCB.setSelected(true);
		
		JPanel cpane = new JPanel(new BorderLayout());
		cpane.add(serverbtn, BorderLayout.WEST);
		cpane.add(clientbtn, BorderLayout.EAST);
		cpane.add(closeCB, BorderLayout.SOUTH);
		
		this.add(cpane);
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.setPreferredSize(new Dimension(250, 100));
		this.setSize(250, 100);
		this.setLocationByPlatform(true);
	}
	
	private void startServer() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				server = new GameServer();
				server.start();
			}
		}).start();
		
		closeIfUserWants();
	}
	
	private void startClient() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				GameController controller = new GameController();
				controller.start();
			}
		}).start();
		
		closeIfUserWants();
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if("server_start".equals(e.getActionCommand())) {
			this.startServer();
		}else if("client_start".equals(e.getActionCommand())) {
			this.startClient();
		}
	}
	
	private void closeIfUserWants() {
		if(closeCB.isSelected()) {
			this.setVisible(false);
			this.dispose();
		}
	}
}
