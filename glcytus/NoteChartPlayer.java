package glcytus;

import static javax.media.opengl.GL.GL_BLEND;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import glcytus.ext.*;
import glcytus.graphics.*;
import glcytus.util.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.*;

import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.*;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import javazoom.jl.converter.Converter;

import com.jogamp.opengl.util.texture.Texture;

public class NoteChartPlayer implements GLEventListener {
	LinkedList<Animation> animq = new LinkedList<Animation>();
	LinkedList<RenderTask> taskq = new LinkedList<RenderTask>();

	public Player mplayer = null; // Media Player
	public AdvancedGLRenderer renderer = null;

	String songtitle = null;
	NoteChart pdata = null;
	ArrayList<Note> notes = new ArrayList<Note>();
	double time = 0, beat = 0, pshift = 0;
	int page = 0, ncount = 0;

	Sprite bg = null;
	Sprite bgmask1 = null, bgmask2 = null, bgmask3 = null, bgmask3flip = null;
	Sprite title = null, titlemask = null, titlemaskflip = null;
	Sprite combosmallbg = null, combosmalltext = null;
	ComboSmallPopTransform poptrans = null;
	ComboEffect comboeffect = null;
	TextSprite fscore = null, fcombosmall = null;
	Sprite scanline = null;

	int combo = 0, maxcombo = 0;
	double score = 0, tp = 0;
	int result[] = new int[4];
	
	public JSPlayer sound = null;

	public NoteChartPlayer(String songtitle, String diff) throws Exception {
		try {
			this.songtitle = songtitle;
			String folder = "assets/songs/" + songtitle + "/";
			String chart = folder + songtitle + "." + diff + ".txt";
			BufferedReader in = new BufferedReader(new FileReader(chart));
			if (in.readLine().equals("VERSION 2"))
				pdata = NoteChartReader2.read(in);
			else
				pdata = NoteChartReader1.read(in);
			in.close();

			String music = folder + songtitle + ".mp3";
			new Converter().convert(music, "temp.wav");
			mplayer = Manager.createRealizedPlayer(new MediaLocator(new File(
					"temp.wav").toURI().toURL()));
					
			sound = new JSPlayer("assets/sounds/beat1.wav");
			for(int i=0;i<10;i++) sound.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2(); 
		gl.glViewport(0, 0, 1024, 683);
		renderer = new AdvancedGLRenderer(gl);
		try {
			GamePlaySpriteLibrary.init();
			GamePlayAnimationPreset.init();
			GamePlayFontLibrary.init();
			loadNoteChart();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			comboeffect = new ComboEffect();
			bg = new SelectCover(songtitle);
		} catch (Exception e) {
			e.printStackTrace();
			bg = new Sprite();
		}
		renderer.finish();

		bgmask1 = new Sprite("gameplay_bg_mask");
		bgmask1.setSize(1024, 683);
		bgmask1.color[3] = 0.75;

		bgmask2 = new Sprite("gameplay_bg_mask_2");
		bgmask2.setWidth(1024);
		bgmask2.setAnchor("Top");
		bgmask2.moveTo(0, 341.5);

		bgmask3 = new Sprite("gameplay_bg_mask_3");
		bgmask3.setHeight(619);
		bgmask3.setAnchor("TopLeft");
		bgmask3.moveTo(-512, 277.5);
		bgmask3.addTransform(new MaskBeatTransform(pshift, beat));

		bgmask3flip = new Sprite("gameplay_bg_mask_3");
		bgmask3flip.flipH();
		bgmask3flip.setHeight(619);
		bgmask3flip.setAnchor("TopRight");
		bgmask3flip.moveTo(512, 277.5);
		bgmask3flip.addTransform(new MaskBeatTransform(pshift, beat));

		title = new Sprite("gameplay_title");
		title.setAnchor("Top");
		title.moveTo(0, 341.5);

		titlemask = new Sprite("gameplay_title_mask");
		titlemask.setAnchor("TopLeft");
		titlemask.moveTo(-512, 341.5);

		titlemaskflip = new Sprite("gameplay_title_mask");
		titlemaskflip.flipH();
		titlemaskflip.setAnchor("TopRight");
		titlemaskflip.moveTo(512, 341.5);

		fscore = new TextSprite("BoltonBold", "0000000");
		fscore.scale(4.0 / 3.0);
		fscore.color = GamePlayFontLibrary.scorecolor;
		fscore.setAnchor("TopRight");
		fscore.moveTo(512, 337.5);

		combosmallbg = new Sprite("combo_small_bg");
		combosmallbg.moveTo(0, 273.5);

		combosmalltext = new Sprite("combo_small_text");
		combosmalltext.moveTo(-70, 273.5);

		fcombosmall = new TextSprite("ComboSmall");
		fcombosmall.moveTo(70, 271);

		poptrans = new ComboSmallPopTransform();
		combosmallbg.addTransform(poptrans);
		fcombosmall.addTransform(poptrans);

		scanline = new Sprite("bar");
	}

	public void loadNoteChart() {
		beat = pdata.beat;
		pshift = pdata.pshift;
		ncount = pdata.notes.size();
		notes.clear();
		for (NoteChart.Note pnote : pdata.notes)
			if (pnote.linkref == -1) {
				if (pnote.holdtime == 0) {
					this.notes.add(new Click(this, pnote.id, pnote.time,
							calcX(pnote.x), calcY(pnote.time)));
				} else
					this.notes.add(new Hold(this, pnote.id, pnote.time,
							pnote.holdtime, calcX(pnote.x), calcY(pnote.time)));
			} else
				this.notes.add(new Drag.Node(this, pnote.id, pnote.time,
						calcX(pnote.x), calcY(pnote.time)));
		for (NoteChart.Link plink : pdata.links) {
			Drag link = new Drag(this);
			for (int i = 0; i < plink.n; i++) {
				NoteChart.Note node = plink.nodes.get(i);
				link.nodes.add((Drag.Node) notes.get(node.id));
			}
			link.recalc();
			this.notes.add(link);
		}

		Collections.sort(notes);
		for (Note note : notes) {
			note.judge(note.stime);
			if (note instanceof Hold)
				note.judge(note.etime);
			// Nothing to do with Links
		}
	}

	public void start() {
	    while(!renderer.isInitialized());
		mplayer.start();
	}

	public double calcX(double x) {
		return x * 960 - 480;
	}

	public double calcY(double time) {
		int cpage = calcPage(time);
		double y = (time + pshift) / beat - cpage;
		if (cpage % 2 == 1)
			y = 1 - y;

		return y * 533 - 266.5;
	}

	public int calcPage(double time) {
		return (int) ((time + pshift) / beat);
	}

	public void addCombo(int judgement) {
		if (judgement == -1) { // Miss
			combo = 0;
			result[3]++;
			return;
		}
		combo++;
		double sratio = 0, tpratio = 0;
		switch (judgement) {
		case 0: // Perfect TP100
			sratio = 1;
			tpratio = 1;
			result[0]++;
			break;
		case 1: // Perfect TP70
			sratio = 1;
			tpratio = 0.7;
			result[0]++;
			break;
		case 2: // Good
			sratio = 0.7;
			tpratio = 0.3;
			result[1]++;
			break;
		case 3: // Bad
			sratio = 0.3;
			tpratio = 0;
			combo = 0;
			result[2]++;
			break;
		}
		poptrans.pop(time);
		if(combo>0)
			try{
				sound.start();
			}catch(Exception e){}
		if ((combo > 0) && (combo % 25 == 0))
			comboeffect.show(time, combo);
		if (combo > maxcombo)
			maxcombo = combo;
		score += 900000.0 / ncount * sratio + combo * 200000.0 / ncount
				/ (ncount + 1);
		tp += 100.0 / ncount * tpratio;
	}

	public void addRenderTask(RenderTask task) {
		taskq.add(task);
	}

	public void addAnimation(Animation anim) {
		animq.add(anim);
	}

	public void display(GLAutoDrawable drawable) {
		time = mplayer.getMediaTime().getSeconds();
		page = calcPage(time);
		double liney = calcY(time);

		bg.paint(renderer, 10); // 10 seconds
		bgmask1.paint(renderer, time);
		bgmask2.paint(renderer, time);
		bgmask3.paint(renderer, time);
		bgmask3flip.paint(renderer, time);
		titlemask.paint(renderer, time);
		titlemaskflip.paint(renderer, time);
		title.paint(renderer, time);
		if (combo > 1) {
			combosmallbg.paint(renderer, time);
			combosmalltext.paint(renderer, time);
			fcombosmall.setText(String.valueOf(combo));
			fcombosmall.paint(renderer, time);
			comboeffect.paint(renderer, time);
		}

		LinkedList<Animation> del = new LinkedList<Animation>();
		for (Animation anim : animq) {
			if (anim.getEndTime() < time)
				del.add(anim);
			else if (anim.getStartTime() <= time) {
				anim.paint(renderer, time);
			}
		}
		animq.removeAll(del);

		if (notes.size() > 0) {
			int i = 0;
			int end = -1;
			for (Note n : notes) {
				if (n.stime > time + beat)
					break;
				end = i++;
			}
			for (i = end; i >= 0; i--) {
				Note n = notes.get(i);
				n.paint();
			}
		}

		fscore.setText(new DecimalFormat("0000000").format(score));
		fscore.paint(renderer, time);

		scanline.moveTo(0, liney);
		scanline.paint(renderer, time);
		
		renderer.flushTaskQueue();
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
	}

	public void dispose(GLAutoDrawable drawable) {
	}
}
