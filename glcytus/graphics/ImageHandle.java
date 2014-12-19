package glcytus.graphics;

import java.awt.geom.Rectangle2D;

import com.jogamp.opengl.util.texture.Texture;

public class ImageHandle {
	public Texture texture = null;
	public String name = "";
	public double x = 0, y = 0, w = 0, h = 0;
	public double srcw = 0, srch = 0;
	public double spsx = 0, spsy = 0, spsw = 0, spsh = 0;
	public boolean blendingAdd = false;
	public double texPts[] = null;
	public double pts[] = null;

	public double[] getPts() {
		if (pts != null)
			return pts;
		Rectangle2D.Double rect1 = new Rectangle2D.Double(0, 0, srcw, srch);
		Rectangle2D.Double rect2 = new Rectangle2D.Double(spsx, spsy, spsw,
				spsh);
		Rectangle2D.Double rect3 = (Rectangle2D.Double) rect1
				.createIntersection(rect2);

		double s = x + (rect3.x - rect2.x) / spsw * w;
		double t = y + (rect3.y - rect2.y) / spsh * h;
		double s2 = s + rect3.width / spsw * w;
		double t2 = t + rect3.height / spsh * h;
		texPts = new double[8];
		texPts[0] = s;
		texPts[2] = s;
		texPts[4] = s2;
		texPts[6] = s2;
		texPts[1] = 1 - t;
		texPts[3] = 1 - t2;
		texPts[5] = 1 - t2;
		texPts[7] = 1 - t;

		pts = new double[8];
		// Vertex 1
		pts[0] = rect3.x;
		pts[1] = srch - rect3.y;
		// Vertex 2
		pts[2] = rect3.x;
		pts[3] = srch - (rect3.y + rect3.height);
		// Vertex 3
		pts[4] = rect3.x + rect3.width;
		pts[5] = srch - (rect3.y + rect3.height);
		// Vertex 4
		pts[6] = rect3.x + rect3.width;
		pts[7] = srch - rect3.y;
		return pts;
	}

	public void scale(double sx, double sy) {
		srcw *= sx;
		srch *= sy;
		spsx *= sx;
		spsy *= sy;
		spsw *= sx;
		spsh *= sy;
	}

	public void scale(double s) {
		scale(s, s);
	}

	public void setWidth(double width) {
		double factor = width / srcw;
		srcw = width;
		spsx *= factor;
		spsw *= factor;
	}

	public void setHeight(double height) {
		double factor = height / srch;
		srch = height;
		spsy *= factor;
		spsh *= factor;
	}

	public void setSize(double width, double height) {
		setWidth(width);
		setHeight(height);
	}
}