package com.jtransc.media.limelibgdx;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Graphics;
import com.jtransc.JTranscSystem;
import com.jtransc.annotation.haxe.HaxeMethodBody;

public class LimeDevice {
	@HaxeMethodBody(target = "android", value = "return true;")
	@HaxeMethodBody("return false;")
	private static boolean isAndroid() {
		return false;
	}

	@HaxeMethodBody(target = "ios", value = "return true;")
	@HaxeMethodBody("return false;")
	private static boolean isIos() {
		return false;
	}

	@HaxeMethodBody(target = "tvos", value = "return true;")
	@HaxeMethodBody("return false;")
	static boolean isTvos() {
		return false;
	}

	private static boolean isJs() {
		return JTranscSystem.isJs();
	}

	static public boolean isFlash() {
		return JTranscSystem.isSwf();
	}

	public static Application.ApplicationType getType() {
		if (isJs()) {
			return Application.ApplicationType.WebGL;
		} else if (isIos() || isTvos()) {
			return Application.ApplicationType.iOS;
		} else if (isAndroid()) {
			return Application.ApplicationType.Android;
		}
		return Application.ApplicationType.Desktop;
	}

	static Graphics.GraphicsType getGraphicsType() {
		if (isJs()) {
			return Graphics.GraphicsType.WebGL;
		} else if (isIos() || isTvos()) {
			return Graphics.GraphicsType.iOSGL;
		} else if (isAndroid()) {
			return Graphics.GraphicsType.AndroidGL;
		}
		return Graphics.GraphicsType.LWJGL;
	}
}
