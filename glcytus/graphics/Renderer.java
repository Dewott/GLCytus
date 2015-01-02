package glcytus.graphics;

public abstract class Renderer {
	public abstract void addRenderTask(RenderTask task);

	public abstract void flushTaskQueue();
}