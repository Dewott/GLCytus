package glcytus.ext;

import glcytus.util.*;
import glcytus.graphics.*;

public class ComboEffect extends Base {
	Base back = null;
	TextSprite text1 = null, text2 = null;
	MorphingAnimation frontanim = null, backanim = null;

	public ComboEffect() throws Exception {
		String folder = "assets/ui/GamePlay/";
		loadSprite(folder, "combo_effect.prefab.json");
		text1 = (TextSprite) elements.get("text");
		text1.setAnchor("Left");
		text1.moveTo(-200, 150);

		back = new Base();
		back.loadSprite(folder, "combo_effect.prefab.json");
		text2 = (TextSprite) back.elements.get("text");
		text2.setAnchor("Left");
		text2.moveTo(-200, 150);
		backanim = ResourceLoader.loadMorphingAnimation(folder,
				"combo_effect_back.anim.json", false);
		addChild(back, true);

		frontanim = ResourceLoader.loadMorphingAnimation(folder,
				"combo_effect_front.anim.json", false);
	}

	public void show(double time, int combo) {
		clearTransforms();
		back.clearTransforms();
		frontanim.use(elements, time);
		backanim.use(back.elements, time);
		text1.setText(String.valueOf(combo));
		text2.setText(String.valueOf(combo));
	}
}