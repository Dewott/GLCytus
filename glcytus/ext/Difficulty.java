package glcytus.ext;

import java.io.FileInputStream;
import java.util.Scanner;

import glcytus.graphics.Sprite;
import glcytus.graphics.TextSprite;
import glcytus.util.ResourceLoader;

public class Difficulty extends Base {
	public Difficulty(String songtitle, String diff) throws Exception {
		String folder = "assets/ui/Result/";
		loadSprite(folder, "difficulty.prefab.json");

		Sprite ch = getChild("Difficulty/Text/Easy");
		if (diff.equals("easy"))
			ch = getChild("Difficulty/Text/Hard");
		removeChild(ch);

		TextSprite text = (TextSprite) getChild("Difficulty/Level");
		if (diff.equals("hard"))
			text.font = ResourceLoader.loadFont("assets/common/", "numbers_hard.fnt");

		Scanner s = new Scanner(new FileInputStream("assets/songs/" + songtitle + "/diff.txt"));
		int easyLevel = s.nextInt(), hardLevel = s.nextInt();
		s.close();
		if (diff.equals("hard"))
			text.setText(String.valueOf(hardLevel));
		else
			text.setText(String.valueOf(easyLevel));
	}
}