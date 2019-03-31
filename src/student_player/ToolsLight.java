package student_player;

import java.util.ArrayList;
import java.util.Random;

import pentago_swap.PentagoBoardState;
import pentago_swap.PentagoMove;
import pentago_swap.PentagoBoardState.Piece;
import pentago_swap.PentagoBoardState.Quadrant;

public class ToolsLight {
    private static int NUM_ROLLS = 60;

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

      return MonteCarloLite(boardState, id, myPiece);

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
    	ArrayList<PentagoMove> allMoves = boardState.getAllLegalMoves();
//    	ArrayList<PentagoMove> movesLeft = getTrimmedMoveList(boardState, myPiece, allMoves);


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
            /*
            if(movesLeft.contains(m)) {
                for(int numRolls = 0; numRolls < 2 * NUM_ROLLS; numRolls++) {//do rollouts
                	bestBranch[i] += rollOutProtocol(myNewBoard, myID, opID);
                }
            	bestBranch[i] = Math.floor((NOT_TRIMMED_WEIGHT * (bestBranch[i]/(2 * NUM_ROLLS))) * 100) / 100;

            }*/
            for(int numRolls = 0; numRolls < NUM_ROLLS; numRolls++) {//do rollouts
            	bestBranch[i] += rollOutProtocol(myNewBoard, myID, opID);

            }
        	bestBranch[i] = Math.floor(bestBranch[i]/NUM_ROLLS * 100) / 100;

            
        	i++;
        }
        

        int index = getIndexOfMax(bestBranch);
		return allMoves.get(index);
    }
//	private static ArrayList<PentagoMove> getTrimmedMoveList(PentagoBoardState boardState, Piece myPiece,
//			ArrayList<PentagoMove> moves) {
//		ArrayList<PentagoMove> movesLeft = new ArrayList<>();
//		double[][] moveWorth = makeWorthArray(boardState, myPiece);//array of weights
//		double medianMoveValue = getMaxMoveVal(moveWorth)/MEDIAN_DIVISOR;
//		for(int i = 0; i < moves.size(); i++) {
//			PentagoMove m = moves.get(i);
//			int x = m.getMoveCoord().getX();
//			int y = m.getMoveCoord().getY();
//
//			if(moveWorth[y][x] > medianMoveValue) {
//				movesLeft.add(moves.get(i));
//
//			}
//		}
//		
//		return movesLeft;
//	}

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

}
