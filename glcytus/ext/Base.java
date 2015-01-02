package glcytus.ext;

import glcytus.graphics.*;
import glcytus.util.*;
import java.util.LinkedHashMap;

public class Base extends Sprite {
	LinkedHashMap<String, Sprite> elements = new LinkedHashMap<String, Sprite>();

	public Base() {
	}

	public void loadSprite(String folder, String name) throws Exception {
		ResourceLoader.loadSprite(folder, this,
				ResourceLoader.loadJSONObjectFromFile(folder, name));
		addElements(this);
	}

	public double loadMorphingAnimation(String folder, String filename,
			boolean loop, double offset) throws Exception {
		MorphingAnimation anim = ResourceLoader.loadMorphingAnimation(folder,
				filename, loop);
		anim.use(elements, offset);
		return anim.endtime + offset;
	}

	public void useAnimation(MorphingAnimation anim, double time) {
		// clearTransforms();
		anim.use(elements, time);
	}

	protected void addElements(Sprite s) {
		elements.put(s.name, s);
		for (Sprite cs : s.childs)
			addElements(cs);
	}
}