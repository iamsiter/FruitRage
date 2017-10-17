
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GamePlay implements contract {

    public  static int plyValue =6;
    public static String INPUT_FILE_NAME = "src\\input.txt";
    public static String OUTPUT_FILE_NAME = "src\\output.txt";
    public static int boardSize = 0;
    public static int numberOfFruits = 0;
    public static double timeLeft = 0;
    public static char[][] board;
    public static char[][] copyboard;
    public static int xScore = 0;
    public static int yScore = 0;
    public static boolean isXscore;
    public static int minCount = 0;
    public static int maxCount = 0;
    public static int pruneCount=0;
    public static int solXValue=0;
    public static int solYValue=0;
    public static int moveScore =0;


    public static void main(String[] args) throws IOException {
        FileReader fileReader = new FileReader(INPUT_FILE_NAME);
        BufferedReader br = new BufferedReader(fileReader);
        boardSize = Integer.parseInt(br.readLine());
        numberOfFruits = Integer.parseInt(br.readLine());
        timeLeft = Double.parseDouble(br.readLine());
        board = new char[boardSize][boardSize];
        copyboard = new char[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            String[] line = br.readLine().split("");
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = line[j].charAt(0);
                copyboard[i][j] = line[j].charAt(0);
            }
        }
//        System.out.println("Showing Board");
//        showBoard(board);
        isXscore=true;
        long ts=System.currentTimeMillis();
        GamePlay gamePlay = new GamePlay(boardSize, numberOfFruits, timeLeft, board);
        char[][] board = gamePlay.getSolutionForBoard(isXscore);
        long tf=System.currentTimeMillis();
		System.out.println("MILISSSSSSSSSSSS:"+(tf-ts));

        
//        System.out.println("The solution board is: ");
//        showBoard(board);
//        System.out.println();
        writeToFile(board);
//        replay(board,8);

        //Read input from input.txt
        //Ouput - NextMove like B3
        // Optional to be removed later = Score of the move
        // Ouput matrix

    }
 public static void replay(char[][] replayBoard, int count)
 {
     if(count--==0)
         return;
     board = new char[boardSize][boardSize];
     copyboard = new char[boardSize][boardSize];
     for (int i = 0; i < boardSize; i++) {
         for (int j = 0; j < boardSize; j++) {
             board[i][j] = replayBoard[i][j];
             copyboard[i][j] = replayBoard[i][j];
         }
     }
//     System.out.println("Showing Board");
//     showBoard(board);
     GamePlay gamePlay = new GamePlay(boardSize, numberOfFruits, timeLeft, board);
     isXscore = !isXscore;
     char[][] board = gamePlay.getSolutionForBoard(isXscore);
     System.out.println("The solution board is:");
     showBoard(board);
     System.out.println();

     replay(board,count);


 }


    public GamePlay(int boardSize, int numberOfFruits, double timeLeft, char[][] board) {
        this.boardSize = boardSize;
        this.numberOfFruits = numberOfFruits;
        this.timeLeft = timeLeft;
        this.board = board;
    }

    public static void writeToFile(char[][] solMatrix) throws IOException {
        FileWriter fileWriter = new FileWriter(OUTPUT_FILE_NAME);
        BufferedWriter br = new BufferedWriter(fileWriter);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Character.toString((char)(solYValue+65)));
        stringBuilder.append(String.valueOf(solXValue));
        stringBuilder.append("\n");
        stringBuilder.append(moveScore);
        stringBuilder.append("\n");
        for(int i=0;i<boardSize;i++)
        {
            for(int j=0;j<boardSize;j++)
            {
                stringBuilder.append(solMatrix[i][j]);
            }
            stringBuilder.append("\n");
        }
        br.write(stringBuilder.toString());
//        System.out.println("Wrote to File");
        br.close();

    }
    public char[][] getSolutionForBoard(boolean isXscore) {

        long before = System.currentTimeMillis();
        //STAR THE CALLS HERE
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        ArrayList<Move> allMoves = getAllMoves(board);

        // ADD CONDITION HERE IF NO VALID MOVES ON THE FIRST MOVE ITSELF.
        Collections.sort(allMoves, new Comparator<Move>() {
            @Override
            public int compare(Move o1, Move o2) {
                return o2.getSize() - o1.getSize();
            }
        });
//        for(Move move:allMoves)
//        {
//            System.out.println("Size: "+move.getSize());
//            for(Point p :move.getPoints())
//                System.out.print("["+p.getX()+","+p.getY()+"],");
//            System.out.println();
//        }
//        System.out.println("-----------------------------------");
//        printAllMoves(allMoves);
        //Returns all sets of points that can be tried of ALL SIZES
        int optimalMoveNumber = -1;//finally this will tell us which move to select on the BOARD===== IMPORTANT FOR THIS TO BE CORRECT
        int maxValue = Integer.MIN_VALUE;
        char fruit='*';
        int moveNumber = -1;
        ArrayList<Node> allChildren = new ArrayList<Node>();
        for (Move curMove : allMoves) {
            // increment the moveCount to select optimal solution when found
            moveNumber++;
            //Generate Node from this move after applying gravity on the current board
            Node child = new Node(new Node(copyboard), (int) Math.pow(curMove.getSize(), 2), 0);
            child.applyGravity(curMove);
//            child.dummyGravityApply(curMove);
            allChildren.add(child); // adding all the children to print out the BEST MOVE SOLUTION FINALLY
            int plycopy = plyValue;
            int curBranchValue = minValue(child, plycopy, alpha, beta);
//            System.out.println((moveNumber+1)+ " Branch Value of Root : "+curBranchValue );
//            a = Math.max(a, );
            if (curBranchValue > alpha) {
                alpha = curBranchValue;
                optimalMoveNumber = moveNumber;//
                fruit = copyboard[curMove.getPoints().get(0).getX()][curMove.getPoints().get(0).getY()];
//                System.out.println("Incremented");
            }
            // CODE TO CHECK IF THE APPLIED GRAVITY WAS CORRECT
            /*System.out.println("\n------------BOARD AFTER A MOVE------");
            for (Point p: curMove.getPoints())
            {
                System.out.println("X: "+p.getX()+" Y:"+p.getY());
            }
            showBoard(child.getBoard());*/
            // Node created for recursive miniMax calls here................
        }
        if(allMoves.isEmpty())
            moveScore = 0;
        long after = System.currentTimeMillis();
        System.out.println("Time taken: "+(after-before)+"ms");
        if(!allMoves.isEmpty()) {
//            System.out.println("Optimal Move for " + (isXscore ? "X" : "Y") + " was: " + allMoves.get(optimalMoveNumber).getPoints().get(0));
            moveScore = (int) Math.pow(allMoves.get(optimalMoveNumber).getSize(), 2);
            solXValue = allMoves.get(optimalMoveNumber).getPoints().get(0).getX();
            solYValue = allMoves.get(optimalMoveNumber).getPoints().get(0).getY();
        }
        System.out.println("Move Score: "+ moveScore );
        System.out.println("Fruit number : "+String.valueOf(fruit));
        System.out.println("Total Max Function Count: "+maxCount);
        System.out.println("Total Min Count:"+ minCount);
        System.out.println("Total Prune Count: "+ pruneCount);
        System.out.println("Total Function count: "+ (maxCount+minCount));
//        if(isXscore)
//            xScore+=Math.pow((int)allMoves.get(optimalMoveNumber).getSize(),2);
//        else
//            yScore+=Math.pow((int)allMoves.get(optimalMoveNumber).getSize(),2);
//        System.out.println("X score: "+ xScore + "  Y score: "+yScore);
        if(!allChildren.isEmpty())
        return allChildren.get(optimalMoveNumber).getBoard();
        else
            return copyboard ;
    }

    public int maxValue(Node node, int ply, int alpha, int beta) {
        maxCount++;
        // initially a = Integer.MIN
        // initially b = Integer.MAX
        // Calculate all moves from this node
        // calculate all moveGroups from this node here.
        //sort those moveGroups here
        // loop on all moveGroups one by one and apply gravity ONE BY ONE.. DO NOT APPLY GRAVITY ON ALL AS THEY MIGHT BE PRUNED.
        // apply gravity on all moveGroups here and get an arraylist of nodes where each node is a child of node
        // if cutOffTest(node)==( ply=0 or no moves left) then return node.getEval()
        Node copyNode = new Node(node.getBoard()); // copyNode would be exhausted in simply finding out all the moves.
        ArrayList<Move> allMoves = getAllMoves(copyNode.getBoard()); // copyNode used instead of Node **
        Collections.sort(allMoves, new Comparator<Move>() {
            @Override
            public int compare(Move o1, Move o2) {
                return o2.getSize() - o1.getSize();
            }
        });
        //CUTOFF TESTS
        if (allMoves.isEmpty())// no valid moves left -- Game over here
        {
            return node.getEval(); // x-y for the current state returned
        }
        ply--;
        if (ply == 0)
            return node.getEval();
        //CUTOFF TESTS
        for (Move curMove : allMoves) {
            //Generate Node from this move after applying gravity on the current board
            Node child = new Node(node, (int) Math.pow(curMove.getSize(), 2), 0);// applying and adding to X value here as MAX FUNCTION
            child.applyGravity(curMove);
//            child.dummyGravityApply(curMove);
            int curBranchValue = minValue(child, ply, alpha, beta);
//            a = Math.max(a, );
            if (curBranchValue > alpha) {
                alpha = curBranchValue;
            }
            if (alpha >= beta) {
                pruneCount++;
                return beta; // Pruning Condition for MIN
            }

        }
        return alpha;
    }

    public int minValue(Node node, int ply, int alpha, int beta) {
        minCount++;
        Node copyNode = new Node(node.getBoard()); // copyNode would be exhausted in simply finding out all the moves.
        ArrayList<Move> allMoves = getAllMoves(copyNode.getBoard()); // copyNode used instead of Node **
        Collections.sort(allMoves, new Comparator<Move>() {
            @Override
            public int compare(Move o1, Move o2) {
                return o2.getSize() - o1.getSize();
            }
        });
        //CUTOFF TESTS
        if (allMoves.isEmpty())// no valid moves left -- Game over here
        {
            return node.getEval(); // x-y for the current state returned
        }
        ply--;
        if (ply == 0)
            return node.getEval();
        //CUTOFF TESTS
        for (Move curMove : allMoves) {
            //Generate Node from this move after applying gravity on the current board
            Node child = new Node(node,0,(int) Math.pow(curMove.getSize(), 2));// applying and adding to Y value here as MIN FUNCTION
            child.applyGravity(curMove);
//            child.dummyGravityApply(curMove);
            int curBranchValue = maxValue(child, ply, alpha, beta);
//            a = Math.max(a, );
            if (curBranchValue < beta) {
                beta = curBranchValue;
            }
            if(beta<=alpha) {
                pruneCount++;
                return alpha; // Pruning Condition for MAX
            }

        }

        return beta;
    }

    public void printAllMoves(ArrayList<Move> allMoves) {
        System.out.println("Total Number of Moves : " + allMoves.size());
        for (Move moveGroup : allMoves) {
            System.out.println("Move Group Fruit Type = " + copyboard[moveGroup.getPoints().get(0).getX()][moveGroup.getPoints().get(0).getY()]);

            for (Point point : moveGroup.getPoints()) {
                System.out.println("X:" + point.getX() + " Y:" + point.getY());
            }
            System.out.println();
        }
    }

    public ArrayList<Move> getAllMoves(char[][] board) {
    	long ts=System.currentTimeMillis();
        ArrayList<Move> allMoves = new ArrayList<Move>();
//        for (int i = boardSize - 1; i >= 0; i--) {
//            for (int j = 0; j < boardSize; j++) {
//                if (board[i][j] != '*') { // to check if point not already part of some other SET of MOVE GROUP
//                    ArrayList<Point> points = propagatePoint(board, i, j);
//                    allMoves.add(new Move(points));
//                }
//            }
//        }
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] != '*') { // to check if point not already part of some other SET of MOVE GROUP
                    ArrayList<Point> points = propagatePoint(board, i, j);
                    allMoves.add(new Move(points));
                }
            }
        }
        
    	long tf=System.currentTimeMillis();
        //System.out.println("milllis"+(tf-ts));
        
        
        return allMoves;
    }


    public ArrayList<Point> propagatePoint(char[][] board, int x, int y) {
        //filler function to get a List of points from the next recursive function as a single arrayList
        ArrayList<Point> curMove = new ArrayList<Point>();
        curMove.add(new Point(x, y)); // add the current point and look recursively around this
        recursiveProp(curMove, board, x, y, board[x][y]);
        return curMove;
    }


    public void recursiveProp(ArrayList<Point> arr, char[][] board, int x, int y, char ele) {

        //mark this place as visited on the board
        board[x][y] = '*';

        int newX;
        int newY;
        // GOING UP IN THE SAME COLUMN
        newX = x - 1;
        newY = y;
        if (isWithinBoardLimits(newX, newY))// not to be considered if x,y out of the board
        {
            if (board[newX][newY] == ele) {
                //add these new coordinates to the arraylist
                arr.add(new Point(newX, newY));
                recursiveProp(arr, board, newX, newY, ele);
            }
        }

        // GOING DOWN IN THE SAME COLUMN
        newX = x + 1;
        newY = y;
        if (isWithinBoardLimits(newX, newY))// not to be considered if x,y out of the board
        {
            if (board[newX][newY] == ele) {
                //add these new coordinates to the arraylist
                arr.add(new Point(newX, newY));
                recursiveProp(arr, board, newX, newY, ele);
            }
        }

        // GOING LEFT IN THE SAME ROW
        newX = x;
        newY = y - 1;
        if (isWithinBoardLimits(newX, newY))// not to be considered if x,y out of the board
        {
            if (board[newX][newY] == ele) {
                //add these new coordinates to the arraylist
                arr.add(new Point(newX, newY));
                recursiveProp(arr, board, newX, newY, ele);
            }
        }

        // GOING RIGHT IN THE SAME ROW
        newX = x;
        newY = y + 1;
        if (isWithinBoardLimits(newX, newY))// not to be considered if x,y out of the board
        {
            if (board[newX][newY] == ele) {
                //add these new coordinates to the arraylist
                arr.add(new Point(newX, newY));
                recursiveProp(arr, board, newX, newY, ele);
            }
        }


    }

    public static void showBoard(char board[][]) {

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
    }

    public boolean isWithinBoardLimits(int x, int y) {
        if (x < boardSize && x >= 0 && y < boardSize && y >= 0)
            return true;
        else
            return false;
    }

    class Node {
        char board[][];
        int xVal; // the score for MAX
        int yVal; // the score for MIN

        public Node(char[][] board) {
            this.board = new char[boardSize][boardSize];
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    this.board[i][j] = board[i][j];
                }
                xVal = 0;
                yVal = 0;
            }
        }

        public Node(Node node, int x, int y) { // copy constructor
            this.board = new char[boardSize][boardSize];// initialize the object's board
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    this.board[i][j] = node.getBoard()[i][j];
                }
            }
            this.xVal = node.getxVal() + x; // parent node value plus the current move value
            this.yVal = node.getyVal() + y; // parent node value plus the current move value
        }

        public char[][] getBoard() {
            return board;
        }

        public void setBoard(char[][] board) {
            this.board = board;
        }

        public int getxVal() {
            return xVal;
        }

        public void setxVal(int xVal) {
            this.xVal = xVal;
        }

        public int getyVal() {
            return yVal;
        }

        public void setyVal(int yVal) {
            this.yVal = yVal;
        }

        public int getEval() {
            return xVal - yVal;
        }

        public void applyGravity(Move move) {
            //Using the point inside this move apply the gravity on the board DS. Make change to the board DS.

            //FIRST make * on all move points
            for (Point p : move.getPoints()) {
                this.board[p.getX()][p.getY()] = '*';
            }
            for (int j = 0; j < boardSize; j++) {
                //traversing in a column here
                int x = boardSize - 1;
                int y = j;
                // x and y are the bottom elements of the column to start with
                for (; x >= 0; x--) {
                    if (board[x][y] != '*')// if its a number I am okay. Move ahead
                        continue;
                    else// its a star == '*'
                    {
                        int nextXwhereNumberPresent = this.getNextAboveColumnFruitNumber(x, y);
                        if (nextXwhereNumberPresent == -1)// all above are *'s
                        {
                            break; // we're done applying gravity on this column
                        } else {
                            //swap the two numbers
                            char temp = this.board[x][y];
                            this.board[x][y] = this.board[nextXwhereNumberPresent][y];
                            this.board[nextXwhereNumberPresent][y] = temp;
                        }
                    }
                }
            }
        }

        public void dummyGravityApply(Move move)
        {
            char[][] myBoard = new char[boardSize][boardSize];

            //Changing the node board to starred board without gravity
            //FIRST make * on all move points
            for (Point p : move.getPoints()) {
                this.board[p.getX()][p.getY()] = '*';
            }

            //making myBoard the gravity applied board
            for(int j=0;j<boardSize;j++)
            {//iterating each column
                int myBoardIcounter=boardSize-1;
                for(int i=boardSize-1;i>=0;i--)
                {
                    if(this.board[i][j]=='*')
                        continue;
                    else
                    {//
                        myBoard[myBoardIcounter][j]=this.board[i][j];
                        myBoardIcounter--;

                    }
                }
                while(myBoardIcounter>=0)
                {
                    myBoard[myBoardIcounter][j]='*';
                    myBoardIcounter--;
                }

            }
        this.board = myBoard;
        }

        public int getNextAboveColumnFruitNumber(int row, int col) {
            //col remains constant here
            for (int i = row; i >= 0; i--) {
                if (this.board[i][col] != '*')
                    return i;
            }
            return -1;// if all of the above values are *
        }
    }

    class Move {
        public ArrayList<Point> points;

        public Move() {
            points = new ArrayList<Point>();
        }

        public Move(ArrayList<Point> points) {
            this.points = points;
        }

        public int getSize() {
            return points.size();
        }

        public ArrayList<Point> getPoints() {
            return points;
        }
    }


    static class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Point point = (Point) o;

            if (x != point.x) return false;
            return y == point.y;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }
    }

}

interface contract {

    // the starter function for the solution
    public char[][] getSolutionForBoard(boolean isXscore);

    //returns all moves for a current boardPosition
    public ArrayList<GamePlay.Move> getAllMoves(char[][] board);


    // returns the PointList if a point x,y is propagated
    // An official move for output.txt can be any of the points inside this arraylist.
    //filler function to get a List of points from the next recursive function as a single arrayList
    public ArrayList<GamePlay.Point> propagatePoint(char[][] board, int x, int y);

    // For recursively calculating all the starred points if we hit x,y
    public void recursiveProp(ArrayList<GamePlay.Point> arr, char[][] board, int x, int y, char ele);

    public boolean isWithinBoardLimits(int x, int y);

}
