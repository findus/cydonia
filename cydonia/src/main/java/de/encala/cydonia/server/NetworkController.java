/**
 * 
 */
package de.encala.cydonia.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.message.CompressedMessage;
import com.jme3.network.serializing.Serializer;

import de.encala.cydonia.events.AddEvent;
import de.encala.cydonia.events.AttackEvent;
import de.encala.cydonia.events.BeamEvent;
import de.encala.cydonia.events.ChooseTeamEvent;
import de.encala.cydonia.events.ConfigEvent;
import de.encala.cydonia.events.ConnectionAddedEvent;
import de.encala.cydonia.events.ConnectionRemovedEvent;
import de.encala.cydonia.events.Event;
import de.encala.cydonia.events.EventListener;
import de.encala.cydonia.events.EventMachine;
import de.encala.cydonia.events.FlagEvent;
import de.encala.cydonia.events.HitEvent;
import de.encala.cydonia.events.InputEvent;
import de.encala.cydonia.events.KillEvent;
import de.encala.cydonia.events.MarkEvent;
import de.encala.cydonia.events.PhaseEvent;
import de.encala.cydonia.events.PickupEvent;
import de.encala.cydonia.events.PlaceEvent;
import de.encala.cydonia.events.PlayerJoinEvent;
import de.encala.cydonia.events.PlayerQuitEvent;
import de.encala.cydonia.events.PushEvent;
import de.encala.cydonia.events.RemoveEvent;
import de.encala.cydonia.events.RespawnEvent;
import de.encala.cydonia.events.RestartRoundEvent;
import de.encala.cydonia.events.RoundEndedEvent;
import de.encala.cydonia.events.SwapEvent;
import de.encala.cydonia.events.WorldStateEvent;
import de.encala.cydonia.level.Map;
import de.encala.cydonia.level.WorldState;
import de.encala.cydonia.messages.BeamerInfo;
import de.encala.cydonia.messages.BulletPhysic;
import de.encala.cydonia.messages.ConnectionInitMessage;
import de.encala.cydonia.messages.EditorInfo;
import de.encala.cydonia.messages.EventMessage;
import de.encala.cydonia.messages.FlagInfo;
import de.encala.cydonia.messages.FlubeStatePartMessage;
import de.encala.cydonia.messages.InitialStateMessage;
import de.encala.cydonia.messages.InputMessage;
import de.encala.cydonia.messages.JoinMessage;
import de.encala.cydonia.messages.LocationUpdatedMessage;
import de.encala.cydonia.messages.MoveableInfo;
import de.encala.cydonia.messages.PhaserInfo;
import de.encala.cydonia.messages.PickerInfo;
import de.encala.cydonia.messages.PlayerInfo;
import de.encala.cydonia.messages.PlayerPhysic;
import de.encala.cydonia.messages.PusherInfo;
import de.encala.cydonia.messages.SpawnPointInfo;
import de.encala.cydonia.messages.SwapperInfo;
import de.encala.cydonia.messages.ViewDirMessage;
import de.encala.cydonia.player.PlayerInputState;
import de.encala.cydonia.share.GameConfig;

/**
 * @author encala
 * 
 */
public class NetworkController implements MessageListener<HostedConnection>,
		ConnectionListener, EventListener {

	private Server server;

	private GameServer gameserver;

	private EventMachine eventMachine;

	private Thread searchResponder;

	public NetworkController(GameServer app, EventMachine em) {
		gameserver = app;
		eventMachine = em;

		initSerializer();

		try {
			server = Network.createServer(6173);
			server.start();

			server.addMessageListener(this);
			server.addConnectionListener(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		eventMachine.registerListener(this);

		initSearchResponder();
	}

	private void initSearchResponder() {
		searchResponder = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					MulticastSocket s = new MulticastSocket(55000);
					s.setSoTimeout(5000);
					s.setReuseAddress(true);

					// join the multicast group
					s.joinGroup(InetAddress.getByName("224.0.0.1"));

					// Create a DatagramPacket and do a receive
					byte buf[] = new byte[1];
					DatagramPacket pack = new DatagramPacket(buf, buf.length);
					while (!Thread.interrupted()) {
						try {
							s.receive(pack);
						} catch (SocketTimeoutException e) {
							continue;
						}

						System.out.println("Received Multicast: "
								+ pack.getAddress());

						byte buf2[] = new byte[1];
						DatagramPacket response = new DatagramPacket(buf2,
								buf2.length);
						response.setAddress(pack.getAddress());
						response.setPort(55001);

						DatagramSocket c = new DatagramSocket();
						c.send(response);
						System.out.println("Sent Response");
						c.close();
					}
					// And when we have finished receiving data leave the
					// multicast group and
					// close the socket
					s.leaveGroup(InetAddress.getByName("224.0.0.1"));
					s.close();
					System.out.println("MulticastReceiver stopped");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		searchResponder.start();
	}

	public void stop() {
		searchResponder.interrupt();
		for (HostedConnection c : server.getConnections()) {
			c.close("Server is shutting down!");
		}
		server.close();
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
		Serializer.registerClass(SwapperInfo.class);
		Serializer.registerClass(PhaserInfo.class);
		Serializer.registerClass(PusherInfo.class);
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

	@Override
	public void messageReceived(HostedConnection con, Message m) {
		if (m instanceof EventMessage) {
			eventMachine.fireEvent(((EventMessage) m).getEvent());
		} else if (m instanceof ViewDirMessage) {
			ViewDirMessage msg = (ViewDirMessage) m;
			gameserver.setViewDir(msg.getPlayerid(), msg.getViewDir());
		} else if (m instanceof InputMessage) {
			InputMessage msg = (InputMessage) m;
			gameserver.handlePlayerInput(con.getId(), msg.getCommand(),
					msg.isValue());
		} else if (m instanceof JoinMessage) {
			JoinMessage msg = (JoinMessage) m;
			gameserver.joinPlayer(msg.getPlayerid(), msg.getPlayername());
		} else if (m instanceof InitialStateMessage) {
			gameserver.sendInitialState(con.getId());
		} else if (m instanceof CompressedMessage) {
			messageReceived(con, ((CompressedMessage) m).getMessage());
		}
	}

	@Override
	public void connectionAdded(Server s, HostedConnection con) {
		if (false) { // check for "too many players"
			ConnectionInitMessage init = new ConnectionInitMessage();
			init.setConnectionAccepted(false);
			init.setText("Server full");
			con.send(init);
		} else {
			ConnectionAddedEvent added = new ConnectionAddedEvent();
			added.setClientid(con.getId());
			eventMachine.fireEvent(added);
		}
	}

	@Override
	public void connectionRemoved(Server s, HostedConnection con) {
		ConnectionRemovedEvent removed = new ConnectionRemovedEvent();
		removed.setClientid(con.getId());
		eventMachine.fireEvent(removed);
	}

	public void broadcast(Message msg) {
		server.broadcast(msg);
	}

	public void sendMessage(Message msg, int clientid) {
		server.getConnection(clientid).send(msg);
	}

	@Override
	public void newEvent(Event e) {
		if (e.isNetworkEvent()) {
			EventMessage msg = new EventMessage();
			msg.setEvent(e);
			server.broadcast(msg);
		}
	}
}
