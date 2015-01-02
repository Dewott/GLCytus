package glcytus.graphics;

import static javax.media.opengl.GL.GL_ONE;
import static javax.media.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static javax.media.opengl.GL.GL_SRC_ALPHA;
import static javax.media.opengl.GL2GL3.GL_QUADS;

import javax.media.opengl.GL2;

public class RenderTask {
	public ImageHandle img = null;
	public boolean blendingAdd = false;
	public double color[] = new double[4];
	public double texPts[] = new double[8];
	public double dstPts[] = new double[8];

	public RenderTask(Sprite s) {
		double srcPts[] = new double[8];
		if (s.img != null) {
			this.img = s.img;
			blendingAdd = s.img.blendingAdd;
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
		} else {
			srcPts[0] = 0;
			srcPts[1] = s.h;
			srcPts[2] = 0;
			srcPts[3] = 0;
			srcPts[4] = s.w;
			srcPts[5] = 0;
			srcPts[6] = s.w;
			srcPts[7] = s.h;
		}
		s.getAffineTransform().transform(srcPts, 0, dstPts, 0, 4);
		System.arraycopy(s.color, 0, color, 0, 3);
		color[3] = s.getFinalAlpha();
	}

	public void paint(GL2 gl) {
		gl.glColor4dv(color, 0);
		if (img != null) {
			if (blendingAdd)
				gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE);
			else
				gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

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
		} else {
			gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			gl.glBegin(GL_QUADS);
			// Vertex 1
			gl.glVertex2d(dstPts[0], dstPts[1]);
			// Vertex 2
			gl.glVertex2d(dstPts[2], dstPts[3]);
			// Vertex 3
			gl.glVertex2d(dstPts[4], dstPts[5]);
			// Vertex 4
			gl.glVertex2d(dstPts[6], dstPts[7]);
			gl.glEnd();
		}
	}
}