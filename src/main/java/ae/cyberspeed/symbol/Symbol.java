package ae.cyberspeed.symbol;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public class Symbol {

    @Nullable
    private final String value;
    private final SymbolType symbolType;
    private final double rewardMultiplier;

    private final Impact impact;
    private final double extra;

    public Symbol(@Nullable String value) {
        this(value, 0);
    }

    public Symbol(@Nullable String value, double rewardMultiplier) {
        this.value = value;
        this.rewardMultiplier = rewardMultiplier;
        this.symbolType = SymbolType.STANDARD;
        this.impact = null;
        this.extra = 0;
    }

    public Symbol(@Nullable String value, Impact impact, double rewardMultiplier, double extra) {
        this.value = value;
        this.rewardMultiplier = rewardMultiplier;
        this.symbolType = SymbolType.BONUS_SYMBOL;
        this.impact = impact;
        this.extra = extra;
    }
    public boolean isEmpty() {
        return value == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return Double.compare(rewardMultiplier, symbol.rewardMultiplier) == 0
                && Double.compare(extra, symbol.extra) == 0
                && Objects.equals(value, symbol.value)
                && symbolType == symbol.symbolType
                && impact == symbol.impact;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, symbolType, rewardMultiplier, impact, extra);
    }

    public enum SymbolType {
        STANDARD, BONUS_SYMBOL
    }

    public enum Impact {
        MULTIPLY_REWARD, EXTRA_BONUS, MISS
    }

}
