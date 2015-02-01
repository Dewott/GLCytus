package glcytus.graphics;

import java.awt.geom.Rectangle2D;

public class ImageHandle {
	public Texture2D texture = null;
	public String name = "";
	public double x = 0, y = 0, w = 0, h = 0;
	public double srcw = 0, srch = 0;
	public double spsx = 0, spsy = 0, spsw = 0, spsh = 0;
	public boolean blendingAdd = false;
	public double texPts[] = null;
	public double pts[] = null;
	public boolean rotated = false;

	public double[] getPts() {
		if (pts != null) {
			return pts;
		}
		if (rotated) {
			// swap(w,h);
			double t = w;
			w = h;
			h = t;
			// swap(srcw,srch);
			t = srcw;
			srcw = srch;
			srch = t;
			// swap(spsx,spsy);
			t = spsx;
			spsx = spsy;
			spsy = t;
			// swap(spsw,spsh);
			t = spsw;
			spsw = spsh;
			spsh = t;
		}
		Rectangle2D.Double rect1 = new Rectangle2D.Double(0, 0, srcw, srch);
		Rectangle2D.Double rect2 = new Rectangle2D.Double(spsx, spsy, spsw,
				spsh);
		Rectangle2D.Double rect3 = (Rectangle2D.Double) rect1
				.createIntersection(rect2);

		texPts = new double[8];
		pts = new double[8];
		double s = x + (rect3.x - rect2.x) / spsw * w;
		double t = y + (rect3.y - rect2.y) / spsh * h;
		double s2 = s + rect3.width / spsw * w;
		double t2 = t + rect3.height / spsh * h;
		s = texture.rect.x + s;
		t = texture.rect.y + texture.rect.h - t;
		s2 = texture.rect.x + s2;
		t2 = texture.rect.y + texture.rect.h - t2;
		if (!rotated) {
			texPts[0] = s;
			texPts[1] = t;
			texPts[2] = s;
			texPts[3] = t2;
			texPts[4] = s2;
			texPts[5] = t2;
			texPts[6] = s2;
			texPts[7] = t;
		} else {
			texPts[0] = s2;
			texPts[1] = t;
			texPts[2] = s;
			texPts[3] = t;
			texPts[4] = s;
			texPts[5] = t2;
			texPts[6] = s2;
			texPts[7] = t2;
		}

		if (!rotated) {
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
		} else {
			// Vertex 1
			pts[0] = rect3.y;
			pts[1] = srcw - rect3.x;
			// Vertex 2
			pts[2] = rect3.y;
			pts[3] = srcw - (rect3.x + rect3.width);
			// Vertex 3
			pts[4] = rect3.y + rect3.height;
			pts[5] = srcw - (rect3.x + rect3.width);
			// Vertex 4
			pts[6] = rect3.y + rect3.height;
			pts[7] = srcw - rect3.x;
		}

		if (rotated) {
			// restore
			// swap(w,h);
			double tt = w;
			w = h;
			h = tt;
			// swap(srcw,srch);
			tt = srcw;
			srcw = srch;
			srch = tt;
			// swap(spsx,spsy);
			tt = spsx;
			spsx = spsy;
			spsy = tt;
			// swap(spsw,spsh);
			tt = spsw;
			spsw = spsh;
			spsh = tt;
		}
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