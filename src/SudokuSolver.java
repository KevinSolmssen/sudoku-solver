import java.io.*;
import java.util.ArrayList;

public class SudokuSolver {

	public static void main(String[] args) throws IOException {
		SudokuSolver.loadSudoku();
		
		do {
			// lauft bis keine Methode mehr das Sudoku verbessern kann
			
			Sudoku.shouldRepeat = false;	
			
			Sudoku.printPossibilities();					
			Sudoku.crossHatching();			
			Sudoku.singleCheck();			
			Sudoku.pairCheck();			
			Sudoku.hiddenPairsRemoveBox();			
			Sudoku.hiddenPairsRemoveColumnRow();
			Sudoku.XWing();
			Sudoku.XYWing();			
			Sudoku.XYZWing();			
		} while (Sudoku.shouldRepeat);
		
		Sudoku.printPossibilities();
		Sudoku.printSudoku();
		Sudoku.checkForPossibleSudoku();
	}
	
	public static void loadSudoku() throws IOException {
		// speichert das Sudoku von der .txt Datei in Sudoku.sudoku
		
		FileReader textFile = new FileReader("sudoku.txt");
		BufferedReader reader = new BufferedReader(textFile);
		
		String line;
		Integer index = 0;
		ArrayList<ArrayList<Integer>> disposalSudoku = new ArrayList<ArrayList<Integer>>();
		while ((line = reader.readLine()) != null) {
			String[] splitLine = line.split(" ");
			ArrayList<Integer> row = new ArrayList<Integer>();
			
			for (int i = 0; i < splitLine.length; i++) {
				int element = Integer.parseInt(splitLine[i]);
				row.add(element);
			}
			
			disposalSudoku.add(index, row);
			index++;
		}
		Sudoku.sudoku = disposalSudoku;
		reader.close();
		Sudoku.SinglePossibilitySolutionsRefresh();
	}
}