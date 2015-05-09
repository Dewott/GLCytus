package glcytus.graphics;

import static javax.media.opengl.GL.GL_ONE_MINUS_SRC_ALPHA;
import static javax.media.opengl.GL.GL_SRC_ALPHA;
import static javax.media.opengl.GL2GL3.GL_QUADS;
import glcytus.NoteChartPlayer;

import java.awt.geom.AffineTransform;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

public class TextSprite extends Sprite {
	public CFont font = null;
	public String text = "";

	public TextSprite(CFont font) {
		this.font = font;
	}

	public TextSprite(CFont font, String text) {
		this.font = font;
		setText(text);
	}
	
	public TextSprite(String fontname) {
		this(GamePlayFontLibrary.get(fontname));
	}

	public TextSprite(String fontname, String text) {
		this(GamePlayFontLibrary.get(fontname), text);
	}
	
	public void setText(String text){
	    this.text = text;
		childs.clear();
		this.w = font.getStringWidth(text);
		this.h = font.lineheight / sy;
	    double xpos = 0;
        for(int i=0;i<text.length();i++){
			CFont.CharFrame frame = font.map.get((int)text.charAt(i));
			Sprite charSprite = new Sprite(frame.img);
			charSprite.setAnchor("BottomLeft");
		    charSprite.moveTo(xpos+frame.xoff,this.h-frame.yoff-frame.h);
			addChild(charSprite,false);
			xpos += frame.xadv;
			if (i < text.length() - 1) {
				CFont.IntPair pair = new CFont.IntPair(text.charAt(i),text.charAt(i + 1));
				if (font.kernings.containsKey(pair))
				xpos += font.kernings.get(pair);
			}
	    }
	}
	
	@Override
	public void paint(Renderer r,double time){
	    updateStatus(time);
	    for(Sprite s:childs){
		    System.arraycopy(this.color, 0, s.color, 0 ,4);
		    s.paint(r,time);
		}
	}
}