package glcytus.util;

import glcytus.graphics.CFont;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jogamp.opengl.util.texture.TextureIO;

public class FontLoader {
	// Read .fnt file
	static HashMap<String, CFont> fonts = new HashMap<String, CFont>();

	public static CFont loadFont(String folder, String name) throws Exception {
		if (fonts.containsKey(name))
			return fonts.get(name);
		if (folder.startsWith("/"))
			folder = folder.substring(1);
		Pattern p = Pattern.compile("(?<=\\=)\\-?\\d*(?=\\s)");
		CFont font = new CFont();
		font.name = name;

		BufferedReader r = new BufferedReader(new FileReader(folder + name));
		r.readLine();
		String str = r.readLine();
		str = str.substring(18);
		font.lineheight = Double
				.parseDouble(str.substring(0, str.indexOf(" ")));

		str = r.readLine();
		String fname = str.substring(str.indexOf("\"") + 1, str.length() - 1);
		font.texture = TextureIO.newTexture(new File(folder + fname), false);

		r.readLine();
		str = r.readLine();
		while (str.indexOf("char") != -1) {
			CFont.CharFrame frame = new CFont.CharFrame();
			Matcher m = p.matcher(str);
			m.find();
			// char id
			int id = Integer.parseInt(m.group());
			m.find();
			// x
			frame.tx = Integer.parseInt(m.group())
					/ (double) font.texture.getImageWidth();
			m.find();
			// y
			frame.ty = Integer.parseInt(m.group())
					/ (double) font.texture.getImageHeight();
			m.find();
			// w
			frame.w = Integer.parseInt(m.group());
			frame.tw = frame.w / (double) font.texture.getImageWidth();
			m.find();
			// h
			frame.h = Integer.parseInt(m.group());
			frame.th = frame.h / (double) font.texture.getImageHeight();
			m.find();
			// xoff
			frame.xoff = Integer.parseInt(m.group());
			m.find();
			// yoff
			frame.yoff = Integer.parseInt(m.group());
			m.find();
			// xadv
			frame.xadv = Integer.parseInt(m.group());

			font.map.put(id, frame);
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
}