/**
 * 
 */
package de.encala.cydonia.game;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.message.CompressedMessage;
import com.jme3.network.serializing.Serializer;

import de.encala.cydonia.game.level.Map;
import de.encala.cydonia.share.GameConfig;
import de.encala.cydonia.share.events.AddEvent;
import de.encala.cydonia.share.events.AttackEvent;
import de.encala.cydonia.share.events.BeamEvent;
import de.encala.cydonia.share.events.ChooseTeamEvent;
import de.encala.cydonia.share.events.ConfigEvent;
import de.encala.cydonia.share.events.ConnectionDeniedEvent;
import de.encala.cydonia.share.events.ConnectionInitEvent;
import de.encala.cydonia.share.events.ConnectionLostEvent;
import de.encala.cydonia.share.events.EventMachine;
import de.encala.cydonia.share.events.FlagEvent;
import de.encala.cydonia.share.events.HitEvent;
import de.encala.cydonia.share.events.InputEvent;
import de.encala.cydonia.share.events.KillEvent;
import de.encala.cydonia.share.events.MarkEvent;
import de.encala.cydonia.share.events.PhaseEvent;
import de.encala.cydonia.share.events.PickupEvent;
import de.encala.cydonia.share.events.PlaceEvent;
import de.encala.cydonia.share.events.PlayerJoinEvent;
import de.encala.cydonia.share.events.PlayerQuitEvent;
import de.encala.cydonia.share.events.PushEvent;
import de.encala.cydonia.share.events.RemoveEvent;
import de.encala.cydonia.share.events.RespawnEvent;
import de.encala.cydonia.share.events.RestartRoundEvent;
import de.encala.cydonia.share.events.RoundEndedEvent;
import de.encala.cydonia.share.events.SwapEvent;
import de.encala.cydonia.share.events.WorldStateEvent;
import de.encala.cydonia.share.messages.BulletPhysic;
import de.encala.cydonia.share.messages.ConnectionInitMessage;
import de.encala.cydonia.share.messages.EditorInfo;
import de.encala.cydonia.share.messages.EventMessage;
import de.encala.cydonia.share.messages.FlagInfo;
import de.encala.cydonia.share.messages.FlubeStatePartMessage;
import de.encala.cydonia.share.messages.InitialStateMessage;
import de.encala.cydonia.share.messages.InputMessage;
import de.encala.cydonia.share.messages.JoinMessage;
import de.encala.cydonia.share.messages.LocationUpdatedMessage;
import de.encala.cydonia.share.messages.MoveableInfo;
import de.encala.cydonia.share.messages.PickerInfo;
import de.encala.cydonia.share.messages.PlayerInfo;
import de.encala.cydonia.share.messages.PlayerPhysic;
import de.encala.cydonia.share.messages.SpawnPointInfo;
import de.encala.cydonia.share.messages.SwapperInfo;
import de.encala.cydonia.share.messages.ViewDirMessage;
import de.encala.cydonia.share.messages.WorldState;
import de.encala.cydonia.share.player.PlayerInputState;

/**
 * The central connection controller.
 * 
 * @author encala
 * 
 */
public class ServerConnector implements MessageListener<Client>,
		ClientStateListener {

	private GameController gameController;

	private EventMachine eventMachine;

	private Client client;

	private InitialStateMessage lastmsg;

	private List<MoveableInfo[]> msgparts = new LinkedList<MoveableInfo[]>();

	/**
	 * Constructor.
	 * 
	 * @param controller
	 *            the game controller
	 */
	public ServerConnector(GameController app, EventMachine em) {
		gameController = app;
		eventMachine = em;
		initSerializer();
	}

	/**
	 * Connects to the specified server.
	 * 
	 * @param address
	 *            IPv4 address of the server
	 * @param port
	 *            listening port of the server
	 */
	public void connectToServer(String address, int port) {
		try {
			client = Network.connectToServer(address, port);
			client.addMessageListener(this);
			client.addClientStateListener(this);

			client.start();
			while (!client.isConnected()) {
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
		Serializer.registerClass(SwapperInfo.class);
		Serializer.registerClass(EditorInfo.class);
		Serializer.registerClass(MoveableInfo.class);
		Serializer.registerClass(FlagInfo.class);
		Serializer.registerClass(SpawnPointInfo.class);
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
		Serializer.registerClass(PushEvent.class);
		Serializer.registerClass(HitEvent.class);
		Serializer.registerClass(BeamEvent.class);
		Serializer.registerClass(MarkEvent.class);
		Serializer.registerClass(SwapEvent.class);
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
		if (client != null && client.isConnected()) {
			this.client.removeClientStateListener(this);
			this.client.close();
		}
	}

	/**
	 * Returns the id of the connection.
	 * 
	 * @return the id
	 */
	public int getConnectionId() {
		return client.getId();
	}

	public boolean sendMessage(Message msg) {
		if (client.isConnected()) {
			client.send(msg);
			return true;
		}
		return false;
	}

	@Override
	public void messageReceived(Client c, Message m) {
		if (m instanceof LocationUpdatedMessage) {
			gameController.setlatestLocationUpdate((LocationUpdatedMessage) m);
		} else if (m instanceof EventMessage) {
			eventMachine.fireEvent(((EventMessage) m).getEvent());
		} else if (m instanceof ConnectionInitMessage) {
			ConnectionInitMessage init = (ConnectionInitMessage) m;
			if (init.isConnectionAccepted()) {
				ConnectionInitEvent established = new ConnectionInitEvent();
				established.setLevel(init.getLevel());
				eventMachine.fireEvent(established);
			} else {
				System.out.println("Server denied connection! Reason: '"
						+ init.getText() + "'");
				ConnectionDeniedEvent denied = new ConnectionDeniedEvent();
				denied.setReason(init.getText());
				eventMachine.fireEvent(denied);
			}
		} else if (m instanceof InitialStateMessage) {
			InitialStateMessage iniState = (InitialStateMessage) m;
			lastmsg = iniState;
		} else if (m instanceof FlubeStatePartMessage) {
			FlubeStatePartMessage part = (FlubeStatePartMessage) m;
			msgparts.add(part.getFlubes());
			if (part.getNumber() == lastmsg.getPartcount()) {
				composeMessage();
			}
		} else if (m instanceof CompressedMessage) {
			messageReceived(c, ((CompressedMessage) m).getMessage());
		}
	}

	public void composeMessage() {
		WorldState state = lastmsg.getWorldState();
		LinkedList<MoveableInfo> list = new LinkedList<MoveableInfo>();
		for (MoveableInfo[] part : msgparts) {
			for (int i = 0; i < part.length; i++) {
				if (part[i] != null) {
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

	@Override
	public void clientConnected(Client arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clientDisconnected(Client client, DisconnectInfo info) {
		ConnectionLostEvent event = new ConnectionLostEvent(info.reason, false);
		eventMachine.fireEvent(event);
	}
}
