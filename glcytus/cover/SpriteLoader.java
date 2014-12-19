package glcytus.cover;

import glcytus.graphics.Atlas;
import glcytus.graphics.ImageHandle;
import glcytus.graphics.Sprite;

import java.io.File;
import java.util.HashMap;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jogamp.opengl.util.texture.TextureIO;

public class SpriteLoader {
	static HashMap<String, Atlas> rsrc = new HashMap<String, Atlas>();

	public static void loadSprite(String path, Sprite s, JSONObject obj)
			throws Exception {
		s.name = obj.getString("Name");
		s.x = obj.getDoubleValue("X");
		s.y = obj.getDoubleValue("Y");
		if (obj.containsKey("Texture")) {
			ImageHandle img = new ImageHandle();
			img.texture = TextureIO.newTexture(
					new File(path + obj.getString("Texture")), false);
			img.x = 0;
			img.y = 0;
			img.w = 1;
			img.h = 1;
			img.srcw = img.texture.getImageWidth();
			img.srch = img.texture.getImageHeight();
			img.spsx = 0;
			img.spsy = 0;
			img.spsw = img.srcw;
			img.spsh = img.srch;
			s.img = img;
		}
		if (obj.containsKey("Atlas")) {
			String filename = obj.getString("Atlas");
			String part = obj.getString("Part");
			if (!rsrc.containsKey(filename))
				rsrc.put(filename, new Atlas(path, filename.split("\\.")[0]));
			s.img = rsrc.get(filename).get(part);
		}
		int width = obj.getIntValue("Width");
		int height = obj.getIntValue("Height");
		if ((width != 0) && (height != 0))
			s.img.setSize(width, height);
		if (obj.containsKey("Alpha"))
			s.alpha = obj.getDoubleValue("Alpha");
		if (obj.containsKey("Rotation"))
			s.angle = Math.toRadians(obj.getDoubleValue("Rotation"));
		if (obj.containsKey("ScaleX"))
			s.sx = obj.getDoubleValue("ScaleX");
		if (obj.containsKey("ScaleY"))
			s.sy = obj.getDoubleValue("ScaleY");
		if (obj.containsKey("Anchor"))
			s.setAnchor(obj.getString("Anchor"));
		if (obj.containsKey("FlipU"))
			s.flipH();
		if (obj.containsKey("Blending"))
			s.img.blendingAdd = true;
		if (obj.containsKey("Color")) {
			JSONArray carr = obj.getJSONArray("Color");
			double r = carr.getDoubleValue(0);
			double g = carr.getDoubleValue(1);
			double b = carr.getDoubleValue(2);
			s.color = new double[] { r, g, b };
		}
		if (obj.containsKey("Children")) {
			JSONArray childsarr = obj.getJSONArray("Children");
			for (Object subobj : childsarr) {
				JSONObject child = (JSONObject) subobj;
				Sprite cs = new Sprite();
				s.addChild(cs, false);
				loadSprite(path, cs, child);
			}
		}
	}
}