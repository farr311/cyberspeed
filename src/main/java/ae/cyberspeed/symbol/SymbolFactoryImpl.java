package ae.cyberspeed.symbol;

import ae.cyberspeed.config.GameConfig;

import java.util.Map;

public class SymbolFactoryImpl implements SymbolFactory {

    private final Map<String, Symbol> symbols;

    public SymbolFactoryImpl(GameConfig config) {
        this.symbols = config.symbols();
    }

    @Override
    public Symbol createSymbol(String value) {
        return symbols.get(value);
    }
}
