/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.jtransc.JTranscArrays;
import com.jtransc.JTranscSystem;
import com.jtransc.annotation.JTranscMethodBody;
import com.jtransc.annotation.haxe.HaxeMethodBody;
import com.jtransc.media.limelibgdx.LimeFiles;
import com.jtransc.media.limelibgdx.imaging.ImageDecoder;
import com.jtransc.media.limelibgdx.util.ColorFormat8;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pixmap implements Disposable {
	public static Map<Integer, Pixmap> pixmaps = new HashMap<Integer, Pixmap>();
	static int nextId = 0;

	/**
	 * Different pixel formats.
	 *
	 * @author mzechner
	 */
	public enum Format {
		Alpha(1, GL20.GL_ALPHA, GL20.GL_UNSIGNED_BYTE, Gdx2DPixmap.GDX2D_FORMAT_ALPHA),
		Intensity(1, GL20.GL_ALPHA, GL20.GL_UNSIGNED_BYTE, Gdx2DPixmap.GDX2D_FORMAT_ALPHA),
		LuminanceAlpha(2, GL20.GL_LUMINANCE_ALPHA, GL20.GL_UNSIGNED_BYTE, Gdx2DPixmap.GDX2D_FORMAT_LUMINANCE_ALPHA),
		RGB565(2, GL20.GL_RGB, GL20.GL_UNSIGNED_SHORT_5_6_5, Gdx2DPixmap.GDX2D_FORMAT_RGB565),
		RGBA4444(2, GL20.GL_RGBA, GL20.GL_UNSIGNED_SHORT_4_4_4_4, Gdx2DPixmap.GDX2D_FORMAT_RGBA4444),
		RGB888(3, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE, Gdx2DPixmap.GDX2D_FORMAT_RGB888),
		RGBA8888(4, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, Gdx2DPixmap.GDX2D_FORMAT_RGBA8888);

		private int bytesPerPixel;
		private int glFormat;
		private int glType;
		private int gdx2dPixmapFormat;

		Format(int bytesPerPixel, int glFormat, int glType, int gdx2dPixmapFormat) {
			this.bytesPerPixel = bytesPerPixel;
			this.glFormat = glFormat;
			this.glType = glType;
			this.gdx2dPixmapFormat = gdx2dPixmapFormat;
		}

		public int getBytesPerPixel() {
			return bytesPerPixel;
		}

		public int getGlFormat() {
			return glFormat;
		}

		public int getGlType() {
			return glType;
		}

		public static int toGdx2DPixmapFormat(Format format) {
			return format.gdx2dPixmapFormat;
		}

		public static Format fromGdx2DPixmapFormat(int gdx2dPixmapFormat) {
			for (Format format : Format.values()) {
				if (format.gdx2dPixmapFormat == gdx2dPixmapFormat) {
					return format;
				}
			}

			throw new GdxRuntimeException("Unknown Gdx2DPixmap Format: " + gdx2dPixmapFormat);
		}

		public static int toGlFormat(Format format) {
			return format.getGlFormat();
		}

		public static int toGlType(Format format) {
			return format.getGlType();
		}
	}

	/**
	 * Blending functions to be set with {@link Pixmap#setBlending}.
	 *
	 * @author mzechner
	 */
	public enum Blending {
		None, SourceOver
	}

	/**
	 * Filters to be used with {@link Pixmap#drawPixmap(Pixmap, int, int, int, int, int, int, int, int)}.
	 *
	 * @author mzechner
	 */
	public enum Filter {
		NearestNeighbour, BiLinear
	}

	int width;
	int height;
	int actualWidth;
	int actualHeight;
	Format format = Format.RGBA8888;
	int id;
	//IntBuffer buffer;
	int[] data;
	byte[] byteData;
	int color;
	private Blending blending;
	private boolean pixelsAvailable = true;

	public Pixmap(FileHandle file) {
		this(file, true);
	}

	public Pixmap(FileHandle file, boolean pixelPerfect) {
		try {
			loadImage((file.file().isAbsolute()) ? file.file().getPath() : LimeFiles.fixpath(file.file().getPath()), pixelPerfect);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Pixmap(int width, int height, Format format) {
		create(width, height, format);
	}

	public Pixmap(byte[] encodedData, int offset, int len) {
		loadImage(encodedData, offset, len);
	}

	@JTranscMethodBody(target = "js", value = {
		"var image = __decodeImage(N.istr(p0));",
		"this.INT_image = image.image;",
		"this.INT_data = image.data;",
		"this['{% FIELD com.badlogic.gdx.graphics.Pixmap:data %}'] = image.data;",
		"this['{% FIELD com.badlogic.gdx.graphics.Pixmap:width %}'] = image.width;",
		"this['{% FIELD com.badlogic.gdx.graphics.Pixmap:height %}'] = image.height;",
		"this['{% FIELD com.badlogic.gdx.graphics.Pixmap:actualWidth %}'] = image.actualWidth;",
		"this['{% FIELD com.badlogic.gdx.graphics.Pixmap:actualHeight %}'] = image.actualHeight;",
	})
	private void loadImageNativeJs(String path) {
	}

	@JTranscMethodBody(target = "js", value = {
		"this.INT_image = null;",
		"this.INT_data = this.{% FIELD com.badlogic.gdx.graphics.Pixmap:data %};",
	})
	private void createdEmpty() {

	}

	@JTranscMethodBody(target = "js", value = {
		"var that = this;",
		"var image = __decodeImageBytes(p0, p1, p2, p3, p4);",
		"this.INT_image = image.image;",
		"this.INT_data = image.data;",
		"image.image.onload = function() { that['{% METHOD com.badlogic.gdx.graphics.Pixmap:loadedTextureAsync %}'](); };",
		"this['{% FIELD com.badlogic.gdx.graphics.Pixmap:data %}'] = image.data;",
		"this['{% FIELD com.badlogic.gdx.graphics.Pixmap:width %}'] = image.width;",
		"this['{% FIELD com.badlogic.gdx.graphics.Pixmap:height %}'] = image.height;",
		"this['{% FIELD com.badlogic.gdx.graphics.Pixmap:actualWidth %}'] = image.actualWidth;",
		"this['{% FIELD com.badlogic.gdx.graphics.Pixmap:actualHeight %}'] = image.actualHeight;",
	})
	private void loadImageNativeJs(byte[] encodedData, int offset, int len, int width, int height) {
	}

	private boolean loaded = true;
	private List<Runnable> loadedImageCallbacks = new ArrayList<Runnable>();

	private void loadedTextureAsync() {
		loaded = true;
		for (Runnable loadedImage : loadedImageCallbacks) {
			loadedImage.run();
		}
		loadedImageCallbacks.clear();
	}

	public void onLoadedTextureAsync(Runnable runnable) {
		if (loaded) {
			runnable.run();
		} else {
			loadedImageCallbacks.add(runnable);
		}
	}

	private void ensureLoaded() {
		if (!pixelsAvailable) {
			throw new RuntimeException("Can't get pixels for an asynchronous loaded texture");
		}
	}

	@HaxeMethodBody(
		"var image = lime.graphics.Image.fromBytes(p0.getBytes());\n" +
		"var bytes = image.data.toBytes();\n" +
		"this.{% FIELD com.badlogic.gdx.graphics.Pixmap:byteData %} = new JA_B(bytes.length, bytes);\n" +
		"this.{% FIELD com.badlogic.gdx.graphics.Pixmap:width %} = image.width;\n" +
		"this.{% FIELD com.badlogic.gdx.graphics.Pixmap:height %} = image.height;\n" +
		"this.{% FIELD com.badlogic.gdx.graphics.Pixmap:actualWidth %} = image.width;\n" +
		"this.{% FIELD com.badlogic.gdx.graphics.Pixmap:actualHeight %} = image.height;\n"
	)
	private void loadImage(byte[] encodedData, int offset, int len) {
		ImageDecoder.BitmapData bitmap = ImageDecoder.decode(encodedData);
		this.data = bitmap.data;
		this.actualWidth = this.width = bitmap.width;
		this.actualHeight = this.height = bitmap.height;
	}

	private void loadImage(String path, boolean pixelPerfect) throws IOException {
		if (!pixelPerfect && JTranscSystem.isPureJs()) {
			loadImageNativeJs(path);
		} else {
			FileHandle fh = Gdx.files.internal(path);
			byte[] compressedData = fh.readBytes();
			loadImage(compressedData, 0, compressedData.length);
		}
	}

	private void create(int width, int height, Format format) {
		this.width = width;
		this.height = height;
		this.actualWidth = width;
		this.actualHeight = height;
		if (JTranscSystem.isPureJs()) {
			this.data = new int[width * height];
			this.format = Format.RGBA8888;
		} else {
			this.byteData = new byte[width * height * format.bytesPerPixel];
			this.format = format;
		}
		createdEmpty();
	}

	/**
	 * Sets the type of {@link Blending} to be used for all operations. Default is {@link Blending#SourceOver}.
	 *
	 * @param _blending the blending type
	 */
	public void setBlending(Blending _blending) {
		this.blending = _blending;
	}

	/**
	 * @return the currently set {@link Blending}
	 */
	public Blending getBlending() {
		return blending;
	}

	/**
	 * Sets the type of interpolation {@link Filter} to be used in conjunction with
	 * {@link Pixmap#drawPixmap(Pixmap, int, int, int, int, int, int, int, int)}.
	 *
	 * @param filter the filter.
	 */
	public static void setFilter(Filter filter) {
	}

	public Format getFormat() {
		return format;
	}

	public int getGLInternalFormat() {
		return format.getGlFormat();
	}

	public int getGLFormat() {
		return format.getGlFormat();
	}

	public int getGLType() {
		return format.getGlType();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getActualWidth() {
		return actualWidth;
	}

	public int getActualHeight() {
		return actualHeight;
	}


	public ByteBuffer getPixels() {
		if (byteData != null) {
			return ByteBuffer.wrap(byteData);
		}

		return ByteBuffer.wrap(JTranscArrays.copyReinterpretReversed(this.data)).order(ByteOrder.LITTLE_ENDIAN);
	}

	@Override
	public void dispose() {
		// don't used
		//pixmaps.remove(id);
	}

	/**
	 * Sets the color for the following drawing operations
	 *
	 * @param color the color, encoded as RGBA8888
	 */
	public void setColor(int color) {
		this.color = color;
	}

	/**
	 * Sets the color for the following drawing operations.
	 *
	 * @param r The red component.
	 * @param g The green component.
	 * @param b The blue component.
	 * @param a The alpha component.
	 */
	public void setColor(float r, float g, float b, float a) {
		color = ColorFormat8.GDX.make(r, g, b, a);
	}

	/**
	 * Sets the color for the following drawing operations.
	 *
	 * @param color The color.
	 */
	public void setColor(Color color) {
		setColor(color.r, color.g, color.b, color.a);
	}

	/**
	 * Fills the complete bitmap with the currently set color.
	 */
	public void fill() {
		byte r = (byte) ((color & 0xFF000000) >> 24);
		byte g = (byte) ((color & 0x00FF0000) >> 16);
		byte b = (byte) ((color & 0x0000FF00) >> 8);
		byte a = (byte) (color & 0x000000FF);

		for (int i = 0; i < byteData.length; ) {
			byteData[i++] = r;
			byteData[i++] = g;
			byteData[i++] = b;
			byteData[i++] = a;
		}
	}

// /**
// * Sets the width in pixels of strokes.
// *
// * @param width The stroke width in pixels.
// */
// public void setStrokeWidth (int width);

	/**
	 * Draws a line between the given coordinates using the currently set color.
	 *
	 * @param x  The x-coodinate of the first point
	 * @param y  The y-coordinate of the first point
	 * @param x2 The x-coordinate of the first point
	 * @param y2 The y-coordinate of the first point
	 */
	public void drawLine(int x, int y, int x2, int y2) {
		line(x, y, x2, y2, DrawType.STROKE);
	}

	/**
	 * Draws a rectangle outline starting at x, y extending by width to the right and by height downwards (y-axis points downwards)
	 * using the current color.
	 *
	 * @param x      The x coordinate
	 * @param y      The y coordinate
	 * @param width  The width in pixels
	 * @param height The height in pixels
	 */
	public void drawRectangle(int x, int y, int width, int height) {
		rectangle(x, y, width, height, DrawType.STROKE);
	}

	/**
	 * Draws an area form another Pixmap to this Pixmap.
	 *
	 * @param pixmap The other Pixmap
	 * @param x      The target x-coordinate (top left corner)
	 * @param y      The target y-coordinate (top left corner)
	 */
	public void drawPixmap(Pixmap pixmap, int x, int y) {
		drawPixmap(pixmap, x, y, 0, 0, pixmap.width, pixmap.height);
	}

	/**
	 * Draws an area form another Pixmap to this Pixmap.
	 *
	 * @param pixmap    The other Pixmap
	 * @param x         The target x-coordinate (top left corner)
	 * @param y         The target y-coordinate (top left corner)
	 * @param srcx      The source x-coordinate (top left corner)
	 * @param srcy      The source y-coordinate (top left corner);
	 * @param srcWidth  The width of the area form the other Pixmap in pixels
	 * @param srcHeight The height of the area form the other Pixmap in pixles
	 */
	// copyPixels (sourceImage:Image, sourceRect:Rectangle, destPoint:Vector2, alphaImage:Image = null, alphaPoint:Vector2 = null, mergeAlpha:Bool = false):Void
	public void drawPixmap(Pixmap pixmap, int x, int y, int srcx, int srcy, int srcWidth, int srcHeight) {
		ensureLoaded();
		int byteWidth = width * 4;
		int pixmapWidth = pixmap.width * 4;

		int offset = y * byteWidth + (x * 4);
		int srcOffset = srcy * pixmapWidth + (srcx * 4);

		for(int i = 0; i < srcHeight; ++i) {
			System.arraycopy(pixmap.byteData, srcOffset, byteData, offset, srcWidth * 4);
			offset += byteWidth;
			srcOffset += pixmapWidth;
		}
	}

	//private void copyPixels(sourceImage:Image, sourceRect:Rectangle, destPoint:Vector2, alphaImage:Image = null, alphaPoint:Vector2 = null, mergeAlpha:Bool = false):Void

	/**
	 * Draws an area form another Pixmap to this Pixmap. This will automatically scale and stretch the source image to the
	 * specified target rectangle. Use {@link Pixmap#setFilter(Filter)} to specify the type of filtering to be used (nearest
	 * neighbour or bilinear).
	 *
	 * @param pixmap    The other Pixmap
	 * @param srcx      The source x-coordinate (top left corner)
	 * @param srcy      The source y-coordinate (top left corner);
	 * @param srcWidth  The width of the area form the other Pixmap in pixels
	 * @param srcHeight The height of the area form the other Pixmap in pixles
	 * @param dstx      The target x-coordinate (top left corner)
	 * @param dsty      The target y-coordinate (top left corner)
	 * @param dstWidth  The target width
	 * @param dstHeight the target height
	 */
	native public void drawPixmap(Pixmap pixmap, int srcx, int srcy, int srcWidth, int srcHeight, int dstx, int dsty, int dstWidth, int dstHeight);

	/**
	 * Fills a rectangle starting at x, y extending by width to the right and by height downwards (y-axis points downwards) using
	 * the current color.
	 *
	 * @param x      The x coordinate
	 * @param y      The y coordinate
	 * @param width  The width in pixels
	 * @param height The height in pixels
	 */
	public void fillRectangle(int x, int y, int width, int height) {
		ensureLoaded();
		rectangle(x, y, width, height, DrawType.FILL);
	}

	/**
	 * Draws a circle outline with the center at x,y and a radius using the current color and stroke width.
	 *
	 * @param x      The x-coordinate of the center
	 * @param y      The y-coordinate of the center
	 * @param radius The radius in pixels
	 */
	public void drawCircle(int x, int y, int radius) {
		ensureLoaded();
		circle(x, y, radius, DrawType.STROKE);
	}

	/**
	 * Fills a circle with the center at x,y and a radius using the current color.
	 *
	 * @param x      The x-coordinate of the center
	 * @param y      The y-coordinate of the center
	 * @param radius The radius in pixels
	 */
	public void fillCircle(int x, int y, int radius) {
		ensureLoaded();
		circle(x, y, radius, DrawType.FILL);
	}

	/**
	 * Fills a triangle with vertices at x1,y1 and x2,y2 and x3,y3 using the current color.
	 *
	 * @param x1 The x-coordinate of vertex 1
	 * @param y1 The y-coordinate of vertex 1
	 * @param x2 The x-coordinate of vertex 2
	 * @param y2 The y-coordinate of vertex 2
	 * @param x3 The x-coordinate of vertex 3
	 * @param y3 The y-coordinate of vertex 3
	 */
	public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
		ensureLoaded();
		triangle(x1, y1, x2, y2, x3, y3, DrawType.FILL);
	}

	/**
	 * Returns the 32-bit RGBA8888 value of the pixel at x, y. For Alpha formats the RGB components will be one.
	 *
	 * @param x The x-coordinate
	 * @param y The y-coordinate
	 * @return The pixel color in RGBA8888 format.
	 */
	public int getPixel(int x, int y) {
		ensureLoaded();
		//if (JTranscSystem.isPureJs()) {
		//	return ColorFormat8.transform(ColorFormat8.PUREJS, ColorFormat8.GDX, _getPixel(x, y));
		//} else {
		//	return ColorFormat8.transform(ColorFormat8.LIME, ColorFormat8.GDX, _getPixel(x, y));
		//}
		return _getPixel(x, y);
	}

	/**
	 * Draws a pixel at the given location with the current color.
	 *
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 */
	public void drawPixel(int x, int y) {
		ensureLoaded();
		drawPixel(x, y, this.color);
	}

	/**
	 * Draws a pixel at the given location with the given color.
	 *
	 * @param x     the x-coordinate
	 * @param y     the y-coordinate
	 * @param color the color in RGBA8888 format.
	 */
	public void drawPixel(int x, int y, int color) {
		ensureLoaded();
		_setPixel(x, y, ColorFormat8.transform(ColorFormat8.GDX, ColorFormat8.LIME, color));
	}

	native private void circle(int x, int y, int radius, DrawType drawType);

	native private void line(int x, int y, int x2, int y2, DrawType drawType);

	native private void rectangle(int x, int y, int width, int height, DrawType drawType);

	native private void triangle(int x1, int y1, int x2, int y2, int x3, int y3, DrawType drawType);

	//native private void image(CanvasElement image, int srcX, int srcY, int srcWidth, int srcHeight, int dstX, int dstY, int dstWidth, int dstHeight);

	native private void fillOrStrokePath(DrawType drawType);

	private enum DrawType {
		FILL, STROKE
	}

	private int _getPixel(int x, int y) {
		if (byteData != null) {
			switch (format) {
				case Alpha:
				case Intensity:
					return ColorFormat8.GDX.make(0, 0, 0, byteData[y * width + x]);

				case LuminanceAlpha: {
					int offset = y * width * format.bytesPerPixel + x * format.bytesPerPixel;
					return ColorFormat8.GDX.make(0, 0, byteData[offset], byteData[offset + 1]);
				}

				case RGB565: {
					int offset = y * width * format.bytesPerPixel + x * format.bytesPerPixel;
					short color = (short) (((byteData[offset] & 0xFF) << 8) | (byteData[offset + 1] & 0xFF));

					int r5 = (color & 0xf800) >> 11;
					int g6 = (color & 0x07e0) >> 5;
					int b5 = color & 0x001f;

					int r8 = (r5 * 527 + 23) >> 6;
					int g8 = (g6 * 259 + 33) >> 6;
					int b8 = (b5 * 527 + 23) >> 6;

					return ColorFormat8.GDX.make(r8, g8, b8, 255);
				}

				case RGBA4444: {
					int offset = y * width * format.bytesPerPixel + x * format.bytesPerPixel;
					int color = byteData[offset] & 0xFF;
					int r4 = (color & 0xF0) >> 4;
					int g4 = (color & 0x0F);
					color = byteData[offset + 1] & 0xFF;
					int b4 = (color & 0xF0) >> 4;
					int a4 = (color & 0x0F);

					int r8 = r4 | (r4 >> 4);
					int g8 = g4 | (g4 >> 4);
					int b8 = b4 | (b4 >> 4);
					int a8 = a4 | (a4 >> 4);

					return ColorFormat8.GDX.make(r8, g8, b8, a8);
				}

				case RGB888: {
					int offset = y * width * format.bytesPerPixel + x * format.bytesPerPixel;
					return ColorFormat8.GDX.make(byteData[offset], byteData[offset + 1], byteData[offset + 2], 255);
				}

				case RGBA8888: {
					int offset = y * width * format.bytesPerPixel + x * format.bytesPerPixel;
					return ColorFormat8.GDX.make(byteData[offset], byteData[offset + 1], byteData[offset + 2], byteData[offset + 3]);
				}
			}
		}

		return data[y * width + x];
	}

	private void _setPixel(int x, int y, int color) {
		if (byteData != null) {
			switch (format) {
				case Alpha:
				case Intensity:
					byteData[y * width + x] = ColorFormat8.GDX.getA(color);
					return;

				case LuminanceAlpha: {
					int offset = y * width * format.bytesPerPixel + x * format.bytesPerPixel;
					byteData[offset] = ColorFormat8.GDX.getB(color);
					byteData[offset + 1] = ColorFormat8.GDX.getA(color);
					return;
				}

				case RGB565: {
					int offset = y * width * format.bytesPerPixel + x * format.bytesPerPixel;

					int r8 = (ColorFormat8.GDX.getR(color) & 0xFF);
					int g8 = (ColorFormat8.GDX.getG(color) & 0xFF);
					int b8 = (ColorFormat8.GDX.getB(color) & 0xFF);

					int r5 = (r8 * 249 + 1014) >> 11;
					int g6 = (g8 * 253 + 505) >> 10;
					int b5 = (b8 * 249 + 1014) >> 11;

					int rgb565 = (r5 << 11) | (g6 << 5) | (b5);

					byteData[offset] = (byte) ((rgb565 & 0xff00) >> 8);
					byteData[offset + 1] = (byte) (rgb565 & 0xFF);
					return;
				}

				case RGBA4444: {
					int offset = y * width * format.bytesPerPixel + x * format.bytesPerPixel;
					int r4 = (ColorFormat8.GDX.getR(color) & 0xFF) >> 4;
					int g4 = (ColorFormat8.GDX.getG(color) & 0xFF) >> 4;
					int b4 = (ColorFormat8.GDX.getB(color) & 0xFF) >> 4;
					int a4 = (ColorFormat8.GDX.getA(color) & 0xFF) >> 4;
					byteData[offset] = (byte) ((r4 << 4) | g4);
					byteData[offset + 1] = (byte) ((b4 << 4) | a4);
					return;
				}

				case RGB888: {
					int offset = y * width * format.bytesPerPixel + x * format.bytesPerPixel;
					byteData[offset] = ColorFormat8.GDX.getR(color);
					byteData[offset + 1] = ColorFormat8.GDX.getG(color);
					byteData[offset + 2] = ColorFormat8.GDX.getB(color);
					return;
				}

				case RGBA8888: {
					int offset = y * width * format.bytesPerPixel + x * format.bytesPerPixel;
					byteData[offset] = ColorFormat8.GDX.getR(color);
					byteData[offset + 1] = ColorFormat8.GDX.getG(color);
					byteData[offset + 2] = ColorFormat8.GDX.getB(color);
					byteData[offset + 3] = ColorFormat8.GDX.getB(color);
					return;
				}
			}
		}
		data[y * width + x] = color;
	}
}
