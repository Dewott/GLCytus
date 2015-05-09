package glcytus.graphics;
import glcytus.NoteChartPlayer;

public class Animation extends Sprite {
	public int n = 1;
	public static final int STATUS_PLAY = 0, STATUS_STOP = 1;
	public int status = STATUS_PLAY;
	public double stime = 0, etime = -1;
	public double interval = 1;
	public boolean once = false, reversed = false;
	public int stopAt = -1;
	public ImageHandle imgs[] = null;

	public Animation() {
	}
	
	public Animation(String str, double stime, double etime) {
		this(1, etime - stime, true, new String[] { str });
		this.stime = stime;
		this.etime = etime;
	}

	public Animation(int n, double duration, boolean once, ImageHandle imgs[]) {
		this.n = n;
		this.interval = duration / n;
		this.once = once;
		this.imgs = imgs;
	}
	
	public Animation(int n, double duration, boolean once, String frames[]) {
		this(n, duration, once, (ImageHandle[]) null);
		imgs = new ImageHandle[n];
		for (int i = 0; i < n; i++)
			imgs[i] = GamePlaySpriteLibrary.get(frames[i]);
	}
	
	public void play(NoteChartPlayer p) {
		play(p, stime);
	}

	public void play(NoteChartPlayer p, double stime) {
		this.stime = stime;
		etime = stime + interval * n;
		p.addAnimation(this);
	}
	
	public void playToAndStop(int frame){
	    this.stopAt = frame;
		this.status = STATUS_PLAY;
	}

	public int getCurrentFrame(double time){
	    int frame = 0;
	    if(time>stime){
		    if((etime!=-1)&&(time>etime)&&once) frame = n - 1;
			else frame = (int)((time - stime)/interval)%n;
		}
		if(reversed) frame = (n - 1) - frame;
		return frame;
	}
	
	public void paint(Renderer r, double time) {
		if (time < stime)
			return;
		if (once && (etime!=-1) && (time >= etime))
			return;
			
		if(status == STATUS_PLAY){
		    int current = getCurrentFrame(time);
			if(current == stopAt) status = STATUS_STOP;
		    updateImage(imgs[current]);
		}
		super.paint(r, time);
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