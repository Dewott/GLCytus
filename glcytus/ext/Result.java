package glcytus.ext;

import glcytus.graphics.Sprite;

import java.util.LinkedHashMap;

public class Result extends Base {
	LinkedHashMap<String, Sprite> elements = new LinkedHashMap<String, Sprite>();

	public Result() throws Exception {
		String folder = "Application/assets/ui/Result/";
		loadSprite(folder, "result_root.prefab.json");

		Base diff = new Base();
		diff.loadSprite(folder, "difficulty.prefab.json");
		diff.loadAnimation(folder, "difficulty.anim.json", false, 0);
		diff.setAnchor("BottomRight");
		diff.moveTo(459.5, -310);
		addElements(diff);
		addChild(diff, false);

		Base perfect = new Base();
		perfect.loadSprite(folder, "perfect.prefab.json");
		perfect.loadAnimation(folder, "BeatStatistics.anim.json", false, 0);
		addChild(perfect, false);

		Base good = new Base();
		good.loadSprite(folder, "good.prefab.json");
		good.loadAnimation(folder, "BeatStatistics.anim.json", false, 0);
		addChild(good, false);

		Base bad = new Base();
		bad.loadSprite(folder, "bad.prefab.json");
		bad.loadAnimation(folder, "BeatStatistics.anim.json", false, 0);
		addChild(bad, false);

		Base miss = new Base();
		miss.loadSprite(folder, "miss.prefab.json");
		miss.loadAnimation(folder, "BeatStatistics.anim.json", false, 0);
		addChild(miss, false);

		loadAnimation(folder, "result_animation.anim.json", false, 0);
	}
}