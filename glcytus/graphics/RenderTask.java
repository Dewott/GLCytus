package glcytus.graphics;
import glcytus.util.packrect.*;
import static javax.media.opengl.GL.GL_ONE;
import static javax.media.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static javax.media.opengl.GL.GL_SRC_ALPHA;
import static javax.media.opengl.GL2GL3.GL_QUADS;
import com.jogamp.opengl.math.*;

import javax.media.opengl.GL2;

public class RenderTask {
	public ImageHandle img = null;
	public boolean blendingAdd = false;
	public double color[] = new double[4];
	public double texPts[] = new double[8];
	public double dstPts[] = new double[8];

	public RenderTask(Sprite s) {
		double srcPts[] = new double[8];
		this.blendingAdd = s.blendingAdd;
		if (s.img != null) {
			this.img = s.img;
			blendingAdd |= s.img.blendingAdd;
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
		Matrix4 mat = s.getTransformMatrix();
		for(int i=0;i<4;i++){
		    float src[] = new float[]{(float)srcPts[i*2],(float)srcPts[i*2+1],0f,1f};
			float dst[] = new float[4];
		    mat.multVec(src,dst);
		    dstPts[i*2] = dst[0];
			dstPts[i*2+1] = dst[1];
		}
		System.arraycopy(s.color, 0, color, 0, 3);
		color[3] = s.getFinalAlpha();
	}
}