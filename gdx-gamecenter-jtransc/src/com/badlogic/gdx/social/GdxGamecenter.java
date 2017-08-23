package com.badlogic.gdx.social;

import com.jtransc.annotation.haxe.HaxeAddFilesTemplate;
import com.jtransc.annotation.haxe.HaxeAddMembers;
import com.jtransc.annotation.haxe.HaxeMethodBody;

@HaxeAddFilesTemplate({
	"GCListener.hx"
})

@HaxeAddMembers({"static var listener:GCListener;"})
public class GdxGamecenter {

	public static final String DISABLED = "disabled";
	public static final String AUTH_SUCCESS = "authSuccess";
	public static final String AUTH_ALREADY = "authAlready";
	public static final String AUTH_FAILURE = "authFailure";
	public static final String SCORE_SUCCESS = "scoreSuccess";
	public static final String SCORE_FAILURE = "scoreFailure";
	public static final String ACHIEVEMENT_SUCCESS = "achievementSuccess";
	public static final String ACHIEVEMENT_FAILURE = "achievementFailure";
	public static final String ACHIEVEMENT_RESET_SUCCESS = "achievementResetSuccess";
	public static final String ACHIEVEMENT_RESET_FAILURE = "achievementResetFailure";

	public static final String ON_GET_ACHIEVEMENT_STATUS_FAILURE = "onGetAchievementStatusFailure";
	public static final String ON_GET_ACHIEVEMENT_STATUS_SUCCESS = "onGetAchievementStatusSuccess";
	public static final String ON_GET_ACHIEVEMENT_PROGRESS_FAILURE = "onGetAchievementProgressFailure";
	public static final String ON_GET_ACHIEVEMENT_PROGRESS_SUCCESS = "onGetAchievementProgressSuccess";
	public static final String ON_GET_PLAYER_SCORE_FAILURE = "onGetPlayerScoreFailure";
	public static final String ON_GET_PLAYER_SCORE_SUCCESS = "onGetPlayerScoreSuccess";

	public static final String ON_GET_PLAYER_FRIENDS_FAILURE = "onGetPlayerFriendsFailure";
	public static final String ON_GET_PLAYER_FRIENDS_SUCCESS = "onGetPlayerFriendsSuccess";
	public static final String ON_GET_PLAYER_PHOTO_FAILURE = "onGetPlayerPhotoFailure";
	public static final String ON_GET_PLAYER_PHOTO_SUCCESS = "onGetPlayerPhotoSuccess";

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

	@HaxeMethodBody(target = "cpp", value =
		"if (listener == null) listener = new GCListener();" +
		"listener.setHandler(p0);" +
		"extension.gamecenter.GameCenter.setEventListener(listener);")
	public static void setListener(GCListener listener){
	}

	@HaxeMethodBody(target = "cpp", value = "if (listener != null) listener.dispose();")
	public static void removeListener(){
	}
}