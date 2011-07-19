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
	
	public int isGameOver = 0; // 0 = no, 1 = white win, 2 = black win
	
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
			
			if (move.finalPos > 36028797018963968l || move.finalPos < 0) {
				isGameOver = 1;
			}
		}
		else {
			blackTable = blackTable &~move.initialPos;
			blackTable |= move.finalPos;
			if ((whiteTable & move.finalPos) != 0) {
				whitePawnCount--;
			}
			whiteTable = whiteTable &~move.finalPos;
			
			if (move.finalPos <= 128 && move.finalPos > 0) {
				isGameOver = 2;
			}
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
			long currentPawn = myTable & 1l << i; // Black
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
		
		long newPawn = 0;
		
		// Ok this big chunk of code could have been reduce to 10 lines
		// But we need performance, it means that we can't afford the log() and mod()
		if (player == Player.WHITE) {
			// This pawn isn't on the last time
			if (pawn <= 36028797018963968l && pawn >= 0) {
				
				// LEFT DIAGONAL - If this pawn isn't in the right column
				if (pawn != 1 && pawn != 256 && pawn != 65536 && pawn != 16777216 && pawn != 4294967296l &&
						pawn != 1099511627776l && pawn != 281474976710656l) {
					
					newPawn = pawn << 7;
					// If I don't have a pawn at my final place
					if ((myTable & newPawn) == 0) {
						validMovePawn.add(new Move(pawn, 7, player));
					}
				}
				
				// RIGHT DIAGONAL - If this pawn isn't in the left column
				if (pawn != 128 && pawn != 32768 && pawn != 8388608 && pawn != 2147483648l && pawn != 549755813888l &&
						pawn != 140737488355328l && pawn != 36028797018963968l) {
					
					newPawn = pawn << 9;
					// If I don't have a pawn at my final place
					if ((myTable & newPawn) == 0) {
						validMovePawn.add(new Move(pawn, 9, player));
					}
				}
				
				// FOWARD
				newPawn = pawn << 8;
				// If he and I don't have a pawn at my final place
				if ((myTable & newPawn) == 0 && (oppTable & newPawn) == 0) {
					validMovePawn.add(new Move(pawn, 8, player));
				}				
				
				return validMovePawn;
			}
		} else {
			// This pawn isn't on the last line
			if (pawn > 128l || pawn < 0) {
			
				// LEFT DIAGONAL - If this pawn isn't in the left column
				if (pawn != -9223372036854775808l && pawn != 32768 && pawn != 8388608 && pawn != 2147483648l && pawn != 549755813888l &&
						pawn != 140737488355328l && pawn != 36028797018963968l) {
					
					newPawn = pawn >>> 7;
					// If I don't have a pawn at my final place
					if ((myTable & newPawn) == 0) {
						validMovePawn.add(new Move(pawn, 7, player));
					}
				}
				
				// RIGHT DIAGONAL - If this pawn isn't in the right column
				if (pawn != 256 && pawn != 65536 && pawn != 16777216 && pawn != 4294967296l &&
						pawn != 1099511627776l && pawn != 281474976710656l && pawn != 72057594037927936l) {
					
					newPawn = pawn >>> 9;
					// If I don't have a pawn at my final place
					if ((myTable & newPawn) == 0) {
						validMovePawn.add(new Move(pawn, 9, player));
					}
				}
				
				// FOWARD
				newPawn = pawn >>> 8;
				// If he and I don't have a pawn at my final place
				if ((myTable & newPawn) == 0 && (oppTable & newPawn) == 0) {
					validMovePawn.add(new Move(pawn, 8, player));
				}				
				
				return validMovePawn;
			}
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
	
	private static boolean isStraightMoveValid(long newPawn, long myTable, long oppTable) {
		if ((myTable & newPawn) == 0 && (oppTable & newPawn) == 0) {
			return true;
		}
		return false;
	}
	
	/*public boolean isInDanger(long pawn, Player player) {
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
	}*/
	
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
