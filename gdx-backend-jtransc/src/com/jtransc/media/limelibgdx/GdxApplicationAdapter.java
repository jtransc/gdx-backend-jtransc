package com.jtransc.media.limelibgdx;

import com.badlogic.gdx.*;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.Queue;

import java.util.ArrayList;
import java.util.HashMap;

// https://github.com/libgdx/libgdx/blob/master/backends/gdx-backend-lwjgl/src/com/badlogic/gdx/backends/lwjgl/LwjglApplication.java
abstract public class GdxApplicationAdapter implements Application {
	ArrayList<LifecycleListener> listeners = new ArrayList<>();
	private Queue<Runnable> postRunnableList = new Queue<>();
	private Queue<Runnable> postRunnableListCopy = new Queue<>();
	private int logLevel = LOG_DEBUG;
	private ApplicationType type = ApplicationType.Desktop;

	final private ApplicationListener applicationListener;
	final private Audio audio;
	final private LimeGraphics graphics;
	final private LimeInput input;
	final private LimeFiles files;
	final private LimeNet net;

	public GdxApplicationAdapter(ApplicationListener applicationListener, int width, int height) {
		Gdx.app = this;
		this.applicationListener = applicationListener;
		Gdx.audio = audio = createAudio();
		this.type = createApplicationType();
		Gdx.graphics = graphics = createGraphics(width, height);
		Gdx.input = input = createInput();
		Gdx.files = files = createFiles();
		Gdx.net = net = createNet();
		Gdx.gl20 = Gdx.gl = graphics.getGL20();
		Gdx.gl30 = graphics.getGL30();
		initialize();
	}

	protected void initialize() {

	}

	protected abstract LimeNet createNet();

	protected abstract LimeFiles createFiles();

	protected abstract LimeInput createInput();

	protected abstract LimeGraphics createGraphics(int width, int height);

	public void log(int level, String tag, String message, Throwable exception) {
		if (level <= this.logLevel) {
			System.out.println("[" + tag + "] " + message + " : " + exception);
		}
	}

	@Override
	public void log(String tag, String message) {
		log(LOG_INFO, tag, message, null);
	}

	@Override
	public void log(String tag, String message, Throwable exception) {
		log(LOG_INFO, tag, message, exception);
	}

	@Override
	public void error(String tag, String message) {
		log(LOG_ERROR, tag, message, null);
	}

	@Override
	public void error(String tag, String message, Throwable exception) {
		log(LOG_ERROR, tag, message, exception);
	}

	@Override
	public void debug(String tag, String message) {
		log(LOG_DEBUG, tag, message, null);
	}

	@Override
	public void debug(String tag, String message, Throwable exception) {
		log(LOG_DEBUG, tag, message, exception);
	}

	@Override
	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}

	@Override
	public int getLogLevel() {
		return this.logLevel;
	}

	@Override
	public void exit() {
		System.exit(0);
	}

	@Override
	public void postRunnable(Runnable runnable) {
		postRunnableList.addLast(runnable);
	}

	@Override
	public void addLifecycleListener(LifecycleListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeLifecycleListener(LifecycleListener listener) {
		listeners.remove(listener);
	}

	protected void executePostRunnableQueue() {
		while (postRunnableList.size > 0) {
			postRunnableListCopy.addLast(postRunnableList.removeFirst());
		}

		while (postRunnableListCopy.size > 0) {
			Runnable runnable = postRunnableListCopy.removeFirst();
			runnable.run();
		}
	}

	public void onFrame() {
		executePostRunnableQueue();
	}

	public void onPaused() {
		applicationListener.pause();
		for (LifecycleListener listener : listeners) {
			listener.pause();
		}
	}

	public void onResumed() {
		applicationListener.resume();
		for (LifecycleListener listener : listeners) {
			listener.resume();
		}
	}

	public void onDisposed() {
		applicationListener.dispose();
		for (LifecycleListener listener : listeners) {
			listener.dispose();
		}
	}

	abstract protected Preferences createPreferences(String name);

	abstract protected Clipboard createClipboard();

	abstract protected Audio createAudio();

	abstract protected ApplicationType createApplicationType();

	private Clipboard clipboard;

	@Override
	public long getJavaHeap() {
		return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
	}

	@Override
	public long getNativeHeap() {
		return getJavaHeap();
	}

	@Override
	final public Clipboard getClipboard() {
		if (this.clipboard == null) this.clipboard = createClipboard();
		return this.clipboard;
	}

	private HashMap<String, Preferences> preferences = new HashMap<String, Preferences>();

	@Override
	final public Preferences getPreferences(String name) {
		if (!this.preferences.containsKey(name)) {
			this.preferences.put(name, createPreferences(name));
		}
		return this.preferences.get(name);
	}

	@Override
	final public ApplicationListener getApplicationListener() {
		return applicationListener;
	}

	@Override
	final public Audio getAudio() {
		return audio;
	}

	@Override
	final public Graphics getGraphics() {
		return graphics;
	}

	@Override
	final public Input getInput() {
		return input;
	}

	@Override
	final public Files getFiles() {
		return files;
	}

	@Override
	final public Net getNet() {
		return net;
	}

	@Override
	final public ApplicationType getType() {
		return type;
	}

	public void create() {
		applicationListener.create();
		if (graphics.isFullscreen()) {
			resized(LimeApplication.getDisplayWidth(), LimeApplication.getDisplayHeight());
		} else {
			resized(LimeApplication.getWindowWidth(), LimeApplication.getWindowHeight());
		}
	}

	public void render() {
		onFrame();
		applicationListener.render();
		graphics.frame();
	}

	private void resized(int width, int height) {
		if (LimeApplication.isAppCreated()) {
			graphics.onResized(width, height);
		}
		applicationListener.resize(graphics.getWidth(), graphics.getHeight());
	}
}
