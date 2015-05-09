package glcytus;

import glcytus.graphics.Animation;
import glcytus.graphics.GamePlayAnimationPreset;
import glcytus.graphics.Sprite;
import glcytus.graphics.Transform;

public class Hold extends Note {
	public double y2 = 0;
	double judgetime1 = -1, judgetime2 = -1;
	Sprite bar = null;
	Sprite head = null, nact = null, shadow = null, light = null,
			nearadd = null;
	Sprite bshadow = null;
	Animation hold1 = null, hold2 = null, light2 = null;
	boolean playsound = false;

	public Hold(NoteChartPlayer p, int id, double time, double holdtime,
			double x, double y) {
		this.p = p;
		this.id = id;
		this.x = x;
		this.y = y;
		this.stime = time;
		this.etime = time + holdtime;
		y2 = p.calcY(etime);
		page = p.calcPage(time);

		head = new Sprite("beat_hold_active");
		head.moveTo(x, y);

		bshadow = new Sprite("beat_shadow");
		bshadow.moveTo(x, y);
		bshadow.addTransform(new Transform(Transform.ALPHA, Transform.LINEAR,
				stime, stime + 1 / 6.0, 1, 0).asLoopTransform());
		bshadow.addTransform(new Transform(Transform.SCALE, Transform.LINEAR,
				stime, stime + 1 / 6.0, 0, 3).asLoopTransform());

		hold1 = GamePlayAnimationPreset.get("hold_pressing_1");
		hold1.moveTo(x, y);
		hold1.setStartTime(stime);
		hold1.setEndTime(stime + 0.75);

		hold2 = GamePlayAnimationPreset.get("hold_pressing_2");
		hold2.moveTo(x, y);
		hold2.setStartTime(stime + 0.75);

		bar = new Sprite("hold_track");
		bar.moveTo(x, y);
		bar.setAnchor(0.5, 0);

		light = new Sprite("light_add");
		light2 = GamePlayAnimationPreset.get("light_add_2");
		light2.setAnchor(0.5, 0.7);

		shadow = new Sprite("shadow");
		shadow.setAnchor(0.5, 0.9);
		nact = new Sprite("flash_01");
		nact.moveTo(x, y);
		nearadd = new Sprite("near_add");
		nearadd.moveTo(x, y);

		double ntime = time - p.beat;
		head.addTransform(new Transform(Transform.ALPHA, Transform.LINEAR,
				ntime, ntime + p.beat / 2, 0.5, 1));
		nact.addTransform(new Transform(Transform.ALPHA, Transform.LINEAR,
				ntime, ntime + p.beat / 2, 0.5, 1));

		if (page % 2 == 1) {
			head.flipV();
			bar.flipV();
			hold1.flipV();
			hold2.flipV();
			light.flipV();
			light2.flipV();
			shadow.flipV();
		}
	}

	public void paint() {
		if (p.time > stime) {
			if ((judgetime1 != -1) && (!playsound)) {
				playsound = true;
				try {
					p.sound.start();
				} catch (Exception e) {
				}
			}
			bar.setHeight(Math.abs(y2 - y));
			bar.paint(p.renderer, p.time);
			nearadd.paint(p.renderer, p.time);
			hold1.paint(p.renderer, p.time);
			hold2.paint(p.renderer, p.time);
			bshadow.paint(p.renderer, p.time);

			if (judgetime1 == -1) {
				if (p.time - stime > 0.3) {
					p.addCombo(-1); // Miss
					p.notes.remove(this);
					Animation vanish = GamePlayAnimationPreset
							.get("beat_vanish");
					Animation miss = GamePlayAnimationPreset.get("judge_miss");
					vanish.moveTo(x, y);
					miss.moveTo(x, y);
					vanish.play(p, p.time);
					miss.play(p, p.time);
				}
			} else {
				double liney = p.calcY(p.time);
				shadow.moveTo(x, liney);
				shadow.paint(p.renderer, p.time);
				light2.moveTo(x, liney);
				light2.paint(p.renderer, p.time);
				light.moveTo(x, liney);
				light.paint(p.renderer, p.time);
				if (p.time > etime)
					judge(etime);
				if ((judgetime2 != -1) && (p.time > judgetime2)) {
					p.addCombo(judgement);
					p.notes.remove(this);
				}
			}
		} else {
			double time = p.time + p.beat;
			if (time > etime)
				time = etime;

			double liney = p.calcY(time);
			bar.setHeight(Math.abs(liney - y));
			bar.paint(p.renderer, p.time);
			head.paint(p.renderer, p.time);

			if (p.page < page)
				nact.paint(p.renderer, p.time);

		}
	}

	public void judge(double time) {
		if (judgetime1 == -1) {
			if (time < stime)
				judgetime1 = stime;
			else
				judgetime1 = time;
		} else if (judgetime2 == -1) {
			judgetime2 = time;
			Animation expanim = null;
			Animation judgeanim = null;
			Animation vanish = GamePlayAnimationPreset.get("beat_vanish");
			vanish.moveTo(x, y);
			vanish.play(p, time);

			double d = (time - judgetime1) / (etime - stime);
			if (d >= 0.9) {
				judgement = 0; // Perfect TP100
				expanim = GamePlayAnimationPreset.get("critical_explosion");
				judgeanim = GamePlayAnimationPreset.get("judge_perfect");
			}
			if ((d >= 0.75) && (d <= 0.9)) {
				judgement = 1; // Perfect TP70
				expanim = GamePlayAnimationPreset.get("explosion");
				judgeanim = GamePlayAnimationPreset.get("judge_perfect");
			}
			if ((d >= 0.5) && (d < 0.75)) {
				judgement = 2; // Good
				expanim = GamePlayAnimationPreset.get("explosion");
				judgeanim = GamePlayAnimationPreset.get("judge_good");
			}
			if (d < 0.5) {
				judgement = 3; // Bad
				judgeanim = GamePlayAnimationPreset.get("judge_bad");
			}
			if (expanim != null) { // judgement!=3
				expanim.moveTo(x, y2);
				expanim.play(p, time);
			}
			judgeanim.moveTo(x, y2);
			judgeanim.play(p, time);
		}
	}
}