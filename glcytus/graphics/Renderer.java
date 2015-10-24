package glcytus.graphics;

import glcytus.graphics.Texture2D;

public abstract class Renderer {
	public static Renderer currentInstance = null;

	public abstract void addRenderTask(RenderTask task);

	public abstract void addTexture(Texture2D texture);

	public abstract void flushTaskQueue();
}