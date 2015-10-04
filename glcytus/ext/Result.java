package glcytus.ext;

import glcytus.graphics.TextSprite;

import java.text.DecimalFormat;

public class Result extends Base {
	Base diff = null, perfect = null, good = null, bad = null, miss = null;
	boolean newbest = false;

	public Result() throws Exception {
		String folder = "assets/ui/Result/";
		loadSprite(folder, "result_root.prefab.json");

		diff = new Base();
		diff.loadSprite(folder, "difficulty.prefab.json");
		diff.loadMorphingAnimation(folder, "difficulty.anim.json", false, 0);
		diff.setAnchor("BottomRight");
		diff.moveTo(459.5, -310);
		addElements(diff);
		addChild(diff, false);

		perfect = new Base();
		perfect.loadSprite(folder, "perfect.prefab.json");
		perfect.loadMorphingAnimation(folder, "BeatStatistics.anim.json",
				false, 0);
		addChild(perfect, false);

		good = new Base();
		good.loadSprite(folder, "good.prefab.json");
		good.loadMorphingAnimation(folder, "BeatStatistics.anim.json", false, 0);
		addChild(good, false);

		bad = new Base();
		bad.loadSprite(folder, "bad.prefab.json");
		bad.loadMorphingAnimation(folder, "BeatStatistics.anim.json", false, 0);
		addChild(bad, false);

		miss = new Base();
		miss.loadSprite(folder, "miss.prefab.json");
		miss.loadMorphingAnimation(folder, "BeatStatistics.anim.json", false, 0);
		addChild(miss, false);

		loadMorphingAnimation(folder, "result_animation.anim.json", false, 0);

		setScore(924950);
		setCombo(920);
		setTP(95.24);
		setPerfect(894);
		setGood(26);
		setBad(0);
		setMiss(6);
	}

	public void setScore(double score) {
		DecimalFormat df = new DecimalFormat("000000");
		((TextSprite) elements.get("Score")).text = df.format(score);
	}

	public void setCombo(int combo) {
		((TextSprite) elements.get("Combo")).text = String.valueOf(combo);
	}

	public void setTP(double tp) {
		DecimalFormat df = new DecimalFormat("00.00");
		((TextSprite) elements.get("TP/TP")).text = df.format(tp);
	}

	public void setPerfect(int number) {
		((TextSprite) perfect.elements.get("Number")).text = String
				.valueOf(number);
	}

	public void setGood(int number) {
		((TextSprite) good.elements.get("Number")).text = String
				.valueOf(number);
	}

	public void setBad(int number) {
		((TextSprite) bad.elements.get("Number")).text = String.valueOf(number);
	}

	public void setMiss(int number) {
		((TextSprite) miss.elements.get("Number")).text = String
				.valueOf(number);
	}
}