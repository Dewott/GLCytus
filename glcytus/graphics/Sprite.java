package glcytus.graphics;

import glcytus.NoteChartPlayer;

import java.awt.geom.AffineTransform;
import java.util.LinkedList;

public class Sprite {
	public Sprite father = null;
	public ImageHandle img = null;
	public String name = "";

	// Independent states
	public double x = 0, y = 0, w = 0, h = 0;
	public double angle = 0, sx = 1, sy = 1, ax = 0.5, ay = 0.5;
	public double color[] = new double[] { 1, 1, 1, 1 };
	public boolean flipH = false, flipV = false;

	public LinkedList<Transform> trans = new LinkedList<Transform>();
	public LinkedList<Sprite> childs = new LinkedList<Sprite>();
	public boolean independent = false;

	public Sprite() {
	}

	public Sprite(ImageHandle img) {
		this.img = img;
		w = img.srcw;
		h = img.srch;
	}

	public Sprite(String name) {
		this.name = name;
		this.img = GamePlaySpriteLibrary.get(name);
		w = img.srcw;
		h = img.srch;
	}

	public void paint(NoteChartPlayer p, double time) {
		updateStatus(time);
		for (Sprite s : childs)
			s.paint(p, time);

		if ((w == 0) || (h == 0))
			return;
		p.addRenderTask(new RenderTask(this));
	}

	public void updateStatus(double time) {
		for (Transform t : trans)
			t.adjust(this, time);
	}

	public AffineTransform getAffineTransform() {
		AffineTransform t = new AffineTransform();
		t.translate(x - w * sx * ax, y - h * sy * ay);
		t.rotate(angle, w * sx * ax, h * sy * ay);
		t.scale(sx, sy);
		if (img != null)
			t.scale(w / img.srcw, h / img.srch);
		if ((!independent) && (father != null))
			t.preConcatenate(father.getAffineTransform());
		return t;
	}

	public double getFinalAlpha() {
		if (father == null)
			return Math.min(Math.max(color[3], 0), 1);
		else
			return father.getFinalAlpha() * Math.min(Math.max(color[3], 0), 1);
	}

	public void scale(double s) {
		scale(s, s);
	}

	public void scale(double sx, double sy) {
		this.sx = sx;
		this.sy = sy;
	}

	public void setWidth(double width) {
		this.w = width;
	}

	public void setHeight(double height) {
		this.h = height;
	}

	public void setSize(double width, double height) {
		setWidth(width);
		setHeight(height);
	}

	public void rotate(double angle) {
		this.angle = angle;
	}

	public void setAnchor(double ax, double ay) {
		this.ax = ax;
		this.ay = ay;
	}

	public void setAnchor(String name) {
		switch (name) {
		case "Center":
			ax = 0.5;
			ay = 0.5;
			break;
		case "Top":
			ax = 0.5;
			ay = 1;
			break;
		case "Bottom":
			ax = 0.5;
			ay = 0;
			break;
		case "TopLeft":
			ax = 0;
			ay = 1;
			break;
		case "TopRight":
			ax = 1;
			ay = 1;
			break;
		case "Left":
			ax = 0;
			ay = 0.5;
			break;
		case "Right":
			ax = 1;
			ay = 0.5;
			break;
		case "BottomLeft":
			ax = 0;
			ay = 0;
			break;
		case "BottomRight":
			ax = 1;
			ay = 0;
			break;
		}
	}

	public void setAlpha(double alpha) {
		this.color[3] = alpha;
	}

	public void moveTo(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void addTransform(Transform t) {
		trans.add(t);
	}

	public void removeTransform(Transform t) {
		trans.remove(t);
	}

	public void clearTransforms() {
		trans.clear();
		for (Sprite s : childs)
			s.clearTransforms();
	}

	public void addChild(Sprite s, boolean independent) {
		s.father = this;
		s.independent = independent;
		childs.add(s);
	}

	public Sprite getChild(String name) {
		for (Sprite s : childs)
			if (s.name.equals(name))
				return s;
		return null;
	}

	public void removeChild(Sprite s) {
		s.father = null;
		childs.remove(s);
	}

	public void flipH() {
		flipH = !flipH;
		ax = 1 - ax;
	}

	public void flipV() {
		flipV = !flipV;
		ay = 1 - ay;
	}
}