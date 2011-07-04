package ca.etsmtl.breaktrough;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import ca.etsmtl.breaktrough.NinjaClient.Player;

public class GameTable {
	private static final long STARTING_BLACK_TABLE = -281474976710656l;
	private static final long STARTING_WHITE_TABLE = 65535l;
	
	public static Hashtable<Long, Integer> transposition = new Hashtable<Long, Integer>(1000000);
	
	public long blackTable;
	public long whiteTable;
	
	public int blackPawnCount;
	public int whitePawnCount;
	
	public GameTable() {
		blackTable = STARTING_BLACK_TABLE;
		whiteTable = STARTING_WHITE_TABLE;
		blackPawnCount = 16;
		whitePawnCount = 16;
	}
	
	public GameTable(GameTable gameTable) {
		blackTable = gameTable.blackTable;
		whiteTable = gameTable.whiteTable;
		blackPawnCount =gameTable.blackPawnCount;
		whitePawnCount = gameTable.whitePawnCount;
	}
	
	public long getTable(Player player) {
		if (player.equals(Player.WHITE)) {
			return whiteTable;
		}
		return blackTable;
	}
	
	public int getTableCount(Player player) {
		if (player.equals(Player.WHITE)) {
			return whitePawnCount;
		}
		return blackPawnCount;		
	}
	
	public long getTable() {
		return (blackTable | whiteTable);
	}
	
	// The move MUST be valid - won't be validated
	public void move(Move move) {
		if (move.player == Player.WHITE) {
			whiteTable = whiteTable &~move.initialPos;
			whiteTable |= move.finalPos;
			if ((blackTable & move.finalPos) != 0) {
				blackPawnCount--;
			}
			blackTable = blackTable &~move.finalPos;
		}
		else {
			blackTable = blackTable &~move.initialPos;
			blackTable |= move.finalPos;
			if ((whiteTable & move.finalPos) != 0) {
				whitePawnCount--;
			}
			whiteTable = whiteTable &~move.finalPos;
		}		
	}
	
	public static List<Move> getAllMove(GameTable gameTable, Player player) {
		List<Move> validMovePawns = new ArrayList<Move>();
		
		long myTable = 0;
		long oppTable = 0;
		if (player == Player.WHITE) {
			myTable = gameTable.whiteTable;
			oppTable = gameTable.blackTable;
		}
		else {
			myTable = gameTable.blackTable;
			oppTable = gameTable.whiteTable;
		}
		
		for (int i = 0; i < 64; i++) {
			long currentPawn = myTable & 1l << i;
			if (player.equals(Player.WHITE)) {
				currentPawn = myTable & -9223372036854775808l >>> i;
			}
			
			if (currentPawn != 0) {
				validMovePawns.addAll(getValidMoves(player, currentPawn, myTable, oppTable));
			}
		}
		
		return validMovePawns;
		
	}
	
	private static List<Move> getValidMoves(Player player, long pawn, long myTable, long oppTable) {	
		List<Move> validMovePawn = new ArrayList<Move>();
		
		if (player == Player.WHITE) {
			if (pawn == -9223372036854775808l || pawn > 36028797018963968l) {
				return validMovePawn;
			}
		}
		else {
			if (pawn <= 128l && pawn != -9223372036854775808l) {
				return validMovePawn;				
			}
		}
		
		long newPawn = 0;
		
		//Left
		newPawn = (player == Player.BLACK ? pawn >>> 9 : pawn << 7);
		if (isLeftDiagonalValid(newPawn, myTable)) {
			validMovePawn.add(new Move(pawn, player == Player.BLACK ? 9 : 7, player));
		}

		//Straight
		newPawn = (player == Player.BLACK ? pawn >>> 8 : pawn << 8);
		if (isStraightMoveValid(newPawn, myTable, oppTable)) {
			validMovePawn.add(new Move(pawn, 8, player));
		}
		
		//Right
		newPawn = (player == Player.BLACK ? pawn >>> 7 : pawn << 9);
		if (isRightDiagonalValid(newPawn, myTable)) {
			validMovePawn.add(new Move(pawn, player == Player.BLACK ? 7 : 9, player));
		}
		
		return validMovePawn;
	}

	public boolean isValidMove(Move move) {
		// If I have a pawn a this pos
		long myTable = blackTable;
		long oppTable = whiteTable;
		if (move.player.equals(Player.WHITE)) {
			myTable = whiteTable;
			oppTable = blackTable;
		}
		
		if ((move.initialPos & myTable) == 0) {
			return false;
		}
		
		// We go straight
		if (move.move == 8) {
			return isStraightMoveValid(move.finalPos, myTable, oppTable);
		}
		// As long as I don't have a pawn on this slot..
		else {
			return ((move.finalPos & myTable) == 0);
		}
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
	
	public boolean isInDanger(long pawn, Player player) {
		if (player.equals(Player.BLACK)) {
			long newPos = pawn >>> 9;
			
			if ((whiteTable & newPos) != 0) {
				return true;
			}
			else {
				newPos = pawn >>> 7;
				return (whiteTable & newPos) != 0;
			}
		}
		else {
			long newPos = pawn << 7;
			
			if ((blackTable & newPos) != 0) {
				return true;
			}
			else {
				newPos = pawn << 9;
				return (blackTable & newPos) != 0;
			}			
		}
	}
	
	public int getTableScore(Player player) {	
		if (player.equals(Player.BLACK)) {
			return getPlayerScore(player) - getPlayerScore(Player.WHITE);
		} else {
			return getPlayerScore(player) - getPlayerScore(Player.BLACK);
		}	 
	}
	
	private int getPlayerScore(Player player) {
		int score = 0;
		long myTable = blackTable;
		int myPawnCount = blackPawnCount;
		if (player.equals(Player.WHITE)) {
			myTable = whiteTable;
			myPawnCount = whitePawnCount;
		}
		if (!transposition.containsKey(myTable)) {
			for (int i = 0; i < 64; i++) {
				long currentPawn = myTable & (1l << i);
				if (currentPawn != 0) {
					if (player.equals(Player.WHITE)) {		
						if (currentPawn == -9223372036854775808l) {
							score += 10000;
						}
						else if (currentPawn <= 128l) {
							score += 1;
						} 
						else if (currentPawn <= 32768l) {
							score += 2;	
						}
						else if (currentPawn <= 8388608l) {
							score += 4;
						}
						else if (currentPawn <= 2147483648l) {
							score += 8;
						}
						else if (currentPawn <= 549755813888l) {
							score += 16;
						}
						else if (currentPawn <= 140737488355328l) {
							score += 32;
						}
						else if (currentPawn <= 36028797018963968l) {
							score += 64;
						}
						else if (currentPawn <= 4611686018427387904l) {
							score += 10000;
						}
						else {
							System.out.println("### Error: This shouldn't happen! Unknown row.");
						}
					}
					else {
						if (currentPawn == -9223372036854775808l) {
							score += 1;
						}
						else if (currentPawn > 36028797018963968l) {
							score += 1;
						}
						else if (currentPawn > 140737488355328l) {
							score += 2;
						}
						else if (currentPawn > 549755813888l) {
							score += 4;
						}
						else if (currentPawn > 2147483648l) {
							score += 8;
						}
						else if (currentPawn > 8388608l) {
							score += 16;
						}
						else if (currentPawn > 32768l) {
							score += 32;	
						}
						else if (currentPawn > 128l) {
							score += 64;
						}
						else if (currentPawn > 0) {
							score += 10000;
						}
						else {
							System.out.println("### Error: This shouldn't happen! Unknown row.");
						}
					}
					myPawnCount--;
					if (myPawnCount == 0) {
						transposition.put(myTable, score);
						break;
					}
				}
			}
		}
		return transposition.get(myTable);
	}

	public static void printGameTable(GameTable gameTable) {
		printTable(((long) gameTable.whiteTable | gameTable.blackTable));
	}
	
	public static void printTable(long table) {
		String stringTable = String.format("%64s", Long.toBinaryString(table)).replace(' ', '0');
		for (int i = 0; i < 8; i++) {
			System.out.println(new StringBuffer(stringTable.substring(i * 8, (i + 1) * 8)).reverse());
		}
	}

}
