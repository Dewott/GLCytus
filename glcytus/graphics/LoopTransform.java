package glcytus.graphics;

import java.util.LinkedList;

public class LoopTransform extends Transform {
	LinkedList<Transform> child = new LinkedList<Transform>();
	double stime = 0, etime = 0;

	public LoopTransform() {
	}

	public LoopTransform(Transform t) {
		super();
		addTransform(t);
	}

	public void addTransform(Transform t) {
		child.add(t);
		t.lborder = false;
		t.rborder = false;
		stime = child.getFirst().stime;
		etime = child.getLast().etime;
	}

	public void adjust(Sprite s, double time) {
		if (time > etime)
			time = (time - etime) % (etime - stime) + stime;
		for (Transform t : child)
			t.adjust(s, time);
	}
}