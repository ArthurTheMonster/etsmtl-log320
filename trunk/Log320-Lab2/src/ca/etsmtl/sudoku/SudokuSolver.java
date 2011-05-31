package ca.etsmtl.sudoku;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SudokuSolver {

	private static final String SUD_EXTENSION = ".sud";

	private static String SUDOKU_FILE_NAME;

	private static final int SUDOKU_LENGTH = 9;

	private static byte[][] SUDOKU_SOLUTION;

	private static final byte[][] SUDOKU = new byte[SUDOKU_LENGTH][SUDOKU_LENGTH];
	private static byte[][][] SUDOKU_CANDIDATES = new byte[SUDOKU_LENGTH][SUDOKU_LENGTH][SUDOKU_LENGTH + 1];

	static {
		for (byte i = 0; i < SUDOKU_LENGTH; i++) {
			for (byte j = 0; j < SUDOKU_LENGTH; j++) {
				SUDOKU_CANDIDATES[i][j][0] = 9;
				for (byte k = 1; k < SUDOKU_LENGTH + 1; k++) {
					SUDOKU_CANDIDATES[i][j][k] = 1;
				}
			}
		}
	}

	public static void main(String[] args) {
		if (args.length > 0) {
			SUDOKU_FILE_NAME = args[0];
			if (SUDOKU_FILE_NAME.endsWith(SUD_EXTENSION)) {
				long firstTime = System.currentTimeMillis();
				readFile();
				boolean solved = solveSudoku2(SUDOKU, SUDOKU_CANDIDATES);
				if (!solved) {
					System.out.println("No.");					
				}
				System.out.println("Total:" + (System.currentTimeMillis() - firstTime));
				for (byte[] line : SUDOKU_SOLUTION) {
					System.out.println(Arrays.toString(line));
				}
			}
		}
	}

	private static boolean solveSudoku2(byte[][] sudoku, byte[][][] candidateCopy) {
		byte[][] sudokuCopy = new byte[SUDOKU_LENGTH][SUDOKU_LENGTH];
		for (int i = 0; i < SUDOKU_LENGTH; i++) {
			System.arraycopy(sudoku[i], 0, sudokuCopy[i], 0, sudoku[i].length);

		}
		byte[] nextPosition = getNextPosition(sudokuCopy, candidateCopy);
		if (nextPosition[0] == -1 || nextPosition[1] == -1) {
			SUDOKU_SOLUTION = sudokuCopy;
			return true;
		} else {
			Byte[] values = getNextValues(candidateCopy[nextPosition[0]][nextPosition[1]]);
			if (values.length == 0) {
				return false;
			} else {
				for (Byte value : values) {
					sudokuCopy[nextPosition[0]][nextPosition[1]] = value;
					byte[][][] candidateCopy2 = new byte[SUDOKU_LENGTH][SUDOKU_LENGTH][10];
					for (int i = 0; i < SUDOKU_LENGTH; i++) {
						for (int j = 0; j < SUDOKU_LENGTH; j++) {
							System.arraycopy(candidateCopy[i][j], 0, candidateCopy2[i][j], 0, candidateCopy[i][j].length);
						}
					}
					updateCandidate(nextPosition[0], nextPosition[1], value, candidateCopy);
					if (!solveSudoku2(sudokuCopy, candidateCopy)) {
						candidateCopy = candidateCopy2;
					} else {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static Byte[] getNextValues(byte[] sudokuCandidates) {
		List<Byte> values = new ArrayList<Byte>();
		for (Byte i = 1; i < SUDOKU_LENGTH + 1; i++) {
			if (sudokuCandidates[i] != 0) {
				values.add(i);
			}
		}
		return values.toArray(new Byte[values.size()]);
	}

	private static byte[] getNextPosition(byte[][] sudoku, byte[][][] sudokuCandidates) {
		byte[] nextPosition = new byte[] {-1, -1, 10};
		for (byte i = 0; i < SUDOKU_LENGTH; i++) {
			for (byte j = 0; j < SUDOKU_LENGTH; j++) {
				// Optimisation: s'inquiete davance des que je trouve un # = 0
				if (sudokuCandidates[i][j][0] < nextPosition[2] && sudoku[i][j] == 0) {
					nextPosition[2] = sudokuCandidates[i][j][0];
					nextPosition[0] = i;
					nextPosition[1] = j;
				}
			}
		}
		return nextPosition;
	}

	private static void readFile() {
		File file = new File(SUDOKU_FILE_NAME);
		FileInputStream fileInputStream;
		byte[] fileBytes = null;
		try {
			fileInputStream = new FileInputStream(file);
			fileBytes = new byte[(int) file.length()];
			fileInputStream.read(fileBytes);
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte rowIndex = 0;
		byte colIndex = 0;
		byte sudokuChar;
		for (byte fileByte : fileBytes) {
			if (fileByte != 48 && colIndex<9) {
				sudokuChar = (byte) (fileByte-48);
				SUDOKU[rowIndex][colIndex] = sudokuChar;
				updateCandidate(rowIndex, colIndex, sudokuChar, SUDOKU_CANDIDATES);
			}
			colIndex++;
			if(colIndex >= 11){
				colIndex = 0;
				rowIndex ++;
			}
		}
	}

	public static void updateCandidate(byte x, byte y, byte value, byte[][][] sudokuCandidates) {
		for (int i = 0; i < SUDOKU_LENGTH; i++) {
			removeCandidates(sudokuCandidates[i][y], value);
			removeCandidates(sudokuCandidates[x][i], value);
		}
		int squareNumX = x - (x % 3);
		int squareNumY = y - (y % 3);
		for (int i = squareNumX; i<(squareNumX+3); i++) {
			if (x != i) {
				for (int j = squareNumY; j<(squareNumY+3); j++) {
					if (y != j) {
						removeCandidates(sudokuCandidates[i][j], value);
					}
				}	
			}
		}
	}

	public static boolean removeCandidates(byte[] currentCandidate, byte value) {
		if (currentCandidate[value] == 1) {
			currentCandidate[0]--;
			currentCandidate[value] = 0;
			return true;
		}
		return false;
	}
	
}
