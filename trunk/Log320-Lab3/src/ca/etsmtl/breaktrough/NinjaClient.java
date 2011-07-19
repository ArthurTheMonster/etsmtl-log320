package ca.etsmtl.breaktrough;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class NinjaClient {

	// TODO: INT
	public enum Player {
		BLACK,
		WHITE;
	}
	
	private Socket clientSocket;
	private BufferedInputStream input;
	private BufferedOutputStream output;
	
	private String[] firstMovesWhite = { "E2 - E3" };
	private String[] firstMovesBlack = { "E7 - E6" };
	
	private final long TIME_TO_THINK = 5200;
	
	private int nbMovePlayed = 0;
	
	private byte[] opponentMoveBuffer = new byte[16];
	private Move lastOpponentMove;
	
	private boolean gameCompleted;
	
	private GameTable gameTable = new GameTable();
	
	private final int defaultDeepnessTree = 5;
	
	private Player maxPlayer = Player.BLACK;
	private Player minPlayer = Player.WHITE;
	
	private static int brainCount = 0;
	
	TheBrain brain = new TheBrain(maxPlayer, minPlayer, brainCount++);
	
	public NinjaClient() {
		initCommunication();
		playBreaktrough();
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
			brain.stopBrain();
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
					gameTable.move(lastOpponentMove);
					System.out.println("Opponent move: " + lastOpponentMove.toString());
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
	
		
	private Move parseMove(String opponentMove, Player player) {
		String[] move = opponentMove.trim().split(" - ");
		return new Move(move[0], move[1], player);
	}
	
	private void sendMove() {
		while (!brain.isBrainStopped) {
			try {
				brain.join(2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//System.out.println("I'm waiting........");
		}
		brain = new TheBrain(maxPlayer, minPlayer, brainCount++);
			
		brain.prepareTheBrain(gameTable, defaultDeepnessTree);
		brain.start();
		
		try {
			brain.join(TIME_TO_THINK);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Move myBestMove = brain.getBestMove();
		if (myBestMove == null) { myBestMove = brain.mySuggestedMove(); }
		
		int deepnessThinking = brain.howSmartAmI();
		
		if (nbMovePlayed < firstMovesWhite.length) {
			System.out.println("Oh. We had a preset for this move: use it.");
			if (maxPlayer.equals(Player.WHITE)) {
				myBestMove = parseMove(firstMovesWhite[nbMovePlayed], maxPlayer);
			} else { 
				myBestMove = parseMove(firstMovesBlack[nbMovePlayed], maxPlayer);
			}
			deepnessThinking = 999;
		}
		
		try {
			output.write(myBestMove.toString().getBytes(), 0, 7);	
			output.flush();
			
			gameTable.move(myBestMove);
			nbMovePlayed++;
			System.out.println("CHOOSED MOVE: " + myBestMove.toString() + " - Deepness: " + deepnessThinking);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new NinjaClient();
	}
}
