package glcytus.graphics;

import static javax.media.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static javax.media.opengl.GL.GL_SRC_ALPHA;
import static javax.media.opengl.GL2GL3.GL_QUADS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

public class GamePlayFontLibrary {
	static String flist[] = new String[] { "combo", "ComboSmall", "BoltonBold" };
	static HashMap<String, CFont> fonts = new HashMap<String, CFont>();
	public static double scorecolor[] = new double[] { 10.0 / 51.0,
			10.0 / 51.0, 10.0 / 51.0, 1 };

	// (50,50,50) 0xFF323232

	public static final class CFont {
		String name = "";
		Texture texture = null;
		HashMap<Integer, CharFrame> map = new HashMap<Integer, CharFrame>();

		public static final class CharFrame {
			int w = 0, h = 0;
			double tx = 0, ty = 0, tw = 0, th = 0; // Texture coords
			int xoff = 0, yoff = 0;
			int xadv = 0;
		}
	}

	public static void init() throws Exception {
		// java.util.regex.Pattern
		Pattern p = Pattern.compile("(?<=\\=)\\-?\\d*(?=\\s)");

		for (int i = 0; i < 3; i++) {
			CFont font = new CFont();
			font.name = flist[i];

			BufferedReader r = new BufferedReader(new FileReader(
					"assets/fonts/" + flist[i] + ".fnt"));
			r.readLine();
			r.readLine();
			String str = r.readLine();
			String fname = str.substring(str.indexOf("\"") + 1,
					str.length() - 1);
			font.texture = TextureIO.newTexture(new File("assets/fonts/"
					+ fname), false);

			r.readLine();
			str = r.readLine();

			while (str.indexOf("char") != -1) {
				CFont.CharFrame frame = new CFont.CharFrame();
				Matcher m = p.matcher(str);
				m.find();
				// char id
				int id = Integer.parseInt(m.group()) - 48;
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

				if (i == 2) { // BoltonBold
					frame.w *= 4.0 / 3.0;
					frame.h *= 4.0 / 3.0;
					frame.xoff *= 4.0 / 3.0;
					frame.yoff *= 4.0 / 3.0;
					frame.xadv *= 4.0 / 3.0;
				}

				font.map.put(id, frame);
				str = r.readLine();
			}
			fonts.put(flist[i], font);
		}
	}

	public static int getStringWidth(String fontname, String str) {
		int len = 0;
		CFont font = fonts.get(fontname);
		for (int i = 0; i < str.length(); i++) {
			int id = str.charAt(i) - 48;
			CFont.CharFrame frame = font.map.get(id);
			if (i < str.length() - 1)
				len += frame.xadv;
			else
				len += frame.w;
		}
		return len;
	}

	public static int getStringHeight(String fontname, String str) {
		int maxh = 0;
		CFont font = fonts.get(fontname);
		for (int i = 0; i < str.length(); i++) {
			int id = str.charAt(i) - 48;
			CFont.CharFrame frame = font.map.get(id);
			if (frame.yoff + frame.h > maxh)
				maxh = frame.yoff + frame.h;
		}
		return maxh;
	}

	public static void drawString(GL2 gl, String fontname, String str,
			double x, double y, double color[]) {
		CFont font = fonts.get(fontname);
		font.texture.bind(gl);

		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		if (color == null)
			color = new double[] { 1, 1, 1, 1 };
		gl.glColor4dv(color, 0);

		double xpos = x;
		for (int i = 0; i < str.length(); i++) {
			int id = str.charAt(i) - 48;
			CFont.CharFrame frame = font.map.get(id);
			gl.glBegin(GL_QUADS);
			// Vertex 1
			gl.glTexCoord2d(frame.tx, 1 - frame.ty);
			gl.glVertex2d(xpos + frame.xoff, y + frame.yoff);
			// Vertex 2
			gl.glTexCoord2d(frame.tx, 1 - (frame.ty + frame.th));
			gl.glVertex2d(xpos + frame.xoff, y - frame.h + frame.yoff);
			// Vertex 3
			gl.glTexCoord2d(frame.tx + frame.tw, 1 - (frame.ty + frame.th));
			gl.glVertex2d(xpos + frame.w + frame.xoff, y - frame.h + frame.yoff);
			// Vertex 4
			gl.glTexCoord2d(frame.tx + frame.tw, 1 - frame.ty);
			gl.glVertex2d(xpos + frame.w + frame.xoff, y + frame.yoff);
			gl.glEnd();
			xpos += frame.xadv;
		}
	}
}