/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.graphics.g2d.freetype;

import java.nio.ByteBuffer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.LongMap;
import com.jtransc.annotation.haxe.HaxeAddMembers;
import com.jtransc.annotation.haxe.HaxeImports;
import com.jtransc.annotation.haxe.HaxeMeta;
import com.jtransc.annotation.haxe.HaxeMethodBody;

public class FreeType {
	static int lastError = 0;

	static int getLastErrorCode() {
		return lastError;
	}

	@HaxeImports(
		"import lime.text.Font;" +
			"import lime.text.Glyph;" +
			"import lime._backend.native.NativeCFFI;" +
			"import lime.text.unifill.CodePoint;" +
			"import lime.text.unifill.Utf8;"
	)
	@HaxeMeta(
		"@:access(lime._backend.native.NativeCFFI)"
	)
	@HaxeAddMembers(
		"public var font: Font;" +
			"public var metrics: NativeFontData;" +
			"public var glyph: Glyph;" +
			"public var pixelHeight: Int;"
	)
	public static class Face {

		@HaxeMethodBody(
			"font = Font.fromBytes(p0.{% FIELD java.nio.ByteBuffer:backingArray %}.data);" +
				"metrics = NativeCFFI.lime_font_outline_decompose (font.src, 0);"
		)
		private Face(ByteBuffer buffer) {
		}

		public static Face getFace(ByteBuffer buffer) {
			return new Face(buffer);
		}

		@HaxeMethodBody(
			"var flags: Int = 0;" +
				"if(metrics.has_kerning) flags |= {% SFIELD com.badlogic.gdx.graphics.g2d.freetype.FreeType:FT_FACE_FLAG_KERNING %};" +
				"if(metrics.is_fixed_width) flags |= {% SFIELD com.badlogic.gdx.graphics.g2d.freetype.FreeType:FT_FACE_FLAG_FIXED_WIDTH %};" +
				"if(metrics.has_glyph_names) flags |= {% SFIELD com.badlogic.gdx.graphics.g2d.freetype.FreeType:FT_FACE_FLAG_GLYPH_NAMES %};" +
				"return flags;"
		)
		public int getFaceFlags() {
			throw new RuntimeException("FreeType.Face.getFaceFlags(): Not implemented.");
		}

		@HaxeMethodBody(
			"var flags: Int = 0;" +
				"if(metrics.is_italic) flags |= {% SFIELD com.badlogic.gdx.graphics.g2d.freetype.FreeType:FT_STYLE_FLAG_ITALIC %};" +
				"if(metrics.is_bold) flags |= {% SFIELD com.badlogic.gdx.graphics.g2d.freetype.FreeType:FT_STYLE_FLAG_BOLD %};" +
				"return flags;"
		)
		public int getStyleFlags() {
			throw new RuntimeException("FreeType.Face.getStyleFlags(): Not implemented.");
		}

		@HaxeMethodBody(
			"return font.numGlyphs;"
		)
		public int getNumGlyphs() {
			throw new RuntimeException("FreeType.Face.getNumGlyphs(): Not implemented.");
		}

		@HaxeMethodBody(
			"return font.ascender;"
		)
		public int getAscender() {
			throw new RuntimeException("FreeType.Face.getAscender(): Not implemented.");
		}

		@HaxeMethodBody(
			"return font.descender;"
		)
		public int getDescender() {
			throw new RuntimeException("FreeType.Face.getDescender(): Not implemented.");
		}

		@HaxeMethodBody(
			"return font.height;"
		)
		public int getHeight() {
			throw new RuntimeException("FreeType.Face.getHeight(): Not implemented.");
		}

		@HaxeMethodBody(
			"var maxAdvance: Int = 0;" +
				"for(glyph in metrics.glyphs)" +
				"	if(glyph.advance > maxAdvance) " +
				"		maxAdvance = glyph.advance;" +
				"return maxAdvance;"
		)
		public int getMaxAdvanceWidth() {
			throw new RuntimeException("FreeType.Face.getMaxAdvanceWidth(): Not implemented.");
		}

		@HaxeMethodBody(
			"return font.height;"
		)
		public int getMaxAdvanceHeight() {
			throw new RuntimeException("FreeType.Face.getMaxAdvanceHeight(): Not implemented.");
		}

		@HaxeMethodBody(
			"return font.underlinePosition;"
		)
		public int getUnderlinePosition() {
			throw new RuntimeException("FreeType.Face.getUnderlinePosition(): Not implemented.");
		}

		@HaxeMethodBody(
			"return font.underlineThickness;"
		)
		public int getUnderlineThickness() {
			throw new RuntimeException("FreeType.Face.getUnderlineThickness(): Not implemented.");
		}

		public boolean selectSize(int strikeIndex) {
			throw new RuntimeException("FreeType.Face.selectSize(): Not implemented.");
		}

		public boolean setCharSize(int charWidth, int charHeight, int horzResolution, int vertResolution) {
			throw new RuntimeException("FreeType.Face.setCharSize(): Not implemented.");
		}

		@HaxeMethodBody(
			"pixelHeight = p1;" +
				"metrics = NativeCFFI.lime_font_outline_decompose (font.src, pixelHeight * 64);" +
				"return true;"
		)
		public boolean setPixelSizes(int pixelWidth, int pixelHeight) {
			throw new RuntimeException("FreeType.Face.setPixelSizes(): Not implemented.");
		}

		public boolean loadGlyph(int glyphIndex, int loadFlags) {
			throw new RuntimeException("FreeType.Face.loadGlyph(): Not implemented.");
		}

		@HaxeMethodBody(
			"var str: String = CodePoint.fromInt(p0).toString();" +
				"glyph = -1;" +
				"if(str != null)" +
				"	glyph = font.getGlyph(str);" +
				"else" +
				"	Sys.println(\"Unsupport character: \"+p0);" +
				"return glyph != -1;"
		)
		public boolean loadChar(int charCode, int loadFlags) {
			throw new RuntimeException("FreeType.Face.loadChar(): Not implemented.");
		}

		public GlyphSlot getGlyph() {
			return new GlyphSlot(this);
		}

		public Size getSize() {
			return new Size(this);
		}

		@HaxeMethodBody(
			"return metrics.has_kerning;"
		)
		public boolean hasKerning() {
			throw new RuntimeException("FreeType.Face.hasKerning(): Not implemented.");
		}

		@HaxeMethodBody(
			"for(kerning in metrics.kerning)" +
				"	if(kerning.left_glyph == p0 && kerning.right_glyph == p1) {" +
				"		return kerning.x;" +
				"	}" +
				"return 0;"
		)
		public int getKerning(int leftGlyph, int rightGlyph, int kernMode) {
			throw new RuntimeException("FreeType.Face.getKerning(): Not implemented.");
		}

		@HaxeMethodBody(
			"var index: Int = 0;" +
				"for(glyph in metrics.glyphs) {" +
				"	if(glyph.char_code == p0) {" +
				"		return index;" +
				"   }" +
				"	index++;" +
				"}" +
				"return 0;"
		)
		public int getCharIndex(int charCode) {
			throw new RuntimeException("FreeType.Face.getCharIndex(): Not implemented.");
		}
	}

	public static class Size {
		private Face face;

		Size(Face face) {
			this.face = face;
		}

		public SizeMetrics getMetrics() {
			return new SizeMetrics(face);
		}
	}

	@HaxeImports(
		"import lime.text.Font;" +
			"import lime.text.unifill.CodePoint;"
	)
	@HaxeAddMembers(
		"public var metrics: NativeFontData;" +
			"public var height: Int;" +
			"public var ascend: Int;"
	)
	public static class SizeMetrics {
		private Face face;

		SizeMetrics(Face face) {
			this.face = face;
			init(face);
		}

		@HaxeMethodBody(
			"metrics = p0.metrics;" +

				"var str: String = CodePoint.fromInt(71).toString();" +
				"var glyph = p0.font.getGlyph(str);" +
				"height = Std.int(p0.font.getGlyphMetrics(glyph).advance.y);" +
				"var coef = height / (metrics.height * 1.0);" +
				"ascend = Std.int(metrics.ascend * coef);"
		)
		private void init(Face face) {
		}

		@HaxeMethodBody(
			"return metrics.em_size;"
		)
		public int getXppem() {
			throw new RuntimeException("FreeType.SizeMetrics.getXppem(): Not implemented.");
		}

		@HaxeMethodBody(
			"return metrics.em_size;"
		)
		public int getYppem() {
			throw new RuntimeException("FreeType.SizeMetrics.getYppem(): Not implemented.");
		}

		public int getXScale() {
			throw new RuntimeException("FreeType.SizeMetrics.getXScale(): Not implemented.");
		}

		public int getYscale() {
			throw new RuntimeException("FreeType.SizeMetrics.getYscale(): Not implemented.");
		}

		@HaxeMethodBody(
			"return ascend;"
		)
		public int getAscender() {
			throw new RuntimeException("FreeType.SizeMetrics.getAscender(): Not implemented.");
		}

		@HaxeMethodBody(
			"return ascend - height;"
		)
		public int getDescender() {
			throw new RuntimeException("FreeType.SizeMetrics.getAscender(): Not implemented.");
		}

		@HaxeMethodBody(
			"return height;"
		)
		public int getHeight() {
			throw new RuntimeException("FreeType.SizeMetrics.getHeight(): Not implemented.");
		}

		public int getMaxAdvance() {
			return face.getMaxAdvanceWidth();
		}
	}

	public static class GlyphSlot {
		private Face face;
		private GlyphMetrics metrics;

		public GlyphSlot(Face face) {
			this.face = face;
		}

		public GlyphMetrics getMetrics() {
			if (metrics == null) {
				metrics = new GlyphMetrics(face);
			}
			return metrics;
		}

		public int getLinearHoriAdvance() {
			throw new RuntimeException("FreeType.GlyphSlot.getLinearHoriAdvance(): Not implemented.");
		}

		public int getLinearVertAdvance() {
			throw new RuntimeException("FreeType.GlyphSlot.getLinearVertAdvance(): Not implemented.");
		}

		public int getAdvanceX() {
			throw new RuntimeException("FreeType.GlyphSlot.getAdvanceX(): Not implemented.");
		}

		public int getAdvanceY() {
			throw new RuntimeException("FreeType.GlyphSlot.getAdvanceY(): Not implemented.");
		}

		public int getFormat() {
			throw new RuntimeException("FreeType.GlyphSlot.getFormat(): Not implemented.");
		}

		public Bitmap getBitmap() {
			return new Bitmap(face);
		}

		public int getBitmapLeft() {
			throw new RuntimeException("FreeType.GlyphSlot.getBitmapLeft(): Not implemented.");
		}

		public int getBitmapTop() {
			throw new RuntimeException("FreeType.GlyphSlot.getBitmapTop(): Not implemented.");
		}

		public boolean renderGlyph(int renderMode) {
			return true;
		}

		public Glyph getGlyph() {
			return new Glyph(this);
		}
	}

	public static class Glyph {
		private boolean rendered;
		private GlyphSlot slot;
		private Bitmap bitmap;

		Glyph(GlyphSlot slot) {
			this.slot = slot;
		}

		public void strokeBorder(Stroker stroker, boolean inside) {
		}

		public void toBitmap(int renderMode) {
			if (bitmap == null) {
				bitmap = slot.getBitmap();
			}
			rendered = true;
		}

		public Bitmap getBitmap() {
			if (bitmap == null) {
				bitmap = slot.getBitmap();
			}
			return bitmap;
		}

		public int getLeft() {
			return toInt(slot.getMetrics().getHoriBearingX());
		}

		public int getTop() {
			return toInt(slot.getMetrics().getHoriBearingY());
		}
	}

	@HaxeImports(
		"import lime.graphics.Image;"
	)
	@HaxeAddMembers(
		"var image: Image;"
	)
	public static class Bitmap {
		private static Face face;

		Bitmap(Face face) {
			this.face = face;
			init(face);
		}

		@HaxeMethodBody(
			"image = p0.font.renderGlyph(p0.glyph, p0.pixelHeight);"
		)
		private void init(Face face) {
		}

		@HaxeMethodBody(
			"return image != null ? image.height : 1;"
		)
		public int getRows() {
			throw new RuntimeException("FreeType.Bitmap.getRows(): Not implemented.");
		}

		@HaxeMethodBody(
			"return image != null ? image.width : 10;"
		)
		public int getWidth() {
			throw new RuntimeException("FreeType.Bitmap.getWidth(): Not implemented.");
		}

		@HaxeMethodBody(
			"return image != null ? (image.buffer.width * image.buffer.bitsPerPixel) : 10;"
		)
		public int getPitch() {
			throw new RuntimeException("FreeType.Bitmap.getPitch(): Not implemented.");
		}

		public ByteBuffer getBuffer() {
			return ByteBuffer.wrap(getData());
		}

		@HaxeMethodBody(
			"if(image == null) " +
				" 	return new JA_B(10);" +
				"var bytes = image.data.toBytes();" +
				"return new JA_B(bytes.length, bytes);"
		)
		private byte[] getData() {
			return new byte[0];
		}

		public Pixmap getPixmap(Format format, Color color, float gamma) {
			Pixmap pixmap = new Pixmap(getWidth(), getRows(), Format.RGBA8888);
			ByteBuffer buffer = pixmap.getPixels();
			byte[] src = getData();
			byte[] dest = new byte[src.length * 4];

			byte r = (byte) (color.r * 0xFF);
			byte g = (byte) (color.g * 0xFF);
			byte b = (byte) (color.b * 0xFF);
			int a = ((int) (color.a * 0xFF)) & 0xFF;

			for (int i = 0; i < dest.length; ) {
				// Use the specified color for RGB, blend the FreeType bitmap with alpha.
				float alpha = (src[i >> 2] & 0xff) / 255f;
				alpha = (float) Math.pow(alpha, gamma); // Inverse gamma.
				dest[i++] = r;
				dest[i++] = g;
				dest[i++] = b;
				dest[i++] = (byte) (a * alpha);
			}

			BufferUtils.copy(dest, 0, buffer, dest.length);
			return pixmap;
		}

		@HaxeMethodBody(
			"return 0;"
		)
		public int getNumGray() {
			throw new RuntimeException("FreeType.Bitmap.getNumGray(): Not implemented.");
		}

		@HaxeMethodBody(
			"return 0;"
		)
		public int getPixelMode() {
			throw new RuntimeException("FreeType.Bitmap.getPixelMode(): Not implemented.");
		}
	}

	@HaxeImports(
		"import lime.text.GlyphMetrics;"
	)
	@HaxeAddMembers(
		"public var metrics: GlyphMetrics;"
	)
	public static class GlyphMetrics {
		private Face face;

		GlyphMetrics(Face face) {
			this.face = face;
			init(face);
		}

		@HaxeMethodBody(
			"metrics = p0.font.getGlyphMetrics(p0.glyph);"
		)
		private void init(Face face) {
		}

		@HaxeMethodBody(
			"return 0;"
		)
		public int getWidth() {
			throw new RuntimeException("FreeType.GlyphMetrics.getWidth(): Not implemented.");
		}

		@HaxeMethodBody(
			"return metrics.height;"
		)
		public int getHeight() {
			throw new RuntimeException("FreeType.GlyphMetrics.getHeight(): Not implemented.");
		}

		@HaxeMethodBody(
			"return Std.int(metrics.horizontalBearing.x);"
		)
		public int getHoriBearingX() {
			throw new RuntimeException("FreeType.GlyphMetrics.getHoriBearingX(): Not implemented.");
		}

		@HaxeMethodBody(
			"return Std.int(metrics.horizontalBearing.y);"
		)
		public int getHoriBearingY() {
			throw new RuntimeException("FreeType.GlyphMetrics.getHoriBearingY(): Not implemented.");
		}

		@HaxeMethodBody(
			"return Std.int(metrics.advance.x);"
		)
		public int getHoriAdvance() {
			throw new RuntimeException("FreeType.GlyphMetrics.getHoriAdvance(): Not implemented.");
		}

		@HaxeMethodBody(
			"return Std.int(metrics.advance.y);"
		)
		public int getVertAdvance() {
			throw new RuntimeException("FreeType.GlyphMetrics.getVertAdvance(): Not implemented.");
		}

		@HaxeMethodBody(
			"return Std.int(metrics.verticalBearing.x);"
		)
		public int getVertBearingX() {
			throw new RuntimeException("FreeType.GlyphMetrics.getVertBearingX(): Not implemented.");
		}

		@HaxeMethodBody(
			"return Std.int(metrics.verticalBearing.y);"
		)
		public int getVertBearingY() {
			throw new RuntimeException("FreeType.GlyphMetrics.getVertBearingY(): Not implemented.");
		}
	}

	public static class Stroker {
		Stroker() {
		}

		public void set(int radius, int lineCap, int lineJoin, int miterLimit) {
		}
	}

	public static Stroker createStroker() {
		return new Stroker();
	}

	public static Library initFreeType() {
		return new Library();
	}

	public static class Library {
		LongMap<ByteBuffer> fontData = new LongMap<ByteBuffer>();

		Library() {
		}

		public Face newFace(FileHandle font, int faceIndex) {
			byte[] data = font.readBytes();
			return newMemoryFace(data, data.length, faceIndex);
		}

		public Face newMemoryFace(byte[] data, int dataSize, int faceIndex) {
			ByteBuffer buffer = BufferUtils.newUnsafeByteBuffer(data.length);
			BufferUtils.copy(data, 0, buffer, data.length);
			return newMemoryFace(buffer, faceIndex);
		}

		public Face newMemoryFace(ByteBuffer buffer, int faceIndex) {
			return Face.getFace(buffer);
		}

		public Stroker createStroker() {
			return new Stroker();
		}
	}


	public static int FT_PIXEL_MODE_NONE = 0;
	public static int FT_PIXEL_MODE_MONO = 1;
	public static int FT_PIXEL_MODE_GRAY = 2;
	public static int FT_PIXEL_MODE_GRAY2 = 3;
	public static int FT_PIXEL_MODE_GRAY4 = 4;
	public static int FT_PIXEL_MODE_LCD = 5;
	public static int FT_PIXEL_MODE_LCD_V = 6;

	private static int encode(char a, char b, char c, char d) {
		return (a << 24) | (b << 16) | (c << 8) | d;
	}

	public static int FT_ENCODING_NONE = 0;
	public static int FT_ENCODING_MS_SYMBOL = encode('s', 'y', 'm', 'b');
	public static int FT_ENCODING_UNICODE = encode('u', 'n', 'i', 'c');
	public static int FT_ENCODING_SJIS = encode('s', 'j', 'i', 's');
	public static int FT_ENCODING_GB2312 = encode('g', 'b', ' ', ' ');
	public static int FT_ENCODING_BIG5 = encode('b', 'i', 'g', '5');
	public static int FT_ENCODING_WANSUNG = encode('w', 'a', 'n', 's');
	public static int FT_ENCODING_JOHAB = encode('j', 'o', 'h', 'a');
	public static int FT_ENCODING_ADOBE_STANDARD = encode('A', 'D', 'O', 'B');
	public static int FT_ENCODING_ADOBE_EXPERT = encode('A', 'D', 'B', 'E');
	public static int FT_ENCODING_ADOBE_CUSTOM = encode('A', 'D', 'B', 'C');
	public static int FT_ENCODING_ADOBE_LATIN_1 = encode('l', 'a', 't', '1');
	public static int FT_ENCODING_OLD_LATIN_2 = encode('l', 'a', 't', '2');
	public static int FT_ENCODING_APPLE_ROMAN = encode('a', 'r', 'm', 'n');

	public static int FT_FACE_FLAG_SCALABLE = 1;
	public static int FT_FACE_FLAG_FIXED_SIZES = (1 << 1);
	public static int FT_FACE_FLAG_FIXED_WIDTH = (1 << 2);
	public static int FT_FACE_FLAG_SFNT = (1 << 3);
	public static int FT_FACE_FLAG_HORIZONTAL = (1 << 4);
	public static int FT_FACE_FLAG_VERTICAL = (1 << 5);
	public static int FT_FACE_FLAG_KERNING = (1 << 6);
	public static int FT_FACE_FLAG_FAST_GLYPHS = (1 << 7);
	public static int FT_FACE_FLAG_MULTIPLE_MASTERS = (1 << 8);
	public static int FT_FACE_FLAG_GLYPH_NAMES = (1 << 9);
	public static int FT_FACE_FLAG_EXTERNAL_STREAM = (1 << 10);
	public static int FT_FACE_FLAG_HINTER = (1 << 11);
	public static int FT_FACE_FLAG_CID_KEYED = (1 << 12);
	public static int FT_FACE_FLAG_TRICKY = (1 << 13);

	public static int FT_STYLE_FLAG_ITALIC = 1;
	public static int FT_STYLE_FLAG_BOLD = (1 << 1);

	public static int FT_LOAD_DEFAULT = 0x0;
	public static int FT_LOAD_NO_SCALE = 0x1;
	public static int FT_LOAD_NO_HINTING = 0x2;
	public static int FT_LOAD_RENDER = 0x4;
	public static int FT_LOAD_NO_BITMAP = 0x8;
	public static int FT_LOAD_VERTICAL_LAYOUT = 0x10;
	public static int FT_LOAD_FORCE_AUTOHINT = 0x20;
	public static int FT_LOAD_CROP_BITMAP = 0x40;
	public static int FT_LOAD_PEDANTIC = 0x80;
	public static int FT_LOAD_IGNORE_GLOBAL_ADVANCE_WIDTH = 0x200;
	public static int FT_LOAD_NO_RECURSE = 0x400;
	public static int FT_LOAD_IGNORE_TRANSFORM = 0x800;
	public static int FT_LOAD_MONOCHROME = 0x1000;
	public static int FT_LOAD_LINEAR_DESIGN = 0x2000;
	public static int FT_LOAD_NO_AUTOHINT = 0x8000;

	public static int FT_LOAD_TARGET_NORMAL = 0x0;
	public static int FT_LOAD_TARGET_LIGHT = 0x10000;
	public static int FT_LOAD_TARGET_MONO = 0x20000;
	public static int FT_LOAD_TARGET_LCD = 0x30000;
	public static int FT_LOAD_TARGET_LCD_V = 0x40000;

	public static int FT_RENDER_MODE_NORMAL = 0;
	public static int FT_RENDER_MODE_LIGHT = 1;
	public static int FT_RENDER_MODE_MONO = 2;
	public static int FT_RENDER_MODE_LCD = 3;
	public static int FT_RENDER_MODE_LCD_V = 4;
	public static int FT_RENDER_MODE_MAX = 5;

	public static int FT_KERNING_DEFAULT = 0;
	public static int FT_KERNING_UNFITTED = 1;
	public static int FT_KERNING_UNSCALED = 2;

	public static int FT_STROKER_LINECAP_BUTT = 0;
	public static int FT_STROKER_LINECAP_ROUND = 1;
	public static int FT_STROKER_LINECAP_SQUARE = 2;

	public static int FT_STROKER_LINEJOIN_ROUND = 0;
	public static int FT_STROKER_LINEJOIN_BEVEL = 1;
	public static int FT_STROKER_LINEJOIN_MITER_VARIABLE = 2;
	public static int FT_STROKER_LINEJOIN_MITER = FT_STROKER_LINEJOIN_MITER_VARIABLE;
	public static int FT_STROKER_LINEJOIN_MITER_FIXED = 3;

	public static int toInt(int value) {
		return ((value + 63) & -64) >> 6;
	}
}
