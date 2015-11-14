package glcytus.graphics;

import static javax.media.opengl.GL.GL_ARRAY_BUFFER;
import static javax.media.opengl.GL.GL_BLEND;
import static javax.media.opengl.GL.GL_CLAMP_TO_EDGE;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DYNAMIC_DRAW;
import static javax.media.opengl.GL.GL_FLOAT;
import static javax.media.opengl.GL.GL_LINEAR;
import static javax.media.opengl.GL.GL_ONE;
import static javax.media.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static javax.media.opengl.GL.GL_RGBA;
import static javax.media.opengl.GL.GL_RGBA4;
import static javax.media.opengl.GL.GL_SRC_ALPHA;
import static javax.media.opengl.GL.GL_TEXTURE0;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_S;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_T;
import static javax.media.opengl.GL.GL_UNPACK_ALIGNMENT;
import static javax.media.opengl.GL.GL_UNSIGNED_BYTE;
import static javax.media.opengl.GL2ES2.GL_COMPILE_STATUS;
import static javax.media.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static javax.media.opengl.GL2ES2.GL_INFO_LOG_LENGTH;
import static javax.media.opengl.GL2ES2.GL_LINK_STATUS;
import static javax.media.opengl.GL2ES2.GL_VERTEX_SHADER;
import static javax.media.opengl.GL2GL3.GL_QUADS;
import glcytus.util.packrect.RectPacker;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import javax.media.opengl.DebugGL2;
import javax.media.opengl.GL2;

import com.jogamp.opengl.math.FloatUtil;

public class AdvancedGLRenderer extends Renderer {
	public static int MAX_OBJECT_COUNT = 512;
	private GL2 gl = null;
	private int textureSize = 4096;
	private RectPacker packer = new RectPacker(textureSize, textureSize);
	private LinkedList<Texture2D> textures = new LinkedList<Texture2D>();

	private FloatBuffer buffer = null;
	private int shaderProgram = 0;
	private int vao = 0, vbo = 0, tex_loc = 0;
	private int drawCalls = 0;
	private int startIndex[] = new int[MAX_OBJECT_COUNT];
	private int vertexCount[] = new int[MAX_OBJECT_COUNT];
	private int texNames[] = new int[MAX_OBJECT_COUNT];
	private int currentIndex = 0;

	boolean initialized = false;

	public AdvancedGLRenderer(GL2 gl) {
		this.gl = new DebugGL2(gl).getGL().getGL2();
		currentInstance = this;

		gl.glClearColor(1, 1, 1, 1);
		gl.glEnable(GL_BLEND);

		// Create shaders
		int vert = gl.glCreateShader(GL_VERTEX_SHADER);
		int frag = gl.glCreateShader(GL_FRAGMENT_SHADER);
		loadShader(vert, getVertexShaderCode());
		loadShader(frag, getFragmentShaderCode());
		shaderProgram = gl.glCreateProgram();
		linkProgram(shaderProgram, vert, frag);
		gl.glUseProgram(shaderProgram);

		int pos_loc = gl.glGetAttribLocation(shaderProgram, "in_pos");
		int color_loc = gl.glGetAttribLocation(shaderProgram, "in_color");
		int texCoord_loc = gl.glGetAttribLocation(shaderProgram, "in_texCoord");
		int matrix_loc = gl.glGetUniformLocation(shaderProgram, "matrix");

		float mat[] = new float[16];
		FloatUtil.makeOrtho(mat, 0, true, -512f, 512f, -341.5f, 341.5f, 1f, -1f);
		gl.glUniformMatrix4fv(matrix_loc, 1, false, FloatBuffer.wrap(mat));

		// Generate VAO & VBO names
		int arr[] = new int[1];
		gl.glGenVertexArrays(1, arr, 0);
		vao = arr[0];

		gl.glGenBuffers(1, arr, 0);
		vbo = arr[0];

		// Buffer bindings & Data specifying
		gl.glBindVertexArray(vao);
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo);
		gl.glBufferData(GL_ARRAY_BUFFER, MAX_OBJECT_COUNT * 144, null, GL_DYNAMIC_DRAW);
		gl.glVertexAttribPointer(pos_loc, 2, GL_FLOAT, false, 36, 0);
		gl.glEnableVertexAttribArray(pos_loc);
		gl.glVertexAttribPointer(color_loc, 4, GL_FLOAT, false, 36, 8);
		gl.glEnableVertexAttribArray(color_loc);
		gl.glVertexAttribPointer(texCoord_loc, 3, GL_FLOAT, false, 36, 24);
		gl.glEnableVertexAttribArray(texCoord_loc);

		gl.glBindVertexArray(0);
		gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

		buffer = createFloatBuffer(MAX_OBJECT_COUNT * 144);
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void addTexture(Texture2D newTexture) {
		textures.add(newTexture);
		packer.add(newTexture.rect);
	}

	public void finish() {
		packer.doLayout();
		int n = packer.getMaxLayer() + 1;
		Collections.sort(textures, new Comparator<Texture2D>() {
			public int compare(Texture2D t1, Texture2D t2) {
				return t1.rect.layer - t2.rect.layer;
			}
		});
		int texName[] = new int[n];
		gl.glGenTextures(n, texName, 0);
		gl.glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

		int i = -1;
		for (Texture2D tex : textures) {
			if (i < tex.rect.layer) {
				i = tex.rect.layer;
				gl.glActiveTexture(GL_TEXTURE0 + i);
				gl.glBindTexture(GL_TEXTURE_2D, texName[i]);
				gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, textureSize, textureSize, 0, GL_RGBA, GL_UNSIGNED_BYTE,
						null);
				gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
				gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
				gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
				gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			}
			gl.glTexSubImage2D(GL_TEXTURE_2D, 0, tex.rect.x, tex.rect.y, tex.rect.w, tex.rect.h,
					tex.data.getPixelFormat(), tex.data.getPixelType(), tex.data.getBuffer());
		}

		/*
		 * gl.glActiveTexture(GL_TEXTURE0); ByteBuffer
		 * buf=ByteBuffer.allocateDirect
		 * (67108864).order(ByteOrder.nativeOrder());
		 * gl.glGetTexImage(GL_TEXTURE_2D,0,GL_RGBA,GL_UNSIGNED_BYTE,buf);
		 * TextureData data = new
		 * TextureData(GLProfile.getDefault(),GL_RGBA,4096
		 * ,4096,0,GL_RGBA,GL_UNSIGNED_BYTE,false,false,false,buf,null); try{
		 * TextureIO.write(data,new java.io.File("out.png")); }catch(Exception
		 * e){} System.exit(1);
		 */

		tex_loc = gl.glGetUniformLocation(shaderProgram, "tex");
		/*
		 * //loc(tex[i])=loc(tex[0])+i for(i=0;i<n;i++)
		 * gl.glUniform1i(tex_loc+i,i);
		 */
		// gl.glUniform1i(tex_loc, 0);

		int texSize_loc = gl.glGetUniformLocation(shaderProgram, "textureSize");
		gl.glUniform1f(texSize_loc, textureSize);

		System.out.println("Initialized");
		initialized = true;
	}

	public void addRenderTask(RenderTask task) {
		if ((task.blendingAdd && (drawCalls % 2 == 0)) || ((!task.blendingAdd) && (drawCalls % 2 == 1))) {
			drawCalls++;
			startIndex[drawCalls] = currentIndex;
			texNames[drawCalls] = task.img.texture.rect.layer;
		} else if ((task.img != null) && (texNames[drawCalls] != task.img.texture.rect.layer)) {
			startIndex[drawCalls + 1] = currentIndex;
			texNames[drawCalls + 1] = 0;

			drawCalls += 2;
			startIndex[drawCalls] = currentIndex;
			texNames[drawCalls] = task.img.texture.rect.layer;
		}

		for (int i = 0; i < 4; i++) {
			buffer.put((float) task.dstPts[i * 2]);
			buffer.put((float) task.dstPts[i * 2 + 1]);
			for (int j = 0; j < 4; j++)
				buffer.put((float) task.color[j]);
			buffer.put((float) task.texPts[i * 2]);
			buffer.put((float) task.texPts[i * 2 + 1]);
			if (task.img != null)
				buffer.put((float) task.img.texture.rect.layer);
			else
				buffer.put(-1f);
		}
		currentIndex += 4;
		vertexCount[drawCalls] += 4;
	}

	public void flushTaskQueue() {
		gl.glBindVertexArray(vao);
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo);
		buffer.flip();
		gl.glBufferSubData(GL_ARRAY_BUFFER, 0, currentIndex * 36, buffer);
		for (int i = 0; i <= drawCalls; i++) {
			gl.glUniform1i(tex_loc, texNames[i]);
			if (i % 2 == 0)
				gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			else
				gl.glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE, GL_ONE, GL_ONE);
			gl.glDrawArrays(GL_QUADS, startIndex[i], vertexCount[i]);
		}
		gl.glFlush();

		// Reset
		gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
		gl.glBindVertexArray(0);
		currentIndex = 0;
		drawCalls = 0;
		Arrays.fill(startIndex, 0);
		Arrays.fill(vertexCount, 0);
		buffer.clear();
	}

	private void loadShader(int shaderName, String shaderCode) {
		int a[] = new int[2];
		gl.glShaderSource(shaderName, 1, new String[] { shaderCode }, new int[] { shaderCode.length() }, 0);
		gl.glCompileShader(shaderName);
		gl.glGetShaderiv(shaderName, GL_COMPILE_STATUS, a, 0);
		gl.glGetShaderiv(shaderName, GL_INFO_LOG_LENGTH, a, 1);
		if (a[0] == 0) {
			ByteBuffer bytebuf = ByteBuffer.wrap(new byte[a[1]]);
			gl.glGetShaderInfoLog(shaderName, a[1], null, bytebuf);
			System.err.println(new String(bytebuf.array()));
		} else
			System.err.println("Compile OK");
	}

	private void linkProgram(int programName, int vertName, int fragName) {
		int a[] = new int[2];
		gl.glAttachShader(programName, vertName);
		gl.glAttachShader(programName, fragName);
		gl.glLinkProgram(programName);

		gl.glGetProgramiv(programName, GL_LINK_STATUS, a, 0);
		gl.glGetProgramiv(programName, GL_INFO_LOG_LENGTH, a, 1);
		if (a[0] == 0) {
			ByteBuffer bytebuf = ByteBuffer.wrap(new byte[a[1]]);
			gl.glGetProgramInfoLog(programName, a[1], null, bytebuf);
			System.err.println(new String(bytebuf.array()));
		} else
			System.err.println("Link OK");
	}

	private String getVertexShaderCode() {
		StringBuilder sb = new StringBuilder();
		if (gl.isGL3())
			sb.append("#version 330 core\n");
		sb.append("uniform mat4 matrix;\n");
		sb.append("uniform float textureSize;\n");
		sb.append("in vec2 in_pos;\n");
		sb.append("in vec4 in_color;\n");
		sb.append("in vec3 in_texCoord;\n");
		sb.append("out vec4 color;\n");
		sb.append("out vec3 texCoord;\n");
		sb.append("void main(){\n");
		sb.append("    gl_Position = matrix * vec4(in_pos, 0, 1);\n");
		sb.append("    color = in_color;\n");
		sb.append("    texCoord = in_texCoord / textureSize;\n");
		sb.append("}\n");
		return sb.toString();
	}

	private String getFragmentShaderCode() {
		StringBuilder sb = new StringBuilder();
		if (gl.isGL3())
			sb.append("#version 330 core\n");
		sb.append("uniform sampler2D tex;\n");
		sb.append("in vec4 color;\n");
		sb.append("in vec3 texCoord;\n");
		sb.append("out vec4 fColor;\n");
		sb.append("void main(){\n");
		sb.append("    if(color.a < 0.01) discard;\n");
		sb.append("    if(texCoord.p >= 0)\n");
		sb.append("        fColor = color * texture(tex, texCoord.st);\n");
		sb.append("    else\n");
		sb.append("        fColor = color;\n");
		sb.append("}\n");
		return sb.toString();
	}

	private FloatBuffer createFloatBuffer(int capacity) {
		ByteBuffer bytebuf = ByteBuffer.allocateDirect(capacity * 4);
		return bytebuf.order(ByteOrder.nativeOrder()).asFloatBuffer();
	}
}