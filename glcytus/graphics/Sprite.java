package glcytus.graphics;

import java.util.LinkedList;

import com.jogamp.opengl.math.Matrix4;

public class Sprite {
	public Sprite father = null;
	public ImageHandle img = null;
	public String name = "";

	// Independent states
	public double x = 0, y = 0, z = 0, w = 0, h = 0;
	public double sx = 1, sy = 1, ax = 0.5, ay = 0.5;
	public double rotationAngle[] = new double[] { 0, 0, 0 };
	public double color[] = new double[] { 1, 1, 1, 1 };
	public boolean flipH = false, flipV = false, blendingAdd = false;

	public LinkedList<Transform> trans = new LinkedList<Transform>();
	public LinkedList<Sprite> childs = new LinkedList<Sprite>();
	public boolean independent = false;

	public Sprite() {
	}

	public Sprite(ImageHandle img) {
		updateImage(img);
	}

	public Sprite(String name) {
		this.name = name;
		updateImage(GamePlaySpriteLibrary.get(name));
	}

	public void updateImage(ImageHandle img) {
		this.img = img;
		w = img.srcw;
		h = img.srch;
	}

	public void paint(Renderer r, double time) {
		updateStatus(time);
		for (Sprite s : childs)
			s.paint(r, time);

		if ((w == 0) || (h == 0))
			return;
		r.addRenderTask(new RenderTask(this));
	}

	public void updateStatus(double time) {
		for (Transform t : trans)
			t.adjust(this, time);
	}

	public Matrix4 getTransformMatrix() {
		Matrix4 mat = new Matrix4();
		mat.loadIdentity();
		mat.translate((float) x, (float) y, (float) z);
		mat.rotate((float) rotationAngle[0], 0f, 0f, 1f);
		mat.rotate((float) rotationAngle[1], 0f, 1f, 0f);
		mat.rotate((float) rotationAngle[2], 1f, 0f, 0f);
		mat.translate((float) (-w * sx * ax), (float) (-h * sy * ay), 0f);
		mat.scale((float) sx, (float) sy, 1f);
		if (img != null)
			mat.scale((float) (w / img.srcw), (float) (h / img.srch), 1f);
		if ((!independent) && (father != null)) {
			Matrix4 prev = father.getTransformMatrix();
			prev.multMatrix(mat);
			return prev;
		} else
			return mat;
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

	public void rotate(double a, double b, double y) {
		this.rotationAngle[0] = a;
		this.rotationAngle[1] = b;
		this.rotationAngle[2] = y;
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

	public void moveTo(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
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