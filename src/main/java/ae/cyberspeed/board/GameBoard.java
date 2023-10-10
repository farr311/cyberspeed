package ae.cyberspeed.board;

import ae.cyberspeed.symbol.Symbol;


public interface GameBoard {

    void generateBoard();

    void updateBoard(Symbol[][] supplyingMatrix);

    int countEntries(Symbol s);

    Symbol getEntry(int rowIndex, int columnIndex);


    String[][] asValueMatrix();
}
