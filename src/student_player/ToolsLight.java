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

public class ToolsLight {

    private static int NUM_ROLLS;
    private static int myID;
    private static int opID;
    
    public ToolsLight(int id) {
    	myID = id;
    	if(myID == 0) {
    		opID = 1;
    	}
    	else {
    		opID = 0;
    	}

    }


    public PentagoMove goFirst(PentagoBoardState boardState) {
    	NUM_ROLLS = (int)(65 + Math.pow(((boardState.getTurnNumber()-4)/2),3));
//    	NUM_ROLLS = 15;
        if(boardState.getTurnNumber() == 0) {
        	return hardMoves(boardState);
        }
        else if(boardState.getTurnNumber() == 1) {
        	return hardMoves(boardState);
        }

//        else if(boardState.getTurnNumber() < 5) {
//        	NUM_ROLLS = (int)(45 + Math.pow(((boardState.getTurnNumber()-4)/2),3));
//
//            return MonteCarloLite(boardState);
//        }
//        else {     
//        	NUM_ROLLS = (int)(15 + Math.pow(((boardState.getTurnNumber()-4)/2),3));
//
//            return MonteCarlo(boardState);
//        }
      return MonteCarloLite(boardState);

    }

	private static PentagoMove hardMoves(PentagoBoardState boardState) {
		PentagoMove move11 = new PentagoMove(1, 1, Quadrant.BL, Quadrant.BR, myID);
		PentagoMove move14 = new PentagoMove(1, 4, Quadrant.BL, Quadrant.BR, myID);
		PentagoMove move41 = new PentagoMove(4, 1, Quadrant.TL, Quadrant.TR, myID);
		PentagoMove move44 = new PentagoMove(4, 4, Quadrant.TL, Quadrant.TR, myID);
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


    private static PentagoMove MonteCarloLite(PentagoBoardState boardState) {
    	//gets trimmed list of moves
//    	System.out.println("numMoves before prune: " + boardState.getAllLegalMoves().size());
    	ArrayList<PentagoMove> allMoves = boardState.getAllLegalMoves();
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
            	break;
            }

            for(int numRolls = 0; numRolls < NUM_ROLLS; numRolls++) {//do rollouts
            	bestBranch[i] += rollOutProtocol(myNewBoard);

            }
        	bestBranch[i] = Math.floor(bestBranch[i]/NUM_ROLLS * 100) / 100;
       
        	i++;
        }
        

        int index = getIndexOfMax(bestBranch);
//        printValArray(bestBranch);
//        System.out.println("Index: "+ index + " Chosen move: " + (allMoves.get(index)).toPrettyString() );
		return allMoves.get(index);
    }
    private static PentagoMove MonteCarlo(PentagoBoardState boardState) {
    	//gets trimmed list of moves
    	ArrayList<PentagoMove> allMoves = boardState.getAllLegalMoves();
//    	System.out.println("Level 1 trim: " + movesLeft.size());
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
            	break;
            }
        	ArrayList<PentagoMove> allMoves2 = myNewBoard.getAllLegalMoves();
//        	System.out.println("Level 2 trim: " + movesLeft2.size());

           	double[] bestBranch2 = new double[allMoves2.size()];
        	for(int j = 0; j < bestBranch2.length; j++) {
        		bestBranch2[j] = 0;
        	}
        	int k = 0;
        	Random rand = new Random();

            for(PentagoMove m2 : allMoves2) {
            	if(rand.nextInt(100)%8 == 0)
            		break;
            	PentagoBoardState myNewBoard2 = (PentagoBoardState) myNewBoard.clone();
            	if(!myNewBoard2.isLegal(m2)) {
            		continue;
            	}

                myNewBoard2.processMove((PentagoMove) m2);           
                if(myNewBoard2.getWinner() == opID) {//case i can win next move
                	bestBranch2[k] -=1000;
                	break;
                }
                
	            for(int numRolls = 0; numRolls < NUM_ROLLS; numRolls++) {//do rollouts
	            	bestBranch2[k] += rollOutProtocol(myNewBoard);
	
	            }
	        	bestBranch2[k] = Math.floor((bestBranch2[k]/NUM_ROLLS) * 100) / 100;
                
//                System.out.println("move val: " + bestBranch2[k]);
	        	k++;
            }
//            System.out.println("bestbranch2: " + i);
//            printValArray(bestBranch2);

//            double sum = 0;
//            for(int l = 0; l < bestBranch2.length; l++) {
//            	sum += bestBranch2[l];
//            }
            bestBranch[i] = getMin(bestBranch2);
//            bestBranch[i] += Math.floor(sum * 100) / 100;

//            bestBranch[i] = Math.floor((bestBranch[i]/(bestBranch2.length)) * 1000) / 1000;
            i++;
        }

        int index = getIndexOfMax(bestBranch);
//        printValArray(bestBranch);
//        System.out.println("Index: "+ index + " Chosen move: " + (allMoves.get(index)).toPrettyString() );
		return allMoves.get(index);
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
    private static double getMin(double[] bestBranch) {
    	double curMin = bestBranch[0];
    	for(int i = 0; i < bestBranch.length; i++) {
    		if(bestBranch[i] < curMin) {
    			curMin = bestBranch[i];
    		}
    	}
    	return curMin;
    }
    private static int rollOutProtocol(PentagoBoardState boardState) {
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
    private static void printValArray(double[] arr) {
        for(int i = 0; i < arr.length; i++) {
        	System.out.println("Move " + i + ": "+ arr[i]);
        }

    }

}