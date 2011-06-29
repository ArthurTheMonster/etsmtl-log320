package ca.etsmtl.breaktrough;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
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
	
	private int deepnessTree = 6;
	
	private Player maxPlayer = Player.BLACK;
	private Player minPlayer = Player.WHITE;

	
	private Move myBestMove;
	
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
	
	private int NinjaMax(GameTable table, int howManyMoveLeft, int alpha, int beta) {
		Move tempBestMove = null;

		if (howManyMoveLeft == 0) {
			return evalTable(table, maxPlayer);
		}

		howManyMoveLeft--;
		
		List<Move> listMove = GameTable.getAllMove(table, maxPlayer);
		
		int currentAlpha = Integer.MIN_VALUE;
		for (Move move : listMove) {
			GameTable newGameTable = new GameTable(table);
			GameTable.move(newGameTable, move);
			int score = NinjaMin(newGameTable, howManyMoveLeft,Math.max(alpha,currentAlpha),beta);

			/*if (table.getBlackPawnCount() > newGameTable.getBlackPawnCount()) {
				score += 500;
			}*/
			
			if (howManyMoveLeft == deepnessTree-1) {
				System.out.println("NEW MOVE: " + score);
			}
			
			
			if (score > currentAlpha) {
				currentAlpha = score;
				tempBestMove = move;

				if (currentAlpha > beta) {
					myBestMove = tempBestMove;
					return currentAlpha;
				}
			}
		}
		
		if (howManyMoveLeft == deepnessTree-1) {
			System.out.println("CHOOSED MOVE:" + currentAlpha);
		}
		myBestMove = tempBestMove;
		return currentAlpha;
	}
	
	private int NinjaMin(GameTable table, int howManyMoveLeft, int alpha, int beta) {	
		if (howManyMoveLeft == 0) {
			return evalTable(table, maxPlayer);
		}

		howManyMoveLeft--;
		
		List<Move> listMove = GameTable.getAllMove(table, minPlayer);
		
		int currentBeta = Integer.MAX_VALUE;
		for (Move move : listMove) {
			GameTable newGameTable = new GameTable(table);
			GameTable.move(newGameTable, move);
			int score = NinjaMax(newGameTable, howManyMoveLeft,alpha,Math.min(beta,currentBeta));
			
			/*if (table.getWhitePawnCount() > newGameTable.getWhitePawnCount()) {
				score -= 500;
			}*/
			
			if (score < currentBeta) {
				currentBeta = score;
				
				if (currentBeta < alpha) {
					return currentBeta;
				}
			}
		}
		return currentBeta;
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
		System.out.println(System.currentTimeMillis() - time);
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
