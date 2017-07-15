package com.jtransc.media.limelibgdx;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.jtransc.annotation.JTranscMethodBody;
import com.jtransc.annotation.haxe.HaxeMethodBody;

import java.util.ArrayDeque;

public class LimeInput implements Input {

	private static final int MAX_TOUCH_POINTS = 20;

	private static final int UNDEFINED = 0;
	private static final int MOUSE_DOWN = 1;
	private static final int MOUSE_UP = 2;
	private static final int MOUSE_MOVE = 3;
	private static final int MOUSE_WHEEL = 4;
	private static final int TOUCH_START = 5;
	private static final int TOUCH_END = 6;
	private static final int TOUCH_MOVE = 7;
	private static final int KEY_DOWN = 8;
	private static final int KEY_TYPED = 9;
	private static final int KEY_UP = 10;

	@HaxeMethodBody("{% if extra.debugLimeInput %}return {{ extra.debugLimeInput }};{% else %}return false;{% end %}")
	private static boolean isLimeInputDebug() {
		return false;
	}

	private static boolean[] keys = new boolean[0x200];
	private static boolean[] justPressed = new boolean[0x200];
	private static boolean[] justReleased = new boolean[0x200];

	private static final Pointer[] workingPointers = new Pointer[MAX_TOUCH_POINTS];
	private static final ArrayDeque<Pointer> queuedPointers = new ArrayDeque<>();
	private static final ArrayDeque<Pointer> freePointers = new ArrayDeque<>();

	private static final Pointer mousePoint = new Pointer();

	private static boolean lockMouse = !LimeDevice.isTvos() &&
		(LimeDevice.getType() == Application.ApplicationType.iOS
			|| LimeDevice.getType() == Application.ApplicationType.Android);

	static private InputProcessor inputProcessor = new InputAdapter();

	private static TextureRegion cursor;
	private static SpriteBatch batch;

	static {
		for (int i = 0; i < MAX_TOUCH_POINTS; i++) {
			workingPointers[i] = new Pointer();
		}
	}

	static void flushInput() {
		while (true) {
			Pointer p;
			synchronized (queuedPointers) {
				if (queuedPointers.size() > 0) {
					p = queuedPointers.poll();
				} else {
					return;
				}
			}
			switch (p.type) {
				case MOUSE_DOWN:
					lime_onMouseDown0(p);
					break;
				case MOUSE_UP:
					lime_onMouseUp0(p);
					break;
				case MOUSE_MOVE:
					lime_onMouseMove0(p);
					break;
				case MOUSE_WHEEL:
					lime_onWheel0(p);
					break;
				case TOUCH_START:
					lime_onTouchStart0(p);
					break;
				case TOUCH_END:
					lime_onTouchEnd0(p);
					break;
				case TOUCH_MOVE:
					lime_onTouchMove0(p);
					break;
				case KEY_DOWN:
					lime_onKeyDown0(p);
					break;
				case KEY_TYPED:
					lime_onKeyTyped0(p);
					break;
				case KEY_UP:
					lime_onKeyUp0(p);
					break;
			}
			p.reset();
			synchronized (freePointers) {
				freePointers.add(p);
			}
		}
	}

	private static void addPointer(int x, int y, int type) {
		addPointer(x, y, type, 0);
	}

	private static void addPointer(int x, int y, int type, int data) {
		Pointer p = getPointer();
		p.setXY(x, y);
		p.type = type;
		p.customData = data;
		synchronized (queuedPointers) {
			queuedPointers.add(p);
		}
	}

	private static Pointer getPointer() {
		synchronized (freePointers) {
			if (freePointers.size() > 0) {
				return freePointers.poll();
			}
		}
		return new Pointer();
	}

	private static int findByIndex(int id) {
		for (int i = 0; i < MAX_TOUCH_POINTS; i++) {
			if (workingPointers[i].getIndex() == id) return i;
		}
		return MAX_TOUCH_POINTS;
	}

	private static int findFree() {
		for (int i = 0; i < MAX_TOUCH_POINTS; i++) {
			if (workingPointers[i].isFree) return i;
		}
		return MAX_TOUCH_POINTS;
	}

	private static int toLogicalX(double realX) {
		float realWidth;
		if (Gdx.graphics.isFullscreen()) {
			realWidth = LimeApplication.getDisplayWidth();
		} else {
			realWidth = LimeApplication.getWindowWidth();
		}
		realX /= realWidth;
		if (LimeDevice.isTvos()) {
			realX -= 0.5;
			realX *= 0.5;
		}
		return (int) (realX * Gdx.graphics.getWidth());
	}

	private static int toLogicalY(double realY) {
		float realHeight;
		if (Gdx.graphics.isFullscreen()) {
			realHeight = LimeApplication.getDisplayHeight();
		} else {
			realHeight = LimeApplication.getWindowHeight();
		}
		realY /= realHeight;
		if (LimeDevice.isTvos()) {
			realY -= 0.5;
			realY *= 0.5;
		}
		return (int) (realY * Gdx.graphics.getHeight());
	}

	static void lime_onMouseUp(double x, double y, int button) {
		if (lockMouse) {
			return;
		}
		if (isLimeInputDebug()) {
			System.out.println("lime_onMouseUp(" + x + "," + y + "," + button + ")");
		}
		addPointer(toLogicalX(x), toLogicalY(y), LimeDevice.isTvos() ? MOUSE_MOVE : MOUSE_UP, button);
	}

	private static void lime_onMouseUp0(Pointer p) {
		if (LimeDevice.isTvos()) {
			mousePoint.addXY(p.getX(), p.getY());
		} else {
			mousePoint.setXY(p.getX(), p.getY());
		}
		mousePoint.releaseButton(p.customData);
		inputProcessor.touchUp((int) mousePoint.getX(), (int) mousePoint.getY(), 0, p.customData);
	}

	static void lime_onMouseDown(double x, double y, int button) {
		if (lockMouse) {
			return;
		}
		if (isLimeInputDebug()) {
			System.out.println("lime_onMouseDown(" + x + "," + y + "," + button + ")");
		}
		addPointer(toLogicalX(x), toLogicalY(y), LimeDevice.isTvos() ? MOUSE_MOVE : MOUSE_DOWN, button);
	}

	private static void lime_onMouseDown0(Pointer p) {
		if (LimeDevice.isTvos()) {
			mousePoint.addXY(p.getX(), p.getY());
		} else {
			mousePoint.setXY(p.getX(), p.getY());
		}
		mousePoint.pressButton(p.customData);
		inputProcessor.touchDown((int) mousePoint.getX(), (int) mousePoint.getY(), 0, p.customData);
	}

	static void lime_onMouseMove(double x, double y) {
		if (lockMouse) {
			return;
		}
		if (isLimeInputDebug()) {
			System.out.println("lime_onMouseMove(" + x + "," + y + ")");
		}
		addPointer(toLogicalX(x), toLogicalY(y), MOUSE_MOVE);
	}

	private static void lime_onMouseMove0(Pointer p) {
		if (LimeDevice.isTvos()) {
			mousePoint.addXY(p.getX(), p.getY());
		} else {
			mousePoint.setXY(p.getX(), p.getY());
		}
		if (mousePoint.isPressingAnyButton()) {
			inputProcessor.touchDragged((int) mousePoint.getX(), (int) mousePoint.getY(), 0);
		} else {
			inputProcessor.mouseMoved((int) mousePoint.getX(), (int) mousePoint.getY());
		}
	}

	@SuppressWarnings("unused")
	static void lime_onWheel(double x, double y, double z) {
		if (isLimeInputDebug()) {
			System.out.println("lime_onWheel(" + x + ", " + y + ", " + z + ")");
		}
		addPointer((int) x, (int) -y, MOUSE_WHEEL);
	}

	private static void lime_onWheel0(Pointer p) {
		inputProcessor.scrolled((int) p.getY());
	}

	static void lime_onKeyUp(int keyCode, int modifier) {
		if (isLimeInputDebug()) {
			System.out.println("lime_onKeyUp(" + keyCode + "," + modifier + ")");
		}
		addPointer(0, 0, KEY_UP, convertKeyCode(keyCode));
	}

	private static void lime_onKeyUp0(Pointer p) {
		int key = p.customData;
		if (LimeDevice.isTvos() && key == Keys.UNKNOWN) {
			lime_onMouseUp0(p);
			return;
		}
		keys[key & 0x1FF] = false;
		justReleased[key & 0x1FF] = true;
		inputProcessor.keyUp(key);
	}

	static void lime_onKeyDown(int keyCode, int modifier) {
		if (isLimeInputDebug()) {
			System.out.println("lime_onKeyDown(" + keyCode + "," + modifier + ")");
		}
		addPointer(0, 0, KEY_DOWN, convertKeyCode(keyCode));
	}

	private static void lime_onKeyDown0(Pointer p) {
		int key = p.customData;
		if (LimeDevice.isTvos() && key == Keys.UNKNOWN) {
			lime_onMouseDown0(p);
			return;
		}
		keys[key & 0x1FF] = true;
		justPressed[key & 0x1FF] = true;
		inputProcessor.keyDown(key);
	}

	private static void lime_onKeyTyped(char character) {
		if (isLimeInputDebug()) {
			System.out.println("lime_onKeyTyped(" + character + ")");
		}
		addPointer(0, 0, KEY_TYPED, character);
	}

	private static void lime_onKeyTyped0(Pointer p) {
		inputProcessor.keyTyped((char) p.customData);
	}

	static void lime_onTouchStart(int id, double x, double y) {
		if (LimeDevice.isTvos()) {
			return;
		}
		if (isLimeInputDebug()) {
			System.out.println("lime_onTouchStart(" + id + "," + x + "," + y + ")");
		}
		int localX = (int) (Gdx.graphics.getWidth() * x);
		int localY = (int) (Gdx.graphics.getHeight() * y);
		addPointer(localX, localY, TOUCH_START, id);
	}

	private static void lime_onTouchStart0(Pointer p) {
		int i = findFree();
		workingPointers[i].isFree = false;
		workingPointers[i].setIndex(p.customData);
		workingPointers[i].setXY(p.getX(), p.getY());
		workingPointers[i].pressButton(0);
		inputProcessor.touchDown((int) p.getX(), (int) p.getY(), i, 0);
	}

	static void lime_onTouchMove(int id, double x, double y) {
		if (LimeDevice.isTvos()) {
			return;
		}
		if (isLimeInputDebug()) {
			System.out.println("lime_onTouchMove(" + id + "," + x + "," + y + ")");
		}
		int localX = (int) (Gdx.graphics.getWidth() * x);
		int localY = (int) (Gdx.graphics.getHeight() * y);
		addPointer(localX, localY, TOUCH_MOVE, id);
	}

	private static void lime_onTouchMove0(Pointer p) {
		int i = findByIndex(p.customData);
		workingPointers[i].setXY(p.getX(), p.getY());
		inputProcessor.touchDragged((int) p.getX(), (int) p.getY(), i);
	}

	static void lime_onTouchEnd(int id, double x, double y) {
		if (LimeDevice.isTvos()) {
			return;
		}
		if (isLimeInputDebug()) {
			System.out.println("lime_onTouchEnd(" + id + "," + x + "," + y + ")");
		}
		int localX = (int) (Gdx.graphics.getWidth() * x);
		int localY = (int) (Gdx.graphics.getHeight() * y);
		addPointer(localX, localY, TOUCH_END, id);
	}

	private static void lime_onTouchEnd0(Pointer p) {
		int i = findByIndex(p.customData);
		workingPointers[i].reset();
		inputProcessor.touchUp((int) p.getX(), (int) p.getY(), i, 0);
	}

	@SuppressWarnings("unused")
	public void onMouseUp(double x, double y, int button) {
		lime_onMouseUp(x, y, button);
	}

	@SuppressWarnings("unused")
	public void onMouseDown(double x, double y, int button) {
		lime_onMouseDown(x, y, button);
	}

	@SuppressWarnings("unused")
	public void onMouseMove(double x, double y) {
		lime_onMouseMove(x, y);
	}

	@SuppressWarnings("unused")
	void onKeyUp(int keyCode, int modifier) {
		lime_onKeyUp(keyCode, modifier);
	}

	@SuppressWarnings("unused")
	public void onKeyDown(int keyCode, int modifier) {
		lime_onKeyDown(keyCode, modifier);
	}

	@SuppressWarnings("unused")
	public void onKeyTyped(char character) {
		lime_onKeyTyped(character);
	}

	@SuppressWarnings("unused")
	public void onTouchStart(int id, double x, double y) {
		lime_onTouchStart(id, x, y);
	}

	@SuppressWarnings("unused")
	public void onTouchMove(int id, double x, double y) {
		lime_onTouchMove(id, x, y);
	}

	@SuppressWarnings("unused")
	public void onTouchEnd(int id, double x, double y) {
		lime_onTouchEnd(id, x, y);
	}

	// Called once per frame1
	static void lime_frame() {
		for (int n = 0; n < justPressed.length; n++) justPressed[n] = false;
		for (int n = 0; n < justReleased.length; n++) justReleased[n] = false;
		for (int i = 0; i < MAX_TOUCH_POINTS; i++) {
			if (!workingPointers[i].isFree) workingPointers[i].frame();
		}
		if (!lockMouse) {
			mousePoint.frame();
		}
		if (LimeDevice.isTvos()) {
			if (batch == null) {
				Pixmap circle = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
				circle.setColor(Color.RED);
				circle.fill();
				cursor = new TextureRegion(new Texture(circle), 10, 10);
				circle.dispose();
				batch = new SpriteBatch();
				batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			}
			batch.begin();
			batch.draw(cursor, (float) mousePoint.getX(),
				(float) (Gdx.graphics.getHeight() - mousePoint.getY() - cursor.getRegionHeight()));
			batch.end();
		}
	}

	// @TODO: https://github.com/openfl/lime/blob/develop/lime/system/Sensor.hx
	@Override
	public float getAccelerometerX() {
		return 0;
	}

	@Override
	public float getAccelerometerY() {
		return 0;
	}

	@Override
	public float getAccelerometerZ() {
		return 0;
	}

	@Override
	public float getGyroscopeX() {
		return 0;
	}

	@Override
	public float getGyroscopeY() {
		return 0;
	}

	@Override
	public float getGyroscopeZ() {
		return 0;
	}

	@Override
	public int getX() {
		return getX(0);
	}

	@Override
	public int getX(int i) {
		if (lockMouse) {
			return (int) workingPointers[i].getX();
		}
		return i == 0 ? (int) mousePoint.getX() : 0;
	}

	@Override
	public int getDeltaX() {
		return getDeltaX(0);
	}

	@Override
	public int getDeltaX(int i) {
		if (lockMouse) {
			return (int) workingPointers[i].getDeltaX();
		}
		return i == 0 ? (int) mousePoint.getDeltaX() : 0;
	}

	@Override
	public int getY() {
		return getY(0);
	}

	@Override
	public int getY(int i) {
		if (lockMouse) {
			return (int) workingPointers[i].getY();
		}
		return i == 0 ? (int) mousePoint.getY() : 0;
	}

	@Override
	public int getDeltaY() {
		return getDeltaY(0);
	}

	@Override
	public int getDeltaY(int i) {
		if (lockMouse) {
			return (int) workingPointers[i].getDeltaY();
		}
		return i == 0 ? (int) mousePoint.getDeltaY() : 0;
	}

	@Override
	public boolean isTouched() {
		if (lockMouse) {
			for (int i = 0; i < MAX_TOUCH_POINTS; i++) {
				if (workingPointers[i].isPressingAnyButton()) return true;
			}
			return false;
		}
		return mousePoint.isPressingAnyButton();
	}

	@Override
	public boolean justTouched() {
		if (lockMouse) {
			for (int i = 0; i < MAX_TOUCH_POINTS; i++) {
				if (workingPointers[i].justPressedAnyButton()) return true;
			}
			return false;
		}
		return mousePoint.justPressedAnyButton();
	}

	@Override
	public boolean isTouched(int i) {
		if (lockMouse) {
			workingPointers[i].isPressingAnyButton();
		}
		return i == 0 && mousePoint.isPressingAnyButton();
	}

	@Override
	public boolean isButtonPressed(int i) {
		return mousePoint.isPressingButton(i);
	}

	@JTranscMethodBody(target = "js", value = "return p0;")
	private static int convertKeyCode(int i) {
		// https://github.com/openfl/lime/blob/develop/lime/ui/KeyCode.hx
		switch (i) {
			case 0x0D:
				return Keys.ENTER;
			case 0x1B:
				return Keys.ESCAPE;
			case 0x20:
				return Keys.SPACE;
			case 0x4000004F:
				return Keys.RIGHT;
			case 0x40000050:
				return Keys.LEFT;
			case 0x40000051:
				return Keys.DOWN;
			case 0x40000052:
				return Keys.UP;
			case 0x61:
				return Keys.A;
			case 0x62:
				return Keys.B;
			case 0x63:
				return Keys.C;
			case 0x64:
				return Keys.D;
			case 0x65:
				return Keys.E;
			case 0x66:
				return Keys.F;
			case 0x67:
				return Keys.G;
			case 0x68:
				return Keys.H;
			case 0x69:
				return Keys.I;
			case 0x6A:
				return Keys.J;
			case 0x6B:
				return Keys.K;
			case 0x6C:
				return Keys.L;
			case 0x6D:
				return Keys.M;
			case 0x6E:
				return Keys.N;
			case 0x6F:
				return Keys.O;
			case 0x70:
				return Keys.P;
			case 0x71:
				return Keys.Q;
			case 0x72:
				return Keys.R;
			case 0x73:
				return Keys.S;
			case 0x74:
				return Keys.T;
			case 0x75:
				return Keys.U;
			case 0x76:
				return Keys.V;
			case 0x77:
				return Keys.W;
			case 0x78:
				return Keys.X;
			case 0x79:
				return Keys.Y;
			case 0x80:
				return Keys.Z;
		}
		return Keys.UNKNOWN;
	}

	private boolean checkKeyArray(boolean[] array, int i) {
		final int actualIndex = (i < 0) ? 0 : i;
		return actualIndex < array.length && array[actualIndex];
	}

	@Override
	public boolean isKeyPressed(int i) {
		return checkKeyArray(keys, i);
	}

	@Override
	public boolean isKeyJustPressed(int i) {
		return checkKeyArray(justPressed, i);
	}

	@Override
	public void getTextInput(TextInputListener textInputListener, String s, String s1, String s2) {
	}

	@Override
	public void setOnscreenKeyboardVisible(boolean b) {
	}

	@Override
	public void vibrate(int i) {
	}

	@Override
	public void vibrate(long[] longs, int i) {
	}

	@Override
	public void cancelVibrate() {
	}

	@Override
	public float getAzimuth() {
		return 0;
	}

	@Override
	public float getPitch() {
		return 0;
	}

	@Override
	public float getRoll() {
		return 0;
	}

	@Override
	public void getRotationMatrix(float[] floats) {
	}

	@Override
	public long getCurrentEventTime() {
		return 0;
	}

	@Override
	public void setCatchBackKey(boolean b) {
	}

	@Override
	public boolean isCatchBackKey() {
		return false;
	}

	@Override
	public void setCatchMenuKey(boolean b) {
	}

	@Override
	public boolean isCatchMenuKey() {
		return false;
	}

	@Override
	public void setInputProcessor(InputProcessor inputProcessor) {
		LimeInput.inputProcessor = inputProcessor;
	}

	@Override
	public InputProcessor getInputProcessor() {
		return inputProcessor;
	}

	@Override
	public boolean isPeripheralAvailable(Peripheral peripheral) {
		return false;
	}

	@Override
	public int getRotation() {
		return 0;
	}

	@Override
	public Orientation getNativeOrientation() {
		return Orientation.Landscape;
	}

	@Override
	public void setCursorCatched(boolean b) {

	}

	@Override
	public boolean isCursorCatched() {
		return false;
	}

	@Override
	public void setCursorPosition(int i, int i1) {
	}

	private static class Pointer {
		private int lastB;
		private int currentB;
		private double lastX = -1;
		private double lastY = -1;
		private double currentX = -1;
		private double currentY = -1;

		private int index = -1;
		private int type;
		private int customData = 0;
		private boolean isFree = true;

		void reset() {
			lastX = lastY = currentY = currentX = -1;
			customData = lastB = currentB = 0;
			type = UNDEFINED;
			index = -1;
			isFree = true;
		}

		void setXY(double x, double y) {
			this.currentX = x;
			this.currentY = y;
		}

		void addXY(double deltaX, double deltaY) {
			int w = Gdx.graphics.getWidth() - cursor.getRegionWidth();
			int h = Gdx.graphics.getHeight() - cursor.getRegionHeight();
			if (currentX < 0 || currentY < 0) { // Set init values for Apple TV
				currentX = w * 0.5;
				currentY = h * 0.5;
			}
			currentX += deltaX;
			currentY += deltaY;

			currentX = currentX < 0 ? 0 : currentX > w ? w : currentX;
			currentY = currentY < 0 ? 0 : currentY > h ? h : currentY;
		}

		public void setB(int b) {
			this.currentB = b;
		}

		void frame() {
			this.lastX = currentX;
			this.lastY = currentY;
			this.lastB = currentB;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public double getX() {
			return currentX;
		}

		public double getY() {
			return currentY;
		}

		double getDeltaX() {
			return currentX - lastX;
		}

		double getDeltaY() {
			return currentY - lastY;
		}

		boolean isPressingButton(int i) {
			return (currentB & (1 << i)) != 0;
		}

		boolean isPressingAnyButton() {
			return currentB != 0;
		}

		boolean justPressedAnyButton() {
			return isPressingAnyButton() && (currentB != lastB);
		}

		void pressButton(int button) {
			int mask = (1 << button);
			currentB = (currentB & ~mask) | mask;
		}

		void releaseButton(int button) {
			int mask = (1 << button);
			currentB = (currentB & ~mask);
		}
	}
}
