package ca.etsmtl.breaktrough;

public class Move {

	public enum Player {
		BLACK,
		WHITE;
	}
	
	public long initialPos;
	public int move;
	public Player player;
	
	public Move(String from, String to) {
		long tableFrom = getPos(from);
		long tableTo = getPos(to);
		
		initialPos = tableFrom;
		
		setMove(tableFrom, tableTo);
	}
	
	public Move(long initialPos, int move, Move.Player player) {
		this.initialPos = initialPos;
		this.move = move;
	}
	
	private long getPos(String pos) {
		long xPos = (int)pos.charAt(0)-65;
		long yPos = pos.charAt(1) - 49;
		return 2l << (xPos + yPos * 8l) - 1;
	}
	
	private void setMove(long from, long to) {	
		long result = (long)(Math.log(to)/Math.log(2)) - (long)(Math.log(from)/Math.log(2));
		
		if (result > 0) { player = Player.BLACK; }
		else { player = Player.WHITE; }
		
		move = (byte)Math.abs(result);
	}
	
	@Override
	public String toString() {
		char a = (char)((Math.log(initialPos)/Math.log(2))%8+65);
		long posFinal = player == Move.Player.WHITE ? initialPos >>> move : initialPos << move; 
		char b = (char)((Math.log(posFinal)/Math.log(2))%8+65);
		return a + "" + (int)((Math.log(initialPos)/Math.log(2))/8+1) + " - " + b + "" + (int)((Math.log(posFinal)/Math.log(2))/8+1);
	}
}
