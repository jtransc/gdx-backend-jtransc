package com.jtransc.media.limelibgdx;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.jtransc.annotation.haxe.HaxeMethodBody;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class LimeFileHandle extends FileHandle {
	public LimeFileHandle(String fileName) {
		this(fileName, FileType.Internal);
	}

	LimeFileHandle(String fileName, FileType type) {
		super(fixPath(fileName, type), type);
	}

	private static String fixPath(String path, FileType type) {
		if (type == FileType.Internal) {
			while (path.startsWith("/")) {
				path = path.substring(1);
			}
			if (!path.startsWith(LimeFiles.internalPath)) {
				path = LimeFiles.internalPath + "/" + path;
			}
		} else if (type == FileType.Local) {
			if (!path.startsWith(LimeFiles.localPath)) {
				path = LimeFiles.localPath + "/" + path;
			}
		}

		path = path.replace("\\", "/").replace("//", "/");
		return path;
	}

	@Override
	public InputStream read() {
		if (type == FileType.Internal) {
			if (exists()) {
				byte[] data = internalRead(file.getPath());
				if (data != null) {
					return new ByteArrayInputStream(data);
				}
			}
			throw new GdxRuntimeException("File not found: " + file + " (" + type + ")");
		} else if (type == FileType.Local) {
			if (exists()) {
				try {
					return new LimeFileInputStream(file.getPath());
				} catch (Exception ex) {
					throw new GdxRuntimeException("Error reading file: " + file + " (" + type + ")", ex);
				}
			}
		}

		throw new GdxRuntimeException("Cannot read: " + file + " (" + type + ")");
	}

	@Override
	public OutputStream write(boolean append) {
		if (type == FileType.Local) {
			parent().mkdirs();
			try {
				return new LimeFileOutputStream(file.getPath(), append);
			} catch (Exception ex) {
				throw new GdxRuntimeException("Error writing file: " + file + " (" + type + ")", ex);
			}
		}
		throw new GdxRuntimeException("Cannot write to an " + type + " file: " + file);
	}

	@Override
	public Writer writer(boolean append, String charset) {
		OutputStream output = write(append);

		try {
			return charset == null ? new OutputStreamWriter(output) : new OutputStreamWriter(output, charset);
		} catch (UnsupportedEncodingException e) {
			throw new GdxRuntimeException(e);
		}
	}

	@Override
	public boolean exists() {
		if (type == FileType.Internal) {
			return internalExist(file.getPath());
		} else if (type == FileType.Classpath) {
			return false;
		}

		return exist(file.getPath());
	}

	@Override
	public void mkdirs() {
		if (type == FileType.Classpath || type == FileType.Internal) {
			throw new GdxRuntimeException("Cannot mkdirs with a " + type + " file: " + file);
		}

		mkdirs(file.getPath());
	}

	@Override
	public FileHandle parent() {
		String parent = file.getPath();

		int index = parent.lastIndexOf('/');
		if (index > 0) {
			parent = parent.substring(0, index + 1);
		}
		if (parent.length() == 0 && type == FileType.Absolute) {
			parent = "/";
		}

		return new LimeFileHandle(parent, type);
	}

	@Override
	public boolean delete() {
		if (type == FileType.Classpath || type == FileType.Internal) {
			throw new GdxRuntimeException("Cannot delete a " + type + " file: " + file);
		}
		try {
			delete(file.getPath());
			return true;
		} catch (Exception ignore) {
		}
		return false;
	}

	@Override
	public boolean deleteDirectory() {
		if (type == FileType.Classpath || type == FileType.Internal) {
			throw new GdxRuntimeException("Cannot delete a " + type + " file: " + file);
		}
		try {
			deleteDirectory(file.getPath());
			return true;
		} catch (Exception ignore) {
			return false;
		}
	}

	@Override
	public String readString() {
		InputStream is = null;

		try {
			is = read();
			int length = is.available();
			byte[] data = new byte[length];
			is.read(data);
			return new String(data);
		} catch (Exception e) {
			System.out.println("Cannot readString from file: " + file.getName() + " exception: " + e.toString());
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ignore) {
				}
			}
		}

	}

	@Override
	public void writeString(String str, boolean append) {
		OutputStream os = null;

		try {
			os = write(append);
			byte[] data = str.getBytes();
			os.write(data, 0, data.length);
		} catch (Exception e) {
			System.out.println("Cannot writeString to file: " + file.getName() + " exception: " + e.toString());
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException ignore) {
				}
			}
		}
	}

	@Override
	public long length() {
		if (!exists()) {
			return 0;
		}
		return fileSize(file.getPath());
	}

	@HaxeMethodBody("sys.FileSystem.deleteFile(N.i_str(p0));")
	private native void delete(String path);

	@HaxeMethodBody("sys.FileSystem.deleteDirectory(N.i_str(p0));")
	private native void deleteDirectory(String path);

	@HaxeMethodBody("return sys.FileSystem.exists(N.i_str(p0));")
	private native boolean exist(String path);

	@HaxeMethodBody("return lime.Assets.exists(N.i_str(p0));")
	private native boolean internalExist(String path);

	@HaxeMethodBody("return JA_B.fromBytes(lime.Assets.getBytes(N.i_str(p0)));")
	private native byte[] internalRead(String path);

	@HaxeMethodBody("sys.FileSystem.createDirectory(N.i_str(p0));")
	private native void mkdirs(String path);

	@HaxeMethodBody("return sys.FileSystem.stat(N.i_str(p0)).size;")
	private native int fileSize(String path);
}
