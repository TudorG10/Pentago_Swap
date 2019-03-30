package student_player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;
import pentago_swap.PentagoBoardState.Piece;
import pentago_swap.PentagoBoardState.Quadrant;
import pentago_swap.PentagoCoord;

public class MyTools {
	//TODO in general
	/*
	 * 
	 */
    private static final int QUAD_SIZE = 3;
    private static final int NUM_QUADS = 4;
    private static Piece[][] myBoard;
	private static Piece[][][] myQuadrants;
    private static final int BOARD_SIZE = 6;
    private static final int NUM_ROLLS = 50;
    private static HashMap<Quadrant, Integer> quadToInt;
    static {
        quadToInt = new HashMap<>(4);
        quadToInt.put(Quadrant.TL, 0);
        quadToInt.put(Quadrant.TR, 1);
        quadToInt.put(Quadrant.BL, 2);
        quadToInt.put(Quadrant.BR, 3);
    }



    public static PentagoMove goFirst(PentagoBoardState boardState, int id) {
    	double[][] moveWorth = new double[BOARD_SIZE][BOARD_SIZE];//weight array
    	Piece myPiece;
    	if(id == 0) {
    		myPiece = Piece.WHITE;
    	}
    	else {
    		myPiece = Piece.BLACK;
    	}
//    	System.out.println("white compared to black: " + Piece.WHITE.compareTo(Piece.BLACK));
//    	System.out.println("black compared to white: " + Piece.BLACK.compareTo(Piece.WHITE));
    	
    	ArrayList<PentagoMove> moves = boardState.getAllLegalMoves();
        if(boardState.getTurnNumber() == 0) {
        	return hardMoves(boardState, id);
        }
        else if(boardState.getTurnNumber() == 1) {
        	return hardMoves(boardState, id);
        }

        else {        	
        	
            makeWorthArray(boardState, moveWorth, myPiece);//array of weights
//            makeBoardFromWorthArray(moveWorth, boardState);//my very own board State Piece[][]
//    		printWorthArray(moveWorth);
    		/*
            ArrayList<PentagoMove> chosenMoves = winOrBlockWin(boardState,id);//available move
            PentagoMove chosenMove = null;
            double bestPlay = 0;
            
            for(PentagoMove move : chosenMoves) {
//            	System.out.print("moveList: " + move.toPrettyString());
            	double moveVal = Math.max(bestPlay, moveWorth[move.getMoveCoord().getY()][move.getMoveCoord().getX()]);
            	if(moveVal > bestPlay) {
            		bestPlay = moveVal;
            		chosenMove = move;
            	}
            }
            */
//        	Random rand = new Random();
//            int n = rand.nextInt(moves.size());//get random move from remaining moves
//        	System.out.println("\nCHOSEN MOVE: " + chosenMove.toPrettyString());
//        	System.out.println("BEST VAL: " + bestPlay);


//            return chosenMove;
            return MonteCarloLite(boardState, id);
        }
 
    }

	private static PentagoMove hardMoves(PentagoBoardState boardState, int id) {
		PentagoMove move11 = new PentagoMove(1, 1, Quadrant.BL, Quadrant.BR, id);
		PentagoMove move14 = new PentagoMove(1, 4, Quadrant.BL, Quadrant.BR, id);
		PentagoMove move41 = new PentagoMove(4, 1, Quadrant.TL, Quadrant.TR, id);
		PentagoMove move44 = new PentagoMove(4, 4, Quadrant.TL, Quadrant.TR, id);
		ArrayList<PentagoMove> myMoves = new ArrayList<>();
		myMoves.add(move11);
		myMoves.add(move14);
		myMoves.add(move41);
		myMoves.add(move44);
		
		for(PentagoMove m : myMoves) {
			if(boardState.isPlaceLegal(m.getMoveCoord())) {
		    	return m;
			}
		}
		return null;
	}

    private static double checkIfAnother(PentagoBoardState currentState, PentagoCoord nextLoc, int deltaX, int deltaY, Piece myPiece) {
   
    	boolean nextOK = true;
    	PentagoCoord newCoord = null;
		Piece newPiece = currentState.getPieceAt(nextLoc);
		
    	try {
		 	newCoord = new PentagoCoord(nextLoc.getX() + deltaX, nextLoc.getY() + deltaY);
    	}
		catch(IllegalArgumentException e){
			nextOK = false;
		}

		if(newPiece.compareTo(Piece.EMPTY) == 0) {
			return 0;
		}
		else if(newPiece.compareTo(myPiece) == 0) {
			if(nextOK){
				return 1 + 1.5 * checkIfAnother(currentState, newCoord, deltaX, deltaY, myPiece);
			}
			else {
				return 1;
			}
		}
		else {
			if(nextOK){
				return 0.5 + 1.2 * checkIfAnother(currentState, newCoord, deltaX, deltaY, myPiece);
			}
			else {
				return 0.5;
			}

		}

    }
    
	private static void makeWorthArray(PentagoBoardState currentState,double[][] moveWorth, Piece myPiece) {
		
		for(int x = 0; x < BOARD_SIZE; x++) {
			for(int y = 0; y < BOARD_SIZE; y++) {
		    	PentagoCoord curCoord = new PentagoCoord(x,y);

				Piece aPiece = currentState.getPieceAt(curCoord);
				if(aPiece == Piece.EMPTY) {//empty tile, means we might place it there
					boolean ok0 = true;
					boolean ok1 = true;
					boolean ok2 = true;
					boolean ok3 = true;
					boolean ok4 = true;
					boolean ok5 = true;
					boolean ok6 = true;
					boolean ok7 = true;
			    	PentagoCoord newCoord0 = null;
			    	PentagoCoord newCoord1 = null;
			    	PentagoCoord newCoord2 = null;
			    	PentagoCoord newCoord3 = null;
			    	PentagoCoord newCoord4 = null;
			    	PentagoCoord newCoord5 = null;
			    	PentagoCoord newCoord6 = null;
			    	PentagoCoord newCoord7 = null;

					try {
				    	 newCoord0 = new PentagoCoord(x-1,y-1);//0
					}
					catch(IllegalArgumentException e){
						ok0 = false;
					}
					try {
				    	 newCoord1 = new PentagoCoord(x,y-1);//1
					}
					catch(IllegalArgumentException e){
						ok1 = false;
					}
					try {
				    	 newCoord2 = new PentagoCoord(x+1,y-1);//2
					}
					catch(IllegalArgumentException e){
						ok2 = false;
					}
					try {
				    	 newCoord3 = new PentagoCoord(x+1,y);//3
					}
					catch(IllegalArgumentException e){
						ok3 = false;
					}
					try {
				    	 newCoord4 = new PentagoCoord(x+1,y+1);//4
					}
					catch(IllegalArgumentException e){
						ok4 = false;
					}
					try {
				    	 newCoord5 = new PentagoCoord(x,y+1);//5
					}
					catch(IllegalArgumentException e){
						ok5 = false;
					}
					try {
				    	 newCoord6 = new PentagoCoord(x-1,y+1);//6
					}
					catch(IllegalArgumentException e){
						ok6 = false;
					}
					try {
				    	 newCoord7 = new PentagoCoord(x-1,y);//7
					}
					catch(IllegalArgumentException e){
						ok7 = false;
					}

					if(ok0) {
						moveWorth[y][x] += checkIfAnother(currentState, newCoord0, -1, -1, myPiece);

					}
					if(ok1) {
						moveWorth[y][x] += checkIfAnother(currentState, newCoord1, 0, -1, myPiece);

					}
					if(ok2) {
						moveWorth[y][x] += checkIfAnother(currentState, newCoord2, 1, -1, myPiece);

					}
					if(ok3) {
						moveWorth[y][x] += checkIfAnother(currentState, newCoord3, 1, 0, myPiece);

					}
					if(ok4) {
						moveWorth[y][x] += checkIfAnother(currentState, newCoord4, 1, 1, myPiece);

					}
					if(ok5) {
						moveWorth[y][x] += checkIfAnother(currentState, newCoord5, 0, 1, myPiece);

					}
					if(ok6) {
						moveWorth[y][x] += checkIfAnother(currentState, newCoord6, -1, 1, myPiece);

					}
					if(ok7) {
						moveWorth[y][x] += checkIfAnother(currentState, newCoord7, -1, 0, myPiece);

					}
				}
			}
		}
	}
	private static void printWorthArray(double[][] moveWorth) {
		for(int y = 0; y < BOARD_SIZE; y++) {
    		for(int x = 0; x < BOARD_SIZE; x++) {
    			System.out.print("["+moveWorth[x][y]+"]\t");
    		}
			System.out.println("");
    	}
	}   
    private static PentagoMove MonteCarloLite(PentagoBoardState boardState, int id) {
    	int myID = id;
    	int opID;
    	if(myID == 0) {
    		opID = 1;
    	}
    	else {
    		opID = 0;
    	}
    	
    	ArrayList<PentagoMove> myMove = boardState.getAllLegalMoves();
    	Integer[] bestBranch = new Integer[myMove.size()];
    	for(int j = 0; j < bestBranch.length; j++) {
    		bestBranch[j] = 0;
    	}
    	int i = 0;
        for(PentagoMove m : myMove) {
        	PentagoBoardState myNewBoard = (PentagoBoardState) boardState.clone();
            myNewBoard.processMove((PentagoMove) m);
            if(myNewBoard.getWinner() == myID) {//case i can win next move
            	bestBranch[i] +=1000;
            }
            
            for(int numRolls = 0; numRolls < NUM_ROLLS; numRolls++) {//do 10 rollouts
            	bestBranch[i] += rollOutProtocol(myNewBoard, myID, opID);

            }

            
          
            i++;
        }

        int index = getIndexOfMax(bestBranch);
//        printValArray(bestBranch);
//        System.out.println("Index: "+ index + " Chosen move: " + (myMove.get(index)).toPrettyString() );
		return myMove.get(index);
    }

    private static PentagoMove MonteCarlo(PentagoBoardState boardState, int id) {
    	//TODO pass the list of moves to work with to reduce computation time
    	int myID = id;
    	int opID;
    	if(myID == 0) {
    		opID = 1;
    	}
    	else {
    		opID = 0;
    	}
    	int i = 0;
    	ArrayList<PentagoMove> myMove = boardState.getAllLegalMoves();
    	Integer[] bestBranch = new Integer[myMove.size()];

        for(PentagoMove m : myMove) {
        	PentagoBoardState myNewBoard = (PentagoBoardState) boardState.clone();
            myNewBoard.processMove((PentagoMove) m);
            if(myNewBoard.getWinner() == myID) {//case i can win next move
            	bestBranch[i] +=1000;
//            	return 100;
            }
            int j = 0;
        	ArrayList<PentagoMove> movesOpponent = myNewBoard.getAllLegalMoves();
        	Integer[] bestBranch2 = new Integer[movesOpponent.size()];

            for(PentagoMove mOp : movesOpponent) {
            	PentagoBoardState opNewBoard = (PentagoBoardState) myNewBoard.clone();
            	opNewBoard.processMove((PentagoMove) mOp);

                if(opNewBoard.getWinner() == opID) {//case opponent wins next move
                	bestBranch2[j] -=100;
//                	return -100;
                }
                int k = 0;
            	ArrayList<PentagoMove> myMoves2 = myNewBoard.getAllLegalMoves();
            	Integer[] bestBranch3 = new Integer[myMoves2.size()];

                for(PentagoMove m2 : myMoves2) {
                	PentagoBoardState myNewBoard2 = (PentagoBoardState) opNewBoard.clone();
                    myNewBoard.processMove((PentagoMove) m2);
                    if(myNewBoard.getWinner() == myID) {//case i can win next move
                    	bestBranch3[k] +=50;
//                    	return 50;
                    }
                    
                    for(int numRolls = 0; numRolls < NUM_ROLLS; numRolls++) {//do 10 rollouts
                    	bestBranch3[k] += rollOutProtocol(myNewBoard2, myID, opID);
                    }
                    bestBranch3[k] = bestBranch3[k/NUM_ROLLS];//value of branch is average of number of wins + losses + ties 
                    k++;

                }
                bestBranch2[j] = Collections.max(Arrays.asList(bestBranch3));
                bestBranch2[j] = bestBranch2[j/myMoves2.size()];
                j++;

            }
            bestBranch[i] = Collections.max(Arrays.asList(bestBranch2));
            bestBranch[i] = bestBranch2[i/movesOpponent.size()];
            i++;
        }
//        int bestMove = Collections.max(Arrays.asList(bestBranch));
        int indexOfBest = getIndexOfMax(bestBranch);
        return myMove.get(indexOfBest);

    }
    private static int getIndexOfMax(Integer[] arr) {
    	int curMax = arr[0];
    	int curIndex = 0;
    	for(int i = 0; i < arr.length; i++) {
    		if(arr[i] > curMax) {
    			curMax = arr[i];
    			curIndex = i;
    		}
    	}
    	return curIndex;
    }
    private static int rollOutProtocol(PentagoBoardState boardState, int myID, int opID) {
    	PentagoBoardState myNewBoard = (PentagoBoardState) boardState.clone();

    	Random rand = new Random();
    	while(!myNewBoard.gameOver()) {
        	ArrayList<PentagoMove> movesList = myNewBoard.getAllLegalMoves();

        	int n = rand.nextInt(movesList.size());
        	if(myNewBoard.isLegal(movesList.get(n))) {
        		myNewBoard.processMove(movesList.get(n));
        	}
    	}
    	if(myNewBoard.getWinner() == myID)
    		return 1;
    	else if(myNewBoard.getWinner() == opID)
    		return -1;
    	else {
    		return 0;
    	}
    }
    /*
    private static ArrayList<PentagoMove> winOrBlockWin(PentagoBoardState boardState, int id) {
    	int myID = id;
    	int opID;
    	if(myID == 0) {
    		opID = 1;
    	}
    	else {
    		opID = 0;
    	}
    	
    	ArrayList<PentagoMove> moves = boardState.getAllLegalMoves();
    	ArrayList<PentagoMove> movePool = new ArrayList<>();
    	
    	//my available moves
//    	System.out.println("\nMY MOVES\n");
//    	printMoves(moves);
    	
        for(PentagoMove m : moves) {
        	int i = 0;

        	PentagoBoardState myNewBoard = (PentagoBoardState) boardState.clone();

            myNewBoard.processMove((PentagoMove) m);
            if(myNewBoard.getWinner() == myID) {//case i can win next move
            	movePool.add(m);
            	return movePool;
            }

            if(myNewBoard.getWinner() != myID) {//case i dont win next move
            	//can opponent win next move??
            	ArrayList<PentagoMove> movesOpponent = myNewBoard.getAllLegalMoves();
            	
            	//his available moves

                for(PentagoMove mOp : movesOpponent) {
                	PentagoBoardState myNewBoard2 = (PentagoBoardState) myNewBoard.clone();//used to be boardState.clone()
                	
                	if(myNewBoard2.isLegal(mOp)) {
	                    myNewBoard2.processMove((PentagoMove) mOp);
	                    if(myNewBoard2.getWinner() == opID) {//case opponent wins next move
	                    	//if opponent can win, remove
	                    	moves.remove(i);
	                		break;
	                    }
                	}
                }
            }
            i++;
        }

    	return moves;
    }*/
    
    private static void printMoves(ArrayList<PentagoMove> moves) {
        for(PentagoMove m : moves) {
        	System.out.println(m.toPrettyString());
        }

    }
    private static void printValArray(Integer[] arr) {
        for(int i = 0; i < arr.length; i++) {
        	System.out.println("Move " + i + ": "+ arr[i]);
        }

    }
    /*
	private static void makeBoardFromWorthArray(double[][] worthArray, PentagoBoardState boardState){
		for(int x = 0; x < BOARD_SIZE; x++) {
			for(int y = 0; y < BOARD_SIZE; y++) {
				if(worthArray[y][x] == 0) {
					myBoard[y][x] = boardState.getPieceAt(y, x);
				}
			}
		}
	}
    private static void updateBoard(PentagoBoardState pbs) {
    	//fuck these private methods
        myBoard = new Piece[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.arraycopy(pbs.board[i], 0, myBoard[i], 0, BOARD_SIZE);
        }
        myQuadrants = new Piece[NUM_QUADS][QUAD_SIZE][QUAD_SIZE];
        for (int i = 0; i < NUM_QUADS; i++) {
            for (int j = 0; j < QUAD_SIZE; j++) {
                System.arraycopy(pbs.quadrants[i][j], 0, myQuadrants[i][j], 0, QUAD_SIZE);
            }
        }
    }

	private static void swap(Quadrant Q1, Quadrant Q2) {
	    //Swapping mechanism
	    int a = quadToInt.get(Q1);
	    int b = quadToInt.get(Q2);
	    Piece[][] tmp = myQuadrants[a];
	    myQuadrants[a] = myQuadrants[b];
	    myQuadrants[b] = tmp;
	
	    buildBoardFromQuadrants();
	}


	private static void buildBoardFromQuadrants() {
	    for (int i = 0; i < BOARD_SIZE; i++) {
	        int quadrantRow = i < 3 ? i : i - 3;
	        int leftQuad = i < 3 ? 0 : 2;
	        int rightQuad = i < 3 ? 1 : 3;
	        System.arraycopy(myQuadrants[leftQuad][quadrantRow], 0, myBoard[i], 0, 3);
	        System.arraycopy(myQuadrants[rightQuad][quadrantRow], 0, myBoard[i], 3, 3);
	    }
	}
	*/
}