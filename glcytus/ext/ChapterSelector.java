package glcytus.ext;

public class ChapterSelector extends Base {
	public ChapterSelector() throws Exception {
		String folder = "Application/assets/ui/ChapterSelector/";
		loadSprite(folder, "clear_info_bar.prefab.json");

		double endtime = loadAnimation(folder, "clear_info_enter.anim.json",
				false, 0);
		loadAnimation(folder, "clear_info_switch.anim.json", false, endtime);
		loadAnimation(folder, "clear_info_back.anim.json", false, endtime + 5); // Test
	}
}