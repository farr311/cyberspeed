package ae.cyberspeed.config;

import ae.cyberspeed.symbol.Symbol;
import lombok.Getter;

import java.util.*;

public record GameConfig(int columns,
                         int rows,
                         Map<String, Symbol> symbols,
                         ae.cyberspeed.config.GameConfig.Probabilities probabilities,
                         List<WinCombination> winCombinations) {

    @Override
    public Map<String, Symbol> symbols() {
        return Map.copyOf(this.symbols);
    }

    @Override
    public List<WinCombination> winCombinations() {
        return List.copyOf(winCombinations);
    }

    public record Probabilities(List<Probability> standardSymbols,
                                GameConfig.Probabilities.Probability bonusSymbols) {

        public record Probability(int column,
                                  int row,
                                  Map<String, Integer> symbols) {

            @Override
            public Map<String, Integer> symbols() {
                return Map.copyOf(symbols);
            }
        }

        @Override
        public List<Probability> standardSymbols() {
                return List.copyOf(standardSymbols);
            }
    }

    @Getter
    public static class WinCombination {

        private final String name;
        private final double rewardMultiplier;
        private final WinCombinationType when;
        private final int count;
        private final WinCombinationGroup group;
        private final Cell[][] coveredAreas;

        public WinCombination(
                String name,
                double rewardMultiplier,
                WinCombinationType when,
                int count,
                WinCombinationGroup group) {

            this.name = name;
            this.rewardMultiplier = rewardMultiplier;
            this.when = when;
            this.count = count;
            this.group = group;
            this.coveredAreas = null;
        }

        public WinCombination(
                String name,
                double rewardMultiplier,
                WinCombinationType when,
                WinCombinationGroup group,
                Cell[][] coveredAreas) {

            this.name = name;
            this.rewardMultiplier = rewardMultiplier;
            this.when = when;
            this.count = 0;
            this.group = group;
            this.coveredAreas = coveredAreas;
        }

        public record Cell(int row, int column) { }

        @SuppressWarnings("unused")
        public enum WinCombinationType {
            SAME_SYMBOLS, LINEAR_SYMBOLS
        }

        @SuppressWarnings("unused")
        public enum WinCombinationGroup {
            SAME_SYMBOLS,
            VERTICALLY_LINEAR_SYMBOLS,
            HORIZONTALLY_LINEAR_SYMBOLS,
            LTR_DIAGONALLY_LINEAR_SYMBOLS,
            RTL_DIAGONALLY_LINEAR_SYMBOLS
        }

    }
}