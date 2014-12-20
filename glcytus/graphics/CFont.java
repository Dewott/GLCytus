package glcytus.graphics;

import java.util.HashMap;
import java.util.Map;

import com.jogamp.opengl.util.texture.Texture;

public final class CFont implements Cloneable {
	public String name = "";
	public Texture texture = null;
	public HashMap<Integer, CharFrame> map = new HashMap<Integer, CharFrame>();
	public HashMap<IntPair, Integer> kernings = new HashMap<IntPair, Integer>();
	public double lineheight = 0;
	public double sx = 1, sy = 1;

	public static final class CharFrame implements Cloneable {
		public int w = 0, h = 0;
		public double tx = 0, ty = 0, tw = 0, th = 0; // Texture coords
		public int xoff = 0, yoff = 0;
		public int xadv = 0;

		public CharFrame clone() {
			CharFrame copy = new CharFrame();
			copy.w = w;
			copy.h = h;
			copy.tx = tx;
			copy.ty = ty;
			copy.tw = tw;
			copy.th = th;
			copy.xoff = xoff;
			copy.yoff = yoff;
			copy.xadv = xadv;
			return copy;
		}
	}

	public void scale(double sx, double sy) {
		for (Map.Entry<Integer, CharFrame> entry : map.entrySet()) {
			CharFrame frame = entry.getValue();
			frame.w *= sx;
			frame.h *= sy;
			frame.xoff *= sx;
			frame.yoff *= sy;
			frame.xadv *= sx;
		}
		for (Map.Entry<IntPair, Integer> entry : kernings.entrySet())
			kernings.put(entry.getKey(), (int) (entry.getValue() * sx));
		lineheight *= sy;
		this.sx = sx;
		this.sy = sy;
	}

	public void scale(double s) {
		scale(s, s);
	}

	public CFont getScaledFont(double sx, double sy) {
		if ((sx == 1) && (sy == 1))
			return this;
		CFont copy = clone();
		copy.scale(sx, sy);
		return copy;
	}

	public CFont getScaledFont(double s) {
		if (s == 1)
			return this;
		else
			return getScaledFont(s, s);
	}

	public int getStringWidth(String str) {
		int len = 0;
		for (int i = 0; i < str.length(); i++) {
			CharFrame frame = map.get((int) str.charAt(i));
			len += frame.xoff;
			if (i < str.length() - 1) {
				len += frame.xadv;
				IntPair pair = new IntPair(str.charAt(i), str.charAt(i + 1));
				if (kernings.containsKey(pair))
					len += kernings.get(pair);
			} else
				len += frame.w;
		}
		return len;
	}

	public CFont clone() {
		CFont copy = new CFont();
		copy.name = name;
		copy.texture = texture;
		copy.lineheight = lineheight;
		copy.sx = 1;
		copy.sy = 1;
		for (Map.Entry<Integer, CharFrame> entry : map.entrySet()) {
			int id = entry.getKey();
			CharFrame frame = entry.getValue();
			copy.map.put(id, frame.clone());
		}
		for (Map.Entry<IntPair, Integer> entry : kernings.entrySet()) {
			IntPair pair = entry.getKey();
			int amount = entry.getValue();
			copy.kernings.put(pair, amount);
		}
		return copy;
	}

	public static class IntPair {
		public int first = 0, second = 0;

		public IntPair() {
		}

		public IntPair(int first, int second) {
			this.first = first;
			this.second = second;
		}

		public IntPair(IntPair pair) {
			this.first = pair.first;
			this.second = pair.second;
		}

		public int hashCode() {
			return first * second;
		}

		public boolean equals(Object obj) {
			if (obj instanceof IntPair) {
				IntPair pair = (IntPair) obj;
				return (first == pair.first) && (second == pair.second);
			} else
				return false;
		}
	}
}