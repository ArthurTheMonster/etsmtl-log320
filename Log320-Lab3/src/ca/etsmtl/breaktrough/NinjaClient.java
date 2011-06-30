package ca.etsmtl.breaktrough;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class NinjaClient {

	// TODO: INT
	public enum Player {
		BLACK,
		WHITE;
	}
	
	private Socket clientSocket;
	private BufferedInputStream input;
	private BufferedOutputStream output;
	
	private String[] firstMoves = { "E2 - E3", "E3 - E4" };
	
	private int nbMovePlayed = 0;
	
	private byte[] opponentMoveBuffer = new byte[16];
	private Move lastOpponentMove;
	
	private boolean gameCompleted;
	
	private GameTable gameTable = new GameTable();
	
	private int deepnessTree = 7;
	
	private Player maxPlayer = Player.BLACK;
	private Player minPlayer = Player.WHITE;
	
	private Move myBestMove;
	
	private long timeGameStarted = 0;
	
	private Hashtable<Long, Move> whatIShouldPlay = new Hashtable<Long, Move>();
	private Hashtable<Long, Integer> whatIShouldPlayPoint = new Hashtable<Long, Integer>();

	private Move choosedMove;
	private Move tempChoosedMove;
	private int tempChoosedMovePoint;
	
	public NinjaClient() {
		initTables();
		initCommunication();
		playBreaktrough();
	}
	
	private void initTables() {
		//TODO: load tables
	}
	
	private void initCommunication() {
		try {
			clientSocket = new Socket("localhost", 8888);
			input = new BufferedInputStream(clientSocket.getInputStream());
			output = new BufferedOutputStream(clientSocket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void playBreaktrough() {
		timeGameStarted = System.currentTimeMillis();
		while (!gameCompleted) {
			readOpponentMove();
			buildDecisionTree();
			sendMove();
		}
	}
	
	private void readOpponentMove() {
		try {
			char cmd = (char) input.read();
			if (cmd == '1') {
				int size = input.available();
				input.read(opponentMoveBuffer, 0, size);
				if (new String(opponentMoveBuffer).trim().equals("A8 - A8")) {
					maxPlayer = Player.WHITE;
					minPlayer = Player.BLACK;
				} else {
					lastOpponentMove = parseMove(new String(opponentMoveBuffer), minPlayer);
					GameTable.move(gameTable, lastOpponentMove);
				}

			} else if (cmd == '2') {
				System.out.println("##### Last move is not valid #####");
			} else {
				System.out.println("##### Unexpected command: " + cmd + " #####");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int NinjaMax(GameTable table, int howManyMovesLeft, int alpha, int beta) {
		Move tempBestMove = null;
		
		if (isMyLastMove(howManyMovesLeft)) {
			return evalTable(table, maxPlayer);
		}
		
		List<Move> listMove = new ArrayList<Move>();

		// Try the move we suggested for this attack
		if (isMyFirstMove(howManyMovesLeft)) {
			// We are ready man!
			if (whatIShouldPlay.containsKey(table.getTable())) {
				Move move = whatIShouldPlay.get(table.getTable());
				if (table.isValidMove(move)) {
					listMove.add(move);	
					System.out.println("I have been suggest to do this move: " + move.toString());
				}
			}
		}
		
		listMove.addAll(GameTable.getAllMove(table, maxPlayer));
		
		int currentAlpha = Integer.MIN_VALUE;
		for (Move move : listMove) {
			GameTable newGameTable = new GameTable(table);
			
			GameTable.move(newGameTable, move);
			int score = NinjaMin(newGameTable, howManyMovesLeft-1,Math.max(alpha,currentAlpha),beta);

			if (isMyFirstMove(howManyMovesLeft)) {
				System.out.println("New score: " + score);
			}
			
			/*if (table.getBlackPawnCount() > newGameTable.getBlackPawnCount()) {
				score += 500;
			}*/
			
			// This is the best score so far						
			if (score > currentAlpha) {
				currentAlpha = score;
				tempBestMove = move;
				
				// Let remember that move for the next round
				if (isMySecondMove(howManyMovesLeft)) {
					tempChoosedMove = new Move(tempBestMove);
					tempChoosedMovePoint = currentAlpha;
				}
				
				// In fact, I can play so good that I don't expect the opponent to 
				// be enough stupid do the last move, let just leave.
				if (currentAlpha >= beta) {					
					return currentAlpha;
				}
			}
		}
		
		// Let save the move we should play
		if (isMyFirstMove(howManyMovesLeft)) {
			myBestMove = tempBestMove;
			System.out.println("CHOOSED MOVE: " + myBestMove.toString() + " - Points: " + currentAlpha);
			// Move to suggest
		}
		
		return currentAlpha;
	}
	
	private int NinjaMin(GameTable table, int howManyMovesLeft, int alpha, int beta) {	
		if (isMyLastMove(howManyMovesLeft)) {
			return evalTable(table, maxPlayer);
		}
			
		List<Move> listMove = GameTable.getAllMove(table, minPlayer);
		
		int currentBeta = Integer.MAX_VALUE;
		for (Move move : listMove) {
			GameTable newGameTable = new GameTable(table);
	
			GameTable.move(newGameTable, move);
			int score = NinjaMax(newGameTable, howManyMovesLeft-1,alpha,Math.min(beta,currentBeta));
			
			/*if (table.getWhitePawnCount() > newGameTable.getWhitePawnCount()) {
				score -= 500;
			}*/
			
			if (isOppFirstMove(howManyMovesLeft)){
				long ltable = newGameTable.getTable();
				if (whatIShouldPlay.containsKey(ltable)) {
					if (tempChoosedMovePoint > whatIShouldPlayPoint.get(ltable)) {
						whatIShouldPlay.remove(ltable);
						whatIShouldPlay.put(ltable, tempChoosedMove);
						
						whatIShouldPlayPoint.remove(ltable);
						whatIShouldPlayPoint.put(ltable, tempChoosedMovePoint);
					}
				} else {
					whatIShouldPlay.put(ltable, tempChoosedMove);					
					whatIShouldPlayPoint.put(ltable, tempChoosedMovePoint);			
				}
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

	// TODO: To be removed
	// We should never get stuck in this function.
	private void isValid(GameTable newGameTable, Move move) {
		if (!newGameTable.isValidMove(move)) {
			while (true) {
				System.out.println("INVALID MOVE");
				GameTable.printGameTable(newGameTable);
				System.out.println(move.toString());
			}
		}
	}
	
	private boolean isMyFirstMove(int howManyMovesLeft) {
		return howManyMovesLeft == deepnessTree;
	}

	private boolean isMySecondMove(int howManyMovesLeft) {
		return howManyMovesLeft == deepnessTree-2;
	}
	private boolean isOppFirstMove(int howManyMovesLeft) {
		return howManyMovesLeft == deepnessTree-1;
	}
	
	private boolean isMyLastMove(int howManyMovesLeft) {
		return howManyMovesLeft == 0;
	}
	
	private int evalTable(GameTable table, Player player) {
		return table.getTableScore(player);
	}
	
	private Move parseMove(String opponentMove, Player player) {
		String[] move = opponentMove.trim().split(" - ");
		return new Move(move[0], move[1], player);
	}
	
	private void buildDecisionTree() {
	}
	
	private void sendMove() {
		long time = System.currentTimeMillis();
		if (nbMovePlayed < firstMoves.length) {
			myBestMove = parseMove(firstMoves[nbMovePlayed], maxPlayer);
		} else {
			NinjaMax(gameTable, deepnessTree,Integer.MIN_VALUE,Integer.MAX_VALUE);
		}
		System.out.println("Oh. Btw. Our awsome transposition table has now: " + GameTable.transposition.size() + " entries.");
		nbMovePlayed++;
		System.out.println("Last move time : " + ((int)System.currentTimeMillis() - (int)time) + " Game time elapsed: " + ((int)System.currentTimeMillis() - (int)timeGameStarted));
		try {
			GameTable.move(gameTable, myBestMove);
			output.write(myBestMove.toString().getBytes(), 0, 7);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new NinjaClient();
	}
}
