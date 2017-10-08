import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class homework {

	static class State {
		public char[][] getConfig() {
			return config;
		}

		public void setConfig(char[][] config) {
			this.config = config;
		}

		public int getxScore() {
			return xScore;
		}

		public void setxScore(int xScore) {
			this.xScore = xScore;
		}

		public int getyScore() {
			return yScore;
		}

		public void setyScore(int yScore) {
			this.yScore = yScore;
		}

		char[][] config;
		int xScore, yScore;

		State(char[][] config, int xs, int ys) {
			this.config = config;
			this.xScore = xs;
			this.yScore = ys;
		}
		
		public String toString() {
			StringBuilder s=new StringBuilder();
			for(int i=0;i<size;i++) {
				s.append(this.config[i]);
				s.append("\n");
			}	
			return s.toString();
		}
	}

	static class Point {
		int row;
		int col;

		Point(int r, int c) {
			this.row = r;
			this.col = c;
		}
		
		public String toString() {
			return "["+row+","+col+"]";	
		}
	}
    static int count=0;
	static int size=0;
	static float timeleft=0;
	static State maxMove=null;
	static int MAX_DEPTH=4;
	public static void main(String[] args) {
		BufferedReader bf = null;
		try {
			bf = new BufferedReader(new FileReader(new File("src\\input.txt")));
			size = Integer.parseInt(bf.readLine());
			int fruits = Integer.parseInt(bf.readLine());
			timeleft = Float.parseFloat(bf.readLine());
			char[][] PLAY = new char[size][size];
			String str = null;
			int row = 0, col = 0;
			while ((str = bf.readLine()) != null) {
				str=str.trim();
				col = 0;
				for (int i = 0; i < str.length(); i++) {
					PLAY[row][col] = str.charAt(i);
					col++;
				}
				row++;
			}

			
			long t1=System.currentTimeMillis();
			int minMaxValue = MaxValue(new State(PLAY, 0, 0), Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
			long t2=System.currentTimeMillis();
			System.out.println("MILIS:"+(t2-t1));
			
			System.out.println("MINMAXVALUE::"+minMaxValue);
			System.out.println("COUNT::"+count);
           // printMatrix(PLAY);
            //Print selection for next move
			char[][] newConfig=maxMove.getConfig();
			
			int p=0,q=0;
			boolean found=false;
			for(p=size-1;p>=0;p--) {
				for(q=size-1;q>=0;q--) {
					if(newConfig[p][q]!=PLAY[p][q]){
					 found=true;
					 break;
					}
				}
				if(found==true)
					break;
			}
			
			writeOutput(newConfig,p,q);
            
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bf.close();
			} catch (IOException e) {

			}
		}
	}

	private static int MaxValue(State state, int alpha, int beta, int depth) {
		count++;
		//CUT-OFF TEST
		if(depth==MAX_DEPTH || isBoardEmpty(state)) {
		  return (state.getxScore()-state.getyScore());	
		}
		int temp_alpha=Integer.MIN_VALUE;
		List<State> sucessors = generateSuccessors(state,true);
		
		for (State s : sucessors) {
			temp_alpha = Math.max(alpha, MinValue(s, alpha, beta, depth + 1));
			
			if(depth==0) {
			  	if(temp_alpha!=alpha)
			  		maxMove=s;
			}
			
			alpha=temp_alpha;
			
			if (alpha >= beta)
				return beta;
		}
		return alpha;
	}

	private static int MinValue(State state, int alpha, int beta, int depth) {
		count++;
		// CUT-OFF TEST
		if (depth == MAX_DEPTH || isBoardEmpty(state)) {
			return (state.getxScore() - state.getyScore());
		}
		
		List<State> sucessors = generateSuccessors(state,false);
		for (State s : sucessors) {
			beta = Math.min(beta, MaxValue(s, alpha, beta, depth + 1));
			if (beta <= alpha)
				return alpha;
		}
		return beta;
	}

	private static List<State> generateSuccessors(State state, boolean isMax) {
		char[][] config = arrayCopier(state.getConfig());
		List<List<Point>> segments = new ArrayList<List<Point>>();
		// generate segments
		for (int i = 0; i < config.length; i++) {
			for (int j = 0; j < config.length; j++) {
				if (config[i][j] == '*')
					continue;
				List<Point> points = new ArrayList<Point>();
				generateSegments(config, points, i, j);
				segments.add(points);
			}
		}

		// Keep max scoring ply on right of the tree
		Collections.sort(segments, new Comparator<List<Point>>() {
			public int compare(List<Point> l1, List<Point> l2) {
				if (l1.size() > l2.size())
					return -1;
				if (l1.size() < l2.size())
					return 1;
				return 0;
			}
		});

		List<State> states = new ArrayList<State>();
		State s = null;
		int changes = 0;
		// apply each segment and return list after applying gravity in each and
		// calculating x and y score
		for (List<Point> list : segments) {
			changes =list.size();
			config = arrayCopier(state.getConfig());
			for (Point p : list) {
				config[p.row][p.col] = '*';
			}

			//apply gravity----> change matrix
			//System.out.println("---------------------------------------");
			//printMatrix(config);
			applyGravity(config);
			//System.out.println("-------------AFTER GRAVITY--------------");
			//printMatrix(config);
			//System.out.println("----------------------------------------");
			
			s = new State(config, state.getxScore(), state.getyScore());
			if (isMax) {
				s.setxScore(s.getxScore() + changes * changes);
			} else {
				s.setyScore(s.getyScore() + changes * changes);
			}
			states.add(s);
		}
		
		/*// Print segments
		System.out.println(segments.size());
		for (List<Point> p : segments) {
			System.out.println(p);
			System.out.println();
		}*/
		 
	/*	// Keep max scoring ply on right of the tree
		Collections.sort(states,new Comparator<State>() {
             public int compare(State s1, State s2) {
				 int l1=s1.getxScore()-s1.getyScore();
				 int l2=s2.getxScore()-s2.getyScore();
            	 
            	 if (l1 > l2)
 					return -1;
 				if (l1 < l2)
 					return 1;
 				return 0; 
           }
		});
		*/
		return states;
	}

	private static void applyGravity(char[][] config) {
          for (int j = 0; j < size; j++) {
            int read = size - 1;
			int write = size - 1;

			while (read >= 0 && write>=0) {
				if (config[write][j] != '*') {
					write--;
				} else {
					if(read>write) {
						read=write-1;
					}
					
					while (read >= 0 && config[read][j] == '*') {
						read--;
					}
					if (read < 0)
						break;

					config[write][j] = config[read][j];
					config[read][j]='*';
					read--;
					write--;
				}
			}
			while (write > 0) {// Fill the rest
				write--;
				config[write][j] = '*';
			}
		}
	}

	
	private static void generateSegments(char[][] config, List<Point> s,int i, int j) {
		s.add(new Point(i, j));
		int val=config[i][j];
		config[i][j]='*';
		if (j+1<size && val == config[i][j + 1]) {
			generateSegments(config, s, i, j + 1);
		}
		if (i+1<size && val== config[i + 1][j]) {
			generateSegments(config, s, i + 1, j);
		}
		if (j-1>=0 && val == config[i][j-1]) {
			 generateSegments(config, s, i, j-1);
		}
		
	}
	
	private static char[][] arrayCopier(char[][] src) {
		char[][] dest = new char[size][size];
		for (int i = 0; i < dest.length; i++) {
			dest[i] = new char[size];
			System.arraycopy(src[i], 0, dest[i], 0, size);
		}
		return dest;
	}
	
	private static void printMatrix(char[][] config) {
        for(int i=0;i<size;i++) {
        	for(int j=0;j<size;j++) {
        		System.out.print(config[i][j]+" ");
        	}
        	System.out.println(); 
        }
     }
	
	private static boolean isBoardEmpty(State state) {
		char[][] config = state.getConfig();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (config[i][j] != '*')
					return false;
			}
		}
		return true;
	}
	
	private static void writeOutput(char[][] newConfig, int p, int q) throws IOException {
		BufferedWriter bw = null;
		try {
			StringBuilder str=new StringBuilder("");
			bw = new BufferedWriter(new FileWriter("src\\output.txt"));
			bw.write((char)(q+65));
			bw.write(String.valueOf(p+1));	
			bw.newLine();		
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					str.append(newConfig[i][j]);
				}
				str.append("\n");
			}
			str.setLength(str.length()-1);
			bw.write(str.toString());		
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				throw e;
			}
		}		
	}
}
