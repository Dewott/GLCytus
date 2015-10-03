package glcytus.ext;

import glcytus.graphics.ImageHandle;
import glcytus.util.ResourceLoader;

public class SelectCover extends Base {
	public SelectCover(String songtitle) {
		String folder = "assets/songs/" + songtitle + "/";
		try {
			loadSprite(folder, "select_cover.prefab.json");

			double endtime = loadMorphingAnimation(folder, songtitle + "_enter.anim.json", false, 0);
			loadMorphingAnimation(folder, songtitle + "_loop.anim.json", true, endtime);
		} catch (Exception e) {
			e.printStackTrace();
			this.img = new ImageHandle();
			try {
				img.texture = ResourceLoader.loadTexture(folder, "bg.png");
				img.w = img.texture.getWidth();
				img.h = img.texture.getHeight();
				img.srcw = img.texture.getWidth();
				img.srch = img.texture.getHeight();
				img.spsw = img.texture.getWidth();
				img.spsh = img.texture.getHeight();
				w = 1024;
				h = 683;
			} catch (Exception ee) {
				ee.printStackTrace();
				img.w = 1024;
				img.h = 683;
				img.srcw = 1024;
				img.srch = 683;
				img.spsw = 1024;
				img.spsh = 683;
			}
		}
	}
}