package glcytus;

public abstract class Note implements Comparable<Note> {
	NoteChartPlayer p = null;
	public int id = 0;
	public double x = 0, y = 0;
	public double stime = 0, etime = 0, judgetime = -1;
	public int page = 0;
	public int judgement = -1;

	public abstract void paint();

	public abstract void judge(double time);

	public int compareTo(Note note) {
		return Double.compare(stime, note.stime);
	}
}
