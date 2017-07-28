package com.jtransc.media.limelibgdx;

import com.badlogic.gdx.audio.Sound;
import com.jtransc.annotation.haxe.HaxeAddMembers;
import com.jtransc.annotation.haxe.HaxeMethodBody;
import com.jtransc.annotation.haxe.HaxeImports;

@HaxeImports(
	"@:access(lime._backend.native.NativeAudioSource)" +
		"@:access(lime.media.AudioSource)"
)

@HaxeAddMembers({"var tracks:Array<lime.media.AudioSource>;" +
				 "var buffer:lime.media.AudioBuffer;" +
				 "private static inline var MAX_INSTANCES = 5;"})
class LimeSound implements Sound {

	private String name;


	@HaxeMethodBody(
			"buffer = lime.media.AudioBuffer.fromBytes(lime.Assets.getBytes(N.i_str(p0)));" +
			"if (buffer == null) return false;" +
			"tracks = new Array<lime.media.AudioSource>(); " +
			"for (i in 0...MAX_INSTANCES) { " +
				"tracks[i] = new lime.media.AudioSource(buffer); " +
				"tracks[i].onComplete.add(function():Void { " +
		  		  "tracks[i].backend.completed = false; " +
				  "tracks[i].currentTime = 0; " +
				"});" +
			"}" +
			"return true;"
	)
	private native boolean init0(String path);

	void init(String path) {
		name = path;
		boolean res = init0(path);
		if (LimeAudio.isAudioDebug()) {
			System.out.println("Lime sound loaded: " + name + (res ? " successful" : " failed"));
		}
	}

	@Override
	public long play() {
		return play(false, -1.0f);
	}

	@Override
	public long play(float volume) {
		return play(false, volume);
	}

	@Override
	public long play(float volume, float pitch, float pan) {
		return play(false, volume);
	}

	@Override
	public long loop() {
		return play(true, -1.0f);
	}

	@Override
	public long loop(float volume) {
		return play(true, volume);
	}

	@Override
	public long loop(float volume, float pitch, float pan) {
		return play(true, volume);
	}

	@HaxeMethodBody("if (buffer == null) return -1;" +
		"for (i in 0...MAX_INSTANCES) { " +
		 "if (!tracks[i].backend.playing) {" +
		  "tracks[i].loops = p0 ? 100000 : 0; " +
		  "var vol = p1 < 0 ? tracks[i].gain : p1 > 1.0 ? 1.0 : p1; tracks[i].gain = vol;" +
		  "tracks[i].play(); return i;" +
		"} }" +
		"return -1;")
	private native long play0(boolean loop, float volume);
	private long play(boolean loop, float volume){
		if (LimeAudio.isAudioDebug()) {
			System.out.println("Lime sound play: " + name + ", vol: " + volume);
		}
		return play0(loop, volume);
	}

	@Override
	@HaxeMethodBody("if (track.buffer == null) return;" +
		"for (i in 0...MAX_INSTANCES) { " +
		  "tracks[i].stop();" +
		"}")
	public native void stop();

	@Override
	@HaxeMethodBody("if (track.buffer == null) return;" +
		"for (i in 0...MAX_INSTANCES) { " +
		  "tracks[i].pause();" +
		"}")
	public native void pause();

	@Override
	public void resume() {
		play();
	}

	@Override
	@HaxeMethodBody("if (buffer == null) return;" +
		"buffer = null;" +
		"for (i in 0...MAX_INSTANCES) { " +
		  "tracks[i].dispose();" +
		"}")
	public native void dispose();

	@Override
	@HaxeMethodBody("if (p0 >= 0 && p0 < MAX_INSTANCES){ tracks[haxe.Int64.toInt(p0)].stop();}")
	public native void stop(long soundId);

	@Override
	@HaxeMethodBody("if (p0 >= 0 && p0 < MAX_INSTANCES){ tracks[haxe.Int64.toInt(p0)].pause();}")
	public native void pause(long soundId);

	@Override
	public void resume(long soundId) {
		resume();
	}

	@Override
	@HaxeMethodBody("if (p0 >= 0 && p0 < MAX_INSTANCES){ tracks[haxe.Int64.toInt(p0)].loops = p1 ? 100000 : 0;}")
	public native void setLooping(long soundId, boolean looping);

	@Override
	public void setPitch(long soundId, float pitch) {
	}

	@Override
	@HaxeMethodBody("if (buffer == null) return;" +
		"if (p0 >= 0 && p0 < MAX_INSTANCES) { " +
		  "var vol = p1 < 0 ? 0 : p1 > 1.0 ? 1.0 : p1; " +
		  "tracks[haxe.Int64.toInt(p0)].gain = vol;" +
		"}")
	public native void setVolume(long soundId, float volume);

	@Override
	public void setPan(long soundId, float pan, float volume) {

	}
}