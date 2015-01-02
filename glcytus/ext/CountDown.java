package glcytus.ext;

public class CountDown extends Base {
	public CountDown() throws Exception {
		String folder = "assets/ui/GamePlay";
		loadSprite(folder, "count_down.prefab.json");

		loadMorphingAnimation(folder, "count_down.anim.json", false, 0);
	}
}