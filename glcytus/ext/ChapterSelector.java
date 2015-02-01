package glcytus.ext;

import glcytus.graphics.MorphingAnimation;
import glcytus.util.ResourceLoader;

public class ChapterSelector extends Base {
	MorphingAnimation enteranim = null, switchanim = null, backanim = null;

	public ChapterSelector() throws Exception {
		String folder = "assets/ui/ChapterSelector/";
		loadSprite(folder, "clear_info_bar.prefab.json");

		enteranim = ResourceLoader.loadMorphingAnimation(folder,
				"clear_info_enter.anim.json", false);
		switchanim = ResourceLoader.loadMorphingAnimation(folder,
				"clear_info_switch.anim.json", false);
		backanim = ResourceLoader.loadMorphingAnimation(folder,
				"clear_info_back.anim.json", false);
	}

	public void ciEnter(double time) {
		useAnimation(enteranim, time);
	}

	public void ciSwitch(double time) {
		useAnimation(switchanim, time);
	}

	public void ciBack(double time) {
		useAnimation(backanim, time);
	}
}