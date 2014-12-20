package glcytus.util;

import glcytus.graphics.Atlas;
import glcytus.graphics.CFont;
import glcytus.graphics.FontSprite;
import glcytus.graphics.ImageHandle;
import glcytus.graphics.Sprite;

import java.io.File;
import java.util.HashMap;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jogamp.opengl.util.texture.TextureIO;

public class SpriteLoader {
	static HashMap<String, Atlas> rsrc = new HashMap<String, Atlas>();

	public static Sprite loadSprite(String path, Sprite s, JSONObject obj)
			throws Exception {
		if (path.startsWith("/"))
			path = path.substring(1);
		if (s == null)
			s = new Sprite();
		if (obj.containsKey("Font")) {
			String str = obj.getString("Font");
			int pos = str.lastIndexOf('/');
			String filename = str.substring(pos + 1);
			str = str.substring(0, pos + 1); // Folder

			CFont font = FontLoader.loadFont(str, filename);
			s = new FontSprite(font, obj.getString("Text"));
		}
		s.name = obj.getString("Name");
		s.x = obj.getDoubleValue("X");
		s.y = obj.getDoubleValue("Y");
		if (obj.containsKey("Texture")) {
			ImageHandle img = new ImageHandle();
			String src = obj.getString("Texture");
			if (src.indexOf("/") == -1)
				src = path + src;
			if (src.startsWith("/"))
				src = src.substring(1);
			img.texture = TextureIO.newTexture(new File(src), false);
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
		if ((s.img != null) && (width != 0) && (height != 0))
			s.img.setSize(width, height);
		if (obj.containsKey("Alpha"))
			s.alpha = obj.getDoubleValue("Alpha");
		if (obj.containsKey("Rotation"))
			s.angle = Math.toRadians(obj.getDoubleValue("Rotation"));
		if (obj.containsKey("ScaleX"))
			s.sx = obj.getDoubleValue("ScaleX");
		if (obj.containsKey("ScaleY"))
			s.sy = obj.getDoubleValue("ScaleY");
		if (obj.containsKey("FlipU"))
			s.flipH();
		if (obj.containsKey("FlipV"))
			s.flipV();
		if (obj.containsKey("Anchor"))
			s.setAnchor(obj.getString("Anchor"));
		if (obj.containsKey("Blending") && (s.img != null))
			s.img.blendingAdd = true;
		if (obj.containsKey("Color")) {
			JSONArray carr = obj.getJSONArray("Color");
			double r = carr.getDoubleValue(0);
			double g = carr.getDoubleValue(1);
			double b = carr.getDoubleValue(2);
			s.color = new double[] { r, g, b, 1 };
		}
		if (obj.containsKey("TopColor")) {
			JSONArray carr = obj.getJSONArray("TopColor");
			double r = carr.getDoubleValue(0);
			double g = carr.getDoubleValue(1);
			double b = carr.getDoubleValue(2);
			s.color = new double[] { r, g, b, 1 };
		}
		if (obj.containsKey("Children")) {
			JSONArray childsarr = obj.getJSONArray("Children");
			for (Object subobj : childsarr) {
				JSONObject child = (JSONObject) subobj;
				Sprite cs = new Sprite();
				cs = loadSprite(path, cs, child);
				s.addChild(cs, false);
			}
		}
		return s;
	}
}