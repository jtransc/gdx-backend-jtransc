package com.jtransc.media.limelibgdx;

import com.jtransc.annotation.haxe.HaxeAddMembers;
import com.jtransc.annotation.haxe.HaxeMethodBody;

import java.io.IOException;
import java.io.OutputStream;

@HaxeAddMembers(
	"var output: sys.io.FileOutput;"
)
public class LimeFileOutputStream extends OutputStream {
	@HaxeMethodBody(
		"output = p1 ? sys.io.File.append(N.i_str(p0)) : sys.io.File.write(N.i_str(p0));"
	)
	LimeFileOutputStream(String path, boolean append) {
	}

	@HaxeMethodBody(
		"output.writeByte(p0);"
	)
	@Override
	public void write(int i) throws IOException {
	}

	@HaxeMethodBody(
		"output.writeFullBytes(p0.data, p1, p2);"
	)
	@Override
	public void write(byte[] bytes, int offset, int length) throws IOException {
	}

	@HaxeMethodBody(
		"output.close();"
	)
	@Override
	public void close() {
	}
}
