package glcytus.graphics;

import glcytus.util.ResourceLoader;

import java.util.HashMap;

public class GamePlayAnimationPreset {
	static String flist[] = new String[] { "arrow_explode", "arrow_flash", "beat_flash", "beat_vanish",
			"critical_explosion", "drag_light", "explosion", "hold_pressing_1", "hold_pressing_2", "judge_bad",
			"judge_good", "judge_miss", "judge_perfect", "light_add_2", "node_explode", "node_flash" };
	static HashMap<String, Animation> map = new HashMap<String, Animation>();

	public static void init() throws Exception {
		for (int i = 0; i < flist.length; i++) {
			Animation anim = ResourceLoader.loadAnimation("assets/ui/GamePlay/", flist[i] + ".anim.json");

			if (i == 2) {
				anim.imgs[2] = anim.imgs[1];
				anim.imgs[3] = anim.imgs[1];
			}

			map.put(flist[i], anim);
		}
	}

	public static Animation get(String str) {
		Animation anim = map.get(str);
		// Make a copy
		Animation copy = new Animation();
		copy.n = anim.n;
		copy.interval = anim.interval;
		copy.once = anim.once;
		copy.imgs = anim.imgs;

		if (str.equals("light_add_2"))
			copy.setAnchor(0.5, 0.7);

		return copy;
	}
}