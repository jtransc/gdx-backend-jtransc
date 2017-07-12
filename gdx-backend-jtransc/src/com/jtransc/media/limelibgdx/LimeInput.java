package com.jtransc.media.limelibgdx;

import com.badlogic.gdx.*;
import com.jtransc.annotation.JTranscMethodBody;
import com.jtransc.annotation.haxe.HaxeMethodBody;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

public class LimeInput implements Input {

	private static final boolean INPUT_QUEUED = true;

	private static final int MAX_TOUCH_POINTS = 10;

	private static boolean[] keys = new boolean[0x200];
	private static boolean[] justPressed = new boolean[0x200];
	private static boolean[] justReleased = new boolean[0x200];
	private static boolean[] touchIndexes = new boolean[MAX_TOUCH_POINTS];
	private static final LinkedBlockingDeque<Pointer> inputQueue = new LinkedBlockingDeque<>();

	@HaxeMethodBody(
		"{% if extra.debugLimeInput %}return {{ extra.debugLimeInput }};{% else %}return false;{% end %}"
	)
	private static boolean isLimeInputDebug() {
		return false;
	}

	private static final HashMap<Integer, Pointer> pointers = new HashMap<>();
	private static final Pointer mousePoint = new Pointer();
	private static boolean lockMouse =
		LimeDevice.getType() == Application.ApplicationType.iOS
			|| LimeDevice.getType() == Application.ApplicationType.Android;

	static private InputProcessor inputProcessor = new InputAdapter();

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

	static void flushInput() {
		if (!INPUT_QUEUED) return;

		Pointer real_p;
		while (inputQueue.size() > 0) {
			Pointer p;
			synchronized (inputQueue) {
				p = inputQueue.poll();
			}
			switch (p.type) {
				case MOUSE_DOWN:
					mousePoint.pressButton(0);
					mousePoint.setXY(p.getX(), p.getY());
					inputProcessor.touchDown((int) p.getX(), (int) p.getY(), 0, 0);
					break;
				case TOUCH_START:
					int id = p.getIndex();
					p.setIndex(getIndex());
					pointers.put(id, p);
					inputProcessor.touchDown((int) p.getX(), (int) p.getY(), p.getIndex(), 0);
					break;
				case MOUSE_UP:
					mousePoint.setXY(p.getX(), p.getY());
					mousePoint.releaseButton(0);
					inputProcessor.touchUp((int) p.getX(), (int) p.getY(), 0, 0);
					break;
				case TOUCH_END:
					real_p = pointers.remove(p.getIndex());
					releaseIndex(real_p.getIndex());
					inputProcessor.touchUp((int) p.getX(), (int) p.getY(), real_p.getIndex(), 0);
					break;
				case MOUSE_MOVE:
					mousePoint.setXY(p.getX(), p.getY());
					if (mousePoint.isPressingAnyButton()) {
						inputProcessor.touchDragged((int) p.getX(), (int) p.getY(), 0);
					} else {
						inputProcessor.mouseMoved((int) p.getX(), (int) p.getY());
					}
					break;
				case MOUSE_WHEEL:
					inputProcessor.scrolled((int) p.getY());
					break;
				case TOUCH_MOVE:
					real_p = pointers.get(p.getIndex());
					real_p.setXY(p.getX(), p.getY());
					inputProcessor.touchDragged((int) p.getX(), (int) p.getY(), real_p.getIndex());
					break;
				case KEY_DOWN:
					break;
				case KEY_TYPED:
					break;
				case KEY_UP:
					break;
			}
		}
	}

	private static void addPointer(Pointer p) {
		if (!INPUT_QUEUED || p.type == UNDEFINED) return;
		synchronized (inputQueue) {
			try {
				inputQueue.put(p);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static int getIndex() {
		for (int i = 0; i < touchIndexes.length; i++) {
			if (!touchIndexes[i]) {
				touchIndexes[i] = true;
				return i;
			}
		}
		return MAX_TOUCH_POINTS;
	}

	private static void releaseIndex(int index) {
		if (index >= 0 && index < MAX_TOUCH_POINTS) {
			touchIndexes[index] = false;
		}
	}

	private static int toLogicalX(double realX) {
		float realWidth;
		if (Gdx.graphics.isFullscreen()) {
			realWidth = LimeApplication.getDisplayWidth();
		} else {
			realWidth = LimeApplication.getWindowWidth();
		}
		return (int) (realX * (Gdx.graphics.getWidth() / realWidth));
	}

	private static int toLogicalY(double realY) {
		float realHeight;
		if (Gdx.graphics.isFullscreen()) {
			realHeight = LimeApplication.getDisplayHeight();
		} else {
			realHeight = LimeApplication.getWindowHeight();
		}
		return (int) (realY * (Gdx.graphics.getHeight() / realHeight));
	}

	static void lime_onMouseUp(double x, double y, int button) {
		if (lockMouse) {
			return;
		}
		if (isLimeInputDebug()) {
			System.out.println("lime_onMouseUp(" + x + "," + y + "," + button + ")");
		}
		int localX = toLogicalX(x);
		int localY = toLogicalY(y);
		if (INPUT_QUEUED) {
			addPointer(new Pointer(localX, localY, MOUSE_UP, 0));
		} else {
			mousePoint.setXY(localX, localY);
			mousePoint.releaseButton(button);
			inputProcessor.touchUp(localX, localY, 0, button);
		}
	}

	static void lime_onMouseDown(double x, double y, int button) {
		if (lockMouse) {
			return;
		}
		if (isLimeInputDebug()) {
			System.out.println("lime_onMouseDown(" + x + "," + y + "," + button + ")");
		}
		int localX = toLogicalX(x);
		int localY = toLogicalY(y);
		if (INPUT_QUEUED) {
			addPointer(new Pointer(localX, localY, MOUSE_DOWN, 0));
		} else {
			mousePoint.setXY(localX, localY);
			mousePoint.pressButton(button);
			inputProcessor.touchDown(localX, localY, 0, button);
		}
	}

	static void lime_onMouseMove(double x, double y) {
		if (lockMouse) {
			return;
		}
		if (isLimeInputDebug()) {
			System.out.println("lime_onMouseMove(" + x + "," + y + ")");
		}
		int localX = toLogicalX(x);
		int localY = toLogicalY(y);
		if (INPUT_QUEUED) {
			addPointer(new Pointer(localX, localY, MOUSE_MOVE, 0));
		} else {
			mousePoint.setXY(localX, localY);
			if (mousePoint.isPressingAnyButton()) {
				inputProcessor.touchDragged(localX, localY, 0);
			} else {
				inputProcessor.mouseMoved(localX, localY);
			}
		}
	}

	@SuppressWarnings("unused")
	static void lime_onWheel(double x, double y, double z) {
		if (isLimeInputDebug()) {
			System.out.println("lime_onWheel(" + x + ", " + y + ", " + z + ")");
		}
		if (INPUT_QUEUED) {
			addPointer(new Pointer(x, y, MOUSE_WHEEL, 0));
		} else {
			inputProcessor.scrolled((int) -y);
		}
	}

	static void lime_onKeyUp(int keyCode, int modifier) {
		if (isLimeInputDebug()) {
			System.out.println("lime_onKeyUp(" + keyCode + "," + modifier + ")");
		}
		int key = convertKeyCode(keyCode);
		keys[key & 0x1FF] = false;
		justReleased[key & 0x1FF] = true;
		inputProcessor.keyUp(key);
	}

	static void lime_onKeyDown(int keyCode, int modifier) {
		if (isLimeInputDebug()) {
			System.out.println("lime_onKeyDown(" + keyCode + "," + modifier + ")");
		}
		int key = convertKeyCode(keyCode);
		keys[key & 0x1FF] = true;
		justPressed[key & 0x1FF] = true;
		inputProcessor.keyDown(key);
	}

	private static void lime_onKeyTyped(char character) {
		if (isLimeInputDebug()) {
			System.out.println("lime_onKeyTyped(" + character + ")");
		}
		inputProcessor.keyTyped(character);
	}

	static void lime_onTouchStart(int id, double x, double y) {
		if (isLimeInputDebug()) {
			System.out.println("lime_onTouchStart(" + id + "," + x + "," + y + ")");
		}
		int localX = (int) (Gdx.graphics.getWidth() * x);
		int localY = (int) (Gdx.graphics.getHeight() * y);
		Pointer p = new Pointer();
		p.setXY(localX, localY);
		p.pressButton(0);
		if (INPUT_QUEUED) {
			p.setIndex(id);
			p.type = TOUCH_START;
			addPointer(p);
		} else {
			p.setIndex(getIndex());
			pointers.put(id, p);
			inputProcessor.touchDown(localX, localY, p.getIndex(), 0);
		}
	}

	static void lime_onTouchMove(int id, double x, double y) {
		if (isLimeInputDebug()) {
			System.out.println("lime_onTouchMove(" + id + "," + x + "," + y + ")");
		}
		int localX = (int) (Gdx.graphics.getWidth() * x);
		int localY = (int) (Gdx.graphics.getHeight() * y);
		if (INPUT_QUEUED) {
			addPointer(new Pointer(localX, localY, TOUCH_MOVE, id));
		} else {
			Pointer p = pointers.get(id);
			p.setXY(localX, localY);
			inputProcessor.touchDragged(localX, localY, p.getIndex());
		}
	}

	static void lime_onTouchEnd(int id, double x, double y) {
		if (isLimeInputDebug()) {
			System.out.println("lime_onTouchEnd(" + id + "," + x + "," + y + ")");
		}
		int localX = (int) (Gdx.graphics.getWidth() * x);
		int localY = (int) (Gdx.graphics.getHeight() * y);
		if (INPUT_QUEUED) {
			addPointer(new Pointer(localX, localY, TOUCH_END, id));
		} else {
			Pointer p = pointers.remove(id);
			releaseIndex(p.getIndex());
			inputProcessor.touchUp(localX, localY, p.getIndex(), 0);
		}
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
		for (Map.Entry<Integer, Pointer> entry : pointers.entrySet()) {
			entry.getValue().frame();
		}
		if (!lockMouse) {
			mousePoint.frame();
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
			for (Map.Entry<Integer, Pointer> entry : pointers.entrySet()) {
				if (entry.getValue().getIndex() == i) return (int) entry.getValue().getX();
			}
			return 0;
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
			for (Map.Entry<Integer, Pointer> entry : pointers.entrySet()) {
				if (entry.getValue().getIndex() == i) return (int) entry.getValue().getDeltaX();
			}
			return 0;
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
			for (Map.Entry<Integer, Pointer> entry : pointers.entrySet()) {
				if (entry.getValue().getIndex() == i) return (int) entry.getValue().getY();
			}
			return 0;
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
			for (Map.Entry<Integer, Pointer> entry : pointers.entrySet()) {
				if (entry.getValue().getIndex() == i) return (int) entry.getValue().getDeltaY();
			}
			return 0;
		}
		return i == 0 ? (int) mousePoint.getDeltaY() : 0;
	}

	@Override
	public boolean isTouched() {
		if (lockMouse) {
			for (Map.Entry<Integer, Pointer> entry : pointers.entrySet()) {
				if (entry.getValue().isPressingAnyButton()) return true;
			}
			return false;
		}
		return mousePoint.isPressingAnyButton();
	}

	@Override
	public boolean justTouched() {
		if (lockMouse) {
			for (Map.Entry<Integer, Pointer> entry : pointers.entrySet()) {
				if (entry.getValue().justPressedAnyButton()) return true;
			}
			return false;
		}
		return mousePoint.justPressedAnyButton();
	}

	@Override
	public boolean isTouched(int i) {
		if (lockMouse) {
			for (Map.Entry<Integer, Pointer> entry : pointers.entrySet()) {
				if (entry.getValue().getIndex() == i) return pointers.get(i).isPressingAnyButton();
			}
			return false;
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
		private double lastX;
		private double lastY;
		private double currentX;
		private double currentY;
		private int index = -1;
		public int type;

		Pointer() {
		}

		Pointer(double x, double y, int type, int index) {
			currentX = x;
			currentY = y;
			this.type = type;
			this.index = index;
		}

		void setXY(double x, double y) {
			this.currentX = x;
			this.currentY = y;
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
