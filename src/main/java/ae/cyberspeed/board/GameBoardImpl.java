package ae.cyberspeed.board;

import ae.cyberspeed.config.GameConfig;
import ae.cyberspeed.symbol.Symbol;

import java.util.*;

public class GameBoardImpl implements GameBoard {

    private final int numColumns;
    private final int numRows;

    private final Symbol[][] matrix;

    public GameBoardImpl(GameConfig config) {
        this.numColumns = config.columns();
        this.numRows = config.rows();
        matrix = new Symbol[numColumns][numRows];
    }

    @Override
    public void generateBoard() {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                matrix[i][j] = new Symbol(null);
            }
        }
    }

    @Override
    public void updateBoard(Symbol[][] supplyingMatrix) {
        for (int i = 0; i < numRows; i++) {
            if (numColumns >= 0) {
                System.arraycopy(supplyingMatrix[i], 0, matrix[i], 0, numColumns);
            }
        }
    }

    @Override
    public int countEntries(Symbol s) {
        return (int) Arrays.stream(matrix)
                .flatMap(Arrays::stream)
                .filter(e -> e.equals(s))
                .count();
    }

    @Override
    public Symbol getEntry(int rowIndex, int columnIndex) {
        return matrix[rowIndex][columnIndex];
    }

    @Override
    public String[][] asValueMatrix() {
        return Arrays.stream(matrix)
                .map(r -> Arrays.stream(r)
                            .map(Symbol::getValue)
                            .toArray(String[]::new))
                .toArray(String[][]::new);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        int cellWidth = 7;

        for (Symbol[] row : matrix) {
            sb.append("|");
            for (int i = 0; i < row.length; i++) {
                sb.append("-".repeat(cellWidth)).append("|");
            }

            sb.append("\n|");
            for (Symbol symbol : row) {
                Object symbolValue = symbol.getValue();
                String symbolString = symbolValue == null ? "0" : symbolValue.toString();
                int prefixLength = (cellWidth - symbolString.length()) / 2;
                int postfixLength = cellWidth - prefixLength - symbolString.length();

                sb.append(" ".repeat(prefixLength)).append(symbolString).append(" ".repeat(postfixLength)).append("|");
            }

            sb.append("\n");
        }

        sb.append("|");
        for (int i = 0; i < matrix[0].length; i++) {
            sb.append("-".repeat(cellWidth)).append("|");
        }

        return sb.toString();
    }
}
