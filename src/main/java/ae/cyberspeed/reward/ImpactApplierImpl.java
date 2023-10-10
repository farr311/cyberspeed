package ae.cyberspeed.reward;

import ae.cyberspeed.symbol.Symbol;

public class ImpactApplierImpl implements ImpactApplier{

    @Override
    public double apply(double identity, Symbol symbol) {
        if (symbol == null || !Symbol.SymbolType.BONUS_SYMBOL.equals(symbol.getSymbolType())) {
            return identity;
        }

        return switch (symbol.getImpact()) {
            case MISS -> identity;
            case MULTIPLY_REWARD -> identity * symbol.getRewardMultiplier();
            case EXTRA_BONUS -> identity + symbol.getExtra();
        };
    }
}
