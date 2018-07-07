import java.util.ArrayList;

public class Sudoku {
	public static boolean shouldRepeat = false;
	public static ArrayList<ArrayList<Integer>> sudoku = new ArrayList<ArrayList<Integer>>();
	public static ArrayList<ArrayList<ArrayList<Integer>>> currentPossibilities = new ArrayList<ArrayList<ArrayList<Integer>>>();
	public static void SinglePossibilitySolutionsRefresh() {
		// rechnet alle mögliche Werte jeder Zelle des Sudokus und trägt sie in die currentPossibilities Variable
		System.out.println("Refreshing Possibilities...");
		
		boolean shouldRefreshPosibilities = false;
		
		if (currentPossibilities.isEmpty()) {
			// mit leeren ArrayList füllen beim ersten durchlauf des Programms
			
			ArrayList<ArrayList<ArrayList<Integer>>> possibilities = new ArrayList<ArrayList<ArrayList<Integer>>>();
			for (int i = 0; i < 9; i++) {
				possibilities.add(new ArrayList<ArrayList<Integer>>());
				for (int j = 0; j < 9; j++) {
					possibilities.get(i).add(j, getPossibilities(i, j));
				}
			}
			System.out.println("Created possibilities");
			
			currentPossibilities = possibilities;
		} else {
			// alle mögliche Werte jeder Zelle wieder aktualisieren
			
			for (int i = 0; i < currentPossibilities.size(); i++) { // iteriert über die Reihen der möglichen Werte
				for (int j = 0; j < currentPossibilities.get(i).size(); j++) { // iteriert über einzelne Elemente
					if (currentPossibilities.get(i).get(j).get(0) != 0) {
						ArrayList<ArrayList<Integer>> listToCompare = new ArrayList<ArrayList<Integer>>();
						listToCompare.add(getPossibilities(i, j));
						listToCompare.add(currentPossibilities.get(i).get(j));
						
						if (getSimilarietiesInArray(listToCompare).size() != 0) {
							currentPossibilities.get(i).set(j, getSimilarietiesInArray(listToCompare));
						}

						if (currentPossibilities.get(i).get(j).size() == 1) {
							
							System.out.println(i + " " + j + ": " + currentPossibilities.get(i).get(j).get(0));
							sudoku.get(i).set(j, currentPossibilities.get(i).get(j).get(0));
							currentPossibilities.get(i).get(j).set(0, new Integer(0));
							shouldRepeat = true;
							shouldRefreshPosibilities = true;
						}
					}
				}
			}
		}
		if (shouldRefreshPosibilities) {
			// Wenn ein Wert geändert wurde, wiederholen
			SinglePossibilitySolutionsRefresh();
		}
		
		System.out.println("End");
		System.out.println("");
	}
	public static void crossHatching() {
		// implementiert die Crosshatching Lösungsmethode
		
		System.out.println("Starting crossHatching...");
		SinglePossibilitySolutionsRefresh();
		for (int i = 0; i < 3; i++) { // iteriert über die Reihen von Boxen
			for (int j = 0; j < 3; j++) { // iteriert über einzelne Boxe
				for (int j2 = 0; j2 < getCoordinatesOfEmptyElementsInBox(i, j).size(); j2++) { // iteriert über einzelne Zellen in der Box
					ArrayList<Integer> possibleNumbersInBox = new ArrayList<Integer>();
					for (int l2 = 0; l2 < 9; l2++) { // fügt alle mögliche Werte einer Box in possibleNumbersInBox ein
						if (!getArrayOfBox(getCoordinatesOfEmptyElementsInBox(i, j).get(0).get(0), getCoordinatesOfEmptyElementsInBox(i, j).get(0).get(1)).contains(l2+1)) {
							possibleNumbersInBox.add(l2+1);
						}
					}
					
					ArrayList<ArrayList<Integer>> coordinatesOfEmptyElementsInBoxWithoutj2 = getCoordinatesOfEmptyElementsInBox(i, j);
					coordinatesOfEmptyElementsInBoxWithoutj2.remove(j2);
					
					ArrayList<ArrayList<Integer>> collectionOfNotPossibleInRowAndColumnButBox = new ArrayList<ArrayList<Integer>>();
					
					ArrayList<Integer> coordinatesOfCurrentElement = getCoordinatesOfEmptyElementsInBox(i, j).get(j2);
										
					for (int k = 0; k < coordinatesOfEmptyElementsInBoxWithoutj2.size(); k++) { // iteriert über einzelne Elemente in der Box ohne j2
						// erstellt ein ArrayList von ArrayList von alle unmögliche Werte jeder Zeile und Spalte aber die für die Box möglich sind
						
						ArrayList<Integer> notPossibleElementsInRow = new ArrayList<Integer>();
						int rowOfCurrentElement = coordinatesOfEmptyElementsInBoxWithoutj2.get(k).get(0);
						
						ArrayList<Integer> notPossibleElementsInColumn = new ArrayList<Integer>();
						int columnOfCurrentElement = coordinatesOfEmptyElementsInBoxWithoutj2.get(k).get(1);
						
						for (int l = 0; l < 9; l++) {
							// erstellt ein ArrayList, der Werte die in die Zelle k nicht möglich sind, in Beziehung mit der Reihe
							// und eine in Beziehung mit der Spalte
							
							if (sudoku.get(rowOfCurrentElement).contains(l+1)) {
								notPossibleElementsInRow.add(l+1);
							}
							
							if (getArrayOfColumnFromCoordinate(columnOfCurrentElement).contains(l+1)) {
								notPossibleElementsInColumn.add(l+1);
							}
						}
						
						ArrayList<ArrayList<Integer>> notPossibleElementsInCell = new ArrayList<ArrayList<Integer>>(); 
						notPossibleElementsInCell.add(notPossibleElementsInColumn);
						notPossibleElementsInCell.add(notPossibleElementsInRow);
						
						ArrayList<ArrayList<Integer>> listToCheckForSimilarieties = new ArrayList<ArrayList<Integer>>();
						listToCheckForSimilarieties.add(getCombinationOfArrays(notPossibleElementsInCell));
						listToCheckForSimilarieties.add(possibleNumbersInBox);
						
						ArrayList<Integer> notPossibleElementsInRowAndColumnButPossibleInBox = new ArrayList<Integer>();
						notPossibleElementsInRowAndColumnButPossibleInBox = getSimilarietiesInArray(listToCheckForSimilarieties);
						
						collectionOfNotPossibleInRowAndColumnButBox.add(notPossibleElementsInRowAndColumnButPossibleInBox);
					}
					
					ArrayList<Integer> possibilitiesInj2 = getSimilarietiesInArray(collectionOfNotPossibleInRowAndColumnButBox);
					
					ArrayList<Integer> zeroArray = new ArrayList<Integer>();
					zeroArray.add(0);
					
					if (possibilitiesInj2.size() == 1) {
						// Wenn ein möglicher Wert nur in j2 vorhanden sein kann, wird er in j2 eingesetzt
						
						System.out.println(coordinatesOfCurrentElement.get(0) + " " + coordinatesOfCurrentElement.get(1) + ": " + possibilitiesInj2.get(0));
						sudoku.get(coordinatesOfCurrentElement.get(0)).set(coordinatesOfCurrentElement.get(1), possibilitiesInj2.get(0));
						possibleNumbersInBox.remove(possibilitiesInj2.get(0));
						
						currentPossibilities.get(coordinatesOfCurrentElement.get(0)).set(coordinatesOfCurrentElement.get(1), zeroArray);
						
						shouldRepeat = true;
					}
					
					if (possibleNumbersInBox.size() == 1) {
						System.out.println(getCoordinatesOfEmptyElementsInBox(i, j).get(0).get(0) + " " + getCoordinatesOfEmptyElementsInBox(i, j).get(0).get(1) + ": " + possibleNumbersInBox.get(0));
						currentPossibilities.get(getCoordinatesOfEmptyElementsInBox(i, j).get(0).get(0)).set(getCoordinatesOfEmptyElementsInBox(i, j).get(0).get(1), zeroArray);
						sudoku.get(getCoordinatesOfEmptyElementsInBox(i, j).get(0).get(0)).set(getCoordinatesOfEmptyElementsInBox(i, j).get(0).get(1), possibleNumbersInBox.get(0));
						shouldRepeat = true;
					}
				}
			}
		}
		
		System.out.println("End");
		System.out.println("");
	}
	public static void singleCheck() {
		// kontrolliert ob ein möglicher Wert nur einmal in der Zeile, Spalte oder Box vorkommt
		// Wenn ja wird er in dieser Zelle eingesetzt wo sie vorkommt
		
		System.out.println("Starting SingleCheck...");
		SinglePossibilitySolutionsRefresh();
		ArrayList<Integer> zeroArray = new ArrayList<Integer>();
		zeroArray.add(0);
		
		// überprüft die Zeilen
		for (int i = 0; i < currentPossibilities.size(); i++) {
			for (int j = 0; j < currentPossibilities.get(i).size(); j++) {
				for (int k = 0; k < currentPossibilities.get(i).get(j).size(); k++) {
					if (currentPossibilities.get(i).get(j).get(k) != 0) {
						Integer singleNumber = new Integer(currentPossibilities.get(i).get(j).get(k));
						ArrayList<ArrayList<Integer>> rowWithoutFirstElement = new ArrayList<ArrayList<Integer>>(currentPossibilities.get(i));
						rowWithoutFirstElement.set(j, zeroArray);
						
						boolean numberTwiceInRow = false;
						for (int l = 0; l < rowWithoutFirstElement.size(); l++) {
							if (rowWithoutFirstElement.get(l).contains(singleNumber)) {
								numberTwiceInRow = true;
							}
						}
						
						if (!numberTwiceInRow) {
							System.out.println("Row: " + i + " " + j + ": " + singleNumber);
							currentPossibilities.get(i).set(j, zeroArray);
							sudoku.get(i).set(j, singleNumber);
							shouldRepeat = true;
							SinglePossibilitySolutionsRefresh();
						}
					}
				}
			}
		}
		
		// überprüft die Spalten
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				for (int k = 0; k < getArrayOfColumnOfCurrentPosibilities(j).get(i).size(); k++) {
					if (getArrayOfColumnOfCurrentPosibilities(j).get(i).get(k) != 0) {
						Integer singleNumber = new Integer(getArrayOfColumnOfCurrentPosibilities(j).get(i).get(k));
						ArrayList<ArrayList<Integer>> columnWithoutFirstElement = new ArrayList<ArrayList<Integer>>(getArrayOfColumnOfCurrentPosibilities(j));
						columnWithoutFirstElement.set(i, zeroArray);
						
						boolean numberTwiceInColumn = false;
						for (int l = 0; l < columnWithoutFirstElement.size(); l++) {
							if (columnWithoutFirstElement.get(l).contains(singleNumber)) {
								numberTwiceInColumn = true;
							}
						}
						
						if (!numberTwiceInColumn) {
							System.out.println("Column: " + i + " " + j + ": " + singleNumber);
							currentPossibilities.get(i).set(j, zeroArray);
							sudoku.get(i).set(j, singleNumber);
							shouldRepeat = true;
							SinglePossibilitySolutionsRefresh();
						}
					}
				}
			}
		}
		
		// überprüft die Boxe
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				for (int k = 0; k < getArrayOfBoxOfCurrentPosibilities(i, j).size(); k++) {
					for (int l = 0; l < getArrayOfBoxOfCurrentPosibilities(i, j).get(k).get(2).size(); l++) {
						if (getArrayOfBoxOfCurrentPosibilities(i, j).get(k).get(2).get(l) != 0) {
							Integer singleNumber = new Integer(getArrayOfBoxOfCurrentPosibilities(i, j).get(k).get(2).get(l));
							ArrayList<ArrayList<ArrayList<Integer>>> boxWithoutFirstElement = new ArrayList<ArrayList<ArrayList<Integer>>>(getArrayOfBoxOfCurrentPosibilities(i, j));
							boxWithoutFirstElement.get(k).set(2, zeroArray);
							
							boolean numberTwiceInBox = false;
							for (int m = 0; m < boxWithoutFirstElement.size(); m++) {
								if (boxWithoutFirstElement.get(m).get(2).contains(singleNumber)) {
									numberTwiceInBox = true;
								}
							}
							
							if (!numberTwiceInBox) {
								System.out.println("Column: " + getArrayOfBoxOfCurrentPosibilities(i, j).get(k).get(0).get(0) + " " + getArrayOfBoxOfCurrentPosibilities(i, j).get(k).get(1).get(0) + ": " + singleNumber);
								currentPossibilities.get(getArrayOfBoxOfCurrentPosibilities(i, j).get(k).get(0).get(0)).set(getArrayOfBoxOfCurrentPosibilities(i, j).get(k).get(1).get(0), zeroArray);
								sudoku.get(getArrayOfBoxOfCurrentPosibilities(i, j).get(k).get(0).get(0)).set(getArrayOfBoxOfCurrentPosibilities(i, j).get(k).get(1).get(0), singleNumber);
								shouldRepeat = true;
								SinglePossibilitySolutionsRefresh();
							}
						}
					}
				}
			}
		}
		System.out.println("End");
		System.out.println("");
	}
	public static void pairCheck() {
		// kontrolliert ob eine Zeile, Spalte oder Box zwei Zellen, die die gleichen zwei mögliche Werte besitzt
		// Wenn ja werden diese zwei möglichen Werte von allen anderen Zellen die mit diese zwei Zellen verbunden sind.
		
		System.out.println("Starting pairCheck...");
		SinglePossibilitySolutionsRefresh();
		
		// überprüft Zeilen
		for (int i = 0; i < currentPossibilities.size(); i++) {
			for (int j = 0; j < currentPossibilities.get(i).size(); j++) {
				
				if (currentPossibilities.get(i).get(j).size() == 2) {
					ArrayList<Integer> firstPair = new ArrayList<Integer>(currentPossibilities.get(i).get(j));
					ArrayList<Integer> firstPairCoordinates = new ArrayList<Integer>();
					firstPairCoordinates.add(i);
					firstPairCoordinates.add(j);
					
					ArrayList<ArrayList<Integer>> currentPosibilitiesOfRowWithoutFirstPair = new ArrayList<ArrayList<Integer>>(currentPossibilities.get(i));
					currentPosibilitiesOfRowWithoutFirstPair.remove(firstPair);
					
					for (int i2 = 0; i2 < currentPosibilitiesOfRowWithoutFirstPair.size(); i2++) {
						if (currentPosibilitiesOfRowWithoutFirstPair.get(i2).equals(firstPair)) {
							
							ArrayList<Integer> secondPair = new ArrayList<Integer>(currentPosibilitiesOfRowWithoutFirstPair.get(i2));
							
							ArrayList<ArrayList<Integer>> currentPosibilitiesOfRowWithoutFirstAndSecondPair = currentPosibilitiesOfRowWithoutFirstPair;
							currentPosibilitiesOfRowWithoutFirstAndSecondPair.remove(secondPair);
							
							for (int k = 0; k < currentPosibilitiesOfRowWithoutFirstAndSecondPair.size(); k++) {
								ArrayList<Integer> currentPossibilitiesOfElementInRow = new ArrayList<Integer>(currentPosibilitiesOfRowWithoutFirstAndSecondPair.get(k));
								int indexOfCurrentElement = currentPossibilities.get(i).indexOf(currentPossibilitiesOfElementInRow);
								
								for (int k2 = 0; k2 < firstPair.size(); k2++) {
									if (currentPosibilitiesOfRowWithoutFirstAndSecondPair.get(k).contains(firstPair.get(k2)) && currentPosibilitiesOfRowWithoutFirstAndSecondPair.get(k).size() != 1) {
										System.out.println("removed: " + firstPair.get(k2) + " from the pos. of: " + i + " " + indexOfCurrentElement + " row");
										currentPossibilitiesOfElementInRow.remove(firstPair.get(k2));
										shouldRepeat = true;
									}
								}
								currentPossibilities.get(i).set(indexOfCurrentElement, currentPossibilitiesOfElementInRow);
							}
						}
					}
				}
			}
		}
		
		// überprüft die Spalten
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				
				if (getArrayOfColumnOfCurrentPosibilities(j).get(i).size() == 2) {
					ArrayList<Integer> firstPair = new ArrayList<Integer>(currentPossibilities.get(i).get(j));
					
					ArrayList<ArrayList<Integer>> currentPosibilitiesOfColumnWithoutFirstAndSecondPair = new ArrayList<ArrayList<Integer>>(getArrayOfColumnOfCurrentPosibilities(j));
					currentPosibilitiesOfColumnWithoutFirstAndSecondPair.remove(firstPair);
					
					for (int k = 0; k < currentPosibilitiesOfColumnWithoutFirstAndSecondPair.size(); k++) {
						
						if (currentPosibilitiesOfColumnWithoutFirstAndSecondPair.get(k).equals(firstPair)) {
							currentPosibilitiesOfColumnWithoutFirstAndSecondPair.remove(firstPair);	// remove the second pair which is the same as firstPair
							
							for (int l = 0; l < currentPosibilitiesOfColumnWithoutFirstAndSecondPair.size(); l++) {
								ArrayList<Integer> currentPosibilitiesOfElementInColumn = new ArrayList<Integer>(currentPosibilitiesOfColumnWithoutFirstAndSecondPair.get(l));
								int indexOfCurrentElement = getArrayOfColumnOfCurrentPosibilities(j).indexOf(currentPosibilitiesOfElementInColumn);
								
								for (int m = 0; m < firstPair.size(); m++) {
									if (currentPosibilitiesOfColumnWithoutFirstAndSecondPair.get(l).contains(firstPair.get(m)) && currentPosibilitiesOfColumnWithoutFirstAndSecondPair.get(l).size() != 1) {
										System.out.println("removed: " + firstPair.get(m) + " from the pos. of: " + indexOfCurrentElement + " " + j + " column");
										currentPosibilitiesOfElementInColumn.remove(firstPair.get(m));
										shouldRepeat = true;
									}
								}
								currentPossibilities.get(indexOfCurrentElement).set(j, currentPosibilitiesOfElementInColumn);
							}
						}
					}
				}
			}
		}

		// überprüft die Boxe
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				for (int k = 0; k < getArrayOfBoxOfCurrentPosibilities(i, j).size(); k++) {
					if (getArrayOfBoxOfCurrentPosibilities(i, j).get(k).get(2).size() == 2) {
						ArrayList<ArrayList<Integer>> firstPair = new ArrayList<ArrayList<Integer>>(getArrayOfBoxOfCurrentPosibilities(i, j).get(k));
						ArrayList<ArrayList<ArrayList<Integer>>> currentPosibilitiesOfBoxWithoutFirstAndSecondPair = new ArrayList<ArrayList<ArrayList<Integer>>>(getArrayOfBoxOfCurrentPosibilities(i, j));
						currentPosibilitiesOfBoxWithoutFirstAndSecondPair.remove(firstPair);
						
						for (int l = 0; l < currentPosibilitiesOfBoxWithoutFirstAndSecondPair.size(); l++) {
							if (currentPosibilitiesOfBoxWithoutFirstAndSecondPair.get(l).get(2).equals(firstPair.get(2))) {
								currentPosibilitiesOfBoxWithoutFirstAndSecondPair.remove(l);
								
								for (int m = 0; m < currentPosibilitiesOfBoxWithoutFirstAndSecondPair.size(); m++) {
									
									for (int n = 0; n < firstPair.get(2).size(); n++) {
										if (currentPosibilitiesOfBoxWithoutFirstAndSecondPair.get(m).get(2).contains(firstPair.get(2).get(n)) && currentPosibilitiesOfBoxWithoutFirstAndSecondPair.get(m).get(2).size() != 1) {
											System.out.println("removed: " + firstPair.get(2).get(n) + " from the pos. of: " + currentPosibilitiesOfBoxWithoutFirstAndSecondPair.get(m).get(0).get(0) + " " + currentPosibilitiesOfBoxWithoutFirstAndSecondPair.get(m).get(1).get(0) + " box");
											currentPossibilities.get(currentPosibilitiesOfBoxWithoutFirstAndSecondPair.get(m).get(0).get(0)).get(currentPosibilitiesOfBoxWithoutFirstAndSecondPair.get(m).get(1).get(0)).remove(firstPair.get(2).get(n));
											shouldRepeat = true;
										}
									}
								}
							}
						}
						
					}
				}
			}
		}
		System.out.println("End");
		System.out.println("");
		
	}
	public static void hiddenPairsRemoveBox() {
		// iteriert über alle Zellen in den Zeilen und Spalten. Wenn ein Zelle
		// nur in dieser Zeile (oder Spalte) in einer Box möglich ist, wird der Wert von allen anderen Zellen der Box entfernt
		
		System.out.println("Starting hiddenPairsRemoveBox...");
		SinglePossibilitySolutionsRefresh();
		
		// kontrolliert Zeilen
		for (int i = 0; i < currentPossibilities.size(); i++) {	// iterate über Zeilen
			for (int j = 0; j < currentPossibilities.get(i).size(); j++) {	// iteriert über einzelnen Zellen
				for (int k = 0; k < currentPossibilities.get(i).get(j).size(); k++) {	// iteriert über die einzelne möglichen Werte in der Zelle
					if (currentPossibilities.get(i).get(j).get(k) != 0) {
						
						Integer currentPossibilityOfElementToCheck = new Integer(currentPossibilities.get(i).get(j).get(k));
						ArrayList<ArrayList<Integer>> currentRowOfPosWithoutKandL = new ArrayList<ArrayList<Integer>>(currentPossibilities.get(i));
						currentRowOfPosWithoutKandL.set(j, new ArrayList<Integer>());
												
						boolean rowDoesContainNumber = false;
						for (int l = 0; l < currentRowOfPosWithoutKandL.size(); l++) {
							if (!getArrayOfBoxOfCurrentPosibilities(i, j).equals(getArrayOfBoxOfCurrentPosibilities(i, l)) && currentRowOfPosWithoutKandL.get(l).contains(currentPossibilityOfElementToCheck)) {
								rowDoesContainNumber = true;
							}
						}
						
						if (!rowDoesContainNumber) {
							for (int m = 0; m < getArrayOfBoxOfCurrentPosibilities(i, j).size(); m++) {
								ArrayList<ArrayList<Integer>> currentElement = new ArrayList<ArrayList<Integer>>(getArrayOfBoxOfCurrentPosibilities(i, j).get(m));
								if (getArrayOfBoxOfCurrentPosibilities(i, j).get(m).get(0).get(0) != i && currentElement.get(2).contains(currentPossibilityOfElementToCheck)) {
									System.out.println(i + " " + j + " row, : " + "removed: " + currentPossibilityOfElementToCheck + " from: " + currentElement.get(0).get(0) + " " + currentElement.get(1).get(0));
									currentElement.get(2).remove(currentPossibilityOfElementToCheck);
									currentPossibilities.get(currentElement.get(0).get(0)).set(currentElement.get(1).get(0), currentElement.get(2));
									shouldRepeat = true;
								}
							}
						}
					}
				}
			}
		}
		
		// kontrolliert Spalten
		
		for (int i = 0; i < 9; i++) {	// iteriert über Zeilen
			for (int j = 0; j < 9; j++) {	// iteriert über einzelnen Elemente
				for (int k = 0; k < getArrayOfColumnOfCurrentPosibilities(j).get(i).size(); k++) {
					if (getArrayOfColumnOfCurrentPosibilities(j).get(i).get(k) != 0) {
						Integer currentPossibilityOfElementToCheck = new Integer(getArrayOfColumnOfCurrentPosibilities(j).get(i).get(k));
						ArrayList<ArrayList<Integer>> columnWithoutCurrentElement = new ArrayList<ArrayList<Integer>>(getArrayOfColumnOfCurrentPosibilities(j));
						ArrayList<Integer> zeroArray = new ArrayList<Integer>();
						zeroArray.add(0);
						columnWithoutCurrentElement.set(i, zeroArray);
						
						boolean columnDoesContainNumber = false;
						for (int l = 0; l < columnWithoutCurrentElement.size(); l++) {
							if (!getArrayOfBoxOfCurrentPosibilities(i, j).equals(getArrayOfBoxOfCurrentPosibilities(l, j)) && columnWithoutCurrentElement.get(l).contains(currentPossibilityOfElementToCheck)) {
								columnDoesContainNumber = true;
							}
						}
						
						if (!columnDoesContainNumber) {
							for (int m = 0; m < getArrayOfBoxOfCurrentPosibilities(i, j).size(); m++) {
								ArrayList<ArrayList<Integer>> currentElement = new ArrayList<ArrayList<Integer>>(getArrayOfBoxOfCurrentPosibilities(i, j).get(m));
								if (getArrayOfBoxOfCurrentPosibilities(i, j).get(m).get(1).get(0) != j && currentElement.get(2).contains(currentPossibilityOfElementToCheck)) {
									System.out.println(i + " " + j + " column, : " + " removed: " + currentPossibilityOfElementToCheck + " from: " + currentElement.get(0).get(0) + " " + currentElement.get(1).get(0));
									currentElement.get(2).remove(currentPossibilityOfElementToCheck);
									currentPossibilities.get(currentElement.get(0).get(0)).set(currentElement.get(1).get(0), currentElement.get(2));
									shouldRepeat = true;
								}
							}
						}
					}
				}
			}
		}
		
		
		System.out.println("End");
		System.out.println("");
		
		
	}
	public static void hiddenPairsRemoveColumnRow() {
		// implementiert der zweiten Teil der hidden Pairs Methode indem es versucht Werte von der Reihen und Spalten zu entfernen
		
		System.out.println("Starting hiddenPairsRemoveColumnRow...");
		SinglePossibilitySolutionsRefresh();
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < getArrayOfBoxOfCurrentPosibilities(i * 3, j * 3).size(); k++) {
					for (int l = 0; l < getArrayOfBoxOfCurrentPosibilities(i * 3, j * 3).get(k).get(2).size(); l++) {
						if (getArrayOfBoxOfCurrentPosibilities(i * 3, j * 3).get(k).get(2).get(l) != 0) {
							ArrayList<ArrayList<Integer>> currentKElement = new ArrayList<ArrayList<Integer>>(getArrayOfBoxOfCurrentPosibilities(i * 3, j * 3).get(k));
							Integer numberToCheck = new Integer(getArrayOfBoxOfCurrentPosibilities(i * 3, j * 3).get(k).get(2).get(l));
							ArrayList<ArrayList<ArrayList<Integer>>> arrayOfBoxWithoutCurrentElement = new ArrayList<ArrayList<ArrayList<Integer>>>(getArrayOfBoxOfCurrentPosibilities(i * 3, j * 3));
							
							for (int m = 0; m < arrayOfBoxWithoutCurrentElement.get(k).size(); m++) {
								arrayOfBoxWithoutCurrentElement.get(k).set(m, new ArrayList<Integer>());
								arrayOfBoxWithoutCurrentElement.get(k).get(m).add(new Integer(0));
							}
							for (int m = 0; m < arrayOfBoxWithoutCurrentElement.size(); m++) {
								ArrayList<ArrayList<ArrayList<Integer>>> arrayOfBoxWithoutBothElement = new ArrayList<ArrayList<ArrayList<Integer>>>(arrayOfBoxWithoutCurrentElement);
								ArrayList<ArrayList<Integer>> currentMElement = new ArrayList<ArrayList<Integer>>(arrayOfBoxWithoutBothElement.get(m));
								
								boolean isInSameRow = false;
								boolean isInSameColumn = false;
								if (arrayOfBoxWithoutBothElement.get(m).get(2).contains(numberToCheck)) {
									if (currentKElement.get(0).get(0) == currentMElement.get(0).get(0)) {
										isInSameRow = true;
									} else if (currentKElement.get(1).get(0) == currentMElement.get(1).get(0)) {							
										isInSameColumn = true;
									}
								}
								
								boolean anotherNumberInSquare = false;
								if (isInSameRow || isInSameColumn) {
									for (int n = 0; n < arrayOfBoxWithoutBothElement.size(); n++) {
										if (arrayOfBoxWithoutBothElement.get(n).get(2).contains(numberToCheck)
												&& ((isInSameRow && !arrayOfBoxWithoutBothElement.get(n).get(0).equals(currentKElement.get(0))
												|| (isInSameColumn && !arrayOfBoxWithoutBothElement.get(n).get(1).equals(currentKElement.get(1)))))) {
											anotherNumberInSquare = true;
										}
									}
								}
								
								if (!anotherNumberInSquare && isInSameRow) {
									for (int n = 0; n < currentPossibilities.get(currentKElement.get(0).get(0)).size(); n++) {
										if (currentPossibilities.get(currentKElement.get(0).get(0)).get(n).contains(numberToCheck)
												&& !getArrayOfBoxOfCurrentPosibilities(currentKElement.get(0).get(0), currentKElement.get(1).get(0)).equals(getArrayOfBoxOfCurrentPosibilities(currentKElement.get(0).get(0), n))) {
											System.out.println(currentKElement.get(0).get(0) + " " + currentKElement.get(1).get(0) + " removed: " + numberToCheck + " from " + currentKElement.get(0).get(0) + " " + n + " row");
											currentPossibilities.get(currentKElement.get(0).get(0)).get(n).remove(numberToCheck);
											shouldRepeat = true;
										}
									}
								}
								if (!anotherNumberInSquare && isInSameColumn) {
									for (int n = 0; n < getArrayOfColumnOfCurrentPosibilities(currentKElement.get(1).get(0)).size(); n++) {
										if (getArrayOfColumnOfCurrentPosibilities(currentKElement.get(1).get(0)).get(n).contains(numberToCheck)
												&& !getArrayOfBoxOfCurrentPosibilities(currentKElement.get(0).get(0), currentKElement.get(1).get(0)).equals(getArrayOfBoxOfCurrentPosibilities(n, currentKElement.get(1).get(0)))) {
											System.out.println(currentKElement.get(0).get(0) + " " + currentKElement.get(1).get(0) + " removed: " + numberToCheck + " from " + n + " " + currentKElement.get(1).get(0) + " column");
											currentPossibilities.get(n).get(currentKElement.get(1).get(0)).remove(numberToCheck);
											shouldRepeat = true;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		System.out.println("End");
		System.out.println("");
	}
	public static void XWing() {
		// implementiert die XY-Wing methode 
		
		System.out.println("Starting X-Wing...");
		SinglePossibilitySolutionsRefresh();
		
		for (int i = 0; i < currentPossibilities.size(); i++) {
			for (int j = 0; j < currentPossibilities.get(i).size(); j++) {
				for (int k = 0; k < currentPossibilities.get(i).get(j).size(); k++) {
					if (currentPossibilities.get(i).get(j).get(k) != 0) {	// sucht erste Zelle
						Integer consideredNumber = new Integer(currentPossibilities.get(i).get(j).get(k));
						
						// überprüft die Reihen
						ArrayList<ArrayList<Integer>> rowWithoutFirstElement = new ArrayList<ArrayList<Integer>>(currentPossibilities.get(i));
						ArrayList<Integer> zeroArray = new ArrayList<Integer>();
						zeroArray.add(new Integer(0));
						rowWithoutFirstElement.set(j, zeroArray);
						for (int l = 0; l < rowWithoutFirstElement.size(); l++) {
							// sucht zweite Zelle in der gleichen Zeile
							
							if (rowWithoutFirstElement.get(l).contains(consideredNumber)) {
								ArrayList<ArrayList<Integer>> rowWithoutFirstAndSecondElement = new ArrayList<ArrayList<Integer>>(rowWithoutFirstElement);
								rowWithoutFirstAndSecondElement.set(l, zeroArray);
								boolean moreThenTwoInRow = false;
								
								for (int m = 0; m < rowWithoutFirstAndSecondElement.size(); m++) {
									if (rowWithoutFirstAndSecondElement.get(m).contains(consideredNumber)) {
										moreThenTwoInRow = true;
									}
								}
								
								if (!moreThenTwoInRow) {
									// kontrolliert ob diese zwei Zellen die einzigen Zellen in der Zeile sind die diesen möglichen Wert besitzen
									
									ArrayList<ArrayList<Integer>> columnWithoutFirstElement = new ArrayList<ArrayList<Integer>>(getArrayOfColumnOfCurrentPosibilities(j));
									columnWithoutFirstElement.set(i, zeroArray);
									
									for (int m = 0; m < columnWithoutFirstElement.size(); m++) {	// sucht dritte Zelle
										if (columnWithoutFirstElement.get(m).contains(consideredNumber)) {
											ArrayList<ArrayList<Integer>> secondRowWithoutFirstElement = new ArrayList<ArrayList<Integer>>(currentPossibilities.get(m));
											secondRowWithoutFirstElement.set(j, zeroArray);
											
											if (secondRowWithoutFirstElement.get(l).contains(consideredNumber)) {
												// kontrolliert ob vierte Zelle den möglichen Wert besitzt
												
												ArrayList<ArrayList<Integer>> secondRowWithoutFirstAndSecondElement = new ArrayList<ArrayList<Integer>>(secondRowWithoutFirstElement);
												secondRowWithoutFirstAndSecondElement.set(l, zeroArray);
												
												boolean secondRowContainsMoreThenTwo = false;
												for (int n = 0; n < secondRowWithoutFirstAndSecondElement.size(); n++) {
													if (secondRowWithoutFirstAndSecondElement.get(n).contains(consideredNumber)) {
														secondRowContainsMoreThenTwo = true;
													}
												}
												
												if (!secondRowContainsMoreThenTwo) {
													// Löschung aus Spalten beginnen
													
													for (int n = 0; n < getArrayOfColumnOfCurrentPosibilities(j).size(); n++) {
														if (getArrayOfColumnOfCurrentPosibilities(j).get(n).contains(consideredNumber) && n != i && n != m) {
															System.out.println("removed: " + consideredNumber + " from: " + n + " " + j);
															currentPossibilities.get(n).get(j).remove(consideredNumber);
															shouldRepeat = true;
														}
													}
													for (int n = 0; n < getArrayOfColumnOfCurrentPosibilities(l).size(); n++) {
														if (getArrayOfColumnOfCurrentPosibilities(l).get(n).contains(consideredNumber) && n != i && n != m) {
															System.out.println("removed: " + consideredNumber + " from: " + n + " " + l);
															currentPossibilities.get(n).get(l).remove(consideredNumber);
															shouldRepeat = true;
														}
													}
												}
											}
										}
									}
								}
							}
						}
						
						// überprüft die Spalten
						ArrayList<ArrayList<Integer>> columnWithoutFirstElement = new ArrayList<ArrayList<Integer>>();
						columnWithoutFirstElement = getArrayOfColumnOfCurrentPosibilities(j);
						columnWithoutFirstElement.set(i, zeroArray);
						
						for (int m = 0; m < columnWithoutFirstElement.size(); m++) {
							// sucht eine zweite Zelle die sich in der gleichen Spalten befindet und der gleichen Wert enthält
							
							if (columnWithoutFirstElement.get(m).contains(consideredNumber)) {
								ArrayList<ArrayList<Integer>> columnWithoutFirstAndSecondElement = new ArrayList<ArrayList<Integer>>(columnWithoutFirstElement);
								columnWithoutFirstAndSecondElement.set(m, zeroArray);
								boolean moreThenTwoInColumn = false;
								
								for (int n = 0; n < columnWithoutFirstAndSecondElement.size(); n++) {
									if (columnWithoutFirstAndSecondElement.get(n).contains(consideredNumber)) {
										moreThenTwoInColumn = true;
									}
								}
								
								if (!moreThenTwoInColumn) {
									ArrayList<ArrayList<Integer>> rowWithoutFirstElementInColumn = new ArrayList<ArrayList<Integer>>(currentPossibilities.get(i));
									rowWithoutFirstElementInColumn.set(j, zeroArray);
									
									for (int n = 0; n < rowWithoutFirstElementInColumn.size(); n++) {	// sucht die dritte Zelle
										if (rowWithoutFirstElementInColumn.get(n).contains(consideredNumber)) {
											ArrayList<ArrayList<Integer>> secondColumnWithoutFirstElement = new ArrayList<ArrayList<Integer>>(getArrayOfColumnOfCurrentPosibilities(n));
											secondColumnWithoutFirstElement.set(i, zeroArray);
											
											if (secondColumnWithoutFirstElement.get(m).contains(consideredNumber)) {
												// falls die vierte Zelle diesen Wert enthält
												
												ArrayList<ArrayList<Integer>> secondColumnWithoutFirstAndSecondElement = new ArrayList<ArrayList<Integer>>(secondColumnWithoutFirstElement);
												secondColumnWithoutFirstAndSecondElement.set(m, zeroArray);
												boolean secondColumnMoreThenTwo = false;
												
												for (int o = 0; o < secondColumnWithoutFirstAndSecondElement.size(); o++) {
													// überprüft ob diese zwei Zellen die einzigen sind die diesen Wert in der Spalte enthalten
													
													if (secondColumnWithoutFirstAndSecondElement.get(o).contains(consideredNumber)) {
														secondColumnMoreThenTwo = true;
													}
												}
												
												if (!secondColumnMoreThenTwo) {
													// Löschung aus Zeilen beginnen
													
													for (int l = 0; l < currentPossibilities.get(i).size(); l++) {
														if (currentPossibilities.get(i).get(l).contains(consideredNumber) && l != j && l != n) {
															System.out.println("removed: " + consideredNumber + " from: " + i + " " + l + " Row: " + i + " " + m + " Column: " + j + " " + n);
															currentPossibilities.get(i).get(l).remove(consideredNumber);
															shouldRepeat = true;
														}
													}
													for (int l = 0; l < currentPossibilities.get(m).size(); l++) {
														if (currentPossibilities.get(m).get(l).contains(consideredNumber) && l != j && l != n) {
															System.out.println("removed: " + consideredNumber + " from: " + m + " " + l + " Row: " + i + " " + m + " Column: " + j + " " + n);
															currentPossibilities.get(m).get(l).remove(consideredNumber);
															shouldRepeat = true;
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		System.out.println("End");
		System.out.println("");
	}
	public static void XYWing() {
		// implementiert die XY-Wing Methode
		
		System.out.println("Starting XY-Wing...");
		
		SinglePossibilitySolutionsRefresh();
		ArrayList<Integer> zeroArray = new ArrayList<Integer>();
		zeroArray.add(new Integer(0));
		
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (currentPossibilities.get(i).get(j).size() == 2) {	// sucht die X-Zelle
					ArrayList<ArrayList<ArrayList<Integer>>> possibilitiesWithoutCenterPair = new ArrayList<ArrayList<ArrayList<Integer>>>(10);
					
					// erstellt eine Kopie von den möglichen Werte des Sudokus
					for (int k = 0; k < currentPossibilities.size(); k++) {
						possibilitiesWithoutCenterPair.add(new ArrayList<ArrayList<Integer>>());
						
						for (int l = 0; l < currentPossibilities.get(k).size(); l++) {
							possibilitiesWithoutCenterPair.get(k).add(new ArrayList<Integer>());
							
							for (int m = 0; m < currentPossibilities.get(k).get(l).size(); m++) {
								possibilitiesWithoutCenterPair.get(k).get(l).add(currentPossibilities.get(k).get(l).get(m));
							}
						}
					}
					ArrayList<Integer> centerPair = new ArrayList<Integer>(currentPossibilities.get(i).get(j));
					possibilitiesWithoutCenterPair.get(i).set(j, zeroArray);	// entfernt die gewählte Zelle von der Kopie
					
					for (int k = 0; k < possibilitiesWithoutCenterPair.size(); k++) {
						for (int l = 0; l < possibilitiesWithoutCenterPair.get(k).size(); l++) {
							for (int m = 0; m < centerPair.size(); m++) {
								if (possibilitiesWithoutCenterPair.get(k).get(l).size() == 2
										&& possibilitiesWithoutCenterPair.get(k).get(l).contains(centerPair.get(m))
										&& !centerPair.equals(possibilitiesWithoutCenterPair.get(k).get(l))
										&& (getArrayOfBoxOfCurrentPosibilities(i, j).equals(getArrayOfBoxOfCurrentPosibilities(k, l))
												|| i == k || j == l)) {
									// sucht einer der Y-Zellen
									ArrayList<Integer> firstPair = new ArrayList<Integer>(possibilitiesWithoutCenterPair.get(k).get(l));
									ArrayList<ArrayList<ArrayList<Integer>>> possibilitiesWithoutCenterAndFirstPair = new ArrayList<ArrayList<ArrayList<Integer>>>(possibilitiesWithoutCenterPair);
									possibilitiesWithoutCenterAndFirstPair.get(k).set(l, zeroArray);
									
									// rechnet wellches Paar von Werten noch gefunden werden kann sodass es um ein XY-Wing handelt
									ArrayList<ArrayList<Integer>> listToCompare = new ArrayList<ArrayList<Integer>>();
									listToCompare.add(centerPair);
									listToCompare.add(firstPair);
									
									Integer firstNumber = new Integer(getSimilarietiesInArray(listToCompare).get(0));
									ArrayList<Integer> disposableArray = new ArrayList<Integer>(centerPair);
									disposableArray.remove(firstNumber);
									Integer secondNumber = new Integer(disposableArray.get(0));
									
									disposableArray = new ArrayList<Integer>(firstPair);
									disposableArray.remove(firstNumber);
									Integer numberInCommonWithFirstAndSecondPair = new Integer(disposableArray.get(0));
									
									for (int n = 0; n < possibilitiesWithoutCenterAndFirstPair.size(); n++) {
										for (int o = 0; o < possibilitiesWithoutCenterAndFirstPair.get(n).size(); o++) {
											if (possibilitiesWithoutCenterAndFirstPair.get(n).get(o).size() == 2
													&& possibilitiesWithoutCenterAndFirstPair.get(n).get(o).contains(secondNumber)
													&& possibilitiesWithoutCenterAndFirstPair.get(n).get(o).contains(numberInCommonWithFirstAndSecondPair)
													&& (getArrayOfBoxOfCurrentPosibilities(i, j).equals(getArrayOfBoxOfCurrentPosibilities(n, o))
															|| i == n || j == o)) {
												// sucht die letzte Y-Zelle
												ArrayList<ArrayList<ArrayList<Integer>>> possibilitiesWithoutCenterFirstSecondPair = new ArrayList<ArrayList<ArrayList<Integer>>>(possibilitiesWithoutCenterAndFirstPair);
												possibilitiesWithoutCenterFirstSecondPair.get(n).set(o, zeroArray);
												
												for (int p = 0; p < possibilitiesWithoutCenterFirstSecondPair.size(); p++) {
													for (int q = 0; q < possibilitiesWithoutCenterFirstSecondPair.get(p).size(); q++) {
														if (possibilitiesWithoutCenterFirstSecondPair.get(p).get(q).contains(numberInCommonWithFirstAndSecondPair)
																&& ((p == k && q == o) || (p == n && q ==l)
																		|| (getArrayOfBoxOfCurrentPosibilities(p, q).equals(getArrayOfBoxOfCurrentPosibilities(k, l)) && (p == n || q == o))
																		|| (getArrayOfBoxOfCurrentPosibilities(p, q).equals(getArrayOfBoxOfCurrentPosibilities(n, o)) && (p == k || q == l))
																		|| (getArrayOfBoxOfCurrentPosibilities(p, q).equals(getArrayOfBoxOfCurrentPosibilities(k, l))
																				&& getArrayOfBoxOfCurrentPosibilities(p, q).equals(getArrayOfBoxOfCurrentPosibilities(n, o)))
																		|| (p == k && p == n) || (q == l && q == o))) {
															System.out.println("centerPair: " + i + " " + j + " firstPair: " + k + " " + l + " secondPair: " + n + " " + o);
															System.out.println("removed: " + numberInCommonWithFirstAndSecondPair + " from: " + p + " " + q);
															
															// entfernt der Wert die die zwei Y-Zellen gemeinsam haben
															currentPossibilities.get(p).get(q).remove(numberInCommonWithFirstAndSecondPair);
															shouldRepeat = true;
															
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		System.out.println("End");
		System.out.println("");
	}
	public static void XYZWing() {
		// implementiert die XYZ-Wing Methode
		
		System.out.println("Starting XYZ-Wing");
		SinglePossibilitySolutionsRefresh();
		
		ArrayList<Integer> zeroArray = new ArrayList<Integer>();
		zeroArray.add(new Integer(0));
		
		for (int i = 0; i < currentPossibilities.size(); i++) {
			for (int j = 0; j < currentPossibilities.get(i).size(); j++) {
				if (currentPossibilities.get(i).get(j).size() == 3) {	// sucht XYZ-Zelle
					ArrayList<Integer> centerTrio = new ArrayList<Integer>(currentPossibilities.get(i).get(j));
					ArrayList<Integer> trioCoordinates = new ArrayList<Integer>();
					trioCoordinates.add(i);
					trioCoordinates.add(j);
					ArrayList<ArrayList<ArrayList<Integer>>> possibilitiesWithoutCenterTrio = new ArrayList<ArrayList<ArrayList<Integer>>>();
					
					// erstellt eine Kopie der möglichen Werte des Sudokus
					for (int k = 0; k < currentPossibilities.size(); k++) {
						possibilitiesWithoutCenterTrio.add(new ArrayList<ArrayList<Integer>>());
						
						for (int l = 0; l < currentPossibilities.get(k).size(); l++) {
							possibilitiesWithoutCenterTrio.get(k).add(new ArrayList<Integer>());
							
							for (int m = 0; m < currentPossibilities.get(k).get(l).size(); m++) {
								possibilitiesWithoutCenterTrio.get(k).get(l).add(currentPossibilities.get(k).get(l).get(m));
							}
						}
					}
					possibilitiesWithoutCenterTrio.get(i).set(j, zeroArray);	// entfern die XYZ-Zelle von dieser Kopie
					for (int k = 0; k < possibilitiesWithoutCenterTrio.size(); k++) {
						for (int l = 0; l < possibilitiesWithoutCenterTrio.get(k).size(); l++) {
							ArrayList<ArrayList<Integer>> firstPairMaker = new ArrayList<ArrayList<Integer>>();
							firstPairMaker.add(centerTrio);
							firstPairMaker.add(possibilitiesWithoutCenterTrio.get(k).get(l));
							
							ArrayList<Integer> firstPairCoordinates = new ArrayList<Integer>();
							firstPairCoordinates.add(k);
							firstPairCoordinates.add(l);
							
							ArrayList<ArrayList<Integer>> firstPairChecker = new ArrayList<ArrayList<Integer>>();
							firstPairChecker.add(trioCoordinates);
							firstPairChecker.add(firstPairCoordinates);
							
							for (int m = 0; m < centerTrio.size(); m++) {	// sucht die XZ-Zelle
								if (getSimilarietiesInArray(firstPairMaker).size() == 2 && possibilitiesWithoutCenterTrio.get(k).get(l).size() == 2 && isBuddy(firstPairChecker)) {
									ArrayList<Integer> firstPair = new ArrayList<Integer>(getSimilarietiesInArray(firstPairMaker));
									ArrayList<ArrayList<ArrayList<Integer>>> possibilitiesWithoutCenterTrioAndFirstPair = new ArrayList<ArrayList<ArrayList<Integer>>>(possibilitiesWithoutCenterTrio);
									possibilitiesWithoutCenterTrioAndFirstPair.get(k).set(l, zeroArray);
									
									for (int n = 0; n < possibilitiesWithoutCenterTrioAndFirstPair.size(); n++) {
										for (int o = 0; o < possibilitiesWithoutCenterTrioAndFirstPair.get(n).size(); o++) {
											ArrayList<ArrayList<Integer>> secondpairMaker = new ArrayList<ArrayList<Integer>>();
											secondpairMaker.add(centerTrio);
											secondpairMaker.add(possibilitiesWithoutCenterTrioAndFirstPair.get(n).get(o));
											
											ArrayList<Integer> secondPairCoordinates = new ArrayList<Integer>();
											secondPairCoordinates.add(n);
											secondPairCoordinates.add(o);
											
											ArrayList<ArrayList<Integer>> secondPairChecker = new ArrayList<ArrayList<Integer>>();
											secondPairChecker.add(trioCoordinates);
											secondPairChecker.add(secondPairCoordinates);
											
											// Kontrolliert ob die letzte die YZ-Zelle ist
											if (getSimilarietiesInArray(secondpairMaker).size() == 2 && !possibilitiesWithoutCenterTrioAndFirstPair.get(n).get(o).equals(firstPair)
													&& possibilitiesWithoutCenterTrioAndFirstPair.get(n).get(o).size() == 2 && isBuddy(secondPairChecker)) {
												ArrayList<Integer> secondPair = new ArrayList<Integer>(possibilitiesWithoutCenterTrioAndFirstPair.get(n).get(o));
												
												// rechnet den Wert die alle drei Zellen gemeinsam haben
												ArrayList<ArrayList<Integer>> numberInCommonFinder = new ArrayList<ArrayList<Integer>>();
												numberInCommonFinder.add(firstPair);
												numberInCommonFinder.add(secondPair);
												numberInCommonFinder.add(centerTrio);
												Integer numberInCommon = new Integer(getSimilarietiesInArray(numberInCommonFinder).get(0));
												
												ArrayList<ArrayList<ArrayList<Integer>>> possibilitiesWithoutCenterTrioFirstAndSecondPair = new ArrayList<ArrayList<ArrayList<Integer>>>(possibilitiesWithoutCenterTrioAndFirstPair);
												possibilitiesWithoutCenterTrioFirstAndSecondPair.get(n).set(o, zeroArray);
												
												for (int p = 0; p < possibilitiesWithoutCenterTrioFirstAndSecondPair.size(); p++) {
													for (int p2 = 0; p2 < possibilitiesWithoutCenterTrioFirstAndSecondPair.get(p).size(); p2++) {
														// sucht eine Zelle die mit die XZ-, YZ- und XYZ-Zelle verbunden ist
														
														ArrayList<Integer> currentElementCoordinates = new ArrayList<Integer>();
														currentElementCoordinates.add(p);
														currentElementCoordinates.add(p2);
														
														ArrayList<ArrayList<Integer>> trioBuddy = new ArrayList<ArrayList<Integer>>();
														trioBuddy.add(trioCoordinates);
														trioBuddy.add(currentElementCoordinates);
														
														ArrayList<ArrayList<Integer>> firstBuddy = new ArrayList<ArrayList<Integer>>();
														firstBuddy.add(firstPairCoordinates);
														firstBuddy.add(currentElementCoordinates);
														
														ArrayList<ArrayList<Integer>> secondBuddy = new ArrayList<ArrayList<Integer>>();
														secondBuddy.add(secondPairCoordinates);
														secondBuddy.add(currentElementCoordinates);
														
														if (possibilitiesWithoutCenterTrioFirstAndSecondPair.get(p).get(p2).contains(numberInCommon)
																&& isBuddy(trioBuddy) && isBuddy(firstBuddy) && isBuddy(secondBuddy)) {
															
															// entfernt den möglichen Wert
															System.out.println("centerTrio: " + i + " " + j + " firstPair: " + k + " " + l + " secondPair: " + n + " " + o );
															System.out.println("removed: " + numberInCommon + " from: " + p + " " + p2);
															currentPossibilities.get(p).get(p2).remove(numberInCommon);
															shouldRepeat = true;
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		System.out.println("End");
		System.out.println("");
	}
	public static ArrayList<ArrayList<Integer>> getCoordinatesOfEmptyElements() {
		// gibt die Koordinaten aller werte die mit 0 bezeichnet sind im Sudoku zurück
		
		ArrayList<ArrayList<Integer>> coordinatesOfEmptyElements = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				
				if (sudoku.get(i).get(j) == 0) {
					ArrayList<Integer> xyCoordinate = new ArrayList<Integer>();
					xyCoordinate.add(i);
					xyCoordinate.add(j);
					coordinatesOfEmptyElements.add(xyCoordinate);
				}
			}
		}
		
		return coordinatesOfEmptyElements;
	}
	public static ArrayList<ArrayList<Integer>> getCoordinatesOfEmptyElementsInBox(int row, int column) {
		// nimmt die Koordinaten einer Box (1 bis 3) und gibt die Koordinaten der Zellen die im Sudoku mit 0 bezeichnet sind (leere Zellen)
		
		ArrayList<ArrayList<Integer>> missingNumbersInBox = new ArrayList<ArrayList<Integer>>(9);
		column = 3 * column;
		row = 3 * row;
		int initialColumn = column;
		
		for (int i = 0; i < 3; i++) {	// iteriert über die Zeilen der Box
			ArrayList<Integer> innerRow = sudoku.get(row);
			column = initialColumn;
			
			for (int j = 0; j < 3; j++) {	// iteriert über die einzelne Zellen in die Box
				Integer numberToCheck = innerRow.get(column);
				if (numberToCheck == 0) {
					ArrayList<Integer> CoordinateOfMissingNumber = new ArrayList<Integer>(2);
					CoordinateOfMissingNumber.add(row);
					CoordinateOfMissingNumber.add(column);
					missingNumbersInBox.add(CoordinateOfMissingNumber);
				}
				column++;
			}
			row++;
		}
		
		for (int i = 0; i < missingNumbersInBox.size(); i++) {
			if (missingNumbersInBox.get(i) == null) {
				missingNumbersInBox.remove(i);
			}
		}
		
		return missingNumbersInBox;
	}
	public static ArrayList<Integer> getArrayOfColumnFromCoordinate(int column) {
		// nimmt eine Spaltennummer als Parameter und gibt eine ArrayList von allen Werten in dieser Spalte im Sudoku zurück
		
		ArrayList<Integer> arrayOfColumn = new ArrayList<Integer>();
		for (int i = 0; i < 9; i++) {
			arrayOfColumn.add(sudoku.get(i).get(column));
		}
		return arrayOfColumn;
	}
	public static ArrayList<ArrayList<Integer>> getArrayOfColumnOfCurrentPosibilities(int column) {
		// nimmt eine Spaltennummer als Parameter und gibt ein ArrayList von allen möglichen Werte in dieser Spalte zurück
		
		ArrayList<ArrayList<Integer>> arrayOfColumn = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < 9; i++) {
			arrayOfColumn.add(currentPossibilities.get(i).get(column));
		}
		return arrayOfColumn;
	}
	public static ArrayList<Integer> getArrayOfBox(int row, int column) {
		// nimmt die Koordinaten einer Box (1 bis 3) als Parameter und gibt ein ArrayList von allen Werten dieser Box im Sudoku
		
		ArrayList<Integer> arrayOfBox = new ArrayList<Integer>();
		int rowCoordinate = 0;
		int columnCoordinate = 0;
		
		if (column < 3) {
			columnCoordinate = 0;
		} else if (column < 6) {
			columnCoordinate = 1;
		} else {
			columnCoordinate = 2;
		}
		
		if (row < 3) {
			rowCoordinate = 0;
		} else if (row < 6) {
			rowCoordinate = 1;
		} else {
			rowCoordinate = 2;
		}
		
		int rowToStartAt = 3 * rowCoordinate;
		int columnToStartAt = 3 * columnCoordinate;
		int initialColumn = columnToStartAt;
		
		for (int i = 0; i < 3; i++) {
			
			columnToStartAt = initialColumn;
			for (int j = 0; j < 3; j++) {
				arrayOfBox.add(sudoku.get(rowToStartAt).get(columnToStartAt));
				columnToStartAt++;
			}
			rowToStartAt++;
		}
		return arrayOfBox;
	}
	public static ArrayList<ArrayList<ArrayList<Integer>>> getArrayOfBoxOfCurrentPosibilities(int row, int column) {
		// nimmt die Koordinaten einer Box als Parameter (1 bis 3) und gibt die Koordinaten aller Zellen die sich in dieser Box befindet zurück
		
		ArrayList<ArrayList<ArrayList<Integer>>> arrayOfBoxOfPosWithCoord = new ArrayList<ArrayList<ArrayList<Integer>>>();
		int rowToStartAt = 0;
		int columnToStartAt = 0;
		
		if (column < 3) {
			columnToStartAt = 0;
		} else if (column < 6) {
			columnToStartAt = 3;
		} else {
			columnToStartAt = 6;
		}
		
		if (row < 3) {
			rowToStartAt = 0;
		} else if (row < 6) {
			rowToStartAt = 3;
		} else {
			rowToStartAt = 6;
		}
		
		int initialColumn = columnToStartAt;
		
		for (int i = 0; i < 3; i++) {
			columnToStartAt = initialColumn;
			for (int j = 0; j < 3; j++) {
				ArrayList<ArrayList<Integer>> createdElementWithCord = new ArrayList<ArrayList<Integer>>();
				ArrayList<Integer> rowCord = new ArrayList<Integer>();
				rowCord.add(rowToStartAt);
				createdElementWithCord.add(rowCord);
				
				ArrayList<Integer> colCord = new ArrayList<Integer>();
				colCord.add(columnToStartAt);
				createdElementWithCord.add(colCord);
				
				createdElementWithCord.add(currentPossibilities.get(rowToStartAt).get(columnToStartAt));
				arrayOfBoxOfPosWithCoord.add(createdElementWithCord);
				
				columnToStartAt++;
			}
			rowToStartAt++;
		}
		
		return arrayOfBoxOfPosWithCoord;
	}
	public static ArrayList<Integer> getSimilarietiesInArray(ArrayList<ArrayList<Integer>> similarietiesToFind) {
		// nimmt mehrere ArrayList als Parameter und gibt ein ArrayList zurück mit den Werten die in allen ArrayList vorkommen
		
		ArrayList<Integer> similarieties = new ArrayList<Integer>();
		for (int i = 0; i < 9; i++) {
			boolean shouldAdd = true;
			for (int j = 0; j < similarietiesToFind.size(); j++) {
				if (!similarietiesToFind.get(j).contains(i+1)) {
					shouldAdd = false;
				}
			}
			if (shouldAdd) {
				similarieties.add(i+1);
			}
		}
		return similarieties;
	}
	public static ArrayList<Integer> getPossibilities(int row, int column) {
		// nimmt die Koordinaten einer Zelle und gibt alle aktuellen möglichen Werte zurück, indem es die möglichen Werte der Zeile Spalte und Box anschaut
		
		ArrayList<Integer> possibleNumberInRow = new ArrayList<Integer>();
		ArrayList<Integer> possibleNumberInColumn = new ArrayList<Integer>();
		ArrayList<Integer> possibleNumberInSquare = new ArrayList<Integer>();
		
		if (sudoku.get(row).get(column) == 0) {
			for (int j = 0; j < 9; j++) {	// iteriert über die 9 möglichen Werte in der Zeile
				if (!sudoku.get(row).contains(j+1)) {
					possibleNumberInRow.add(j+1);
				}
				
				if (!getArrayOfColumnFromCoordinate(column).contains(j+1)) {
					possibleNumberInColumn.add(j+1);
				}
				
				if (!getArrayOfBox(row, column).contains(j+1)) {
					possibleNumberInSquare.add(j+1);
				}
			}
			ArrayList<ArrayList<Integer>> possibilities = new ArrayList<ArrayList<Integer>>();
			possibilities.add(possibleNumberInRow);
			possibilities.add(possibleNumberInColumn);
			possibilities.add(possibleNumberInSquare);
			
			return getSimilarietiesInArray(possibilities);
		} else {
			ArrayList<Integer> arrayOfZeroToReturn = new ArrayList<Integer>();
			arrayOfZeroToReturn.add(Integer.valueOf(0));
			return arrayOfZeroToReturn;
		}
	}
	public static ArrayList<Integer> getCombinationOfArrays(ArrayList<ArrayList<Integer>> arraysToCombined) {
		// gibt eine Kombination von allen ArrayLists vom Parameter zurück, sodass die Rückgabe ein ArrayList von allen Werten, welche in diesen Parameter vorkommen, ist
		// Diese Werte kommen in der Rückgabe nur einmal vor
		
		ArrayList<Integer> combination = new ArrayList<Integer>();
		
		for (int i = 0; i < arraysToCombined.size(); i++) {
			for (int j = 0; j < arraysToCombined.get(i).size(); j++) {
				if (!combination.contains(arraysToCombined.get(i).get(j))) {
					combination.add(arraysToCombined.get(i).get(j));
				}
			}
		}
		
		return combination;
	}
	public static boolean isBuddy(ArrayList<ArrayList<Integer>> coordinates) {
		// nimmt die Koordinaten von zwei Zellen und kontrolliert, ob diese zwei Zellen verbunden sind entweder durch die Spalte, Zeile oder Box
		// gibt true zurück falls sie verbunden sind
		
		boolean isBuddy = false;
		if ((coordinates.get(0).get(0).equals(coordinates.get(1).get(0)) && !coordinates.get(0).get(1).equals(coordinates.get(1).get(1)))
				|| (coordinates.get(0).get(1).equals(coordinates.get(1).get(1)) && !coordinates.get(0).get(0).equals(coordinates.get(1).get(0)))
				|| (getArrayOfBoxOfCurrentPosibilities(coordinates.get(0).get(0), coordinates.get(0).get(1))
				.equals(getArrayOfBoxOfCurrentPosibilities(coordinates.get(1).get(0), coordinates.get(1).get(1))))
					&& !coordinates.get(0).get(0).equals(coordinates.get(1).get(0)) && !coordinates.get(0).get(1).equals(coordinates.get(1).get(1))) {
			isBuddy = true;
		}
		return isBuddy;
	}
	public static void checkForPossibleSudoku() {
		// kontrolliert, ob die Lösung des Sudoku tatsächlich möglich ist
		
		boolean sudokuIsPossible = true;
		for (int i = 0; i < sudoku.size(); i++) {
			for (int j = 0; j < sudoku.get(i).size(); j++) {
				if (sudoku.get(i).get(j) != 0) {
					Integer numberToCheck = new Integer(sudoku.get(i).get(j));
					for (int k = 0; k < sudoku.get(i).size(); k++) {
						if (sudoku.get(i).get(k).equals(numberToCheck) && j != k) {
							System.out.println("row: " + i + " : " + j + " and " + k);
							sudokuIsPossible = false;
						}
					}
					
					for (int k = 0; k < getArrayOfColumnFromCoordinate(j).size(); k++) {
						if (getArrayOfColumnFromCoordinate(j).get(k).equals(numberToCheck) && k != i) {
							System.out.println("column: " + j + " : " + i + " and " + k);
							sudokuIsPossible = false;
						}
					}
					
					ArrayList<Integer> arrayWithoutCurrentElement = new ArrayList<Integer>(getArrayOfBox(i, j));
					arrayWithoutCurrentElement.remove(numberToCheck);
					for (int k = 0; k < arrayWithoutCurrentElement.size(); k++) {
						if (arrayWithoutCurrentElement.contains(numberToCheck)) {
							sudokuIsPossible = false;
						}
					}
				}
			}
		}
		
		System.out.println("");
		if (sudokuIsPossible) {
			System.out.println("Sudoku A OK");
		} else {
			System.out.println("SUDOKU IS WRONG");
		}
		System.out.println("");
	}
	public static void printSudoku() {
		// Druckt auf der Konsole das aktuelle Sudoku gelöste Sudoku aus
		
		System.out.println("Printing Sudoku... ");
		for (int i = 0; i < sudoku.size(); i++) {
			for (int j = 0; j < sudoku.get(i).size(); j++) {
				System.out.print(sudoku.get(i).get(j) + " ");
			}
			System.out.println("");
		}
		System.out.println("0s left: " + getCoordinatesOfEmptyElements().size());
		System.out.println("End");
		System.out.println("");
	}
	public static void printPossibilities() {
		// Druckt auf der Konsole alle möglichen Werte von jeder Zelle aus
		
		System.out.println("Printing Posibilities...");
		
		for (int i = 0; i < currentPossibilities.size(); i++) {
			for (int j = 0; j < currentPossibilities.get(i).size(); j++) {
				System.out.print(currentPossibilities.get(i).get(j));
				int spacesToPrint = 8 - currentPossibilities.get(i).get(j).size();
				for (int k = 0; k < spacesToPrint * 3 - 11; k++) {
					System.out.print(" ");
				}
			}
			System.out.println("");
		}
		
		int length = 0;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (currentPossibilities.get(i).get(j).get(0) != 0) {
					length++;
				}
			}
		}

		System.out.println("Elements left to solve: " + length);
		System.out.println("End");
		System.out.println("");
	}
	public static void printSudPos() {
		if (shouldRepeat) {
			printPossibilities();
			printSudoku();
		}
	}
}