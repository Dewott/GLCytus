package glcytus.ext;

import glcytus.graphics.Sprite;
import glcytus.util.MorphingAnimation;
import glcytus.util.SpriteLoader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedHashMap;

import com.alibaba.fastjson.JSON;

public class Base extends Sprite {
	LinkedHashMap<String, Sprite> elements = new LinkedHashMap<String, Sprite>();

	public Base() {
	}

	public void loadSprite(String folder, String filename) throws Exception {
		SpriteLoader.loadSprite(folder, this,
				JSON.parseObject(readFile(folder + filename)));
		addElements(this);
	}

	public double loadAnimation(String folder, String filename, boolean loop,
			double stime) throws Exception {
		return MorphingAnimation
				.read(folder + filename, elements, false, stime);
	}

	protected void addElements(Sprite s) {
		elements.put(s.name, s);
		for (Sprite cs : s.childs)
			addElements(cs);
	}

	protected String readFile(String path) throws Exception {
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