package glcytus.ext;

public class AllPerfect extends Base {
	public AllPerfect() throws Exception {
		String folder = "assets/ui/GamePlay";
		loadSprite(folder, "all_perfect_fx.prefab.json");

		loadMorphingAnimation(folder, "all_perfect_enter.anim.json", false, 0);
	}
}