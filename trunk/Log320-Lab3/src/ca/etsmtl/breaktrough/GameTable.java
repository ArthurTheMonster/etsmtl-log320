package ca.etsmtl.breaktrough;

import java.util.ArrayList;
import java.util.List;

import ca.etsmtl.breaktrough.Move.Player;

public class GameTable {
	private static final long STARTING_BLACK_TABLE = 65535l;
	private static final long STARTING_WHITE_TABLE = -281474976710656l;
	
	public long blackTable;
	public long whiteTable;
	
	public GameTable() {
		blackTable = STARTING_BLACK_TABLE;
		whiteTable = STARTING_WHITE_TABLE;
	}
	
	public GameTable(GameTable gameTable) {
		blackTable = gameTable.blackTable;
		whiteTable = gameTable.whiteTable;
	}
	
	public static void move(GameTable gameTable, Move move) {
		long table = (move.player == Move.Player.WHITE ? gameTable.whiteTable : gameTable.blackTable);
		long tableDeplacement = table & move.initialPos;
		tableDeplacement = (move.player == Move.Player.WHITE ? tableDeplacement >>> move.move : tableDeplacement << move.move);
		table = table &~move.initialPos;
		table |= tableDeplacement;
		if (move.player == Move.Player.WHITE) {
			gameTable.whiteTable = table;
		} else {
			gameTable.blackTable = table;
		}
	}

	public static void main(String[] args) {
		GameTable gameTable = new GameTable();
		List<Move> moves = getAllMove(gameTable, Player.BLACK);
		System.out.println(moves.size());
		
		System.out.println(Math.log(2));
		
		for (Move move : moves) {
			System.out.println(move);
		}
	}
	
	public static List<Move> getAllMove(GameTable gameTable, Move.Player player) {
		List<Move> validMovePawns = new ArrayList<Move>();
		
		long myTable = 0;
		long oppTable = 0;
		if (player == Move.Player.WHITE) {
			myTable = gameTable.whiteTable;
			oppTable = gameTable.blackTable;
		}
		else {
			myTable = gameTable.blackTable;
			oppTable = gameTable.whiteTable;	
		}
			
		for (int i = 0; i < 63; i++) {
			long currentPawn = myTable & 1l << i;
			
			if (currentPawn != 0) {
				validMovePawns.addAll(GetValidMoves(player, currentPawn, myTable, oppTable));
			}
		}
		
		return validMovePawns;
		
	}
	
	private static List<Move> GetValidMoves(Move.Player player, long pawn, long myTable, long oppTable) {
		List<Move> validMovePawn = new ArrayList<Move>();
		long newPawn = 0;
		
		//Left
		newPawn = (player == Move.Player.WHITE ? pawn >> 9 : pawn << 7);
		if (isLeftDiagonalValid(newPawn, myTable)) {
			validMovePawn.add(new Move(pawn, player == Move.Player.WHITE ? 9 : 7, player));
		}

		//Straight
		newPawn = (player == Move.Player.WHITE ? pawn >> 8 : pawn << 8);
		if (isStraightMoveValid(newPawn, myTable, oppTable)) {
			validMovePawn.add(new Move(pawn, 8, player));
		}
		
		//Right
		newPawn = (player == Move.Player.WHITE ? pawn >> 7 : pawn << 9);
		if (isRightDiagonalValid(newPawn, myTable)) {
			validMovePawn.add(new Move(pawn, player == Move.Player.WHITE ? 7 : 9, player));
		}
		
		return validMovePawn;
	}

	private static boolean isLeftDiagonalValid(long newPawn, long myTable) {
		// If I can move to left (i.e. not first column)
		if (Math.log(newPawn)/Math.log(2)%8 < 7) {

			// If I don't have one of my own pawn at this slot
			if ((myTable & newPawn) == 0) {
				return true;
			}
		}	
		return false;
	}

	private static boolean isStraightMoveValid(long newPawn, long myTable, long oppTable) {
		if ((myTable & newPawn) == 0 && (oppTable & newPawn) == 0) {
			return true;
		}
		return false;
	}
	
	private static boolean isRightDiagonalValid(long newPawn, long myTable) {
		// If I can move to right (i.e. not the last column)
		if (Math.log(newPawn)/Math.log(2)%8 > 0) {
			// If I don't have one of my own pawn at this slot

			if ((myTable & newPawn) == 0) {
				return true;
			}
		}	
		return false;
	}

	public static void printGameTable(GameTable gameTable) {
		printTable(((long) gameTable.whiteTable | gameTable.blackTable));
	}
	
	private static void printTable(long table) {
		String stringTable = String.format("%64s", Long.toBinaryString(table)).replace(' ', '0');
		for (int i = 0; i < 8; i++) {
			System.out.println(new StringBuffer(stringTable.substring(i * 8, (i + 1) * 8)).reverse());
		}
	}

}
