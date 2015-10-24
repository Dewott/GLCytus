package glcytus;

import glcytus.graphics.Animation;
import glcytus.graphics.BeatTransform;
import glcytus.graphics.GamePlayAnimationPreset;
import glcytus.graphics.Sprite;
import glcytus.graphics.Transform;

public class Click extends Note {
	Sprite circle = null, nearadd = null;
	Animation flash = null, shadow = null;

	public Click(NoteChartPlayer p, int id, double time, double x, double y) {
		this.p = p;
		this.id = id;
		this.x = x;
		this.y = y;
		this.stime = time;
		this.etime = time;
		page = p.calcPage(time);
		circle = page % 2 == 0 ? new Sprite("red_active") : new Sprite("yellow_active");
		circle.moveTo(x, y);

		flash = GamePlayAnimationPreset.get("beat_flash");
		flash.moveTo(x, y);
		flash.stime = time - p.beat;
		flash.playToAndStop(3);

		nearadd = new Sprite("near_add");
		nearadd.moveTo(x, y);

		double ntime1 = time - p.beat;
		double ntime2 = page * p.beat - p.pshift;

		switch (Preferences.popupMode) {
		case 1:// Default
			circle.addTransform(new Transform(Transform.SCALE, Transform.LINEAR, ntime1, time, 0.5, 1));
			nearadd.addTransform(new Transform(Transform.SCALE, Transform.LINEAR, ntime1, time, 0.5, 1));
			flash.addTransform(new Transform(Transform.SCALE, Transform.LINEAR, ntime1, time, 0.5, 1));
			break;

		case 2:// Grouped
			circle.addTransform(new Transform(Transform.SCALE, Transform.LINEAR, ntime2 - p.beat / 3, ntime2, 0.5, 1));
			circle.addTransform(new BeatTransform(ntime2, p.pshift, p.beat / 2));
			nearadd.addTransform(new Transform(Transform.SCALE, Transform.LINEAR, ntime2 - p.beat / 3, ntime2, 0.5, 1));
			nearadd.addTransform(new BeatTransform(ntime2, p.pshift, p.beat / 2));
			flash.addTransform(new Transform(Transform.SCALE, Transform.LINEAR, ntime2 - p.beat / 3, ntime2, 0.5, 1));
			break;

		default: // None
			circle.addTransform(new BeatTransform(ntime2, p.pshift, p.beat / 2));
			nearadd.addTransform(new BeatTransform(ntime2, p.pshift, p.beat / 2));
		}

		circle.addTransform(new Transform(Transform.ALPHA, Transform.LINEAR, ntime1, ntime1 + p.beat / 2, 0, 1));
		nearadd.addTransform(
				new Transform(Transform.ALPHA, Transform.LINEAR, time - p.beat / 2, time - p.beat * 0.4, 0, 1));
		flash.addTransform(new Transform(Transform.ALPHA, Transform.LINEAR, ntime1, ntime1 + p.beat / 2, 0, 1));

		shadow = new Animation("beat_shadow", time - p.beat * 0.3, time + 1 / 3.0);
		shadow.moveTo(x, y);
		shadow.addTransform(
				new Transform(Transform.ALPHA, Transform.LINEAR, time - p.beat * 0.3, time, 1, 0, true, false));
		shadow.addTransform(
				new Transform(Transform.SCALE, Transform.LINEAR, time - p.beat * 0.3, time, 1, 2, false, false));
		shadow.play(p);
	}

	public void paint() {
		if ((judgement != -1) && (p.time >= judgetime)) {
			p.addCombo(judgement);
			p.notes.remove(this);
			return;
		}
		if (p.time > stime + 0.3) {
			p.addCombo(-1); // Miss
			Animation judgeanim = GamePlayAnimationPreset.get("judge_miss");
			judgeanim.moveTo(x, y);

			circle.addTransform(new Transform(Transform.ALPHA, Transform.LINEAR, p.time, p.time + 1 / 3.0, 1, 0));
			judgeanim.addChild(circle, true);

			judgeanim.play(p, p.time);
			p.notes.remove(this);
		}
		circle.paint(p.renderer, p.time);
		nearadd.paint(p.renderer, p.time);
		if ((p.page == page) && (flash.stopAt != -1)) {
			flash.stopAt = -1;
			flash.status = Animation.STATUS_PLAY;
			flash.stime = page * p.beat - p.pshift - 4 / 27.0;
			flash.etime = page * p.beat - p.pshift + 5 / 27.0;
		}
		flash.paint(p.renderer, p.time);
	}

	public void judge(double time) {
		judgetime = time;
		Animation expanim = null;
		Animation judgeanim = null;
		Animation blow = new Animation(page % 2 == 0 ? "red_blow" : "yellow_blow", time, time + 1 / 3.0);
		blow.addTransform(new Transform(Transform.ROTATION, Transform.LINEAR, time, time + 1 / 6.0, 0, Math.PI));
		blow.addTransform(new Transform(Transform.ALPHA, Transform.LINEAR, time, time + 1 / 6.0, 1, 0));
		blow.addTransform(new Transform(Transform.SCALE, Transform.LINEAR, time, time + 1 / 6.0, 1, 2));
		blow.moveTo(x, y);
		boolean blowPlay = false;

		double d = Math.abs(time - stime);
		if (d <= 0.075) {
			judgement = 0; // Perfect TP100
			expanim = GamePlayAnimationPreset.get("critical_explosion");
			judgeanim = GamePlayAnimationPreset.get("judge_perfect");
			blowPlay = true;
		}
		if ((d > 0.075) && (d <= 0.150)) {
			judgement = 1; // Perfect TP70
			expanim = GamePlayAnimationPreset.get("explosion");
			judgeanim = GamePlayAnimationPreset.get("judge_perfect");
			blowPlay = true;
		}
		if ((d > 0.150) && (d <= 0.225)) {
			judgement = 2; // Good
			expanim = GamePlayAnimationPreset.get("explosion");
			judgeanim = GamePlayAnimationPreset.get("judge_good");
			blowPlay = true;
		}
		if (d > 0.225) {
			judgement = 3; // Bad
			judgeanim = GamePlayAnimationPreset.get("judge_bad");
			circle.addTransform(new Transform(Transform.ALPHA, Transform.LINEAR, time, time + 1 / 3.0, 1, 0));
			judgeanim.addChild(circle, true);
		}
		if (expanim != null) { // judgement!=3
			expanim.moveTo(x, y);
			expanim.play(p, time);
			// shadow.clearTransforms();
			shadow.addTransform(
					new Transform(Transform.SCALE, Transform.LINEAR, time, time + 1 / 6.0, 1.2, 1.2, false, false));
			shadow.addTransform(
					new Transform(Transform.ALPHA, Transform.LINEAR, time, time + 1 / 6.0, 0.8, 0, false, true));
		}
		judgeanim.moveTo(x, y);
		judgeanim.play(p, time);
		judgeanim.scale(1.2);
		if (blowPlay)
			blow.play(p);
	}
}