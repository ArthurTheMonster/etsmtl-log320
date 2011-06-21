package ca.etsmtl.breaktrough;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class NinjaClient {

	private Socket clientSocket;
	private BufferedInputStream input;
	private BufferedOutputStream output;
	
	private byte[] opponentMoveBuffer = new byte[16];
	private Move lastOpponentMove;
	
	private String bestMove;
	
	private boolean gameCompleted;
	
	private GameTable gameTable = new GameTable();
	
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
				lastOpponentMove = parseOpponentMove(new String(opponentMoveBuffer));
				System.out.println("Last opponent move is " + lastOpponentMove);
				GameTable.move(gameTable, lastOpponentMove);
				GameTable.printGameTable(gameTable);
			} else if (cmd == '2') {
				System.out.println("Last move is not valid");
				//TODO ?
			} else {
				System.out.println("cmd is " + cmd);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Move parseOpponentMove(String opponentMove) {
		String[] move = opponentMove.trim().split(" - ");
		return new Move(move[0], move[1]);
	}
	
	private void buildDecisionTree() {
		
	}
	
	private String[] startingMoves = new String[] {"A2 - A3", "A3 - A4", "A4 - A5"};
	private int startingMovesIndex = 0;
	
	private void sendMove() {
		bestMove = startingMoves[startingMovesIndex++];
		try {
			GameTable.move(gameTable, parseOpponentMove(bestMove));
			output.write(bestMove.getBytes(), 0, bestMove.length());
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new NinjaClient();
	}
}
