package glcytus.graphics;

import static javax.media.opengl.GL.GL_ONE;
import static javax.media.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static javax.media.opengl.GL.GL_SRC_ALPHA;
import static javax.media.opengl.GL2GL3.GL_QUADS;

import javax.media.opengl.GL2;

public class RenderTask {
	public ImageHandle img = null;
	double color[] = new double[4];
	double texPts[] = new double[8];
	double dstPts[] = new double[8];

	public RenderTask(Sprite s) {
		this.img = s.img;
		double srcPts[] = new double[8];
		System.arraycopy(img.getPts(), 0, srcPts, 0, 8);
		if (s.flipH) {
			srcPts[0] = img.srcw - srcPts[0];
			srcPts[2] = img.srcw - srcPts[2];
			srcPts[4] = img.srcw - srcPts[4];
			srcPts[6] = img.srcw - srcPts[6];
		}
		if (s.flipV) {
			srcPts[1] = img.srch - srcPts[1];
			srcPts[3] = img.srch - srcPts[3];
			srcPts[5] = img.srch - srcPts[5];
			srcPts[7] = img.srch - srcPts[7];
		}
		System.arraycopy(img.texPts, 0, texPts, 0, 8);
		s.getAffineTransform().transform(srcPts, 0, dstPts, 0, 4);
		System.arraycopy(s.color, 0, color, 0, 3);
		color[3] = s.alpha;
	}

	public void paint(GL2 gl) {
		if (img.blendingAdd)
			gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE);
		else
			gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4dv(color, 0);

		gl.glBegin(GL_QUADS);
		// Vertex 1
		gl.glTexCoord2d(texPts[0], texPts[1]);
		gl.glVertex2d(dstPts[0], dstPts[1]);
		// Vertex 2
		gl.glTexCoord2d(texPts[2], texPts[3]);
		gl.glVertex2d(dstPts[2], dstPts[3]);
		// Vertex 3
		gl.glTexCoord2d(texPts[4], texPts[5]);
		gl.glVertex2d(dstPts[4], dstPts[5]);
		// Vertex 4
		gl.glTexCoord2d(texPts[6], texPts[7]);
		gl.glVertex2d(dstPts[6], dstPts[7]);
		gl.glEnd();
	}
}