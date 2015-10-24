package glcytus.graphics;

import static javax.media.opengl.GL.GL_BLEND;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_LEQUAL;
import static javax.media.opengl.GL.GL_ONE;
import static javax.media.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static javax.media.opengl.GL.GL_SRC_ALPHA;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL2GL3.GL_QUADS;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

import java.util.ArrayList;
import java.util.Arrays;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;

public class BasicGLRenderer extends Renderer {
	public static int MAX_OBJECT_COUNT = 128;
	private GL2 gl = null;
	private ArrayList<RenderTask> taskQueue = new ArrayList<RenderTask>();

	public BasicGLRenderer(GL2 gl) {
		this.gl = gl;
		currentInstance = this;

		gl.glViewport(0, 0, 1024, 683);
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(-512, 512, -341.5, 341.5, 0, MAX_OBJECT_COUNT);
		gl.glClearColor(1, 1, 1, 1);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glEnable(GL_BLEND);
		gl.glEnable(GL_TEXTURE_2D);
	}

	public void addRenderTask(RenderTask task) {
		taskQueue.add(task);
	}

	public void addTexture(Texture2D texture) {
		// Auto binding on construction
		texture.boundTextureObject = new Texture(gl, texture.data);
	}

	public void flushTaskQueue() {
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		boolean v[] = new boolean[taskQueue.size()];
		Arrays.fill(v, true);
		int remaining = v.length;
		while (remaining > 0) {
			int i = 0;
			for (i = 0; i < v.length; i++)
				if (v[i])
					break;
			// if(remaining > 0) i != v.length
			RenderTask task = taskQueue.get(i);
			if (task.img == null) {
				gl.glBindTexture(GL_TEXTURE_2D, 0);
				gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
				gl.glColor4dv(task.color, 0);
				gl.glBegin(GL_QUADS);
				// Vertex 1
				gl.glVertex3d(task.dstPts[0], task.dstPts[1], -MAX_OBJECT_COUNT + i);
				// Vertex 2
				gl.glVertex3d(task.dstPts[2], task.dstPts[3], -MAX_OBJECT_COUNT + i);
				// Vertex 3
				gl.glVertex3d(task.dstPts[4], task.dstPts[5], -MAX_OBJECT_COUNT + i);
				// Vertex 4
				gl.glVertex3d(task.dstPts[6], task.dstPts[7], -MAX_OBJECT_COUNT + i);
				gl.glEnd();
				gl.glFlush();
				v[i] = false;
				remaining--;
			} else {
				Texture currentTexture = task.img.texture.boundTextureObject;
				currentTexture.bind(gl);
				for (int j = 0; j < v.length; j++)
					if (taskQueue.get(j).img.texture.boundTextureObject == currentTexture) {
						// v[j] = true;
						task = taskQueue.get(j);
						if (task.blendingAdd)
							gl.glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE, GL_ONE, GL_ONE);
						else
							gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
						gl.glColor4dv(task.color, 0);
						gl.glBegin(GL_QUADS);
						// Vertex 1
						gl.glTexCoord2d(task.texPts[0], task.texPts[1]);
						gl.glVertex3d(task.dstPts[0], task.dstPts[1], -MAX_OBJECT_COUNT + j);
						// Vertex 2
						gl.glTexCoord2d(task.texPts[2], task.texPts[3]);
						gl.glVertex3d(task.dstPts[2], task.dstPts[3], -MAX_OBJECT_COUNT + j);
						// Vertex 3
						gl.glTexCoord2d(task.texPts[4], task.texPts[5]);
						gl.glVertex3d(task.dstPts[4], task.dstPts[5], -MAX_OBJECT_COUNT + j);
						// Vertex 4
						gl.glTexCoord2d(task.texPts[6], task.texPts[7]);
						gl.glVertex3d(task.dstPts[6], task.dstPts[7], -MAX_OBJECT_COUNT + j);
						gl.glEnd();
						gl.glFlush();
						v[j] = false;
						remaining--;
					}
				gl.glFlush();
			}
		}
		taskQueue.clear();
	}
}