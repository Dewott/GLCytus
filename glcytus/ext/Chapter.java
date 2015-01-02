package glcytus.ext;

public class Chapter extends Base {
	public Chapter(int ch) throws Exception {
		String folder1 = "assets/chapters/";
		loadSprite(folder1, "chapter_" + ch + ".prefab.json");

		String folder2 = "assets/ui/ChapterSelector/";
		loadMorphingAnimation(folder2, "chapter_fadein.anim.json", false, 0);
	}
}