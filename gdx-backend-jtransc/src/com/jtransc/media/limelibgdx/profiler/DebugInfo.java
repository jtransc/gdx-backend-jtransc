package com.jtransc.media.limelibgdx.profiler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.jtransc.annotation.haxe.HaxeMethodBody;

import java.util.ArrayList;

/**
 * A nicer class for showing framerate that doesn't spam the console
 * like Logger.log()
 *
 * @author William Hartman
 */
public class DebugInfo implements Disposable {
	// From cpp.vm.GC class:
	//
	// Introduced hxcpp_api_level 310
	// Returns stats on memory usage:
	//   MEM_INFO_USAGE - estimate of how much is needed by program (at last collect)
	//   MEM_INFO_RESERVED - memory allocated for possible use
	//   MEM_INFO_CURRENT - memory in use, includes uncollected garbage.
	//     This will generally saw-tooth between USAGE and RESERVED
	//   MEM_INFO_LARGE - Size of separate pool used for large allocs.  Included in all the above.


	public static final int DONT_SHOW_MEMORY_INFO = 0;

	public static final int SHOW_MEM_INFO_USAGE = 1 << 1;
	public static final int SHOW_MEM_INFO_RESERVED = 2 << 1;
	public static final int SHOW_MEM_INFO_CURRENT = 2 << 1;
	public static final int SHOW_MEM_INFO_LARGE = 3 << 1;

	public static final int SHOW_FULL_MEM_INFO = SHOW_MEM_INFO_USAGE | SHOW_MEM_INFO_RESERVED | SHOW_MEM_INFO_CURRENT | SHOW_MEM_INFO_LARGE;

	private int memInfoFlags = DONT_SHOW_MEMORY_INFO;

	private BitmapFont font;
	private SpriteBatch batch;
	private OrthographicCamera cam;

	private long frameStart = 0;
	private int frames = 0;
	private int fps = 0;

	private ArrayList<String> debugInfo = new ArrayList<String>();

	public DebugInfo() {
		if (isShowMemoryInfo()) {
			this.memInfoFlags = DebugInfo.SHOW_FULL_MEM_INFO;
		}

		font = new BitmapFont();
		batch = new SpriteBatch();
		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public void resize(int screenWidth, int screenHeight) {
		cam = new OrthographicCamera(screenWidth, screenHeight);
		cam.translate(screenWidth / 2, screenHeight / 2);
		cam.update();
		batch.setProjectionMatrix(cam.combined);
	}

	public void update() {
		long time = System.nanoTime();

		if (time - frameStart >= 1000000000) {
			fps = frames;
			frames = 0;
			frameStart = time;
			prepareDebugInfo();
		}

		frames++;
	}

	private void prepareDebugInfo() {
		debugInfo.clear();
		if (isShowFPS()) {
			debugInfo.add(fps + " fps");
		}

		if (memInfoFlags != DONT_SHOW_MEMORY_INFO) {
			debugInfo.add("Memory info:");

			gc();
			debugInfo.add("free: " + (Runtime.getRuntime().freeMemory() >> 20) + " MB");
			if ((memInfoFlags & SHOW_MEM_INFO_USAGE) != 0) {
				debugInfo.add("usage: " + (memInfo((SHOW_MEM_INFO_USAGE >> 1) - 1) >> 20) + " MB");
			}
			if ((memInfoFlags & SHOW_MEM_INFO_CURRENT) != 0) {
				debugInfo.add("current: " + (memInfo((SHOW_MEM_INFO_CURRENT >> 1) - 1) >> 20) + " MB");
			}
			if ((memInfoFlags & SHOW_MEM_INFO_RESERVED) != 0) {
				debugInfo.add("reserved: " + (memInfo((SHOW_MEM_INFO_RESERVED >> 1) - 1) >> 20) + " MB");
			}
			if ((memInfoFlags & SHOW_MEM_INFO_LARGE) != 0) {
				debugInfo.add("large: " + (memInfo((SHOW_MEM_INFO_LARGE >> 1) - 1) >> 20) + " MB");
			}
		}
	}

	@HaxeMethodBody("cpp.vm.Gc.run(true);")
	private void gc() {
		System.gc();
	}

	public void render() {
		batch.begin();
		int x = 3;
		int y = Gdx.graphics.getHeight() - 3;
		int offset = (int) font.getLineHeight();
		for (String info : debugInfo) {
			font.draw(batch, info, x, y);
			y -= offset;
		}
		batch.end();
	}

	@HaxeMethodBody("return cpp.vm.Gc.memInfo(p0);")
	public int memInfo(int type) {
		return 0;
	}

	@HaxeMethodBody("{% if extra.showFPS %}return {{ extra.showFPS }};{% else %}return false;{% end %}")
	private static boolean isShowFPS() {
		return false;
	}

	@HaxeMethodBody("{% if extra.showMemInfo %}return {{ extra.showMemInfo }};{% else %}return false;{% end %}")
	private static boolean isShowMemoryInfo() {
		return false;
	}

	public static boolean isShowDebugInfo() {
		return isShowFPS() || isShowMemoryInfo();
	}

	public void dispose() {
		font.dispose();
		batch.dispose();
	}
}