package ca.etsmtl.sudoku;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SudokuBox implements Comparable<SudokuBox> {

	private static final Byte[] CANDIDATES = new Byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9};
	
	public byte x;
	public byte y;
	
	public byte value;
	
	public List<Byte> candidates = new ArrayList<Byte>(Arrays.asList(CANDIDATES));
	
	public SudokuBox() {
		this((byte) 0, (byte) 0);
	}
	
	public SudokuBox(byte x, byte y) {
		this(x, y, (byte)0);
	}
	
	public SudokuBox(byte x, byte y, byte value) {
		this.x = x;
		this.y = y;
		this.value = value;
	}
	@Override
	public int compareTo(SudokuBox sudokuBox) {
		return candidates.size() - sudokuBox.candidates.size();
	}
	
	@Override
	public String toString() {
		return "Sudoku Box, x: " + x + " y: " + y + " candidates count:" + candidates.size() + " candidates:" + candidates.toString();
	}
	
}
