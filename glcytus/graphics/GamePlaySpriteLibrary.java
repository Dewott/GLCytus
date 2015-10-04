package glcytus.graphics;

import glcytus.util.ResourceLoader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class GamePlaySpriteLibrary {
	static HashMap<String, ImageHandle> map = new HashMap<String, ImageHandle>();

	public static void init() throws Exception {
		String flist[] = new String[] { "animation_1", "animation_2", "common",
				"common_2", "common_add" };

		HashMap<String, Double> objlist = new HashMap<String, Double>();
		BufferedReader r1 = new BufferedReader(new InputStreamReader(
				GamePlaySpriteLibrary.class.getClassLoader().getResourceAsStream("glcytus/graphics/objlist.txt")));
		String str1 = r1.readLine();

		while (str1 != null) {
			String pair[] = str1.split("\\t");
			objlist.put(pair[0], Double.parseDouble(pair[1]));
			str1 = r1.readLine();
		}
		r1.close();

		for (int i = 0; i < 5; i++) {
			Atlas atlas = ResourceLoader.loadAtlas("assets/ui/GamePlay/",
					flist[i]);
			if (i == 4) { // common_add
				for (Map.Entry<String, ImageHandle> entry : atlas.map
						.entrySet())
					entry.getValue().blendingAdd = true;
			}
			map.putAll(atlas.map);
		}
		// map.keySet().retainAll(objlist.keySet());
		for (Map.Entry<String, Double> entry : objlist.entrySet()) {
			ImageHandle img = map.get(entry.getKey());
			img.scale(entry.getValue());
		}

		map.get("bar").setWidth(1024);
		map.get("light_add").blendingAdd = true;
	}

	public static ImageHandle get(String str) {
		return map.get(str);
	}
}