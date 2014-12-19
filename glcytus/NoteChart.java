package glcytus;

import java.util.Collections;
import java.util.LinkedList;

public class NoteChart implements Cloneable {
	public double beat = 0, offset = 0, pshift = 0;
	public LinkedList<NoteChart.Note> notes = new LinkedList<NoteChart.Note>();
	public LinkedList<NoteChart.Link> links = new LinkedList<NoteChart.Link>();

	public NoteChart(double beat, double pshift) {
		this.beat = beat;
		this.pshift = pshift;
	}

	public void modified() {
		Collections.sort(notes);
		for (int i = 0; i < notes.size(); i++)
			notes.get(i).id = i;
		LinkedList<Link> dellist = new LinkedList<Link>();
		for (Link link : links) {
			LinkedList<Note> ndel = new LinkedList<Note>();
			for (Note node : link.nodes)
				if (!notes.contains(node)) {
					ndel.add(node);
					link.n--;
				}
			link.nodes.removeAll(ndel);

			if (link.n <= 1)
				dellist.add(link);
			else
				Collections.sort(link.nodes);
		}
		links.removeAll(dellist);
		Collections.sort(links);
		for (int i = 0; i < links.size(); i++)
			links.get(i).setID(i);
	}

	public class Note implements Comparable<Note> {
		public int id = 0, page = 0;
		public double x = 0;
		public double time = 0, holdtime = 0;
		public int linkref = -1;

		public Note() {
		}

		public Note(int id, double time, double x, double holdtime) {
			this.id = id;
			this.time = time;
			this.x = x;
			this.page = calcPage(time);
			this.holdtime = holdtime;
		}

		public int calcPage(double time) {
			return (int) ((time + pshift) / beat);
		}

		public double calcY(double time) {
			int cpage = calcPage(time);
			double y = (time + pshift) / beat - cpage;
			if (cpage % 2 == 0)
				y = 1 - y;
			return y;
		}

		public int compareTo(Note n) {
			return Double.compare(time, n.time);
		}
	}

	public class Link implements Comparable<Link> {
		public int n = 0, id = 0;
		public LinkedList<Note> nodes = new LinkedList<Note>();

		public Link(int id) {
			this.id = id;
		}

		public void add(Note note) {
			note.linkref = id;
			nodes.add(note);
			n++;
		}

		public void remove(Note note) {
			note.linkref = -1;
			nodes.remove(note);
			n--;
			if (n == 1)
				nodes.get(0).linkref = -1;
		}

		public void removeAll() {
			for (Note note : nodes)
				note.linkref = -1;
			nodes.clear();
			n = 0;
		}

		public void add(int p) {
			add(notes.get(p));
		}

		public void remove(int p) {
			remove(notes.get(p));
		}

		public void setID(int id) {
			this.id = id;
			for (Note note : nodes)
				note.linkref = id;
		}

		public int compareTo(Link link) {
			return Double.compare(nodes.get(0).time, link.nodes.get(0).time);
		}
	}

	public Object clone() {
		NoteChart copy = new NoteChart(beat, pshift);
		copy.offset = offset;
		for (Note n : this.notes)
			copy.notes.add(copy.new Note(n.id, n.time, n.x, n.holdtime));
		for (Link l : this.links) {
			Link nlink = copy.new Link(l.id);
			for (int i = 0; i < l.n; i++)
				nlink.add(l.nodes.get(i).id);
			copy.links.add(nlink);
		}
		return copy;
	}
}