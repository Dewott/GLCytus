package glcytus.ext;

import java.util.LinkedHashMap;

public class ChapterInfo {
	public String chapterName = "";
	public boolean hasCover = true;
	public Chapter chapter = null;
	public String bgpath = "";
	public int totalScore = 0, clear_easy = 0, clear_hard = 0, total = 0;
	public LinkedHashMap<String, SongInfo> songs = new LinkedHashMap<String, SongInfo>();
}
