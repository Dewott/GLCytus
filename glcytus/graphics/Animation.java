package glcytus.graphics;

import glcytus.NoteChartPlayer;

public class Animation extends Sprite {
	public int n = 1;
	public double stime = 0, etime = 0;
	public double interval = 1;
	public boolean once = false;
	public ImageHandle imgs[] = null;

	public Animation() {
	}

	public Animation(String str, double stime, double etime) {
		this(1, etime - stime, true, new String[] { str });
		this.stime = stime;
		this.etime = etime;
	}

	public Animation(int n, double duration, boolean once, String frames[]) {
		this(n, duration, once, (ImageHandle[]) null);
		imgs = new ImageHandle[n];
		for (int i = 0; i < n; i++)
			imgs[i] = GamePlaySpriteLibrary.get(frames[i]);
	}

	public Animation(int n, double duration, boolean once, ImageHandle imgs[]) {
		this.n = n;
		this.interval = duration / n;
		this.once = once;
		this.imgs = imgs;
	}

	public void paint(NoteChartPlayer p, double time) {
		if (time < stime)
			return;

		if (once && (time >= etime))
			return;

		int current = (int) ((time - stime) / interval);
		current %= n;
		img = imgs[current];
		w = img.srcw;
		h = img.srch;
		super.paint(p, time);
	}

	public void play(NoteChartPlayer p) {
		play(p, stime);
	}

	public void play(NoteChartPlayer p, double stime) {
		this.stime = stime;
		etime = stime + interval * n;
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