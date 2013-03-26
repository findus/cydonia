/**
 * 
 */
package de.findus.cydonia.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.findus.cydonia.server.GameServer;

/**
 * @author Findus
 *
 */
public class ServerBrowser implements ActionListener {

	public static void main(String[] args) {
		ServerBrowser s = new ServerBrowser();
		s.show();
	}
	
	private Thread replyListener;
	
	private JFrame frame;
	
	private DefaultListModel<String> model;
	
	private JList<String> list;
	
	private JCheckBox closeCB;
	
	public ServerBrowser() {
		initGUI();
	}
	
	public void show() {
		frame.setVisible(true);
		
		initReplyListener();
		multicastServerRequest();
	}
	
	public void initGUI() {
		frame = new JFrame(GameController.APPTITLE + " - Serverbrowser");
		frame.setMinimumSize(new Dimension(400, 300));
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        cleanup();
		        frame.setVisible(false);
		        frame.dispose();
		    }
		});
		
		model = new DefaultListModel<String>();
		list = new JList<String>(model);
		frame.add(list, BorderLayout.CENTER);
		
		JPanel topPanel = new JPanel(new FlowLayout());
		frame.add(topPanel, BorderLayout.NORTH);
		
		JPanel bottomPanel = new JPanel(new FlowLayout());
		frame.add(bottomPanel, BorderLayout.SOUTH);
		
		JButton addButton = new JButton("Add Server");
		addButton.setActionCommand("add");
		topPanel.add(addButton);
		addButton.addActionListener(this);
		
		JButton refreshButton = new JButton("Refresh List");
		refreshButton.setActionCommand("refresh");
		topPanel.add(refreshButton);
		refreshButton.addActionListener(this);
		
		JButton serverButton = new JButton("Start Server");
		serverButton.setActionCommand("startServer");
		topPanel.add(serverButton);
		serverButton.addActionListener(this);
		
		JButton joinButton = new JButton("Join");
		joinButton.setActionCommand("join");
		bottomPanel.add(joinButton);
		joinButton.addActionListener(this);
		
		closeCB = new JCheckBox("close after choosing one option");
		closeCB.setSelected(true);
		bottomPanel.add(closeCB);
		
		frame.pack();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("join")) {
			if(list.getSelectedValue() != null) {
				joinServer(list.getSelectedValue());
				cleanup();
				if(closeCB.isSelected()) {
					frame.setVisible(false);
					frame.dispose();
				}
			}
		}else if(e.getActionCommand().equals("add")) {
			String address = JOptionPane.showInputDialog(frame, "Please insert the name or IP of the server", "");
			if(address != null && address != "") {
				addServer(address);
			}
		}else if(e.getActionCommand().equals("refresh")) {
			list.removeAll();
			multicastServerRequest();
		}else if(e.getActionCommand().equals("startServer")) {
			startServer();
			multicastServerRequest();
		}
	}

	private void joinServer(final String address) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				GameController controller = new GameController();
				controller.start(address);
			}
		}).start();
	}
	
	private void startServer() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				GameServer server = new GameServer();
				server.setConfigFrameVisible(true);
			}
		}).start();
	}

	private void cleanup() {
		if(replyListener != null) {
			if(replyListener.isAlive()) {
				replyListener.interrupt();
			}
		}
	}
	
	private void addServer(String address) {
		if(!model.contains(address)) {
			model.addElement(address);
		}
	}
	
	private void multicastServerRequest() {
		try {
			// Create the socket but we don't bind it as we are only going to send data
			MulticastSocket s = new MulticastSocket();

			byte buf[] = new byte[1];
			// Create a DatagramPacket 
			DatagramPacket pack = new DatagramPacket(buf, buf.length,
					InetAddress.getByName("224.0.0.1"), 55000);
			s.send(pack);
			
			s.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initReplyListener() {
		// if thread is already initialised, dont do it again
		if(replyListener != null) {
			// if thread is not running, start it
			if(!replyListener.isAlive()) {
				replyListener.start();
			}
			return;
		}
		
		byte buf[] = new byte[1];
		final DatagramPacket pack = new DatagramPacket(buf, buf.length);

		try {
			final DatagramSocket s = new DatagramSocket(55001, InetAddress.getLocalHost());
			System.out.println(s.getLocalAddress());
			s.setSoTimeout(5000);

			replyListener = new Thread(new Runnable() {
				@Override
				public void run() {

					while(!Thread.interrupted()) {
						try {
							System.out.println("Listening for Servers...");
							s.receive(pack);
							System.out.println("Found Server: " + pack.getAddress());
							addServer(pack.getAddress().getHostAddress());
						} catch (SocketTimeoutException e) {
							
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					s.close();
					System.out.println("Socket closed");
				}
			});

			replyListener.start();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}
