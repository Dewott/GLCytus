package glcytus.ext;

public class SelectCover extends Base {
	public SelectCover(String songtitle) throws Exception {
		String folder = "assets/songs/" + songtitle + "/";
		loadSprite(folder, "select_cover.prefab.json");

		double endtime = loadMorphingAnimation(folder, songtitle
				+ "_enter.anim.json", false, 0);
		loadMorphingAnimation(folder, songtitle + "_loop.anim.json", true,
				endtime);
	}
}