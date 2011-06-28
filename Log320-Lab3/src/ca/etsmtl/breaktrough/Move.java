package ca.etsmtl.breaktrough;

import ca.etsmtl.breaktrough.NinjaClient.Player;

public class Move {

	public long initialPos;
	public int move;
	public long finalPos;
	
	public Player player;
	
	public Move(String from, String to, Player player) {
		long tableFrom = getPos(from);
		long tableTo = getPos(to);
		
		initialPos = tableFrom;
		this.player = player;
		setMove(tableFrom, tableTo);
	}
	
	public Move(long initialPos, int move, Player player) {
		this.initialPos = initialPos;
		this.move = move;
		this.player = player;
		setFinalPosition();
	}
	
	private long getPos(String pos) {
		long xPos = (int)pos.charAt(0)-65;
		long yPos = pos.charAt(1) - 49;
		return 2l << (xPos + yPos * 8l) - 1;
	}
	
	private void setMove(long from, long to) {	
		long result = (long)(Math.log(to)/Math.log(2)) - (long)(Math.log(from)/Math.log(2));
		
		move = (byte)Math.abs(result);
		
		setFinalPosition();
	}
	
	private void setFinalPosition() {
		finalPos = player == Player.BLACK ? initialPos >>> move : initialPos << move; 
	}
	
	@Override
	public String toString() {
		char a = (char)((Math.log(initialPos)/Math.log(2))%8+65);
		char b = (char)((Math.log(finalPos)/Math.log(2))%8+65);
		return a + "" + (int)((Math.log(initialPos)/Math.log(2))/8+1) + " - " + b + "" + (int)((Math.log(finalPos)/Math.log(2))/8+1);
	}
}
