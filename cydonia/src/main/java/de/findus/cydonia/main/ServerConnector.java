/**
 * 
 */
package de.findus.cydonia.main;

import java.io.IOException;

import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;

import de.findus.cydonia.messages.AttackMessage;
import de.findus.cydonia.messages.BulletPhysic;
import de.findus.cydonia.messages.ConnectionInitMessage;
import de.findus.cydonia.messages.HitMessage;
import de.findus.cydonia.messages.PlayerInputMessage;
import de.findus.cydonia.messages.PlayerJoinMessage;
import de.findus.cydonia.messages.PlayerPhysic;
import de.findus.cydonia.messages.PlayerQuitMessage;
import de.findus.cydonia.messages.RespawnMessage;
import de.findus.cydonia.messages.WorldStateMessage;
import de.findus.cydonia.player.PlayerInputState;

/**
 * The central connection controller.
 * @author Findus
 *
 */
public class ServerConnector {
	private Client client;
	private GameController controller;
	private Thread senderLoop;
	
	/**
	 * Constructor.
	 * @param controller the game controller
	 */
	public ServerConnector(GameController controller) {
		this.controller = controller;
	}
	
	/**
	 * Connects to the specified server.
	 * @param address IPv4 address of the server
	 * @param port listening port of the server
	 */
	public void connectToServer(String address, int port, MessageListener<? super Client> listener) {
		try {
			client = Network.connectToServer(address, port);
			client.addMessageListener(listener);
			
			Serializer.registerClass(ConnectionInitMessage.class);
			Serializer.registerClass(WorldStateMessage.class);
			Serializer.registerClass(PlayerPhysic.class);
			Serializer.registerClass(BulletPhysic.class);
			Serializer.registerClass(PlayerInputMessage.class);
			Serializer.registerClass(PlayerInputState.class);
			Serializer.registerClass(AttackMessage.class);
			Serializer.registerClass(HitMessage.class);
			Serializer.registerClass(RespawnMessage.class);
			Serializer.registerClass(PlayerJoinMessage.class);
			Serializer.registerClass(PlayerQuitMessage.class);
			
			client.start();
			while(!client.isConnected()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					
				}
			}
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
		this.senderLoop = new Thread(new InputSenderLoop());
		this.senderLoop.start();
	}
	
	/**
	 * Stops the input sender loop.
	 */
	public void stopInputSender() {
		this.senderLoop.interrupt();
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
	
	public boolean sendMessage(Message msg) {
		if(client.isConnected()) {
			client.send(msg);
			return true;
		}
		return false;
	}
	
	/**
	 * This class is used to send the user input state to the server in constant time intervals.
	 * @author Findus
	 *
	 */
	private class InputSenderLoop implements Runnable {

		@Override
		public void run() {
			while(!Thread.interrupted()) {
				PlayerInputMessage m = new PlayerInputMessage();
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
					break;
				}
			}
		}
		
	}
}
