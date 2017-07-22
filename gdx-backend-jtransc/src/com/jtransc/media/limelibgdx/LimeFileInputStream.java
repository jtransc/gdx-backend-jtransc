package com.jtransc.media.limelibgdx;

import com.jtransc.annotation.haxe.HaxeAddMembers;
import com.jtransc.annotation.haxe.HaxeMethodBody;

import java.io.IOException;
import java.io.InputStream;

@HaxeAddMembers(
	"var input: sys.io.FileInput;" +
		"var stat: sys.FileStat;"
)
public class LimeFileInputStream extends InputStream {
	@HaxeMethodBody(
		"stat = sys.FileSystem.stat(N.i_str(p0));" +
			"input = sys.io.File.read(N.i_str(p0));"
	)
	LimeFileInputStream(String path) {
	}

	@HaxeMethodBody(
		"try { return input.readByte(); } catch (eof: haxe.io.Eof) { return -1; }"
	)
	@Override
	public int read() throws IOException {
		return 0;
	}

	@HaxeMethodBody(
		"try { return input.readBytes(p0.data, p1, p2); } catch (eof: haxe.io.Eof) { return -1; }"
	)
	@Override
	public int read(byte[] bytes, int offset, int length) throws IOException {
		return 0;
	}

	@HaxeMethodBody(
		"return stat.size;"
	)
	@Override
	public int available() throws IOException {
		return 0;
	}

	@HaxeMethodBody(
		"input.close();"
	)
	@Override
	public void close() {
	}
}