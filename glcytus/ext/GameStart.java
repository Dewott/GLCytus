package glcytus.ext;

public class GameStart extends Base {
	public GameStart() throws Exception {
		String folder = "assets/ui/GameStart/";
		loadSprite(folder, "game_start.prefab.json");

		Base optionpop = new Base();
		optionpop.loadSprite(folder, "option_pop.prefab.json");
		optionpop.loadMorphingAnimation(folder, "option_pop_enter.anim.json",
				false, 0);
		addChild(optionpop, false);

		Base optionsound = new Base();
		optionsound.loadSprite(folder, "option_sound.prefab.json");
		optionsound.loadMorphingAnimation(folder,
				"option_sound_enter.anim.json", false, 0);
		addChild(optionsound, false);

		double endtime = loadMorphingAnimation(folder,
				"game_start_enter.anim.json", false, 0);
		loadMorphingAnimation(folder, "game_start_loop.anim.json", true,
				endtime);
	}
}