import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class RandomAgent {

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
    static int countMax=0;
    static int countMin=0;
    static int count=0;

	static int size=0;
	static float timeleft=0;
	static State maxMove=null;
	static int MAX_DEPTH=1;
	static String fruit=null;
	
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
			
			System.out.println("RANDOMEMILIS:"+(t2-t1));
			/*System.out.println("MINMAXVALUE::"+minMaxValue);
			System.out.println("COUNTMIN::"+countMin);
			System.out.println("COUNTMAX::"+countMax);
			System.out.println("TOTAL:"+(countMax+countMin));
			System.out.println("COUNT::"+count);*/

            //Print selection for next move
			char[][] newConfig=maxMove.getConfig();
			int score=maxMove.xScore-maxMove.yScore;//TODO:remove it ------
			writeOutput(newConfig,score,fruit);
			
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
		countMax++;		
		//CUT-OFF TEST
		if(depth==MAX_DEPTH) {
			return (state.getxScore()-state.getyScore());	
		}
		
		int temp_alpha=Integer.MIN_VALUE;
		char[][] config = arrayCopier(state.getConfig());
		List<List<Point>> segments = findSegments(config);

		if (segments.isEmpty()) {
			return (state.getxScore() - state.getyScore());
		}
		
		for (List<Point> list : segments) {
			int changes = 0;
			// apply applying gravity in each segment and calculating x and y score
			changes = list.size();
			config = arrayCopier(state.getConfig());
			for (Point p : list) {
				config[p.row][p.col] = '*';
			}

			applyGravity(config);
			State s = new State(config, state.getxScore(), state.getyScore());
			s.setxScore(s.getxScore() + changes * changes);

	        temp_alpha = Math.max(alpha, MinValue(s, alpha, beta, depth + 1));
			
			if(depth==0) {
				if(temp_alpha!=alpha) {
					maxMove=s;
					int a = list.get(0).row;
					int b = list.get(0).col;
					fruit = String.valueOf((char) (b + 65)) + String.valueOf(a + 1);
				}			
			}
			
			alpha=temp_alpha;
			
			if (alpha >= beta) {
			    count++;
				return beta;
			}
		}
		
		return alpha;
	}

	private static int MinValue(State state, int alpha, int beta, int depth) {
		countMin++;

		// CUT-OFF TEST
		if (depth == MAX_DEPTH) {
			return (state.getxScore() - state.getyScore());
		}

		char[][] config = arrayCopier(state.getConfig());
		List<List<Point>> segments = findSegments(config);

		if (segments.isEmpty()) {
			return (state.getxScore() - state.getyScore());
		}

		for (List<Point> list : segments) {
			int changes = 0;
			// apply applying gravity in each segment and calculating x and y score
			changes = list.size();
			config = arrayCopier(state.getConfig());
			for (Point p : list) {
				config[p.row][p.col] = '*';
			}

			applyGravity(config);
			State s = new State(config, state.getxScore(), state.getyScore());
			s.setyScore(s.getyScore() + changes * changes);
			beta = Math.min(beta, MaxValue(s, alpha, beta, depth + 1));
			if (beta <= alpha) {
				count++;
				return alpha;
			}
		}
		return beta;
	}

	private static List<List<Point>> findSegments(char[][] config) {
        List<List<Point>> segments=new ArrayList<List<Point>>();
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
				return l2.size() - l1.size();
			}
		});
		
		Random r=new Random();
		int i =r.nextInt(segments.size());
		
		return Collections.singletonList(segments.get(i));
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
		if (i-1>=0 && val == config[i-1][j]) {
			 generateSegments(config, s, i-1, j);
		}
	}
	
	private static char[][] arrayCopier(char[][] src) {
		char[][] dest = new char[size][size];
		for (int i = 0; i < dest.length; i++) {
			for(int j = 0; j < dest.length; j++) {
				dest[i][j]=src[i][j];
			}
		}
		return dest;
	}
	
	private static void printMatrix(char[][] config) {//remove
        for(int i=0;i<size;i++) {
        	for(int j=0;j<size;j++) {
        		System.out.print(config[i][j]+" ");
        	}
        	System.out.println(); 
        }
     }
	
	private static boolean isBoardEmpty(State state) {//remove
		char[][] config = state.getConfig();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (config[i][j] != '*')
					return false;
			}
		}
		return true;
	}
	
	private static void writeOutput(char[][] newConfig,int score,String f) throws IOException {
		BufferedWriter bw = null;
		try {
			StringBuilder str=new StringBuilder("");
			bw = new BufferedWriter(new FileWriter("src\\output.txt"));
			//output fruit to select
			bw.write(f);
			bw.newLine();
			
			
			bw.write(String.valueOf(score));//remove 
			bw.newLine();	//remove

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
