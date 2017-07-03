package com.jtransc.media.limelibgdx;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.jtransc.JTranscSystem;
import com.jtransc.annotation.haxe.HaxeMethodBody;
import com.jtransc.media.limelibgdx.logger.LoggerGL20;

public class LimeGraphics implements Graphics {

	final private GL20 gl;

	private int frameId = 0;

	private int currentWidth = LwjglApplicationConfiguration.defaultWidth;
	private int currentHeight = LwjglApplicationConfiguration.defaultHeight;

	public static class JtranscMonitor extends Monitor {
		JtranscMonitor(int virtualX, int virtualY, String name) {
			super(virtualX, virtualY, name);
		}
	}
	public static class JtranscMode extends DisplayMode {
		public JtranscMode(int width, int height, int refreshRate, int bitsPerPixel) {
			super(width, height, refreshRate, bitsPerPixel);
		}
	}
	private final Monitor[] monitors;
	private JtranscMode[] displayModes;

	LimeGraphics(int width, int height, boolean trace) {
		currentWidth = width;
		currentHeight = height;
		monitors = new Monitor[]{
			new JtranscMonitor(width, height, "default")
		};
		displayModes = new JtranscMode[]{
			new JtranscMode(width, height, getFramesPerSecond(), 32)
		};
		GL20Ext gl = getInternalGl();
		if (trace) gl = new LoggerGL20(gl);
		this.gl = gl;
	}

	@Override
	public int getWidth() {
		return currentWidth;
	}

	@Override
	public int getHeight() {
		return currentHeight;
	}

	@Override
	public int getBackBufferWidth() {
		if (isFullscreen()) {
			return LimeApplication.getDisplayWidth();
		}
		return LimeApplication.getWindowWidth();
	}

	@Override
	public int getBackBufferHeight() {
		if (isFullscreen()) {
			return LimeApplication.getDisplayHeight();
		}
		return LimeApplication.getWindowHeight();
	}

	void onResized(int width, int height) {
		monitors[0] = new JtranscMonitor(LimeApplication.getDisplayWidth(), LimeApplication.getDisplayHeight(), "default");
		displayModes[0] = new JtranscMode(LimeApplication.getDisplayWidth(), LimeApplication.getDisplayHeight(), getFramesPerSecond(), 32);
	}

	@HaxeMethodBody("return {% SMETHOD com.jtransc.media.limelibgdx.gl.LimeGL20:create %}();")
	native private GL20Ext getInternalGl();

	double lastStamp = 0.0;
	float deltaTime = 0f;

	void frame() {
		double currentStamp = JTranscSystem.stamp();
		frameId++;

		double ms = JTranscSystem.elapsedTime(lastStamp, currentStamp);
		deltaTime = (float) ms / 1000f;

		lastStamp = currentStamp;
	}

	@Override
	public boolean isGL30Available() {
		return false;
	}

	@Override
	public GL20 getGL20() {
		return gl;
	}

	@Override
	public GL30 getGL30() {
		return null;
	}

	@Override
	public long getFrameId() {
		return frameId;
	}

	@Override
	public float getDeltaTime() {
		return deltaTime;
	}

	@Override
	public float getRawDeltaTime() {
		return deltaTime;
	}

	@Override
	public int getFramesPerSecond() {
		return LwjglApplicationConfiguration.getFramesPerSecond();
	}

	@Override
	public GraphicsType getType() {
		return LimeDevice.getGraphicsType();
	}

	@Override
	public GLVersion getGLVersion() {
		return new GLVersion(LimeDevice.getType(), "2.0", "lime", "lime");
	}

	// https://github.com/openfl/lime/blob/develop/lime/system/System.hx
	// https://github.com/openfl/lime/blob/develop/lime/system/Display.hx
	@HaxeMethodBody("return lime.system.System.getDisplay(0).dpi;")
	private float getPpi() {
		return 96;
	}

	@Override
	public float getPpiX() {
		return getPpi();
	}

	@Override
	public float getPpiY() {
		return getPpi();
	}

	static private final float CENTIMETERS_PER_INCH = 2.54f;
	static private final float DENSITY_FACTOR = 160f;

	@Override
	public float getPpcX() {
		return getPpiX() / CENTIMETERS_PER_INCH;
	}

	@Override
	public float getPpcY() {
		return getPpiY() / CENTIMETERS_PER_INCH;
	}

	@Override
	public float getDensity() {
		return getPpi() / DENSITY_FACTOR;
	}

	@Override
	public boolean supportsDisplayModeChange() {
		return false;
	}

	@Override
	public Monitor getPrimaryMonitor() {
		return monitors[0];
	}

	@Override
	public Monitor getMonitor() {
		return monitors[0];
	}

	@Override
	public Monitor[] getMonitors() {
		return monitors;
	}

	@Override
	public DisplayMode[] getDisplayModes() {
		return displayModes;
	}

	@Override
	public DisplayMode[] getDisplayModes(Monitor monitor) {
		return displayModes;
	}

	@Override
	public DisplayMode getDisplayMode() {
		return displayModes[0];
	}

	@Override
	public DisplayMode getDisplayMode(Monitor monitor) {
		return displayModes[0];
	}

	@Override
	public boolean setFullscreenMode(DisplayMode displayMode) {
		return false;
	}

	@Override
	public boolean setWindowedMode(int width, int height) {
		return false;
	}

	@Override
	public void setTitle(String title) {

	}

	@Override
	public void setUndecorated(boolean undecorated) {

	}

	@Override
	public void setResizable(boolean resizable) {

	}

	@Override
	public void setVSync(boolean vsync) {

	}

	@Override
	public BufferFormat getBufferFormat() {
		return null;
	}

	@Override
	public boolean supportsExtension(String extension) {
		return false;
	}

	@Override
	public void setContinuousRendering(boolean isContinuous) {

	}

	@Override
	public boolean isContinuousRendering() {
		return false;
	}

	@Override
	public void requestRendering() {

	}

	@HaxeMethodBody(
		"{% if fullscreen %}return {{ fullscreen }};{% end %}" +
		"{% if !fullscreen %}return false;{% end %}"
	)
	@Override
	public boolean isFullscreen() {
		return false;
	}

	@Override
	public Cursor newCursor(Pixmap pixmap, int xHotspot, int yHotspot) {
		return null;
	}

	@Override
	public void setCursor(Cursor cursor) {

	}

	@Override
	public void setSystemCursor(Cursor.SystemCursor systemCursor) {

	}
}
