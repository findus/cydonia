/**
 * 
 */
package de.findus.cydonia.main;

import java.io.IOException;

import com.jme3.network.Client;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;

import de.findus.cydonia.server.InputUpdate;
import de.findus.cydonia.server.PlayerInputState;
import de.findus.cydonia.server.PlayerPhysic;
import de.findus.cydonia.server.WorldStateUpdate;

/**
 * The central connection controller.
 * @author Findus
 *
 */
public class ServerConnector {
	private Client client;
	private GameController controller;
	private Thread senderLoop;
	private boolean senderRunning = false;
	
	/**
	 * Constructor.
	 * @param controller the game controller
	 */
	public ServerConnector(GameController controller) {
		this.controller = controller;
		this.senderLoop = new Thread(new InputSenderLoop());
	}
	
	/**
	 * Connects to the specified server.
	 * @param address IPv4 address of the server
	 * @param port listening port of the server
	 */
	public void connectToServer(String address, int port) {
		try {
			client = Network.connectToServer(address, port);
			Serializer.registerClass(WorldStateUpdate.class);
			Serializer.registerClass(PlayerPhysic.class);
			Serializer.registerClass(InputUpdate.class);
			Serializer.registerClass(PlayerInputState.class);
			client.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Closes the connection to the server.
	 */
	public void disconnectFromServer() {
		this.client.close();
	}
	
	/**
	 * Starts the input sender loop.
	 */
	public void startInputSender() {
		this.senderRunning = true;
		this.senderLoop.start();
	}
	
	/**
	 * Stops the input sender loop.
	 */
	public void stopInputSender() {
		this.senderRunning = false;
	}
	
	/**
	 * Adds a listener for messages from the server.
	 * @param listener the listener obejct
	 */
	public void addMessageListener(MessageListener<? super Client> listener) {
		this.client.addMessageListener(listener);
	}
	
	/**
	 * Returns the id of the connection.
	 * @return the id
	 */
	public int getConnectionId() {
		return client.getId();
	}
	
	/**
	 * This class is used to send the user input state to the server in constant time intervals.
	 * @author Findus
	 *
	 */
	private class InputSenderLoop implements Runnable {

		@Override
		public void run() {
			while(senderRunning) {
				InputUpdate m = new InputUpdate();
				m.setInputs(controller.getPlayer().getInputState());
				m.setPlayerId(controller.getPlayer().getId());
				m.setViewDir(controller.getPlayer().getControl().getViewDirection());
				m.setReliable(false);
				if(client.isConnected()) {
					client.send(m);
				}
				
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
}
