package glcytus.util;

import glcytus.graphics.Sprite;
import glcytus.graphics.Transform;

public class SpriteState {
	public double x = 0, y = 0, w = 0, h = 0;
	public double angle = 0, sx = 1, sy = 1;
	public double color[] = new double[] { 1, 1, 1, 1 };

	public SpriteState() {
	}

	public SpriteState(Sprite s, double time) {
		Sprite copy = new Sprite();
		copy.x = s.x;
		copy.y = s.y;
		copy.w = s.w;
		copy.h = s.h;
		copy.angle = s.angle;
		copy.sx = s.sx;
		copy.sy = s.sy;
		System.arraycopy(s.color, 0, copy.color, 0, 4);
		for (Transform t : s.trans)
			t.adjust(copy, time);

		this.x = copy.x;
		this.y = copy.y;
		this.w = copy.w;
		this.h = copy.h;
		this.angle = copy.angle;
		this.sx = copy.sx;
		this.sy = copy.sy;
		System.arraycopy(this.color, 0, copy.color, 0, 4);
	}
}