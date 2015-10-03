package glcytus.ext;

import glcytus.graphics.MorphingAnimation;
import glcytus.util.ResourceLoader;

public class Loading extends Base {
	MorphingAnimation enteranim = null, loopanim = null, leaveanim = null;

	public Loading() throws Exception {
		String folder = "assets/ui/Loading/";
		loadSprite(folder, "loading_gate.prefab.json");

		enteranim = ResourceLoader.loadMorphingAnimation(folder, "loading_enter.anim.json", false);
		loopanim = ResourceLoader.loadMorphingAnimation(folder, "waiting_loop.anim.json", true);
		leaveanim = ResourceLoader.loadMorphingAnimation(folder, "loading_leave.anim.json", false);
	}

	public void enter(double time) {
		useAnimation(enteranim, time);
	}

	public void loop(double time) {
		useAnimation(loopanim, time);
	}

	public void leave(double time) {
		useAnimation(leaveanim, time);
	}
}