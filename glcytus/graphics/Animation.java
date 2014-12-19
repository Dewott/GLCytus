package glcytus.graphics;

import glcytus.NoteChartPlayer;

public class Animation extends Sprite {
	int n = 1;
	double stime = 0, etime = 0;
	double len = 1;
	boolean once = false;
	String frames[] = null;
	ImageHandle imgs[] = null;

	protected Animation() {
	}

	public Animation(String str, double stime, double etime) {
		this(1, etime - stime, true, new String[] { str });
		this.stime = stime;
		this.etime = etime;
	}

	public Animation(int n, double len, boolean once, String frames[]) {
		this.n = n;
		this.len = len / n;
		this.once = once;
		this.frames = frames;
		imgs = new ImageHandle[n];

		for (int i = 0; i < n; i++)
			imgs[i] = GamePlaySpriteLibrary.get(frames[i]);
	}

	public void paint(NoteChartPlayer p, double time) {
		if (time < stime)
			return;

		if (once && (time >= etime))
			return;

		int current = (int) ((time - stime) / len);
		current %= n;
		img = imgs[current];
		super.paint(p, time);
	}

	public void play(NoteChartPlayer p) {
		play(p, stime);
	}

	public void play(NoteChartPlayer p, double stime) {
		this.stime = stime;
		etime = stime + len * n;
		p.addAnimation(this);
	}

	public void setStartTime(double stime) {
		this.stime = stime;
	}

	public double getStartTime() {
		return stime;
	}

	public void setEndTime(double etime) {
		this.etime = etime;
	}

	public double getEndTime() {
		return etime;
	}

	public boolean started(double time) {
		return time >= stime;
	}

	public boolean ended(double time) {
		return time > etime;
	}
}