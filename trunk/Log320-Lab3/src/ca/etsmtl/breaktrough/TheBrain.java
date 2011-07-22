package ca.etsmtl.breaktrough;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import ca.etsmtl.breaktrough.NinjaClient.Player;

public class TheBrain extends Thread {

	private GameTable gameTable;
	private int deepnessTree;
	private int howSmartAmI;
	private Move myBestMove;
	private Move mySuggestedMove;
	
	private Move tempChoosedMove;
	
	private static Hashtable<Long, Move> whatIShouldPlay = new Hashtable<Long, Move>();
	private static Hashtable<Long, Integer> whatIShouldPlayDeepness = new Hashtable<Long, Integer>();
	
	private Player maxPlayer;
	private Player minPlayer;
	private int iMaxPlayer;
	private int iMinPlayer;
	
	private boolean running = false;
	
	public boolean isBrainStopped = true;
	
	private int brainId;
	
	public TheBrain(Player maxPlayer, Player minPlayer, int brainId) {
		gameTable = null;
		deepnessTree = 0;
		howSmartAmI = 0;
		myBestMove = null;
		tempChoosedMove = null;
		
		this.maxPlayer = maxPlayer;
		this.minPlayer = minPlayer;
		if (maxPlayer == Player.WHITE) {
			iMaxPlayer = 1;
			iMinPlayer = 2;
		} else {
			iMaxPlayer = 2;
			iMinPlayer = 1;	
		}
		
		this.brainId = brainId;
	}
	
	public Move getBestMove() {
		return myBestMove;
	}
	
	public Move mySuggestedMove() {
		return mySuggestedMove;
	}
	
	public int howSmartAmI() {
		return howSmartAmI;
	}
	
	public void run() {
		isBrainStopped = false;
		System.out.println("Brain " + brainId + " is running");
		while (running && deepnessTree < 50) {
			NinjaMax(gameTable, deepnessTree,Integer.MIN_VALUE,Integer.MAX_VALUE);
			deepnessTree = deepnessTree + 1;
		}
		isBrainStopped = true;
		System.out.println("Brain " + brainId + " is stopped");
	}
	
	public void stopBrain() {
		running = false;
	}
	
	public void prepareTheBrain(GameTable gameTable, int deepnessTree) {
		running = true;
		this.gameTable = new GameTable(gameTable);
		this.deepnessTree = deepnessTree;
		howSmartAmI = 0;
		myBestMove = null;
	}
	
	private int NinjaMax(GameTable table, int howManyMovesLeft, int alpha, int beta) {

		int currentAlpha = Integer.MIN_VALUE;
		
		if (!running) {
			return currentAlpha;
		}
		
		if (isLastMove(howManyMovesLeft)) {
			return evalTable(table, maxPlayer);
		}
		
		List<Move> listMove = new ArrayList<Move>();

		int deepnessSuggested = 0; 
		// Try the move we suggested for this attack
		if (isMyFirstMove(howManyMovesLeft)) {
			// We are ready man!
			Move move = null;
			
			long key = table.getTable();
			if (whatIShouldPlay.containsKey(key)) {
				move = whatIShouldPlay.get(key);
				//pointSuggested = whatIShouldPlayPoint.get(key);
				deepnessSuggested = whatIShouldPlayDeepness.get(key);
				
				if (table.isValidMove(move)) {
					mySuggestedMove = move;
					listMove.add(move);	
			
					if (deepnessSuggested < deepnessTree) {
						System.out.println("I have been suggest to do this move: " + move.toString());
					}
				}
				else {
					System.out.println("I have been suggested an invalid move (" + move.toString() + "). Damn you!");
				}
			}
		}
		
		// If the answer is more precise than our current analyze
		if (deepnessSuggested >= deepnessTree && listMove.size() > 0) {
			System.out.println("The given problem is level " + deepnessTree + ". TheBrain already had a solution for level " + deepnessSuggested + ".");
			myBestMove = listMove.get(0);
			howSmartAmI = deepnessSuggested;
		// We have to check our answer
		} else {
			listMove.addAll(GameTable.getAllMove(table, maxPlayer));
			
			for (Move move : listMove) {
				GameTable newGameTable = new GameTable(table);	
				newGameTable.move(move);
				
				// We just won, that's awesome
				if(newGameTable.isGameOver == iMaxPlayer) {
					if (isMySecondMove(howManyMovesLeft)) {
						tempChoosedMove = move;
					}
					return 10000*howManyMovesLeft;
				}
				
				int score = NinjaMin(newGameTable, howManyMovesLeft-1, Math.max(alpha,currentAlpha),beta);
				
				// Add some points if we eat a pawn
				if (table.getTableCount(minPlayer) > newGameTable.getTableCount(minPlayer)) {
					score += 30;
				}
				
				// This is the best score so far						
				if (score > currentAlpha) {
					currentAlpha = score;
					
					if (isMyFirstMove(howManyMovesLeft)) {
						myBestMove = move;
						howSmartAmI = deepnessTree;
					}
					
					// This is our best move so far for this table
					if (isMySecondMove(howManyMovesLeft)) {
						tempChoosedMove = move;
					}
					
					// In fact, I can play so good that I don't expect the opponent to 
					// be enough stupid do the last move, let just leave.
					if (currentAlpha >= beta) {					
						return currentAlpha;
					}
				}
			}
		}
		
		// Let save the move we should play
		if (isMyFirstMove(howManyMovesLeft)) {		
			if (deepnessSuggested < deepnessTree) {
				System.out.println("Hey. TheBrain " + brainId + " solved the problem for level " + deepnessTree);
				
				// Let remember that move, we will try it first next level
				long key = gameTable.getTable();
				whatIShouldPlay.put(key, new Move(myBestMove));
				whatIShouldPlayDeepness.put(key, howManyMovesLeft);
			}
		}
		
		return currentAlpha;
	}
	
	private int NinjaMin(GameTable table, int howManyMovesLeft, int alpha, int beta) {	
		int currentBeta = Integer.MAX_VALUE;
		
		if (!running) {
			return currentBeta;
		}
		
		if (isLastMove(howManyMovesLeft)) {
			return evalTable(table, maxPlayer);
		}
			
		List<Move> listMove = GameTable.getAllMove(table, minPlayer);

		for (Move move : listMove) {
			GameTable newGameTable = new GameTable(table);
			newGameTable.move(move);
			
			// We just lost, that really sucks (we should never drop in that if!!!!)
			if(newGameTable.isGameOver == iMinPlayer) {
				// This way we want will choose a move that make us win right away
				return -10000*howManyMovesLeft;
			}
			
			int score = NinjaMax(newGameTable, howManyMovesLeft-1,alpha,Math.min(beta,currentBeta));
			
			// Remove some points if we lost a pawn
			if (table.getTableCount(maxPlayer) > newGameTable.getTableCount(maxPlayer)) {
				score -= 30;
			}
			
			// Let save the best move if we face that table
			if (isOppFirstMove(howManyMovesLeft) && running){
				long key = newGameTable.getTable();
				whatIShouldPlay.put(key, new Move(tempChoosedMove));
				whatIShouldPlayDeepness.put(key, deepnessTree-2);
			}
			
			// This is the best score so far
			if (score < currentBeta) {
				currentBeta = score;
				
				// In fact, he can play so good that I will never do that
				// last move, let just leave.
				if (currentBeta <= alpha) {
					return currentBeta;
				}
			}
		}
		
		return currentBeta;
	}

	/*private boolean isSuicidal(GameTable table, Move move) {
		if (table.isInDanger(move.finalPos, maxPlayer)) {
			if ((table.getTable(minPlayer) & move.finalPos) == 0) {
				return true;
			}	
		}
		return false;
	}*/
	
	// TODO: To be removed
	// We should never get stuck in this function.
	/*private boolean isValid(GameTable newGameTable, Move move) {
		if (!newGameTable.isValidMove(move)) {
			//while (true) {
				System.out.println("INVALID MOVE");
				GameTable.printGameTable(newGameTable);
				System.out.println(move.toString());
				return false;
			//}
		}
		return true;
	}*/
	
	private boolean isMyFirstMove(int howManyMovesLeft) {
		return howManyMovesLeft == deepnessTree;
	}

	private boolean isMySecondMove(int howManyMovesLeft) {
		return howManyMovesLeft == deepnessTree-2;
	}
	
	private boolean isOppFirstMove(int howManyMovesLeft) {
		return howManyMovesLeft == deepnessTree-1;
	}
	
	private boolean isLastMove(int howManyMovesLeft) {
		return howManyMovesLeft == 0;
	}
	
	private int evalTable(GameTable table, Player player) {
		return table.getTableScore(player);
	}

}