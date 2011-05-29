package ca.etsmtl.sudoku;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SudokuSolver {

	private static final String TXT_EXTENSION = ".txt";

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
			if (SUDOKU_FILE_NAME.endsWith(TXT_EXTENSION)) {
				readFile();

				/*for (byte[] row : SUDOKU) {
					System.out.println(Arrays.toString(row));	
				}

				System.out.println("---------------------");*/

				/*for (byte[][] row : SUDOKU_CANDIDATES) {
					for (byte[] a : row) {
						System.out.println(Arrays.toString(a));	
					}
				}*/

				boolean isPossible = solveSudoku2(SUDOKU, SUDOKU_CANDIDATES);
				if (isPossible) {
					for (byte[] row : SUDOKU_SOLUTION) {
						System.out.println(Arrays.toString(row));	
					}
				}
				else {
					System.out.println("No.");					
				}

				
			}
		}
	}

	/*Résoudre grille (n)
	Debut
	  Rechercher tous les candidats
	  Rechercher la première cellule qui possède le moins de candidats possibles
	  Si toutes les cellules sont remplies
	  Alors La résolution est terminée avec succès
	  Sinon 
	    Si on a trouvé une cellule avec aucun candidat
	    Alors la grille n'a pas de solution
	    Sinon 
	      Pour chaque candidat de la cellule trouvée
	      Debut
	        remplir la cellule avec le candidat
	        Résoudre la grille (N+1)
	      Fin pour
	    Fin si
	  Fin si
	Fin*/
	
	private static boolean solveSudoku2(byte[][] sudokuCopy, byte[][][] candidateCopy) {
		/*byte[][][] candidateCopy = new byte[9][9][10];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				for (int k = 0; k < 10; k++) {
					candidateCopy[i][j][k] = sudokuCandidates[i][j][k];
				}
			}
		}
		
		byte[][] sudokuCopy = new byte[9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				sudokuCopy[i][j] = sudoku[i][j];
			}
		}*/
		
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
					byte[][][] candidateCopy2 = new byte[9][9][10];
					for (int i = 0; i < 9; i++) {
						for (int j = 0; j < 9; j++) {
							for (int k = 0; k < 10; k++) {
								candidateCopy2[i][j][k] = candidateCopy[i][j][k];
							}
						}
					}
					candidateCopy = updateCandidate(nextPosition[0], nextPosition[1], value, candidateCopy);
					if (!solveSudoku2(sudokuCopy, candidateCopy)) {
						candidateCopy = candidateCopy2;
						System.out.println("BACKTRACK!");
					} else {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/*private static boolean solveSudoku(byte[][] sudoku, byte[][][] sudokuCandidates) {
		byte[] nextPosition = getNextPosition(sudoku, sudokuCandidates);
		if (nextPosition[2] == 0) {
			return false;
		}
		
		if (nextPosition[2] == 10) {
			SUDOKU_SOLUTION = sudoku;
			return true;
		}

		//byte nextValue = getNextValue(sudokuCandidates[nextPosition[0]][nextPosition[1]]);
		//System.out.println("PositionX:" + nextPosition[0] + " PositionY:" + nextPosition[1] + " nextValue:" + nextValue);
		if (nextValue == -1) {
			return false;
		}

		sudoku[nextPosition[0]][nextPosition[1]] = nextValue;
		sudokuCandidates = updateCandidate(nextPosition[0],nextPosition[1], nextValue, sudokuCandidates);

		while (!solveSudoku(sudoku,sudokuCandidates)) {
			/nextValue = getNextValue(sudokuCandidates[nextPosition[0]][nextPosition[1]]);
			//System.out.println("PositionX:" + nextPosition[0] + " PositionY:" + nextPosition[1] + " nextValue:" + nextValue);
			if (nextValue == -1) return false;
			sudoku[nextPosition[0]][nextPosition[1]] = nextValue;
			sudokuCandidates = updateCandidate(nextPosition[0],nextPosition[1], nextValue, sudokuCandidates);
		}

		return true;
	}*/

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
				//System.out.println(Arrays.toString(sudokuCandidates[i][j]));
				// Optimisation: s'inquiete davance des que je trouve un # = 0
				if (sudokuCandidates[i][j][0] < nextPosition[2] && sudoku[i][j] == 0) {
					nextPosition[2] = sudokuCandidates[i][j][0];
					nextPosition[0] = i;
					nextPosition[1] = j;
				}
			}
		}
		//System.out.println("lowestCandidateCount" + nextPosition[2]);
		//System.out.println("x:" + nextPosition[0] + " y:" + nextPosition[1]);
		return nextPosition;
	}

	private static void readFile() {
		try {
			Scanner scanner = new Scanner(new File(SUDOKU_FILE_NAME));
			byte rowIndex = 0;
			while (scanner.hasNextLine()) {
				setSudokuRow(scanner.nextLine(), rowIndex);
				rowIndex++;
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void setSudokuRow(String sudokuRow, byte rowIndex) {
		char[] sudokuRowCharacters = sudokuRow.toCharArray();
		for (byte i = 0; i < sudokuRowCharacters.length; i++) {
			byte value = Byte.valueOf(String.valueOf(sudokuRowCharacters[i]));
			SUDOKU[rowIndex][i] = value;
			if (value != 0) {
				SUDOKU_CANDIDATES = updateCandidate(rowIndex, i, value, SUDOKU_CANDIDATES);
				for (byte[][] row : SUDOKU_CANDIDATES) {
					for (byte[] a : row) {
						//System.out.println(Arrays.toString(a));	
					}
				}
				//System.out.println("----------------------------");
			}
		}
	}

	public static byte[][][] updateCandidate(byte x, byte y, byte value, byte[][][] sudokuCandidates) {
		sudokuCandidates[x][y] = removeCandidates(sudokuCandidates[x][y], value);
		for (int i = 0; i < SUDOKU_LENGTH; i++) {
			if (x != i) {
				sudokuCandidates[i][y] = removeCandidates(sudokuCandidates[i][y], value);
			}
		}
		for (int j = 0; j < SUDOKU_LENGTH; j++) {
			if (y != j) {
				sudokuCandidates[x][j] = removeCandidates(sudokuCandidates[x][j], value);
			}
		}
		int squareNumX = x/3;
		int squareNumY = y/3;
		for (int i = squareNumX*3; i<(squareNumX+1)*3; i++) {
			if (x != i) {
				for (int j = squareNumY*3; j<(squareNumY+1)*3; j++) {
					if (y != j) {
						sudokuCandidates[i][j] = removeCandidates(sudokuCandidates[i][j], value);
					}
				}	
			}
		}
		return sudokuCandidates;
	}

	public static byte[] removeCandidates(byte[] currentCandidate, byte value) {
		if (currentCandidate[value] == 1) {
			currentCandidate[0]--;
			currentCandidate[value] = 0;
			//System.out.println(Arrays.toString(currentCandidate));
		}
		return currentCandidate;
	}















	/*




































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
	}*/

}
