package glcytus.graphics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class GamePlayAnimationPreset {
	static String flist[] = new String[] { "arrow_explode", "arrow_flash",
			"beat_flash", "beat_vanish", "critical_explosion", "drag_light",
			"explosion", "hold_pressing_1", "hold_pressing_2", "judge_bad",
			"judge_good", "judge_miss", "judge_perfect", "light_add_2",
			"node_explode", "node_flash" };
	static HashMap<String, Double> map1 = new HashMap<String, Double>();
	static HashMap<String, Boolean> map2 = new HashMap<String, Boolean>();
	static HashMap<String, String[]> map3 = new HashMap<String, String[]>();

	public static void init() throws Exception {

		for (int i = 0; i < 16; i++) {
			BufferedReader r = new BufferedReader(
					new FileReader("Application/assets/ui/GamePlay/" + flist[i]
							+ ".anim.json"));
			String s = "";
			String str = r.readLine();

			while (str != null) {
				s += str;
				str = r.readLine();
			}

			r.close();
			JSONObject obj = JSON.parseObject(s);
			map1.put(flist[i], obj.getDoubleValue("length"));
			map2.put(flist[i], obj.getString("mode").equals("once"));
			JSONArray arr = obj.getJSONArray("frames");
			String frames[] = new String[arr.size()];
			for (int j = 0; j < arr.size(); j++) {
				JSONArray arr2 = arr.getJSONArray(j);
				frames[j] = arr2.getString(1);
			}

			map3.put(flist[i], frames);
		}
	}

	public static Animation get(String str) {
		double len = map1.get(str);
		boolean mode = map2.get(str);
		String frames[] = map3.get(str);
		// Make a copy
		String copy[] = new String[frames.length];
		System.arraycopy(frames, 0, copy, 0, frames.length);
		Animation anim = new Animation(frames.length, len, mode, copy);

		if (str.equals("light_add_2"))
			anim.setAnchor(0.5, 0.7);

		return anim;
	}
}