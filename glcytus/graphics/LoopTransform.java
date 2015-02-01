package glcytus.graphics;

import java.util.LinkedList;
import java.util.List;

public class LoopTransform extends Transform {
	public LinkedList<Transform> child = new LinkedList<Transform>();

	public LoopTransform() {
	}

	public LoopTransform(Transform t) {
		addTransform(t);
	}

	public void addTransform(Transform t) {
		child.add(t);
		t.lborder = false;
		t.rborder = false;
		stime = child.getFirst().stime;
		etime = child.getLast().etime;
	}

	public void addTransform(List<Transform> list) {
		for (Transform t : list)
			addTransform(t);
	}

	public void adjust(Sprite s, double time) {
		if (time < stime)
			return;
		if (time > etime)
			time = (time - etime) % (etime - stime) + stime;
		for (Transform t : child)
			t.adjust(s, time);
	}

	public Transform clone() {
		LoopTransform copy = new LoopTransform();
		for (Transform t : child)
			copy.addTransform(t.clone());
		return copy;
	}
}