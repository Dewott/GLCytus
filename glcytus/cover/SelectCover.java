package glcytus.cover;

import glcytus.graphics.MorphingAnimation;
import glcytus.graphics.Sprite;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedHashMap;

import com.alibaba.fastjson.JSON;

public class SelectCover extends Sprite {
	LinkedHashMap<String, Sprite> elements = new LinkedHashMap<String, Sprite>();

	public SelectCover(String songtitle) throws Exception {
		String folder = "assets/songs/" + songtitle + "/";
		SpriteLoader
				.loadSprite(
						folder,
						this,
						JSON.parseObject(readFile(folder
								+ "select_cover.prefab.json")));
		addElements(this);

		double endtime = MorphingAnimation.read(folder + songtitle
				+ "_enter.anim.json", elements, false, 0);
		MorphingAnimation.read(folder + songtitle + "_loop.anim.json",
				elements, true, endtime);
	}

	private void addElements(Sprite s) {
		elements.put(s.name, s);
		for (Sprite cs : s.childs)
			addElements(cs);
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