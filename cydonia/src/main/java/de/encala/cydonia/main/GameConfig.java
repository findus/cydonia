/**
 * 
 */
package de.encala.cydonia.main;

import java.util.HashMap;

import com.jme3.network.serializing.Serializable;

/**
 * @author encala
 * 
 */
@Serializable
public class GameConfig {

	private static final HashMap<String, Object> defaults = new HashMap<String, Object>();

	static {
		defaults.put("mp_timelimit", 10 * 60L);
		defaults.put("mp_scorelimit", 3);
		defaults.put("mp_map", "");
		defaults.put("mp_respawntime", 5L);

		/*
		 * Possible values: "editor", "ctf"
		 */
		defaults.put("mp_gamemode", "ctf");
	}

	private HashMap<String, Object> map = new HashMap<String, Object>();

	public GameConfig() {
		this(false);
	}

	public GameConfig(boolean loadDefaults) {
		if (loadDefaults) {
			map.putAll(defaults);
		}
	}

	HashMap<String, Object> getMap() {
		return map;
	}

	/**
	 * Copies all settings from <code>other</code> to <code>this</code>
	 * GameConfig.
	 * <p>
	 * Any settings that are specified in other will overwrite settings set on
	 * this GameConfig.
	 * 
	 * @param other
	 *            The GameConfig to copy the settings from
	 */
	public void copyFrom(GameConfig other) {
		map.putAll(other.getMap());
	}

	/**
	 * Same as {@link #copyFrom(de.encala.cydonia.main.GameConfig) }, except
	 * doesn't overwrite settings that are already set.
	 * 
	 * @param other
	 *            The GameConfig to merge the settings from
	 */
	public void mergeFrom(GameConfig other) {
		HashMap<String, Object> othermap = other.getMap();
		for (String key : othermap.keySet()) {
			if (map.get(key) == null) {
				map.put(key, othermap.get(key));
			}
		}
	}

	/**
	 * Get an object from the settings.
	 * <p>
	 * If the key is not set, then null is returned.
	 */
	public Object getObject(String key) {
		return map.get(key);
	}

	/**
	 * Get an integer from the settings.
	 * <p>
	 * If the key is not set, then 0 is returned.
	 */
	public int getInteger(String key) {
		Integer i = (Integer) map.get(key);
		if (i == null) {
			return 0;
		}

		return i.intValue();
	}

	/**
	 * Get an long from the settings.
	 * <p>
	 * If the key is not set, then 0 is returned.
	 */
	public long getLong(String key) {
		Long i = (Long) map.get(key);
		if (i == null) {
			return 0;
		}

		return i.longValue();
	}

	/**
	 * Get a boolean from the settings.
	 * <p>
	 * If the key is not set, then false is returned.
	 */
	public boolean getBoolean(String key) {
		Boolean b = (Boolean) map.get(key);
		if (b == null) {
			return false;
		}

		return b.booleanValue();
	}

	/**
	 * Get a string from the settings.
	 * <p>
	 * If the key is not set, then null is returned.
	 */
	public String getString(String key) {
		String s = (String) map.get(key);
		if (s == null) {
			return null;
		}

		return s;
	}

	/**
	 * Get a float from the settings.
	 * <p>
	 * If the key is not set, then 0.0 is returned.
	 */
	public float getFloat(String key) {
		Float f = (Float) map.get(key);
		if (f == null) {
			return 0f;
		}

		return f.floatValue();
	}

	/**
	 * Set an object on the settings.
	 */
	public void putObject(String key, Object value) {
		map.put(key, value);
	}

	/**
	 * Set an integer on the settings.
	 */
	public void putInteger(String key, int value) {
		map.put(key, Integer.valueOf(value));
	}

	/**
	 * Set an long on the settings.
	 */
	public void putLong(String key, long value) {
		map.put(key, Long.valueOf(value));
	}

	/**
	 * Set a boolean on the settings.
	 */
	public void putBoolean(String key, boolean value) {
		map.put(key, Boolean.valueOf(value));
	}

	/**
	 * Set a string on the settings.
	 */
	public void putString(String key, String value) {
		map.put(key, value);
	}

	/**
	 * Set a float on the settings.
	 */
	public void putFloat(String key, float value) {
		map.put(key, Float.valueOf(value));
	}
}
