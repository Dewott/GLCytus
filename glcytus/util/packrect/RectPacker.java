package glcytus.util.packrect;

import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class RectPacker{
    private int maxWidth = 0, maxHeight = 0;
    private LinkedList<Rect> rects = new LinkedList<Rect>();
	
	private ArrayList<HorizontalLine> lines = new ArrayList<HorizontalLine>();
	private ArrayList<HorizontalLine> sortedLines = new ArrayList<HorizontalLine>();
	private int currentLayer = 0;
	
	public RectPacker(int width,int height){
	    maxWidth = width;
		maxHeight = height;
	}
	public void add(Rect rect){
	    rects.add(rect);
	}
	public int getMaxLayer(){
	    return currentLayer;
	}
    public void doLayout(){
	    Collections.sort(rects);
		HorizontalLine topLine = new HorizontalLine(0, 0, maxWidth);
		lines.add(topLine);
		sortedLines.add(topLine);
        
		while(rects.size() > 0){
			boolean done = false;
			for(int i=0;i<sortedLines.size();i++){
			    HorizontalLine line = sortedLines.get(i);
			    int index=subListIndex(0,rects.size(),sortedLines.get(i).w);
				
			    //Full-Fit First
				{
				int bestScore = 0;
				Rect bestFit = null;
			    for(int j=index;j<rects.size();j++){
				    if(rects.get(j).w==line.w){
					    if(line.y+rects.get(j).h<=maxHeight){
					        int score = (leftFit(line,rects.get(j).h)?1:0) + 
						            (rightFit(line,rects.get(j).h)?1:0);
						    if(score > bestScore){
						        bestScore = score;
							    bestFit = rects.get(j);
							    if(score == 2) break;
						    }
					    }
					}
					else break;
				}
				if(bestFit!=null){
				    System.out.println("FFF");
				    putRect(bestFit,line.x,line.y);
					line.y+=bestFit.h;
					done=true;
					break;
				}
				}
			    //Width-Fit First
			    if(!done){
				    Rect bestFit = null;
			        for(int j=index;j<rects.size();j++)
				        if(rects.get(j).w==line.w){
					        if(line.y+rects.get(j).h<=maxHeight){
					            bestFit = rects.get(j);
						        break;
					         }
					    }
					    else break;
				    if(bestFit != null){
					    System.out.println("WFF");
				        putRect(bestFit,line.x,line.y);
					    line.y+=bestFit.h;
					    done=true;
					    break;
				    }
			    }
				
			    //Height-Fit First
			    if(!done){
				     Rect bestFit = null;
			         for(int j=index;j<rects.size();j++)
				        //rects.get(j).w < line.w
				        if((line.y+rects.get(j).h<=maxHeight)&&leftFit(line,rects.get(j).h)){
					        bestFit = rects.get(j);
						    break;
					    }
				    if(bestFit != null){
					    System.out.println("HFF");
				        putRect(bestFit,line.x,line.y);
					    HorizontalLine newLine=new HorizontalLine(line.x+bestFit.w,line.y,line.w-bestFit.w);
					    line.w=bestFit.w;
					    line.y+=bestFit.h;
					    lines.add(newLine);
					    done=true;
					    break;
				    }
			    }
				
			    //Joint-Width-Fit First
			    if(!done){
					int n = Math.min(rects.size() - index, 32);
					int bestScore = 0;
					Rect bestFit1 = null, bestFit2 = null;
					for(int j=0;j<n;j++)
					    for(int k=j+1;k<n;k++)
						    if(((line.y+rects.get(index+j).h<=maxHeight)&&((line.y+rects.get(index+k).h<=maxHeight))&&
							    (rects.get(index+j).w+rects.get(index+k).w==line.w))){
							    int score = rects.get(index+j).h + rects.get(index+k).h;
								if(score > bestScore){
								    bestScore = score;
									bestFit1 = rects.get(index+j);
									bestFit2 = rects.get(index+k);
								}
							}
					if(bestScore > 0){
					    System.out.println("JWFF");
					    putRect(bestFit1,line.x,line.y);
						putRect(bestFit2,line.x+bestFit1.w,line.y);
						HorizontalLine newLine = new HorizontalLine(line.x + bestFit1.w, line.y + bestFit2.h, line.w - bestFit1.w);
						line.y += bestFit1.h;
						line.w = bestFit1.w;
						lines.add(newLine);
						done=true;
						break;
					}
				}
				
			    //Placeable First
			    if(!done){
				    Rect bestFit = null;
			        for(int j=index;j<rects.size();j++)
				        //rects.get(j).w < line.w
				        if(line.y+rects.get(j).h<=maxHeight){
					        bestFit = rects.get(j);
						    break;
					    }
				    if(bestFit != null){
					    System.out.println("PF");
				        putRect(bestFit,line.x,line.y);
					    HorizontalLine newLine = new HorizontalLine(line.x + bestFit.w, line.y, line.w - bestFit.w);
						line.y += bestFit.h;
						line.w = bestFit.w;
						lines.add(newLine);
					    done = true;
						break;
				    }
			    }
				if(done) break;
			}
			if(!done){
			    //Next layer
				System.out.println("Next Layer");
			    currentLayer++;
				lines.clear();
				sortedLines.clear();
				lines.add(topLine);
				sortedLines.add(topLine);
			}
			else checkAndMerge();
		}
	}
	private int subListIndex(int left,int right,int key){
	    if(left==right) return left;
		int mid = (left+right)>>1;
		if(rects.get(mid).w>key) return subListIndex(mid+1,right,key);
		else return subListIndex(left,mid,key);
	}
	private boolean leftFit(HorizontalLine current,int h){
	    int index = lines.indexOf(current);
		if(index == 0) return current.y + h == maxHeight;
		else return current.y + h == lines.get(index-1).y;
	}
	private boolean rightFit(HorizontalLine current,int h){
	    int index = lines.indexOf(current);
		if(index == lines.size()-1) return current.y + h == maxHeight;
		else return current.y + h == lines.get(index+1).y;
	}
	private void putRect(Rect r,int x,int y){
	    r.x = x;
		r.y = y;
		r.layer = currentLayer;
		rects.remove(r);
		System.out.println("PutRect "+x+" "+y+" "+r.w+" "+r.h+" "+currentLayer);
	}
	private void checkAndMerge(){
	    Collections.sort(lines,HorizontalLine.xComparator);
		for(int i=0;i<lines.size()-1;i++)
		    if(lines.get(i).y==lines.get(i+1).y){
			    //lines.get(i).x+lines.get(i).w = lines.get(i+1).x;
				lines.get(i).w += lines.get(i+1).w;
				lines.remove(i+1);
			}
		int i=0;
		while(i<lines.size()){
		    if(lines.get(i).y == maxHeight) lines.remove(i);
			else i++;
		}
		sortedLines.clear();
		sortedLines.addAll(lines);
	    Collections.sort(sortedLines,HorizontalLine.yComparator);
	}
	/*public static void main(String args[]) throws Exception{
	    Rect rects[]=new Rect[128*128];
	    RectPacker packer = new RectPacker(1024,1024);
		for(int i=0;i<rects.length;i++){
		    rects[i]=new Rect(8,8);
			packer.add(rects[i]);
		}
		packer.doLayout();
		
		BufferedImage img = new BufferedImage(1024,1024,BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		g.setColor(Color.RED);
		for(int i=0;i<rects.length;i++){
			g.drawRect(rects[i].x,rects[i].y,rects[i].w,rects[i].h);
		}
		g.dispose();
		ImageIO.write(img,"png",new File("out2.png"));
	}*/
}