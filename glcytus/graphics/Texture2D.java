package glcytus.graphics;

import com.jogamp.opengl.util.texture.TextureData;

public final class Texture2D {
	public TextureData data = null;
	public int layer = 0;

	public Texture2D(TextureData data) {
		this.data = data;
	}
}