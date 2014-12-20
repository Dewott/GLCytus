package glcytus.ext;

public class SelectCover extends Base {
	public SelectCover(String songtitle) throws Exception {
		String folder = "Application/assets/songs/" + songtitle + "/";
		loadSprite(folder, "select_cover.prefab.json");

		double endtime = loadAnimation(folder, songtitle + "_enter.anim.json",
				false, 0);
		loadAnimation(folder, songtitle + "_loop.anim.json", true, endtime);
	}
}