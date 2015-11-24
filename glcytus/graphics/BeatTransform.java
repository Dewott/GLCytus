package glcytus.graphics;

public class BeatTransform extends Transform {
	double beat = 0, pshift = 0;
	double stime = 0;

	public BeatTransform(double stime, double pshift, double beat) {
		this.stime = stime;
		this.pshift = pshift;
		this.beat = beat;
	}

	public void adjust(Sprite s, double time) {
		if (time < stime)
			return;

		double pos = ((time + pshift) % beat) / beat;
		double size = 1 + 0.08 * Math.max(Math.cos(pos * Math.PI * 2), 0);
		s.scale(size);
	}
}
