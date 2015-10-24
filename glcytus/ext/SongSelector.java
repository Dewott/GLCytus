package glcytus.ext;

public class SongSelector extends Base {
	public SongSelector() throws Exception {
		new Result();
		String folder = "assets/ui/SongSelector/";

		Base background = new Base();
		background.loadSprite(folder, "background.prefab.json");
		addChild(background, false);

		Base diffselector = new Base();
		diffselector.loadSprite(folder, "difficulty_selector.prefab.json");
		diffselector.loadMorphingAnimation(folder, "select_easy.anim.json", false, 0);
		addChild(diffselector, false);

		/*
		 * Base gamestats=new Base();
		 * gamestats.loadSprite(folder,"game_stats.prefab.json");
		 * addChild(gamestats,false);
		 */

		Base upperpanelback = new Base();
		upperpanelback.loadSprite(folder, "upper_panel_back.prefab.json");
		upperpanelback.loadMorphingAnimation(folder, "upper_panel_back_enter.anim.json", false, 0);
		addChild(upperpanelback, false);

		Base upperpanelfront = new Base();
		upperpanelfront.loadSprite(folder, "upper_panel_front.prefab.json");
		upperpanelfront.loadMorphingAnimation(folder, "upper_panel_front_enter.anim.json", false, 0);
		addChild(upperpanelfront, false);
	}
}