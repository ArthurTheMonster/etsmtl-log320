package ca.etsmtl.breaktrough;

import java.util.ArrayList;
import java.util.List;

import ca.etsmtl.breaktrough.NinjaClient.Player;

public class GameTable {
	private static final long STARTING_BLACK_TABLE = -281474976710656l;
	private static final long STARTING_WHITE_TABLE = 65535l;
	
	public long blackTable;
	public long whiteTable;
	
	private int blackPawnCount;
	private int whitePawnCount;
	
	public GameTable() {
		blackTable = STARTING_BLACK_TABLE;
		whiteTable = STARTING_WHITE_TABLE;
		blackPawnCount = 16;
		whitePawnCount = 16;
	}
	
	public GameTable(GameTable gameTable) {
		blackTable = gameTable.blackTable;
		whiteTable = gameTable.whiteTable;
		setBlackPawnCount(gameTable.getBlackPawnCount());
		setWhitePawnCount(gameTable.getWhitePawnCount());
	}
	
	// The move MUST be valid - won't be validated
	public static void move(GameTable gameTable, Move move) {
		long ourTable;
		long oppTable;
		
		if (move.player == Player.WHITE) {
			ourTable = gameTable.whiteTable;
			oppTable = gameTable.blackTable;
		}
		else {
			ourTable = gameTable.blackTable;
			oppTable = gameTable.whiteTable;
		}
		
		ourTable = ourTable &~move.initialPos;
		ourTable |= move.finalPos;
		
		if ((oppTable & move.finalPos) > 0) {
			if (move.player == Player.WHITE) {
				gameTable.setBlackPawnCount(gameTable.getBlackPawnCount()-1);
				assert(gameTable.getBlackPawnCount() >= 0);
			} else {
				gameTable.setWhitePawnCount(gameTable.getWhitePawnCount()-1);
				assert(gameTable.getWhitePawnCount() >= 0);
			}
			oppTable = oppTable &~move.finalPos;
			if (move.player == Player.WHITE) {
				gameTable.blackTable = oppTable;
			} else {
				gameTable.whiteTable = oppTable;
			}
		}
		
		if (move.player == Player.WHITE) {
			gameTable.whiteTable = ourTable;
		} else {
			gameTable.blackTable = ourTable;
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
		
		for (int i = 0; i < 63; i++) {
			long currentPawn = myTable & 1l << i;
			if (player.equals(Player.WHITE)) {
				currentPawn = myTable & -9223372036854775808l >>> i;
			}
			
			if (currentPawn != 0) {
				validMovePawns.addAll(GetValidMoves(player, currentPawn, myTable, oppTable));
			}
		}
		
		return validMovePawns;
		
	}
	
	private static List<Move> GetValidMoves(Player player, long pawn, long myTable, long oppTable) {
		List<Move> validMovePawn = new ArrayList<Move>();
		long newPawn = 0;
		
		//Left
		newPawn = (player == Player.BLACK ? pawn >> 9 : pawn << 7);
		if (isLeftDiagonalValid(newPawn, myTable)) {
			validMovePawn.add(new Move(pawn, player == Player.BLACK ? 9 : 7, player));
		}

		//Straight
		newPawn = (player == Player.BLACK ? pawn >> 8 : pawn << 8);
		if (isStraightMoveValid(newPawn, myTable, oppTable)) {
			validMovePawn.add(new Move(pawn, 8, player));
		}
		
		//Right
		newPawn = (player == Player.BLACK ? pawn >> 7 : pawn << 9);
		if (isRightDiagonalValid(newPawn, myTable)) {
			validMovePawn.add(new Move(pawn, player == Player.BLACK ? 7 : 9, player));
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
		
		for (int i = 0; i < 63; i++) {
			long currentPawn = myTable & 1l << i;
			
			if (player == Player.WHITE) {
				if (currentPawn == -9223372036854775808l) {
					score += 128;
				}
				else if (currentPawn <= 128l) {
					score += 1;
				} 
				else if (currentPawn <= 32768l) {
					score += 4;	
				}
				else if (currentPawn <= 8388608l) {
					score += 16;
				}
				else if (currentPawn <= 2147483648l) {
					score += 64;
				}
				else if (currentPawn <= 549755813888l) {
					score += 256;
				}
				else if (currentPawn <= 140737488355328l) {
					score += 1024;
				}
				else if (currentPawn <= 36028797018963968l) {
					score += 4048;
				}
				else if (currentPawn <= 4611686018427387904l) {
					score += 16000;
				}
			}
			else {
				if (currentPawn == -9223372036854775808l) {
					score += 1;
				}
				else if (currentPawn <= 128l) {
					score += 16000;
				} 
				else if (currentPawn <= 32768l) {
					score += 4048;	
				}
				else if (currentPawn <= 8388608l) {
					score += 1024;
				}
				else if (currentPawn <= 2147483648l) {
					score += 256;
				}
				else if (currentPawn <= 549755813888l) {
					score += 64;
				}
				else if (currentPawn <= 140737488355328l) {
					score += 16;
				}
				else if (currentPawn <= 36028797018963968l) {
					score += 4;
				}
				else if (currentPawn <= 4611686018427387904l) {
					score += 1;
				}	
			}
			
			if (currentPawn > 0) {
				myPawnCount--;
				if (myPawnCount == 0) {
					return score;
				}
			}
		}
		
		return 0;
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
	
	public int getBlackPawnCount() {
		return blackPawnCount;
	}

	public void setBlackPawnCount(int blackPawnCount) {
		this.blackPawnCount = blackPawnCount;
	}

	public int getWhitePawnCount() {
		return whitePawnCount;
	}

	public void setWhitePawnCount(int whitePawnCount) {
		this.whitePawnCount = whitePawnCount;
	}

}
