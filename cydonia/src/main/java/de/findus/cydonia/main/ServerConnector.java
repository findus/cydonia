/**
 * 
 */
package de.findus.cydonia.main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;

import de.findus.cydonia.events.AttackEvent;
import de.findus.cydonia.events.ChooseTeamEvent;
import de.findus.cydonia.events.ConnectionDeniedEvent;
import de.findus.cydonia.events.ConnectionInitEvent;
import de.findus.cydonia.events.EventMachine;
import de.findus.cydonia.events.HitEvent;
import de.findus.cydonia.events.InputEvent;
import de.findus.cydonia.events.PickupEvent;
import de.findus.cydonia.events.PlaceEvent;
import de.findus.cydonia.events.PlayerJoinEvent;
import de.findus.cydonia.events.PlayerQuitEvent;
import de.findus.cydonia.events.RespawnEvent;
import de.findus.cydonia.events.RestartRoundEvent;
import de.findus.cydonia.events.RoundEndedEvent;
import de.findus.cydonia.messages.BulletPhysic;
import de.findus.cydonia.messages.ConnectionInitMessage;
import de.findus.cydonia.messages.EventMessage;
import de.findus.cydonia.messages.InitialStateMessage;
import de.findus.cydonia.messages.InputMessage;
import de.findus.cydonia.messages.JoinMessage;
import de.findus.cydonia.messages.MoveableInfo;
import de.findus.cydonia.messages.PickerInfo;
import de.findus.cydonia.messages.PlayerInfo;
import de.findus.cydonia.messages.PlayerPhysic;
import de.findus.cydonia.messages.ViewDirMessage;
import de.findus.cydonia.messages.WorldStateUpdatedMessage;
import de.findus.cydonia.player.PlayerInputState;

/**
 * The central connection controller.
 * @author Findus
 *
 */
public class ServerConnector implements MessageListener<Client> {
	
	private GameController gameController;
	
	private EventMachine eventMachine;
	
	private Client client;

	private Thread replyListener;
	
	/**
	 * Constructor.
	 * @param controller the game controller
	 */
	public ServerConnector(GameController app, EventMachine em) {
		gameController = app;
		eventMachine = em;
		initSerializer();
		
		updateLocalServers();
	}
	
	public void updateLocalServers() {
		initSearchListener();
		
		try {
			// Create the socket but we don't bind it as we are only going to send data
			MulticastSocket s = new MulticastSocket();

			// Note that we don't have to join the multicast group if we are only
			// sending data and not receiving

			byte buf[] = new byte[1];
			// Create a DatagramPacket 
			DatagramPacket pack = new DatagramPacket(buf, buf.length,
					InetAddress.getByName("224.0.0.1"), 55000);
			// Do a send.
			s.send(pack);
			
			// And when we have finished sending data close the socket
			s.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initSearchListener() {
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

			replyListener = new Thread(new Runnable() {
				@Override
				public void run() {

					while(!Thread.interrupted()) {
						try {
							System.out.println("Listening for Servers...");
							s.receive(pack);
							System.out.println("Found Server: " + pack.getAddress());
							
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					s.close();
				}
			});

			replyListener.start();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Connects to the specified server.
	 * @param address IPv4 address of the server
	 * @param port listening port of the server
	 */
	public void connectToServer(String address, int port) {
		replyListener.interrupt();
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
		Serializer.registerClass(JoinMessage.class);
		Serializer.registerClass(InitialStateMessage.class);
		Serializer.registerClass(GameConfig.class);
		Serializer.registerClass(PlayerInfo.class);
		Serializer.registerClass(PickerInfo.class);
		Serializer.registerClass(MoveableInfo.class);
		Serializer.registerClass(WorldStateUpdatedMessage.class);
		Serializer.registerClass(ViewDirMessage.class);
		Serializer.registerClass(PlayerPhysic.class);
		Serializer.registerClass(BulletPhysic.class);
		Serializer.registerClass(PlayerInputState.class);
		Serializer.registerClass(EventMessage.class);
		Serializer.registerClass(InputMessage.class);
		
		Serializer.registerClass(InputEvent.class);
		Serializer.registerClass(AttackEvent.class);
		Serializer.registerClass(HitEvent.class);
		Serializer.registerClass(PickupEvent.class);
		Serializer.registerClass(PlaceEvent.class);
		Serializer.registerClass(RespawnEvent.class);
		Serializer.registerClass(RestartRoundEvent.class);
		Serializer.registerClass(RoundEndedEvent.class);
		Serializer.registerClass(WorldStateUpdatedMessage.class);
		Serializer.registerClass(PlayerJoinEvent.class);
		Serializer.registerClass(PlayerQuitEvent.class);
		Serializer.registerClass(ChooseTeamEvent.class);
	}
	
	/**
	 * Closes the connection to the server.
	 */
	public void disconnectFromServer() {
		if(client != null) {
			this.client.close();
		}
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
		if(m instanceof WorldStateUpdatedMessage) {
			gameController.setlatestWorldstate((WorldStateUpdatedMessage) m);
		}else if(m instanceof EventMessage) {
			eventMachine.fireEvent(((EventMessage) m).getEvent());
		}else if(m instanceof ConnectionInitMessage) {
			ConnectionInitMessage init = (ConnectionInitMessage) m;
			if(init.isConnectionAccepted()) {
				ConnectionInitEvent established = new ConnectionInitEvent();
				established.setLevel(init.getLevel());
				eventMachine.fireEvent(established);
			}else {
				System.out.println("Server denied connection! Reason: '" + init.getText() + "'");
				ConnectionDeniedEvent denied = new ConnectionDeniedEvent();
				denied.setReason(init.getText());
				eventMachine.fireEvent(denied);
			}
		}else if (m instanceof InitialStateMessage) {
			InitialStateMessage iniState = (InitialStateMessage) m;
			gameController.setInitialState(iniState.getConfig(), iniState.getPlayers(), iniState.getMoveables());
		}
	}
}
