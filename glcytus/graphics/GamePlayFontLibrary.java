package glcytus.graphics;

import glcytus.util.FontLoader;

import java.util.HashMap;

public class GamePlayFontLibrary {
	static String flist[] = new String[] { "combo", "ComboSmall", "BoltonBold" };
	static HashMap<String, CFont> fonts = new HashMap<String, CFont>();
	public static double scorecolor[] = new double[] { 10.0 / 51.0,
			10.0 / 51.0, 10.0 / 51.0, 1 };

	// (50,50,50) 0xFF323232

	public static void init() throws Exception {
		for (int i = 0; i < 3; i++) {
			CFont font = FontLoader.loadFont("Application/assets/fonts/",
					flist[i] + ".fnt");
			font.name = flist[i];
			fonts.put(flist[i], font);
		}
	}

	public static CFont get(String fontname) {
		return fonts.get(fontname);
	}
}