package glcytus.ext;

import glcytus.graphics.MorphingAnimation;
import glcytus.util.ResourceLoader;

public class PausePanel extends Base {
	MorphingAnimation enteranim = null, leaveanim = null;

	public PausePanel() throws Exception {
		String folder = "assets/ui/GamePlay/";
		loadSprite(folder, "pause.prefab.json");

		enteranim = ResourceLoader.loadMorphingAnimation(folder,
				"pause_enter.anim.json", false);
		leaveanim = ResourceLoader.loadMorphingAnimation(folder,
				"pause_leave.anim.json", false);
	}

	public void enter(double time) {
		useAnimation(enteranim, time);
	}

	public void leave(double time) {
		useAnimation(leaveanim, time);
	}
}