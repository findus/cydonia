/**
 * 
 */
package de.findus.cydonia.level;

import com.jme3.network.serializing.Serializable;

import de.findus.cydonia.main.GameConfig;
import de.findus.cydonia.messages.FlagInfo;
import de.findus.cydonia.messages.MoveableInfo;
import de.findus.cydonia.messages.PlayerInfo;
import de.findus.cydonia.messages.SpawnPointInfo;

/**
 * @author Findus
 *
 */
@Serializable
public class WorldState {

	private long passedRoundTime;
	
	private int team1score;
	
	private int team2score;
	
	private PlayerInfo[] players;
	
	private MoveableInfo[] flubes;
	
	private FlagInfo[] flags;
	
	private SpawnPointInfo[] spawnPoints;
	
	private GameConfig config;

	/**
	 * 
	 */
	public WorldState() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param net
	 */
	public WorldState(PlayerInfo[] players, MoveableInfo[] flubes, FlagInfo[] flags, SpawnPointInfo[] spawnPoints, GameConfig config) {
		this.players = players;
		this.flubes = flubes;
		this.flags = flags;
		this.spawnPoints = spawnPoints;
		this.config = config;
	}
	
	/**
	 * @return the passedRoundTime
	 */
	public long getPassedRoundTime() {
		return passedRoundTime;
	}

	/**
	 * @param passedRoundTime the passedRoundTime to set
	 */
	public void setPassedRoundTime(long passedRoundTime) {
		this.passedRoundTime = passedRoundTime;
	}

	/**
	 * @return the players
	 */
	public PlayerInfo[] getPlayers() {
		return players;
	}

	/**
	 * @param players the players to set
	 */
	public void setPlayers(PlayerInfo[] infos) {
		this.players = infos;
	}

	/**
	 * @return the moveablesNode
	 */
	public MoveableInfo[] getFlubes() {
		return flubes;
	}

	/**
	 * @param moveablesNode the moveablesNode to set
	 */
	public void setFlubes(MoveableInfo[] moveables) {
		this.flubes = moveables;
	}

	/**
	 * @return the config
	 */
	public GameConfig getConfig() {
		return config;
	}

	/**
	 * @param config the config to set
	 */
	public void setconfig(GameConfig config) {
		this.config = config;
	}

	/**
	 * @return the flags
	 */
	public FlagInfo[] getFlags() {
		return flags;
	}

	/**
	 * @param flags the flags to set
	 */
	public void setFlags(FlagInfo[] flags) {
		this.flags = flags;
	}

	public SpawnPointInfo[] getSpawnPoints() {
		return spawnPoints;
	}

	public void setSpawnPoints(SpawnPointInfo[] spawnPoints) {
		this.spawnPoints = spawnPoints;
	}

	public int getTeam1score() {
		return team1score;
	}

	public void setTeam1score(int team1score) {
		this.team1score = team1score;
	}

	public int getTeam2score() {
		return team2score;
	}

	public void setTeam2score(int team2score) {
		this.team2score = team2score;
	}

}
