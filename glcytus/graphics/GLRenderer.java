package glcytus.graphics;

import com.jogamp.opengl.util.texture.*;
import javax.media.opengl.*;
import static javax.media.opengl.GL.*;
import static javax.media.opengl.GL2.*;
import java.nio.*;

public class GLRenderer extends Renderer {
	static int MAX_TEXTURE_SIZE = 2048, MAX_ARRAY_TEXTURE_LAYERS = 32,
			MAX_OBJECT_COUNT = 128;
	private GL2 gl = null;
	private int texture2DArray = 0, numOfTextures = 0;

	// Normal: glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	private FloatBuffer posBuffer1 = null, colorBuffer1 = null,
			texCoordBuffer1 = null;
	private int vao1 = 0, posVBO1 = 0, colorVBO1 = 0, texCoordVBO1 = 0;
	private int objCount1 = 0;

	// Additive: glBlendFunc(GL_SRC_ALPHA, GL_ONE);
	private FloatBuffer posBuffer2 = null, colorBuffer2 = null,
			texCoordBuffer2 = null;
	private int vao2 = 0, posVBO2 = 0, colorVBO2 = 0, texCoordVBO2 = 0;
	private int objCount2 = 0;

	private int currentZPos = 0;

	public GLRenderer(GL2 gl) {
		this.gl = gl;
		gl.glEnable(GL_DEPTH_TEST);
		gl.glEnable(GL_BLEND);

		int arr[] = new int[6];
		gl.glGetIntegerv(GL_MAX_3D_TEXTURE_SIZE, arr, 0);
		gl.glGetIntegerv(GL_MAX_ARRAY_TEXTURE_LAYERS, arr, 1);
		MAX_TEXTURE_SIZE = arr[0];
		MAX_ARRAY_TEXTURE_LAYERS = arr[1];

		// Create shaders
		int vert = gl.glCreateShader(GL_VERTEX_SHADER);
		int frag = gl.glCreateShader(GL_FRAGMENT_SHADER);
		loadShader(vert, getVertexShaderCode());
		loadShader(frag, getFragmentShaderCode());
		int program = gl.glCreateProgram();
		linkProgram(program, vert, frag);
		gl.glUseProgram(program);

		int pos_loc = gl.glGetAttribLocation(program, "in_pos");
		int color_loc = gl.glGetAttribLocation(program, "in_color");
		int texCoord_loc = gl.glGetAttribLocation(program, "in_texCoord");
		int matrix_loc = gl.glGetUniformLocation(program, "matrix");
		int sampler_loc = gl.glGetUniformLocation(program, "tex");

		gl.glUniformMatrix4fv(matrix_loc, 1, false, FloatBuffer
				.wrap(createOrthoMatrix(-512, 512, 341.5, -341.5,
						MAX_OBJECT_COUNT, 0)));

		// Generate Texture2D Array
		gl.glGenTextures(1, arr, 0);
		texture2DArray = arr[0];
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D_ARRAY, texture2DArray);
		gl.glTexImage3D(GL_TEXTURE_2D_ARRAY, // Target
				0, // Mipmap Level
				GL_RGBA4, // Internal Format
				MAX_TEXTURE_SIZE, // Width
				MAX_TEXTURE_SIZE, // Height
				MAX_ARRAY_TEXTURE_LAYERS, // Depth
				0, // Border
				GL_RGBA, // Format
				GL_UNSIGNED_BYTE, // Type
				null); // Pointer to data
		gl.glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER,
				GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER,
				GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S,
				GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T,
				GL_CLAMP_TO_EDGE);
		gl.glUniform1i(sampler_loc, 0);

		// Generate VAO & VBO names
		gl.glGenVertexArrays(2, arr, 0);
		vao1 = arr[0];
		vao2 = arr[1];

		gl.glGenBuffers(6, arr, 0);
		posVBO1 = arr[0];
		colorVBO1 = arr[1];
		texCoordVBO1 = arr[2];
		posVBO2 = arr[3];
		colorVBO2 = arr[4];
		texCoordVBO2 = arr[5];

		// Buffer bindings & Data specifying
		gl.glBindVertexArray(vao1);
		gl.glBindBuffer(GL_ARRAY_BUFFER, posVBO1);
		gl.glBufferData(GL_ARRAY_BUFFER, MAX_OBJECT_COUNT * 3 * Float.SIZE,
				null, GL_DYNAMIC_DRAW);
		gl.glVertexAttribPointer(pos_loc, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(pos_loc);
		gl.glBindBuffer(GL_ARRAY_BUFFER, colorVBO1);
		gl.glBufferData(GL_ARRAY_BUFFER, MAX_OBJECT_COUNT * 4 * Float.SIZE,
				null, GL_DYNAMIC_DRAW);
		gl.glVertexAttribPointer(color_loc, 4, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(color_loc);
		gl.glBindBuffer(GL_ARRAY_BUFFER, texCoordVBO1);
		gl.glBufferData(GL_ARRAY_BUFFER, MAX_OBJECT_COUNT * 3 * Float.SIZE,
				null, GL_DYNAMIC_DRAW);
		gl.glVertexAttribPointer(texCoord_loc, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(texCoord_loc);

		gl.glBindVertexArray(vao2);
		gl.glBindBuffer(GL_ARRAY_BUFFER, posVBO2);
		gl.glBufferData(GL_ARRAY_BUFFER, MAX_OBJECT_COUNT * 3 * Float.SIZE,
				null, GL_DYNAMIC_DRAW);
		gl.glVertexAttribPointer(pos_loc, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(pos_loc);
		gl.glBindBuffer(GL_ARRAY_BUFFER, colorVBO2);
		gl.glBufferData(GL_ARRAY_BUFFER, MAX_OBJECT_COUNT * 4 * Float.SIZE,
				null, GL_DYNAMIC_DRAW);
		gl.glVertexAttribPointer(color_loc, 4, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(color_loc);
		gl.glBindBuffer(GL_ARRAY_BUFFER, texCoordVBO2);
		gl.glBufferData(GL_ARRAY_BUFFER, MAX_OBJECT_COUNT * 3 * Float.SIZE,
				null, GL_DYNAMIC_DRAW);
		gl.glVertexAttribPointer(texCoord_loc, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(texCoord_loc);

		gl.glBindVertexArray(0);
		gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

		posBuffer1 = createFloatBuffer(MAX_OBJECT_COUNT * 3);
		colorBuffer1 = createFloatBuffer(MAX_OBJECT_COUNT * 4);
		texCoordBuffer1 = createFloatBuffer(MAX_OBJECT_COUNT * 3);
		posBuffer2 = createFloatBuffer(MAX_OBJECT_COUNT * 3);
		colorBuffer2 = createFloatBuffer(MAX_OBJECT_COUNT * 4);
		texCoordBuffer2 = createFloatBuffer(MAX_OBJECT_COUNT * 3);
	}

	public void addTexture(Texture2D newTexture) {
		if (numOfTextures < MAX_ARRAY_TEXTURE_LAYERS) {
			TextureData data = newTexture.data;
			newTexture.layer = numOfTextures;
			gl.glTexSubImage3D(GL_TEXTURE_2D_ARRAY, // Target
					0, // Mipmap Level
					0, // X Offset
					0, // Y Offset
					newTexture.layer, // Z Offset
					data.getWidth(), // Width
					data.getHeight(), // Height
					1, // Depth
					data.getPixelFormat(), // Format
					data.getPixelType(), // Type
					data.getBuffer()); // Pointer to data
			numOfTextures++;
		}
	}

	public void addRenderTask(RenderTask task) {
		int layer = 0;
		/*
		 * if(task.img != null) layer = task.img.texture.layer; else layer = -1;
		 */
		FloatBuffer posBuffer = null, colorBuffer = null, texCoordBuffer = null;
		if (!task.blendingAdd) {
			posBuffer = posBuffer1;
			colorBuffer = colorBuffer1;
			texCoordBuffer = texCoordBuffer1;
			objCount1++;
		} else {
			posBuffer = posBuffer2;
			colorBuffer = colorBuffer2;
			texCoordBuffer = texCoordBuffer2;
			objCount2++;
		}
		for (int i = 0; i < 4; i++) {
			posBuffer.put((float) task.dstPts[i * 2]);
			posBuffer.put((float) task.dstPts[i * 2 + 1]);
			posBuffer.put(currentZPos);
			for (int j = 0; j < 4; j++)
				colorBuffer.put((float) task.color[j]);
			texCoordBuffer.put((float) task.texPts[i * 2]);
			texCoordBuffer.put((float) task.texPts[i * 2 + 1]);
			texCoordBuffer.put(layer);
		}
		currentZPos++;
	}

	public void flushTaskQueue() {
		// Normal
		posBuffer1.flip();
		colorBuffer1.flip();
		texCoordBuffer1.flip();
		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		gl.glBindBuffer(GL_ARRAY_BUFFER, posVBO1);
		gl.glBufferSubData(GL_ARRAY_BUFFER, 0, posBuffer1.capacity()
				* Float.SIZE, posBuffer1);
		gl.glBindBuffer(GL_ARRAY_BUFFER, colorVBO1);
		gl.glBufferSubData(GL_ARRAY_BUFFER, 0, colorBuffer1.capacity()
				* Float.SIZE, colorBuffer1);
		gl.glBindBuffer(GL_ARRAY_BUFFER, texCoordVBO1);
		gl.glBufferSubData(GL_ARRAY_BUFFER, 0, texCoordBuffer1.capacity()
				* Float.SIZE, texCoordBuffer1);
		gl.glBindVertexArray(vao1);
		gl.glDrawArrays(GL_QUADS, 0, objCount1 * 4);

		// Additive
		posBuffer2.flip();
		colorBuffer2.flip();
		texCoordBuffer2.flip();
		gl.glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE, GL_ONE, GL_ONE);
		gl.glBindBuffer(GL_ARRAY_BUFFER, posVBO2);
		gl.glBufferSubData(GL_ARRAY_BUFFER, 0, posBuffer2.capacity()
				* Float.SIZE, posBuffer2);
		gl.glBindBuffer(GL_ARRAY_BUFFER, colorVBO2);
		gl.glBufferSubData(GL_ARRAY_BUFFER, 0, colorBuffer2.capacity()
				* Float.SIZE, colorBuffer2);
		gl.glBindBuffer(GL_ARRAY_BUFFER, texCoordVBO2);
		gl.glBufferSubData(GL_ARRAY_BUFFER, 0, texCoordBuffer2.capacity()
				* Float.SIZE, texCoordBuffer2);
		gl.glBindVertexArray(vao2);
		gl.glDrawArrays(GL_QUADS, 0, objCount2 * 4);

		// Reset
		gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
		gl.glBindVertexArray(0);
		objCount1 = 0;
		objCount2 = 0;
		currentZPos = 0;
		posBuffer1.clear();
		colorBuffer1.clear();
		texCoordBuffer1.clear();
		posBuffer2.clear();
		colorBuffer2.clear();
		texCoordBuffer2.clear();
	}

	private void loadShader(int shaderName, String shaderCode) {
		int a[] = new int[2];
		gl.glShaderSource(shaderName, 1, new String[] { shaderCode },
				new int[] { shaderCode.length() }, 0);
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
		sb.append("in vec2 in_pos;\n");
		sb.append("in vec4 in_color;\n");
		sb.append("in vec3 in_texCoord;\n");
		sb.append("out vec4 color;\n");
		sb.append("out vec3 texCoord;\n");
		sb.append("void main(){\n");
		sb.append("    gl_Position = matrix * vec4(in_pos, 0, 1);\n");
		sb.append("    color = in_color;\n");
		sb.append("    texCoord = in_texCoord;\n");
		sb.append("}\n");
		return sb.toString();
	}

	private String getFragmentShaderCode() {
		StringBuilder sb = new StringBuilder();
		if (gl.isGL3())
			sb.append("#version 330 core\n");
		sb.append("uniform sampler2DArray tex;\n");
		sb.append("in vec4 color;\n");
		sb.append("in vec3 texCoord;\n");
		sb.append("out vec4 fColor;\n");
		sb.append("void main(){\n");
		sb.append("    if(texCoord.z != -1)\n");
		sb.append("        fColor = color * texture(tex, texCoord);\n");
		sb.append("    else\n");
		sb.append("        fColor = color;\n");
		sb.append("}\n");
		return sb.toString();
	}

	private FloatBuffer createFloatBuffer(int capacity) {
		ByteBuffer bytebuf = ByteBuffer.allocateDirect(capacity * Float.SIZE);
		return bytebuf.order(ByteOrder.nativeOrder()).asFloatBuffer();
	}

	private float[] createOrthoMatrix(double left, double right, double top,
			double bottom, double zNear, double zFar) {
		float mat[] = new float[16];
		// Column major: mat[col*4 + row]
		mat[0] = (float) (1.0 / ((right - left) / 2.0));
		mat[1] = 0;
		mat[2] = 0;
		mat[3] = 0;
		mat[4] = 0;
		mat[5] = (float) (1.0 / ((top - bottom) / 2.0));
		mat[6] = 0;
		mat[7] = 0;
		mat[8] = 0;
		mat[9] = 0;
		mat[10] = (float) (-1.0 / ((zFar - zNear) / 2.0));
		mat[11] = 0;
		mat[12] = (float) (-(right + left) / (right - left));
		mat[13] = (float) (-(top + bottom) / (top - bottom));
		mat[14] = (float) (-(zFar + zNear) / (zFar - zNear));
		mat[15] = 1;
		return mat;
	}
}