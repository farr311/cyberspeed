package ae.cyberspeed.reward;

import ae.cyberspeed.config.GameConfig;
import ae.cyberspeed.symbol.Symbol;

import java.util.List;
import java.util.Map;

public interface WinCombinationsValidator {

    Map.Entry<Symbol, List<GameConfig.WinCombination>> validate(Symbol symbol);

    boolean isBonusSymbolApplicable(Symbol symbol);
}
