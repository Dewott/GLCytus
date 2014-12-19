package glcytus.graphics;

public class Transform {
	public static final int NONE = 0, TRANS_X = 1, TRANS_Y = 2, ALPHA = 3,
			ROTATION = 4, SX = 5, SY = 6, SCALE = 7;
	public int type = 0;

	public static final int LINEAR = 0, BEZIER = 1, CUBIC = 2;
	public int easing = 0;

	public double stime = 0, etime = 0, sval = 0, eval = 0;
	public boolean lborder = true, rborder = true;

	public Transform() {
	}

	public Transform(int type, int easing, double stime, double etime,
			double sval, double eval, boolean lborder, boolean rborder) {
		this(type, easing, stime, etime, sval, eval);
		this.lborder = lborder;
		this.rborder = rborder;
	}

	public Transform(int type, int easing, double stime, double etime,
			double sval, double eval) {
		this.type = type;
		this.easing = easing;
		this.stime = stime;
		this.etime = etime;
		this.sval = sval;
		this.eval = eval;
	}

	public void setValue(Sprite s, double val) {
		switch (type) {
		case 0:
			break;
		case 1:
			s.x = val;
			break;
		case 2:
			s.y = val;
			break;
		case 3:
			s.alpha = val;
			break;
		case 4:
			s.angle = val;
			break;
		case 5:
			s.sx = val;
			break;
		case 6:
			s.sy = val;
			break;
		case 7:
			s.sx = val;
			s.sy = val;
			break;
		}
	}

	public void adjust(Sprite s, double time) {
		if (time < stime) {
			if (lborder)
				setValue(s, sval);
			return;
		}
		if (time > etime) {
			if (rborder)
				setValue(s, eval);
			return;
		}
		double pos = (time - stime) / (etime - stime);
		switch (type) {
		case LINEAR:
		default:
			setValue(s, (eval - sval) * pos + sval);
			break;
		}
	}

	public LoopTransform asLoopTransform() {
		return new LoopTransform(this);
	}
}