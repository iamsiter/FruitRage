import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PlayingScript {

	static long timeA = 300000, timeB = 300000;
	static boolean isOpponent = false;
	static int scoreA = 0, scoreB = 0;
	static int size, score, fruit;
	static long timeStart = System.currentTimeMillis(), timeFinish = System.currentTimeMillis();
	static char[][] PLAY;
	static {
		readInputFile();
	}

	public static void main(String[] args) {

		try {
			while (timeA >= 0 & timeB >= 0) {
				if (isOpponent) {
					//com.mana.homework2.main(args);
					GamePlay.main(args);
					//RandomAgent.main(args);
					// System.out.println("----A played a move -----");

					// update A score
					readOutputFile();

					scoreA = scoreA + score;

					if (isBoardEmpty()) {
						break;
					}

					// update time
					timeFinish = System.currentTimeMillis();
					timeA = timeA - (timeFinish - timeStart);
					timeStart = timeFinish;

					// prepare output
					prepareInputFile(timeB);
					isOpponent = false;

				} else {
					homework.main(args);
					// System.out.println("----B played a move -----");

					// update B score
					readOutputFile();

					scoreB = scoreB + score;

					if (isBoardEmpty()) {
						break;
					}

					// update time
					timeFinish = System.currentTimeMillis();
					timeB = timeB - (timeFinish - timeStart);
					timeStart = timeFinish;

					// prepare output
					prepareInputFile(timeA);
					isOpponent = true;
				}
			}

			System.out.println("-----------STATS---------");
			System.out.println("TIME LEFT FOR  Opp :"+timeA/1000+"s,"+timeA%1000+" ms");
			System.out.println("TIME LEFT FOR  You :"+timeB/1000+"s,"+timeA%1000+" ms");
			System.out.println("SCORE Opp :"+scoreA);
			System.out.println("SCORE You :"+scoreB);
			System.out.println("-------------------------");

			if (timeA <= 0) {
				System.out.println("You won");
			}
			if (timeB <= 0) {
				System.out.println("Opp won");
			}
			if (isBoardEmpty()) {
				if (scoreA > scoreB) {
					System.out.println("Opp won");
				} else if (scoreA < scoreB) {
					System.out.println("You won");
				} else {
					if (timeA > timeB) {
						System.out.println("Opp won");
					} else if (timeA < timeB) {
						System.out.println("You won");
					} else {
						System.out.println("DRAW");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void readInputFile() {
		BufferedReader bf = null;
		try {
			bf = new BufferedReader(new FileReader(new File("src\\input.txt")));
			size = Integer.parseInt(bf.readLine());
			fruit = Integer.parseInt(bf.readLine());
	        double timeLeft = Double.parseDouble(bf.readLine());

			PLAY = new char[size][size];
			String str = null;
			int row = 0, col = 0;
			while ((str = bf.readLine()) != null) {
				str = str.trim();
				col = 0;
				for (int i = 0; i < str.length(); i++) {
					PLAY[row][col] = str.charAt(i);
					col++;
				}
				row++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				bf.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static boolean isBoardEmpty() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (PLAY[i][j] != '*')
					return false;
			}
		}
		return true;
	}

	private static void prepareInputFile(long timeLeft) throws IOException {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter("src\\input.txt"));
			bw.write(String.valueOf(size));
			bw.newLine();
			bw.write(String.valueOf(fruit));// fruit value
			bw.newLine();
			bw.write(String.valueOf(timeLeft));
			bw.newLine();
			StringBuilder str = new StringBuilder();
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					str.append(PLAY[i][j]);
				}
				str.append("\n");
			}
			str.setLength(str.length() - 1);
			bw.write(str.toString());
		} catch (IOException e) {
			try {
				throw e;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				throw e;
			}
		}

	}

	private static void readOutputFile() {
		BufferedReader bf = null;
		try {
			bf = new BufferedReader(new FileReader(new File("src\\output.txt")));
			String move = bf.readLine();
			score = Integer.parseInt(bf.readLine());
			PLAY = new char[size][size];
			String str = null;
			int row = 0, col = 0;
			while ((str = bf.readLine()) != null) {
				str = str.trim();
				col = 0;
				for (int i = 0; i < str.length(); i++) {
					PLAY[row][col] = str.charAt(i);
					col++;
				}
				row++;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				bf.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
