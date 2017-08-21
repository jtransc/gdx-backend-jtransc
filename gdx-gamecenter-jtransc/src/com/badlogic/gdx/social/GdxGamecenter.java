package com.badlogic.gdx.social;


import com.jtransc.annotation.haxe.HaxeMethodBody;

public class GdxGamecenter {

	@HaxeMethodBody(target = "cpp", value = "return extension.gamecenter.GameCenter.available;")
	public native static boolean isAvailable();

	@HaxeMethodBody(target = "cpp", value = "extension.gamecenter.GameCenter.authenticate();")
	public native static void login();

	@HaxeMethodBody(target = "cpp", value = "return N.str(extension.gamecenter.GameCenter.getPlayerName());")
	public native static String getPlayerName();

	@HaxeMethodBody(target = "cpp", value = "return N.str(extension.gamecenter.GameCenter.getPlayerID());")
	public native static String getPlayerID();

	@HaxeMethodBody(target = "cpp", value = "extension.gamecenter.GameCenter.getPlayerPhoto(N.i_str(p0));")
	public native static void getPlayerPhoto(String playerID);

	@HaxeMethodBody(target = "cpp", value = "extension.gamecenter.GameCenter.getPlayerFriends();")
	public native static void getPlayerFriends();

	@HaxeMethodBody(target = "cpp", value = "extension.gamecenter.GameCenter.reportAchievement(N.i_str(p0), p1, p2);")
	public native static void reportAchievement(String id, float percent, boolean showBanner);
	public static void reportAchievement(String id, float percent) {
		reportAchievement(id, percent, true);
	}

	@HaxeMethodBody(target = "cpp", value = "extension.gamecenter.GameCenter.reportScore(N.i_str(p0), p1);")
	public native static void reportScore(String id, int score);

	@HaxeMethodBody(target = "cpp", value = "extension.gamecenter.GameCenter.resetAchievements();")
	public native static void resetAchievements();

	@HaxeMethodBody(target = "cpp", value = "extension.gamecenter.GameCenter.showAchievements();")
	public native static void showAchievements();

	@HaxeMethodBody(target = "cpp", value = "extension.gamecenter.GameCenter.showLeaderboard(N.i_str(p0));")
	public native static void showLeaderboard(String id);

	@HaxeMethodBody(target = "cpp", value = "extension.gamecenter.GameCenter.getAchievementProgress(N.i_str(p0));")
	public native static void getAchievementProgress(String id);

	@HaxeMethodBody(target = "cpp", value = "extension.gamecenter.GameCenter.getAchievementStatus(N.i_str(p0));")
	public native static void getAchievementStatus(String id);

	@HaxeMethodBody(target = "cpp", value = "extension.gamecenter.GameCenter.getPlayerScore(N.i_str(p0));")
	public native static void getPlayerScore(String id);



	// @TODO: replace openfl to lime only classes
	//public static setListener(GCListener listener);
	//public static removeListener();

	interface GCListener {
		void onEvent(String type, String d1, String d2, String d3, String d4);
	}
}