package com.jtransc.media.limelibgdx;

import com.badlogic.gdx.audio.Music;
import com.jtransc.annotation.haxe.HaxeAddMembers;
import com.jtransc.annotation.haxe.HaxeImports;
import com.jtransc.annotation.haxe.HaxeMethodBody;

@HaxeImports(
	"@:access(lime._backend.native.NativeAudioSource)" +
		"@:access(lime.media.AudioSource)"
)

@HaxeAddMembers({"var track:lime.media.AudioSource;"})
class LimeMusic implements Music {
	private boolean isPlaying = false;
	private String name;
	private OnCompletionListener listener;

	@HaxeMethodBody("track = new lime.media.AudioSource(lime.media.AudioBuffer.fromFile(N.i_str(p0))); " +
		"track.onComplete.add(function():Void { this.{% METHOD com.jtransc.media.limelibgdx.LimeMusic:onComplete %}();});")
	private native void init0(String path);

	void init(String path) {
		name = path;
		init0(path);
	}

	@SuppressWarnings("unused")
	void onComplete() {
		if (LimeAudio.isAudioDebug()) {
			System.out.println("Lime music onComplete: " + name);
		}
		isPlaying = false;
		setPosition(0);
		if (listener != null) {
			listener.onCompletion(this);
		}
	}

	@HaxeMethodBody("track.play();")
	private native void play0();

	@Override
	public void play() {
		if (LimeAudio.isAudioDebug()) {
			System.out.println("Lime music play: " + name);
		}
		isPlaying = true;
		play0();
	}

	@HaxeMethodBody("track.pause();")
	private native void pause0();

	@Override
	public void pause() {
		if (LimeAudio.isAudioDebug()) {
			System.out.println("Lime music pause: " + name);
		}
		pause0();
		isPlaying = false;
	}

	@HaxeMethodBody("track.stop();")
	private native void stop0();

	@Override
	public void stop() {
		if (LimeAudio.isAudioDebug()) {
			System.out.println("Lime music stop: " + name);
		}
		stop0();
		isPlaying = false;
	}

	@Override
	public boolean isPlaying() {
		return isPlaying;
	}

	@Override
	@HaxeMethodBody("track.loops = p0 ? 100000 : 0;")
	public native void setLooping(boolean isLooping);

	@Override
	@HaxeMethodBody("return track.loops > 0;")
	public native boolean isLooping();

	@Override
	@HaxeMethodBody("track.gain = p0;")
	public native void setVolume(float volume);

	@Override
	@HaxeMethodBody("return track.gain;")
	public native float getVolume();

	@Override
	public void setPan(float pan, float volume) {
		// not implemented yet
	}

	@Override
	@HaxeMethodBody(
		"track.backend.completed = false;" +
			"track.currentTime = Std.int(p0 * 1000);"
	)
	public native void setPosition(float position);

	@Override
	@HaxeMethodBody("return track.currentTime / 1000;")
	public native float getPosition();

	@HaxeMethodBody("track.dispose();")
	private native void dispose0();

	@Override
	public void dispose() {
		dispose0();
	}

	@Override
	public void setOnCompletionListener(OnCompletionListener listener) {
		this.listener = listener;
	}
}
