package glcytus;

import java.io.BufferedReader;
import java.util.Scanner;

public class NoteChartReader1 {
	public static NoteChart read(BufferedReader in) throws Exception {
		double beat = 0, pshift = 0;
		String str = in.readLine();
		while (str.indexOf("SECTION") == -1) {
			// SHIFT X.XXXXXX
			if (str.startsWith("SHIFT\t"))
				pshift = Double.parseDouble(str.substring(6));
			str = in.readLine();
		}
		// SECTION X.XXXXXX X.XXXXXX X
		beat = Double.parseDouble(str.split("\\t|\\s")[2]);

		NoteChart pdata = new NoteChart(beat, pshift);

		while (str.indexOf("SECTION") != -1)
			str = in.readLine();

		int count = 0;
		int next[] = new int[3000];
		for (int i = 0; i < next.length; i++)
			next[i] = -1;

		while (str != null) {
			str = str.substring(5);
			Scanner s = new Scanner(str);
			s.nextInt();
			s.nextInt();
			double time = s.nextDouble();
			s.nextInt();
			double x = s.nextDouble();
			next[count] = s.nextInt();
			double hold = s.nextDouble();
			s.close();

			pdata.notes.add(pdata.new Note(count++, time, x, hold));
			str = in.readLine();
		}

		int linkid = 0;
		for (int i = 0; i < count; i++)
			if (next[i] != -1) {
				NoteChart.Link l = pdata.new Link(linkid++);
				int p = i, temp = 0;
				while (next[p] != -1) {
					l.add(p);
					temp = next[p];
					next[p] = -1;
					p = temp;
				}
				l.add(p);
				pdata.links.add(l);
			}

		return pdata;
	}
}