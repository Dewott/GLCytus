package glcytus;

import java.io.*;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.jogamp.opengl.util.*;

public class Main extends JFrame {
	public Main() throws Exception {
		super("GLCytus");
		Preferences.load();
		String songtitle = JOptionPane.showInputDialog("Input song title");
		String diff = JOptionPane.showInputDialog("Input difficulty", "hard");
		if (Preferences.fullScreen == 0) {
			setSize(Preferences.width, Preferences.height);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
		} else {
			setUndecorated(true);
			GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice dev = env.getDefaultScreenDevice();
			dev.setFullScreenWindow(this);
		}

		GLCanvas canvas = new GLCanvas();
		NoteChartPlayer p = new NoteChartPlayer(songtitle, diff);
		canvas.addGLEventListener(p);
		add(canvas);

		setVisible(true);
		new Animator(canvas).start();
		Thread.sleep(1000);
		p.start();
	}

	public static void main(String args[]) throws Exception {
		System.setProperty("sun.java2d.opengl", "false");
		System.setProperty("sun.java2d.noddraw", "true");
		try {
			PrintStream stdout = new PrintStream(new FileOutputStream("stdout.txt"));
			PrintStream stderr = new PrintStream(new FileOutputStream("stderr.txt"));
			System.setOut(stdout);
			System.setErr(stderr);
			new Main();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}