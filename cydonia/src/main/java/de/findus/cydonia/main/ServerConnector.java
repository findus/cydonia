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

import de.findus.cydonia.events.ConnectionDeniedEvent;
import de.findus.cydonia.events.ConnectionEstablishedEvent;
import de.findus.cydonia.events.EventMachine;
import de.findus.cydonia.messages.AttackMessage;
import de.findus.cydonia.messages.BulletPhysic;
import de.findus.cydonia.messages.ConnectionInitMessage;
import de.findus.cydonia.messages.EventMessage;
import de.findus.cydonia.messages.HitMessage;
import de.findus.cydonia.messages.JumpMessage;
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
public class ServerConnector implements MessageListener<Client> {
	
	private EventMachine eventMachine;
	
	private Client client;
	
	
	/**
	 * Constructor.
	 * @param controller the game controller
	 */
	public ServerConnector(EventMachine em) {
		eventMachine = em;
		initSerializer();
	}
	
	/**
	 * Connects to the specified server.
	 * @param address IPv4 address of the server
	 * @param port listening port of the server
	 */
	public void connectToServer(String address, int port) {
		try {
			client = Network.connectToServer(address, port);
			client.addMessageListener(this);
			
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
	
	private void initSerializer() {
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
		Serializer.registerClass(JumpMessage.class);
		Serializer.registerClass(EventMessage.class);
	}
	
	/**
	 * Closes the connection to the server.
	 */
	public void disconnectFromServer() {
		this.client.close();
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

	@Override
	public void messageReceived(Client c, Message m) {
		if(m instanceof EventMessage) {
			eventMachine.fireEvent(((EventMessage) m).getEvent());
		}else if(m instanceof ConnectionInitMessage) {
			ConnectionInitMessage init = (ConnectionInitMessage) m;
			if(init.isDenied()) {
				System.out.println("Server denied connection! Reason: '" + init.getReason() + "'");
				ConnectionDeniedEvent denied = new ConnectionDeniedEvent();
				denied.setReason(init.getReason());
			}else {
				ConnectionEstablishedEvent established = new ConnectionEstablishedEvent();
				established.setLevel(init.getLevel());
				eventMachine.fireEvent(established);
			}
		}
	}
}
