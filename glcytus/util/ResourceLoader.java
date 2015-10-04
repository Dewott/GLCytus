package glcytus.util;

import glcytus.graphics.Animation;
import glcytus.graphics.Atlas;
import glcytus.graphics.CFont;
import glcytus.graphics.ImageHandle;
import glcytus.graphics.MorphingAnimation;
import glcytus.graphics.Renderer;
import glcytus.graphics.Sprite;
import glcytus.graphics.TextSprite;
import glcytus.graphics.Texture2D;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.media.opengl.GLProfile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

public class ResourceLoader {
	static HashMap<String, CFont> fonts = new HashMap<String, CFont>();
	static HashMap<String, Animation> anims = new HashMap<String, Animation>();
	static HashMap<String, Atlas> atlasmap = new HashMap<String, Atlas>();
	static HashMap<String, Texture2D> textures = new HashMap<String, Texture2D>();
	static HashMap<String, MorphingAnimation> mpanims = new HashMap<String, MorphingAnimation>();

	public static CFont loadFont(String folder, String name) throws Exception {
		if (fonts.containsKey(name))
			return fonts.get(name);
		Pattern p = Pattern.compile("(?<=\\=)\\-?\\d*(?=\\s)");
		CFont font = new CFont();
		font.name = name;

		File f = getFile(folder, name);
		BufferedReader r = getBufferedReader(f);
		r.readLine();
		String str = r.readLine();
		str = str.substring(18);
		font.lineheight = Double
				.parseDouble(str.substring(0, str.indexOf(' ')));

		str = r.readLine();
		String fname = str.substring(str.indexOf("\"") + 1, str.length() - 1);
		font.texture = loadTexture(f.getParent(), fname);

		r.readLine();
		str = r.readLine();
		while (str.indexOf("char") != -1) {
			CFont.CharFrame frame = font.new CharFrame();
			Matcher m = p.matcher(str);
			m.find();
			// char id
			frame.id = Integer.parseInt(m.group());
			m.find();
			// x
			frame.tx = Integer.parseInt(m.group());
			m.find();
			// y
			frame.ty = Integer.parseInt(m.group());
			m.find();
			// w
			frame.w = Integer.parseInt(m.group());
			frame.tw = frame.w;
			m.find();
			// h
			frame.h = Integer.parseInt(m.group());
			frame.th = frame.h;
			m.find();
			// xoff
			frame.xoff = Integer.parseInt(m.group());
			m.find();
			// yoff
			frame.yoff = Integer.parseInt(m.group());
			m.find();
			// xadv
			frame.xadv = Integer.parseInt(m.group());

			frame.updateImageHandle();
			font.map.put(frame.id, frame);
			str = r.readLine();
			if (str == null)
				break;
		}

		str = r.readLine();
		while (str != null) {
			Matcher m = p.matcher(str);
			m.find();
			CFont.IntPair pair = new CFont.IntPair();
			// first
			pair.first = Integer.parseInt(m.group());
			m.find();
			// second
			pair.second = Integer.parseInt(m.group());
			m.find();
			// amount
			int amount = Integer.parseInt(m.group());

			font.kernings.put(pair, amount);
			str = r.readLine();
		}
		r.close();
		fonts.put(name, font);
		return font;
	}

	public static Animation loadAnimation(String folder, String name)
			throws Exception {
		if (anims.containsKey(name))
			return anims.get(name);
		JSONObject obj = loadJSONObjectFromFile(folder, name);
		double length = obj.getDouble("length");
		boolean once = obj.getString("mode").equals("once");
		JSONArray frames = obj.getJSONArray("frames");
		ImageHandle imgs[] = new ImageHandle[frames.size()];
		for (int i = 0; i < frames.size(); i++) {
			JSONArray frame = frames.getJSONArray(i);
			Atlas atlas = loadAtlas(folder, frame.getString(0)); // filename
			imgs[i] = atlas.get(frame.getString(1)); // part
		}
		Animation anim = new Animation(frames.size(), length, once, imgs);
		anims.put(name, anim);
		return anim;
	}

	public static Atlas loadAtlas(String folder, String name) throws Exception {
		if (name.endsWith(".png")) // No File suffix
			name = name.substring(0, name.length() - 4);
		if (atlasmap.containsKey(name))
			return atlasmap.get(name);
		Atlas atlas = new Atlas(folder, name);
		atlasmap.put(name, atlas);
		return atlas;
	}

	public static Sprite loadSprite(String folder, Sprite s, JSONObject obj)
			throws Exception {
		if (s == null)
			s = new Sprite();
		if (obj.containsKey("Font")) {
			CFont font = loadFont(folder, obj.getString("Font"));
			s = new TextSprite(font, obj.getString("Text"));
		}
		s.name = obj.getString("Name");
		s.x = obj.getDoubleValue("X");
		s.y = obj.getDoubleValue("Y");
		if (obj.containsKey("Texture")) {
			ImageHandle img = new ImageHandle();
			img.texture = loadTexture(folder, obj.getString("Texture"));
			img.x = 0;
			img.y = 0;
			img.w = img.texture.getWidth();
			img.h = img.texture.getHeight();
			img.srcw = img.texture.getWidth();
			img.srch = img.texture.getHeight();
			img.spsx = 0;
			img.spsy = 0;
			img.spsw = img.srcw;
			img.spsh = img.srch;
			s.img = img;
			s.w = img.srcw;
			s.h = img.srch;
		}
		if (obj.containsKey("Atlas")) {
			String altas = obj.getString("Atlas");
			String part = obj.getString("Part");
			s.img = loadAtlas(folder, altas).get(part);
			s.w = s.img.srcw;
			s.h = s.img.srch;
		}
		if (obj.containsKey("Width")) {
			s.w = obj.getIntValue("Width");
			if (s.img != null)
				s.img.setWidth(s.w);
		}
		if (obj.containsKey("Height")) {
			s.h = obj.getIntValue("Height");
			if (s.img != null)
				s.img.setHeight(s.h);
		}
		if (obj.containsKey("Rotation"))
			s.rotationAngle[0] = Math.toRadians(obj.getDoubleValue("Rotation"));
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
			s.color[0] = carr.getDoubleValue(0);
			s.color[1] = carr.getDoubleValue(1);
			s.color[2] = carr.getDoubleValue(2);
		}
		if (obj.containsKey("TopColor")) {
			JSONArray carr = obj.getJSONArray("TopColor");
			s.color[0] = carr.getDoubleValue(0);
			s.color[1] = carr.getDoubleValue(1);
			s.color[2] = carr.getDoubleValue(2);
		}
		if (obj.containsKey("Alpha"))
			s.color[3] = obj.getDoubleValue("Alpha");
		if (obj.containsKey("Children")) {
			JSONArray childsarr = obj.getJSONArray("Children");
			for (Object subobj : childsarr) {
				JSONObject child = (JSONObject) subobj;
				Sprite cs = new Sprite();
				cs = loadSprite(folder, cs, child);
				s.addChild(cs, false);
			}
		}
		return s;
	}

	public static Texture2D loadTexture(String folder, String name)
			throws Exception {
		File f = getFile(folder, name);
		if (textures.containsKey(f.getName()))
			return textures.get(f.getName());
		TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), f,
				false, f.getName().split("\\.")[1]);
		Texture2D t = new Texture2D(data);
		textures.put(f.getName(), t);
		Renderer.currentInstance.addTexture(t);
		return t;
	}

	public static MorphingAnimation loadMorphingAnimation(String folder,
			String path, boolean loop) throws Exception {
		File f = getFile(folder, path);
		if (mpanims.containsKey(f.getName()))
			return mpanims.get(f.getName());
		MorphingAnimation anim = new MorphingAnimation(folder, path, loop);
		mpanims.put(f.getName(), anim);
		return anim;
	}

	public static String getPath(String folder, String name) {
		URI src = null;
		if (name.startsWith("/Application/")) {
			name = name.substring(13);
			src = new File(name).toURI();
		} else {
			if (folder.startsWith("/Application/"))
				folder = folder.substring(13);
			src = new File(folder).toURI().resolve(name);
		}
		File f = new File(src);
		return f.getPath();
	}

	public static String getPath(String path) {
		return getPath("./", path);
	}

	public static File getFile(String folder, String name) {
		return new File(getPath(folder, name));
	}

	public static File getFile(String path) {
		return new File(getPath(path));
	}

	public static String loadFile(File f) throws Exception {
		BufferedReader r = getBufferedReader(f);
		if (r == null)
			return null;
		StringBuilder content = new StringBuilder();
		String str = r.readLine();
		while (str != null) {
			content.append(str);
			content.append('\n');
			str = r.readLine();
		}
		r.close();
		return content.toString();
	}

	public static String loadFile(String folder, String name) throws Exception {
		return loadFile(getFile(folder, name));
	}

	public static String loadFile(String path) throws Exception {
		return loadFile(getFile(path));
	}

	public static BufferedReader getBufferedReader(File f) throws Exception {
		return new BufferedReader(new FileReader(f));
	}

	public static BufferedReader getBufferedReader(String folder, String name)
			throws Exception {
		return getBufferedReader(getFile(folder, name));
	}

	public static BufferedReader getBufferedReader(String path)
			throws Exception {
		return getBufferedReader(getFile(path));
	}

	public static JSONObject loadJSONObjectFromFile(File f) throws Exception {
		String content = loadFile(f);
		return JSON.parseObject(content);
	}

	public static JSONObject loadJSONObjectFromFile(String folder, String name)
			throws Exception {
		return loadJSONObjectFromFile(getFile(folder, name));
	}

	public static JSONObject loadJSONObjectFromFile(String path)
			throws Exception {
		return loadJSONObjectFromFile(getFile(path));
	}
}