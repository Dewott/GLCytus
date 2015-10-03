package glcytus;

import glcytus.graphics.Animation;
import glcytus.graphics.GamePlayAnimationPreset;
import glcytus.graphics.Sprite;
import glcytus.graphics.Transform;

import java.util.ArrayList;

public class Drag extends Note {
	int n = 0;
	public ArrayList<Node> nodes = new ArrayList<Node>();
	Sprite head = null, shadow = null, nearadd = null;
	Animation arrow = null, arrexp = null, dlight = null, blow = null;
	boolean endblow = false;

	public Drag(NoteChartPlayer p) {
		this.p = p;
		head = new Sprite("drag_head_active");
		shadow = new Sprite("shadow");
		shadow.setAnchor(0.5, 1);
		nearadd = new Sprite("near_add");
		arrow = GamePlayAnimationPreset.get("arrow_flash");
		dlight = GamePlayAnimationPreset.get("drag_light");
		dlight.setAnchor(0.5, 0.7);
		arrexp = GamePlayAnimationPreset.get("arrow_explode");
	}

	public double calcAngle(int p, int q) {
		Node n1 = nodes.get(p);
		Node n2 = nodes.get(q);
		double x = n2.x - n1.x;
		double y = n2.y - n1.y;
		return Math.atan2(y, x) - Math.PI / 2.0;
	}

	public void paint() {
		int i = 0;
		if (p.time >= stime) {
			for (i = n - 1; i > 0; i--)
				if (nodes.get(i).stime >= p.time) {
					double angle = calcAngle(i - 1, i);
					double x1 = nodes.get(i - 1).x, x2 = nodes.get(i).x;
					double y1 = nodes.get(i - 1).y, y2 = nodes.get(i).y;
					double x = (x1 + x2) / 2, y = (y1 + y2) / 2;
					double len = Math.hypot(x1 - x2, y1 - y2);
					len = Math.max(len - 48, 0);
					Sprite dragline = new Sprite("drag_line");
					dragline.moveTo(x, y);
					dragline.setHeight(len);
					dragline.rotate(angle, 0, 0);
					dragline.paint(p.renderer, p.time);
				}

			for (i = 0; i < n; i++)
				if (p.time < nodes.get(i).stime)
					break;
			if (i == n)
				i = n - 1;

			if (p.time >= etime) {
				if (!endblow) {
					Node lastnode = nodes.get(n - 1);
					if (lastnode.judgement != -1) {
						dlight.moveTo(lastnode.x, lastnode.y);
						dlight.addTransform(new Transform(Transform.ROTATION, Transform.LINEAR, etime, etime + 1 / 4.0,
								0, Math.PI));
						blow.addChild(dlight, true);
						arrexp.play(p);
						blow.play(p);
					}
					endblow = true;
				}
				if (p.time > etime + 0.3)
					p.notes.remove(this);
				return;
			}
			double pos = (p.time - nodes.get(i - 1).stime) / (nodes.get(i).stime - nodes.get(i - 1).stime);
			int cx = (int) ((nodes.get(i).x - nodes.get(i - 1).x) * pos + nodes.get(i - 1).x);
			int cy = (int) ((nodes.get(i).y - nodes.get(i - 1).y) * pos + nodes.get(i - 1).y);
			double angle = calcAngle(i - 1, i);
			shadow.moveTo(cx, cy);
			shadow.rotate(angle, 0, 0);
			shadow.paint(p.renderer, p.time);
			head.moveTo(cx, cy);
			head.paint(p.renderer, p.time);
			nearadd.moveTo(cx, cy);
			nearadd.paint(p.renderer, p.time);
			arrow.moveTo(cx, cy);
			arrow.rotate(angle, 0, 0);
			arrow.paint(p.renderer, p.time);
			dlight.moveTo(cx, cy);
			dlight.rotate(angle, 0, 0);
			dlight.paint(p.renderer, p.time);
		} else {
			int end = n;
			for (i = 1; i < n; i++)
				if (p.time + p.beat < nodes.get(i).stime) {
					end = i;
					break;
				}
			for (i = 0; i < end - 1; i++) {
				double angle = calcAngle(i, i + 1);
				double x1 = nodes.get(i).x, x2 = nodes.get(i + 1).x;
				double y1 = nodes.get(i).y, y2 = nodes.get(i + 1).y;
				double x = (x1 + x2) / 2, y = (y1 + y2) / 2;
				double len = Math.hypot(x1 - x2, y1 - y2);
				len = Math.max(len - 48, 0);
				Sprite dragline = new Sprite("drag_line");
				dragline.moveTo(x, y);
				dragline.setHeight(len);
				dragline.rotate(angle, 0, 0);
				dragline.paint(p.renderer, p.time);
			}

			head.moveTo(nodes.get(0).x, nodes.get(0).y);
			head.paint(p.renderer, p.time);
			nearadd.moveTo(nodes.get(0).x, nodes.get(0).y);
			if (p.time + p.beat * 0.4 >= nodes.get(0).stime)
				nearadd.paint(p.renderer, p.time);

			if (p.page == page) {
				arrow.moveTo(nodes.get(0).x, nodes.get(0).y);
				arrow.rotate(calcAngle(0, 1), 0, 0);
				arrow.paint(p.renderer, p.time);
			}
		}
	}

	public void recalc() {
		n = nodes.size();
		stime = nodes.get(0).stime;
		etime = nodes.get(n - 1).stime;
		page = p.calcPage(stime);

		double ntime = page * p.beat - p.pshift;
		head.clearTransforms();
		head.addTransform(new Transform(Transform.ALPHA, Transform.LINEAR, stime - p.beat, stime - p.beat / 2, 0, 1));

		int popupmode = 2;
		switch (popupmode) {
		case 0:// None
			break;
		case 1:// Default
			head.addTransform(new Transform(Transform.SCALE, Transform.LINEAR, stime - p.beat / 2, stime, 0.8, 1));
			nearadd.addTransform(new Transform(Transform.SCALE, Transform.LINEAR, stime - p.beat / 2, stime, 0.8, 1));
			break;

		case 2:// Grouped
			head.addTransform(new Transform(Transform.SCALE, Transform.LINEAR, ntime - p.beat / 2, ntime, 0.5, 1));
			nearadd.addTransform(new Transform(Transform.SCALE, Transform.LINEAR, ntime - p.beat / 2, ntime, 0.5, 1));
			break;
		}

		arrexp.moveTo(nodes.get(n - 1).x, nodes.get(n - 1).y);
		arrexp.rotate(calcAngle(n - 2, n - 1), 0, 0);
		blow = new Animation("drag_head_blow", etime, etime + 1 / 6.0);
		blow.moveTo(nodes.get(n - 1).x, nodes.get(n - 1).y);
		blow.clearTransforms();
		blow.addTransform(
				new Transform(Transform.ROTATION, Transform.LINEAR, etime, etime + 1 / 4.0, 0, Math.PI * 1.5));
		blow.addTransform(new Transform(Transform.SCALE, Transform.LINEAR, etime, etime + 1 / 4.0, 1, 2));
		blow.addTransform(new Transform(Transform.ALPHA, Transform.LINEAR, etime, etime + 1 / 4.0, 1, 0.5));
	}

	public void judge(double time) {
	}

	public int compareTo(Note note) {
		if (note.stime == stime)
			return -1;
		else
			return super.compareTo(note);
	}

	public static class Node extends Note {
		Animation nflash = null, nexp = null, perfect = null;
		Sprite ps = null, nps = null;

		public Node(NoteChartPlayer p, int id, double time, double x, double y) {
			this.p = p;
			this.id = id;
			this.x = x;
			this.y = y;
			this.page = p.calcPage(time);
			this.stime = time;
			this.etime = time;

			nflash = GamePlayAnimationPreset.get("node_flash");
			nflash.moveTo(x, y);
			nflash.setStartTime(page * p.beat - p.pshift);
			nflash.setEndTime(nflash.getStartTime() + 1 / 3.0);

			ps = new Sprite("node_flash_04");
			ps.moveTo(x, y);
			ps.addTransform(new Transform(Transform.ALPHA, Transform.LINEAR, time - p.beat, time - p.beat / 2, 0, 1));

			nps = new Sprite("node_flash_01");
			nps.moveTo(x, y);
			nps.addTransform(new Transform(Transform.ALPHA, Transform.LINEAR, time - p.beat, time - p.beat / 2, 0, 1));
		}

		public void paint() {
			if ((judgement != -1) && (p.time >= judgetime)) {
				p.addCombo(judgement);
				p.notes.remove(this);
				return;
			}
			if (page == p.page) {
				if (nflash.ended(p.time))
					ps.paint(p.renderer, p.time);
				else
					nflash.paint(p.renderer, p.time);
			} else
				nps.paint(p.renderer, p.time);

			if (p.time > stime + 0.3) {
				p.addCombo(-1); // Miss
				Animation judgeanim = GamePlayAnimationPreset.get("judge_miss");
				judgeanim.moveTo(x, y);
				ps.addTransform(new Transform(Transform.ALPHA, Transform.LINEAR, p.time, p.time + 1 / 3.0, 1, 0));
				judgeanim.addChild(ps, true);
				judgeanim.play(p, p.time);
				p.notes.remove(this);
			}
		}

		public void judge(double time) {
			judgetime = time;
			Animation judgeanim = null;
			Animation expanim = null;
			double d = Math.abs(time - stime);
			if (d < 0.175) {
				judgement = 0; // Perfect TP100
				judgeanim = GamePlayAnimationPreset.get("judge_perfect");
				expanim = GamePlayAnimationPreset.get("critical_explosion");
				judgeanim.scale(0.75);
			} else {
				judgement = 1; // Perfect TP70;
				judgeanim = GamePlayAnimationPreset.get("judge_perfect");
				if (time > stime)
					expanim = GamePlayAnimationPreset.get("explosion");
				else
					expanim = GamePlayAnimationPreset.get("node_explode");
			}
			expanim.moveTo(x, y);
			judgeanim.moveTo(x, y);
			expanim.play(p, time);
			judgeanim.play(p, time);
		}

		public int compareTo(Note note) {
			if (note instanceof Drag) {
				Drag d = (Drag) note;
				if (d.nodes.get(0).stime == this.stime)
					return 1;
			}
			return super.compareTo(note);
		}
	}
}