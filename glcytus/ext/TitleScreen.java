package glcytus.ext;

public class TitleScreen extends Base {
	public TitleScreen() throws Exception {
		String folder = "Application/assets/ui/Title/New/";
		loadSprite(folder, "titleScreen.prefab.json");

		double endtime = loadAnimation(folder, "enter.anim.json", false, 0);
		loadAnimation(folder, "loop.anim.json", true, endtime);
	}
}