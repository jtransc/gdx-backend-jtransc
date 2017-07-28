package com.jtransc.media.limelibgdx;

import com.badlogic.gdx.Application;
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
	private boolean isReady = false;

	private boolean enableStreams = LimeDevice.getType() != Application.ApplicationType.iOS;

	// Stream works with .ogg files only. There are some troubles on iOS.
	@HaxeMethodBody("var vorbis = lime.media.codecs.vorbis.VorbisFile.fromFile(N.i_str(p0));" +
		"track = new lime.media.AudioSource(lime.media.AudioBuffer.fromVorbisFile(vorbis)); " +
		"return track.buffer != null;")
	private native boolean initStream(String path);

	// Load BINARY resources as bytes. Works on iOS.
	@HaxeMethodBody("track = new lime.media.AudioSource(lime.media.AudioBuffer.fromBytes(lime.Assets.getBytes(N.i_str(p0)))); " +
		"return track.buffer != null;")
	private native boolean initBytes(String path);

	@HaxeMethodBody("track.onComplete.add(function():Void { this.{% METHOD com.jtransc.media.limelibgdx.LimeMusic:onComplete %}();});")
	private native void setCallback();

	void init(String path) {
		name = path;
		if (enableStreams) {
			isReady = initStream(path);
			if (LimeAudio.isAudioDebug()) {
				System.out.println("Lime music loaded as stream: " + name + (isReady ? " successful" : " failed"));
			}
		}
		if (!isReady) {
			isReady = initBytes(path);
			if (LimeAudio.isAudioDebug()) {
				System.out.println("Lime music loaded as bytes: " + name + (isReady ? " successful" : " failed"));
			}
		}
		if (isReady) {
			setCallback();
		}
	}

	@SuppressWarnings("unused")
	void onComplete() {
		if (LimeAudio.isAudioDebug()) {
			System.out.println("Lime music onComplete: " + name);
		}
		if (!isReady) return;

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
		if (!isReady) return;
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
		if (!isReady) return;
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
		if (!isReady) return;
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
	@HaxeMethodBody("if (track.buffer == null) return;" +
		"var vol = p0 < 0 ? 0 : p0 > 1.0 ? 1.0 : p0; track.gain = vol;")
	public native void setVolume(float volume);

	@Override
	@HaxeMethodBody("if (track.buffer == null) return 0; +" +
		"return track.gain;")
	public native float getVolume();

	@Override
	public void setPan(float pan, float volume) {
		// not implemented yet
	}

	@Override
	@HaxeMethodBody(
		"if (track.buffer == null) return;" +
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
		if (!isReady) return;
		dispose0();
	}

	@Override
	public void setOnCompletionListener(OnCompletionListener listener) {
		this.listener = listener;
	}
}
