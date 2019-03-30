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

    private static final int BOARD_SIZE = 6;
    private static int NUM_ROLLS = 70;
    private static final double WEIGHT_SAME = 1.2;
    private static final double WEIGHT_DIFF = 1.5;
    private static final double MEDIAN_DIVISOR = 2;
    private static final double NOT_TRIMMED_WEIGHT = 1.1;
    


    public static PentagoMove goFirst(PentagoBoardState boardState, int id) {
    	Piece myPiece;
    	if(id == 0) {
    		myPiece = Piece.WHITE;
    	}
    	else {
    		myPiece = Piece.BLACK;
    	}
    	
        if(boardState.getTurnNumber() == 0) {
        	return hardMoves(boardState, id);
        }
        else if(boardState.getTurnNumber() == 1) {
        	return hardMoves(boardState, id);
        }
        else{
            return MonteCarloLite(boardState, id, myPiece);
        }
//        else if(boardState.getTurnNumber() < 8) {
//            return MonteCarloLite(boardState, id, myPiece);
//        }
//        else {        
//        	NUM_ROLLS = 10;
//            return MonteCarlo(boardState, id, myPiece);
//        }
 
    }
    public static PentagoMove goSecond(PentagoBoardState boardState, int id) {
    	Piece myPiece;
    	if(id == 0) {
    		myPiece = Piece.WHITE;
    	}
    	else {
    		myPiece = Piece.BLACK;
    	}
    	
        if(boardState.getTurnNumber() == 0) {
        	return hardMoves(boardState, id);
        }
        else{
            return MonteCarloLite(boardState, id, myPiece);
        }
//        else if(boardState.getTurnNumber() < 8) {
//            return MonteCarloLite(boardState, id, myPiece);
//        }
//        else {        
//        	NUM_ROLLS = 10;
//            return MonteCarlo(boardState, id, myPiece);
//        }

    }

	private static ArrayList<PentagoMove> getTrimmedMoveList(PentagoBoardState boardState, Piece myPiece,
			ArrayList<PentagoMove> moves) {
		ArrayList<PentagoMove> movesLeft = new ArrayList<>();
		double[][] moveWorth = makeWorthArray(boardState, myPiece);//array of weights
		double medianMoveValue = getMaxMoveVal(moveWorth)/MEDIAN_DIVISOR;
//		printWorthArray(moveWorth);
//		System.out.println("medianVal: " + medianMoveValue);
//    		System.out.println("numMoves before trim: " + movesLeft.size());

		//add good moves
		for(int i = 0; i < moves.size(); i++) {
			PentagoMove m = moves.get(i);
			int x = m.getMoveCoord().getX();
			int y = m.getMoveCoord().getY();
//			System.out.println("Evaluating move: " + moves.get(i).toPrettyString());

			if(moveWorth[y][x] > medianMoveValue) {
				movesLeft.add(moves.get(i));
//				System.out.println("added move: " + movesLeft.get(i).toPrettyString());

			}
		}
		
//		printMoves(movesLeft);
		return movesLeft;
	}
    private static double getMaxMoveVal(double[][] arr) {
    	double max = 0;
    	for(int x = 0; x < arr.length; x++) {
    		for(int y = 0; y < arr[x].length; y++) {
    			if(arr[y][x] > max)
    				max = arr[y][x];
    		}
    	}
    	return max;
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

    private static double getTileWorth(PentagoBoardState currentState, PentagoCoord nextLoc, int deltaX, int deltaY, Piece myPiece) {
   
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
			if(nextOK) {
				return 0 + getTileWorth(currentState, newCoord, deltaX, deltaY, myPiece) ;

			}
			else {
				return 0;
			}
		}
		else if(newPiece.compareTo(myPiece) == 0) {
			if(nextOK){
				return 1 + WEIGHT_SAME * getTileWorth(currentState, newCoord, deltaX, deltaY, myPiece);
			}
			else {
				return 1;
			}
		}
		else {
			if(nextOK){
				return 0.5 + WEIGHT_DIFF * getTileWorth(currentState, newCoord, deltaX, deltaY, myPiece);
			}
			else {
				return 0.5;
			}

		}

    }
    
	private static double[][] makeWorthArray(PentagoBoardState currentState, Piece myPiece) {
		double[][] moveWorth = new double[BOARD_SIZE][BOARD_SIZE];
		for(int x = 0; x < BOARD_SIZE; x++) {
			for(int y = 0; y < BOARD_SIZE; y++) {
				moveWorth[x][y] = 0;
			}
		}

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
						moveWorth[y][x] += getTileWorth(currentState, newCoord0, -1, -1, myPiece);

					}
					if(ok1) {
						moveWorth[y][x] += getTileWorth(currentState, newCoord1, 0, -1, myPiece);

					}
					if(ok2) {
						moveWorth[y][x] += getTileWorth(currentState, newCoord2, 1, -1, myPiece);

					}
					if(ok3) {
						moveWorth[y][x] += getTileWorth(currentState, newCoord3, 1, 0, myPiece);

					}
					if(ok4) {
						moveWorth[y][x] += getTileWorth(currentState, newCoord4, 1, 1, myPiece);

					}
					if(ok5) {
						moveWorth[y][x] += getTileWorth(currentState, newCoord5, 0, 1, myPiece);

					}
					if(ok6) {
						moveWorth[y][x] += getTileWorth(currentState, newCoord6, -1, 1, myPiece);

					}
					if(ok7) {
						moveWorth[y][x] += getTileWorth(currentState, newCoord7, -1, 0, myPiece);

					}
				}
			}
		}
		for(int x = 0; x < BOARD_SIZE; x++) {
			for(int y = 0; y < BOARD_SIZE; y++) {
				moveWorth[x][y] = Math.floor((moveWorth[x][y]) * 100) / 100;;
			}
		}
		return moveWorth;
	}
    private static PentagoMove MonteCarloLite(PentagoBoardState boardState, int id, Piece myPiece) {
    	int myID = id;
    	int opID;
    	if(myID == 0) {
    		opID = 1;
    	}
    	else {
    		opID = 0;
    	}
    	//gets trimmed list of moves
//    	System.out.println("numMoves before prune: " + boardState.getAllLegalMoves().size());
    	ArrayList<PentagoMove> allMoves = boardState.getAllLegalMoves();
    	ArrayList<PentagoMove> movesLeft = getTrimmedMoveList(boardState, myPiece, allMoves);
//    	System.out.println("numMoves after prune: " + movesLeft.size());

//    	ArrayList<PentagoMove> movesLeft = boardState.getAllLegalMoves();

    	double[] bestBranch = new double[allMoves.size()];
    	for(int j = 0; j < bestBranch.length; j++) {
    		bestBranch[j] = 0;
    	}
    	int i = 0;
        for(PentagoMove m : allMoves) {
        	PentagoBoardState myNewBoard = (PentagoBoardState) boardState.clone();
            myNewBoard.processMove((PentagoMove) m);           
            if(myNewBoard.getWinner() == myID) {//case i can win next move
            	bestBranch[i] +=1000;
            	continue;
            }
            if(movesLeft.contains(m)) {
                for(int numRolls = 0; numRolls < 2 * NUM_ROLLS; numRolls++) {//do rollouts
                	bestBranch[i] += rollOutProtocol(myNewBoard, myID, opID);
                }
            	bestBranch[i] = Math.floor((NOT_TRIMMED_WEIGHT * (bestBranch[i]/NUM_ROLLS)) * 100) / 100;

            }
            else {
                for(int numRolls = 0; numRolls < NUM_ROLLS; numRolls++) {//do rollouts
                	bestBranch[i] += rollOutProtocol(myNewBoard, myID, opID);

                }
            	bestBranch[i] = bestBranch[i]/NUM_ROLLS;

            }
        	i++;
        }
        

        int index = getIndexOfMax(bestBranch);
//        printValArray(bestBranch);
//        System.out.println("Index: "+ index + " Chosen move: " + (allMoves.get(index)).toPrettyString() );
		return allMoves.get(index);
    }
    private static PentagoMove MonteCarlo(PentagoBoardState boardState, int id, Piece myPiece) {
    	int myID = id;
    	int opID;
    	if(myID == 0) {
    		opID = 1;
    	}
    	else {
    		opID = 0;
    	}
    	//gets trimmed list of moves
    	ArrayList<PentagoMove> movesLeft = getTrimmedMoveList(boardState, myPiece, boardState.getAllLegalMoves());
//    	System.out.println("Level 1 trim: " + movesLeft.size());
    	double[] bestBranch = new double[movesLeft.size()];
    	for(int j = 0; j < bestBranch.length; j++) {
    		bestBranch[j] = 0;
    	}
    	int i = 0;
        for(PentagoMove m : movesLeft) {
        	PentagoBoardState myNewBoard = (PentagoBoardState) boardState.clone();
            myNewBoard.processMove((PentagoMove) m);           
            if(myNewBoard.getWinner() == myID) {//case i can win next move
            	bestBranch[i] +=1000;
            	continue;
            }
            
        	ArrayList<PentagoMove> movesLeft2 = getTrimmedMoveList(myNewBoard, myPiece, myNewBoard.getAllLegalMoves());
//        	System.out.println("Level 2 trim: " + movesLeft2.size());

           	double[] bestBranch2 = new double[movesLeft2.size()];
        	for(int j = 0; j < bestBranch2.length; j++) {
        		bestBranch2[j] = 0;
        	}
        	int k = 0;
            for(PentagoMove m2 : movesLeft2) {
            	PentagoBoardState myNewBoard2 = (PentagoBoardState) myNewBoard.clone();
//            	if(!myNewBoard2.isLegal(m2)) {
//            		continue;
//            	}
                myNewBoard2.processMove((PentagoMove) m2);           
                if(myNewBoard2.getWinner() == opID) {//case i can win next move
                	bestBranch2[k] -=1000;
                	continue;
                }
        	
	            for(int numRolls = 0; numRolls < NUM_ROLLS; numRolls++) {//do rollouts
	            	bestBranch2[k] += rollOutProtocol(myNewBoard, myID, opID);
	
	            }
	        	bestBranch2[k] = Math.floor((bestBranch2[k]/NUM_ROLLS) * 100) / 100;
	        	k++;
            }
//            System.out.print("bestbranch2: " + k);
//            printValArray(bestBranch2);

            double sum = 0;
            for(int l = 0; l < bestBranch2.length; l++) {
            	sum += bestBranch2[l];
            }
            bestBranch[i] += Math.floor(sum * 100) / 100;
            bestBranch[i] = Math.floor((bestBranch[i]/(bestBranch2.length)) * 1000) / 1000;
            i++;
        }

        int index = getIndexOfMax(bestBranch);
//        printValArray(bestBranch);
//        System.out.println("Index: "+ index + " Chosen move: " + (movesLeft.get(index)).toPrettyString() );
		return movesLeft.get(index);
    }
    private static int getIndexOfMax(double[] bestBranch) {
    	double curMax = bestBranch[0];
    	int curIndex = 0;
    	for(int i = 0; i < bestBranch.length; i++) {
    		if(bestBranch[i] > curMax) {
    			curMax = bestBranch[i];
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
	private static void printWorthArray(double[][] moveWorth) {
		for(int y = 0; y < BOARD_SIZE; y++) {
    		for(int x = 0; x < BOARD_SIZE; x++) {
    			System.out.print("["+moveWorth[x][y]+"]\t");
    		}
			System.out.println("");
    	}
	}   

    private static void printMoves(ArrayList<PentagoMove> moves) {
        for(PentagoMove m : moves) {
        	System.out.println(m.toPrettyString());
        }

    }
    private static void printValArray(double[] arr) {
        for(int i = 0; i < arr.length; i++) {
        	System.out.println("Move " + i + ": "+ arr[i]);
        }

    }
}