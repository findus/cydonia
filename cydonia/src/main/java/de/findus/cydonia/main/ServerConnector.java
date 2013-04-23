/**
 * 
 */
package de.findus.cydonia.main;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.message.CompressedMessage;
import com.jme3.network.serializing.Serializer;

import de.findus.cydonia.events.AddEvent;
import de.findus.cydonia.events.AttackEvent;
import de.findus.cydonia.events.BeamEvent;
import de.findus.cydonia.events.ChooseTeamEvent;
import de.findus.cydonia.events.ConfigEvent;
import de.findus.cydonia.events.ConnectionDeniedEvent;
import de.findus.cydonia.events.ConnectionInitEvent;
import de.findus.cydonia.events.EventMachine;
import de.findus.cydonia.events.FlagEvent;
import de.findus.cydonia.events.HitEvent;
import de.findus.cydonia.events.InputEvent;
import de.findus.cydonia.events.KillEvent;
import de.findus.cydonia.events.PhaseEvent;
import de.findus.cydonia.events.PickupEvent;
import de.findus.cydonia.events.PlaceEvent;
import de.findus.cydonia.events.PlayerJoinEvent;
import de.findus.cydonia.events.PlayerQuitEvent;
import de.findus.cydonia.events.RemoveEvent;
import de.findus.cydonia.events.RespawnEvent;
import de.findus.cydonia.events.RestartRoundEvent;
import de.findus.cydonia.events.RoundEndedEvent;
import de.findus.cydonia.events.WorldStateEvent;
import de.findus.cydonia.level.Map;
import de.findus.cydonia.level.WorldState;
import de.findus.cydonia.messages.BeamerInfo;
import de.findus.cydonia.messages.BulletPhysic;
import de.findus.cydonia.messages.ConnectionInitMessage;
import de.findus.cydonia.messages.EditorInfo;
import de.findus.cydonia.messages.EventMessage;
import de.findus.cydonia.messages.FlagInfo;
import de.findus.cydonia.messages.FlubeStatePartMessage;
import de.findus.cydonia.messages.InitialStateMessage;
import de.findus.cydonia.messages.InputMessage;
import de.findus.cydonia.messages.JoinMessage;
import de.findus.cydonia.messages.LocationUpdatedMessage;
import de.findus.cydonia.messages.MoveableInfo;
import de.findus.cydonia.messages.PhaserInfo;
import de.findus.cydonia.messages.PickerInfo;
import de.findus.cydonia.messages.PlayerInfo;
import de.findus.cydonia.messages.PlayerPhysic;
import de.findus.cydonia.messages.SpawnPointInfo;
import de.findus.cydonia.messages.ViewDirMessage;
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
	
	private InitialStateMessage lastmsg;
	
	private List<MoveableInfo[]> msgparts = new LinkedList<MoveableInfo[]>();
	
	/**
	 * Constructor.
	 * @param controller the game controller
	 */
	public ServerConnector(GameController app, EventMachine em) {
		gameController = app;
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
		Serializer.registerClass(CompressedMessage.class);
		Serializer.registerClass(ConnectionInitMessage.class);
		Serializer.registerClass(JoinMessage.class);
		Serializer.registerClass(InitialStateMessage.class);
		Serializer.registerClass(FlubeStatePartMessage.class);
		Serializer.registerClass(WorldState.class);
		Serializer.registerClass(GameConfig.class);
		Serializer.registerClass(PlayerInfo.class);
		Serializer.registerClass(PickerInfo.class);
		Serializer.registerClass(BeamerInfo.class);
		Serializer.registerClass(PhaserInfo.class);
		Serializer.registerClass(EditorInfo.class);
		Serializer.registerClass(MoveableInfo.class);
		Serializer.registerClass(FlagInfo.class);
		Serializer.registerClass(SpawnPointInfo.class);
		Serializer.registerClass(Map.class);
		Serializer.registerClass(LocationUpdatedMessage.class);
		Serializer.registerClass(ViewDirMessage.class);
		Serializer.registerClass(PlayerPhysic.class);
		Serializer.registerClass(BulletPhysic.class);
		Serializer.registerClass(PlayerInputState.class);
		Serializer.registerClass(EventMessage.class);
		Serializer.registerClass(InputMessage.class);
		
		Serializer.registerClass(InputEvent.class);
		Serializer.registerClass(AttackEvent.class);
		Serializer.registerClass(PhaseEvent.class);
		Serializer.registerClass(HitEvent.class);
		Serializer.registerClass(BeamEvent.class);
		Serializer.registerClass(PickupEvent.class);
		Serializer.registerClass(PlaceEvent.class);
		Serializer.registerClass(AddEvent.class);
		Serializer.registerClass(RemoveEvent.class);
		Serializer.registerClass(KillEvent.class);
		Serializer.registerClass(FlagEvent.class);
		Serializer.registerClass(RespawnEvent.class);
		Serializer.registerClass(RestartRoundEvent.class);
		Serializer.registerClass(RoundEndedEvent.class);
		Serializer.registerClass(PlayerJoinEvent.class);
		Serializer.registerClass(PlayerQuitEvent.class);
		Serializer.registerClass(ChooseTeamEvent.class);
		Serializer.registerClass(ConfigEvent.class);
		Serializer.registerClass(WorldStateEvent.class);
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
		if(m instanceof LocationUpdatedMessage) {
			gameController.setlatestLocationUpdate((LocationUpdatedMessage) m);
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
			lastmsg = iniState;
		}else if(m instanceof FlubeStatePartMessage) {
			FlubeStatePartMessage part = (FlubeStatePartMessage) m;
			msgparts.add(part.getFlubes());
			if(part.getNumber() == lastmsg.getPartcount()) {
				composeMessage();
			}
		}else if(m instanceof CompressedMessage) {
			messageReceived(c, ((CompressedMessage) m).getMessage());
		}
	}
	
	public void composeMessage() {
		WorldState state = lastmsg.getWorldState();
		LinkedList<MoveableInfo> list = new LinkedList<MoveableInfo>();
		for(MoveableInfo[] part : msgparts) {
			for(int i=0; i<part.length; i++) {
				if(part[i] != null) {
					list.add(part[i]);
				}
			}
		}
		MoveableInfo[] infos = list.toArray(new MoveableInfo[list.size()]);
		state.setFlubes(infos);
		msgparts.clear();
		lastmsg = null;
		
		WorldStateEvent event = new WorldStateEvent();
		event.setWorldState(state);
		eventMachine.fireEvent(event);
	}
}
