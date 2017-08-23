import extension.gamecenter.GameCenterListener;

class GCListener implements GameCenterListener {

	private var handler:{% CLASS com.badlogic.gdx.social.GCListener %};

	public function new () {}
	
	public function onEvent (type:String, data1:String, data2:String, data3:String, data4:String):Void {
		if (handler != null)
			handler{% IMETHOD com.badlogic.gdx.social.GCListener:onEvent %}(N.str(type), N.str(data1), N.str(data2), N.str(data3), N.str(data4));
	}
	
	public function setHandler(handler:{% CLASS com.badlogic.gdx.social.GCListener %}):Void {
		this.handler = handler;
	}
	
	public function dispose():Void {
		handler = null;
	}
	
}