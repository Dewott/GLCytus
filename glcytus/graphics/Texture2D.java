package glcytus.graphics;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;

import glcytus.util.packrect.Rect;

public final class Texture2D {
	public Texture boundTextureObject = null;
	public TextureData data = null;
	public Rect rect = null;

	public Texture2D(TextureData data) {
		this.data = data;
		rect = new Rect(getWidth(), getHeight());
	}

	public int getWidth() {
		return data.getWidth();
	}

	public int getHeight() {
		return data.getHeight();
	}
}