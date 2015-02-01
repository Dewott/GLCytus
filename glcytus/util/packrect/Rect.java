package glcytus.util.packrect;

public class Rect implements Comparable<Rect> {
	public int x = 0, y = 0, w = 0, h = 0;
	public int layer = -1;

	public Rect(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	public Rect(int w, int h) {
		this(0, 0, w, h);
	}

	public int compareTo(Rect r) {
		if (this.w == r.w)
			return r.h - this.h;
		else
			return r.w - this.w;
	}
}