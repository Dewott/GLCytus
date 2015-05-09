package glcytus;

import javax.sound.sampled.*;

import java.io.*;

public class JSPlayer implements Runnable {
	ByteArrayOutputStream buf = new ByteArrayOutputStream();
	AudioFormat af = null;
	boolean muted = false;

	public JSPlayer(String file) throws Exception {
		AudioInputStream in = AudioSystem.getAudioInputStream(new File(file));
		af = in.getFormat();
		byte data[] = new byte[1024];
		int len = in.read(data, 0, 1024);

		while (len != -1) {
			buf.write(data, 0, len);
			len = in.read(data, 0, 1024);
		}
	}

	public void start() throws Exception {
		if (!muted)
			new Thread(this).start();
	}

	public void mute() {
		muted = true;
	}

	public void setRate(float speed) {
		af = new AudioFormat(af.getEncoding(), af.getSampleRate() * speed,
				af.getSampleSizeInBits(), af.getChannels(), af.getFrameSize(),
				af.getFrameRate(), af.isBigEndian());
	}

	public void run() {
		ByteArrayInputStream in = new ByteArrayInputStream(buf.toByteArray());
		SourceDataLine out = null;

		try {
			out = AudioSystem.getSourceDataLine(af);
			out.open(af);
			out.start();
			byte data[] = new byte[1024];
			int len = in.read(data, 0, 1024);

			while (len != -1) {
				out.write(data, 0, len);
				len = in.read(data, 0, 1024);
			}

			out.drain();
		} catch (Exception e) {
		} finally {
			out.close();
		}
	}

	public static void main(String args[]) throws Exception {
		JSPlayer p = new JSPlayer(args[0]);
		p.setRate(1.5f);
		p.start();
	}
}