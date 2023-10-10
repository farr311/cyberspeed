package ae.cyberspeed.reward;

import ae.cyberspeed.board.GameBoard;
import ae.cyberspeed.config.GameConfig;
import ae.cyberspeed.symbol.Symbol;

import java.util.*;

public class WinCombinationsValidatorImpl implements WinCombinationsValidator {

    private final List<GameConfig.WinCombination> combinations;
    private final GameBoard board;

    public WinCombinationsValidatorImpl(GameConfig config, GameBoard board) {
        this.combinations = config.winCombinations();
        this.board = board;
    }

    private boolean validateCombination(GameConfig.WinCombination combination, Symbol symbol) {
        if (GameConfig.WinCombination.WinCombinationType.SAME_SYMBOLS.equals(combination.getWhen())) {
            return board.countEntries(symbol) == combination.getCount();
        } else {
            GameConfig.WinCombination.Cell[][] coveredAreas = combination.getCoveredAreas();

            for (GameConfig.WinCombination.Cell[] area : coveredAreas) {
                Set<Symbol> set = new HashSet<>();
                int count = 0;

                for (GameConfig.WinCombination.Cell c : area) {
                    count++;
                    set.add(board.getEntry(c.row(), c.column()));
                }

                if (set.contains(symbol) && set.size() == 1 && count >= area.length) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Map.Entry<Symbol, List<GameConfig.WinCombination>> validate(Symbol symbol) {
        List<GameConfig.WinCombination> applicableCombinations = new ArrayList<>();

        for (GameConfig.WinCombination combination : combinations) {
            if (validateCombination(combination, symbol)) {
                applicableCombinations.add(combination);
            }
        }

        return Map.entry(symbol, applicableCombinations);
    }

    @Override
    public boolean isBonusSymbolApplicable(Symbol s) {
        return !(Objects.equals(s.getValue(), "MISS")) && board.countEntries(s) > 0;
    }
}
