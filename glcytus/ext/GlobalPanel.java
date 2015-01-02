package glcytus.ext;

public class GlobalPanel extends Base {
	public GlobalPanel() throws Exception {
		String folder = "assets/common/";
		loadSprite(folder, "global_panel.prefab.json");

		loadMorphingAnimation(folder, "global_panel_enter.anim.json", false, 0);
	}
}