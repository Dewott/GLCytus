package glcytus.graphics;

public class ComboSmallPopTransform extends Transform {
	double stime = Integer.MAX_VALUE;

	public void pop(double stime) {
		this.stime = stime;
	}

	public void adjust(Sprite s, double time) {
		double delta = time - stime;
		if (delta > 1 / 6.0)
			return;
		double pos = Math.sin(delta * 6 * Math.PI);
		if (s.name.equals("combo_small_bg"))
			s.scale(1 + 0.2 * pos, 1);
		else
			s.scale(1 + 0.2 * pos);
	}
}
