package student_player;

import java.util.ArrayList;
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



    public static PentagoMove goFirst(PentagoBoardState boardState, int id) {
    	double[][] moveWorth = new double[6][6];//weight array
    	Piece myPiece;
    	if(id == 0) {
    		myPiece = Piece.WHITE;
    	}
    	else {
    		myPiece = Piece.BLACK;
    	}

    	ArrayList<PentagoMove> moves = boardState.getAllLegalMoves();
        if(boardState.getTurnNumber() == 0) {
        	return hardMoves(boardState, id);
        }
        else if(boardState.getTurnNumber() == 1) {
        	return hardMoves(boardState, id);
        }

        else {        	
        	
            makeWorthArray(boardState, moveWorth, myPiece);//array of weights
            
    		printWorthArray(moveWorth);
    		
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
            
//        	Random rand = new Random();
//            int n = rand.nextInt(moves.size());//get random move from remaining moves
//        	System.out.println("\nCHOSEN MOVE: " + chosenMove.toPrettyString());
//        	System.out.println("BEST VAL: " + bestPlay);


            return chosenMove;
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
    
    private static double checkIfAnother(PentagoBoardState currentState,PentagoCoord curLoc, PentagoCoord dirCoor, Piece myPiece) {
    	//TODO doesn't keep chaining
    	int dirX = dirCoor.getX() - curLoc.getX();
    	int dirY = dirCoor.getY() - curLoc.getY();
    	boolean nextOK = true;
    	PentagoCoord newCoord = null;
    	try {
		 	newCoord = new PentagoCoord(dirCoor.getX() + dirX, dirCoor.getY() + dirY);
    	}
		catch(IllegalArgumentException e){
			nextOK = false;
		}
		if(nextOK){
			Piece newPiece = currentState.getPieceAt(dirCoor);
			if(newPiece.compareTo(Piece.EMPTY) == 0) {
				return 0;
			}
			if(newPiece.compareTo(myPiece) == 0) {
				System.out.println("\nDOES IT CHAIN TO ANOTHER SAME COLOUR\n");
				return 1 + checkIfAnother(currentState, dirCoor, newCoord, myPiece);

			}
			if(newPiece.compareTo(myPiece) != 0) {
				System.out.println("\nDOES IT CHAIN TO ANOTHER DIFF COLOUR\n");
				return 0.5 + checkIfAnother(currentState, dirCoor, newCoord, myPiece);

			}
		}
    	return 0;
    }
    
	private static void makeWorthArray(PentagoBoardState currentState,double[][] moveWorth, Piece myPiece) {
		
		for(int x = 0; x < 6; x++) {
			for(int y = 0; y < 6; y++) {
		    	PentagoCoord curCoord = new PentagoCoord(x,y);

				Piece aPiece = currentState.getPieceAt(curCoord);
				if(aPiece == Piece.EMPTY) {//empty tile, means we might place it there
					//need function to get value of this empty tile
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
						moveWorth[y][x] += checkIfAnother(currentState, curCoord, newCoord0, myPiece);

					}
					if(ok1) {
						moveWorth[y][x] += checkIfAnother(currentState, curCoord, newCoord1, myPiece);

					}
					if(ok2) {
						moveWorth[y][x] += checkIfAnother(currentState, curCoord, newCoord2, myPiece);

					}
					if(ok3) {
						moveWorth[y][x] += checkIfAnother(currentState, curCoord, newCoord3, myPiece);

					}
					if(ok4) {
						moveWorth[y][x] += checkIfAnother(currentState, curCoord, newCoord4, myPiece);

					}
					if(ok5) {
						moveWorth[y][x] += checkIfAnother(currentState, curCoord, newCoord5, myPiece);

					}
					if(ok6) {
						moveWorth[y][x] += checkIfAnother(currentState, curCoord, newCoord6, myPiece);

					}
					if(ok7) {
						moveWorth[y][x] += checkIfAnother(currentState, curCoord, newCoord7, myPiece);

					}
				}
			}
		}
	}
	private static void printWorthArray(double[][] moveWorth) {
		for(int y = 0; y < 6; y++) {
    		for(int x = 0; x < 6; x++) {
    			System.out.print("["+moveWorth[x][y]+"]\t");
    		}
			System.out.println("");
    	}
	}   
    
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
                	PentagoBoardState myNewBoard2 = (PentagoBoardState) boardState.clone();
                	
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
    }
    
    private static void printMoves(ArrayList<PentagoMove> moves) {
        for(PentagoMove m : moves) {
        	System.out.println(m.toPrettyString());
        }

    }
}