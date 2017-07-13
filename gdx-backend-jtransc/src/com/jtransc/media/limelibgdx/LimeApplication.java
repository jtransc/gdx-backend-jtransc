package com.jtransc.media.limelibgdx;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Clipboard;
import com.jtransc.JTranscSystem;
import com.jtransc.annotation.*;
import com.jtransc.annotation.haxe.*;
import com.jtransc.media.limelibgdx.profiler.DebugInfo;

import java.io.File;
import java.io.IOException;

@HaxeAddFilesTemplate({
	"HaxeLimeGdxApplication.hx"
})
@HaxeAddFilesBeforeBuildTemplate({
	"program.xml"
})
@HaxeCustomMain("" +
	"package {{ entryPointPackage }};\n" +
	"class {{ entryPointSimpleName }} extends HaxeLimeGdxApplication {\n" +
	"    public function new() {\n" +
	"        super();\n" +
	"        {{ inits }}\n" +
	"        {{ mainClass }}.{{ mainMethod }}(N.strArray(N.args()));\n" +
	"    }\n" +
	"}\n"
)
@HaxeAddSubtargetList({
	@HaxeAddSubtarget(name = "android"),
	@HaxeAddSubtarget(name = "blackberry"),
	@HaxeAddSubtarget(name = "desktop"),
	@HaxeAddSubtarget(name = "emscripten"),
	@HaxeAddSubtarget(name = "flash", alias = {"swf", "as3"}),
	@HaxeAddSubtarget(name = "html5", alias = {"js"}),
	@HaxeAddSubtarget(name = "ios"),
	@HaxeAddSubtarget(name = "linux"),
	@HaxeAddSubtarget(name = "mac"),
	@HaxeAddSubtarget(name = "tvos"),
	@HaxeAddSubtarget(name = "webos"),
	@HaxeAddSubtarget(name = "windows"),
	@HaxeAddSubtarget(name = "neko")
})
@HaxeCustomBuildCommandLine({
	"@limebuild.cmd"
})
@HaxeCustomBuildAndRunCommandLine({
	"@limetest.cmd"
})
@HaxeAddLibraries({
	"lime:5.2.1"
})
@HaxeAddAssets({
	"com/badlogic/gdx/utils/arial-15.fnt",
	"com/badlogic/gdx/utils/arial-15.png"
})
//@JTranscMethodBody(target = "js", value = "")
@JTranscAddFile(target = "js", priority = 0, process = true, prepend = "js/libgdx_polyfills.js")
@JTranscAddFile(target = "js", priority = 1, process = true, prepend = "js/libgdx_assets.js")
@JTranscAddFile(target = "js", priority = 2, process = true, prepend = "js/libgdx_keys.js")
@JTranscAddFile(target = "js", priority = 3, process = true, prepend = "js/libgdx.js")
@JTranscAddFile(target = "js", process = true, src = "js/template/index.html", dst = "index.html")
@JTranscAddFile(target = "js", process = true, src = "js/template/electron-main.js", dst = "electron-main.js")
@JTranscAddFile(target = "all", process = false, isAsset = true, src = "com/badlogic/gdx/utils/arial-15.fnt", dst = "com/badlogic/gdx/utils/arial-15.fnt")
@JTranscAddFile(target = "all", process = false, isAsset = true, src = "com/badlogic/gdx/utils/arial-15.png", dst = "com/badlogic/gdx/utils/arial-15.png")
@JTranscRegisterCommand(target = "js", name = "electron", command = "electron", check = { "electron", "--version" }, getFolderCmd = { "npm", "list", "-g" }, install = {"npm", "-g", "install", "electron-prebuilt" })
@JTranscRunCommand(target = "js", value = { "electron", "{{ outputFolder }}/electron-main.js" })
@JTranscAddKeywords(target = "all", value = { "GL", "gl", "app" })
public class LimeApplication extends GdxApplicationAdapter implements Application {
	static private final boolean TRACE = false;
	//static private final boolean TRACE = true;

	public LimeApplication(ApplicationListener applicationListener, String title, int width, int height) {
		super(applicationListener, width, height);
		setApplicationToLime(this);
		referenceClasses();
		setTitle(title);
		setSize(width, height);
	}

	@JTranscMethodBody(target = "js", value = "libgdx.setTitle(N.istr(p0));")
	static public void setTitle(String title) {
	}

	@JTranscMethodBody(target = "js", value = "libgdx.setSize(p0, p1);")
	static public void setSize(int width, int height) {
	}

	@JTranscMethodBody(target = "js", value = "libgdx.show();")
	static public void show() {
	}

	@Override
	protected LimeNet createNet() {
		return new LimeNet();
	}

	@Override
	protected LimeFiles createFiles() {
		return new LimeFiles();
	}

	@Override
	protected LimeInput createInput() {
		return new LimeInput();
	}

	@Override
	protected LimeGraphics createGraphics(int width, int height) {
		return new LimeGraphics(width, height, TRACE);
	}

	private ApplicationLogger applicationLogger;
	@Override
	public void setApplicationLogger(ApplicationLogger logger) {
		this.applicationLogger = logger;
	}

	@Override
	public ApplicationLogger getApplicationLogger() {
		return applicationLogger;
	}

	// @TODO: mark package to include!
	static private void referenceClasses() {
		//new com.badlogic.gdx.graphics.g2d.BitmapFont();
		new com.badlogic.gdx.graphics.Color(0xFFFFFFFF);
		new com.badlogic.gdx.scenes.scene2d.ui.Skin.TintedDrawable();
		new com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle();
		new com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle();
		new com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle();
		new com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle();
		new com.badlogic.gdx.scenes.scene2d.ui.SplitPane.SplitPaneStyle();
		new com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle();
		new com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle();
		new com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle();
		new com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle();
		new com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle();
		new com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle();
		new com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle();
		new com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle();
		new com.badlogic.gdx.scenes.scene2d.ui.Tree.TreeStyle();
		new com.badlogic.gdx.scenes.scene2d.ui.TextTooltip.TextTooltipStyle();
		new GlyphLayout.GlyphRun();
	}

	@HaxeMethodBody("HaxeLimeGdxApplication.app = p0;")
	@JTranscMethodBody(target = "js", value = "app = p0;")
	private void setApplicationToLime(LimeApplication app) {
	}

	@HaxeMethodBody("return HaxeLimeGdxApplication.app != null;")
	@JTranscMethodBody(target = "js", value = "return app !== null;")
	public static boolean isAppCreated() {
		return false;
	}

	@Override
	public int getVersion() {
		return 0;
	}

	@Override
	protected Preferences createPreferences(String name) {
		// @TODO: node.js support
		return new GdxPreferencesAdapter(name, false) {
			private File getFile(String name) {
				return new File(name + ".prefs");
			}

			@Override
			@HaxeMethodBody(target = "js", value = "trace('localStorage.getItem:' + p0); return N.str(js.Browser.window.localStorage.getItem(p0._str));")
			protected String loadString(String name) throws IOException {
				//return new String(JTranscIoTools.readFile(getFile(name)), "utf-8");
				return super.loadString(name);
			}

			@Override
			@HaxeMethodBody(target = "js", value = "trace('localStorage.setItem:' + p0 + ':' + p1); js.Browser.window.localStorage.setItem(p0._str, p1._str);")
			protected void storeString(String name, String prefs) throws IOException {
				//writeFile(getFile(name), prefs.getBytes("utf-8"));
				super.storeString(name, prefs);
			}

			@Override
			public void flush() {
				System.out.println("GdxPreferencesAdapter.flush();");
				super.flush();
			}
		};
	}

	@Override
	protected Clipboard createClipboard() {
		if (JTranscSystem.usingJTransc()) {
			return new Clipboard() {
				private String content = "";

				@Override
				@HaxeMethodBody("return N.str(lime.system.Clipboard.text);")
				native public String getContents();

				@Override
				@HaxeMethodBody("lime.system.Clipboard.text = p0._str;")
				native public void setContents(String content);
			};
		} else {
			try {
				return (com.badlogic.gdx.utils.Clipboard) Class.forName("com.jtransc.media.limelibgdx.LimeApplicationAwtUtils$AwtClipboardAdaptor").newInstance();
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}
	}

	@Override
	protected Audio createAudio() {
		return new LimeAudio();
	}

	@HaxeMethodBody("return HaxeLimeGdxApplication.instance.getWindowWidth();")
	@JTranscMethodBody(target = "js", value = "return window.innerWidth|0;")
	static int getWindowWidth() {
		return LwjglApplicationConfiguration.defaultWidth;
	}

	@HaxeMethodBody("return HaxeLimeGdxApplication.instance.getWindowHeight();")
	@JTranscMethodBody(target = "js", value = "return window.innerHeight|0;")
	static int getWindowHeight() {
		return LwjglApplicationConfiguration.defaultHeight;
	}

	@HaxeMethodBody("return lime.app.Application.current.window.scale;")
	public static float getApplicationScale() {
		return 1f;
	}

	@HaxeMethodBody("return HaxeLimeGdxApplication.instance.getDisplayWidth(0);")
	static int getDisplayWidth() {
		return LwjglApplicationConfiguration.defaultWidth;
	}

	@HaxeMethodBody("return HaxeLimeGdxApplication.instance.getDisplayHeight(0);")
	static int getDisplayHeight() {
		return LwjglApplicationConfiguration.defaultHeight;
	}

	private boolean firstFrame = true;

	@SuppressWarnings("unused")
	public void render() {
		if (!isAppCreated()) {
			return;
		}
		LimeInput.flushInput();
		super.render();
		if (Gdx.gl instanceof GL20Ext) {
			((GL20Ext)Gdx.gl).present();
		}
		if (firstFrame) {
			firstFrame = false;
			if (DebugInfo.isShowDebugInfo()) {
				debugInfo = new DebugInfo();
			}
			show();
		}
		if (DebugInfo.isShowDebugInfo()) {
			debugInfo.update();
			debugInfo.render();
		}
		LimeInput.lime_frame();
	}

	@Override
	public void onResumed() {
		super.onResumed();
	}

	@Override
	public void onPaused() {
		super.onPaused();
	}

	@Override
	public void onDisposed() {
		super.onDisposed();
		if (DebugInfo.isShowDebugInfo()) {
			debugInfo.dispose();
		}
	}

	@Override
	protected ApplicationType createApplicationType() {
		return LimeDevice.getType();
	}

	private DebugInfo debugInfo;
}
