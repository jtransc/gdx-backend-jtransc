package com.jtransc.media.limelibgdx.profiler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

/**
 * A nicer class for showing framerate that doesn't spam the console
 * like Logger.log()
 * 
 * @author William Hartman
 */
public class FrameRate implements Disposable{
    private BitmapFont font;
    private SpriteBatch batch;
    private OrthographicCamera cam;

	private long frameStart = 0;
	private int frames = 0;
	private int fps = 0;

    public FrameRate() {
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
		}
		frames++;
    }

    public void render() {
        batch.begin();
        font.draw(batch, fps + " fps", 3, Gdx.graphics.getHeight() - 3);
        batch.end();
    }

    public void dispose() {
        font.dispose();
        batch.dispose();
    }
}