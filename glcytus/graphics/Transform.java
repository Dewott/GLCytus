package glcytus.graphics;

public class Transform implements Cloneable {
	public static final int NONE = 0, TRANS_X = 1, TRANS_Y = 2, ALPHA = 3,
			ROTATION = 4, SX = 5, SY = 6, SCALE = 7, COLOR_RED = 8,
			COLOR_GREEN = 9, COLOR_BLUE = 10;
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
		case NONE:
			break;
		case TRANS_X:
			s.x = val;
			break;
		case TRANS_Y:
			s.y = val;
			break;
		case ALPHA:
			s.color[3] = val;
			break;
		case ROTATION:
			s.rotationAngle[0] = val;
			break;
		case SX:
			s.sx = val;
			break;
		case SY:
			s.sy = val;
			break;
		case SCALE:
			s.sx = val;
			s.sy = val;
			break;
		case COLOR_RED:
			s.color[0] = val;
			break;
		case COLOR_GREEN:
			s.color[1] = val;
			break;
		case COLOR_BLUE:
			s.color[2] = val;
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

	public Transform clone() {
		Transform copy = new Transform();
		copy.type = type;
		copy.easing = easing;
		copy.stime = stime;
		copy.etime = etime;
		copy.sval = sval;
		copy.eval = eval;
		copy.lborder = lborder;
		copy.rborder = rborder;
		return copy;
	}
}