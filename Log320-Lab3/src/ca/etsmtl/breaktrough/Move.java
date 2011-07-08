package ca.etsmtl.breaktrough;

import ca.etsmtl.breaktrough.NinjaClient.Player;

public class Move {

	public long initialPos;
	public int move;
	public long finalPos;
	
	public Player player;
	
	public Move(String from, String to, Player player) {	
		initialPos = getPos(from);
		finalPos = getPos(to);
		this.player = player;
		setMove(initialPos, finalPos);
	}
	
	public Move(Move copyMove) {
		initialPos = copyMove.initialPos;
		move = copyMove.move;
		finalPos = copyMove.finalPos;
		player = copyMove.player;
	}
	
	public Move(long initialPos, int move, Player player) {
		this.initialPos = initialPos;
		this.move = move;
		this.player = player;
		this.finalPos = player == Player.BLACK ? initialPos >>> move : initialPos << move;
	}
	
	private long getPos(String pos) {
		long xPos = (int)pos.charAt(0)-65;
		long yPos = pos.charAt(1) - 49;

		return 1l << (xPos + yPos * 8l);
	}
	
	private void setMove(long from, long to) {	
		long result = (long)(Math.log(to)/Math.log(2)) - (long)(Math.log(from)/Math.log(2));
		
		move = (byte)Math.abs(result);
	}
	
	@Override
	public String toString() {
		char a = (char)((Math.log(initialPos)/Math.log(2))%8+65);
		char b = (char)((Math.log(finalPos)/Math.log(2))%8+65);
		return a + "" + (int)((Math.log(initialPos)/Math.log(2))/8+1) + " - " + b + "" + (int)((Math.log(finalPos)/Math.log(2))/8+1);
	}
}
