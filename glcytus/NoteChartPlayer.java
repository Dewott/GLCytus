package glcytus;

import static com.jogamp.openal.ALConstants.AL_BUFFER;
import static com.jogamp.openal.ALConstants.AL_FALSE;
import static com.jogamp.openal.ALConstants.AL_GAIN;
import static com.jogamp.openal.ALConstants.AL_LOOPING;
import static com.jogamp.openal.ALConstants.AL_PITCH;
import static com.jogamp.openal.ALConstants.AL_POSITION;
import static com.jogamp.openal.ALConstants.AL_SEC_OFFSET;
import static com.jogamp.openal.ALConstants.AL_SOURCE_STATE;
import static com.jogamp.openal.ALConstants.AL_STOPPED;
import static com.jogamp.openal.ALConstants.AL_VELOCITY;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import com.jogamp.openal.AL;
import com.jogamp.openal.ALFactory;
import com.jogamp.openal.util.ALut;
import com.jogamp.openal.util.WAVData;
import com.jogamp.openal.util.WAVLoader;

import glcytus.ext.ComboEffect;
import glcytus.ext.Difficulty;
import glcytus.ext.IconText;
import glcytus.ext.SelectCover;
import glcytus.graphics.AdvancedGLRenderer;
import glcytus.graphics.Animation;
import glcytus.graphics.ComboSmallPopTransform;
import glcytus.graphics.GamePlayAnimationPreset;
import glcytus.graphics.GamePlayFontLibrary;
import glcytus.graphics.GamePlaySpriteLibrary;
import glcytus.graphics.MaskBeatTransform;
import glcytus.graphics.RenderTask;
import glcytus.graphics.Sprite;
import glcytus.graphics.TextSprite;
import javazoom.jl.converter.Converter;

public class NoteChartPlayer implements GLEventListener {
	LinkedList<Animation> animq = new LinkedList<Animation>();
	LinkedList<RenderTask> taskq = new LinkedList<RenderTask>();

	public AdvancedGLRenderer renderer = null;
	static AL al = null;

	String songtitle = "", diff = "";
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
	Sprite icontext = null;
	Sprite difficulty = null;

	int combo = 0, maxcombo = 0;
	double score = 0, tp = 0;
	int result[] = new int[4];

	static LinkedList<Integer> snd_srcs = new LinkedList<Integer>();
	int mplayer = -1, bgm = -1, sound = -1;
	boolean started = false, stopped = true;

	double lastMediaTime = 0;
	long lastUpdateTime = 0;

	public NoteChartPlayer(String songtitle, String diff) throws Exception {
		try {
			this.songtitle = songtitle;
			this.diff = diff;
			String folder = "assets/songs/" + songtitle + "/";
			String chart = folder + songtitle + "." + diff + ".txt";
			BufferedReader in = new BufferedReader(new FileReader(chart));
			if (in.readLine().equals("VERSION 2"))
				pdata = NoteChartReader2.read(in);
			else
				pdata = NoteChartReader1.read(in);
			in.close();

			ALut.alutInit();
			al = ALFactory.getAL();

			String music = folder + songtitle + ".mp3";
			if (Preferences.convertMP3 == 1) {
				if (new File("temp.wav").exists())
					new File("temp.wav").delete();
				new Converter().convert(music, "temp.wav");
			}

			bgm = loadSoundFile(new File("temp.wav"));

			if (Preferences.clickfx == 1)
				sound = loadSoundFile(new File("assets/sounds/beat1.wav"));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private int loadSoundFile(File f) {
		int a[] = new int[1];
		al.alGenBuffers(1, a, 0);
		WAVData wavdata = null;
		try {
			wavdata = WAVLoader.loadFromFile(f.getPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		al.alBufferData(a[0], wavdata.format, wavdata.data, wavdata.size, wavdata.freq);
		return a[0];
	}

	public int playSound(int handle, float gain) {
		if (handle == -1)
			return -1;
		int a[] = new int[1];
		al.alGenSources(1, a, 0);
		int src = a[0];
		al.alSourcei(src, AL_BUFFER, handle);
		al.alSourcef(src, AL_PITCH, 1.0f);
		al.alSourcef(src, AL_GAIN, gain);
		al.alSourcefv(src, AL_POSITION, new float[] { 0, 0, 0 }, 0);
		al.alSourcefv(src, AL_VELOCITY, new float[] { 0, 0, 0 }, 0);
		al.alSourcei(src, AL_LOOPING, AL_FALSE);
		al.alSourcePlay(src);
		snd_srcs.add(src);
		return src;
	}

	private double getMediaTime(int handle) {
		float time[] = new float[1];
		al.alGetSourcef(handle, AL_SEC_OFFSET, time, 0);
		return (double) time[0];
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
			icontext = new IconText(songtitle);
			difficulty = new Difficulty(songtitle, diff);
		} catch (Exception e) {
			e.printStackTrace();
			if (bg == null)
				bg = new Sprite();
			if (icontext == null)
				icontext = new Sprite();
			if (difficulty == null)
				difficulty = new Sprite();
		}
		renderer.finish();

		bgmask1 = new Sprite("gameplay_bg_mask");
		bgmask1.setSize(1280, 853);
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

		icontext.scale(0.8);
		double bounds[] = icontext.getBounds();
		icontext.moveTo(-492 - bounds[0], -320 - bounds[2]);

		difficulty.moveTo(440, -292);

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
					this.notes.add(new Click(this, pnote.id, pnote.time, calcX(pnote.x), calcY(pnote.time)));
				} else
					this.notes.add(
							new Hold(this, pnote.id, pnote.time, pnote.holdtime, calcX(pnote.x), calcY(pnote.time)));
			} else
				this.notes.add(new Drag.Node(this, pnote.id, pnote.time, calcX(pnote.x), calcY(pnote.time)));
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
		// while (!renderer.isInitialized());
		started = true;
		stopped = false;
		mplayer = playSound(bgm, (float) (Preferences.bgmGain / 10.0));
	}

	public double calcX(double x) {
		return x * 960 - 480;
	}

	public double calcY(double time) {
		int cpage = calcPage(time);
		double y = (time + pshift) / beat - cpage;
		if (cpage % 2 == 1)
			y = 1 - y;

		return y * 546.4 - 273.2;
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
		if (combo > 0)
			playSound(sound, (float) (Preferences.fxGain / 10.0));
		if ((combo > 0) && (combo % 25 == 0))
			comboeffect.show(time, combo);
		if (combo > maxcombo)
			maxcombo = combo;
		score += 900000.0 / ncount * sratio + combo * 200000.0 / ncount / (ncount + 1);
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
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

		int a[] = new int[1];
		LinkedList<Integer> delList = new LinkedList<Integer>();
		for (Integer i : snd_srcs) {
			al.alGetSourcei(i, AL_SOURCE_STATE, a, 0);
			if (a[0] == AL_STOPPED) {
				if ((i == mplayer) && started) {
					started = false;
					stopped = true;
					continue;
				}
				al.alDeleteSources(1, new int[] { i }, 0);
				delList.add(i);
			}
		}
		snd_srcs.removeAll(delList);

		if (!stopped) {
			// interpolation
			double newTime = getMediaTime(mplayer);
			if (lastUpdateTime == 0)
				lastUpdateTime = System.nanoTime();
			if (lastMediaTime == newTime)
				time = lastMediaTime + (System.nanoTime() - lastUpdateTime) / 1e9;
			else {
				lastMediaTime = newTime;
				lastUpdateTime = System.nanoTime();
				time = newTime;
			}
		}

		page = calcPage(time);
		double liney = calcY(time);

		if (Preferences.enableBG == 1)
			bg.paint(renderer, 10); // 10 seconds

		if (Preferences.enableBG == 2)
			bg.paint(renderer, time);

		bgmask1.paint(renderer, time);
		bgmask2.paint(renderer, time);
		bgmask3.paint(renderer, time);
		bgmask3flip.paint(renderer, time);
		icontext.paint(renderer, time);
		difficulty.paint(renderer, time);
		titlemask.paint(renderer, time);
		titlemaskflip.paint(renderer, time);
		title.paint(renderer, time);

		LinkedList<Animation> del = new LinkedList<Animation>();
		for (Animation anim : animq) {
			if (anim.getEndTime() < time)
				del.add(anim);
			else if (anim.getStartTime() <= time) {
				anim.paint(renderer, time);
			}
		}
		animq.removeAll(del);

		comboeffect.paint(renderer, time);
		renderer.flushTaskQueue();

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

		if (combo > 1) {
			combosmallbg.paint(renderer, time);
			combosmalltext.paint(renderer, time);
			fcombosmall.setText(String.valueOf(combo));
			fcombosmall.paint(renderer, time);
		}

		fscore.setText(new DecimalFormat("0000000").format(score));
		fscore.paint(renderer, time);

		scanline.moveTo(0, liney);
		scanline.paint(renderer, time);
		renderer.flushTaskQueue();
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	}

	public void dispose(GLAutoDrawable drawable) {
		ALut.alutExit();
	}
}
