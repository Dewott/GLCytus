package glcytus.ext;

public class IconText extends Base {
	public IconText(String songtitle) throws Exception {
		String folder = "assets/songs/" + songtitle + "/";
		loadSprite(folder, "icon_text_" + songtitle + ".prefab.json");
	}
}