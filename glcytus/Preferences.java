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
	public static double bgmGain = 1;
	public static double fxGain = 1;
	public static double chartOffset = 0;

	public static void load() throws Exception {
		BufferedReader in = new BufferedReader(new FileReader("preferences.txt"));
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
				bgmGain = val / 10.0;
				break;
			case "fxGain":
				fxGain = val / 10.0;
				break;
			case "chartOffset":
				chartOffset = val / 1000.0;
				break;
			}
			str = in.readLine();
		}
		in.close();
	}
}