package ae.cyberspeed.game;

import ae.cyberspeed.config.GameConfig;

import java.util.*;
import java.util.stream.Stream;

public class ProbabilityMatrix {
    private final ProbabilityCell[][] matrix;

    private boolean usedBonus = false;

    public ProbabilityMatrix(int columns, int rows, GameConfig.Probabilities probabilities) {
        List<GameConfig.Probabilities.Probability> standardSymbolsProbabilities = probabilities.standardSymbols();
        GameConfig.Probabilities.Probability bonusSymbolsProbabilities = probabilities.bonusSymbols();

        matrix = new ProbabilityCell[rows][columns];

        standardSymbolsProbabilities.forEach(w ->
                matrix[w.row()][w.column()] =
                        new ProbabilityCell(w.symbols(), bonusSymbolsProbabilities.symbols()));

    }

    public ProbabilityCell getCell(int i, int j) {
        return matrix[i][j];
    }

    public class ProbabilityCell {
        private final String[] symbolSequence;

        private final int bonusStartIndex;

        public ProbabilityCell(
                Map<String, Integer> standardSymbolsWeights,
                Map<String, Integer> bonusSymbolWeights) {

            Set<Map.Entry<String, Integer>> standardEntries = standardSymbolsWeights.entrySet();
            Set<Map.Entry<String, Integer>> bonusEntries = bonusSymbolWeights.entrySet();

            int cumulativeWeight = Stream.concat(standardEntries.stream(), bonusEntries.stream())
                    .map(Map.Entry::getValue)
                    .reduce(0, Integer::sum);

            symbolSequence = new String[cumulativeWeight];

            int index = 0;
            for (Map.Entry<String, Integer> e : standardSymbolsWeights.entrySet()) {
                for (int i = 0; i < e.getValue(); i++) {
                    symbolSequence[index++] = e.getKey();
                }
            }

            bonusStartIndex = index;
            for (Map.Entry<String, Integer> e : bonusSymbolWeights.entrySet()) {
                for (int i = 0; i < e.getValue(); i++) {
                    symbolSequence[index++] = e.getKey();
                }
            }
        }

        public String nextWeightedRandomValue() {
            Random r = new Random();
            int limit = ProbabilityMatrix.this.usedBonus ? bonusStartIndex : symbolSequence.length;
            int index = r.nextInt(limit);

            if (index >= bonusStartIndex) {
                ProbabilityMatrix.this.usedBonus = true;
            }

            return symbolSequence[index];
        }
    }

}
