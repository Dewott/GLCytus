package glcytus.graphics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class Atlas {
	public Texture texture = null;
	public HashMap<String, ImageHandle> map = new HashMap<String, ImageHandle>();

	public Atlas(String folder, String filename) throws Exception {
		texture = TextureIO.newTexture(new File(folder + filename + ".png"),
				false);
		JSONObject frames = JSON.parseObject(
				readFile(folder + filename + ".json")).getJSONObject("frames");

		for (Map.Entry<String, Object> entry : frames.entrySet()) {
			ImageHandle img = new ImageHandle();
			img.name = entry.getKey();
			img.texture = texture;

			JSONObject part = (JSONObject) entry.getValue();
			JSONObject fpos = part.getJSONObject("frame");
			img.x = fpos.getIntValue("x") / (double) texture.getWidth();
			img.y = fpos.getIntValue("y") / (double) texture.getHeight();
			img.w = fpos.getIntValue("w") / (double) texture.getWidth();
			img.h = fpos.getIntValue("h") / (double) texture.getHeight();

			JSONObject srcsize = part.getJSONObject("sourceSize");
			img.srcw = srcsize.getIntValue("w");
			img.srch = srcsize.getIntValue("h");

			JSONObject sprsrcsize = part.getJSONObject("spriteSourceSize");
			img.spsx = sprsrcsize.getIntValue("x");
			img.spsy = sprsrcsize.getIntValue("y");
			img.spsw = sprsrcsize.getIntValue("w");
			img.spsh = sprsrcsize.getIntValue("h");

			map.put(img.name, img);
		}
	}

	public ImageHandle get(String part) {
		return map.get(part);
	}

	private String readFile(String path) throws Exception {
		BufferedReader in = new BufferedReader(new FileReader(path));
		String str = "";
		String s = in.readLine();
		while (s != null) {
			str += s;
			s = in.readLine();
		}
		in.close();
		return str;
	}
}