package com.jtransc.media.limelibgdx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class GdxPreferencesAdapter implements Preferences {
	final private String name;
	final private Map<String, Object> prefs = new HashMap<>();
	private boolean autoflush;

	public GdxPreferencesAdapter(String name, boolean autoflush) {
		this.name = name;
		this.autoflush = autoflush;
		this.prefs.clear();
		this.prefs.putAll(load(name));
	}

	// Default generic storage!
	protected String loadString(String name) throws IOException {
		FileHandle fileHandle = Gdx.files.local(name + ".prefs");
		return fileHandle.readString();
	}

	protected void storeString(String name, String prefs) throws IOException {
		FileHandle fileHandle = Gdx.files.local(name + ".prefs");
		fileHandle.writeString(prefs, false);
	}

	private Json json = new Json();

	protected Map<String, Object> load(String name) {
		try {
			String s = loadString(name);
			if (s == null || s.length() == 0) {
				s = "{}";
			}
			return json.fromJson(HashMap.class, s);
		} catch (Throwable t) {
			t.printStackTrace();
			return new HashMap<>();
		}
	}

	protected void store(String name, Map<String, Object> prefs) {
		try {
			String s = json.toJson(prefs);
			if (s == null) s = "{}";
			storeString(name, s);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Override
	public Preferences putBoolean(String key, boolean val) {
		prefs.put(key, val);
		autoflush();
		return this;
	}

	@Override
	public Preferences putInteger(String key, int val) {
		prefs.put(key, val);
		autoflush();
		return this;
	}

	@Override
	public Preferences putLong(String key, long val) {
		prefs.put(key, val);
		autoflush();
		return this;
	}

	@Override
	public Preferences putFloat(String key, float val) {
		prefs.put(key, val);
		autoflush();
		return this;
	}

	@Override
	public Preferences putString(String key, String val) {
		prefs.put(key, val);
		autoflush();
		return this;
	}

	@Override
	public Preferences put(Map<String, ?> vals) {
		for (Map.Entry<String, ?> item : vals.entrySet()) {
			prefs.put(item.getKey(), item.getValue());
		}
		autoflush();
		return this;
	}

	@Override
	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	@Override
	public int getInteger(String key) {
		return getInteger(key, 0);
	}

	@Override
	public long getLong(String key) {
		return getLong(key, 0L);
	}

	@Override
	public float getFloat(String key) {
		return getFloat(key, 0f);
	}

	@Override
	public String getString(String key) {
		return getString(key, "");
	}

	@Override
	public boolean getBoolean(String key, boolean defValue) {
		return prefs.containsKey(key) ? (boolean) prefs.get(key) : defValue;
	}

	@Override
	public int getInteger(String key, int defValue) {
		return prefs.containsKey(key) ? (int) prefs.get(key) : defValue;
	}

	@Override
	public long getLong(String key, long defValue) {
		return prefs.containsKey(key) ? (long) prefs.get(key) : defValue;
	}

	@Override
	public float getFloat(String key, float defValue) {
		return prefs.containsKey(key) ? (float) prefs.get(key) : defValue;
	}

	@Override
	public String getString(String key, String defValue) {
		return prefs.containsKey(key) ? (String) prefs.get(key) : defValue;
	}

	@Override
	public Map<String, ?> get() {
		return new HashMap<>(prefs);
	}

	@Override
	public boolean contains(String key) {
		return prefs.containsKey(key);
	}

	@Override
	public void clear() {
		prefs.clear();
		autoflush();
	}

	@Override
	public void remove(String key) {
		prefs.remove(key);
		autoflush();
	}

	private void autoflush() {
		if (autoflush) flush();
	}

	@Override
	public void flush() {
		// @TODO: write!
		store(name, prefs);
	}
}
