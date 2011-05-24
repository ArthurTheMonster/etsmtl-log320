package ca.etsmtl.sudoku;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class SudokuSolver {

	private static final String TXT_EXTENSION = ".txt";
	
	private static String SUDOKU_FILE_NAME;
	private static String SOLUTION_FILE_NAME;
	
	private static final int SUDOKU_LENGTH = 9;
	
	private static final byte[][] SUDOKU = new byte[SUDOKU_LENGTH][SUDOKU_LENGTH];
	private static final byte[][] SUDOKU_CANDIDATES = new byte[SUDOKU_LENGTH][SUDOKU_LENGTH];

	private static final byte[][] SOLUTION = new byte[SUDOKU_LENGTH][SUDOKU_LENGTH];
	
	private static final SudokuBox[][] SUDOKU_BOXES = new SudokuBox[SUDOKU_LENGTH][SUDOKU_LENGTH];
	private static final List<SudokuBox> SORTED_SUDOKU_BOXES = new ArrayList<SudokuBox>(SUDOKU_LENGTH * SUDOKU_LENGTH);
	
	public static void main(String[] args) {
		if (args.length > 1) {
			SUDOKU_FILE_NAME = args[0];
			SOLUTION_FILE_NAME = args[1];
			if (SUDOKU_FILE_NAME.endsWith(TXT_EXTENSION) && SOLUTION_FILE_NAME.endsWith(TXT_EXTENSION)) {
				readFile(SUDOKU_FILE_NAME, SUDOKU);
				readFile(SOLUTION_FILE_NAME, SOLUTION);
				System.out.println("Before");
				for (byte[] sudokuRow : SUDOKU) {
					System.out.println(Arrays.toString(sudokuRow));
				}
				for (byte i = 0; i < SUDOKU_LENGTH; i++) {
					for (byte j = 0; j < SUDOKU_LENGTH; j++) {
						if (isEmpty(i, j)) {
							if (isValid(i, j, SOLUTION[i][j])) {
								SUDOKU[i][j] = SOLUTION[i][j];
							} else {
								SUDOKU[i][j] = (byte) 255;
							}
						}
					}
				}
				System.out.println("After");
				for (byte[] sudokuRow : SUDOKU) {
					System.out.println(Arrays.toString(sudokuRow));
				}
			}
		} else if (args.length > 0) {
			SUDOKU_FILE_NAME = args[0];
			if (SUDOKU_FILE_NAME.endsWith(TXT_EXTENSION)) {
				readFile(SUDOKU_FILE_NAME, SUDOKU);
				for (byte i = 0; i < SUDOKU_LENGTH; i++) {
					for (byte j = 0; j < SUDOKU_LENGTH; j++) {
						SudokuBox box = createSudokuBox(i,j);
							
						SORTED_SUDOKU_BOXES.add(box);
						SUDOKU_BOXES[i][j] = box;
					}
				}
				Collections.sort(SORTED_SUDOKU_BOXES);
				for (SudokuBox sudokuBox : SORTED_SUDOKU_BOXES) {
					System.out.println(sudokuBox);
				}
			}
		}

		boolean solving = false;
		
		while (solving) {
			
			SudokuBox sudokuBox = getNextSudokuBox(null);
			
			
		}
		
		
	}
	
	private static SudokuBox getNextSudokuBox(List<SudokuBox> listSudokuBox) {
		for (SudokuBox sudokuBox : listSudokuBox) {
			if (sudokuBox.value == 0) {
				return sudokuBox;
			}
		}
		return null;
	}
	
	
	private static boolean solveBox(List<SudokuBox> listSudokuBox) {
		Collections.sort(listSudokuBox);
		
		SudokuBox box = getNextSudokuBox(listSudokuBox);
		
		return true;
	}

	private static void readFile(String fileName, byte[][] sudoku) {
		try {
			Scanner scanner = new Scanner(new File(fileName));
			int rowIndex = 0;
			while (scanner.hasNextLine()) {
				setSudokuRow(scanner.nextLine(), rowIndex, sudoku);
				rowIndex++;
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static void setSudokuRow(String sudokuRow, int rowIndex, byte[][] sudoku) {
		char[] sudokuRowCharacters = sudokuRow.toCharArray();
		for (int i = 0; i < sudokuRowCharacters.length; i++) {
			sudoku[rowIndex][i] = Byte.valueOf(String.valueOf(sudokuRowCharacters[i]));
		}
	}
	
	private static boolean isEmpty(byte x, byte y) {
		return SUDOKU[x][y] == 0;
	}
	
	private static boolean isValid(byte x, byte y, byte value) {
		for (int i = 0; i < SUDOKU_LENGTH; i++) {
			if (SUDOKU[x][i] == value) {
				return false;
			}
		}
		for (int i = 0; i < SUDOKU_LENGTH; i++) {
			if (SUDOKU[i][y] == value) {
				return false;
			}
		}
		int squareNumX = x/3;
		int squareNumY = y/3;
		for (int i = squareNumX*3; i<(squareNumX+1)*3; i++) {
			for (int j = squareNumY*3; j<(squareNumY+1)*3; j++) {
				if (SUDOKU[i][j] == value) {
					return false;
				} 
			}	
		}
		return true;
	}
	
	private static void setValue(SudokuBox box, byte value) {
		box.value = value;
		
		for (int i = 0; i < SUDOKU_LENGTH; i++) {
			SUDOKU_BOXES[i][box.y].candidates.remove(new Byte(value));
		}
		for (int j = 0; j < SUDOKU_LENGTH; j++) {
			SUDOKU_BOXES[box.x][j].candidates.remove(new Byte(value));
		}
		
		int squareNumX = box.x/3;
		int squareNumY = box.y/3;
		for (int i = squareNumX*3; i<(squareNumX+1)*3; i++) {
			if (i != box.x) {
				for (int j = squareNumY*3; j<(squareNumY+1)*3; j++) {
					if (j != box.y) {
						SUDOKU_BOXES[box.x][j].candidates.remove(new Byte(value));
					}
				}	
			}
		}
	}
	
	private static SudokuBox createSudokuBox(byte x, byte y) {
		SudokuBox sudokuBox = new SudokuBox(x, y, SUDOKU[x][y]);
		for (int i = 0; i < SUDOKU_LENGTH; i++) {
			if (SUDOKU[x][i] != 0) {
				sudokuBox.candidates.remove(new Byte(SUDOKU[x][i]));
			}
		}
		for (int i = 0; i < SUDOKU_LENGTH; i++) {
			if (SUDOKU[i][y] != 0) {
				sudokuBox.candidates.remove(new Byte(SUDOKU[i][y]));
			}
		}
		int squareNumX = x/3;
		int squareNumY = y/3;
		for (int i = squareNumX*3; i<(squareNumX+1)*3; i++) {
			if (i != x) {
				for (int j = squareNumY*3; j<(squareNumY+1)*3; j++) {
					if (j != y) {
						if (SUDOKU[i][j] != 0) {
							sudokuBox.candidates.remove(new Byte(SUDOKU[i][j]));
						} 
					}
				}	
			}
		}
		return sudokuBox;
	}

}
