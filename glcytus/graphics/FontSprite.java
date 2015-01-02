package glcytus.graphics;

import static javax.media.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static javax.media.opengl.GL.GL_SRC_ALPHA;
import static javax.media.opengl.GL2GL3.GL_QUADS;
import glcytus.NoteChartPlayer;

import java.awt.geom.AffineTransform;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

public class FontSprite extends Sprite {
	public CFont font = null;
	public CFont scaledfont = null;
	public String text = "";

	public FontSprite(CFont font) {
		this.font = font;
	}

	public FontSprite(CFont font, String text) {
		this.font = font;
		this.text = text;
	}

	public FontSprite(String fontname, String text) {
		this(GamePlayFontLibrary.get(fontname), text);
	}

	public FontSprite(String fontname) {
		this(GamePlayFontLibrary.get(fontname));
	}

	public void paint(NoteChartPlayer p, double time) {
		updateStatus(time);
		GL2 gl = GLU.getCurrentGL().getGL2();
		p.flushTaskQueue(gl);
		paint(gl);
	}

	public void paint(GL2 gl) {
		AffineTransform tx = getAffineTransform();
		if ((scaledfont == null) || (scaledfont.sx != tx.getScaleX())
				|| (scaledfont.sy != tx.getScaleY()))
			scaledfont = font.getScaledFont(tx.getScaleX(), tx.getScaleY());
		// TopLeft position
		double xpos = tx.getTranslateX() - ax * scaledfont.getStringWidth(text);
		double ypos = tx.getTranslateY() + (1 - ay) * scaledfont.lineheight;
		font.texture.bind(gl);
		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		double alpha = getFinalAlpha();
		gl.glColor4d(color[0], color[1], color[2], alpha);

		for (int i = 0; i < text.length(); i++) {
			CFont.CharFrame frame = scaledfont.map.get((int) text.charAt(i));
			gl.glBegin(GL_QUADS);
			// Vertex 1
			gl.glTexCoord2d(frame.tx, 1 - frame.ty);
			gl.glVertex2d(xpos + frame.xoff, ypos - frame.yoff);
			// Vertex 2
			gl.glTexCoord2d(frame.tx, 1 - (frame.ty + frame.th));
			gl.glVertex2d(xpos + frame.xoff, ypos - frame.h - frame.yoff);
			// Vertex 3
			gl.glTexCoord2d(frame.tx + frame.tw, 1 - (frame.ty + frame.th));
			gl.glVertex2d(xpos + frame.w + frame.xoff, ypos - frame.h
					- frame.yoff);
			// Vertex 4
			gl.glTexCoord2d(frame.tx + frame.tw, 1 - frame.ty);
			gl.glVertex2d(xpos + frame.w + frame.xoff, ypos - frame.yoff);
			gl.glEnd();

			xpos += frame.xadv;
			if (i < text.length() - 1) {
				CFont.IntPair pair = new CFont.IntPair(text.charAt(i),
						text.charAt(i + 1));
				if (scaledfont.kernings.containsKey(pair))
					xpos += scaledfont.kernings.get(pair);
			}
		}
	}
}