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

import de.findus.cydonia.events.ConnectionAddedEvent;
import de.findus.cydonia.events.ConnectionRemovedEvent;
import de.findus.cydonia.events.Event;
import de.findus.cydonia.events.EventListener;
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
 * @author Findus
 *
 */
public class NetworkController implements MessageListener<HostedConnection>, ConnectionListener, EventListener {

	private Server server;
	
	private EventMachine eventMachine;
	
	public NetworkController(EventMachine em) {
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

	@Override
	public void messageReceived(HostedConnection con, Message m) {
		if(m instanceof EventMessage) {
			eventMachine.fireEvent(((EventMessage) m).getEvent());
		}
	}

	@Override
	public void connectionAdded(Server s, HostedConnection con) {
		ConnectionAddedEvent added = new ConnectionAddedEvent();
		added.setClientid(con.getId());
		eventMachine.fireEvent(added);
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
