package de.findus.cydonia.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
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
		serverbtn = new JButton("Start Server");
		serverbtn.setActionCommand("server_start");
		serverbtn.addActionListener(this);
		
		JButton clientbtn = new JButton("Start Game");
		clientbtn.setActionCommand("client_start");
		clientbtn.addActionListener(this);
		
		JPanel cpane = new JPanel(new BorderLayout());
		cpane.add(serverbtn, BorderLayout.NORTH);
		cpane.add(clientbtn, BorderLayout.SOUTH);
		
		this.add(cpane);
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.setPreferredSize(new Dimension(100, 100));
		this.setSize(200, 100);
	}
	
	private void startServer() {
		server = new GameServer();
		server.start();
		serverbtn.setText("Stop Server");
		serverbtn.setActionCommand("server_stop");
	}
	
	private void stopServer() {
		server.stop(true);
//		server.destroy();
		serverbtn.setText("Start Server");
		serverbtn.setActionCommand("server_start");
	}
	
	private void startClient() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				GameController controller = new GameController();
				controller.start();
			}
		}).start();
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if("server_start".equals(e.getActionCommand())) {
			this.startServer();
		}else if("server_stop".equals(e.getActionCommand())) {
			this.stopServer();
		}else if("client_start".equals(e.getActionCommand())) {
			this.startClient();
		}
	}
}
