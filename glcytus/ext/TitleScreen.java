package glcytus.ext;

public class TitleScreen extends Base {
	public TitleScreen() throws Exception {
		String folder = "assets/ui/Title/New/";
		loadSprite(folder, "titleScreen.prefab.json");

		double endtime = loadMorphingAnimation(folder, "enter.anim.json", false, 0);
		loadMorphingAnimation(folder, "loop.anim.json", true, endtime);
	}
}