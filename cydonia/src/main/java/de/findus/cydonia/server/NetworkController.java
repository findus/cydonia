/**
 * 
 */
package de.findus.cydonia.server;

import java.io.IOException;

import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;

import de.findus.cydonia.events.AttackEvent;
import de.findus.cydonia.events.ChooseTeamEvent;
import de.findus.cydonia.events.ConnectionAddedEvent;
import de.findus.cydonia.events.ConnectionRemovedEvent;
import de.findus.cydonia.events.Event;
import de.findus.cydonia.events.EventListener;
import de.findus.cydonia.events.EventMachine;
import de.findus.cydonia.events.HitEvent;
import de.findus.cydonia.events.InputEvent;
import de.findus.cydonia.events.JumpEvent;
import de.findus.cydonia.events.PickupEvent;
import de.findus.cydonia.events.PlaceEvent;
import de.findus.cydonia.events.PlayerJoinEvent;
import de.findus.cydonia.events.PlayerQuitEvent;
import de.findus.cydonia.events.RespawnEvent;
import de.findus.cydonia.events.RestartRoundEvent;
import de.findus.cydonia.events.RoundEndedEvent;
import de.findus.cydonia.main.GameConfig;
import de.findus.cydonia.messages.BulletPhysic;
import de.findus.cydonia.messages.ConnectionInitMessage;
import de.findus.cydonia.messages.EventMessage;
import de.findus.cydonia.messages.InitialStateMessage;
import de.findus.cydonia.messages.InputMessage;
import de.findus.cydonia.messages.MoveableInfo;
import de.findus.cydonia.messages.PlayerInfo;
import de.findus.cydonia.messages.PlayerPhysic;
import de.findus.cydonia.messages.ViewDirMessage;
import de.findus.cydonia.messages.WorldStateUpdatedMessage;
import de.findus.cydonia.player.PlayerInputState;

/**
 * @author Findus
 *
 */
public class NetworkController implements MessageListener<HostedConnection>, ConnectionListener, EventListener {

	private Server server;
	
	private GameServer gameserver;
	
	private EventMachine eventMachine;
	
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
	}
	
	public void stop() {
		server.close();
	}
	
	private void initSerializer() {
		Serializer.registerClass(ConnectionInitMessage.class);
		Serializer.registerClass(InitialStateMessage.class);
		Serializer.registerClass(GameConfig.class);
		Serializer.registerClass(PlayerInfo.class);
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
		Serializer.registerClass(JumpEvent.class);
		Serializer.registerClass(RespawnEvent.class);
		Serializer.registerClass(RestartRoundEvent.class);
		Serializer.registerClass(RoundEndedEvent.class);
		Serializer.registerClass(WorldStateUpdatedMessage.class);
		Serializer.registerClass(PlayerJoinEvent.class);
		Serializer.registerClass(PlayerQuitEvent.class);
		Serializer.registerClass(ChooseTeamEvent.class);
	}

	@Override
	public void messageReceived(HostedConnection con, Message m) {
		if(m instanceof EventMessage) {
			eventMachine.fireEvent(((EventMessage) m).getEvent());
		}else if (m instanceof ViewDirMessage) {
			ViewDirMessage msg = (ViewDirMessage) m;
			gameserver.setViewDir(msg.getPlayerid(), msg.getViewDir());
		}else if (m instanceof InputMessage) {
			InputMessage msg = (InputMessage) m;
			gameserver.handlePlayerInput(msg.getPlayerid(), msg.getCommand(), msg.isValue());
		}
	}

	@Override
	public void connectionAdded(Server s, HostedConnection con) {
		if(false) { //check for "too many players"
			ConnectionInitMessage init = new ConnectionInitMessage();
			init.setConnectionAccepted(false);
			init.setText("Server full");
			con.send(init);
		}else {
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
		if(e.isNetworkEvent()) {
			EventMessage msg = new EventMessage();
			msg.setEvent(e);
			server.broadcast(msg);
		}
	}
}
