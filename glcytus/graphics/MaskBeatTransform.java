package glcytus.graphics;

public class MaskBeatTransform extends Transform {
	double beat = 0, pshift = 0;

	public MaskBeatTransform(double pshift, double beat) {
		this.pshift = pshift;
		this.beat = beat / 2;
	}

	public void adjust(Sprite s, double time) {
		double pos = ((time + pshift) % beat) / beat;
		double size = 0.8 + 0.2 * Math.max(Math.cos(pos * Math.PI * 2), 0);
		s.scale(size, 1);
	}
}
