package glcytus;

import java.io.PrintWriter;
import java.text.DecimalFormat;

public class NoteChartWriter {
	public NoteChartWriter(NoteChart pdata, PrintWriter out) throws Exception {
		DecimalFormat df = new DecimalFormat("0.000000");
		// Header
		out.println("VERSION 2");
		out.println("BPM " + df.format(240.0 / pdata.beat));
		out.println("PAGE_SHIFT " + df.format(pdata.pshift));
		out.println("PAGE_SIZE " + df.format(pdata.beat));

		for (NoteChart.Note note : pdata.notes) {
			out.print("NOTE\t");
			out.print(note.id + "\t");
			out.print(df.format(note.time) + "\t");
			out.print(df.format(note.x) + "\t");
			out.println(df.format(note.holdtime));
		}
		for (NoteChart.Link link : pdata.links) {
			out.print("LINK ");
			for (int i = 0; i < link.n; i++)
				out.print(link.nodes.get(i).id + " ");
			out.println();
		}
		out.close();
	}

}