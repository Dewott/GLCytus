package glcytus;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;

public class Main extends JFrame {
	public Main(String songtitle) throws Exception {
		super("GLCytus");
		setSize(966, 648);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		GLCanvas canvas = new GLCanvas();
		NoteChartPlayer p = new NoteChartPlayer(songtitle, "hard");
		canvas.addGLEventListener(p);
		add(canvas);

		setVisible(true);
		new FPSAnimator(canvas, 60, true).start();
		try {
			Thread.sleep(3000);
		} catch (Exception e) {
		}
		p.start();
	}

	public static void main(String args[]) throws Exception {
		System.setProperty("sun.java2d.opengl", "false");
		System.setProperty("sun.java2d.noddraw", "true");
		new Main(args[0]);
	}
}