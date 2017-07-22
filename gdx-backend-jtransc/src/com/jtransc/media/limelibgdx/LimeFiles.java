package com.jtransc.media.limelibgdx;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.jtransc.annotation.JTranscMethodBody;
import com.jtransc.annotation.haxe.HaxeMethodBody;
import com.jtransc.io.JTranscSyncIO;

import java.io.FileNotFoundException;

public class LimeFiles implements Files {

	static final String internalPath = "assets";
	static String localPath;

	@HaxeMethodBody(
		"{% if extra.useLimeFileHandle %}return {{ extra.useLimeFileHandle }};{% else %}return false;{% end %}"
	)
	private static boolean useLimeFileHandle() {
		return false;
	}

	@HaxeMethodBody(
		"{% if extra.debugLimeFiles %}return {{ extra.debugLimeFiles }};{% else %}return false;{% end %}"
	)
	private static boolean isLimeFilesDebug() {
		return false;
	}

	LimeFiles() {
		JTranscSyncIO.impl = new JTranscSyncIOLimeImpl(JTranscSyncIO.impl);
		localPath = getLocalPath();
		localPath = localPath.replace("\\", "/").replace("//", "/");
		if (isLimeFilesDebug()) {
			System.out.println("localPath=" + localPath);
		}
	}

	@HaxeMethodBody("return N.str(lime.system.System.applicationStorageDirectory);")
	private String getLocalPath() {
		return "/";
	}

	@Override
	public FileHandle getFileHandle(String s, FileType fileType) {
		return new LimeFileHandle(s);
	}

	@Override
	public FileHandle classpath(String s) {
		if (useLimeFileHandle()) {
			return new LimeFileHandle(s);
		}
		return new FileHandle(s);
	}

	@Override
	public FileHandle internal(String s) {
		if (useLimeFileHandle()) {
			return new LimeFileHandle(s);
		}
		return new FileHandle(s);
	}

	@Override
	public FileHandle external(String s) {
		if (useLimeFileHandle()) {
			return new LimeFileHandle(s);
		}
		return new FileHandle(s);
	}

	@Override
	public FileHandle absolute(String s) {
		if (useLimeFileHandle()) {
			return new LimeFileHandle(s);
		}
		return new FileHandle(s);
	}

	@Override
	public FileHandle local(String s) {
		if (useLimeFileHandle()) {
			return new LimeFileHandle(s, FileType.Local);
		}
		return new FileHandle(s);
	}

	@Override
	public String getExternalStoragePath() {
		return "/";
	}

	@Override
	public boolean isExternalStorageAvailable() {
		return false;
	}

	@Override
	public String getLocalStoragePath() {
		return useLimeFileHandle() ? localPath : "/";
	}

	@Override
	public boolean isLocalStorageAvailable() {
		return useLimeFileHandle();
	}

	static public String fixpath(String path) {
		while (path.startsWith("/")) path = path.substring(1);
		if (!path.startsWith("assets") && !path.startsWith("/assets")) {
			path = "assets/" + path;
		}
		path = path.replace("\\", "/").replace("//", "/");
		return path;
	}

	static private class JTranscSyncIOLimeImpl extends JTranscSyncIO.Impl {
		JTranscSyncIOLimeImpl(JTranscSyncIO.Impl parent) {
			super(parent);
		}

		@Override
		public JTranscSyncIO.ImplStream open(String path, int mode) {
			String pathFixed = fixpath(path);
			if (isLimeFilesDebug()) {
				System.out.println("JTranscSyncIOLimeImpl.open: " + pathFixed + " || " + path + " in mode " + mode);
			}
			if (mode == JTranscSyncIO.O_RDWR) {
				try {
					return super.open(path, mode);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			byte[] bytes = readBytes(pathFixed, mode);
			if (bytes == null) {
				System.out.println("Can't find: " + pathFixed);
				throw new RuntimeException(new FileNotFoundException(path));
			}
			return new JTranscSyncIO.ByteStream(bytes);
		}

		@Override
		public String getCwd() {
			return "";
		}

		@Override
		public int getBooleanAttributes(String path) {
			String pathFixed = fixpath(path);
			if (isLimeFilesDebug()) {
				System.out.println("JTranscSyncIOLimeImpl.getBooleanAttributes: " + pathFixed + " || " + path);
			}
			int result = 0;
			result |= BA_REGULAR;
			if (exists(pathFixed)) result |= BA_EXISTS;
			return result;
		}

		private static final int BA_EXISTS = 0x01;
		private static final int BA_REGULAR = 0x02;
		private static final int BA_DIRECTORY = 0x04;
		private static final int BA_HIDDEN = 0x08;

		@HaxeMethodBody("return lime.Assets.exists(N.i_str(p0));")
		@JTranscMethodBody(target = "js", value = "return libgdx.io.exists(N.istr(p0));")
		native private boolean exists(String path);

		@HaxeMethodBody("return JA_B.fromBytes(lime.Assets.getBytes(N.i_str(p0)));")
		@JTranscMethodBody(target = "js", value = "return libgdx.io.readBytes(N.istr(p0));")
		native private byte[] readBytes(String path, int mode);
	}
}
