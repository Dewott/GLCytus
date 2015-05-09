package glcytus.util.packrect;

import java.util.*;

public class HorizontalLine{
    public int x = 0, y = 0, w = 0;
	public static XComparator xComparator = new XComparator();
	public static YComparator yComparator = new YComparator();
	public HorizontalLine(int x,int y,int w){
	    this.x=x;
		this.y=y;
		this.w=w;
	}
	public static class XComparator implements Comparator<HorizontalLine>{
	    public int compare(HorizontalLine line1,HorizontalLine line2){
	        return line1.x - line2.x;
		}
	}
	public static class YComparator implements Comparator<HorizontalLine>{
	    public int compare(HorizontalLine line1,HorizontalLine line2){
	        if(line1.y == line2.y) return line1.x - line2.x;
		    else return line1.y - line2.y;
		}
	}
}