package glcytus;

import java.io.BufferedReader;
import java.io.FileReader;

public class Preferences {
	public static int convertMP3 = 1;
	public static int clickfx = 1;
	public static int popupMode = 2;
	public static int enableBG = 1;
	public static int width = 960, height = 640;
	public static int fullScreen = 0;
	public static int bgmGain = 10;
	public static int fxGain = 10;

	public static void load() throws Exception {
		BufferedReader in = new BufferedReader(
				new FileReader("preferences.txt"));
		String str = in.readLine();
		while (str != null) {
			String part[] = str.split("\\=");
			int val = Integer.parseInt(part[1]);
			switch (part[0]) {
			case "convertMP3":
				convertMP3 = val;
				break;
			case "clickfx":
				clickfx = val;
				break;
			case "popupMode":
				popupMode = val;
				break;
			case "enableBG":
				enableBG = val;
				break;
			case "width":
				width = val;
				break;
			case "height":
				height = val;
				break;
			case "fullScreen":
				fullScreen = val;
				break;
			case "bgmGain":
				bgmGain = val;
				break;
			case "fxGain":
				fxGain = val;
				break;
			}
			str = in.readLine();
		}
		in.close();
	}
}