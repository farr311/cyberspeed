package util;

import ae.cyberspeed.config.GameConfig;
import ae.cyberspeed.symbol.Symbol;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class TestConfigResolverImpl implements TestConfigResolver {

    private final int columns;
    private final int rows;
    private final Map<String, Symbol> symbols;
    private final GameConfig.Probabilities probabilities;
    private final List<GameConfig.WinCombination> combinations;

    private TestConfigResolverImpl(
            int columns,
            int rows,
            Map<String, Symbol> symbols,
            GameConfig.Probabilities probabilities,
            List<GameConfig.WinCombination> combinations) {

        this.columns = columns;
        this.rows = rows;
        this.symbols = symbols;
        this.probabilities = probabilities;
        this.combinations = combinations;
    }

    @Override
    public GameConfig resolve() {
        return new GameConfig(columns, rows, symbols, probabilities, combinations);
    }

    public static Builder Builder() {
        return new Builder();
    }

    public static class Builder {
        private int columns = 3;
        private int rows = 3;
        private Map<String, Symbol> symbols = null;
        private GameConfig.Probabilities probabilities = null;
        private List<GameConfig.WinCombination> combinations = null;

        public Builder setColumns(int columns) {
            this.columns = columns;
            return this;
        }

        public Builder setRows(int rows) {
            this.rows = rows;
            return this;
        }

        public Builder setSymbols(Map<String, Symbol> symbols) {
            this.symbols = symbols;
            return this;
        }

        public Builder setProbabilities(GameConfig.Probabilities probabilities) {
            this.probabilities = probabilities;
            return this;
        }

        public Builder setCombinations(List<GameConfig.WinCombination> combinations) {
            this.combinations = combinations;
            return this;
        }


        public TestConfigResolver build() {
            return new TestConfigResolverImpl(columns, rows, symbols, probabilities, combinations);
        }
    }
}
