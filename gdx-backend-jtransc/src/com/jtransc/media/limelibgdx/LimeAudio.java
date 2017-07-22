package com.jtransc.media.limelibgdx;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.AudioRecorder;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.jtransc.annotation.haxe.HaxeMethodBody;

public class LimeAudio implements Audio {

	@HaxeMethodBody(
		"{% if extra.debugAudio %}return {{ extra.debugAudio }};{% else %}return false;{% end %}"
	)
	static boolean isAudioDebug() {
		return false;
	}

	@Override
	public AudioDevice newAudioDevice(int i, boolean b) {
		return new AudioDevice() {
			@Override
			public boolean isMono() {
				return false;
			}

			@Override
			public void writeSamples(short[] samples, int offset, int numSamples) {

			}

			@Override
			public void writeSamples(float[] samples, int offset, int numSamples) {

			}

			@Override
			public int getLatency() {
				return 0;
			}

			@Override
			public void dispose() {

			}

			@Override
			public void setVolume(float volume) {

			}
		};
	}

	@Override
	public AudioRecorder newAudioRecorder(int i, boolean b) {
		return new AudioRecorder() {
			@Override
			public void read(short[] samples, int offset, int numSamples) {

			}

			@Override
			public void dispose() {

			}
		};
	}

	@Override
	public Sound newSound(final FileHandle fileHandle) {
		LimeSound sound = new LimeSound();
		sound.init(LimeFiles.fixpath(fileHandle.path()));
		return sound;
	}

	@Override
	public Music newMusic(final FileHandle fileHandle) {
		LimeMusic music = new LimeMusic();
		music.init(LimeFiles.fixpath(fileHandle.path()));
		return music;
	}
}