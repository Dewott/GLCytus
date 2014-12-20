package glcytus;

import static javax.media.opengl.GL.GL_BLEND;
import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import glcytus.ext.SelectCover;
import glcytus.graphics.Animation;
import glcytus.graphics.FontSprite;
import glcytus.graphics.GamePlayAnimationPreset;
import glcytus.graphics.GamePlayFontLibrary;
import glcytus.graphics.GamePlaySpriteLibrary;
import glcytus.graphics.RenderTask;
import glcytus.graphics.Sprite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.LinkedList;

import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import javazoom.jl.converter.Converter;

import com.jogamp.opengl.util.texture.Texture;

public class NoteChartPlayer implements GLEventListener {
	LinkedList<Animation> animq = new LinkedList<Animation>();
	LinkedList<RenderTask> taskq = new LinkedList<RenderTask>();

	Player mplayer = null; // Media Player

	String songtitle = null;
	NoteChart pdata = null;
	LinkedList<Note> notes = new LinkedList<Note>();
	double time = 0, beat = 0, pshift = 0;
	int page = 0, ncount = 0;

	Sprite scanline = null;
	Sprite bg = null;
	Sprite bgmask1 = null, bgmask2 = null, bgmask3 = null, bgmask3flip = null;
	Sprite title = null, titlemask = null, titlemaskflip = null;
	FontSprite fscore = null;

	int combo = 0, maxcombo = 0;
	double score = 0, tp = 0;
	int result[] = new int[4];

	public NoteChartPlayer(String songtitle, String diff) throws Exception {
		try {
			this.songtitle = songtitle;
			String folder = "Application/assets/songs/" + songtitle + "/";
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
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glViewport(0, 0, 1024, 683);
		gl.glOrtho(-512, 512, -341.5, 341.5, 1, -1);
		gl.glClearColor(1, 1, 1, 1);
		gl.glEnable(GL_BLEND);
		gl.glEnable(GL_TEXTURE_2D);
		gl.glDisable(GL_DEPTH_TEST);
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
			bg = new SelectCover(songtitle);
		} catch (Exception e) {
			System.out.println("SelectCover load failed");
			bg = new Sprite();
		}

		bgmask1 = new Sprite("gameplay_bg_mask");
		bgmask1.setSize(1024, 683);
		bgmask1.alpha = 0.75;

		bgmask2 = new Sprite("gameplay_bg_mask_2");
		bgmask2.setWidth(1024);
		bgmask2.setAnchor("Top");
		bgmask2.moveTo(0, 341.5);

		bgmask3 = new Sprite("gameplay_bg_mask_3");
		bgmask3.setHeight(614.7);
		bgmask3.setAnchor("TopLeft");
		bgmask3.moveTo(-512, 273.5);

		bgmask3flip = new Sprite("gameplay_bg_mask_3");
		bgmask3flip.flipH();
		bgmask3flip.setHeight(614.7);
		bgmask3flip.setAnchor("TopRight");
		bgmask3flip.moveTo(512, 273.5);

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

		fscore = new FontSprite("BoltonBold", "0000000");
		fscore.scale(4.0 / 3.0);
		fscore.color = GamePlayFontLibrary.scorecolor;
		fscore.setAnchor("TopRight");
		fscore.moveTo(512, 341.5);

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
		GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL_COLOR_BUFFER_BIT);

		time = mplayer.getMediaTime().getSeconds();
		page = calcPage(time);
		double liney = calcY(time);

		bg.paint(this, 10); // 10 seconds
		bgmask1.paint(this, time);
		bgmask2.paint(this, time);
		bgmask3.paint(this, time);
		bgmask3flip.paint(this, time);
		titlemask.paint(this, time);
		titlemaskflip.paint(this, time);
		title.paint(this, time);

		LinkedList<Animation> del = new LinkedList<Animation>();
		for (int i = 0; i < animq.size(); i++) {
			Animation anim = animq.get(i);
			if (anim.getEndTime() < time)
				del.add(anim);
			else if (anim.getStartTime() <= time) {
				anim.paint(this, time);
				flushTaskQueue(gl);
			}
		}
		animq.removeAll(del);

		if (notes.size() > 0) {
			int end = -1;
			int i = 0;
			for (i = 0; i < notes.size(); i++) {
				Note n = notes.get(i);
				if (n.stime > time + beat)
					break;
				end = i;
			}
			for (i = end; i >= 0; i--) {
				Note n = notes.get(i);
				n.paint();
				flushTaskQueue(gl);
			}
		}

		fscore.text = new DecimalFormat("0000000").format(score);
		fscore.paint(gl);

		scanline.moveTo(0, liney);
		scanline.paint(this, time);

		flushTaskQueue(gl);
	}

	public void flushTaskQueue(GL2 gl) {
		Texture cur = null;
		LinkedList<RenderTask> del = new LinkedList<RenderTask>();
		while (taskq.size() > 0) {
			cur = taskq.getFirst().img.texture;
			cur.bind(gl);
			for (RenderTask task : taskq)
				if (cur == task.img.texture) {
					task.paint(gl);
					del.add(task);
				}
			taskq.removeAll(del);
			del.clear();
		}
		gl.glFlush();
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
	}

	public void dispose(GLAutoDrawable drawable) {
	}
}
