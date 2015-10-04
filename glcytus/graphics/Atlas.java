package glcytus.graphics;

import glcytus.util.ResourceLoader;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public class Atlas {
	public Texture2D texture = null;
	public HashMap<String, ImageHandle> map = new HashMap<String, ImageHandle>();

	public Atlas(String folder, String name) throws Exception {
		texture = ResourceLoader.loadTexture(folder, name + ".png");
		JSONObject frames = ResourceLoader.loadJSONObjectFromFile(folder,
				name + ".json").getJSONObject("frames");

		for (Map.Entry<String, Object> entry : frames.entrySet()) {
			ImageHandle img = new ImageHandle();
			img.name = entry.getKey();
			img.texture = texture;

			JSONObject part = (JSONObject) entry.getValue();
			JSONObject fpos = part.getJSONObject("frame");
			img.x = fpos.getIntValue("x");
			img.y = fpos.getIntValue("y");
			img.w = fpos.getIntValue("w");
			img.h = fpos.getIntValue("h");

			JSONObject srcsize = part.getJSONObject("sourceSize");
			img.srcw = srcsize.getIntValue("w");
			img.srch = srcsize.getIntValue("h");

			JSONObject sprsrcsize = part.getJSONObject("spriteSourceSize");
			img.spsx = sprsrcsize.getIntValue("x");
			img.spsy = sprsrcsize.getIntValue("y");
			img.spsw = sprsrcsize.getIntValue("w");
			img.spsh = sprsrcsize.getIntValue("h");

			if (part.containsKey("rotated"))
				img.rotated = part.getBoolean("rotated");

			map.put(img.name, img);
		}
	}

	public ImageHandle get(String part) {
		return map.get(part);
	}
}